package org.doit.muffin.regexp.gnu;

import gnu.regexp.REException;

import org.doit.muffin.regexp.Pattern;
import org.doit.muffin.regexp.Matcher;
//import gnu.regexp.Pattern;
//import gnu.regexp.Matcher;

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
	
	public PatternAdapter(gnu.regexp.RE re){
		this.fPattern = re;
	}
	
	public boolean matches(String input){
		return fPattern.getMatch(input) != null;
		
		// cannot use fPattern.isMatch() because that's an EXACT match!

	}
	
	public Matcher getMatch(String input){
		gnu.regexp.REMatch matcher = fPattern.getMatch(input);
		return matcher == null ? null :
			new MatcherAdapter(matcher);
	}
	
	public Matcher getMatch(String input, int index){
		gnu.regexp.REMatch matcher = fPattern.getMatch(input, index);
		return matcher == null ? null :
			new MatcherAdapter(matcher);
	}
	
	public String substituteAll(String input, String replace){
		return fPattern.substituteAll(input, replace);
	}
	
	public String toString(){
		return fPattern.toString();
	}
	
	private void makePattern(String pattern){
		try {
			this.fPattern = new gnu.regexp.RE(pattern);
		} catch (REException e) {
			e.printStackTrace();
		}
	}
	
	private void makePatternIgnoreCase(String pattern){
		try {
			this.fPattern = new gnu.regexp.RE(pattern, gnu.regexp.RE.REG_ICASE);
		} catch (REException e) {
			e.printStackTrace();
		}
	}
	
	private gnu.regexp.RE fPattern;
	
}