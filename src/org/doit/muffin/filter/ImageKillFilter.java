/**
 * ImageKillFilter.java -- kill images that match a certain size ratio
 *
 * @author  Heinrich Opgenoorth <opgenoorth@gmd.de>
 * @version 0.1
 *
 * Last update: 98/07/23 H.O.
 */

/* 
 * Copyright (C) 1996-98 Mark Boyns <boyns@sdsu.edu>
 *
 * This file is part of Muffin.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package org.doit.muffin.filter;

import org.doit.muffin.*;
import org.doit.io.*;
import org.doit.html.*;
import java.util.Enumeration;
import java.io.IOException;

public class ImageKillFilter implements ContentFilter
{
    ImageKill factory;
    Prefs prefs;
    InputObjectStream in = null;
    OutputObjectStream out = null;
    Request request = null;

    ImageKillFilter (ImageKill factory)
    {
	this.factory = factory;
    }
    
    public void setPrefs (Prefs prefs)
    {
	this.prefs = prefs;
    }

    public boolean needsFiltration (Request request, Reply reply)
    {
	this.request = request;
	String s = reply.getContentType ();
	return s != null && s.startsWith ("text/html");
    }
    
    public void setInputObjectStream (InputObjectStream in)
    {
	this.in = in;
    }

    public void setOutputObjectStream (OutputObjectStream out)
    {
	this.out = out;
    }
    
    public void run ()
    {
	Thread.currentThread ().setName ("ImageKill");

	try
	{
	    int minheight = prefs.getInteger ("ImageKill.minheight");
	    int minwidth = prefs.getInteger ("ImageKill.minwidth");
	    int ratio = prefs.getInteger ("ImageKill.ratio");
	    boolean keepmaps = prefs.getBoolean ("ImageKill.keepmaps");

	    Tag tag;
	    Object obj;
	    while ((obj = in.read()) != null)
            {
		Token token = (Token) obj;
		if (token.getType () == Token.TT_TAG)
		{
		    tag = token.createTag ();
		    if (tag.is ("img") && 
			tag.has ("width") && 
			tag.has ("height") &&
			!(keepmaps && tag.has ("usemap")) &&
			!factory.isExcluded (tag.get ("src")))
		    {
			try
			{
			    int h = Integer.parseInt (tag.get ("height"));
			    if (h > minheight)
			    {
				int w = Integer.parseInt (tag.get ("width"));
				if  ((w > minwidth) && (w/h > ratio))
				{
				    factory.process
					("tag removed: " + tag + "\n");
				    continue;
				}
			    }
			}
			catch (NumberFormatException e) {
			    factory.process ("malformed image size: "
					     + tag + "\n");
			}
		    }
		}
		out.write (token);
	    }
	    
	    out.flush ();
	    out.close ();
	}
	catch (Exception e)
	{
	    e.printStackTrace ();
	}
    }
}

