/* $Id: AnimationKillerFilter.java,v 1.8 2000/10/10 04:51:09 boyns Exp $ */

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
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.io.OutputStream;
import java.io.IOException;

import haui.gif.*;

public class AnimationKillerFilter implements RequestFilter, ReplyFilter, ContentFilter
{
    Prefs prefs;
    AnimationKiller factory;
    InputObjectStream in = null;
    OutputObjectStream out = null;
    Request request;

    AnimationKillerFilter(AnimationKiller factory)
    {
	this.factory = factory;
    }
    
    public void setPrefs(Prefs prefs)
    {
	this.prefs = prefs;
    }

    public void filter(Request request) throws FilterException
    {
	this.request = request;
    }

    public void filter(Reply reply) throws FilterException
    {
// 	String s = reply.getContentType();
// 	if (s != null && s.startsWith("multipart/x-mixed-replace"))
// 	{
// 	    factory.process("Found server push " + request.getURL() + "\n");
// 	    throw new FilterException("Killed server push " + request.getURL());
// 	}
    }

    public boolean needsFiltration(Request request, Reply reply)
    {
	String s = reply.getContentType();
	if (s == null)
	{
	    return false;
	}
	return s.startsWith("image/gif");
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
	Thread.currentThread().setName("AnimationKiller");

	try
	{
	  AnimationFilter filter;
	  if (prefs.getBoolean("AnimationKiller.break")) {
	    filter = new AnimationFilter(AnimationFilter.MODE_WIPE_OUT); 
	  } else {
	    switch (prefs.getInteger("AnimationKiller.maxLoops")) {
	    case 0:
	      filter = new AnimationFilter(AnimationFilter.MODE_SHOW_FIRST); 
	      break;

	    case 1:
	      filter = new AnimationFilter(AnimationFilter.MODE_SHOW_LAST);
	      break;

	    case 2:
	      filter = new AnimationFilter(AnimationFilter.MODE_INTERACTIVE);
	      break;

	    default:
	      filter = new AnimationFilter(AnimationFilter.MODE_ANIMATION);
	      break;
	    }
	  }

	  ObjectStreamToInputStream gin = new ObjectStreamToInputStream(in);
	  ObjectStreamToOutputStream gout = new ObjectStreamToOutputStream(out);

	  filter.filter(gin, gout);

	  gout.close();
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
