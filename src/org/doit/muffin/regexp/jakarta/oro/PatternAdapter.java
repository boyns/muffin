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
