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
import java.lang.String;
import java.lang.Object;
/**
 * @author bw@xmlizer.biz
 * 
 * The interface Matcher models the subset of methods from gnu.regexp
 * that are used within muffin. Thus, 
 * the subexpressions are indexed starting with one, not zero. 
 *
 */

public interface Matcher {

    /**
     * Returns the index within the input text where the match in its entirety
     * began.
     */
	int getStartIndex();
	
    /** 
     * Returns the index within the input string used to generate this match
     * where subexpression number <i>sub</i> begins, or <code>-1</code> if
     * the subexpression does not exist.  The initial position is zero.
     *
     * @param sub Subexpression index
     */
	int getStartIndex(int sub);
	
    /**
     * Returns the index within the input string where the match in
     * its entirety ends.  The return value is the next position after
     * the end of the string; therefore, a match created by the
     * following call:
     *
     * <P>
     * <code>REMatch myMatch = myExpression.getMatch(myString);</code>
     * <P>
     * can be viewed (given that myMatch is not null) by creating
     * <P>
     * <code>String theMatch = myString.substring(myMatch.getStartIndex(),
     * myMatch.getEndIndex());</code>
     * <P>
     * But you can save yourself that work, since the <code>toString()</code>
     * method (above) does exactly that for you.  
     */
	int getEndIndex();
    
    /** 
     * Returns the index within the input string used to generate this match
     * where subexpression number <i>sub</i> ends, or <code>-1</code> if
     * the subexpression does not exist.  The initial position is zero.
     *
     * @param sub Subexpression index
     */
	int getEndIndex(int sub);
	
    /**
     * Substitute the results of this match to create a new string.
     * This is patterned after PERL, so the tokens to watch out for are
     * <code>$0</code> through <code>$9</code>.  <code>$0</code> matches
     * the full substring matched; <code>$<i>n</i></code> matches
     * subexpression number <i>n</i>.
     *
     * @param input A string consisting of literals and <code>$<i>n</i></code> tokens.
     */
	String substituteInto(String input);
}