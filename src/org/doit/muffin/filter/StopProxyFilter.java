/* $Id: StopProxyFilter.java,v 1.6 2000/01/24 04:02:21 boyns Exp $ */

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

import java.util.Vector;
import java.util.Enumeration;
import java.io.IOException;


public class StopProxyFilter implements ContentFilter
{
    // HTTP header if rejecting page
    private static final String noPage = "HTTP/1.0 204 No Response";
    
    StopProxy factory;
    Prefs prefs;
    InputObjectStream in = null;
    OutputObjectStream out = null;
    // Request request = null;
    Reply reply = null;

    StopProxyFilter(StopProxy factory)
    {
	this.factory = factory;
    }
    
    public void setPrefs(Prefs prefs)
    {
	this.prefs = prefs;
    }

    public boolean needsFiltration(Request request, Reply reply)
    {
	// this.request = request;
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
    
    public void run()
    {
	Thread.currentThread().setName("StopProxy");

	try
	{
	    Tag tag;
	    int inTitle = 0;          // Are we in the title here?
	    boolean output = true;    // Innocent until proved guilty!
	    String pageTitle = prefs.getString("StopProxy.PageTitle");
	    Vector saveTokens = new Vector();

	    Object obj;
            while ((obj = in.read()) != null)
            {
		Token token = (Token) obj;
		if ((inTitle >= 0) && (token.getType() == Token.TT_TAG))
		{
		    tag = token.createTag();

		    /* <title> */
		    if (tag.is("title"))
		    {
			inTitle = 1; // In the title now
		    }
		    /* </title> - in case the title is empty! */
		    /* </body>  - in case no title at all!    */
		    else if (tag.is("/title") || tag.is("/head"))
		    {
			dumpTokens(saveTokens);
			saveTokens = null;
			inTitle = -1; // Don't bother checking now
		    }
		    if (saveTokens != null)
		    {
			saveTokens.addElement(token);
		    } else if (output) {
			out.write(token);
		    }
		}
		else if ((inTitle > 0) && (token.getType() == Token.TT_TEXT))
		{
		    String title = token.toString();
		    // Should check against value in preferences when I get that far
		    if (title.startsWith(pageTitle))
		    {
			reply.setStatusLine(noPage);
			reply.removeHeaderField("Content-length");
			factory.process("Page rejected - title: \"" + title + "\"\n");
			output = false; // No(body) output
			/* break;    // No need to read any more */
		    } else {
			dumpTokens(saveTokens);
		    }
		    out.write(token);
		    saveTokens = null;  // We do not need this any more
		    inTitle = -1;   // Don't bother checking now
		}
		else
		{
		    if (saveTokens != null)
		    {
			saveTokens.addElement(token);
		    } else if (output) {
			out.write(token);
		    }
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

    private void dumpTokens(Vector saveTokens) throws IOException
    {
	Enumeration e = saveTokens.elements();
	while (e.hasMoreElements())
	{
	    Token token = (Token) e.nextElement();
	    out.write(token);
	}
    }
}

