/* $Id: FilterManager.java,v 1.4 1999/03/12 15:47:39 boyns Exp $ */

/*
 * Copyright (C) 1996-99 Mark R. Boyns <boyns@doit.org>
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

import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;

/**
 * Class to manage all filters.  This class maintains the list of
 * known filters, enabled filters, and all filter preferences.
 *
 * @see muffin.Filter
 * @see muffin.FilterFactory
 * @author Mark Boyns
 */
public class FilterManager implements ConfigurationListener
{
    final String defaultKnownList[] = 
    { 
	"AnimationKiller",
	"CookieMonster",
	"Decaf",
	"DocumentInfo",
	"EmptyFont",
	"ForwardedFor",
	"Glossary",
	"History",
	"ImageKill",
	"HostnameExpander",
	"NoCode",
	"Painter",
	"Preview",
	"Rewrite",
	"NoThanks",
	"Referer",
	"Secretary",
	"SecretAgent",
	"SecretServer",
	"Snoop",
	"Stats",
    };

    Options options = null;
    Configuration configs = null;
    FilterManagerFrame frame = null;

    UserPrefs userPrefs = null;
    Vector knownFilters = null;
    Vector enabledFilters = null;

    Hashtable knownFiltersCache = null;
    Hashtable enabledFiltersCache = null;

    /**
     * Create a FilterManager.
     */
    FilterManager(Options options, Configuration configs)
    {
	this.options = options;
	this.configs = configs;

	knownFiltersCache = new Hashtable();
	enabledFiltersCache = new Hashtable();

	configs.addConfigurationListener(this);
    }

    public void configurationChanged(String name)
    {
	userPrefs = configs.getUserPrefs();
	knownFilters = getKnownFilters(name);
	enabledFilters = getEnabledFilters(name);
    }

    Vector getKnownFilters(String config)
    {
	if (! knownFiltersCache.containsKey(config))
	{
	    Vector known = new Vector(32);
	    knownFiltersCache.put(config, known);
	    UserPrefs uprefs = configs.getUserPrefs(config);
	    String list[] = uprefs.getStringList("muffin.knownFilters");
	    if (list.length == 0)
	    {
		list = defaultKnownList;
	    }
	    for (int i = 0; i < list.length; i++)
	    {
		known.addElement(list[i]);
	    }
	}
	return(Vector) knownFiltersCache.get(config);
    }

    Vector getEnabledFilters(String config)
    {
	if (! enabledFiltersCache.containsKey(config))
	{
	    Vector enabled = new Vector(32);
	    enabledFiltersCache.put(config, enabled);
	    UserPrefs uprefs = configs.getUserPrefs(config);
	    String list[] = uprefs.getStringList("muffin.enabledFilters");
	    for (int i = 0; i < list.length; i++)
	    {
		enable(config, list[i]);
	    }
	}
	return(Vector) enabledFiltersCache.get(config);
    }

    void checkAutoConfig(String pattern)
    {
	String name = configs.autoConfig(pattern);
	if (!name.equals(configs.getCurrent()))
	{
	    System.out.println("Automatic change to " + name);
	    configs.setCurrent(name);
	}
    }

    /**
     * Return a list of filters created by each filter's
     * factory method.
     *
     * @see muffin.Handler
     */
    Filter[] createFilters()
    {
	FilterFactory ff;
	Filter list[] = new Filter[enabledFilters.size()];
	for (int i = 0; i < list.length; i++)
	{
	    ff = (FilterFactory) enabledFilters.elementAt(i);
	    list[i] = ff.createFilter();
	}
	return list;
    }

    void createFrame()
    {
	if (frame == null)
	{
	    frame = new FilterManagerFrame(this);
	}
	frame.hideshow();
    }

    String shortName(String clazz)
    {
	if (clazz.startsWith("muffin.filter.")
	    || clazz.startsWith("org.doit.muffin.filter."))
	{
	    return clazz.substring(clazz.lastIndexOf('.') + 1);
	}
	return clazz;
    }

    void append(String clazz)
    {
	knownFilters.addElement(clazz);
	if (frame != null)
	{
	    frame.updateKnownFiltersList();
	}
    }

    void remove(String clazz)
    {
	for (int i = 0; i < knownFilters.size(); i++)
	{
	    String name = (String) knownFilters.elementAt(i);
	    if (name.equals(clazz))
	    {
		knownFilters.removeElementAt(i);
	    }
	}
	if (frame != null)
	{
	    frame.updateKnownFiltersList();
	}
    }

    void enable(String config, String clazz)
    {
	clazz = shortName(clazz);

	if (clazz.indexOf('.') == -1)
	{
	    clazz = "org.doit.muffin.filter." + clazz;
	}
	
	try
	{
	    //System.out.println("Enabling " + shortName(clazz) + " in " + config);
	    FilterFactory ff = (FilterFactory) (Class.forName(clazz)).newInstance();
	    UserPrefs uprefs = configs.getUserPrefs(config);
	    Vector enabled = getEnabledFilters(config);
	    Prefs prefs = uprefs.extract(clazz.substring(clazz.lastIndexOf('.') + 1));
	    ff.setPrefs(prefs);
	    ff.setManager(this);
	    enabled.addElement(ff);
	    if (frame != null)
	    {
		frame.updateEnabledFiltersList();
	    }
	}
	catch (Exception e)
	{
	    System.out.println("");
	    System.out.println("WARNING: Can't load " + clazz + ": ");
	    System.out.println("");
	    System.out.println("         " + e);
	    System.out.println("");
	    System.out.println("         You may need to restart muffin with a different CLASSPATH.");
	    System.out.println("");
	}
    }

    void enable(String clazz)
    {
	enable(configs.getCurrent(), clazz);
    }

    void disable(Vector enabled, int i)
    {
	FilterFactory ff = (FilterFactory) enabled.elementAt(i);
	ff.shutdown();
	enabled.removeElementAt(i);
	if (frame != null)
	{
	    frame.updateEnabledFiltersList();
	}
    }

    void disable(int i)
    {
	disable(configs.getCurrent(), i);
    }

    void disable(String config, int i)
    {
	Vector enabled = getEnabledFilters(config);
	disable(enabled, i);
    }

    void disableAll()
    {
	Enumeration e = configs.keys();
	while (e.hasMoreElements())
	{
	    String config = (String) e.nextElement();
	    Vector enabled = getEnabledFilters(config);
	    for (int i = enabled.size() - 1; i >= 0; i--)
	    {
		disable(enabled, i);
	    }
	}
    }

    void save(String config)
    {
	Vector enabled = getEnabledFilters(config);
	String list[] = new String[enabled.size()];
	for (int i = 0; i < list.length; i++)
	{
	    FilterFactory ff = (FilterFactory) enabled.elementAt(i);
	    list[i] = shortName((ff.getClass()).getName());
	}
	UserPrefs uprefs = configs.getUserPrefs(config);
	uprefs.putStringList("muffin.enabledFilters", list);
	uprefs.save();
    }
    
    void save()
    {
	save(configs.getCurrent());
    }

    void saveAll()
    {
	Enumeration e = configs.keys();
	while (e.hasMoreElements())
	{
	    String config = (String) e.nextElement();
	    save(config);
	}
    }

    /**
     * Save filter preferences.
     *
     * @param ff filter factory
     */
    public void save(FilterFactory ff)
    {
	userPrefs.merge(ff.getPrefs());
	userPrefs.save();
    }
}    
