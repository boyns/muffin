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

//import java.util.regex.Matcher;
import org.doit.muffin.regexp.Matcher;
import org.doit.muffin.regexp.AbstractMatcherAdapter;

/**
 * @author bw@xmlizer.biz
 *
 */
public class MatcherAdapter extends AbstractMatcherAdapter
{

    public MatcherAdapter(java.util.regex.Matcher matcher)
    {
        this.fMatcher = matcher;
    }

    /**
     * @see org.doit.muffin.regexp.Matcher#getStartIndex()
     */
    public int getStartIndex()
    {
        return fMatcher.start();
    }

    /**
     * @see org.doit.muffin.regexp.Matcher#getStartIndex(int)
     */
    public int getStartIndex(int sub)
    {
        return fMatcher.start(sub);
    }

    /**
     * @see org.doit.muffin.regexp.Matcher#getEndIndex()
     */
    public int getEndIndex()
    {
        return fMatcher.end();
    }

    /**
     * @see org.doit.muffin.regexp.Matcher#getEndIndex(int)
     */
    public int getEndIndex(int sub)
    {
        return fMatcher.end(sub);
    }

    protected int doGetSubCount()
    {
        return fMatcher.groupCount();
    }

    protected String doGetNthSub(int n)
    {
        return fMatcher.group(n);
    }

    private java.util.regex.Matcher fMatcher;

}
