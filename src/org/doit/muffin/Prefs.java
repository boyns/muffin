/* Prefs.java */

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

import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Enumeration;
import java.io.File;
import java.awt.Color;
import java.awt.Font;
import sdsu.compare.StringIgnoreCaseComparer;
import sdsu.util.SortedList;

/**
 * @author Mark Boyns
 */
public class Prefs extends Hashtable
{
    String userDirectory = "Muffin";
    
    boolean allowOverride = true;
    
    public Prefs (int capacity)
    {
	super (capacity);
    }

    public Prefs ()
    {
	super ();
    }

    public void setUserDirectory (String dir)
    {
	userDirectory = dir;
    }

    public String getUserDirectory ()
    {
	/* Check for a pathname */
	if (userDirectory.indexOf (System.getProperty ("file.separator")) != -1)
	{
	    return userDirectory;
	}
	/* Use user.home */
	else
	{
	    return System.getProperty ("user.home") +
		System.getProperty ("file.separator") +
		userDirectory;
	}
    }

    public void checkDirectory (String name)
    {
	File dir = new File (name);
	if (!dir.exists ())
	{
	    System.out.println ("Creating " + name);
	    if (!dir.mkdirs ())
	    {
		System.out.println ("Can't create " + name);
	    }
	}
    }

    public void checkUserDirectory ()
    {
	checkDirectory (getUserDirectory ());
    }

    public String getUserFile (String file)
    {
	if (file.indexOf (System.getProperty ("file.separator")) != -1)
	{
	    return file;
	}
	return getUserDirectory () + System.getProperty ("file.separator") + file;
    }

    public boolean exists (Object key)
    {
	return containsKey (key);
    }

    public synchronized Object put (Object key, Object value)
    {
	if (allowOverride || !exists (key))
	{
	    return super.put (key, value);
	}
	return null;
    }

    public void setOverride (boolean o)
    {
	allowOverride = o;
    }

    public boolean getOverride ()
    {
	return allowOverride;
    }
    
    public void putString (String key, String value)
    {
	put (key, value);
    }

    public void putBoolean (String key, boolean value)
    {
	put (key, value ? "true" : "false");
    }

    public void putInteger (String key, int value)
    {
	put (key, Integer.toString (value));
    }

    public String getString (String key)
    {
	return (String) get (key);
    }

    public boolean getBoolean (String key)
    {
	String val = getString (key);
	if (val == null)
	{
	    return false;
	}
	return "true".equalsIgnoreCase (val) ? true : false;
    }

    public int getInteger (String key)
    {
	int val;
	try
	{
	    val = Integer.parseInt (getString (key));
	}
	catch (Exception e)
	{
	    val = -1;
	}
	return val;
    }

    String toHexString (int value, int length)
    {
	String s = Integer.toHexString (value);

	while (s.length () < length)
	{
	    s = "0" + s;
	}

	return s;
    }

    public void putColor (String key, Color c)
    {
	putString (key, "#"
		   + toHexString (c.getRed (), 2)
		   + toHexString (c.getGreen (), 2)
		   + toHexString (c.getBlue (), 2));
    }

    public Color getColor (String key)
    {
	Color c = null;
	try
	{
	    c = Color.decode (getString (key));
	}
	catch (Exception e)
	{
	}
	return c;
    }

    public Font getFont (String key)
    {
	return Font.decode (getString (key));
    }

    public Prefs extract (String clazz)
    {
	Prefs prefs = new Prefs ();
	Enumeration e = keys ();
	while (e.hasMoreElements ())
	{
	    String key = (String) e.nextElement ();
	    if (key.startsWith (clazz + "."))
	    {
		prefs.put (key, get (key));
	    }
	}
	return prefs;
    }

    public void merge (Prefs prefs)
    {
	Enumeration e = prefs.keys ();
	while (e.hasMoreElements ())
	{
	    String key = (String) e.nextElement ();
	    put (key, prefs.get (key));
	}
    }

    public String[] getStringList (String key)
    {
	String tokens = getString (key);
	if (tokens == null)
	{
	    return new String[0];
	}
	StringTokenizer st = new StringTokenizer (tokens, ",");
	String list[] = new String[st.countTokens ()];
	for (int i = 0; st.hasMoreTokens (); i++)
	{
	    list[i] = new String ((String) st.nextToken ());
	}
	return list;
    }

    public void putStringList (String key, String list[])
    {
	StringBuffer buf = new StringBuffer ();
	for (int i = 0; i < list.length; i++)
	{
	    buf.append (list[i]);
	    if (i != list.length - 1)
	    {
		buf.append (",");
	    }
	}
	putString (key, buf.toString ());
    }

    public synchronized Enumeration sortedKeys ()
    {
	Enumeration e = keys ();
	SortedList sorter = new SortedList (StringIgnoreCaseComparer.getInstance ());
	while (e.hasMoreElements ())
	{
	    sorter.addElement ((String) e.nextElement ());
	}
	return sorter.elements ();
    }

    void print ()
    {
	Enumeration e = keys ();
	while (e.hasMoreElements ())
	{
	    String key = (String) e.nextElement ();
	    System.out.println (" * " + key + " = " + get (key));
	}
    }
}
