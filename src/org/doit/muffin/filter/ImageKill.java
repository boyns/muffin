/**
 * ImageKill.java -- kill images that match a certain size ratio
 *
 * @author  Heinrich Opgenoorth <opgenoorth@gmd.de>
 * @version 0.1
 *
 * Last update: 98/07/08 H.O.
 */

/*
 * Copyright (C) 1996-99 Mark R. Boyns <boyns@doit.org>
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
import java.util.Hashtable;
import gnu.regexp.*;

public class ImageKill implements FilterFactory
{
    FilterManager manager;
    Prefs prefs;
    ImageKillFrame frame = null;
    MessageArea messages = null;
    private RE exclude = null;

    public void setManager(FilterManager manager)
    {
	this.manager = manager;
    }
    
    public void setPrefs(Prefs prefs)
    {
	this.prefs = prefs;

	boolean o = prefs.getOverride();
	prefs.setOverride(false);
	prefs.putInteger("ImageKill.minheight", 10);
	prefs.putInteger("ImageKill.minwidth", 100);
	prefs.putInteger("ImageKill.ratio", 5);
	prefs.putBoolean("ImageKill.keepmaps", true);
	prefs.putString("ImageKill.exclude", "(button|map)");
	prefs.setOverride(o);

	messages = new MessageArea();

	setExclude();
    }

    public Prefs getPrefs()
    {
	return prefs;
    }

    public void viewPrefs()
    {
	if (frame == null)
	{
	    frame = new ImageKillFrame(prefs, this);
	}
	frame.setVisible(true);
    }
    
    public Filter createFilter()
    {
	Filter f = new ImageKillFilter(this);
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
	request.addLogEntry("ImageKill", message);
	messages.append(message + "\n");
    }

    boolean isExcluded(String s)
    {
	return(exclude != null && exclude.getMatch(s) != null);
    }

    public void setExclude()
    {
        exclude = null;
        String ex = prefs.getString("ImageKill.exclude");
	if (ex != null && !ex.equals(""))
	{
            try {
	        exclude = new RE(ex);
	    }
	    catch (REException e) {
	        System.out.println("NoThanks REException: "
				    + e.getMessage());
	    }
	}
    }
}
