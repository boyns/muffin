/* $Id: AnimationKiller.java,v 1.3 1998/12/19 21:24:17 boyns Exp $ */

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
package org.doit.muffin.filter;

import org.doit.muffin.*;

public class AnimationKiller implements FilterFactory
{
    FilterManager manager;
    Prefs prefs;
    AnimationKillerFrame frame = null;
    MessageArea messages = null;

    public void setManager(FilterManager manager)
    {
	this.manager = manager;
    }
    
    public void setPrefs(Prefs prefs)
    {
	this.prefs = prefs;

	boolean o = prefs.getOverride();
	prefs.setOverride(false);
	prefs.putInteger("AnimationKiller.maxLoops", 1);
	prefs.putBoolean("AnimationKiller.break", false);
	prefs.putInteger("AnimationKiller.historySize", 500);
	prefs.putString("AnimationKiller.logfile", Main.getOptions().getString("muffin.logfile"));
	prefs.setOverride(o);

	messages = new MessageArea(prefs.getUserFile(prefs.getString("AnimationKiller.logfile")),
				   "AnimationKiller",
				   prefs.getInteger("AnimationKiller.historySize"));
    }

    public Prefs getPrefs()
    {
	return prefs;
    }

    public void viewPrefs()
    {
	if (frame == null)
	{
	    frame = new AnimationKillerFrame(prefs, this);
	}
	frame.setVisible(true);
    }
    
    public Filter createFilter()
    {
	Filter f = new AnimationKillerFilter(this);
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

    void report(Request request, String message)
    {
	report("[" + request.getRequest() + "] " + message);
    }

    void report(String message)
    {
	messages.append(message + "\n");
    }
}
