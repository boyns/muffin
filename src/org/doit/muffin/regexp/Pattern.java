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
import java.lang.StringBuffer;
import java.lang.Object;
import java.lang.String;
/**
 * @author bw@xmlizer.biz
 *
 */
public interface Pattern {
	
	/**
	 * Returns true if pattern matches the string given in input, false otherwise.
	 * 	 * @param input	 * @return boolean	 */
	boolean matches(String input);
	
//	boolean isMatch(Object input, int index);
//	boolean isMatch(Object input, int index, int eflags);

  /**
   * Returns the first match found in the input.
   * If no match is found, returns null.
   *
   * @param input The input text.
   */
	Matcher getMatch(String input);
	
  /**
   * Returns the first match found in the input, beginning
   * the search at the specified index.
   * If no match is found, returns null.
   *
   * @param input The input text.
   * @param index The offset within the text to begin looking for a match.
   */
	Matcher getMatch(String input, int index);
	
//	Matcher getMatch(Object input, int index, int eflags);
//	Matcher getMatch(Object input, int index, int eflags, StringBuffer buffer);
//	String substitute(Object input, String replace);
//	String substitute(Object input, String replace, int index);
//	String substitute(Object input, String replace, int index, int eflags);

  /**
   * Substitutes the replacement text for each non-overlapping match found 
   * in the input text.
   *
   * @param input The input text.
   * @param replace The replacement text, which may contain $x metacharacters (see Matcher.substituteInto).
   * @see Matcher#substituteInto
   */
	String substituteAll(String input, String replace);
	
//	String substituteAll(Object input, String replace, int index);
//	String substituteAll(Object input, String replace, int index, int eflags);

	String toString();
}