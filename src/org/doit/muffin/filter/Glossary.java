/* $Id: Glossary.java,v 1.7 2000/03/29 15:13:22 boyns Exp $ */

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

import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

public class Glossary extends Hashtable implements FilterFactory
{
    FilterManager manager;
    Prefs prefs;
    GlossaryFrame frame = null;

    public Glossary()
    {
	super(33);
    }

    public void setManager(FilterManager manager)
    {
	this.manager = manager;
    }
    
    public void setPrefs(Prefs prefs)
    {
	this.prefs = prefs;
	boolean o = prefs.getOverride();
	prefs.setOverride(false);
	String filename = "glossary";
	prefs.putString("Glossary.glossaryfile", filename);
	prefs.setOverride(o);
	load();
    }

    public Prefs getPrefs()
    {
	return prefs;
    }

    public void viewPrefs()
    {
	if (frame == null)
	{
	    frame = new GlossaryFrame(prefs, this);
	}
	frame.setVisible(true);
    }
    
    public Filter createFilter()
    {
	Filter f = new GlossaryFilter(this);
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

    String lookup(String term)
    {
	return(String) get(term.toLowerCase());
    }

    void load()
    {
	try
	{
	    UserFile file = prefs.getUserFile(prefs.getString("Glossary.glossaryfile"));
	    BufferedReader in = new BufferedReader(new InputStreamReader(file.getInputStream()));
	    String s;
	    while ((s = in.readLine()) != null)
	    {
		StringTokenizer st = new StringTokenizer(s, " \t");
		String term = st.nextToken();
		String url = st.nextToken();
		put(term.toLowerCase(), url);
	    }
	    in.close();
	}
	catch (FileNotFoundException e)
	{
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }
}
