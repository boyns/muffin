/* $Id: DocumentInfo.java,v 1.7 2000/01/24 04:02:19 boyns Exp $ */

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
import org.doit.html.*;
import java.util.Date;
import java.util.StringTokenizer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParsePosition;
import java.io.*;

public class DocumentInfo implements FilterFactory, ContentFilter
{
    FilterManager manager;
    Prefs prefs;
    InputObjectStream in = null;
    OutputObjectStream out = null;
    Reply reply;
    Request request;
    DocumentInfoFrame frame = null;

    public void setManager(FilterManager manager)
    {
	this.manager = manager;
    }
    
    public void setPrefs(Prefs prefs)
    {
	this.prefs = prefs;
	
	boolean o = prefs.getOverride();
	prefs.setOverride(false);
	prefs.putString("DocumentInfo.location", "bottom");
	prefs.putString("DocumentInfo.align", "right");
	prefs.putString("DocumentInfo.info", "URL,Server,LastModified");
	prefs.putString("DocumentInfo.htmlBefore", "<small>");
	prefs.putString("DocumentInfo.htmlAfter", "</small>");
	prefs.setOverride(o);
    }

    public Prefs getPrefs()
    {
	return prefs;
    }

    public void viewPrefs()
    {
	if (frame == null)
	{
	    frame = new DocumentInfoFrame(prefs, this);
	}
	frame.setVisible(true);
    }
    
    public Filter createFilter()
    {
	Filter f = new DocumentInfo();
	f.setPrefs(prefs);
	return f;
    }

    public void shutdown()
    {
	if (frame != null)
	{
	    frame.dispose();
	}
    }

    void save()
    {
	manager.save(this);
    }

    public boolean needsFiltration(Request request, Reply reply)
    {
	this.request = request;
	this.reply = reply;
	
	String s = reply.getContentType();
	return s != null && s.startsWith("text/html");
    }
    
    public void setInputObjectStream(InputObjectStream in)
    {
	this.in = in;
    }

    public void setOutputObjectStream(OutputObjectStream out)
    {
	this.out = out;
    }

    void addURL(StringBuffer buf)
    {
	buf.append("URL: ");
	buf.append(request.getURL());
	buf.append("<br>\n");
    }

    void addLastModified(StringBuffer buf)
    {
	if (! reply.containsHeaderField("Last-Modified"))
	{
	    return;
	}
	
	buf.append("Last-Modified: ");
	String str = reply.getHeaderField("Last-Modified");
	/* Try to put date in localtime */
	try
	{
	    /*
	     * HTTP date formats:
	     * Sun, 06 Nov 1994 08:49:37 GMT  ; RFC 822, updated by RFC 1123
	     * Sunday, 06-Nov-94 08:49:37 GMT ; RFC 850, obsoleted by RFC 1036
	     * Sun Nov  6 08:49:37 1994       ; ANSI C's asctime() format
	     */
	    SimpleDateFormat format = (SimpleDateFormat) DateFormat.getDateInstance();
	    switch (str.charAt(3)) // Try to guess the format
	    {
	    case ',':
		format.applyPattern("EEE, dd MMM yyyy HH:mm:ss z");
		break;

	    case ' ':
		format.applyPattern("EEE MMM dd HH:mm:ss yyyy");
		break;

	    default:
		format.applyPattern("EEEE, dd-MMM-yyyy HH:mm:ss z");
		break;
	    }
	    ParsePosition pos = new ParsePosition(0);
	    Date date = format.parse(reply.getHeaderField("Last-Modified"), pos);
	    buf.append(format.format(date));
	}
	catch (Exception e)
	{
	    buf.append(str);
	}
	buf.append("<br>\n");
    }

    void addClient(StringBuffer buf)
    {
	buf.append("Client: ");
	buf.append(request.getClient().getInetAddress().getHostAddress());
	buf.append("<br>\n");
    }
    
    void addDate(StringBuffer buf)
    {
	SimpleDateFormat fmt = (SimpleDateFormat) DateFormat.getDateInstance();
	fmt.applyPattern("EEE, dd MMM yyyy HH:mm:ss z");
	buf.append("Date: ");
	buf.append(fmt.format(new Date()));
	buf.append("<br>\n");
    }

    void addHeader(StringBuffer buf, String header)
    {
	Message msg = null;
	
	if (request.containsHeaderField(header))
	{
	    msg = request;
	}
	else if (reply.containsHeaderField(header))
	{
	    msg = reply;
	}

	if (msg != null)
	{
	    for (int i = msg.getHeaderValueCount(header)-1; i >= 0; i--) 
	    {
		buf.append(header);
		buf.append(": ");
		buf.append(msg.getHeaderField(header, i));
		buf.append("<br>\n");
	    }
	}
    }
    
    String generateInfo()
    {
	StringBuffer buf = new StringBuffer();
	StringTokenizer st = new StringTokenizer(prefs.getString("DocumentInfo.info"), ",");
	while (st.hasMoreTokens())
	{
	    String token = st.nextToken();
	    token = token.trim();
	    if (token.equalsIgnoreCase("URL"))
	    {
		addURL(buf);
	    }
	    else if (token.equalsIgnoreCase("Client"))
	    {
		addClient(buf);
	    }
	    else if (token.equalsIgnoreCase("LastModified"))
	    {
		addLastModified(buf);
	    }
	    else if (token.equalsIgnoreCase("Date"))
	    {
		addDate(buf);
	    }
	    else /* Add any http header if it exists */
	    {
		addHeader(buf, token);
	    }
	}

	return buf.toString();
    }

    Token generateTop()
    {
	Token info = new Token();
	info.append(prefs.getString("DocumentInfo.htmlBefore"));
	info.append("<p align=");
	info.append(prefs.getString("DocumentInfo.align"));
	info.append(">");
	info.append(generateInfo());
	info.append("</p>\n");
	info.append(prefs.getString("DocumentInfo.htmlAfter"));
	return info;
    }

    Token generateBottom()
    {
	Token info = new Token();
	info.append(prefs.getString("DocumentInfo.htmlBefore"));
	info.append("<p align=");
	info.append(prefs.getString("DocumentInfo.align"));
	info.append(">");
	info.append(generateInfo());
	info.append("</p>\n");
	info.append(prefs.getString("DocumentInfo.htmlAfter"));
	return info;
    }
    
    public void run()
    {
	Thread.currentThread().setName("DocumentInfo");

	try
	{
	    boolean found = false;
	    Object obj;
            while ((obj = in.read()) != null)
            {
		Token token = (Token) obj;
		if (token.getType() == Token.TT_TAG)
		{
		    Tag tag = token.createTag();
		    if (tag.is("body")
			&& prefs.getString("DocumentInfo.location").equals("top"))
		    {
			out.write(obj);
			out.write(generateTop());
			found = true;
		    }
		    else if (tag.is("/body")
			     && prefs.getString("DocumentInfo.location").equals("bottom"))
		    {
			out.write(generateBottom());
			out.write(obj);
			found = true;
		    }
		    else
		    {
			out.write(obj);
		    }
		}
		else
		{
		    out.write(obj);
		}
	    }
	    if (!found) /* no body or /body found */
	    {
		out.write(generateBottom());
	    }
	}
	catch (IOException ioe)
	{
	    ioe.printStackTrace();
	}
	finally
	{
	    try
	    {
		out.flush();
		out.close();
	    }
	    catch (IOException ioe)
	    {
	    }
	}
    }
}
