/* $Id: SecretaryFrame.java,v 1.7 2006/06/18 23:25:51 forger77 Exp $ */

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

public class SecretaryFrame extends MuffinFrame implements ActionListener, WindowListener
{
    /**
	 * Serializable should define this:
	 */
	private static final long serialVersionUID = 1L;

    Prefs prefs;
    Secretary parent;

    TextField input = null;

    public SecretaryFrame(Prefs prefs, Secretary parent)
    {
	super(Strings.getString("Secretary.title"));

	this.prefs = prefs;
	this.parent = parent;

	Panel panel = new Panel();
	GridBagLayout layout = new GridBagLayout();
        panel.setLayout(layout);

	panel.add(new Label(Strings.getString("Secretary.formfile")+":", Label.RIGHT));

	input = new TextField(50);
	input.setText(prefs.getString("Secretary.formfile"));
	panel.add(input);

	Button browse = new Button(Strings.getString("browse")+"...");
	browse.setActionCommand("doBrowse");
	browse.addActionListener(this);
	panel.add(browse);

	add("Center", panel);

	Panel buttonPanel = new Panel();
	buttonPanel.setLayout(new GridLayout(1, 5));
	Button b;
	b = new Button(Strings.getString("apply"));
	b.setActionCommand("doApply");
	b.addActionListener(this);
	buttonPanel.add(b);
	b = new Button(Strings.getString("save"));
	b.setActionCommand("doSave");
	b.addActionListener(this);
	buttonPanel.add(b);
	b = new Button(Strings.getString("Secretary.reload"));
	b.setActionCommand("doReload");
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

	if ("doApply".equals(arg))
	{
	    prefs.putString("Secretary.formfile", input.getText());
	}
	else if ("doSave".equals(arg))
	{
	    parent.save();
	}
	else if ("doReload".equals(arg))
	{
	    parent.load();
	}
	else if ("doClose".equals(arg))
	{
	    setVisible(false);
	}
	else if ("doBrowse".equals(arg))
	{
	    FileDialog dialog = new FileDialog(this, "Secretary Load");
	    dialog.setVisible(true);
	    if (dialog.getFile() != null)
	    {
		input.setText(dialog.getDirectory() + dialog.getFile());
	    }
	}
	else if ("doHelp".equals(arg))
	{
	    new HelpFrame("Secretary");
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
