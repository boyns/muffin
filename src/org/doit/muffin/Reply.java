/* Reply.java */

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
package org.doit.muffin;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.io.SequenceInputStream;
import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 * @author Mark Boyns
 */
public class Reply extends Message
{
    InputStream in = null;

    public Reply ()
    {
    }

    public Reply (InputStream in)
    {
	setContent (in);
    }

    public void setContent (InputStream in)
    {
	this.in = in;
    }

    public InputStream getContent ()
    {
	return in;
    }
    
    void read () throws IOException
    {
	if (in != null)
	{
	    read (in);
	}
    }
    
    void read (InputStream in) throws IOException
    {
	statusLine = readLine (in);
	if (statusLine == null || statusLine.length () == 0)
	{
	    throw new IOException ("Missing HTTP status line");
	}

	/* Look for HTTP/0.9 */
	if (!statusLine.startsWith ("HTTP"))
	{
	    /* Put back the line */
	    if (this.in != null)
	    {
		String putback = new String (statusLine + "\n");
		this.in = new SequenceInputStream (new StringBufferInputStream (putback), in);
	    }
	    /* Fake a status line and upgrade to HTTP/1.0 */
	    statusLine = "HTTP/1.0 200 OK";
	    return;
	}
	
	readHeaders (in);
	int code = getStatusCode ();

	/* RFC 2068: 204 and 304 MUST NOT contain a message body. */
	switch (code)
	{
	case 204: /* No Content */
	case 304: /* Not Modified */
	    /* Ignore the message body if it exists */
	    if (containsHeaderField ("Content-length"))
	    {
		System.out.println ("RFC 2068: Ignoring message-body from " + code + " response - "
				    + getHeaderField ("Server"));
		int contentLength = 0;
		try 
		{
		    contentLength = Integer.parseInt (getHeaderField ("Content-length"));
		}
		catch (Exception e)
		{
		}
		int n;
		byte buffer[] = new byte[8192];
		while ((n = in.read (buffer, 0, buffer.length)) > 0)
		{
		    /* ignore */
		}
		removeHeaderField ("Content-length");
	    }
	    break;
	}
    }

    void write (OutputStream out) throws IOException
    {
	String s = toString ();
	out.write (s.getBytes (), 0, s.length ());
	out.flush ();
    }

    public boolean hasContent ()
    {
	switch (getStatusCode ())
	{
	case 204:
	case 304:
	    return false;

	default:
	    return true;
	}
    }

    public String getProtocol ()
    {
	StringTokenizer st = new StringTokenizer (statusLine);
	String protocol = (String) st.nextToken ();
	return protocol;
    }

    public int getStatusCode ()
    {
	StringTokenizer st = new StringTokenizer (statusLine);
	String protocol = (String) st.nextToken ();
	String status = (String) st.nextToken ();
	int code = 0;
	try
	{
	    code = Integer.parseInt (status);
	}
	catch (Exception e)
	{
	    System.out.println ("Malformed or missing status code");
	}
	return code;
    }

    private Hashtable headerParser (String header)
    {
	Hashtable table = new Hashtable ();
	String type = getHeaderField (header);
	if (type == null)
	{
	    return table;
	}

	StringTokenizer st = new StringTokenizer (type, ";");
	int count = 0;
	while (st.hasMoreTokens ())
	{
	    String token = st.nextToken ();
	    token = token.trim ();
	    String name;
	    String value;
	    int i = token.indexOf ('=');
	    if (i != -1)
	    {
		name = token.substring (0, i);
		value = token.substring (i+1);
	    }
	    else
	    {
		name = token;
		value = "";
	    }

	    if (count == 0)
	    {
		table.put (header, name);
	    }
	    else
	    {
		table.put (name, value);
	    }

	    count++;
	}

	return table;
    }

    public String getContentType ()
    {
	Hashtable table = headerParser ("Content-type");
	return (String) table.get ("Content-type");
    }

    public String getBoundary ()
    {
	Hashtable table = headerParser ("Content-type");
	return (String) table.get ("boundary");
    }

    public String getTransferEncoding ()
    {
	Hashtable table = headerParser ("Transfer-Encoding");
	return (String) table.get ("Transfer-Encoding");
    }

    public int getChunkSize (InputStream in) throws IOException
    {
	String line = readLine (in);
	line = line.trim (); /* apache can have trailing spaces */
	int size = -1;
	try
	{
	    size = Integer.valueOf (line, 16).intValue ();
	}
	catch (Exception e)
	{
	    System.out.println (e);
	}
	return size;
    }

    public void getChunkedFooter (InputStream in) throws IOException
    {
	for (;;)
	{
	    String line = readLine (in);
	    if (line == null)
	    {
		break;
	    }
	    int i = line.indexOf (':');
	    if (i == -1)
	    {
		break;
	    }
	}
    }

    public void setStatusLine (String line)
    {
	this.statusLine = line;
    }

    public static Reply createRedirect (String url)
    {
	Reply r = new Reply ();
	r.setStatusLine ("HTTP/1.0 302 Moved Temporarily");
	r.setHeaderField ("Location", url);
	return r;
    }
}
