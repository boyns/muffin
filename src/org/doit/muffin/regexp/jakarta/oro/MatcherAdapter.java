package org.doit.muffin.regexp.jakarta.oro;

import org.doit.muffin.regexp.Matcher;

/**
 * @author bw@xmlizer.biz
 *
 */
public class MatcherAdapter implements Matcher {
	
	// matcher would need to be: org.apache.oro.text.regex.MatchResult
	public MatcherAdapter(Object matcher){
		throw new RuntimeException("org.doit.muffin.regexp.jakarta.oro.MatcherAdapter ctor"
			+" not yet implemented. Args were input <"+matcher+">"
		);
	}

	/**
	 * @see org.doit.muffin.regexp.Matcher#getStartIndex()
	 */
	public int getStartIndex() {
		throw new RuntimeException("org.doit.muffin.regexp.jakarta.oro.MatcherAdapter.getStartIndex"
		);
	}

	/**
	 * @see org.doit.muffin.regexp.Matcher#getStartIndex(int)
	 */
	public int getStartIndex(int sub) {
		throw new RuntimeException("org.doit.muffin.regexp.jakarta.oro.MatcherAdapter.getStartIndex"
			+" not yet implemented. Args were sub <"+sub+">"
		);
	}

	/**
	 * @see org.doit.muffin.regexp.Matcher#getEndIndex()
	 */
	public int getEndIndex() {
		throw new RuntimeException("org.doit.muffin.regexp.jakarta.oro.MatcherAdapter.getEndIndex"
		);
	}

	/**
	 * @see org.doit.muffin.regexp.Matcher#getEndIndex(int)
	 */
	public int getEndIndex(int sub) {
		throw new RuntimeException("org.doit.muffin.regexp.jakarta.oro.MatcherAdapter.getEndIndex"
			+" not yet implemented. Args were sub <"+sub+">"
		);
	}

	/**
	 * @see org.doit.muffin.regexp.Matcher#substituteInto(java.lang.String)
	 */
	public String substituteInto(String input) {
		throw new RuntimeException("org.doit.muffin.regexp.jakarta.oro.MatcherAdapter.substituteInto"
			+" not yet implemented. Args were sub <"+input+">"
		);
	}

	private Object fMatcher; // this would need to be: org.apache.oro.text.regex.MatchResult

}
