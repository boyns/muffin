package org.doit.muffin.regexp.jakarta.regexp;

import org.doit.muffin.regexp.Matcher;
import org.doit.muffin.regexp.AbstractMatcherAdapter;

import org.apache.regexp.RE;

/**
 * @author bw@xmlizer.biz
 *
 */
public class MatcherAdapter extends AbstractMatcherAdapter {
	
	public MatcherAdapter(RE pattern){
		fPattern = pattern;
	}

	/**
	 * @see org.doit.muffin.regexp.Matcher#getStartIndex()
	 */
	public int getStartIndex() {
		return fPattern.getParenStart(0);
	}

	/**
	 * @see org.doit.muffin.regexp.Matcher#getStartIndex(int)
	 */
	public int getStartIndex(int sub) {
		return fPattern.getParenStart(sub);
	}

	/**
	 * @see org.doit.muffin.regexp.Matcher#getEndIndex()
	 */
	public int getEndIndex() {
		return fPattern.getParenEnd(0);
	}

	/**
	 * @see org.doit.muffin.regexp.Matcher#getEndIndex(int)
	 */
	public int getEndIndex(int sub) {
		return fPattern.getParenEnd(sub);
	}

	protected int doGetSubCount(){
		return fPattern.getParenCount();
	}
	
	protected String doGetNthSub(int n){
		return fPattern.getParen(n);
	}
	
	private org.apache.regexp.RE fPattern;

}
