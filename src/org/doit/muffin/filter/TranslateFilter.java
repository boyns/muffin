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

/*
 * written by Mike Baroukh.
 */

package org.doit.muffin.filter;

import org.doit.muffin.*;
import java.util.Hashtable;
import java.util.Enumeration;

public class TranslateFilter implements RequestFilter, ReplyFilter
{
    Translate factory;
    Prefs prefs;

    public TranslateFilter(Translate factory)
    {
	this.factory = factory;
    }
    
    public void setPrefs(Prefs prefs)
    {
	this.prefs = prefs;
    }

    public void filter(Request request)
    {
	factory.translate(request);
    }

    public void filter(Reply reply)
    {
	factory.translate(reply);
    }
}

