/**
 * SelectToRadio.java -- transforms html selects to radio buttons
 * 
 * A client of mine was complaining about the web client of his
 * issue tracking tool ("TestTrack Pro", http://www.seapine.com/ttpro.html).
 * There are lots of selects where radio buttons would be a better
 * ui, because all options would be visible at once.
 *
 * @author  Bernhard Wagner <muffinsrc@xmlizer.biz>
 * @version 0.1
 *
 * Last update: 06/10/06 B.W.
 */

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

import org.doit.muffin.Filter;

public class SelectToRadio extends AbstractFilterFactory
{
	
    final static String HORIZONTAL = "horizontal";

    /**
     * @see org.doit.muffin.filter.AbstractFilterFactory#doSetDefaultPrefs()
     */
    protected void doSetDefaultPrefs()
    {
        putPrefsBoolean(HORIZONTAL, false);
    }

    /**
     * @see org.doit.muffin.filter.AbstractFilterFactory#doMakeFrame()
     */
    protected AbstractFrame doMakeFrame()
    {
        return new SelectToRadioFrame(this);
    }

    /**
     * @see org.doit.muffin.filter.AbstractFilterFactory#doMakeFilter()
     */
    protected Filter doMakeFilter()
    {
        return new SelectToRadioFilter(this);
    }

    /**
     * @see org.doit.muffin.filter.AbstractFilterFactory#getName()
     */
    public String getName()
    {
        return "SelectToRadio";
    }
}
