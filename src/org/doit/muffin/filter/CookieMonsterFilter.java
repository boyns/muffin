/* $Id: CookieMonsterFilter.java,v 1.2 1998/08/13 06:02:03 boyns Exp $ */

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

public class CookieMonsterFilter implements RequestFilter, ReplyFilter
{
    Prefs prefs;
    CookieMonster factory;
    Request request = null;

    CookieMonsterFilter (CookieMonster factory)
    {
	this.factory = factory;
    }
    
    public void setPrefs (Prefs prefs)
    {
	this.prefs = prefs;
    }
    
    public void filter (Request r) throws FilterException
    {
	this.request = r;
	
	if (r.containsHeaderField ("Cookie"))
	{
	    String cookie = r.getHeaderField ("Cookie");
	    factory.process ("Browser sending \"" + cookie + "\" for " + request.getURL () + "\n");
	    if (prefs.getBoolean ("CookieMonster.eatRequestCookies"))
	    {
		r.removeHeaderField ("Cookie");
		factory.process ("Eating " + cookie + "\n");
	    }
	}
    }
    
    public void filter (Reply r) throws FilterException
    {
	if (r.containsHeaderField ("Set-Cookie"))
	{
	    Cookie cookie = new Cookie (r.getHeaderField ("Set-Cookie"), request);
	    factory.process ("Server sent \"" + cookie.toString () + "\" for " + request.getURL () + "\n");
	    if (prefs.getBoolean ("CookieMonster.eatReplyCookies"))
	    {
		r.removeHeaderField ("Set-Cookie");
		factory.process ("Eating " + cookie.toString () + "\n");
	    }
	}
    }
}
