/* $Id: NoCodeFrame.java,v 1.1 1999/03/12 15:47:44 boyns Exp $ */

/* Based upon DecafFrame by Mark R. Boyns so here is his copyright notice: */

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

/* Modifications by Neil Hodgson <neilh@hare.net.au> 5/December/1998
 * The modifications are also licensed under the terms of the GNU General
 * Public License as described above.
 */

package org.doit.muffin.filter;

import java.awt.*;
import java.awt.event.*;
import org.doit.muffin.*;

public class NoCodeFrame extends MuffinFrame implements ActionListener, WindowListener
{
    Prefs prefs;
    NoCode parent;
    Checkbox noJava, noJavaScript, noVBScript, noOtherScript;
    Checkbox noEncodedScript, noEvalInScript;
    
    public NoCodeFrame(Prefs prefs, NoCode parent)
    {
	super("Muffin: NoCode");

	this.prefs = prefs;
	this.parent = parent;

	Panel panel = new Panel();
	GridBagLayout layout = new GridBagLayout();
	panel.setLayout(layout);
	GridBagConstraints c;

	noJavaScript = new Checkbox("No JavaScript");
	noJavaScript.setState(prefs.getBoolean("NoCode.noJavaScript"));

	noVBScript = new Checkbox("No VBScript");
	noVBScript.setState(prefs.getBoolean("NoCode.noVBScript"));

	noOtherScript = new Checkbox("No Other Script Language");
	noOtherScript.setState(prefs.getBoolean("NoCode.noOtherScript"));

	noEncodedScript = new Checkbox("No Encoded Script");
	noEncodedScript.setState(prefs.getBoolean("NoCode.noEncodedScript"));

	noEvalInScript = new Checkbox("No Eval");
	noEvalInScript.setState(prefs.getBoolean("NoCode.noEvalInScript"));

	noJava = new Checkbox("No Java");
	noJava.setState(prefs.getBoolean("NoCode.noJava"));
	
	c = new GridBagConstraints();
	layout.setConstraints(noJavaScript, c);
	panel.add(noJavaScript);
	layout.setConstraints(noVBScript, c);
	panel.add(noVBScript);

	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	layout.setConstraints(noOtherScript, c);
	panel.add(noOtherScript);

	c = new GridBagConstraints();
	layout.setConstraints(noEncodedScript, c);
	panel.add(noEncodedScript);
	layout.setConstraints(noEvalInScript, c);
	panel.add(noEvalInScript);

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
	    prefs.putBoolean("NoCode.noJavaScript", noJavaScript.getState());
	    prefs.putBoolean("NoCode.noVBScript", noVBScript.getState());
	    prefs.putBoolean("NoCode.noOtherScript", noOtherScript.getState());
	    prefs.putBoolean("NoCode.noEncodedScript", noEncodedScript.getState());
	    prefs.putBoolean("NoCode.noEvalInScript", noEvalInScript.getState());
	    prefs.putBoolean("NoCode.noJava", noJava.getState());
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
	    new HelpFrame("NoCode");
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
