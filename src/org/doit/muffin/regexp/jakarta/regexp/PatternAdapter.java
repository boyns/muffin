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
package org.doit.muffin.regexp.jakarta.regexp;

import java.util.*;

import org.apache.regexp.RESyntaxException;
import org.doit.muffin.regexp.Pattern;
import org.doit.muffin.regexp.Matcher;
import org.doit.muffin.regexp.AbstractPatternAdapter;
import org.doit.muffin.regexp.Factory;

import org.apache.regexp.RE;

/**
 * @author bw@xmlizer.biz
 *
 */
public class PatternAdapter extends AbstractPatternAdapter
{

    public PatternAdapter(String pattern)
    {
        super(pattern);
        fStringPattern = pattern;
    }

    public PatternAdapter(String pattern, boolean ignoreCase)
    {
        super(pattern, ignoreCase);
        fStringPattern = pattern;
    }

    public String toString()
    {
        return fStringPattern;
    }

    /**
     * @see org.doit.muffin.regexp.Pattern#isMatch(java.lang.Object)
     */
    public boolean matches(String input)
    {
        return fPattern.match(input);
    }

    /**
     * @see org.doit.muffin.regexp.Pattern#getMatch(java.lang.Object)
     */
    public Matcher getMatch(String input)
    {
        return fPattern.match(input) ? new MatcherAdapter(fPattern) : null;

        // alternative implemenation reusing other:
        // getMatch(input, 0);
    }

    /**
     * @see org.doit.muffin.regexp.Pattern#getMatch(java.lang.Object, int)
     */
    public Matcher getMatch(String input, int index)
    {
        return fPattern.match(input, index)
            ? new MatcherAdapter(fPattern)
            : null;
    }

    /**
     * @see org.doit.muffin.regexp.Pattern#substituteAll(java.lang.Object, java.lang.String)
     */
    public String substituteAll(String input, String replace)
    {
        //		unfortunately jakarta.regexp does not support the PERL $x Syntax, 
        //		thus we can't do this:
        //		return fPattern.subst(input, replace); 

        String[] split = fPattern.split(input);

        int position = 0;
        Matcher matcher;
        List replacedMatches = new ArrayList();
        while ((matcher = getMatch(input, position)) != null)
        {
            int start = matcher.getStartIndex();
            int end = matcher.getEndIndex();
            if (start < 0)
                break;
            position = end;
            String sub = input.substring(start, end);
            replacedMatches.add(matcher.substituteInto(replace));
        }

        Iterator it = replacedMatches.iterator();
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < split.length; i++)
        {
            result.append(split[i]);
            if (it.hasNext())
            {
                result.append(it.next());
            }
        }
        return result.toString();
    }

    protected void doMakePattern(String pattern)
    {
        try
        {
            fPattern = new org.apache.regexp.RE(pattern);
        } catch (RESyntaxException e)
        {
            e.printStackTrace();
        }
    }

    protected void doMakePatternIgnoreCase(String pattern)
    {
        try
        {
            fPattern =
                new org.apache.regexp.RE(
                    pattern,
                    org.apache.regexp.RE.MATCH_CASEINDEPENDENT);
        } catch (RESyntaxException e)
        {
            e.printStackTrace();
        }
    }

    private RE fPattern;
    private String fStringPattern;

    // Announce this implementation to the Factory.
    // It would work in C++ where static code gets executed at any rate.
    // Not so in Java.
    //	
    //	static {
    //		Factory.instance().addImplementation(PatternAdapter.class);
    //	}
}
