/* $Id */

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
package org.doit.util;

import java.util.*;

public class ThreadPool implements Cleanable
{
    private String name;
    private Vector pool = new Vector();
    
    public ThreadPool(String name)
    {
	this.name = name;
    }

    public synchronized ReusableThread get()
    {
	ReusableThread rt = null;

	if (pool.size() > 0)
	{
	    rt = (ReusableThread)pool.firstElement();
	    pool.removeElement(rt);
	}

	if (rt == null)
	{
	    rt = new ReusableThread(this);
	    rt.start();
	}

	return rt;
    }

    public synchronized void put(ReusableThread rt)
    {
	pool.addElement(rt);
    }

    public synchronized void clean()
    {
	long now = System.currentTimeMillis();
	
	for (Enumeration e = pool.elements(); e.hasMoreElements(); )
	{
	    ReusableThread rt = (ReusableThread) e.nextElement();
	    if (now - rt.getLastRunTime() >= 30000)
	    {
		rt.terminate();
		pool.removeElement(rt);
	    }
	}
    }
}
