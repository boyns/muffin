/*
 *  gnu/regexp/REMatch.java
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

public class REMatch implements java.io.Serializable
{
  // An instance of this class represents a match in progress or
  // completed by an RE function

  private String refString;
  int index;
  int[] start; // package scope for
  int[] end;   // quick access internally
  int[] count; // runtime count of times through each subexpression

  void clear()
  {
    for (int i = 0; i < start.length; i++)
      {
	start[i] = end[i] = -1;
	count[i] = 0;
      }
  }
    
  REMatch(String f_refString, int f_subs, int f_realStart)
  {
    refString = f_refString;
    start = new int[f_subs+1];
    end = new int[f_subs+1];
    count = new int[f_subs+1];
    clear();
    index = f_realStart;
  }
  
  /**
   * Returns the string (a substring of the input string used to
   * generate this match) matching the pattern.  This makes it convenient
   * to write code like the following:
   * <P>
   * <code> REMatch myMatch = myExpression.getMatch(myString);<br>
   * if (myMatch != null) System.out.println("Regexp found: "+myMatch);</code>
   *
   **/

  public String toString()
  {
    if (start[0] == -1) return null;
    return refString.substring(start[0],end[0]);
  }
  
  /**
   * Returns the index within the input string where the match in its entirety
   * begins.
   **/
  
  public int getStartIndex()
  {
    return start[0];
  }
  
  /** 
   * Returns the index within the input string used to generate this match
   * where subexpression number <i>sub</i> begins, or <code>-1</code> if
   * the subexpression does not exist.
   *
   * @param sub Subexpression index
   **/
  
  public int getSubStartIndex(int sub)
  {
    if (sub < start.length) return start[sub];
    return -1;
  }
  
  /**
   * Returns the index within the input string where the match in its entirety 
   * ends.  The return value is the next position after the end of the string;
   * therefore, a match created by the following call:
   * <P>
   * <code>REMatch myMatch = myExpression.getMatch(myString);</code>
   * <P>
   * can be viewed (given that myMatch is not null) by creating
   * <P>
   * <code>String theMatch = myString.substring(myMatch.getStartIndex(),
   * myMatch.getEndIndex());</code>
   * <P>
   * But you can save yourself that work, since the <code>toString()</code>
   * method (above) does exactly that for you.
   **/
  
  public int getEndIndex()
  {
    return end[0];
  }
  
  /** 
   * Returns the index within the input string used to generate this match
   * where subexpression number <i>sub</i> ends, or <code>-1</code> if
   * the subexpression does not exist.
   *
   * @param sub Subexpression index
   **/
  
  public int getSubEndIndex(int sub)
  {
    return (sub < start.length) ? end[sub] : -1;
  }
  
  /**
   * Substitute the results of this match to create a new string.
   * This is patterned after PERL, so the tokens to watch out for are
   * <code>$0</code> through <code>$9</code>.  <code>$0</code> matches
   * the full substring matched; <code>$<i>n</i></code> matches
   * subexpression number <i>n</i>.
   *
   * @param input A string consisting of literals and <code>$<i>n</i></code> tokens.
   * @exception REException A specified substring index did not exist.
   **/
  
  public String substituteInto(String input) throws REException
  {
    // a la Perl, $0 is whole thing, $1 - $9 are subexpressions
    char[] inbuf = input.toCharArray();
    StringBuffer output = new StringBuffer();
    for (int pos = 0; pos < input.length(); pos++)
      {
	if (inbuf[pos] == '$')
	  {
	    if (Character.isDigit(inbuf[pos+1]))
	      {
		int val = Character.digit(inbuf[++pos],10);
		if (val < start.length)
		  output.append(refString.substring(start[val],end[val]));
		else throw new REException("no such subexpression",REException.REG_ESUBREG,val);
	      }
	  }
	else output.append(inbuf[pos]);
      }
    return new String(output);
  }
  
}
