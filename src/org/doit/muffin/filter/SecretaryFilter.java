/* $Id: SecretaryFilter.java,v 1.7 2000/01/24 04:02:21 boyns Exp $ */

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
import java.io.*;

public class SecretaryFilter implements ContentFilter
{
    Secretary factory;
    Prefs prefs;
    InputObjectStream in = null;
    OutputObjectStream out = null;
    Request request = null;

    public SecretaryFilter(Secretary factory)
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
	Thread.currentThread().setName("Secretary");
	
	try
	{
	    String s;
	    boolean inform = false;
	    String selectName = null;

	    Object obj;
            while ((obj = in.read()) != null)
            {
		Token token = (Token) obj;
		if (token.getType() == Token.TT_TAG)
		{
		    Tag tag = token.createTag();
		    if (tag.is("form"))
		    {
			inform = true;
		    }
		    else if (tag.is("/form"))
		    {
			inform = false;
		    }
		    else if (tag.is("select"))
		    {
			if (tag.has("name"))
			{
			    selectName = tag.get("name").toLowerCase();
			}
		    }
		    else if (tag.is("/select"))
		    {
			selectName = null;
		    }
		    /* <input type=(text|password) name=xxx> */
		    else if (inform && tag.is("input") && tag.has("name")
			     && (!tag.has("type") /* type can be missing */
			         || tag.get("type").equalsIgnoreCase("text")
			         || tag.get("type").equalsIgnoreCase("password")))
		    {
			String name = tag.get("name").toLowerCase();
			if (factory.containsKey(name))
			{
			    tag.put("value", (String) factory.get(name));
			}
		    }
		    /* <option value=XXX> */
		    else if (inform && tag.is("option")
			     && selectName != null
			     && factory.containsKey(selectName)
			     && tag.has("value"))
		    {
			if (tag.has("selected"))
			{
			    tag.remove("selected");
			}
			
			String value = tag.get("value");
			if (factory.get(selectName).equals(tag.get("value")))
			{
			    tag.put("selected", "");
			}
		    }

		    token.importTag(tag);
		}
		out.write(token);
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

