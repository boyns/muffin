/* $Id: AnimationKillerFrame.java,v 1.2 1998/08/13 06:02:01 boyns Exp $ */

/*
 * Copyright (C) 1996-98 Mark R. Boyns <boyns@doit.org>
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

public class AnimationKillerFrame extends MuffinFrame implements ActionListener, WindowListener
{
    Prefs prefs;
    AnimationKiller parent;
    Checkbox breakem;
    TextField maxLoops;
    
    public AnimationKillerFrame (Prefs prefs, AnimationKiller parent)
    {
	super ("Muffin: Animation Killer");

	this.prefs = prefs;
	this.parent = parent;

	Panel panel = new Panel ();
	GridBagLayout layout = new GridBagLayout ();
	panel.setLayout (layout);
	GridBagConstraints c;

	breakem = new Checkbox ("Break Animations");
	breakem.setState (prefs.getBoolean ("AnimationKiller.break"));
	c = new GridBagConstraints ();
	c.anchor = GridBagConstraints.WEST;
	layout.setConstraints (breakem, c);
	panel.add (breakem);
	
	Label label = new Label ("Max Loops:", Label.RIGHT);
	c = new GridBagConstraints ();
	c.anchor = GridBagConstraints.WEST;
	layout.setConstraints (label, c);
	panel.add (label);
	
	maxLoops = new TextField (2);
	maxLoops.setText (prefs.getString ("AnimationKiller.maxLoops"));
	c = new GridBagConstraints ();
	c.gridwidth = GridBagConstraints.REMAINDER;
	c.anchor = GridBagConstraints.WEST;
	layout.setConstraints (maxLoops, c);
	panel.add (maxLoops);
	
	add ("North", panel);
	
	parent.messages.setEditable (false);
	add ("Center", parent.messages);

	Button b;
	Panel buttonPanel = new Panel ();
	buttonPanel.setLayout (new GridLayout (1, 5));
	b = new Button ("Apply");
	b.setActionCommand ("doApply");
	b.addActionListener (this);
	buttonPanel.add (b);
	b = new Button ("Save");
	b.setActionCommand ("doSave");
	b.addActionListener (this);
	buttonPanel.add (b);
	b = new Button ("Clear");
	b.setActionCommand ("doClear");
	b.addActionListener (this);
	buttonPanel.add (b);
	b = new Button ("Close");
	b.setActionCommand ("doClose");
	b.addActionListener (this);
	buttonPanel.add (b);
	b = new Button ("Help");
	b.setActionCommand ("doHelp");
	b.addActionListener (this);
	buttonPanel.add (b);

	add ("South", buttonPanel);

	addWindowListener (this);
	
	pack ();
	setSize (getPreferredSize ());

	show ();
    }

    public void actionPerformed (ActionEvent event)
    {
	String arg = event.getActionCommand ();
	
	if ("doApply".equals (arg))
	{
	    prefs.putString ("AnimationKiller.maxLoops", maxLoops.getText ());
	    prefs.putBoolean ("AnimationKiller.break", breakem.getState ());
	}
	else if ("doSave".equals (arg))
	{
	    parent.save ();
	}
	else if ("doClose".equals (arg))
	{
	    setVisible (false);
	}
	else if ("doClear".equals (arg))
	{
	    parent.messages.clear ();
	}
	else if ("doHelp".equals (arg))
	{
	    new HelpFrame ("AnimationKiller");
	}
    }

    public void windowActivated (WindowEvent e)
    {
    }
  
    public void windowDeactivated (WindowEvent e)
    {
    }
  
    public void windowClosing (WindowEvent e)
    {
	setVisible (false);
    }
  
    public void windowClosed (WindowEvent e)
    {
    }
  
    public void windowIconified (WindowEvent e)
    {
    }
  
    public void windowDeiconified (WindowEvent e)
    {
    }
  
    public void windowOpened (WindowEvent e)
    {
    }
}
