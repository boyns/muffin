/* $Id: Painter.java,v 1.5 2000/01/24 04:02:20 boyns Exp $ */

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
import org.doit.html.*;

public class Painter implements FilterFactory
{
    FilterManager manager;
    Prefs prefs;
    PainterFrame frame = null;

    public void setManager(FilterManager manager)
    {
	this.manager = manager;
    }
    
    public void setPrefs(Prefs prefs)
    {
	this.prefs = prefs;

	boolean o = prefs.getOverride();
	prefs.setOverride(false);
	prefs.putString("Painter.bgcolor", "");
	prefs.putString("Painter.background", "");
	prefs.putString("Painter.text", "");
	prefs.putString("Painter.alink", "");
	prefs.putString("Painter.vlink", "");
	prefs.putString("Painter.link", "");
	prefs.setOverride(o);
    }

    public Prefs getPrefs()
    {
	return prefs;
    }

    public void viewPrefs()
    {
	if (frame == null)
	{
	    frame = new PainterFrame(prefs, this);
	}
	frame.setVisible(true);
    }
    
    public Filter createFilter()
    {
	Filter f = new PainterFilter(this);
	f.setPrefs(prefs);
	return f;
    }

    public void shutdown()
    {
	if (frame != null)
	{
	    frame.dispose();
	}
    }

    void save()
    {
	manager.save(this);
    }
}

