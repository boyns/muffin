/* $Id: DecafFrame.java,v 1.5 2000/01/24 04:02:19 boyns Exp $ */

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

public class DecafFrame extends MuffinFrame implements ActionListener, WindowListener
{
    Prefs prefs;
    Decaf parent;
    Checkbox noJava, noJavaScript;
    
    public DecafFrame(Prefs prefs, Decaf parent)
    {
	super("Muffin: Decaf");

	this.prefs = prefs;
	this.parent = parent;

	Panel panel = new Panel();
	GridBagLayout layout = new GridBagLayout();
	panel.setLayout(layout);
	GridBagConstraints c;

	noJavaScript = new Checkbox("No JavaScript");
	noJavaScript.setState(prefs.getBoolean("Decaf.noJavaScript"));
	noJava = new Checkbox("No Java");
	noJava.setState(prefs.getBoolean("Decaf.noJava"));
	
	c = new GridBagConstraints();
	layout.setConstraints(noJavaScript, c);
	panel.add(noJavaScript);
	
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	layout.setConstraints(noJava, c);
	panel.add(noJava);
	
	add("North", panel);
	
	parent.messages.setEditable(false);
	add("Center", parent.messages);

	Button b;
	Panel buttonPanel = new Panel();
	buttonPanel.setLayout(new GridLayout(1, 5));
	b = new Button("Apply");
	b.setActionCommand("doApply");
	b.addActionListener(this);
	buttonPanel.add(b);
	b = new Button("Save");
	b.setActionCommand("doSave");
	b.addActionListener(this);
	buttonPanel.add(b);
	b = new Button("Clear");
	b.setActionCommand("doClear");
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

    public void actionPerformed(ActionEvent event)
    {
	String arg = event.getActionCommand();
	
	if ("doApply".equals(arg))
	{
	    prefs.putBoolean("Decaf.noJavaScript", noJavaScript.getState());
	    prefs.putBoolean("Decaf.noJava", noJava.getState());
	}
	else if ("doSave".equals(arg))
	{
	    parent.save();
	}
	else if ("doClose".equals(arg))
	{
	    setVisible(false);
	}
	else if ("doClear".equals(arg))
	{
	    parent.messages.clear();
	}
	else if ("doHelp".equals(arg))
	{
	    new HelpFrame("Decaf");
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
