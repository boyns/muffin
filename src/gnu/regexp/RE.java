/*
 *  gnu/regexp/RE.java
 *  Copyright 1998 Wes Biggs, wes@cacas.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package gnu.regexp;
import java.util.Vector;

/**
 * RE provides the user interface for compiling and matching regular
 * expressions.
 * <P>
 * A regular expression object (class RE) is compiled by constructing it
 * from a string or character array, with optional compilation flags to
 * case-insensitive matching and so on, and an optional syntax specification
 * (if not specified, it defaults to <code>RESyntax.RE_SYNTAX_PERL5</code>).
 * Once you've got an RE instance, match it against Strings or character arrays.  If a match is
 * found, you get a match object (class REMatch) back, which you can use to
 * view the indices of your match in the input string as well as the indices
 * of any subexpressions in the match.
 * <P>
 * If a match is not found (by <code>getMatch()</code>), the REMatch instance
 * returned will be null.
 * <P>
 * You can affect the compilation and execution environment by using a
 * combination of flags (constants listed below), which have been implemented
 * to match the GNU C library in their semantics (plus some additions).
 * <P>
 * See the RETest source code for some coding examples.
 *
 * @author	Wes Biggs
 * @version	1.00
 **/

// helper class
class CharUnit
{
  public char ch;
  public boolean bk;
  CharUnit() {};
}

public class RE extends REToken implements java.io.Serializable
{
  private static final String version = "1.00";

  private REToken firstToken, lastToken;
  private Vector branches;
  private int numSubs;
  private class IntPair implements java.io.Serializable
  {
    public int first, second;
    public IntPair() { }
  }

  /**
   * Compilation flag. Do  not  differentiate  case.   Subsequent
   * searches  using  this  pattern  buffer will be case insensitive.
   **/

  public static final int REG_ICASE = 2;

  /**
   * Compilation flag. The match-any-character operator (dot)
   * will match a newline character.  When set this overrides the syntax
   * bit RE_DOT_NEWLINE (see RESyntax for details).  This is equivalent to
   * the "/s" operator in Perl.
   **/

  public static final int REG_DOT_NEWLINE = 4;

  /**
   * Compilation flag. Use multiline mode.  In this mode, the ^ and $
   * anchors will match at a newline within the input string. This is
   * equivalent to the "/m" operator in Perl.
   **/

  public static final int REG_MULTILINE = 8;

  /**
   * Execution flag.
   * The match-beginning operator (^) does not match at the beginning
   * of the input string.
   **/

  public static final int REG_NOTBOL = 16;

  /**
   * Execution flag.
   * The match-end operator ($) does not match at the end
   * of the input string.
   **/

  public static final int REG_NOTEOL = 32;

  /**
   * Execution flag.
   * The match-beginning operator (^) matches not at position 0
   * in the input string, but at the position the search started at
   * (based on the index input given to the getMatch function).
   **/

  public static final int REG_ANCHORINDEX = 64;

  /**
   * Constructs a regular expression pattern buffer.
   *
   * @param pattern A regular expression string.
   * @exception REException The input pattern could not be parsed.
   **/

  public RE(String pattern) throws REException
  {
    this(pattern.toCharArray(),0,RESyntax.RE_SYNTAX_PERL5);
  }

  /**
   * Constructs a regular expression pattern buffer.
   *
   * @param pattern A regular expression string.
   * @param cflags The logical OR of any combination of the compilation flags listed above.
   * @exception REException The input pattern could not be parsed.
   **/

  public RE(String pattern, int cflags) throws REException
  {
    this(pattern.toCharArray(),cflags,RESyntax.RE_SYNTAX_PERL5);
  }

  /**
   * Constructs a regular expression pattern buffer.
   *
   * @param pattern A regular expression string.
   * @param cflags The logical OR of any combination of the compilation flags listed above.
   * @param syntax The type of regular expression syntax to use.
   * @exception REException The input pattern could not be parsed.
   **/

  public RE(String pattern, int cflags, RESyntax syntax) throws REException
  {
    this(pattern.toCharArray(),cflags,syntax);
  }

  /**
   * Constructs a regular expression pattern buffer.
   *
   * @param pattern A regular expression represented as a character array.
   * @exception REException The input pattern could not be parsed.
   **/

  public RE(char[] pattern) throws REException
  {
    this(pattern,0);
  }

  /**
   * Constructs a regular expression pattern buffer.
   *
   * @param pattern A regular expression represented as a character array.
   * @param cflags The logical OR of any combination of the compilation flags listed above.
   * @exception REException The input pattern could not be parsed.
   **/

  public RE(char[] pattern, int cflags) throws REException
  {
    this(pattern,cflags,RESyntax.RE_SYNTAX_PERL5);
  }

  // internal constructor used for alternation
  private RE(REToken f_first, REToken f_last)
  {
    firstToken = f_first;
    lastToken = f_last;
  }

  private static int getCharUnit(char[] input, int index, CharUnit unit) throws REException
  {
    unit.ch = input[index++];
    if (unit.bk = (unit.ch == '\\'))
      if (index < input.length)
	unit.ch = input[index++];
      else throw new REException("\\ at end of pattern.",REException.REG_ESCAPE,index);
    return index;
  }

  /**
   * Constructs a regular expression pattern buffer.
   *
   * @param pattern A regular expression represented as a character array.
   * @param cflags The logical OR of any combination of the compilation flags listed above.
   * @exception REException The input pattern could not be parsed.
   **/

  public RE(char[] pattern, int cflags, RESyntax syntax) throws REException
  {
    // linked list of tokens (sort of -- some closed loops can exist)
    firstToken = lastToken = null;

    // Keep a running count of the max number of subexpressions
    numSubs = 0;

    // Precalculate these so we don't pay for the math every time we
    // need to access them.
    boolean insens = ((cflags & REG_ICASE)>0);

    // Parse pattern into tokens.  Does anyone know if it's more efficient
    // to use char[] than a String.charAt()?  I'm assuming so.

    // index tracks the position in the char array
    int index = 0;

    // this will be the current parse character (pattern[index])
    CharUnit unit = new CharUnit();
    IntPair minMax = new IntPair();

    // we mark this if we see a backslash
    boolean backslash = false;

    // Buffer a token so we can create a TokenRepeated, etc.
    REToken currentToken = null;
    char ch;

    while (index < pattern.length)
      {
	// read the next character unit (including backslash escapes)
	index = getCharUnit(pattern,index,unit);

	// ALTERNATION OPERATOR
	//  \| or | (if RE_NO_BK_VBAR) or newline (if RE_NEWLINE_ALT)
	//  not available if RE_LIMITED_OPS is set

	if ( ( (unit.ch == '|' && (syntax.get(RESyntax.RE_NO_BK_VBAR) ^ unit.bk))
	       || (syntax.get(RESyntax.RE_NEWLINE_ALT) && (unit.ch == '\n') && !unit.bk) )
	     && !syntax.get(RESyntax.RE_LIMITED_OPS))
	  {
	    // make everything up to here be a branch. create vector if nec.
	    if (branches == null) branches = new Vector();
	    addToken(currentToken);
	    branches.addElement(new RE(firstToken,lastToken));
	    firstToken = lastToken = currentToken = null;
	  }

	// INTERVAL OPERATOR:
	//  {x} | {x,} | {x,y}  (RE_INTERVALS && RE_NO_BK_BRACES)
	//  \{x\} | \{x,\} | \{x,y\} (RE_INTERVALS && !RE_NO_BK_BRACES)
	//
	// OPEN QUESTION: 
	//  what is proper interpretation of '{' at start of string?

		     else if ((unit.ch == '{') && syntax.get(RESyntax.RE_INTERVALS) && (syntax.get(RESyntax.RE_NO_BK_BRACES) || unit.bk))
	  {
	    if (currentToken == null) throw new REException("{ without preceding token",REException.REG_EBRACE,index);

	    index = getMinMax(pattern,index,minMax,syntax);
	    currentToken = new RETokenRepeated(currentToken,minMax.first,minMax.second);
	  }

	// LIST OPERATOR:
	//  [...] | [^...]

	// OPEN QUESTIONS:
	//  What to do with a '[' at the end of the pattern?

	else if ((unit.ch == '[') && !unit.bk)
	  {
	    Vector options = new Vector();
	    if (index == pattern.length) throw new REException("unmatched [",REException.REG_EBRACK,index);
	    
	    // Check for initial items: ^, -
	    ch = pattern[index];
	    boolean negative = (ch == '^');
	    if (negative) ch = pattern[++index];
	    char lastChar = 0;
	    if ((ch == ']') || (ch == '-'))
	      {
		lastChar = ch;
		++index;
	      }

	    while ((ch = pattern[index]) != ']')
	      {
		if (ch == '-')
		  {
		    if (lastChar != 0)
		      {
			ch = pattern[++index];
			
			// Need more error checking here
		      
			options.addElement(new RETokenRange(lastChar,ch,insens));
			lastChar = 0;
		      }
		    else throw new REException("- indeterminate",REException.REG_ERANGE,index);
		  }
		else if ((ch == '\\') && syntax.get(RESyntax.RE_BACKSLASH_ESCAPE_IN_LISTS))
		  {
		    ch = pattern[++index];
		    if (lastChar != 0) options.addElement(new RETokenChar(lastChar,insens));
		    lastChar= ch;
		  }
		else if (ch == '[')
		  {
		    
		    if (syntax.get(RESyntax.RE_CHAR_CLASSES) && (pattern[index+1]==':'))
		      {
			StringBuffer posixSet = new StringBuffer();
			index = getPosixSet(pattern,index+2,posixSet);
			String posixSetStr = posixSet.toString();
			if (posixSetStr.equals("alnum"))
			  options.addElement(new RETokenPOSIX(RETokenPOSIX.ALNUM,insens,false));
			else if (posixSetStr.equals("alpha"))
			  options.addElement(new RETokenPOSIX(RETokenPOSIX.ALPHA,insens,false));
			else if (posixSetStr.equals("blank"))
			  options.addElement(new RETokenPOSIX(RETokenPOSIX.BLANK,insens,false));
			else if (posixSetStr.equals("cntrl"))
			  options.addElement(new RETokenPOSIX(RETokenPOSIX.CNTRL,insens,false));
			else if (posixSetStr.equals("digit"))
			  options.addElement(new RETokenPOSIX(RETokenPOSIX.DIGIT,insens,false));
			else if (posixSetStr.equals("graph"))
			  options.addElement(new RETokenPOSIX(RETokenPOSIX.GRAPH,insens,false));
			else if (posixSetStr.equals("lower"))
			  options.addElement(new RETokenPOSIX(RETokenPOSIX.LOWER,insens,false));
			else if (posixSetStr.equals("print"))
			  options.addElement(new RETokenPOSIX(RETokenPOSIX.PRINT,insens,false));
			else if (posixSetStr.equals("punct"))
			  options.addElement(new RETokenPOSIX(RETokenPOSIX.PUNCT,insens,false));
			else if (posixSetStr.equals("space"))
			  options.addElement(new RETokenPOSIX(RETokenPOSIX.SPACE,insens,false));
			else if (posixSetStr.equals("upper"))
			  options.addElement(new RETokenPOSIX(RETokenPOSIX.UPPER,insens,false));
			else if (posixSetStr.equals("xdigit"))
			  options.addElement(new RETokenPOSIX(RETokenPOSIX.XDIGIT,insens,false));
		      }
		  }
		else
		  {
		    if (lastChar != 0) options.addElement(new RETokenChar(lastChar,insens));
		    lastChar= ch;
		  }
		++index;
	      } // while in list
	    ++index;
	    
	    if (lastChar != 0) options.addElement(new RETokenChar(lastChar,insens));
	    
	    // Create a new RETokenOneOf
	    addToken(currentToken);
	    options.trimToSize();
	    currentToken = new RETokenOneOf(options,negative);
	  }

	// SUBEXPRESSIONS
	//  (...) | \(...\) depending on RE_NO_BK_PARENS

	else if ((unit.ch == '(') && (syntax.get(RESyntax.RE_NO_BK_PARENS) ^ unit.bk))
	  {
	    // find end of subexpression
	    int endIndex = index;
	    int nextIndex = index;

	    while ( ((nextIndex = getCharUnit(pattern,endIndex,unit))>0)
		    && !((unit.ch == ')') && (syntax.get(RESyntax.RE_NO_BK_PARENS) ^ unit.bk)) )
	      if ((endIndex = nextIndex) >= pattern.length)
		throw new REException("no end of subexpression",REException.REG_ESUBREG,index-1);

	    // endIndex is now position at a ')','\)' 
	    // nextIndex is end of string or position after ')' or '\)'

	    // create RE subexpression as token.
	    addToken(currentToken);
	    currentToken = null;

	    numSubs++;
	    RE subExpression = new RE(String.valueOf(pattern,index,endIndex-index).toCharArray(),cflags,syntax);
	    index = nextIndex;
	    
	    // Check for repeats.
	    if (index == pattern.length)
	      addToken(new RETokenSubMarker(subExpression,numSubs,1,1));
	    else
	      {
		// read next unit.

		nextIndex = getCharUnit(pattern,index,unit);

		if ((unit.ch == '{') && syntax.get(RESyntax.RE_INTERVALS) && (syntax.get(RESyntax.RE_NO_BK_BRACES) ^ unit.bk))
		  {
		    index = getMinMax(pattern,nextIndex,minMax,syntax);
		    currentToken = new RETokenSubMarker(subExpression,numSubs,minMax.first,minMax.second);
		  }
		else if ((unit.ch == '*') && !unit.bk)
		  {
		    currentToken = new RETokenSubMarker(subExpression,numSubs,0,Integer.MAX_VALUE);
		    index = nextIndex;
		  }
		else if ((unit.ch == '+') && !syntax.get(RESyntax.RE_LIMITED_OPS) && (!syntax.get(RESyntax.RE_BK_PLUS_QM) ^ unit.bk))
		  {
		    currentToken = new RETokenSubMarker(subExpression,numSubs,1,Integer.MAX_VALUE);
		    index = nextIndex;
		  }
		else if ((unit.ch == '?') && !syntax.get(RESyntax.RE_LIMITED_OPS) && (!syntax.get(RESyntax.RE_BK_PLUS_QM) ^ unit.bk))
		  {
		    currentToken = new RETokenSubMarker(subExpression,numSubs,0,1);
		    index = nextIndex;
		  }
		else
		  {
		    // normal, unrepeated subexpression
		    addToken(new RETokenSubMarker(subExpression,numSubs,1,1));
		  }
	      } // not at end of string
	  } // subexpression
      
	// START OF LINE OPERATOR
	//  ^

	else if ((unit.ch == '^') && !unit.bk)
	  {
	    addToken(currentToken);
	    currentToken = null;
	    addToken(new RETokenStart((cflags & REG_MULTILINE)>0));
	  }

	// END OF LINE OPERATOR
	//  $

	else if ((unit.ch == '$') && !unit.bk)
	  {
	    addToken(currentToken);
	    currentToken = null;
	    addToken(new RETokenEnd((cflags & REG_MULTILINE)>0));
	  }

	// MATCH-ANY-CHARACTER OPERATOR (except possibly newline and null)
	//  .

	else if ((unit.ch == '.') && !unit.bk)
	  {
	    addToken(currentToken);
	    currentToken = new RETokenAny(syntax.get(RESyntax.RE_DOT_NEWLINE) || ((cflags & REG_DOT_NEWLINE)>0),syntax.get(RESyntax.RE_DOT_NOT_NULL));
	  }

	// ZERO-OR-MORE REPEAT OPERATOR
	//  *

	else if ((unit.ch == '*') && !unit.bk)
	  {
	    if (currentToken == null) throw new REException("* without preceding token",REException.REG_BADRPT,index);
	    currentToken = new RETokenRepeated(currentToken,0,Integer.MAX_VALUE);
	  }

	// ONE-OR-MORE REPEAT OPERATOR
	//  + | \+ depending on RE_BK_PLUS_QM
	//  not available if RE_LIMITED_OPS is set

	else if ((unit.ch == '+') && !syntax.get(RESyntax.RE_LIMITED_OPS) && (!syntax.get(RESyntax.RE_BK_PLUS_QM) ^ unit.bk))
	  {
	    if (currentToken == null) throw new REException("+ without preceding token",REException.REG_BADRPT,index);
	    currentToken = new RETokenRepeated(currentToken,1,Integer.MAX_VALUE);
	  }

	// ZERO-OR-ONE REPEAT OPERATOR / STINGY MATCHING OPERATOR
	//  ? | \? depending on RE_BK_PLUS_QM
	//  not available if RE_LIMITED_OPS is set

	else if ((unit.ch == '?') && !syntax.get(RESyntax.RE_LIMITED_OPS) && (!syntax.get(RESyntax.RE_BK_PLUS_QM) ^ unit.bk))
	  {
	    if (currentToken == null) throw new REException("? without preceding token",REException.REG_BADRPT,index);

	    // Check for stingy matching on RETokenRepeated or RETokenSubMarker
	    if (currentToken instanceof RETokenStingifiable) 
	      ((RETokenStingifiable) currentToken).makeStingy();
	    else
	      currentToken = new RETokenRepeated(currentToken,0,1);
	  }
	
	// BACKREFERENCE OPERATOR
	//  \1 \2 \3 \4 ...
        // not available if RE_NO_BK_REFS is set

	else if (unit.bk && Character.isDigit(unit.ch) && !syntax.get(RESyntax.RE_NO_BK_REFS))
	  {
	    addToken(currentToken);
	    currentToken = new RETokenBackRef(Character.digit(unit.ch,10),insens);
	  }

	// START OF STRING OPERATOR
        //  \A

	else if (unit.bk && (unit.ch == 'A') && syntax.get(RESyntax.RE_STRING_ANCHORS))
	  {
	    addToken(currentToken);
	    currentToken = new RETokenStart(false);
	  }

	// DIGIT OPERATOR
        //  \d

	else if (unit.bk && (unit.ch == 'd') && syntax.get(RESyntax.RE_CHAR_CLASS_ESCAPES))
	  {
	    addToken(currentToken);
	    currentToken = new RETokenPOSIX(RETokenPOSIX.DIGIT,insens,false);
	  }

	// NON-DIGIT OPERATOR
        //  \D

	else if (unit.bk && (unit.ch == 'D') && syntax.get(RESyntax.RE_CHAR_CLASS_ESCAPES))
	  {
	    addToken(currentToken);
	    currentToken = new RETokenPOSIX(RETokenPOSIX.DIGIT,insens,true);
	  }

	// NEWLINE ESCAPE
        //  \n

	else if (unit.bk && (unit.ch == 'n'))
	  {
	    addToken(currentToken);
	    currentToken = new RETokenChar('\n',false);
	  }

	// RETURN ESCAPE
        //  \r

	else if (unit.bk && (unit.ch == 'r'))
	  {
	    addToken(currentToken);
	    currentToken = new RETokenChar('\r',false);
	  }

	// WHITESPACE OPERATOR
        //  \s

	else if (unit.bk && (unit.ch == 's') && syntax.get(RESyntax.RE_CHAR_CLASS_ESCAPES))
	  {
	    addToken(currentToken);
	    currentToken = new RETokenPOSIX(RETokenPOSIX.SPACE,insens,false);
	  }

	// NON-WHITESPACE OPERATOR
        //  \S

	else if (unit.bk && (unit.ch == 'S') && syntax.get(RESyntax.RE_CHAR_CLASS_ESCAPES))
	  {
	    addToken(currentToken);
	    currentToken = new RETokenPOSIX(RETokenPOSIX.SPACE,insens,true);
	  }

	// TAB ESCAPE
        //  \t

	else if (unit.bk && (unit.ch == 't'))
	  {
	    addToken(currentToken);
	    currentToken = new RETokenChar('\t',false);
	  }

	// ALPHANUMERIC OPERATOR
        //  \w

	else if (unit.bk && (unit.ch == 'w') && syntax.get(RESyntax.RE_CHAR_CLASS_ESCAPES))
	  {
	    addToken(currentToken);
	    currentToken = new RETokenPOSIX(RETokenPOSIX.ALNUM,insens,false);
	  }

	// NON-ALPHANUMERIC OPERATOR
        //  \W

	else if (unit.bk && (unit.ch == 'W') && syntax.get(RESyntax.RE_CHAR_CLASS_ESCAPES))
	  {
	    addToken(currentToken);
	    currentToken = new RETokenPOSIX(RETokenPOSIX.ALNUM,insens,true);
	  }

	// END OF STRING OPERATOR
        //  \Z

	else if (unit.bk && (unit.ch == 'Z') && syntax.get(RESyntax.RE_STRING_ANCHORS))
	  {
	    addToken(currentToken);
	    currentToken = new RETokenEnd(false);
	  }

	// NON-SPECIAL CHARACTER (or escape to make literal)
        //  c | \* for example

	else  // not a special character
	  {
	    addToken(currentToken);
	    currentToken = new RETokenChar(unit.ch,insens);
	  } 
      } // end while

    // Add final buffered token if applicable
    addToken(currentToken);
      
    if (branches != null)
      {
	branches.addElement(new RE(firstToken,lastToken));
	firstToken = lastToken = currentToken = null;
	branches.trimToSize(); // compact the Vector

	// Go through to set numSubs to max possible.
	int maxSubs;
	for (int z=0; z<branches.size(); z++)
	  if ((maxSubs=((RE) branches.elementAt(z)).getNumSubs()) > numSubs)
	    numSubs = maxSubs;
      }
  }

  /**
   * Checks if the input string in its entirety is an exact match of
   * this regular expression.
   **/
  
  public boolean isMatch(String input)
  {
    return isMatch(input,0,0);
  }
  
  /**
   * Checks if the input string, starting from index, is an exact match of
   * this regular expression.
   **/
  
  public boolean isMatch(String input,int index)
  {
    return isMatch(input,index,0);
  }
  

  /**
   * Checks if the input string, starting from index and using the specified
   * execution flags, is an exact match of this regular expression.
   **/

  public boolean isMatch(String input,int index,int eflags)
  {
    if (branches != null)
      {
	for (int i = 0; i < branches.size(); i++)
	  if (((RE) branches.elementAt(i)).isMatch(input,index,eflags)) return true;
	return false;
      }
    else return (firstToken.match(input,index,eflags,new REMatch(input,numSubs,index)) == input.length());
  }

  /**
   * Returns the maximum number of subexpressions in this regular expression.
   * If the expression contains branches, the value returned will be the
   * maximum subexpressions in any of the branches.
   **/
  
  public int getNumSubs()
  {
    return numSubs;
  }
  
  /**
   * Returns an array of all matches found in the input string.
   **/

  public REMatch[] getAllMatches(String input)
  {
    return getAllMatches(input,0,0);
  }

  /**
   * Returns an array of all matches found in the input string,
   * beginning at the specified index position.
   **/

  public REMatch[] getAllMatches(String input, int index)
  {
    return getAllMatches(input,index,0);
  }

  /**
   * Returns an array of all matches found in the input string,
   * beginning at the specified index position and using the specified
   * execution flags.
   **/

  public REMatch[] getAllMatches(String input, int index, int eflags)
  {
    Vector all = new Vector();
    REMatch m = null;
    while ((m = getMatch(input,index,eflags)) != null)
      {
	all.addElement(m);
	index = m.start[0]+1;
      }
    REMatch[] mset = new REMatch[all.size()];
    all.copyInto(mset);
    return mset;
  }
  
  /* Implements abstract method REToken.match() */
  
  int match(String input, int index, int eflags, REMatch mymatch)
  { 
    if (branches != null)
      {
	int newIndex;
	for (int i=0; i < branches.size(); i++)
	  {
	    newIndex = ((REToken) branches.elementAt(i)).match(input,index,eflags,mymatch);
	    if (newIndex != -1) // match was successful
	      return newIndex;
	  }
	return -1; // no branches matched
      }
    
    return firstToken.match(input,index,eflags,mymatch);
  }
  
  /**
   * Returns the first match found in the input string.
   **/
  
  public REMatch getMatch(String input)

  {
    return getMatch(input,0,0);
  }
  
  /**
   * Returns the first match found in the input string, beginning
   * the search at the specified index.
   **/
  
  public REMatch getMatch(String input, int index)
  {
    return getMatch(input,index,0);
  }
  
  /**
   * Returns the first match found in the input string, beginning
   * the search at the specified index, and using the specified
   * execution flags.  If no match is found, returns null.
   **/

  public REMatch getMatch(String input, int index, int eflags)
  {
    REMatch mymatch = new REMatch(input,numSubs,index);
    
    while (index<=input.length())
      if ((mymatch.end[0] = match(input,index,eflags,mymatch)) != -1)
	{
	  mymatch.start[0] = index;
	  return mymatch;
	}
      else 
	{
	  mymatch.clear();
	  index++;
	}
    
    return null;
  }
  
  /**
   * Substitutes the replacement text for the first match found in the input
   * string.
   **/
  
  public String substitute(String input,String replace)
  {
    return substitute(input,replace,0,0);
  }

  /**
   * Substitutes the replacement text for the first match found in the input
   * string, beginning at the specified index position.
   **/
  
  public String substitute(String input,String replace,int index)
  {
    return substitute(input,replace,index,0);
  }

  /**
   * Substitutes the replacement text for the first match found in the input
   * string, beginning at the specified index position and using the
   * specified execution flags.
   **/
  
  public String substitute(String input,String replace,int index,int eflags)
  {
    // substitute first occurrence of this in input with replace
    REMatch m = getMatch(input,index,eflags);
    if (m==null) return input;
    StringBuffer sb = new StringBuffer(input.substring(0,m.start[0]));
    sb.append(replace);
    sb.append(input.substring(m.end[0]));
    return new String(sb);
  }
  
  /**
   * Substitutes the replacement text for each non-overlapping match found 
   * in the input string.
   **/
  
  public String substituteAll(String input,String replace)
  {
    return substituteAll(input,replace,0,0);
  }

  /**
   * Substitutes the replacement text for each non-overlapping match found 
   * in the input string, starting at the specified index.
   **/
  
  public String substituteAll(String input,String replace,int index)
  {
    return substituteAll(input,replace,index,0);
  }
 
  /**
   * Substitutes the replacement text for each non-overlapping match found 
   * in the input string, starting at the specified index and using the
   * specified execution flags.
   **/
  
  public String substituteAll(String input,String replace,int index,int eflags)
  {
    // While there is an occurrence of this RE, replace it with "replace".
    StringBuffer sb = new StringBuffer();
    if (index > 0) sb.append(input.substring(0,index));
    REMatch m = null;
    while ((m=getMatch(input,index,0))!=null)
      {
	sb.append(input.substring(index,m.start[0]));
	sb.append(replace);
	index = m.end[0];
      }
    sb.append(input.substring(index));
    return new String(sb);
  }
  
  /* Helper function for constructor */
  private void addToken(REToken next)
  {
    if (next == null) return;

    if (firstToken == null)
      lastToken = firstToken = next;
    else
      // if chain returns false, it "rejected" the token due to
      // an optimization, and next was combined with lastToken
      if (lastToken.chain(next)) lastToken = next;
  }

  void addLoop(REToken next)
  {
    if (branches != null)
      for (int i=0; i<branches.size(); i++)
	((RE) branches.elementAt(i)).addLoop(next);
    else
      if (firstToken == null) lastToken = firstToken = next;
      else 
	if (lastToken.chain(next)) lastToken = next;
  }


  /**
   * Returns a string representing the version of the gnu.regexp package.
   **/
  
  public static String version()
  {
    return version;
  }
  
  private static int getPosixSet(char[] pattern,int index,StringBuffer buf)
  {
    // Precondition: pattern[index-1] == ':'
    // we will return pos of closing ']'.
    int i;
    for (i=index; i<(pattern.length-1); i++)
      {
	if ((pattern[i] == ':') && (pattern[i+1] == ']'))
	  return i+1;
	buf.append(pattern[i]);
      }
    return index; // didn't match up
  }

  private static int getMinMax(char[] input,int index,IntPair minMax,RESyntax syntax) throws REException
  {
    // Precondition: input[index-1] == '{', minMax != null

    if (index == input.length) throw new REException("no matching brace",REException.REG_EBRACE,index);
	
    int min,max=0;
    CharUnit unit = new CharUnit();
    StringBuffer buf = new StringBuffer();
    
    // Read string of digits
    while (((index = getCharUnit(input,index,unit)) != input.length)
	   && Character.isDigit(unit.ch))
      buf.append(unit.ch);

    // Check for {} tomfoolery
    if (buf.length() == 0) throw new REException("bad brace construct",REException.REG_EBRACE,index);

    min = Integer.parseInt(buf.toString());
	
    if ((unit.ch == '}') && (syntax.get(RESyntax.RE_NO_BK_BRACES) ^ unit.bk))
      max = min;
    else if ((unit.ch == ',') && !unit.bk)
      {
	buf = new StringBuffer();
	// Read string of digits
	while (((index = getCharUnit(input,index,unit)) != input.length)
	       && Character.isDigit(unit.ch))
	  buf.append(unit.ch);

	if (!((unit.ch == '}') && (syntax.get(RESyntax.RE_NO_BK_BRACES) ^ unit.bk)))
	  throw new REException("expected end of interval",REException.REG_EBRACE,index);

	// This is the case of {x,}
	if (buf.length() == 0) max = Integer.MAX_VALUE;
	else max = Integer.parseInt(buf.toString());
      }
    else throw new REException("invalid character in brace expression",REException.REG_EBRACE,index);

    // We know min and max now, and they are valid.

    minMax.first = min;
    minMax.second = max;

    // return the index following the '}'
    return index;
  }

}

