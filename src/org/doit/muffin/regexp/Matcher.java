package org.doit.muffin.regexp;
import java.lang.String;
import java.lang.Object;
/**
 * @author bw@xmlizer.biz
 * 
 * The interface Matcher models the subset of methods from gnu.regexp
 * that are used within muffin. Thus, 
 * the subexpressions are indexed starting with one, not zero. 
 *
 */

public interface Matcher {

    /**
     * Returns the index within the input text where the match in its entirety
     * began.
     */
	int getStartIndex();
	
    /** 
     * Returns the index within the input string used to generate this match
     * where subexpression number <i>sub</i> begins, or <code>-1</code> if
     * the subexpression does not exist.  The initial position is zero.
     *
     * @param sub Subexpression index
     */
	int getStartIndex(int sub);
	
    /**
     * Returns the index within the input string where the match in
     * its entirety ends.  The return value is the next position after
     * the end of the string; therefore, a match created by the
     * following call:
     *
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
     */
	int getEndIndex();
    
    /** 
     * Returns the index within the input string used to generate this match
     * where subexpression number <i>sub</i> ends, or <code>-1</code> if
     * the subexpression does not exist.  The initial position is zero.
     *
     * @param sub Subexpression index
     */
	int getEndIndex(int sub);
	
    /**
     * Substitute the results of this match to create a new string.
     * This is patterned after PERL, so the tokens to watch out for are
     * <code>$0</code> through <code>$9</code>.  <code>$0</code> matches
     * the full substring matched; <code>$<i>n</i></code> matches
     * subexpression number <i>n</i>.
     *
     * @param input A string consisting of literals and <code>$<i>n</i></code> tokens.
     */
	String substituteInto(String input);
}