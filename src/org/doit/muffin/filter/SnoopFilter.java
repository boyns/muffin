/* SnoopFilter.java */

/*
 * Copyright (C) 1996-98 Mark R. Boyns <boyns@doit.org>
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

public class SnoopFilter implements RequestFilter, ReplyFilter
{
    Prefs prefs;
    Snoop factory;

    SnoopFilter (Snoop factory)
    {
	this.factory = factory;
    }
    
    public void setPrefs (Prefs prefs)
    {
	this.prefs = prefs;
    }
    
    public void filter (Request r) throws FilterException
    {
	factory.process (r.toString (System.getProperty ("line.separator")));
    }
    
    public void filter (Reply r) throws FilterException
    {
	factory.process (r.toString (System.getProperty ("line.separator")));
    }
}
