/* $Id: HostnameExpanderFilter.java,v 1.6 2000/01/24 04:02:20 boyns Exp $ */

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
import java.net.InetAddress;
import java.net.UnknownHostException;

public class HostnameExpanderFilter implements RedirectFilter
{
    Prefs prefs;
    HostnameExpander factory;

    HostnameExpanderFilter(HostnameExpander factory)
    {
	this.factory = factory;
    }
    
    public void setPrefs(Prefs prefs)
    {
	this.prefs = prefs;
    }

    public boolean needsRedirection(Request r)
    {
	String host = r.getHost();
	return host.indexOf(".") < 0; /* doesn't have a dot */
    }
    
    public String redirect(Request r)
    {
	String domain = prefs.getString("HostnameExpander.defaultDomain");
	String host = expandHostname(r.getHost());
	if (host == null && domain.length() > 0)
	{
	    host = r.getHost();
	    if (! domain.startsWith("."))
	    {
		host = host + ".";
	    }
	    host = host + domain;
	}
	if (host != null)
	{
	    StringBuffer buf = new StringBuffer();
	    buf.append("http://");
	    buf.append(host);
	    if (r.getPort() != 80)
	    {
		buf.append(":");
		buf.append(r.getPort());
	    }
	    buf.append(r.getPath());
	    factory.report(r, r.getHost() + " -> " + buf.toString());
	    return buf.toString();
	}
	return null;
    }

    /**
     * Return true iff request is redirected.  A request will be
     * redirected if the requested host is not fully qualified and is
     * not locally resolvable and *is* resovable to
     * www.$host.{com,edu,net,org}.
     */
    String expandHostname(String host)
    {
        String resolve;
        int pos;
        InetAddress address;
                
	// this could be a local address attempt to resolve
	try
	{
	    //address = InetAddress.getByName(host);
	    address = MuffinResolver.getByName(host);
	}
	catch (UnknownHostException uhe)
	{
	    address = null;
	}

	if (address == null)
	{
	    String prefix[] = prefs.getStringList("HostnameExpander.prefix");
	    String suffix[] = prefs.getStringList("HostnameExpander.suffix");
	    
	    for (int i = 0; i < prefix.length; i++)
	    {
		for (int j = 0; j < suffix.length; j++)
		{
		    resolve = prefix[i] + host + suffix[j];
		    try
		    {
			//address = InetAddress.getByName(resolve);
			address = MuffinResolver.getByName(resolve);
		    }
		    catch (UnknownHostException uhe)
		    {
			address = null;
		    }
		    if (address != null)
		    {
			return resolve;
		    }
		}
	    }
	}
	
        return null;
    }
}
