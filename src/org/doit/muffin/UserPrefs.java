/* $Id: UserPrefs.java,v 1.11 2000/03/29 15:16:58 boyns Exp $ */

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

import java.io.*;
import java.util.Enumeration;
import java.util.Properties;
import org.doit.util.*;

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

    String getPrefsFile()
    {
	return rcfile;
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
	UserFile file = getUserFile(rcfile);
	//System.out.println("Loading " + file.getAbsolutePath());
	try
	{
	    InputStream in = file.getInputStream();
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
	catch (FileNotFoundException e)
	{
	}
	catch (IOException e)
	{
	    System.out.println(e);
	}
	loaded = true;
    }

    void save()
    {
	Properties props = new SortedProperties();
	Enumeration e = keys();
	while (e.hasMoreElements())
	{
	    String key = (String) e.nextElement();
	    props.put(key, get(key));
	}
	checkUserDirectory();
	try
	{
	    UserFile file = getUserFile(rcfile);
	    if (file instanceof LocalFile)
	    {
		OutputStream out = file.getOutputStream();
		props.save(out, null);
		out.close();
	    }
	    else
	    {
		System.out.println("Can't save to " + file.getName());
	    }
	}
	catch (IOException ex)
	{
	    System.out.println(e);
	}
    }
}
