/* $Id: HistoryFilter.java,v 1.5 2000/01/24 04:02:20 boyns Exp $ */

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
import org.doit.io.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayInputStream;
import java.util.Hashtable;
import java.util.Enumeration;

public class HistoryFilter implements HttpFilter, RequestFilter
{
    Prefs prefs;
    History history;
    Reply reply;
    Request request;

    public HistoryFilter(History history)
    {
	this.history = history;
    }
    
    public void setPrefs(Prefs prefs)
    {
	this.prefs = prefs;
    }

    public void filter(Request request)
    {
	String url = request.getURL();
	synchronized(history)
	{
	    HistoryData b = history.get(url);
	    b.count++;
	    b.time = System.currentTimeMillis();
	}
    }

    public boolean wantRequest(Request request)
    {
	return request.getURL().startsWith("http://" + history.getClass().getName());
    }

    public void sendRequest(Request request)
    {
    }

    public Reply recvReply(Request request)
    {
	Reply reply = new Reply();

	reply.setStatusLine("HTTP/1.0 200 Ok");
	reply.setHeaderField("Content-type", "text/html");

	Enumeration e = history.sortByCount();
	StringBuffer buf = new StringBuffer();

	buf.append("<ul>\n");
	while (e.hasMoreElements())
	{
	    HistoryData b = (HistoryData) e.nextElement();
	    buf.append("<li><a href=\"" + b.url + "\">" + b.url + "</a> -> " + b.count + " " + b.time + "\n");
	}
	buf.append("</ul>\n");
	
	byte bytes[] = buf.toString().getBytes();
	reply.setHeaderField("Content-length", Integer.toString(bytes.length));
	reply.setContent((InputStream) new ByteArrayInputStream(bytes));
	
	return reply;
    }

    public void close()
    {
    }
}
