/* $Id: OptionsFrame.java,v 1.9 2000/03/08 15:21:25 boyns Exp $ */

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

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Event;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import org.doit.util.ColorSample;

/**
 * @author Mark Boyns
 */
class OptionsFrame extends MuffinFrame
    implements ActionListener, WindowListener//, ConfigurationListener
{
    Options options;
//     Configuration configs;
    TextField httpProxyHost;
    TextField httpProxyPort;
    TextField httpsProxyHost;
    TextField httpsProxyPort;
    TextField hostsAllowList;
    TextField hostsDenyList;
    TextField adminAllowList;
    TextField adminDenyList;
    TextField adminUser;
    TextField adminPassword;
    TextField fg, bg;
    TextField geometry;
    ColorSample fgSample, bgSample;
    Checkbox proxyKeepAlive;
    Checkbox logFilters;
    TextField nameservers;

    OptionsFrame(Options options)//, Configuration configs)
    {
	super("Muffin: Options");

	this.options = options;

// 	this.configs = configs;

	setResizable(false);
	
	Panel panel = new Panel();
	GridBagLayout layout = new GridBagLayout();
	panel.setLayout(layout);

	Label l;
	TextField t;
	Button b;
	GridBagConstraints c;

// 	Label currentLabel = new Label();
// 	//currentLabel.setFont(new Font("Fixed", Font.PLAIN, 12));
// 	c = new GridBagConstraints();
// 	c.gridwidth = GridBagConstraints.REMAINDER;
// 	layout.setConstraints(currentLabel, c);
// 	panel.add(currentLabel);

	l = new Label("HTTP Proxy:", Label.RIGHT);
	c = new GridBagConstraints();
	layout.setConstraints(l, c);
	panel.add(l);

	httpProxyHost = new TextField(20);
	httpProxyHost.setText(options.getString("muffin.httpProxyHost"));
	c = new GridBagConstraints();
	layout.setConstraints(httpProxyHost, c);
	panel.add(httpProxyHost);

	l = new Label("Port:", Label.RIGHT);
	c = new GridBagConstraints();
	layout.setConstraints(l, c);
	panel.add(l);
	
	httpProxyPort = new TextField(10);
	httpProxyPort.setText(options.getString("muffin.httpProxyPort"));
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	c.anchor = GridBagConstraints.WEST;
	layout.setConstraints(httpProxyPort, c);
	panel.add(httpProxyPort);

	l = new Label("HTTPS Proxy:", Label.RIGHT);
	c = new GridBagConstraints();
	layout.setConstraints(l, c);
	panel.add(l);

	httpsProxyHost = new TextField(20);
	httpsProxyHost.setText(options.getString("muffin.httpsProxyHost"));
	c = new GridBagConstraints();
	layout.setConstraints(httpsProxyHost, c);
	panel.add(httpsProxyHost);

	l = new Label("Port:", Label.RIGHT);
	c = new GridBagConstraints();
	layout.setConstraints(l, c);
	panel.add(l);
	
	httpsProxyPort = new TextField(10);
	httpsProxyPort.setText(options.getString("muffin.httpsProxyPort"));
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	c.anchor = GridBagConstraints.WEST;
	layout.setConstraints(httpsProxyPort, c);
	panel.add(httpsProxyPort);

	l = new Label("HostsAllow:", Label.RIGHT);
	c = new GridBagConstraints();
	layout.setConstraints(l, c);
	panel.add(l);
	
	hostsAllowList = new TextField(50);
	hostsAllowList.setText(options.getString("muffin.hostsAllow"));
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	layout.setConstraints(hostsAllowList, c);
	panel.add(hostsAllowList);

	l = new Label("HostsDeny:", Label.RIGHT);
	c = new GridBagConstraints();
	layout.setConstraints(l, c);
	panel.add(l);
	
	hostsDenyList = new TextField(50);
	hostsDenyList.setText(options.getString("muffin.hostsDeny"));
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	layout.setConstraints(hostsDenyList, c);
	panel.add(hostsDenyList);
	
	l = new Label("AdminAllow:", Label.RIGHT);
	c = new GridBagConstraints();
	layout.setConstraints(l, c);
	panel.add(l);
	
	adminAllowList = new TextField(50);
	adminAllowList.setText(options.getString("muffin.adminAllow"));
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	layout.setConstraints(adminAllowList, c);
	panel.add(adminAllowList);

	l = new Label("AdminDeny:", Label.RIGHT);
	c = new GridBagConstraints();
	layout.setConstraints(l, c);
	panel.add(l);
	
	adminDenyList = new TextField(50);
	adminDenyList.setText(options.getString("muffin.adminDeny"));
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	layout.setConstraints(adminDenyList, c);
	panel.add(adminDenyList);

	l = new Label("AdminUser:", Label.RIGHT);
	c = new GridBagConstraints();
	layout.setConstraints(l, c);
	panel.add(l);
	
	adminUser = new TextField(8);
	adminUser.setText(options.getString("muffin.adminUser"));
	c = new GridBagConstraints();
	c.anchor = GridBagConstraints.WEST;
	layout.setConstraints(adminUser, c);
	panel.add(adminUser);

	l = new Label("AdminPassword:", Label.RIGHT);
	c = new GridBagConstraints();
	layout.setConstraints(l, c);
	panel.add(l);
	
	adminPassword = new TextField(8);
	adminPassword.setText(options.getString("muffin.adminPassword"));
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	c.anchor = GridBagConstraints.WEST;
	layout.setConstraints(adminPassword, c);
	panel.add(adminPassword);

	l = new Label("Geometry:", Label.RIGHT);
	c = new GridBagConstraints();
	layout.setConstraints(l, c);
	panel.add(l);
	
	geometry = new TextField(16);
	geometry.setText(options.getString("muffin.geometry"));
                          //MuffinFrame.getFrame("Muffin").getGeometry());
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	c.anchor = GridBagConstraints.WEST;
	layout.setConstraints(geometry, c);
	panel.add(geometry);

	Panel colorPanel = new Panel();
	
	l = new Label("Foreground:", Label.RIGHT);
	c = new GridBagConstraints();
	layout.setConstraints(l, c);
	colorPanel.add(l);

	fg = new TextField(7);
	fg.setText(options.getString("muffin.fg"));
	fg.addActionListener(this);
	c = new GridBagConstraints();
	c.anchor = GridBagConstraints.WEST;
	layout.setConstraints(fg, c);
	colorPanel.add(fg);

	fgSample = new ColorSample(options.getString("muffin.fg"));
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	c.anchor = GridBagConstraints.WEST;
	layout.setConstraints(fgSample, c);
	colorPanel.add(fgSample);

	l = new Label("Background:", Label.RIGHT);
	c = new GridBagConstraints();
	layout.setConstraints(l, c);
	colorPanel.add(l);

	bg = new TextField(7);
	bg.setText(options.getString("muffin.bg"));
	bg.addActionListener(this);
	c = new GridBagConstraints();
	c.anchor = GridBagConstraints.WEST;
	layout.setConstraints(bg, c);
	colorPanel.add(bg);

	bgSample = new ColorSample(options.getString("muffin.bg"));
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	c.anchor = GridBagConstraints.WEST;
	layout.setConstraints(bgSample, c);
	colorPanel.add(bgSample);

	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	c.anchor = GridBagConstraints.WEST;
	layout.setConstraints(colorPanel, c);
	panel.add(colorPanel);

	l = new Label("Nameservers:", Label.RIGHT);
	c = new GridBagConstraints();
	layout.setConstraints(l, c);
	panel.add(l);
	
	nameservers = new TextField(50);
	nameservers.setText(options.getString("muffin.nameservers"));
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	layout.setConstraints(nameservers, c);
	panel.add(nameservers);

	proxyKeepAlive = new Checkbox("Enable Proxy Keep-Alive",
				      options.getBoolean("muffin.proxyKeepAlive"));
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	c.anchor = GridBagConstraints.WEST;
	layout.setConstraints(proxyKeepAlive, c);
	panel.add(proxyKeepAlive);

	logFilters = new Checkbox("Enable Filter Logging",
				  !options.getBoolean("muffin.dontLogFilters"));
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	c.anchor = GridBagConstraints.WEST;
	layout.setConstraints(logFilters, c);
	panel.add(logFilters);

	add("Center", panel);

	Panel buttonPanel = new Panel();
	buttonPanel.setLayout(new GridLayout(1, 2));
	b = new Button("Apply");
	b.setActionCommand("doApply");
	b.addActionListener(this);
	buttonPanel.add(b);
	b = new Button("Save");
	b.setActionCommand("doSave");
	b.addActionListener(this);
	buttonPanel.add(b);
	b = new Button("Close");
	b.setActionCommand("doClose");
	b.addActionListener(this);
	buttonPanel.add(b);
	add("South", buttonPanel);

	addWindowListener(this);
// 	configs.addConfigurationListener(currentLabel);
// 	configs.addConfigurationListener(this);

	pack();
	setSize(getPreferredSize());
    }
    
    void hideshow()
    {
	if (isShowing())
	{
	    setVisible(false);
	}
	else
	{
	    show();
	}
    }

//     public void configurationChanged(String name)
//     {
// 	httpProxyHost.setText(options.getString("muffin.httpProxyHost"));
// 	httpProxyPort.setText(options.getString("muffin.httpProxyPort"));
// 	httpsProxyHost.setText(options.getString("muffin.httpsProxyHost"));
// 	httpsProxyPort.setText(options.getString("muffin.httpsProxyPort"));
// 	hostsAllowList.setText(options.getString("muffin.hostsAllow"));
// 	hostsDenyList.setText(options.getString("muffin.hostsDeny"));
// 	adminAllowList.setText(options.getString("muffin.adminAllow"));
// 	adminDenyList.setText(options.getString("muffin.adminDeny"));
// 	adminUser.setText(options.getString("muffin.adminUser"));
// 	adminPassword.setText(options.getString("muffin.adminPassword"));
// 	bg.setText(options.getString("muffin.bg"));
// 	bgSample.setColor(bg.getText());
// 	fg.setText(options.getString("muffin.fg"));
// 	fgSample.setColor(fg.getText());
// 	proxyKeepAlive.setState(options.getBoolean("muffin.proxyKeepAlive"));
// 	nameservers.setText(options.getString("muffin.nameservers"));

// 	MuffinFrame.repaintFrames();
//     }

    void sync()
    {
	options.putString("muffin.httpProxyHost", httpProxyHost.getText());
	options.putString("muffin.httpProxyPort", httpProxyPort.getText());
	options.putString("muffin.httpsProxyHost", httpsProxyHost.getText());
	options.putString("muffin.httpsProxyPort", httpsProxyPort.getText());
	options.putString("muffin.hostsAllow", hostsAllowList.getText());
	options.putString("muffin.hostsDeny", hostsDenyList.getText());
	options.putString("muffin.adminAllow", adminAllowList.getText());
	options.putString("muffin.adminDeny", adminDenyList.getText());
	options.putString("muffin.adminUser", adminUser.getText());
	options.putString("muffin.adminPassword", adminPassword.getText());
	options.putString("muffin.geometry", geometry.getText());
	options.putString("muffin.fg", fg.getText());
	options.putString("muffin.bg", bg.getText());
	fgSample.setColor(fg.getText());
	bgSample.setColor(bg.getText());
	MuffinFrame.getFrame("Muffin").updateGeometry(options.getString("muffin.geometry"));
	options.putBoolean("muffin.proxyKeepAlive", proxyKeepAlive.getState());
	options.putBoolean("muffin.dontLogFilters", !logFilters.getState());
	options.putString("muffin.nameservers", nameservers.getText());
	options.sync();
    }

    public void actionPerformed(ActionEvent event)
    {
	String arg = event.getActionCommand();

	if ("doApply".equals(arg))
	{
	    sync();
	    MuffinFrame.repaintFrames();
	}
	else if ("doSave".equals(arg))
	{
	    sync();
	    options.save();
	}
	else if ("doClose".equals(arg))
	{
	    setVisible(false);
	}
    }

    public void windowActivated(WindowEvent e)
    {
    }
  
    public void windowDeactivated(WindowEvent e)
    {
    }
  
    public void windowClosing(WindowEvent e)
    {
	setVisible(false);
    }
  
    public void windowClosed(WindowEvent e)
    {
    }
  
    public void windowIconified(WindowEvent e)
    {
    }
  
    public void windowDeiconified(WindowEvent e)
    {
    }
  
    public void windowOpened(WindowEvent e)
    {
    }
}
