/* EmptyFontFrame.java */

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

public class EmptyFontFrame extends MuffinFrame implements ActionListener, WindowListener
{
    Prefs prefs;
    EmptyFont parent;
    Checkbox debug;
    
    public EmptyFontFrame (Prefs prefs, EmptyFont parent)
    {
	super ("Muffin: EmptyFont");

	this.prefs = prefs;
	this.parent = parent;

	Panel panel = new Panel ();
	GridBagLayout layout = new GridBagLayout ();
	panel.setLayout (layout);
	GridBagConstraints c;

	debug = new Checkbox ("Debug");
	debug.setState (prefs.getBoolean ("EmptyFont.debug"));
	
	c = new GridBagConstraints ();
	c.gridwidth = GridBagConstraints.REMAINDER;
	layout.setConstraints (debug, c);
	panel.add (debug);
	
	add ("North", panel);
	
	parent.messages.setEditable (false);
	add ("Center", parent.messages);

	Button b;
	Panel buttonPanel = new Panel ();
	buttonPanel.setLayout (new GridLayout (1, 4));
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
	    prefs.putBoolean ("EmptyFont.debug", debug.getState ());
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
