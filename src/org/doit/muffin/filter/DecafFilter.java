/* $Id: DecafFilter.java,v 1.7 2000/01/26 03:53:34 boyns Exp $ */

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
import java.util.Enumeration;
import java.io.IOException;

public class DecafFilter implements ContentFilter, ReplyFilter
{
    Decaf factory;
    Prefs prefs;
    InputObjectStream in = null;
    OutputObjectStream out = null;
    Request request = null;

    DecafFilter(Decaf factory)
    {
	this.factory = factory;
    }
    
    public void setPrefs(Prefs prefs)
    {
	this.prefs = prefs;
    }

    public void filter(Reply reply) throws FilterException
    {
	if (prefs.getBoolean("Decaf.noJavaScript"))
	{
	    String content = reply.getContentType();
	    if (content != null && content.equalsIgnoreCase("application/x-javascript"))
	    {
		factory.report(request, "rejecting " + content);
		throw new FilterException("Decaf " + content + " rejected");
	    }
	}
    }

    public boolean needsFiltration(Request request, Reply reply)
    {
	this.request = request;
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
    
    public void run()
    {
	Thread.currentThread().setName("Decaf");

	try
	{
	    boolean eatingJavaScript = false;
	    boolean eatingJava = false;
	    boolean noJavaScript = prefs.getBoolean("Decaf.noJavaScript");
	    boolean noJava = prefs.getBoolean("Decaf.noJava");

	    Tag tag;
	    Object obj;
	    while ((obj = in.read()) != null)
            {
		Token token = (Token) obj;
		if (token.getType() == Token.TT_TAG)
		{
		    tag = token.createTag();

		    if (eatingJavaScript && tag.is("/script"))
		    {
			eatingJavaScript = false;
			continue;
		    }
		    if (eatingJava && tag.is("/applet"))
		    {
			eatingJava = false;
			continue;
		    }
		    
		    if (noJavaScript)
		    {
			if (tag.is("script"))
			{
			    eatingJavaScript = true;
			    factory.report(request, "removed <script>");
			}
			else if (factory.isJavaScriptTag(tag.name()) && tag.attributeCount() > 0)
			{
			    StringBuffer str = new StringBuffer();
			    String value;

			    Enumeration e = tag.enumerate();
			    while (e.hasMoreElements())
			    {
				String attr = (String) e.nextElement();
				if (factory.isJavaScriptAttr(attr))
				{
				    value = tag.remove(attr);
				    if (value != null)
				    {
					str.append("<");
					str.append(tag.name());
					str.append("> ");
					str.append(attr);
					str.append("=\"");
					str.append(value);
					str.append("\" ");
				    }
				}
			    }

			    if (tag.has("href")
				&& ((value = tag.get("href")) != null)
				&& "javascript:".startsWith(value))
			    {
				value = tag.remove("href");
				str.append("<");
				str.append(tag.name());
				str.append("> ");
				str.append("href=\"");
				str.append(value);
				str.append("\" ");
			    }

			    if (tag.has("language")
				&& tag.get("language").equalsIgnoreCase("javascript"))
			    {
				value = tag.remove("language");
				str.append("<");
				str.append(tag.name());
				str.append("> ");
				str.append("language=\"");
				str.append(value);
				str.append("\" ");
			    }

			    if (str.length() > 0)
			    {
				factory.report(request, "removed " + str.toString());
			    }
			}
		    }
		    if (noJava)
		    {
			if (tag.is("applet"))
			{
			    eatingJava = true;
			    factory.report(request, "removed <applet>");
			}
		    }
		    if (!eatingJavaScript && !eatingJava)
		    {
			token.importTag(tag);
			out.write(token);
		    }
		}
		else if (!eatingJavaScript && !eatingJava)
		{
		    out.write(token);
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

