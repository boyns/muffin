/* $Id: Configuration.java,v 1.2 1998/08/13 06:01:00 boyns Exp $ */

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
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.util.StringTokenizer;
import java.util.Vector;
import java.awt.Label;
import java.awt.Choice;
import gnu.regexp.RE;

class Configuration extends Prefs
{
    final String autoConfigFile = "autoconfig";
    String currentConfig = null;
    String defaultConfig = null;
    Vector autoConfigPatterns = null;
    Vector autoConfigNames = null;
    ConfigurationFrame frame = null;
    Vector configurationListeners = null;
    
    Configuration ()
    {
	autoConfigPatterns = new Vector ();
	autoConfigNames = new Vector ();
	configurationListeners = new Vector ();
    }

    void addConfigurationListener (Object obj)
    {
	configurationListeners.addElement (obj);
	updateConfigurationListener (obj);
    }

    synchronized void updateConfigurationListener (Object obj)
    {
	if (obj instanceof Label)
	{
	    Label label = (Label) obj;
	    label.setText ("Current Configuration: " + getCurrent ());
	    label.doLayout ();
	}
	else if (obj instanceof Choice)
	{
	    Choice choice = (Choice) obj;
	    choice.select (getCurrent ());
	}
	else if (obj instanceof ConfigurationListener)
	{
	    ConfigurationListener cl = (ConfigurationListener) obj;
	    cl.configurationChanged (getCurrent ());
	}
    }

    void updateConfigurationListeners ()
    {
	for (int i = 0; i < configurationListeners.size (); i++)
	{
	    Object obj = configurationListeners.elementAt (i);
	    updateConfigurationListener (obj);
	}
    }

    void setDefault (String name)
    {
	defaultConfig = name;
    }

    String getDefault ()
    {
	return defaultConfig;
    }
    
    synchronized void setCurrent (String name)
    {
	currentConfig = name;
	if (! exists (currentConfig))
	{
	    createConfig (currentConfig);
	}
	updateConfigurationListeners ();
    }

    String getCurrent ()
    {
	return currentConfig;
    }

    void createConfig (String name)
    {
	if (! name.endsWith (".conf"))
	{
	    name = name + ".conf";
	}
	System.out.println ("Creating new configuration: " + name);
	UserPrefs prefs = new UserPrefs (name);
	prefs.setUserDirectory (getUserDirectory ());
	put (name, prefs);
	updateConfigurationListeners ();
    }

    UserPrefs getUserPrefs ()
    {
	return getUserPrefs (currentConfig);
    }

    UserPrefs getUserPrefs (String config)
    {
	UserPrefs prefs = (UserPrefs) get (config);
	prefs.load (); /* Load if necessary */
	return prefs;
    }

    String autoConfig (String pattern)
    {
	for (int i = 0; i < autoConfigPatterns.size (); i++)
	{
	    RE re = (RE) autoConfigPatterns.elementAt (i);
	    if (re.getMatch (pattern) != null)
	    {
		return (String) autoConfigNames.elementAt (i);
	    }
	}
	return defaultConfig;
    }

    String getAutoConfigFile ()
    {
	return getUserFile (autoConfigFile);
    }

    String getUserConfigFile (String name)
    {
	return getUserFile (name);
    }

    boolean deleteUserConfigFile (String name)
    {
	File file = new File (getUserConfigFile (name));
	return file.delete ();
    }

    boolean delete (String name)
    {
	if (name.equals (getDefault ()))
	{
	    System.out.println ("Can't delete default configuration");
	    return false;
	}
	
	remove (name);

	if (name.equals (getCurrent ()))
	{
	    setCurrent (getDefault ());
	}

	return true;
    }

    void load (Reader reader)
    {
	autoConfigPatterns = new Vector ();
	autoConfigNames = new Vector ();
	
	try
	{
	    BufferedReader in = new BufferedReader (reader);
	    String s;
	    while ((s = in.readLine ()) != null)
	    {
		StringTokenizer st = new StringTokenizer (s, " \t");
		String pattern = st.nextToken ();
		String name = st.nextToken ();

		RE re = new RE (pattern);
		autoConfigPatterns.addElement (re);
		autoConfigNames.addElement (name);
	    }
	    in.close ();
	}
	catch (Exception e)
	{
	    System.out.println (e);
	}
    }

    void load ()
    {
	File file = new File (getAutoConfigFile ());
	if (!file.exists ())
	{
	    System.out.println (file.getAbsolutePath () + " does not exist");
	    return;
	}

	System.out.println ("Using " + file.getAbsolutePath ());
	try
	{
	    load (new FileReader (file));
	}
	catch (Exception e)
	{
	    System.out.println (e);
	}
    }

    void reload ()
    {
	load ();
	if (frame != null)
	{
	    frame.loadAutoConfigFile ();
	}
    }

    void rescan ()
    {
	clear ();
	scan ();
    }

    void scan ()
    {
	File dir = new File (getUserDirectory ());
	if (!dir.exists ())
	{
	    return;
	}

	String list[] = dir.list (new ConfFileFilter ());
	for (int i = 0; i < list.length; i++)
	{
	    UserPrefs prefs = new UserPrefs (list[i]);
	    prefs.setUserDirectory (getUserDirectory ());
	    put (list[i], prefs);
	}
    }

    void createFrame ()
    {
	if (frame == null)
	{
	    frame = new ConfigurationFrame (this);
	}
	frame.show ();
    }
}
