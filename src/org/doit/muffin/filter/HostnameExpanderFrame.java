/* $Id: HostnameExpanderFrame.java,v 1.6 2003/01/08 18:59:52 boyns Exp $ */

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
import org.doit.util.*;

public class HostnameExpanderFrame extends MuffinFrame implements ActionListener, WindowListener
{
    Prefs prefs;
    HostnameExpander parent;
    Checkbox eatReply, eatRequest;
    TextField domain;
    TextField prefix;
    TextField suffix;

    public HostnameExpanderFrame(Prefs prefs, HostnameExpander parent)
    {
	super(Strings.getString("HostnameExpander.title"));

	this.prefs = prefs;
	this.parent = parent;

	Panel panel = new Panel();
	GridBagLayout layout = new GridBagLayout();
	GridBagConstraints c;
        panel.setLayout(layout);

	panel.add(new Label(Strings.getString("HostnameExpander.defaultDomain")+":"));
	domain = new TextField(50);
	domain.setText(prefs.getString("HostnameExpander.defaultDomain"));
	c = new GridBagConstraints();
	c.anchor = GridBagConstraints.WEST;
	c.gridwidth = GridBagConstraints.REMAINDER;
	layout.setConstraints(domain, c);
	panel.add(domain);

	panel.add(new Label(Strings.getString("HostnameExpander.prefix")+":"));
	prefix = new TextField(50);
	prefix.setText(prefs.getString("HostnameExpander.prefix"));
	c = new GridBagConstraints();
	c.anchor = GridBagConstraints.WEST;
	c.gridwidth = GridBagConstraints.REMAINDER;
	layout.setConstraints(prefix, c);
	panel.add(prefix);

	panel.add(new Label(Strings.getString("HostnameExpander.suffix")+":"));
	suffix = new TextField(50);
	suffix.setText(prefs.getString("HostnameExpander.suffix"));
	c = new GridBagConstraints();
	c.anchor = GridBagConstraints.WEST;
	c.gridwidth = GridBagConstraints.REMAINDER;
	layout.setConstraints(suffix, c);
	panel.add(suffix);

	add("North", panel);

        parent.messages.setEditable(false);
        add("Center", parent.messages);

	Button b;
	Panel buttonPanel = new Panel();
	buttonPanel.setLayout(new GridLayout(1, 5));
	b = new Button(Strings.getString("apply"));
	b.setActionCommand("doApply");
	b.addActionListener(this);
	buttonPanel.add(b);
	b = new Button(Strings.getString("save"));
	b.setActionCommand("doSave");
	b.addActionListener(this);
	buttonPanel.add(b);
	b = new Button(Strings.getString("clear"));
	b.setActionCommand("doClear");
	b.addActionListener(this);
	buttonPanel.add(b);
	b = new Button(Strings.getString("close"));
	b.setActionCommand("doClose");
	b.addActionListener(this);
	buttonPanel.add(b);
	b = new Button(Strings.getString("help"));
	b.setActionCommand("doHelp");
	b.addActionListener(this);
	buttonPanel.add(b);

	add("South", buttonPanel);

	addWindowListener(this);

	pack();
	setSize(getPreferredSize());

	show();
    }

    public void actionPerformed(ActionEvent event)
    {
	String arg = event.getActionCommand();

	if ("doClose".equals(arg))
	{
	    setVisible(false);
	}
	else if ("doClear".equals(arg))
	{
	    parent.messages.clear();
	}
	else if ("doApply".equals(arg))
	{
	    prefs.putString("HostnameExpander.defaultDomain", domain.getText().trim());
	    prefs.putString("HostnameExpander.prefix", prefix.getText().trim());
	    prefs.putString("HostnameExpander.suffix", suffix.getText().trim());
	}
	else if ("doSave".equals(arg))
	{
	    parent.save();
	}
	else if ("doHelp".equals(arg))
	{
	    new HelpFrame("HostnameExpander");
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
