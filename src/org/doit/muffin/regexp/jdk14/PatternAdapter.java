package org.doit.muffin.regexp.jdk14;

//import java.util.regex.Pattern;
import org.doit.muffin.regexp.Pattern;
import org.doit.muffin.regexp.Matcher;
import org.doit.muffin.regexp.AbstractPatternAdapter;

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
		return fPattern.matcher(input).find();
			
//		can't use return fPattern.matcher(input).matches();
//		because that's an exact match!
	}

	/**
	 * @see org.doit.muffin.regexp.Pattern#getMatch(java.lang.Object)
	 */
	public Matcher getMatch(String input) {
		java.util.regex.Matcher matcher = fPattern.matcher(input);
		return matcher.find() ? new MatcherAdapter(matcher) : null;
		
		// alternative implemenation reusing other:
		// getMatch(input, 0);
	}

	/**
	 * @see org.doit.muffin.regexp.Pattern#getMatch(java.lang.Object, int)
	 */
	public Matcher getMatch(String input, int index) {
		java.util.regex.Matcher matcher = fPattern.matcher(input);
		return (matcher.find(index)) ?	new MatcherAdapter(matcher) : null;
	}

	/**
	 * @see org.doit.muffin.regexp.Pattern#substituteAll(java.lang.Object, java.lang.String)
	 */
	public String substituteAll(String input, String replace) {
		return fPattern.matcher(input).replaceAll(replace);
	}

	/**
	 * @see org.doit.muffin.regexp.AbstractPatternAdapter#doMakePattern(java.lang.String)
	 */
	protected void doMakePattern(String pattern) {
		this.fPattern = java.util.regex.Pattern.compile(pattern);
	}
	/**
	 * @see org.doit.muffin.regexp.AbstractPatternAdapter#doMakePatternIgnoreCase(java.lang.String)
	 */
	protected void doMakePatternIgnoreCase(String pattern) {
		this.fPattern = java.util.regex.Pattern.compile(
			pattern,
			java.util.regex.Pattern.CASE_INSENSITIVE
		);
	}

	private java.util.regex.Pattern fPattern;

}
