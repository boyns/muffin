/* $Id: RefererFilter.java,v 1.6 2000/03/30 06:34:51 boyns Exp $ */

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

public class RefererFilter implements RequestFilter
{
    Prefs prefs;
    Referer factory;

    RefererFilter(Referer factory)
    {
	this.factory = factory;
    }
    
    public void setPrefs(Prefs prefs)
    {
	this.prefs = prefs;
    }

    String getHost(String url)
    {
	String s;
	
    	if (url.startsWith("http://"))
	{
	    s = url.substring(7, url.indexOf('/', 7));
	}
	else
	{
	    s = url;
	}

	if (s.indexOf(':') != -1)
	{
	    return s.substring(0, s.indexOf(':'));
	}
	return s;
    }

    String getDomain(String host)
    {
	int i = host.lastIndexOf('.');
	if (i == -1)
	{
	    return null;
	}
	int d = host.lastIndexOf('.', i-1);
	if (d == -1)
	{
	    return null;
	}
	return host.substring(d + 1);
    }
    
    public void filter(Request r) throws FilterException
    {
	String s = prefs.getString("Referer.referer");
	if (s == null || s.length() == 0)
	{
	    return;
	}
	
	if (r.containsHeaderField("Referer") && prefs.getBoolean("Referer.allowSameDomain"))
	{
	    String refdom = getDomain(getHost(r.getHeaderField("Referer")));
	    String reqdom = getDomain(r.getHost());
	    if (refdom != null && reqdom != null && refdom.equalsIgnoreCase(reqdom))
	    {
		return;
	    }
	}
	    
	if (s.equalsIgnoreCase("NONE"))
	{
	    r.removeHeaderField("Referer");
	}
	else
	{
	    r.setHeaderFields("Referer", s);
	}
    }
}
