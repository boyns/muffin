package org.doit.muffin.regexp.jdk14;

//import java.util.regex.Pattern;
import org.doit.muffin.regexp.Pattern;
import org.doit.muffin.regexp.Matcher;

/**
 * @author bw@xmlizer.biz
 *
 */
public class PatternAdapter implements Pattern {

	
	public PatternAdapter(String pattern){
		this(pattern, false);
	}
	
	public PatternAdapter(String pattern, boolean ignoreCase){
		if (ignoreCase){
			makePatternIgnoreCase(pattern);
		} else {
			makePattern(pattern);
		}
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
	
	private void makePattern(String pattern){
		this.fPattern = java.util.regex.Pattern.compile(pattern);
	}
	
	private void makePatternIgnoreCase(String pattern){
		this.fPattern = java.util.regex.Pattern.compile(
			pattern,
			java.util.regex.Pattern.CASE_INSENSITIVE
		);
	}

	private java.util.regex.Pattern fPattern;

}
