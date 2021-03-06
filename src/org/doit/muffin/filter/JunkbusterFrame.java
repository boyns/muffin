/* $Id: JunkbusterFrame.java,v 1.4 2006/10/28 19:12:40 forger77 Exp $ */

/*
 * Copyright (C) 1996-2000 Mark R. Boyns <boyns@doit.org>
 * Copyright (C) 2001 Doug Porter
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
 *
 * Based on code by Mark R. Boyns.
 */
package org.doit.muffin.filter;

import java.awt.*;
import java.awt.event.*;
import org.doit.muffin.*;
import org.doit.util.*;

public class JunkbusterFrame extends MuffinFrame implements ActionListener, WindowListener
{
    /**
	 * Serializable should define this:
	 */
	private static final long serialVersionUID = 1L;

    Prefs prefs;
    Junkbuster parent;
    TextField blockfile = null;
    final int TextFieldSize = 50;

    public JunkbusterFrame(Prefs prefs, Junkbuster parent)
    {
	super(Strings.getString("Junkbuster.title"));

	this.prefs = prefs;
	this.parent = parent;

	Panel panel = new Panel();
	GridBagLayout layout = new GridBagLayout();
        panel.setLayout(layout);
	GridBagConstraints c;

	panel.add(new Label(Strings.getString("Junkbuster.blockfileLocation")+":", Label.RIGHT));

	blockfile = new TextField (TextFieldSize);
	blockfile.setText (prefs.getString(Junkbuster.BlockfileLocation));
	panel.add(blockfile);

	Button browse = new Button(Strings.getString("browse")+"...");
	browse.setActionCommand("doBrowse");
	browse.addActionListener(this);
	c = new GridBagConstraints();
	c.anchor = GridBagConstraints.NORTHWEST;
	layout.setConstraints(browse, c);
	panel.add(browse);

	add(BorderLayout.NORTH, panel);

	panel = new Panel();
	layout = new GridBagLayout();
        panel.setLayout(layout);

	Label l = new Label(Strings.getString("Junkbuster.messages"));
	c = new GridBagConstraints();
	c.insets = new Insets(0, 10, 5, 10);
	c.gridwidth = GridBagConstraints.REMAINDER;
	c.anchor = GridBagConstraints.CENTER;
	layout.setConstraints(l, c);
	panel.add(l);

	c = new GridBagConstraints();
	c.insets = new Insets(0, 10, 5, 10);
	c.gridwidth = GridBagConstraints.REMAINDER;
	c.anchor = GridBagConstraints.CENTER;
	layout.setConstraints(parent.messages, c);
	parent.messages.setEditable(false);
	panel.add(parent.messages);

	add(BorderLayout.CENTER, panel);

	Panel buttonPanel = new Panel();
	buttonPanel.setLayout(new GridLayout(1, 4));
	Button b;
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
	b = new Button(Strings.getString("help"));
	b.setActionCommand("doHelp");
	b.addActionListener(this);
	buttonPanel.add(b);

        add(BorderLayout.SOUTH, buttonPanel);

	addWindowListener(this);

	pack();
	setSize(getPreferredSize());
	show();
    }

    public void actionPerformed(ActionEvent event)
    {
	String arg = event.getActionCommand();

	if ("doBrowse".equals(arg))
	{
	    FileDialog dialog = new FileDialog(this, Strings.getString("load"));
	    dialog.setVisible(true);
	    if (dialog.getFile() != null)
	    {
		blockfile.setText(dialog.getDirectory() + dialog.getFile());
	    }
	}
        else if ("doApply".equals(arg))
	{
	    prefs.putString(Junkbuster.BlockfileLocation, blockfile.getText());
	}
	else if ("doSave".equals(arg))
	{
	    prefs.putString(Junkbuster.BlockfileLocation, blockfile.getText());
	    parent.save();
	}
	else if ("doClose".equals(arg))
	{
	    setVisible(false);
	}
	else if ("doHelp".equals(arg))
	{
	    new HelpFrame("Junkbuster");
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
