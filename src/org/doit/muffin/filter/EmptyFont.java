/* $Id: EmptyFont.java,v 1.7 2003/06/03 23:09:29 forger77 Exp $ */

/*
 * Copyright (C) 1996-2000 Mark R. Boyns <boyns@doit.org>
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
package org.doit.muffin.filter;

import org.doit.muffin.*;

public class EmptyFont extends AbstractFilterFactory
{

    static final String DEBUG = "debug";

    /**
     * 
     * @see org.doit.muffin.filter.AbstractFilterFactory#doSetDefaultPrefs()
     */
    protected void doSetDefaultPrefs()
    {
        putPrefsBoolean(DEBUG, false);
    }

    /**
     * @see org.doit.muffin.filter.AbstractFilterFactory#doMakeFrame()
     */
    protected AbstractFrame doMakeFrame()
    {
        return new EmptyFontFrame(this);
    }

    /**
     * @see org.doit.muffin.filter.AbstractFilterFactory#doMakeFilter()
     */
    protected Filter doMakeFilter()
    {
        return new EmptyFontFilter(this);
    }

    /**
     * @see org.doit.muffin.filter.AbstractFilterFactory#getName()
     */
    public String getName()
    {
        return "EmptyFont";
    }

    /**
     * Reports to the MessageArea if debug is true in Prefs.
     * Actually, all calls to debug in @see org.doit.muffin.EmptyFontFilter are
     * commented. Thus, this method never gets called...
     * @param message The message to report.
     */
    void debug(String message)
    {
        if (getPrefsBoolean(DEBUG))
        {
            report(message);
        }
    }
}
