/* $Id: PreviewFilter.java,v 1.7 2000/01/24 04:02:20 boyns Exp $ */

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
import org.doit.util.*;
import org.doit.html.Token;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Vector;
import java.util.Enumeration;


public class PreviewFilter implements ContentFilter
{
    Preview factory;
    Prefs prefs;
    InputObjectStream in = null;
    OutputObjectStream out = null;
    Reply reply;
    Request request;

    PreviewFilter(Preview factory)
    {
	this.factory = factory;
    }

    public void setPrefs(Prefs prefs)
    {
	this.prefs = prefs;
    }

    public boolean needsFiltration(Request request, Reply reply)
    {
	this.request = request;
	this.reply = reply;

	String type = reply.getContentType();
	if (type == null)
	{
	    return false;
	}

	if (prefs.getString("Preview.contentTypes").equals("ALL"))
	{
	    return true;
	}
	
	String previewTypes[] = prefs.getStringList("Preview.contentTypes");
	for (int i = 0; i < previewTypes.length; i++)
	{
	    if (type.startsWith(previewTypes[i]))
	    {
		return true;
	    }
	}
	
	return false;
    }
    
    public void setInputObjectStream(InputObjectStream in)
    {
	this.in = in;
    }

    public void setOutputObjectStream(OutputObjectStream out)
    {
	this.out = out;
    }

    public void run()
    {
	Thread.currentThread().setName("Preview");

	try
	{
	    ByteArray buffer = new ByteArray(8192);
	    boolean accepted = false;
	    byte content[] = null;
	    Object obj;

	    while ((obj = in.read()) != null)
	    {
		ByteArray b = (ByteArray) obj;
		buffer.append(b);
	    }
	    
	    synchronized(factory.previewFrame)
	    {
		PreviewDialog dialog = new PreviewDialog(factory.previewFrame,
							  request,
							  reply,
							  buffer.getBytes());
		dialog.show();
		if (dialog.accept())
		{
		    content = dialog.getContent();
		    accepted = true;
		}
		dialog.dispose();
	    }

	    if (accepted)
	    {
		InputObjectStream in = new InputObjectStream();
		SourceObjectStream src;

		if (reply.containsHeaderField("Content-type") &&
		    reply.getContentType().equals("text/html"))
		{
		    src = new HtmlObjectStream(in);
		}
		else
		{
		    src = new SourceObjectStream(in);
		}
		src.setSourceInputStream(new ByteArrayInputStream(content));

		ReusableThread thread = Main.getThread();
		thread.setName("Preview ObjectStream Source");
		thread.setRunnable(src);

		while ((obj = in.read()) != null)
		{
		    out.write(obj);
		}
	    }
	    else
	    {
		if (reply.containsHeaderField("Content-type") &&
		    reply.getContentType().startsWith("text"))
		{
		    Token token = new Token(Token.TT_TEXT);
		    token.append("Rejected by Preview filter");
		    out.write(token);
		}
		else
		{
		    ByteArray b = new ByteArray();
		    b.append((byte)0x0);
		    out.write(b);
		}
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
