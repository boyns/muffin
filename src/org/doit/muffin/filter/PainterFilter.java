/* $Id: PainterFilter.java,v 1.6 2000/01/24 04:02:20 boyns Exp $ */

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
import gnu.regexp.*;
import java.io.*;

public class PainterFilter implements ContentFilter
{
    private static RE pattern1;
    private static RE pattern2;

    static
    {
	try
	{
	    pattern1 = new RE("^(body|td|table)$");
	    pattern2 = new RE("^(tr|th)$");
	}
	catch (REException ree)
	{
	    ree.printStackTrace();
	}
    }
    
    Painter factory;
    Prefs prefs;
    InputObjectStream in = null;
    OutputObjectStream out = null;
    Request request = null;

    PainterFilter(Painter factory)
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
	Thread.currentThread().setName("Painter");
	
	try
	{
	    Tag tag;
	    Object obj;

            while ((obj = in.read()) != null)
            {
		Token token = (Token) obj;
		if (token.getType() == Token.TT_TAG)
		{
		    tag = token.createTag();

		    if (tag.matches(pattern1))
		    {
			String value;

			value = prefs.getString("Painter.bgcolor");
			if (value.length() > 0)
			{
			    if (value.equalsIgnoreCase("None"))
			    {
				tag.remove("bgcolor");
				tag.remove("background");
			    }
			    else
			    {
				tag.put("bgcolor", value);
				tag.remove("background");
			    }
			}
			else
			{
			    value = prefs.getString("Painter.background");
			    if (value.length() > 0)
			    {
				if (value.equalsIgnoreCase("None"))
				{
				    tag.remove("background");
				    tag.remove("bgcolor");
				}
				else
				{
				    tag.put("background", value);
				    tag.remove("bgcolor");
				}
			    }
			}
			
			value = prefs.getString("Painter.text");
			if (value.length() > 0)
			{
			    if (value.equalsIgnoreCase("None"))
			    {
				tag.remove("text");
			    }
			    else
			    {
				tag.put("text", value);
			    }
			}

			value = prefs.getString("Painter.link");
			if (value.length() > 0)
			{
			    if (value.equalsIgnoreCase("None"))
			    {
				tag.remove("link");
			    }
			    else
			    {
				tag.put("link", value);
			    }
			}

			value = prefs.getString("Painter.alink");
			if (value.length() > 0)
			{
			    if (value.equalsIgnoreCase("None"))
			    {
				tag.remove("alink");
			    }
			    else
			    {
				tag.put("alink", value);
			    }
			}

			value = prefs.getString("Painter.vlink");
			if (value.length() > 0)
			{
			    if (value.equalsIgnoreCase("None"))
			    {
				tag.remove("vlink");
			    }
			    else
			    {
				tag.put("vlink", value);
			    }
			}
		    }
		    else if (tag.matches(pattern2) && tag.has("bgcolor"))
		    {
			String value = prefs.getString("Painter.bgcolor");
			if (value.length() > 0)
			{
			    if (value.equalsIgnoreCase("None"))
			    {
				tag.remove("bgcolor");
			    }
			    else
			    {
				tag.put("bgcolor", value);
			    }
			}
		    }
		    /* <font> */
		    else if (tag.is("font") && tag.has("color"))
		    {
			String value = prefs.getString("Painter.text");
			if (value.length() > 0)
			{
			    if (value.equalsIgnoreCase("None"))
			    {
				tag.remove("color");
			    }
			    else
			    {
				tag.put("color", value);
			    }
			}
		    }

		    token.importTag(tag);
		    out.write(token);
		}
		else
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

