/* $Id: ReusableThread.java,v 1.2 2000/01/24 04:02:26 boyns Exp $ */

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

public class ReusableThread extends Thread
{
    private ThreadPool pool = null;
    private Runnable runnable = null;
    private boolean alive = true;
    private long lastrun = 0;
    private int used = 0;

    public ReusableThread(ThreadPool pool)
    {
	this.pool = pool;
    }

    public synchronized void setRunnable(Runnable runnable)
    {
	this.runnable = runnable;
	notify();
    }

    public synchronized void terminate()
    {
	alive = false;
	notify();
    }

    public long getLastRunTime()
    {
	return lastrun;
    }

    public int useCount()
    {
	return used;
    }
    
    public void run()
    {
	while (alive)
	{
	    setName("ReusableThread: idle");
	    
	    while (runnable == null && alive)
	    {
		synchronized (this)
		{
		    try
		    {
			wait();
		    }
		    catch (InterruptedException ie)
		    {
		    }
		}
	    }

	    if (alive)
	    {
		setName("ReusableThread: busy");
		setPriority(Thread.NORM_PRIORITY);
		lastrun = System.currentTimeMillis();
		used++;
		runnable.run();
		runnable = null;
		pool.put(this);
	    }
	}
    }
}
