/* $Id: AnimationKillerFilter.java,v 1.7 2000/01/24 04:02:19 boyns Exp $ */

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
	    int b, i;
	    int pattern[] = { 0x21, 0xff, 0x0b,
			       'N', 'E', 'T', 'S', 'C', 'A', 'P', 'E', '2', '.', '0',
			      0x03, 0x01 };
// 			      '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?',
// 			      '#', '#' };
	    int undo[] = new int[pattern.length];
	    
	    int index = 0;
	    boolean killed = false;
	    
	    PushbackInputStream gifInput = new PushbackInputStream(new ObjectStreamToInputStream(in));
	    ObjectStreamToOutputStream gifOutput = new ObjectStreamToOutputStream(out);

	    int count = 0;

	    /* Look for the GIF89a block extension */
	    while ((b = gifInput.read()) != -1)
	    {
		count++;
		
		if (killed)
		{
		    gifOutput.write(b);
		    continue;
		}
		
		if (b == pattern[index]
		    || (pattern[index] == '?' && b >= ' ' && b <= '~')
		    || pattern[index] == '#')
		{
		    undo[index] = b;
		    index++;
		    if (index == pattern.length)
		    {
			index = 0;
			killed = true;

			if (prefs.getBoolean("AnimationKiller.break"))
			{
			    factory.report(request, "breaking animation");
			    while ((b = gifInput.read()) != -1)
			    {
				/* ignore the rest */
			    }
			    break;
			}
			else
			{
			    String id = null;
			    
			    if (prefs.getInteger("AnimationKiller.maxLoops") == -1)
			    {
				factory.report(request, "removing animation extension");
				gifOutput.write(0x21);
				gifOutput.write(0xfe); /* comment extension */
				id = new String("XXXXXXXX1.0");
			    }
			    else
			    {
				gifOutput.write(0x21);
				gifOutput.write(0xff); /* application extension */
				id = new String("NETSCAPE2.0");
			    }
			    gifOutput.write(0x0b);
			    gifOutput.write(id.getBytes(), 0, id.length());
			    gifOutput.write(0x03);
			    gifOutput.write(0x01);
			
			    b = gifInput.read(); // high
			    b = gifInput.read(); // low
			    b = gifInput.read(); // terminator
			    
			    if (prefs.getInteger("AnimationKiller.maxLoops") == -1)
			    {
				// replaced with comment extension
			    }
			    else
			    {
				int loops = prefs.getInteger("AnimationKiller.maxLoops");
				factory.report(request, "setting maxLoops to " + loops);
				int high = loops / 256;
				int low = loops % 256;
				gifOutput.write(low);
				gifOutput.write(high);
			    }
			    gifOutput.write(0x00); // terminator
			}
		    }
		}
		else if (index > 0)
		{
		    for (i = 0; i < index; i++)
		    {
			gifOutput.write(undo[i]);
		    }
		    gifInput.unread(b);
		    count--;
		    index = 0;
		}
		else
		{
		    gifOutput.write(b);
		}
	    }

	    gifOutput.flush();
	    gifOutput.close();
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
