/* $Id: Stats.java,v 1.5 2000/01/24 04:02:21 boyns Exp $ */

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

import java.util.Hashtable;
import org.doit.muffin.*;

public class Stats implements FilterFactory
{
    FilterManager manager;
    Prefs prefs;
    StatsFrame frame = null;

    int requests;
    int replies;
    Hashtable hosts;
    Hashtable servers;
    Hashtable contentLengths;
    Hashtable contentTypes;

    public Stats()
    {
	reset();
    }

    public void setManager(FilterManager manager)
    {
	this.manager = manager;
    }
    
    public void setPrefs(Prefs prefs)
    {
	this.prefs = prefs;
    }

    public Prefs getPrefs()
    {
	return prefs;
    }

    public void viewPrefs()
    {
	if (frame == null)
	{
	    frame = new StatsFrame(prefs, this);
	}
	frame.setVisible(true);
    }
    
    public Filter createFilter()
    {
	Filter f = new StatsFilter(this);
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

    void increment(Hashtable h, String key, int value)
    {
        Integer count = (Integer) h.get(key);
        if (count == null)
        {
            count = new Integer(0);
        }
        count = new Integer(count.intValue() + value);
        h.put(key, count);
    }

    synchronized void recordRequest(Request r)
    {
	requests++;
	String s = r.getHost();
	increment(hosts, s != null ? s : "null", 1);
    }
    
    synchronized void recordReply(Reply r)
    {
	replies++;
	String s = r.getContentType();
        increment(contentTypes, s != null ? s : "null", 1);
        if (s != null)
        {
            try 
            {
                int length = Integer.parseInt(r.getHeaderField("Content-length"));
                increment(contentLengths, s, length);
            }
            catch (Exception e)
            {
            }
        }
        
        s = r.getHeaderField("Server");
        increment(servers, s != null ? s : "null", 1);
    }

    void reset()
    {
        requests = 0;
        replies = 0;
        hosts = new Hashtable(100);
        servers = new Hashtable(100);
        contentTypes = new Hashtable(100);
        contentLengths = new Hashtable(100);
    }
}
