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
