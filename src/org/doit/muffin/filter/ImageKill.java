/**
 * ImageKill.java -- kill images that match a certain size ratio
 *
 * @author  Heinrich Opgenoorth <opgenoorth@gmd.de>
 * @version 0.2
 *
 * Last update: 98/11/30 H.O.
 */

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
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import java.lang.Integer;
import java.awt.Dimension;
import gnu.regexp.*;

public class ImageKill implements FilterFactory
{
    FilterManager manager;
    Prefs prefs;
    ImageKillFrame frame = null;
    MessageArea messages = null;
    private RE exclude = null;
    private Hashtable removeSizes = null;

    public void setManager (FilterManager manager)
    {
	this.manager = manager;
    }
    
    public void setPrefs (Prefs prefs)
    {
	this.prefs = prefs;

	boolean o = prefs.getOverride ();
	prefs.setOverride (false);
	prefs.putInteger ("ImageKill.minheight", 49);
	prefs.putInteger ("ImageKill.minwidth", 300);
	prefs.putInteger ("ImageKill.ratio", 6);
	prefs.putBoolean ("ImageKill.keepmaps", true);
	prefs.putString ("ImageKill.exclude", "(button|map)");
	prefs.putString ("ImageKill.rmSizes", "468x60,450x40");
	prefs.putBoolean ("ImageKill.replace", false);
	prefs.putString ("ImageKill.replaceURL",
			 "file:/usr/local/images/empty.gif");
	prefs.setOverride (o);
	messages = new MessageArea();
	setExclude ();
	setRemoveSizes ();
    }

    public Prefs getPrefs ()
    {
	return prefs;
    }

    public void viewPrefs ()
    {
	if (frame == null)
	{
	    frame = new ImageKillFrame (prefs, this);
	}
	frame.setVisible (true);
    }
    
    public Filter createFilter ()
    {
	Filter f = new ImageKillFilter (this);
	f.setPrefs (prefs);
	return f;
    }

    public void shutdown ()
    {
	if (frame != null)
	{
	    frame.dispose ();
	}
    }

    void save ()
    {
	manager.save (this);
    }

    void report(Request request, String message)
    {
	request.addLogEntry("ImageKill", message);
	messages.append(message + "\n");
    }

    /********************************************************************
     * Creates (from the preferences) a regexp to match images that
     * we should <b>not</b> try to remove.
     */

    public void setExclude ()
    {
        exclude = null;
        String ex = prefs.getString ("ImageKill.exclude");
	if (ex != null && !ex.equals (""))
	{
            try {
	        exclude = new RE (ex);
	    }
	    catch (REException e) {
	        System.out.println ("ImageKill REException: "
				    + e.getMessage ());
	    }
	}
    }


    /********************************************************************
     * Checks if a given string (image source attribute) matches the
     * regexp for exclusion from removal.
     *
     * @return   True if the string matches the exclusion regexp.
     */

    boolean isExcluded (String s)
    {
	return (exclude != null && exclude.getMatch (s) != null);
    }


    /********************************************************************
     * Fetches (from the preferences) the list of image geometries
     * to be removed, and converts it from string format to a
     * hashtable (for easy matching later). The width is used as
     * a key, with all heights for that key stored in a vector.
     */

    public void setRemoveSizes ()
    {
	String size;
	Integer w, h;
	Vector hlist;

	try {
	    removeSizes = new Hashtable ();
	    String rm = prefs.getString ("ImageKill.rmSizes");
	    if (rm == null || rm.equals (""))
	    {
		return;
	    }
	    StringTokenizer st = new StringTokenizer (rm, ",");
	    while (st.hasMoreTokens ())
	    {
		size = st.nextToken ();
		StringTokenizer st2 = new StringTokenizer (size, "x");
		w = new Integer (Integer.parseInt (st2.nextToken ()));
		h = new Integer (Integer.parseInt (st2.nextToken ()));
		/* If this is the first entry for that width, we have
		 * to create the vector to store the height. If not,
		 * we just need to add a new element to that vector. */
		hlist = (Vector)removeSizes.get (w);
		if (hlist == null)
		{
		    hlist = new Vector ();
		    removeSizes.put (w, hlist);
		}
		hlist.addElement (h);
	    }
	}
	catch (Exception e) {
	    e.printStackTrace ();
	}
    }


    /********************************************************************
     * Checks if a given image geometry is contained in the list of
     * geometries that should be removed/replaced.
     *
     * @return  True if the given geometry is found in the list of
     *          geometries to remove, false otherwise.
     */

    boolean inRemoveSizes (int width, int height)
    {
	Vector hlist;
	Integer w;

	if (removeSizes != null)
	{
	    try {
		w = new Integer (width);
		hlist = (Vector)removeSizes.get (w);
		if ( hlist != null && 
		     hlist.contains (new Integer (height)))
		{
		    return true;
		}
	    }
	    catch (Exception e) {
		e.printStackTrace ();
	    }
	}
	return false;
    }
}
