/* $Id: UserPrefs.java,v 1.4 1998/12/19 21:24:17 boyns Exp $ */

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.Properties;
import org.doit.util.SortedProperties;

/**
 * @author Mark Boyns
 */
class UserPrefs extends Prefs
{
    String rcfile = "unknown";
    boolean loaded = false;

    UserPrefs()
    {
    }
    
    UserPrefs(String filename)
    {
	setPrefsFile(filename);
    }

    void setPrefsFile(String filename)
    {
	rcfile = filename;
    }

    void unload()
    {
	clear();
	loaded = false;
    }
    
    void load()
    {
	if (loaded)
	{
	    return;
	}
	File file = new File(getUserFile(rcfile));
	if (!file.exists())
	{
	    return;
	}
	//System.out.println("Loading " + file.getAbsolutePath());
	try
	{
	    FileInputStream in = new FileInputStream(file);
	    Properties props = new Properties();
	    props.load(in);
	    Enumeration e = props.keys();
	    while (e.hasMoreElements())
	    {
		String key = (String) e.nextElement();
		put(key, props.get(key));
	    }
	    in.close();
	}
	catch (Exception e)
	{
	    System.out.println(e);
	}
	loaded = true;
    }

    void save()
    {
	// XXX commented due to reportred stack overflow
	// bug.  For now just use plain old properties.
	//Properties props = new SortedProperties();
	Properties props = new Properties();
	Enumeration e = keys();
	while (e.hasMoreElements())
	{
	    String key = (String) e.nextElement();
	    props.put(key, get(key));
	}
	checkUserDirectory();
	File file = new File(getUserFile(rcfile));
	System.out.println("Saving " + file.getAbsolutePath());
	try
	{
	    FileOutputStream out = new FileOutputStream(file);
	    props.save(out, null);
	    out.close();
	}
	catch (Exception ex)
	{
	    System.out.println(ex);
	}
    }
}
