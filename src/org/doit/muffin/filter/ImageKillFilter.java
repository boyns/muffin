/**
 * ImageKillFilter.java -- kill images that match a certain size ratio
 *
 * @author  Heinrich Opgenoorth <opgenoorth@gmd.de>
 * @version 0.2
 *
 * Last update: 98/11/30 H.O.
 */

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
	    boolean replace = prefs.getBoolean ("ImageKill.replace");
	    String replaceURL = prefs.getString ("ImageKill.replaceURL");

	    int w = 0;
	    int h = 0;

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
			    w = Integer.parseInt (tag.get ("width"));
			    h = Integer.parseInt (tag.get ("height"));
			}
			catch (NumberFormatException e) {
			    factory.report(request,
					   "malformed image size: "
					   + tag);
			    out.write (token);
			    continue;
			}
			if ( factory.inRemoveSizes (w, h) ||
			     ((h>minheight) && (w>minwidth) && (w/h>ratio)))
			{
			    if (replace)
			    {
				factory.report(request,
				    "tag replaced: " + tag);
				tag.put ("src", replaceURL);
				token.importTag (tag);
			    }
			    else
			    {
				factory.report(request,
				    "tag removed: " + tag);
				continue;
			    }
			}
		    }
		}
		out.write (token);
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
	        ioe.printStackTrace();
	    }
	}
    }
}

