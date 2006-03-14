/* $Id: OutputObjectStream.java,v 1.6 2006/03/14 17:00:05 flefloch Exp $ */

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
package org.doit.io;

import java.io.IOException;

/**
 * Output-side of an ObjectStream.
 *
 * @author Mark Boyns
 */
public class OutputObjectStream
{
    protected InputObjectStream in = null;
    protected boolean connected = false;

    public OutputObjectStream(InputObjectStream in) throws IOException
    {
	connect(in);
    }

    public OutputObjectStream()
    {
    }

    public void connect(InputObjectStream in) throws IOException
    {
	if (connected)
	{
	    throw new IOException("Output side already connected");
	}
	if (in.connected)
	{
	    throw new IOException("Input side already connected");
	}

	this.in = in;
	in.closed = false;
	in.objects.removeAllElements();
	connected = true;
    }

    public void write(Object obj) throws IOException
    {
	in.append(obj);
    }

    public synchronized void flush() throws IOException
    {
	if (in != null)
	{
	    synchronized(in)
	    {
		in.notify();
	    }
	}
    }

    public void close() throws IOException
    {
	if (in != null)
	{
	    in.done();
	}
    }
}
