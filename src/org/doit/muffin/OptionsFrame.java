/* $Id: OptionsFrame.java,v 1.13 2003/07/31 19:34:46 flefloch Exp $ */

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

import java.awt.*;
import java.awt.event.*;
import org.doit.util.*;

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
    TextField readTimeout;

    OptionsFrame(Options options)//, Configuration configs)
    {
        super(Strings.getString("options.title"));

        this.options = options;


        String vmVendor = System.getProperty("java.vm.vendor");
        if (vmVendor != null && vmVendor.equals("Sun Microsystems Inc."))
        {
            // do not work with IBM JVM 1.3.1 on Redhat 9.0.
            // TO DO: check real vm vendor for IBM
            setResizable(false);
        }

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

        l = new Label(Strings.getString("options.muffin.httpProxyHost")+":", Label.RIGHT);
        c = new GridBagConstraints();
        layout.setConstraints(l, c);
        panel.add(l);

        httpProxyHost = new TextField(20);
        httpProxyHost.setText(options.getString("muffin.httpProxyHost"));
        c = new GridBagConstraints();
        layout.setConstraints(httpProxyHost, c);
        panel.add(httpProxyHost);

        l = new Label(Strings.getString("options.muffin.httpProxyPort")+":", Label.RIGHT);
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

        l = new Label(Strings.getString("options.muffin.httpsProxyHost")+":", Label.RIGHT);
        c = new GridBagConstraints();
        layout.setConstraints(l, c);
        panel.add(l);

        httpsProxyHost = new TextField(20);
        httpsProxyHost.setText(options.getString("muffin.httpsProxyHost"));
        c = new GridBagConstraints();
        layout.setConstraints(httpsProxyHost, c);
        panel.add(httpsProxyHost);

        l = new Label(Strings.getString("options.muffin.httpsProxyPort")+":", Label.RIGHT);
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

        l = new Label(Strings.getString("options.muffin.hostsAllow")+":", Label.RIGHT);
        c = new GridBagConstraints();
        layout.setConstraints(l, c);
        panel.add(l);

        hostsAllowList = new TextField(50);
        hostsAllowList.setText(options.getString("muffin.hostsAllow"));
        c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        layout.setConstraints(hostsAllowList, c);
        panel.add(hostsAllowList);

        l = new Label(Strings.getString("options.muffin.hostsDeny")+":", Label.RIGHT);
        c = new GridBagConstraints();
        layout.setConstraints(l, c);
        panel.add(l);

        hostsDenyList = new TextField(50);
        hostsDenyList.setText(options.getString("muffin.hostsDeny"));
        c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        layout.setConstraints(hostsDenyList, c);
        panel.add(hostsDenyList);

        l = new Label(Strings.getString("options.muffin.adminAllow")+":", Label.RIGHT);
        c = new GridBagConstraints();
        layout.setConstraints(l, c);
        panel.add(l);

        adminAllowList = new TextField(50);
        adminAllowList.setText(options.getString("muffin.adminAllow"));
        c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        layout.setConstraints(adminAllowList, c);
        panel.add(adminAllowList);

        l = new Label(Strings.getString("options.muffin.adminDeny")+":", Label.RIGHT);
        c = new GridBagConstraints();
        layout.setConstraints(l, c);
        panel.add(l);

        adminDenyList = new TextField(50);
        adminDenyList.setText(options.getString("muffin.adminDeny"));
        c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        layout.setConstraints(adminDenyList, c);
        panel.add(adminDenyList);

        l = new Label(Strings.getString("options.muffin.adminUser")+":", Label.RIGHT);
        c = new GridBagConstraints();
        layout.setConstraints(l, c);
        panel.add(l);

        adminUser = new TextField(8);
        adminUser.setText(options.getString("muffin.adminUser"));
        c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        layout.setConstraints(adminUser, c);
        panel.add(adminUser);

        l = new Label(Strings.getString("options.muffin.adminPassword")+":", Label.RIGHT);
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

        l = new Label(Strings.getString("options.muffin.geometry")+":", Label.RIGHT);
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

        l = new Label(Strings.getString("options.muffin.foreground")+":", Label.RIGHT);
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

        l = new Label(Strings.getString("options.muffin.background")+":", Label.RIGHT);
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

        l = new Label(Strings.getString("options.muffin.nameservers")+":", Label.RIGHT);
        c = new GridBagConstraints();
        layout.setConstraints(l, c);
        panel.add(l);

        nameservers = new TextField(50);
        nameservers.setText(options.getString("muffin.nameservers"));
        c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        layout.setConstraints(nameservers, c);
        panel.add(nameservers);

        l = new Label(Strings.getString("options.muffin.readTimeout")+":", Label.RIGHT);
        c = new GridBagConstraints();
        layout.setConstraints(l, c);
        panel.add(l);

        Panel timeoutPanel = new Panel ();
        timeoutPanel.setLayout (new BorderLayout ());

        readTimeout = new TextField(16);
        readTimeout.setText(options.getString("muffin.readTimeout"));
        timeoutPanel.add(readTimeout, BorderLayout.WEST);

        l = new Label(Strings.getString("options.milliseconds"), Label.LEFT);
        timeoutPanel.add(l, BorderLayout.EAST);

        c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = GridBagConstraints.REMAINDER;
        layout.setConstraints(timeoutPanel, c);
        panel.add(timeoutPanel);

        proxyKeepAlive = new Checkbox(Strings.getString("options.muffin.proxyKeepAlive"),
                                      options.getBoolean("muffin.proxyKeepAlive"));
        c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.anchor = GridBagConstraints.WEST;
        layout.setConstraints(proxyKeepAlive, c);
        panel.add(proxyKeepAlive);

        logFilters = new Checkbox(Strings.getString("options.muffin.dontLogFilters"),
                                  options.getBoolean("muffin.dontLogFilters"));
        c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.anchor = GridBagConstraints.WEST;
        layout.setConstraints(logFilters, c);
        panel.add(logFilters);

        add("Center", panel);

        Panel buttonPanel = new Panel();
        buttonPanel.setLayout(new GridLayout(1, 2));
        b = new Button(Strings.getString("apply"));
        b.setActionCommand("doApply");
        b.addActionListener(this);
        buttonPanel.add(b);
        b = new Button(Strings.getString("save"));
        b.setActionCommand("doSave");
        b.addActionListener(this);
        buttonPanel.add(b);
        b = new Button(Strings.getString("close"));
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
        MuffinFrame.getFrame(Strings.getString("muffin.title")).updateGeometry(options.getString("muffin.geometry"));
        options.putBoolean("muffin.proxyKeepAlive", proxyKeepAlive.getState());
        options.putBoolean("muffin.dontLogFilters", logFilters.getState());
        options.putString("muffin.nameservers", nameservers.getText());
        options.putString("muffin.readTimeout", readTimeout.getText());
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
