package org.doit.muffin.regexp.jdk14;

//import java.util.regex.Matcher;
import org.doit.muffin.regexp.Matcher;

/**
 * @author bw@xmlizer.biz
 *
 */
public class MatcherAdapter implements Matcher {
	
	public MatcherAdapter(java.util.regex.Matcher matcher){
		this.fMatcher = matcher;
	}

	/**
	 * @see org.doit.muffin.regexp.Matcher#getStartIndex()
	 */
	public int getStartIndex() {
		return fMatcher.start();
	}

	/**
	 * @see org.doit.muffin.regexp.Matcher#getStartIndex(int)
	 */
	public int getStartIndex(int sub) {
		return fMatcher.start(sub);
	}

	/**
	 * @see org.doit.muffin.regexp.Matcher#getEndIndex()
	 */
	public int getEndIndex() {
		return fMatcher.end();
	}

	/**
	 * @see org.doit.muffin.regexp.Matcher#getEndIndex(int)
	 */
	public int getEndIndex(int sub) {
		return fMatcher.end(sub);
	}

	/**
	 * @see org.doit.muffin.regexp.Matcher#substituteInto(java.lang.String)
	 */
	public String substituteInto(String input) {
		return substituteInto(fMatcher, input);
	}
	
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
     * @param input A string consisting of literals and <code>$<i>n</i></code> tokens.
     */
	private static String substituteInto(java.util.regex.Matcher m, String replace) {
		StringBuffer output = new StringBuffer();
		int pos;

		for (pos = 0; pos < replace.length() - 1; pos++) {
			if ((replace.charAt(pos) == '$')
				&& (Character.isDigit(replace.charAt(pos + 1)))) {
				int val = Character.digit(replace.charAt(++pos), 10);
				if (val <= m.groupCount()) {
					output.append(m.group(val));
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

	private java.util.regex.Matcher fMatcher;

}
