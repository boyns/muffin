/* $Id: About.java,v 1.9 2006/06/18 23:25:51 forger77 Exp $ */

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
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.StringTokenizer;

import org.doit.util.Strings;

/**
 * Create an about window.
 *
 * @author Mark Boyns
 */
class About extends MuffinFrame implements ActionListener, WindowListener
{
    /**
	 * Seriarlizable class should declare this:
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Create About.
     *
     */
    About(Options options)
    {
	super(Strings.getString("about.muffin"));

	Panel panel = new Panel();
	GridBagLayout layout = new GridBagLayout();
	GridBagConstraints c;
	Label l;
	panel.setLayout(layout);
	panel.setBackground(Color.white);

//  	l = new Label("Muffin");
//  	l.setFont(Main.getOptions().getFont("muffin.bigfont"));
//  	c = new GridBagConstraints();
//  	c.gridwidth = GridBagConstraints.REMAINDER;
//  	layout.setConstraints(l, c);
//  	panel.add(l);

	StringTokenizer st = new StringTokenizer(Main.copyleft(), "\n");
	while (st.hasMoreTokens())
	{
	    l = new Label(st.nextToken());
	    l.setFont(Main.getOptions().getFont("muffin.smallfont"));
	    c = new GridBagConstraints();
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.insets = new Insets(1, 1, 1, 1);
	    layout.setConstraints(l, c);
	    panel.add(l);
	}

	ImageCanvas logo = new ImageCanvas("/images/muffin.jpg", null, false);
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	layout.setConstraints(logo, c);
	panel.add(logo);

	l = new Label("Muffin logo by Tilo Lier");
	l.setFont(Main.getOptions().getFont("muffin.smallfont"));
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	layout.setConstraints(l, c);
	panel.add(l);

// 	l = new Label("More information is available at:");
// 	c = new GridBagConstraints();
// 	c.gridwidth = GridBagConstraints.REMAINDER;
// 	layout.setConstraints(l, c);
// 	panel.add(l);

	l = new Label(Main.getMuffinUrl());
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	layout.setConstraints(l, c);
	panel.add(l);

	add("Center", panel);

	Button b = new Button(Strings.getString("ok"));
	b.addActionListener(this);
	add("South", b);

	addWindowListener(this);

	setSize(getPreferredSize());
	pack();
	show();
    }

    /**
     * Hide the frame on button events.
     *
     * @param event some event
     */
    public void actionPerformed(ActionEvent event)
    {
	setVisible(false);
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
