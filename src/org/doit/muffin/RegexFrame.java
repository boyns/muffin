/* $Id: RegexFrame.java,v 1.7 2003/01/08 18:59:52 boyns Exp $ */

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

import org.doit.util.*;
import java.awt.Button;
import java.awt.Color;
import java.awt.Event;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import gnu.regexp.*;

/**
 * @author Mark Boyns
 */
class RegexFrame extends MuffinFrame implements ActionListener, WindowListener
{
    TextArea text;
    TextField pattern;
    REMatch match = null;

    RegexFrame()
    {
	super(Strings.getString("regex.title"));

	Button b;
	Panel panel = new Panel();
	GridBagLayout layout = new GridBagLayout();
        panel.setLayout(layout);
	GridBagConstraints c;

	Label l = new Label(Strings.getString("regex.label")+":", Label.RIGHT);
	panel.add(l);

	pattern = new TextField(32);
	panel.add(pattern);

	b = new Button(Strings.getString("regex.match"));
	b.setActionCommand("doMatch");
	b.addActionListener(this);
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	layout.setConstraints(b, c);
	panel.add(b);

	add("North", panel);

	text = new TextArea();
	text.setEditable(true);
	//text.setFont(new Font("Fixed", Font.PLAIN, 12));
	text.setText(Strings.getString("regex.text"));
	add("Center", text);

	Panel buttonPanel = new Panel();
	buttonPanel.setLayout(new GridLayout(1, 2));
	b = new Button(Strings.getString("clear"));
	b.setActionCommand("doClear");
	b.addActionListener(this);
	buttonPanel.add(b);
	b = new Button(Strings.getString("close"));
	b.setActionCommand("doClose");
	b.addActionListener(this);
	buttonPanel.add(b);
	add("South", buttonPanel);

	addWindowListener(this);

	setSize(getPreferredSize());
	pack();
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
	    text.setText("");
	}
	else if ("doMatch".equals(arg))
	{
	    try
	    {
		RE re = new RE(pattern.getText());

		if (match != null)
		{
		    match = re.getMatch(text.getText(), match.getEndIndex(), text.getText().length());

		}
		else
		{
		    match = re.getMatch(text.getText());
		}

		if (match != null)
		{
		    text.select(match.getStartIndex(), match.getEndIndex());
		}
	    }
	    catch (REException e)
	    {
		System.out.println(e);
	    }
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
