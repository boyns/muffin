/* $Id: Janitor.java,v 1.6 2000/01/24 04:02:14 boyns Exp $ */

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
package org.doit.muffin;

import java.util.Enumeration;
import java.util.Vector;
import org.doit.util.*;

class Janitor implements Runnable
{
    private Vector cleanable = new Vector();
    
    public void add(Cleanable c)
    {
	cleanable.addElement(c);
    }
    
    public void run()
    {
	Thread.currentThread().setName("Janitor");
	
	for (;;)
	{
	    try
	    {
		Thread.sleep(30 * 1000); /* 30 seconds */
	    }
	    catch (Exception e)
	    {
		e.printStackTrace();
	    }

	    for (Enumeration e = cleanable.elements();
		 e.hasMoreElements(); )
	    {
		((Cleanable)e.nextElement()).clean();
	    }

	    Http.clean();

	    /* AutoSave any AutoSaveable filters */
	    FilterManager manager = Main.getFilterManager();
	    Enumeration configs = manager.configs.keys();
	    while (configs.hasMoreElements())
	    {
		String config = (String) configs.nextElement();
		Vector enabled = manager.getEnabledFilters(config);
		for (int i = 0; i < enabled.size(); i++)
		{
		    FilterFactory ff = (FilterFactory) enabled.elementAt(i);
		    if (ff instanceof AutoSaveable)
		    {
			((AutoSaveable) ff).autoSave();
		    }
		}
	    }
	}
    }
}
