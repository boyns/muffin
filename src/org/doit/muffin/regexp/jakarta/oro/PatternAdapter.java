package org.doit.muffin.regexp.jakarta.oro;

//import java.util.regex.Pattern;
import org.doit.muffin.regexp.Pattern;
import org.doit.muffin.regexp.Matcher;
import org.doit.muffin.regexp.Factory;;

/**
 * @author bw@xmlizer.biz
 *
 */
public class PatternAdapter implements Pattern {

	
	public PatternAdapter(String pattern){
		this(pattern, false);
	}
	
	public PatternAdapter(String pattern, boolean ignoreCase){
//		throw new RuntimeException("org.doit.muffin.regexp.jakarta.oro.PatternAdapter.getMatch"
//			+" not yet implemented. Args were input <"+pattern+"> ignoreCase <"+ignoreCase+">"
//		);
	}

	/**
	 * @see org.doit.muffin.regexp.Pattern#isMatch(java.lang.Object)
	 */
	public boolean matches(String input) {
//		throw new RuntimeException("org.doit.muffin.regexp.jakarta.oro.PatternAdapter.matches"
//			+" not yet implemented. Args were input <"+input+">"
//		);
		return false;
//		can't use return fPattern.matcher(input).matches();
//		because that's an exact match!
	}

	/**
	 * @see org.doit.muffin.regexp.Pattern#getMatch(java.lang.Object)
	 */
	public Matcher getMatch(String input) {
//		throw new RuntimeException("org.doit.muffin.regexp.jakarta.oro.PatternAdapter.getMatch"
//			+" not yet implemented. Args were input <"+input+">"
//		);
		return null;
		
		// alternative implemenation reusing other:
		// getMatch(input, 0);
	}

	/**
	 * @see org.doit.muffin.regexp.Pattern#getMatch(java.lang.Object, int)
	 */
	public Matcher getMatch(String input, int index) {
//		throw new RuntimeException("org.doit.muffin.regexp.jakarta.oro.PatternAdapter.getMatch"
//			+" not yet implemented. Args were input <"+input+"> index <"+index+">"
//		);
		return null;
	}

	/**
	 * @see org.doit.muffin.regexp.Pattern#substituteAll(java.lang.Object, java.lang.String)
	 */
	public String substituteAll(String input, String replace) {
//		throw new RuntimeException("org.doit.muffin.regexp.jakarta.oro.PatternAdapter.substituteAll"
//			+" not yet implemented. Args were input <"+input+"> replace <"+replace+">"
//		);
		return null;
	}
	
	private void makePattern(String pattern){
//		throw new RuntimeException("org.doit.muffin.regexp.jakarta.oro.PatternAdapter.substituteAll"
//			+" not yet implemented. Args were input <"+pattern+">"
//		);
	}
	
	private void makePatternIgnoreCase(String pattern){
//		throw new RuntimeException("org.doit.muffin.regexp.jakarta.oro.PatternAdapter.makePatternIgnoreCase"
//			+" not yet implemented. Args were input <"+pattern+">"
//		);
	}

	public static String fPattern = "what now"; // this would need to be: org.apache.oro.text.regex.Pattern
	
	class My {
		My(String str){
			System.out.println(str);
		}
		
	}
	// Announce this implementation to the Factory.
	// It would work in C++ where static code gets executed at any rate.
	// Not so in Java.
//	
//	static {
//		Factory.instance().addImplementation(PatternAdapter.class);
//	}
}
