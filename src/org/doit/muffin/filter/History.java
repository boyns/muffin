/* History.java */

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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import org.doit.muffin.*;
import sdsu.util.SortedList;

public class History implements FilterFactory, AutoSaveable
{
    private Hashtable data;

    FilterManager manager;
    Prefs prefs;

    HistoryData get (String url)
    {
	HistoryData b = (HistoryData) data.get (url);
	if (b == null)
	{
	    b = new HistoryData ();
	    b.url = url;
	    b.time = 0;
	    b.count = 0;
	    put (url, b);
	}
	return b;
    }

    void put (String url, HistoryData b)
    {
	data.put (url, b);
    }

    Enumeration keys ()
    {
	return data.keys ();
    }

    Enumeration sortByCount ()
    {
	SortedList sorter = new SortedList (HistoryDataCountComparer.getInstance ());
	Enumeration e = keys ();
	while (e.hasMoreElements ())
	{
	    sorter.addElement (get ((String) e.nextElement ()));
	}
	return sorter.elements ();
    }

    Enumeration sortByTime ()
    {
	SortedList sorter = new SortedList (HistoryDataTimeComparer.getInstance ());
	Enumeration e = keys ();
	while (e.hasMoreElements ())
	{
	    sorter.addElement (get ((String) e.nextElement ()));
	}
	return sorter.elements ();
    }

    public void setManager (FilterManager manager)
    {
	this.manager = manager;
    }
    
    public void setPrefs (Prefs prefs)
    {
	this.prefs = prefs;

	boolean o = prefs.getOverride ();
	prefs.setOverride (false);
	prefs.putString ("History.histfile", "history");
	prefs.setOverride (o);

	loadData ();
    }

    public Prefs getPrefs ()
    {
	return prefs;
    }

    public void viewPrefs ()
    {
    }
    
    public Filter createFilter ()
    {
	Filter f = new HistoryFilter (this);
	f.setPrefs (prefs);
	return f;
    }

    public void shutdown ()
    {
	saveData ();
    }

    public void autoSave ()
    {
	synchronized (this)
	{
	    saveData ();
	}
    }

    void save ()
    {
	manager.save (this);
    }

    void loadData ()
    {
	try
	{
	    FileInputStream in = new FileInputStream (prefs.getUserFile (prefs.getString ("History.histfile")));
	    ObjectInputStream obj = new ObjectInputStream (in);
	    data = (Hashtable) obj.readObject ();
	    in.close ();
	}
	catch (Exception e)
	{
	    System.out.println (e);
	    data = new Hashtable ();
	}
    }

    void saveData ()
    {
	try
	{
	    FileOutputStream out = new FileOutputStream (prefs.getUserFile (prefs.getString ("History.histfile")));
	    ObjectOutputStream obj = new ObjectOutputStream (out);
	    obj.writeObject (data);
	    obj.flush ();
	    out.close ();
	}
	catch (Exception e)
	{
	    System.out.println (e);
	}
    }
}

