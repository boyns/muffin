/* $Id: Request.java,v 1.9 2000/03/08 15:25:25 boyns Exp $ */

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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import gnu.regexp.*;
import org.doit.io.*;

/**
 * @author Mark Boyns
 */
public class Request extends Message
{
    private static RE httpRegex;
    
    private String command = null;
    private String url = null;
    private String protocol = null;
    private byte[] data = null;
    private Client client = null;
    private Hashtable log;
    private Vector logHeaders;

    static
    {
	try
	{
	    httpRegex = new RE("^(http|https):", RE.REG_ICASE);
	}
	catch (REException e)
	{
	    e.printStackTrace();
	}
    }

    Request(Client c)
    {
	client = c;
    }

    void read(InputStream in) throws IOException
    {
	statusLine = readLine(in);
	if (statusLine == null || statusLine.length() == 0)
	{
	    throw new IOException("Empty request");
	}

	StringTokenizer st = new StringTokenizer(statusLine);
	command = (String) st.nextToken();
	url = (String) st.nextToken();
	protocol = (String) st.nextToken();

	if (!url.startsWith("http"))
	{
	    REMatch match = httpRegex.getMatch(url);
	    if (match != null)
	    {
		url = url.substring(match.getStartIndex(),
				    match.getEndIndex()).toLowerCase()
		    + url.substring(match.getEndIndex());
	    }
	}

	readHeaders(in);

	if ("POST".equals(command) || "PUT".equals(command))
	{
	    try
	    {
		int n = Integer.parseInt(getHeaderField("Content-length"));
		data = new byte[n];
		int offset = 0;
		while (offset < data.length)
		{
		    n = in.read(data, offset, data.length - offset);
		    if (n < 0)
		    {
			throw new IOException("Not enough " + command + " data");
		    }
		    offset += n;
		}
	    }
	    catch (NumberFormatException e)
	    {
		System.out.println("Malformed or missing " + command + " Content-length");
	    }
	}
    }

    public void write(OutputStream out)
	throws IOException
    {
	super.write(out);
	if (data != null)
	{
	    out.write(data);
	    out.flush();
	}
    }

    public String getRequest()
    {
	return statusLine;
    }

    public String getCommand()
    {
	return command;
    }
    
    public void setCommand(String command)
    {
	this.command = command;
    }

    public String getURL()
    {
	return url;
    }

    public void setURL(String url)
    {
	this.url = url;
    }

    public String getProtocol()
    {
	return protocol;
    }

    public void setProtocol(String protocol)
    {
	this.protocol = protocol;
    }

    public String getHost()
    {
	String url = getURL();
	String s;

	if (url.startsWith("http://"))
	{
	    s = url.substring(7, url.indexOf('/', 7));
	}
	else
	{
	    s = url;
	}

	int at = s.indexOf('@');
	if (at != -1 )
	{
	    s = s.substring(at+1);
	}

	if (s.indexOf(':') != -1)
	{
	    return s.substring(0, s.indexOf(':'));
	}

	return s;
    }

    public int getPort()
    {
	int port = 80;
	String url = getURL();
	String s;

	if (url.startsWith("http://"))
	{
	    s = url.substring(7, url.indexOf('/', 7));
	}
	else
	{
	    s = url;
	}

	int at = s.indexOf('@');
	if (at != -1 )
	{
	    s = s.substring(at+1);
	}

	if (s.indexOf(':') != -1)
	{
	    try
	    {
		port = Integer.parseInt(s.substring(s.indexOf(':') + 1));
	    }
	    catch (NumberFormatException e)
	    {
		System.out.println("Invalid port in " + url);
	    }
	}
	return port;
    }

    public String getData()
    {
	if (data == null)
	{
	    return null;
	}
	return new String(data);
    }

    public String getPath()
    {	
	String str = getURL();
	int pos = 0;
	for (int i = 0; i < 3; i++)
	{
	    pos = str.indexOf('/', pos);
	    pos++;
	}
	pos--;
	return str.substring(pos);
    }
    
    public String getDocument()
    {
	String path = getPath();
	int n = path.lastIndexOf('/');
	if (n == path.length() - 1)
	{
	    n = path.lastIndexOf('/', n - 1);
	}
	if (n < 0)
	{
	    return "/";
	}
	else
	{
	    return path.substring(n + 1);
	}
    }

    public Client getClient()
    {
	return client;
    }

    public String getQueryString()
    {
	String path = getPath();
	int n = path.indexOf('?');
	if (n < 0)
	{
	    return null;
	}
	return path.substring(n + 1);
    }

    public synchronized void addLogEntry(String header,
					 String message)
    {
	if (log == null)
	{
	    log = new Hashtable();
	    logHeaders = new Vector();
	}

	Vector v = (Vector)log.get(header);
	if (log.get(header) == null)
	{
	    v = new Vector();
	    log.put(header, v);
	    logHeaders.addElement(header);
	}
	v.addElement(message);
    }

    public Enumeration getLogHeaders()
    {
	return logHeaders != null ? logHeaders.elements() : null;
    }

    public Enumeration getLogEntries(String header)
    {
	return log != null ? ((Vector)log.get(header)).elements() : null;
    }
}
