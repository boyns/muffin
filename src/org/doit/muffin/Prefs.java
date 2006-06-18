/* $Id: Prefs.java,v 1.15 2006/06/18 23:25:51 forger77 Exp $ */

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

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;

import org.doit.util.StringUtil;

import sdsu.compare.StringIgnoreCaseComparer;
import sdsu.util.SortedList;

/**
 * @author Mark Boyns
 */
public class Prefs extends Hashtable
{
    /**
	 * Serializable class should define this:
	 */
	private static final long serialVersionUID = 1L;

    private static String userDirectory = "Muffin";
    private static String userHomeProperty = "user.home";

    boolean allowOverride = true;

    public Prefs(int capacity)
    {
	super(capacity);
    }

    public Prefs()
    {
	super();
    }

    public void setUserDirectory(String dir)
    {
	userDirectory = dir;
    }

    public String getUserDirectory()
    {
	/* Check for a pathname */
	if (userDirectory != null &&
            userDirectory.indexOf(System.getProperty("file.separator")) != -1)
	{
	    return userDirectory;
	}

	/* Use user.home */
	StringBuffer sb = new StringBuffer();
	sb.append(System.getProperty(userHomeProperty));
	if (userDirectory != null && userDirectory.length() > 0)
	{
	    sb.append(System.getProperty("file.separator"));
	    sb.append(userDirectory);
	}

	return sb.toString();
    }

    public void checkDirectory(String name)
    {
	File dir = new File(name);
	if (!dir.exists())
	{
	    System.out.println("Creating " + name);
	    if (!dir.mkdirs())
	    {
		System.out.println("Can't create " + name);
	    }
	}
    }

    public void checkUserDirectory()
    {
	checkDirectory(getUserDirectory());
    }

    public UserFile getUserFile(String file)
    {
	UserFile userFile = null;

	try
	{
	    userFile = new URLFile(new URL(file));
	}
	catch (MalformedURLException e)
	{
	    if (file.indexOf(System.getProperty("file.separator")) != -1)
	    {
		userFile = new LocalFile(file);
	    }
	    else
	    {
		userFile = new LocalFile(getUserDirectory() +
					 System.getProperty("file.separator") +
					 file);
	    }
	}

	return userFile;
    }

    public boolean exists(Object key)
    {
	return containsKey(key);
    }

    public synchronized Object put(Object key, Object value)
    {
	if (allowOverride || !exists(key))
	{
	    return super.put(key, value);
	}
	return null;
    }

	/**
	 * Affects the override mode for put operations. 
	 * If set to true, existing keys will be overridden.
	 * If false, existing keys will not be overridden.
	 * New keys (i.e. not present in the Prefs so far) will be accepted in any case.
	 * @see org.doit.muffin.Prefs#put(Object, Object).
	 * 	 * @param o The override mode to set.	 */
	public void setOverride(boolean o) {
		allowOverride = o;
	}

    public boolean getOverride()
    {
	return allowOverride;
    }

    public void putString(String key, String value)
    {
	put(key, value);
    }

    public void putBoolean(String key, boolean value)
    {
	put(key, value ? "true" : "false");
    }

    public void putInteger(String key, int value)
    {
	put(key, Integer.toString(value));
    }

    public String getString(String key)
    {
	return (String) get(key);
    }

    public boolean getBoolean(String key)
    {
	String val = getString(key);
	return val != null &&
               "true".equalsIgnoreCase(val);
    }

    public int getInteger(String key)
    {
	int val;
	try
	{
	    val = Integer.parseInt(getString(key));
	}
	catch (NumberFormatException e)
	{
	    val = -1;
	}
	return val;
    }

    public long getLong(String key)
    {
	long val;
	try
	{
	    val = Long.parseLong(getString(key));
	}
	catch (NumberFormatException e)
	{
	    val = -1;
	}
	return val;
    }

    String toHexString(int value, int length)
    {
	String s = Integer.toHexString(value);

	while (s.length() < length)
	{
	    s = "0" + s;
	}

	return s;
    }

    public void putColor(String key, Color c)
    {
	putString(key, "#"
		   + toHexString(c.getRed(), 2)
		   + toHexString(c.getGreen(), 2)
		   + toHexString(c.getBlue(), 2));
    }

    public Color getColor(String key)
    {
	Color c = null;
	try
	{
	    c = Color.decode(getString(key));
	}
	catch (NumberFormatException e)
	{
	}
	return c;
    }

    public Font getFont(String key)
    {
	return Font.decode(getString(key));
    }

    public Prefs extract(String clazz)
    {
	Prefs prefs = new Prefs();
	Enumeration e = keys();
	while (e.hasMoreElements())
	{
	    String key = (String) e.nextElement();
	    if (key.startsWith(clazz + "."))
	    {
		prefs.put(key, get(key));
	    }
	}
	return prefs;
    }

    public void merge(Prefs prefs)
    {
	Enumeration e = prefs.keys();
	while (e.hasMoreElements())
	{
	    String key = (String) e.nextElement();
	    put(key, prefs.get(key));
	}
    }

    public String[] getStringList(String key, String sep)
    {
        return StringUtil.getList(getString(key));
    }

    public String[] getStringList(String key)
    {
        return StringUtil.getList(getString(key), ",");
    }

    public void putStringList(String key, String list[])
    {
	StringBuffer buf = new StringBuffer();
	for (int i = 0; i < list.length; i++)
	{
	    buf.append(list[i]);
	    if (i != list.length - 1)
	    {
		buf.append(",");
	    }
	}
	putString(key, buf.toString());
    }

    public synchronized Enumeration sortedKeys()
    {
	Enumeration e = keys();
	SortedList sorter = new SortedList(StringIgnoreCaseComparer.getInstance());
	while (e.hasMoreElements())
	{
	    sorter.addElement((String) e.nextElement());
	}
	return sorter.elements();
    }

    void print()
    {
	Enumeration e = keys();
	while (e.hasMoreElements())
	{
	    String key = (String) e.nextElement();
	    System.out.println(" * " + key + " = " + get(key));
	}
    }
}
