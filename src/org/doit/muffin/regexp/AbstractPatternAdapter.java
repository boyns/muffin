package org.doit.muffin.regexp;

/**
 * @author bw@xmlizer.biz
 *
 */
public abstract class AbstractPatternAdapter implements Pattern {

		
	public AbstractPatternAdapter(String pattern){
		this(pattern, false);
	}
	
	public AbstractPatternAdapter(String pattern, boolean ignoreCase){
		if (ignoreCase){
			doMakePatternIgnoreCase(pattern);
		} else {
			doMakePattern(pattern);
		}
	}

	/**
	 * Hook method for constructor. Makes a case-sensitive Pattern.
	 * 	 * @param pattern	 */
	protected abstract void doMakePattern(String pattern);
	
	/**
	 * Hook method for constructor. Makes a case-insensitive Pattern.
	 * 
	 * @param pattern
	 */
	protected abstract void doMakePatternIgnoreCase(String pattern);

}
