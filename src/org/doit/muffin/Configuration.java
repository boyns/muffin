/* $Id: Configuration.java,v 1.15 2003/05/20 21:02:37 flefloch Exp $ */

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
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.NoSuchElementException;
import java.awt.Label;
import java.awt.Choice;
import org.doit.muffin.regexp.Pattern;
import org.doit.muffin.regexp.Factory;
import org.doit.util.*;

class Configuration extends Prefs
{
	// FIXME: these instance vars ought to be private
	// FIXME: why are we using Vectors and not ArrayLists (or LinkedList)?
	// FIXME: autoConfigPatterns and autoConfigNames:
	//        create a new kind of HashMap that matches regexes instead of keys
	//        because this functionality is used in several places.
	
    String autoConfigFile = "autoconfig";
    String currentConfig = null;
    String defaultConfig = null;
    Vector autoConfigPatterns = null;
    Vector autoConfigNames = null;
    ConfigurationFrame frame = null;
    Vector configurationListeners = null;

    Configuration()
    {
	autoConfigPatterns = new Vector();
	autoConfigNames = new Vector();
	configurationListeners = new Vector();
    }

    void setAutoConfigFile(String file)
    {
	autoConfigFile = file;
    }

    void addConfigurationListener(Object obj)
    {
	configurationListeners.addElement(obj);
	updateConfigurationListener(obj);
    }

    synchronized void updateConfigurationListener(Object obj)
    {
	if (obj instanceof Label)
	{
	    Label label = (Label) obj;
	    label.setText(Strings.getString("config.current", getCurrent()));
	    label.doLayout();
	}
	else if (obj instanceof Choice)
	{
	    Choice choice = (Choice) obj;
	    choice.select(getCurrent());
	}
	else if (obj instanceof ConfigurationListener)
	{
	    ConfigurationListener cl = (ConfigurationListener) obj;
	    cl.configurationChanged(getCurrent());
	}
    }

    void updateConfigurationListeners()
    {
	for (int i = 0; i < configurationListeners.size(); i++)
	{
	    Object obj = configurationListeners.elementAt(i);
	    updateConfigurationListener(obj);
	}
    }

    void setDefault(String name)
    {
	defaultConfig = name;
    }

    String getDefault()
    {
	return defaultConfig;
    }

    synchronized void setCurrent(String name)
    {
	if (!name.equals(currentConfig))
	{
	    currentConfig = name;
	    if (! exists(currentConfig))
	    {
		createConfig(currentConfig);
	    }
	    updateConfigurationListeners();
	}
    }

    String getCurrent()
    {
	return currentConfig;
    }

    void createConfig(String name)
    {
	if (! name.endsWith(".conf"))
	{
	    name = name + ".conf";
	}
	//System.out.println("Creating new configuration: " + name);
	UserPrefs prefs = new UserPrefs(name);
	put(name, prefs);
	updateConfigurationListeners();
    }

    UserPrefs getUserPrefs()
    {
	return getUserPrefs(currentConfig);
    }

    UserPrefs getUserPrefs(String config)
    {
	UserPrefs prefs = (UserPrefs) get(config);
	prefs.load(); /* Load if necessary */
	return prefs;
    }

	/**
	 * Returns first autoConfigName that matches the given regexp pattern.	 * @param pattern The regexp pattern for which to find an autoConfigName.	 * @return String The first autoConfigName that matches the given regexp pattern.	 */
    String autoConfig(String pattern) {
		for (int i = 0; i < autoConfigPatterns.size(); i++) {
		    Pattern re = (Pattern) autoConfigPatterns.elementAt(i);
		    if (re.matches(pattern)){
				return (String) autoConfigNames.elementAt(i);
		    }
		}
		return defaultConfig;
    }

    UserFile getAutoConfigFile()
    {
	return getUserFile(autoConfigFile);
    }

    UserFile getUserConfigFile(String name)
    {
	return getUserFile(name);
    }

    boolean deleteUserConfigFile(String name)
    {
	UserFile file = getUserConfigFile(name);
	if (file instanceof LocalFile)
	{
	    return ((LocalFile)file).delete();
	}
	return false;
    }

    boolean delete(String name)
    {
	if (name.equals(getDefault()))
	{
	    System.out.println("Can't delete default configuration");
	    return false;
	}

	remove(name);

	if (name.equals(getCurrent()))
	{
	    setCurrent(getDefault());
	}

	return true;
    }

	/**
	 * Loads our config patterns and associated names from the given Reader.	 * @param reader The Reader from which to load config patterns and names.	 */
    void load(Reader reader) {
		autoConfigPatterns = new Vector();
		autoConfigNames = new Vector();
	
		try {
		    BufferedReader in = new BufferedReader(reader);
		    String s;
		    while ((s = in.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(s, " \t");
				String pattern, name;
		
				try {
				    pattern = st.nextToken();
				    name = st.nextToken();
				} catch (NoSuchElementException nsee) {
				    continue;
				}
		
				Pattern re = Factory.instance().getPattern(pattern);
				autoConfigPatterns.addElement(re);
				autoConfigNames.addElement(name);
		    }
		    in.close();
		} catch (IOException e) {
		    System.out.println(e);
		}
    }

    void load()
    {
	InputStream in = null;

	try
	{
	    UserFile file = getAutoConfigFile();
	    in = file.getInputStream();
	    load(new InputStreamReader(in));
	    System.out.println("Using " + file.getName());
	}
	catch (FileNotFoundException e)
	{
	}
	catch (IOException e)
	{
	    System.out.println(e);
	}
	finally
	{
	    try
	    {
		if (in != null)
		{
		    in.close();
		}
	    }
	    catch (IOException e)
	    {
	    }
	}
    }

    void reload()
    {
	load();
	if (frame != null)
	{
	    frame.loadAutoConfigFile();
	}
    }

    void rescan()
    {
	clear();
	scan();
    }

    void scan()
    {
	File dir = new File(getUserDirectory());
	if (!dir.exists())
	{
	    return;
	}

	String list[] = dir.list(new ConfFileFilter());
	for (int i = 0; i < list.length; i++)
	{
	    UserPrefs prefs = new UserPrefs(list[i]);
	    put(list[i], prefs);
	}
    }

    void createFrame()
    {
	if (frame == null)
	{
	    frame = new ConfigurationFrame(this);
	}
	frame.show();
    }
    
    /**
     * Returns the autoConfigNames.
     * @return Vector
     */
    public Vector getAutoConfigNames()
    {
        return autoConfigNames;
    }

    /**
     * Returns the autoConfigPatterns.
     * @return Vector
     */
    public Vector getAutoConfigPatterns()
    {
        return autoConfigPatterns;
    }

}
