package org.doit.muffin.regexp.jakarta.oro;

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
		throw new RuntimeException("org.doit.muffin.regexp.jakarta.oro.PatternAdapter.getMatch"
			+" not yet implemented. Args were input <"+pattern+"> ignoreCase <"+ignoreCase+">"
		);
	}

	/**
	 * @see org.doit.muffin.regexp.Pattern#isMatch(java.lang.Object)
	 */
	public boolean matches(String input) {
		throw new RuntimeException("org.doit.muffin.regexp.jakarta.oro.PatternAdapter.matches"
			+" not yet implemented. Args were input <"+input+">"
		);
			
//		can't use return fPattern.matcher(input).matches();
//		because that's an exact match!
	}

	/**
	 * @see org.doit.muffin.regexp.Pattern#getMatch(java.lang.Object)
	 */
	public Matcher getMatch(String input) {
		throw new RuntimeException("org.doit.muffin.regexp.jakarta.oro.PatternAdapter.getMatch"
			+" not yet implemented. Args were input <"+input+">"
		);
		
		// alternative implemenation reusing other:
		// getMatch(input, 0);
	}

	/**
	 * @see org.doit.muffin.regexp.Pattern#getMatch(java.lang.Object, int)
	 */
	public Matcher getMatch(String input, int index) {
		throw new RuntimeException("org.doit.muffin.regexp.jakarta.oro.PatternAdapter.getMatch"
			+" not yet implemented. Args were input <"+input+"> index <"+index+">"
		);
	}

	/**
	 * @see org.doit.muffin.regexp.Pattern#substituteAll(java.lang.Object, java.lang.String)
	 */
	public String substituteAll(String input, String replace) {
		throw new RuntimeException("org.doit.muffin.regexp.jakarta.oro.PatternAdapter.substituteAll"
			+" not yet implemented. Args were input <"+input+"> replace <"+replace+">"
		);
	}
	
	private void makePattern(String pattern){
		throw new RuntimeException("org.doit.muffin.regexp.jakarta.oro.PatternAdapter.substituteAll"
			+" not yet implemented. Args were input <"+pattern+">"
		);
	}
	
	private void makePatternIgnoreCase(String pattern){
		throw new RuntimeException("org.doit.muffin.regexp.jakarta.oro.PatternAdapter.makePatternIgnoreCase"
			+" not yet implemented. Args were input <"+pattern+">"
		);
	}

	private Object fPattern; // this would need to be: org.apache.oro.text.regex.Pattern

}
