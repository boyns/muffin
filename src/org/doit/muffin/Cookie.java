/* $Id: Cookie.java,v 1.5 2000/01/24 04:02:13 boyns Exp $ */

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
package org.doit.muffin;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

public class Cookie extends Hashtable
{
    public Cookie(String cookie, Request request)
    {
	parse(cookie, request);
    }
    
    private void parse(String cookie, Request request)
    {
	StringTokenizer st = new StringTokenizer(cookie, ";");
	while (st.hasMoreTokens())
	{
	    String token = st.nextToken();
	    token = token.trim();
	    String name;
	    String value;
	    int i = token.indexOf('=');
	    if (i != -1)
	    {
		name = token.substring(0, i);
		value = token.substring(i+1);
	    }
	    else
	    {
		name = token;
		value = "";
	    }
	    put(name, value);
	}
	if (! containsKey("domain"))
	{
	    put("domain", request.getHost());
	}
	if (! containsKey("path"))
	{
	    put("path", request.getPath());
	}
    }

    public String getDomain()
    {
	return(String) get("domain");
    }
    
    public String getPath()
    {
	return(String) get("path");
    }

    public boolean compare(Request request)
    {
	return request.getHost().endsWith(getDomain())
	    && request.getPath().startsWith(getPath());
    }

    public String toString()
    {
	StringBuffer buf = new StringBuffer();
	Enumeration e = keys();
	while (e.hasMoreElements())
	{
	    if (buf.length() > 0)
	    {
		buf.append("; ");
	    }
	    String key = (String) e.nextElement();
	    String value = (String) get(key);
	    buf.append(key);
	    if (value.length() > 0)
	    {
		buf.append("=");
		buf.append(value);
	    }
	}
	return buf.toString();
    }
}
