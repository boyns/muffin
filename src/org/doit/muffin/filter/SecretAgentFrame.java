/* $Id: SecretAgentFrame.java,v 1.5 2000/01/24 04:02:21 boyns Exp $ */

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
package org.doit.muffin.filter;

import java.awt.*;
import java.awt.event.*;
import org.doit.muffin.*;

public class SecretAgentFrame extends MuffinFrame implements ActionListener, ItemListener, WindowListener
{
    Prefs prefs;
    SecretAgent parent;
    
    TextField input = null;
    
    public SecretAgentFrame(Prefs prefs, SecretAgent parent)
    {
	super("Muffin: Secret Agent");

	this.prefs = prefs;
	this.parent = parent;

	String sampleAgents[] =
	{
	    "Secret/1.0",
	    "Muffin(muffin.doit.org)",
	    "NONE",
	    "Uhm... no. (WWW compatible)",
	    "aolbrowser 1.1 Macintosh; 68K",
	    "ArchitextSpider",
	    "Java1.0.2",
	    "Lynx 2.5  libwww-FM/2.14",
	    "Lynx/2.6  libwww-FM/2.14",
	    "MacWeb/1.00ALPHA3.2  libwww/2.17",
	    "Microsoft Internet Explorer/4.40.425(Windows 95)",
	    "MOMspider/1.00 libwww-perl/0.40",
	    "Mozilla/0.9 Beta(Windows)",
	    "Mozilla/1.0(Windows)",
	    "Mozilla/1.0N(Macintosh)",
	    "Mozilla/1.1(Windows; I; 16bit)",
	    "Mozilla/1.1N(Macintosh; I; PPC)",
	    "Mozilla/1.2(Windows; U; 16bit)",
	    "Mozilla/1.22(compatible; MSIE 1.5; Windows 95)",
	    "Mozilla/1.22(compatible; MSIE 2.0; Windows 95)",
	    "Mozilla/2.0(X11; U; SunOS 5.4 sun4m)",
	    "Mozilla/2.0(compatible; MSIE 2.0; Mac_PowerPC)",
	    "Mozilla/2.01(WinNT; U)",
	    "Mozilla/2.01Gold(Win95; I)",
	    "Mozilla/2.02(Macintosh; I; PPC)",
	    "Mozilla/3.0(Win95; I)",
	    "Mozilla/3.0Gold(X11; U; Linux 2.0.22 i586)",
	    "Mozilla/4.01 [de] (WinNT; I)",
	    "Mozilla/4.02 [en] (X11; U; SunOS 5.5.1 sun4u)",
	    "Mozilla/4.0(compatible; MSIE 4.0b1; SunOS 5.5 sun4u; X11)",
	    "NCSA Mosaic for the X Window System/2.0",
	    "NCSA Mosaic/2.0(Windows x86)",
	    "NCSA_Mosaic/2.6-1(X11;OpenVMS Unknown VAX_ALPHA)",
	    "NCSA_Mosaic/2.7b5(X11;SunOS 5.5 sun4m)",
	    "PRODIGY-WB 3.1d",
	};
	
	Panel panel = new Panel();
	GridBagLayout layout = new GridBagLayout();
	GridBagConstraints c;
        panel.setLayout(layout);

	panel.add(new Label("User-Agent:", Label.RIGHT));

	input = new TextField(32);
	input.setText(prefs.getString("SecretAgent.userAgent"));
	c = new GridBagConstraints();
	c.anchor = GridBagConstraints.WEST;
	c.gridwidth = GridBagConstraints.REMAINDER;
	layout.setConstraints(input, c);
	panel.add(input);

	panel.add(new Label("Sample Agents:", Label.RIGHT));

	Choice choice = new Choice();
	choice.addItemListener(this);
	for (int i = 0; i < sampleAgents.length; i++)
	{
	    choice.addItem(sampleAgents[i]);
	}
	
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	layout.setConstraints(choice, c);
	panel.add(choice);
	
	add("Center", panel);

	Panel buttonPanel = new Panel();
	buttonPanel.setLayout(new GridLayout(1, 4));
	Button b;
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
	b = new Button("Help");
	b.setActionCommand("doHelp");
	b.addActionListener(this);
	buttonPanel.add(b);
	add("South", buttonPanel);

	addWindowListener(this);
	
	pack();
	setSize(getPreferredSize());

	show();
    }

    public void itemStateChanged(ItemEvent event)
    {
	input.setText(event.getItem().toString());
    }

    public void actionPerformed(ActionEvent event)
    {
	String arg = event.getActionCommand();
	
	if ("doApply".equals(arg))
	{
	    prefs.putString("SecretAgent.userAgent", input.getText());
	}
	else if ("doSave".equals(arg))
	{
	    parent.save();
	}
	else if ("doClose".equals(arg))
	{
	    setVisible(false);
	}
	else if ("doHelp".equals(arg))
	{
	    new HelpFrame("SecretAgent");
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
