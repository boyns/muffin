/* $Id: InputObjectStream.java,v 1.5 2000/01/24 04:02:09 boyns Exp $ */

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
import java.io.InterruptedIOException;
import java.util.Vector;

/**
 * Input-side of an ObjectStream.
 *
 * @author Mark Boyns
 */
public class InputObjectStream
{
    protected final int maxObjects = 64;
    protected Vector objects = new Vector();

    protected Thread inputThread = null;
    protected Thread outputThread = null;

    protected boolean closed = true;
    protected boolean connected = false;

    public InputObjectStream(OutputObjectStream out) throws IOException
    {
	connect(out);
    }

    public InputObjectStream()
    {
    }

    public void connect(OutputObjectStream out) throws IOException
    {
	if (connected)
	{
	    throw new IOException("Input side already connected");
	}
	out.connect(this);
	connected = true;
    }
    
    public synchronized Object read() throws IOException
    {
	inputThread = Thread.currentThread();

	while (objects.isEmpty())
	{
	    if (closed)
	    {
		return null; /* EOF */
	    }
	    if (outputThread != null && !outputThread.isAlive())
	    {
		throw new IOException("Output side not connected");
	    }
	    notify();
	    try
	    {
		wait();
	    }
	    catch (InterruptedException e)
	    {
		throw new InterruptedIOException();
	    }
	}

	Object o = objects.firstElement();
	try
	{
	    objects.removeElementAt(0);
	}
	catch (Exception e)
	{
	}
	return o;
    }

    public synchronized void unread(Object obj) throws IOException
    {
	objects.insertElementAt(obj, 0);
    }
    
    public void close() throws IOException
    {
	objects.removeAllElements();
    }

    protected synchronized void append(Object newObject) throws IOException
    {
	outputThread = Thread.currentThread();

	while (objects.size() == maxObjects)
	{
	    if (inputThread != null && !inputThread.isAlive())
	    {
		throw new IOException("Input side not connected");
	    }
	    notify();
	    try
	    {
		wait();
	    }
	    catch (InterruptedException e)
	    {
		throw new InterruptedIOException();
	    }
	}

	objects.addElement(newObject);
    }

    protected synchronized void done()
    {
	closed = true;
	notify();
    }
}
