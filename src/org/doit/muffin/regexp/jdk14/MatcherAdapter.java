package org.doit.muffin.regexp.jdk14;

//import java.util.regex.Matcher;
import org.doit.muffin.regexp.Matcher;
import org.doit.muffin.regexp.AbstractMatcherAdapter;

/**
 * @author bw@xmlizer.biz
 *
 */
public class MatcherAdapter extends AbstractMatcherAdapter {
	
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

	protected int doGetSubCount(){
		return fMatcher.groupCount();
	}
	
	protected String doGetNthSub(int n){
		return fMatcher.group(n);
	}

	private java.util.regex.Matcher fMatcher;

}
