/* Janitor.java */

/*
 * Copyright (C) 1996-98 Mark R. Boyns <boyns@doit.org>
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
package org.doit.muffin;

import java.util.Enumeration;
import java.util.Vector;

class Janitor implements Runnable
{
    public void run ()
    {
	Thread.currentThread ().setName ("Janitor");
	
	for (;;)
	{
	    try
	    {
		Thread.sleep (30 * 1000); /* 30 seconds */
	    }
	    catch (Exception e)
	    {
		e.printStackTrace ();
	    }

	    /* Clean HTTP persistent server connections. */
	    Http.clean ();

	    /* Clean keep-alive client connections. */
	    Server.clean ();

	    /* AutoSave any AutoSaveable filters */
	    FilterManager manager = Main.getFilterManager ();
	    Enumeration configs = manager.configs.keys ();
	    while (configs.hasMoreElements ())
	    {
		String config = (String) configs.nextElement ();
		Vector enabled = manager.getEnabledFilters (config);
		for (int i = 0; i < enabled.size (); i++)
		{
		    FilterFactory ff = (FilterFactory) enabled.elementAt (i);
		    if (ff instanceof AutoSaveable)
		    {
			((AutoSaveable) ff).autoSave ();
		    }
		}
	    }
	}
    }
}
