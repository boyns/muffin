/* $Id: Options.java,v 1.11 2000/03/27 04:24:41 boyns Exp $ */

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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import java.awt.Color;

/**
 * @author Mark Boyns
 */
public class Options extends UserPrefs
{
    OptionsFrame frame;
    Vector hostsAllow;
    Vector hostsDeny;
    Vector adminAllow;
    Vector adminDeny;

//     Configuration configs = null;
    
//     Options(Configuration configs)
//     {
// 	this.configs = configs;
    Options(String filename)
    {
	super(filename);
	
	/* Load the properties with some reasonable defaults. */
	putInteger("muffin.port", 51966);
	putString("muffin.httpProxyHost", "");
	putString("muffin.httpProxyPort", "");
	putString("muffin.httpsProxyHost", "");
	putString("muffin.httpsProxyPort", "");
	putString("muffin.geometry", "");
	putBoolean("muffin.noWindow", false);
        
	/* By default only allow the localhost access */
	try
	{
	    InetAddress thishost = InetAddress.getLocalHost();
	    putString("muffin.hostsAllow", "127.0.0.1," + thishost.getHostAddress());
	}
	/* If this fails allow access to everyone */
	catch (UnknownHostException e)
	{
	    putString("muffin.hostsAllow", "ALL");
	}
	/* Deny the rest */
	putString("muffin.hostsDeny", "ALL");

	/* Use hostsAllow and hostsDeny as defaults */
	putString("muffin.adminAllow", getString("muffin.hostsAllow"));
	putString("muffin.adminDeny", getString("muffin.hostsDeny"));
	putString("muffin.adminUser", "");
	putString("muffin.adminPassword", "");

	/* Default colors */
	putColor("muffin.bg", Color.lightGray);
	putColor("muffin.fg", Color.black);

	/* Default fonts */
 	putString("muffin.font", "helvetica-bold-12");
 	putString("muffin.smallfont", "helvetica-plain-10");
 	putString("muffin.bigfont", "helvetica-bold-16");

	/* Default log file */
 	putString("muffin.logfile", "muffin.log");
 	putString("muffin.maxLogFileSize", "1048576");
 	putString("muffin.maxLogFileHistory", "10");
 	putString("muffin.autoconfig", "autoconfig");

	/* Other defaults */
 	putBoolean("muffin.proxyKeepAlive", false);
 	putInteger("muffin.readTimeout", 90000);
 	putString("muffin.nameservers", "");

// 	include(configs.getUserPrefs());
// 	configs.addConfigurationListener(this);

	load();
    }

//     public void configurationChanged(String name)
//     {
// 	include(configs.getUserPrefs());
//     }

    void createFrame()
    {
	if (frame == null)
	{
	    frame = new OptionsFrame(this);//, configs);
	}
	frame.hideshow();	
    }
    
    void updateHostsAllow()
    {
	hostsAllow = new Vector(100);
	StringTokenizer st = new StringTokenizer(getString("muffin.hostsAllow"), ", \t");
	while (st.hasMoreTokens())
	{
	    hostsAllow.addElement(st.nextToken());
	}
    }

    void updateHostsDeny()
    {
	hostsDeny = new Vector(100);
	StringTokenizer st = new StringTokenizer(getString("muffin.hostsDeny"), ", \t");
	while (st.hasMoreTokens())
	{
	    hostsDeny.addElement(st.nextToken());
	}
    }

    void updateAdminAllow()
    {
	adminAllow = new Vector(100);
	StringTokenizer st = new StringTokenizer(getString("muffin.adminAllow"), ", \t");
	while (st.hasMoreTokens())
	{
	    adminAllow.addElement(st.nextToken());
	}
    }

    void updateAdminDeny()
    {
	adminDeny = new Vector(100);
	StringTokenizer st = new StringTokenizer(getString("muffin.adminDeny"), ", \t");
	while (st.hasMoreTokens())
	{
	    adminDeny.addElement(st.nextToken());
	}
    }

    void sync()
    {
	updateHostsAllow();
	updateHostsDeny();
	updateAdminAllow();
	updateAdminDeny();

	MuffinResolver.init(getStringList("muffin.nameservers"));
    }
    
    boolean hostAccess(InetAddress addr)
    {
	String ip = addr.getHostAddress();
	    
	/* First check if the addr is on the allow list */
	Enumeration e = hostsAllow.elements();
 	while (e.hasMoreElements())
 	{
	    String str = (String) e.nextElement();
	    if (str.equals("ALL") || ip.startsWith(str))
	    {
		return true;
	    }
	}

	/* Next check if the addr is on the deny list */
	e = hostsDeny.elements();
 	while (e.hasMoreElements())
 	{
	    String str = (String) e.nextElement();
	    if (str.equals("ALL") || ip.startsWith(str))
	    {
		return false;
	    }
	}

	/* Otherwise allow access */
	return true;
    }

    boolean adminInetAccess(InetAddress addr)
    {
	String ip = addr.getHostAddress();
	    
	/* First check if the addr is on the allow list */
	Enumeration e = adminAllow.elements();
 	while (e.hasMoreElements())
 	{
	    String str = (String) e.nextElement();
	    if (str.equals("ALL") || ip.startsWith(str))
	    {
		return true;
	    }
	}

	/* Next check if the addr is on the deny list */
	e = adminDeny.elements();
 	while (e.hasMoreElements())
 	{
	    String str = (String) e.nextElement();
	    if (str.equals("ALL") || ip.startsWith(str))
	    {
		return false;
	    }
	}

	/* Otherwise allow access */
	return true;
    }

    boolean useHttpProxy()
    {
	return((getString("muffin.httpProxyHost")).length() > 0
		&& getInteger("muffin.httpProxyPort") > 0);
    }

    boolean useHttpsProxy()
    {
	return((getString("muffin.httpsProxyHost")).length() > 0
		&& getInteger("muffin.httpsProxyPort") > 0);
    }
}
