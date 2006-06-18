/*
 * Copyright 2002 Doug Porter
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

public class PlainHtmlFrame
extends MuffinFrame
implements ActionListener, WindowListener
{
    /**
	 * Serializable should define this:
	 */
	private static final long serialVersionUID = 1L;

    Prefs prefs;
    PlainHtml parent;
    Checkbox noJava, noJavaScript, noVBScript, noOtherScript;
    Checkbox noEncodedScript, noEvalInScript;

    public PlainHtmlFrame (Prefs prefs, PlainHtml parent)
    {
	super ("Muffin: PlainHtml");

	this.prefs = prefs;
	this.parent = parent;

	Panel panel = new Panel ();
	GridBagLayout layout = new GridBagLayout ();
	panel.setLayout (layout);
	add ("North", panel);

	parent.messages.setEditable (false);
	add ("Center", parent.messages);

	Button b;
	Panel buttonPanel = new Panel ();
	buttonPanel.setLayout (new GridLayout (1, 5));
	b = new Button (Strings.getString("apply"));
	b.setActionCommand ("doApply");
	b.addActionListener (this);
	buttonPanel.add (b);
	b = new Button (Strings.getString("save"));
	b.setActionCommand ("doSave");
	b.addActionListener (this);
	buttonPanel.add (b);
	b = new Button (Strings.getString("clear"));
	b.setActionCommand ("doClear");
	b.addActionListener (this);
	buttonPanel.add (b);
	b = new Button (Strings.getString("close"));
	b.setActionCommand ("doClose");
	b.addActionListener (this);
	buttonPanel.add (b);
	b = new Button (Strings.getString("help"));
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
            // save any prefs here
	    // prefs.putBoolean ("PlainHtml.noJavaScript", noJavaScript.getState ());
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
	    new HelpFrame ("PlainHtml");
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
