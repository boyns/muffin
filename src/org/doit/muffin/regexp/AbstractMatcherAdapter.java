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
package org.doit.muffin.regexp;

/**
 * @author bw@xmlizer.biz
 *
 */
public abstract class AbstractMatcherAdapter implements Matcher {

	/**
     * Substitute the results of a match to create a new string.
     * This is patterned after PERL, so the tokens to watch out for are
     * <code>$0</code> through <code>$9</code>.  <code>$0</code> matches
     * the full substring matched; <code>$<i>n</i></code> matches
     * subexpression number <i>n</i>. No special back slash characters
     * processing.
     * 
     * This method was contributed by:
     * <a href="mailto:C.Mallwitz@intershop.com">Christian Mallwitz</a>
     * in his posting:
     * <a href="http://sourceforge.net/mailarchive/forum.php?thread_id=2361087&forum_id=5036">2003-05-17 15:38</a>
     * 
     * I turned it into a Template Method.
     * 
     * @param input A string consisting of literals and <code>$<i>n</i></code> tokens.
     */
	public String substituteInto(String replace) {
		StringBuffer output = new StringBuffer();
		int pos;

		for (pos = 0; pos < replace.length() - 1; pos++) {
			if ((replace.charAt(pos) == '$')
				&& (Character.isDigit(replace.charAt(pos + 1)))) {
				int val = Character.digit(replace.charAt(++pos), 10);
				if (val <= doGetSubCount()) {
					output.append(doGetNthSub(val));
				}
			} else {
				output.append(replace.charAt(pos));
			}
		}

		if (pos < replace.length()) {
			output.append(replace.charAt(pos));
		}

		return output.toString();
	}
	
	/**
	 * Hook Method for @see substituteInto.
	 * Returns the number of parenthesized subregexps.	 * 
	 * @return int	 */
	protected abstract int doGetSubCount();
	
	/**
	 * Hook Method for @see substituteInto. Returns the nth parenthesized subregexp matched.
	 * @param int Which parenthesized subregexp to return.
	 * 
	 * @return String The nth parenthesized subregexp.
	 */
	protected abstract String doGetNthSub(int n);


}
