/*
 *  gnu/regexp/RESyntax.java
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

/**
 * @author      Wes Biggs
 * @version     1.00
 **/

public class RESyntax implements java.io.Serializable
{
  private java.util.BitSet bits;

  // bit indexes

  /**
   * Syntax bit. Backslash is an escape character in lists.
   **/
  public static final int RE_BACKSLASH_ESCAPE_IN_LISTS =  0;

  /**
   * Syntax bit. Use \? instead of ? and \+ instead of +.
   **/
  public static final int RE_BK_PLUS_QM                =  1; // impl.

  /**
   * Syntax bit. POSIX character classes [:...:] are allowed.
   **/
  public static final int RE_CHAR_CLASSES              =  2; // impl.

  /**
   * Syntax bit. ^ and $ are special everywhere.
   **/
  public static final int RE_CONTEXT_INDEP_ANCHORS     =  3; 

  /**
   * Syntax bit. Repetition operators are only special in valid positions.
   **/
  public static final int RE_CONTEXT_INDEP_OPS         =  4; 

  /**
   * Syntax bit. Repetition and alternation operators are invalid
   * at start and end of pattern and other places. 
   * <B>Not fully implemented</B>.
   **/
  public static final int RE_CONTEXT_INVALID_OPS       =  5; 

  /**
   * Syntax bit. Dot matches a newline.
   **/
  public static final int RE_DOT_NEWLINE               =  6; // impl.

  /**
   * Syntax bit. Dot does not match a null.
   **/
  public static final int RE_DOT_NOT_NULL              =  7; // impl.

  /**
   * Syntax bit. Intervals are allowed.
   **/
  public static final int RE_INTERVALS                 =  8; // impl.

  /**
   * Syntax bit. No |, +, or ? operators.
   **/
  public static final int RE_LIMITED_OPS               =  9; // impl.

  /**
   * Syntax bit. Newline is an alternation operator.
   **/
  public static final int RE_NEWLINE_ALT               = 10; // impl.

  /**
   * Syntax bit. Intervals use { } instead of \{ \}
   **/
  public static final int RE_NO_BK_BRACES              = 11; 

  /**
   * Syntax bit. Grouping uses ( ) instead of \( \).
   **/
  public static final int RE_NO_BK_PARENS              = 12;

  /**
   * Syntax bit. Backreferences not allowed.
   **/
  public static final int RE_NO_BK_REFS                = 13; // impl.

  /**
   * Syntax bit. Alternation uses | instead of \|
   **/
  public static final int RE_NO_BK_VBAR                = 14;

  /**
   * Syntax bit. <B>Not implemented</B>.
   **/
  public static final int RE_NO_EMPTY_RANGES           = 15;

  /**
   * Syntax bit. <B>Not implemented</B>.
   **/
  public static final int RE_UNMATCHED_RIGHT_PAREN_ORD = 16;

  /**
   * Syntax bit.
   **/
  public static final int RE_HAT_LISTS_NOT_NEWLINE     = 17;


  /**
   * Syntax bit.
   **/
  public static final int RE_STINGY_OPS                = 18;

  /**
   * Syntax bit. Allow \d, \D, \s, \S, \w, \W.
   **/
  public static final int RE_CHAR_CLASS_ESCAPES        = 19;

  /**
   * Syntax bit. <B>Not implemented</B>.
   **/
  public static final int RE_PURE_GROUPING             = 20;

  /**
   * Syntax bit. <B>Not implemented</B>.
   **/
  public static final int RE_LOOKAHEAD                 = 21;

  /**
   * Syntax bit. Allow \A, \Z.
   **/
  public static final int RE_STRING_ANCHORS            = 22;

  private static final int BIT_TOTAL                   = 23;


  /**
   * Predefined syntax.
   **/
  public static final RESyntax RE_SYNTAX_AWK;

  /**
   * Predefined syntax.
   **/
  public static final RESyntax RE_SYNTAX_ED;

  /**
   * Predefined syntax.
   **/
  public static final RESyntax RE_SYNTAX_EGREP;

  /**
   * Predefined syntax.
   **/
  public static final RESyntax RE_SYNTAX_EMACS;

  /**
   * Predefined syntax.
   **/
  public static final RESyntax RE_SYNTAX_GREP;

  /**
   * Predefined syntax.
   **/
  public static final RESyntax RE_SYNTAX_POSIX_AWK;

  /**
   * Predefined syntax.
   **/
  public static final RESyntax RE_SYNTAX_POSIX_BASIC;

  /**
   * Predefined syntax.
   **/
  public static final RESyntax RE_SYNTAX_POSIX_EGREP;

  /**
   * Predefined syntax.
   **/
  public static final RESyntax RE_SYNTAX_POSIX_EXTENDED;

  /**
   * Predefined syntax.
   **/
  public static final RESyntax RE_SYNTAX_POSIX_MINIMAL_BASIC;

  /**
   * Predefined syntax.
   **/
  public static final RESyntax RE_SYNTAX_POSIX_MINIMAL_EXTENDED;

  /**
   * Predefined syntax.
   **/
  public static final RESyntax RE_SYNTAX_SED;

  /**
   * Predefined syntax.
   **/
  public static final RESyntax RE_SYNTAX_PERL4;

  /**
   * Predefined syntax.
   **/
  public static final RESyntax RE_SYNTAX_PERL4_S; // single line mode (/s)

  /**
   * Predefined syntax.
   **/
  public static final RESyntax RE_SYNTAX_PERL5;  

  /**
   * Predefined syntax.
   **/
  public static final RESyntax RE_SYNTAX_PERL5_S; // single line mode (/s)
  
  static
    {
      // Define syntaxes

      RE_SYNTAX_EMACS = new RESyntax();

      RESyntax RE_SYNTAX_POSIX_COMMON = new RESyntax()
        .set(RE_CHAR_CLASSES)
        .set(RE_DOT_NEWLINE)
        .set(RE_DOT_NOT_NULL)
        .set(RE_INTERVALS)
        .set(RE_NO_EMPTY_RANGES);

      RE_SYNTAX_POSIX_BASIC = new RESyntax(RE_SYNTAX_POSIX_COMMON)
        .set(RE_BK_PLUS_QM);

      RE_SYNTAX_POSIX_EXTENDED = new RESyntax(RE_SYNTAX_POSIX_COMMON)
        .set(RE_CONTEXT_INDEP_ANCHORS)
        .set(RE_CONTEXT_INDEP_OPS)
        .set(RE_NO_BK_BRACES)
        .set(RE_NO_BK_PARENS)
        .set(RE_NO_BK_VBAR)
        .set(RE_UNMATCHED_RIGHT_PAREN_ORD);

      RE_SYNTAX_AWK = new RESyntax()
        .set(RE_BACKSLASH_ESCAPE_IN_LISTS)
        .set(RE_DOT_NOT_NULL)
        .set(RE_NO_BK_PARENS)
        .set(RE_NO_BK_REFS)
        .set(RE_NO_BK_VBAR)
        .set(RE_NO_EMPTY_RANGES)
        .set(RE_UNMATCHED_RIGHT_PAREN_ORD);

      RE_SYNTAX_POSIX_AWK = new RESyntax(RE_SYNTAX_POSIX_EXTENDED)
        .set(RE_BACKSLASH_ESCAPE_IN_LISTS);

      RE_SYNTAX_GREP = new RESyntax()
        .set(RE_BK_PLUS_QM)
        .set(RE_CHAR_CLASSES)
        .set(RE_HAT_LISTS_NOT_NEWLINE)
        .set(RE_INTERVALS)
        .set(RE_NEWLINE_ALT);

      RE_SYNTAX_EGREP = new RESyntax()
        .set(RE_CHAR_CLASSES)
        .set(RE_CONTEXT_INDEP_ANCHORS)
        .set(RE_CONTEXT_INDEP_OPS)
        .set(RE_HAT_LISTS_NOT_NEWLINE)
        .set(RE_NEWLINE_ALT)
        .set(RE_NO_BK_PARENS)
        .set(RE_NO_BK_VBAR);

      RE_SYNTAX_POSIX_EGREP = new RESyntax(RE_SYNTAX_EGREP)
        .set(RE_INTERVALS)
        .set(RE_NO_BK_BRACES);

      /* P1003.2/D11.2, section 4.20.7.1, lines 5078ff.  */

      RE_SYNTAX_ED = new RESyntax(RE_SYNTAX_POSIX_BASIC);

      RE_SYNTAX_SED = new RESyntax(RE_SYNTAX_POSIX_BASIC);

      RE_SYNTAX_POSIX_MINIMAL_BASIC = new RESyntax(RE_SYNTAX_POSIX_COMMON)
        .set(RE_LIMITED_OPS);

      /* Differs from RE_SYNTAX_POSIX_EXTENDED in that RE_CONTEXT_INVALID_OPS
         replaces RE_CONTEXT_INDEP_OPS and RE_NO_BK_REFS is added. */

      RE_SYNTAX_POSIX_MINIMAL_EXTENDED = new RESyntax(RE_SYNTAX_POSIX_COMMON)
        .set(RE_CONTEXT_INDEP_ANCHORS)
        .set(RE_CONTEXT_INVALID_OPS)
        .set(RE_NO_BK_BRACES)
        .set(RE_NO_BK_PARENS)
        .set(RE_NO_BK_REFS)
        .set(RE_NO_BK_VBAR)
        .set(RE_UNMATCHED_RIGHT_PAREN_ORD);

      /* There is no official Perl spec, but here's a "best guess" */

      RE_SYNTAX_PERL4 = new RESyntax()
	.set(RE_BACKSLASH_ESCAPE_IN_LISTS)
	.set(RE_CONTEXT_INDEP_ANCHORS)
	.set(RE_CONTEXT_INDEP_OPS)          // except for '{', apparently
	.set(RE_INTERVALS)
	.set(RE_NO_BK_BRACES)
	.set(RE_NO_BK_PARENS)
	.set(RE_NO_BK_VBAR)
	.set(RE_NO_EMPTY_RANGES)
	.set(RE_CHAR_CLASS_ESCAPES);    // \d,\D,\w,\W,\s,\S

      RE_SYNTAX_PERL4_S = new RESyntax(RE_SYNTAX_PERL4)
	.set(RE_DOT_NEWLINE);

      RE_SYNTAX_PERL5 = new RESyntax(RE_SYNTAX_PERL4)
	.set(RE_PURE_GROUPING)          // (?:) not implemented
	.set(RE_STINGY_OPS)             // *?,??,+?,{}?
	.set(RE_LOOKAHEAD)              // (?=)(?!) not implemented
	.set(RE_STRING_ANCHORS);        // \A,\Z

      RE_SYNTAX_PERL5_S = new RESyntax(RE_SYNTAX_PERL5)
	.set(RE_DOT_NEWLINE);
    }


  /**
   * Construct a new syntax object with all bits turned off.
   **/
  public RESyntax()
    {
      bits = new java.util.BitSet(BIT_TOTAL);
    }

  /**
   * Construct a new syntax object with all bits set the same 
   * as the other syntax.
   **/
  public RESyntax(RESyntax other)
    {
      bits = (java.util.BitSet) other.bits.clone();
    }

  /**
   * Check if a given bit is set in this syntax.
   **/
  public boolean get(int index)
    {
      return bits.get(index);
    }

  /**
   * Set a given bit in this syntax.
   **/
  public RESyntax set(int index)
    {
      bits.set(index);
      return this;
    }
}
