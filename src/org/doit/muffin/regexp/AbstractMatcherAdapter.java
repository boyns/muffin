package org.doit.muffin.regexp;

/**
 * @author bw@xmlizer.biz
 *
 */
public abstract class AbstractMatcherAdapter implements Matcher {

	/**
     * Substitute the results of a match to create a new string.
     * This is patterned after PERL, so the tokens to watch out for are
     * <code>$0</code> through <code>$9</code>.  <code>$0</code> matches
     * the full substring matched; <code>$<i>n</i></code> matches
     * subexpression number <i>n</i>. No special back slash characters
     * processing.
     * 
     * This method was contributed by:
     * <a href="mailto:C.Mallwitz@intershop.com">Christian Mallwitz</a>
     * in his posting:
     * <a href="http://sourceforge.net/mailarchive/forum.php?thread_id=2361087&forum_id=5036">2003-05-17 15:38</a>
     * 
     * I turned it into a Template Method.
     * 
     * FIXME: since this is close to identical to the corresponding method in
     * org.doit.muffin.regexp.jdk14, the common code should be factored out.
     * 
     * @param input A string consisting of literals and <code>$<i>n</i></code> tokens.
     */
	public String substituteInto(String replace) {
		StringBuffer output = new StringBuffer();
		int pos;

		for (pos = 0; pos < replace.length() - 1; pos++) {
			if ((replace.charAt(pos) == '$')
				&& (Character.isDigit(replace.charAt(pos + 1)))) {
				int val = Character.digit(replace.charAt(++pos), 10);
				if (val <= doGetSubCount()) {
					output.append(doGetNthSub(val));
				}
			} else {
				output.append(replace.charAt(pos));
			}
		}

		if (pos < replace.length()) {
			output.append(replace.charAt(pos));
		}

		return output.toString();
	}
	
	/**
	 * Hook Method for @see substituteInto. Returns the number of parenthesized subregexps.	 * @return int	 */
	protected abstract int doGetSubCount();
	
	/**
	 * Hook Method for @see substituteInto. Returns the nth parenthesized subregexp matched.
	 * @return int
	 */
	protected abstract String doGetNthSub(int n);


}
