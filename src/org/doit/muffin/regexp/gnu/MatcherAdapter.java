package org.doit.muffin.regexp.gnu;

import org.doit.muffin.regexp.Matcher;

/**
 * @author bernhard.wagner
 *
 */
public class MatcherAdapter implements Matcher {
	
	public MatcherAdapter(gnu.regexp.REMatch rematch){
		this.fMatcher = rematch;
	}

	public int getStartIndex() {
		return fMatcher.getStartIndex();
	}

	public int getEndIndex() {
		return fMatcher.getEndIndex();
	}

	public int getStartIndex(int sub) {
		return fMatcher.getStartIndex(sub);
	}

	public int getEndIndex(int sub) {
		return fMatcher.getEndIndex(sub);
	}

	public String substituteInto(String input) {
		return fMatcher.substituteInto(input);
	}
	
	private gnu.regexp.REMatch fMatcher;
}
