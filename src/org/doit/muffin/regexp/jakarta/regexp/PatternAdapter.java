package org.doit.muffin.regexp.jakarta.regexp;

//import java.util.regex.Pattern;
import org.apache.regexp.RESyntaxException;
import org.doit.muffin.regexp.Pattern;
import org.doit.muffin.regexp.Matcher;
import org.doit.muffin.regexp.AbstractPatternAdapter;

import org.apache.regexp.RE;

/**
 * @author bw@xmlizer.biz
 *
 */
public class PatternAdapter extends AbstractPatternAdapter {
	
	public PatternAdapter(String pattern){
		super(pattern);
	}
	
	public PatternAdapter(String pattern, boolean ignoreCase){
		super(pattern, ignoreCase);
	}

	/**
	 * @see org.doit.muffin.regexp.Pattern#isMatch(java.lang.Object)
	 */
	public boolean matches(String input) {
		return fPattern.match(input);
	}

	/**
	 * @see org.doit.muffin.regexp.Pattern#getMatch(java.lang.Object)
	 */
	public Matcher getMatch(String input) {
		return fPattern.match(input) ? new MatcherAdapter(fPattern) : null;
		
		// alternative implemenation reusing other:
		// getMatch(input, 0);
	}

	/**
	 * @see org.doit.muffin.regexp.Pattern#getMatch(java.lang.Object, int)
	 */
	public Matcher getMatch(String input, int index) {
		return fPattern.match(input, index) ? new MatcherAdapter(fPattern) : null;
	}

	/**
	 * @see org.doit.muffin.regexp.Pattern#substituteAll(java.lang.Object, java.lang.String)
	 */
	public String substituteAll(String input, String replace) {
		return fPattern.subst(input, replace);
	}
	
	protected void doMakePattern(String pattern){
		try {
			fPattern = new org.apache.regexp.RE(pattern);
		} catch (RESyntaxException e) {
			e.printStackTrace();
		}
	}
	
	protected void doMakePatternIgnoreCase(String pattern){
		try {
			fPattern = new org.apache.regexp.RE(pattern, org.apache.regexp.RE.MATCH_CASEINDEPENDENT);
		} catch (RESyntaxException e) {
			e.printStackTrace();
		}
	}

	private RE fPattern;

}
