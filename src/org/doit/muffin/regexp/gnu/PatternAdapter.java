/* $Id: ProxyCacheBypassFilter.java,v 1.1 2003/05/25 02:51:50 cmallwitz Exp $ */

/*
 * Copyright (C) 2003 Bernhard Wagner <bw@xmlizer.biz>
 *
 * This file is part of Muffin.
 *
 * Muffin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Muffin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Muffin; see the file COPYING.  If not, write to the
 * Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */
package org.doit.muffin.regexp.gnu;

//import gnu.regexp.Pattern;
//import gnu.regexp.Matcher;
import gnu.regexp.REException;

import org.doit.muffin.regexp.Pattern;
import org.doit.muffin.regexp.Matcher;
import org.doit.muffin.regexp.AbstractPatternAdapter;
import org.doit.muffin.regexp.Factory;;

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
	
	protected void doMakePattern(String pattern){
		try {
			this.fPattern = new gnu.regexp.RE(pattern);
		} catch (REException e) {
			e.printStackTrace();
		}
	}
	
	protected void doMakePatternIgnoreCase(String pattern){
		try {
			this.fPattern = new gnu.regexp.RE(pattern, gnu.regexp.RE.REG_ICASE);
		} catch (REException e) {
			e.printStackTrace();
		}
	}
	
	private gnu.regexp.RE fPattern;
	
	// Announce this implementation to the Factory.
	// It would work in C++ where static code gets executed at any rate.
	// Not so in Java.
//	
//	static {
//		Factory.instance().addImplementation(PatternAdapter.class);
//	}
	
}