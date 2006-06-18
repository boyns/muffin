/* $Id$ */

/*
 * Copyright (C) 2003 Bernhard Wagner <muffinsrc@xmlizer.biz>
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
 * @author muffinsrc@xmlizer.biz
 *
 */
public abstract class AbstractPatternAdapter implements Pattern
{

    public AbstractPatternAdapter(String pattern)
    {
        this(pattern, false);
    }

    public AbstractPatternAdapter(String pattern, boolean ignoreCase)
    {
        if (ignoreCase)
        {
            doMakePatternIgnoreCase(pattern);
        } else
        {
            doMakePattern(pattern);
        }
    }

    /**
     * Hook method for constructor. Makes a case-sensitive Pattern.
     * 
     * @param pattern
     */
    protected abstract void doMakePattern(String pattern);

    /**
     * Hook method for constructor. Makes a case-insensitive Pattern.
     * 
     * @param pattern
     */
    protected abstract void doMakePatternIgnoreCase(String pattern);

}
