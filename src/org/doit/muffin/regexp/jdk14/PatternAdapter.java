/* $Id$ */

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
package org.doit.muffin.regexp.jdk14;

//import java.util.regex.Pattern;
import org.doit.muffin.regexp.Pattern;
import org.doit.muffin.regexp.Matcher;
import org.doit.muffin.regexp.AbstractPatternAdapter;
import org.doit.muffin.regexp.Factory;

/**
 * @author bw@xmlizer.biz
 *
 */
public class PatternAdapter extends AbstractPatternAdapter
{

    public PatternAdapter(String pattern)
    {
        super(pattern);
    }

    public PatternAdapter(String pattern, boolean ignoreCase)
    {
        super(pattern, ignoreCase);
    }

    /**
     * @see org.doit.muffin.regexp.Pattern#isMatch(java.lang.Object)
     */
    public boolean matches(String input)
    {
        return fPattern.matcher(input).find();
    }

    /**
     * @see org.doit.muffin.regexp.Pattern#getMatch(java.lang.Object)
     */
    public Matcher getMatch(String input)
    {
        java.util.regex.Matcher matcher = fPattern.matcher(input);
        return matcher.find() ? new MatcherAdapter(matcher) : null;

        // alternative implemenation reusing other:
        // getMatch(input, 0);
    }

    /**
     * @see org.doit.muffin.regexp.Pattern#getMatch(java.lang.Object, int)
     */
    public Matcher getMatch(String input, int index)
    {
        java.util.regex.Matcher matcher = fPattern.matcher(input);
        return (matcher.find(index)) ? new MatcherAdapter(matcher) : null;
    }

    /**
     * @see org.doit.muffin.regexp.Pattern#substituteAll(java.lang.Object, java.lang.String)
     */
    public String substituteAll(String input, String replace)
    {
        return fPattern.matcher(input).replaceAll(replace);
    }

    /**
     * @see org.doit.muffin.regexp.AbstractPatternAdapter#doMakePattern(java.lang.String)
     */
    protected void doMakePattern(String pattern)
    {
        this.fPattern = java.util.regex.Pattern.compile(pattern);
    }
    /**
     * @see org.doit.muffin.regexp.AbstractPatternAdapter#doMakePatternIgnoreCase(java.lang.String)
     */
    protected void doMakePatternIgnoreCase(String pattern)
    {
        this.fPattern =
            java.util.regex.Pattern.compile(
                pattern,
                java.util.regex.Pattern.CASE_INSENSITIVE);
    }

    private java.util.regex.Pattern fPattern;

    // Announce this implementation to the Factory.
    // It would work in C++ where static code gets executed at any rate.
    // Not so in Java.
    //	
    //	static {
    //		Factory.instance().addImplementation(PatternAdapter.class);
    //	}
}
