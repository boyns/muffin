/* $Id: PreviewFrame.java,v 1.4 1999/03/12 15:47:45 boyns Exp $ */

/*
 * Copyright (C) 1996-99 Mark R. Boyns <boyns@doit.org>
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

public class PreviewFrame extends MuffinFrame implements ActionListener, WindowListener
{
    Prefs prefs;
    Preview parent;
    TextField input = null;
    
    public PreviewFrame(Prefs prefs, Preview parent)
    {
	super("Muffin: Preview");

	this.prefs = prefs;
	this.parent = parent;

	Panel panel = new Panel();
	GridBagLayout layout = new GridBagLayout();
        panel.setLayout(layout);
	GridBagConstraints c;
	     
	panel.add(new Label("Content types to preview:", Label.RIGHT));

	input = new TextField(50);
	input.setText(prefs.getString("Preview.contentTypes"));
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	layout.setConstraints(input, c);
	panel.add(input);

	Label label = new Label("Example: text/html,image/gif");
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	layout.setConstraints(label, c);
	panel.add(label);

	add("Center", panel);

	Panel buttonPanel = new Panel();
	buttonPanel.setLayout(new GridLayout(1, 4));
	Button b;
	b = new Button("Apply");
	b.setActionCommand("doApply");
	b.addActionListener(this);
	buttonPanel.add(b);
	b = new Button("Save");
	b.setActionCommand("doSave");
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
	    prefs.putString("Preview.contentTypes", input.getText());
	}
	else if ("doSave".equals(arg))
	{
	    parent.save();
	}
	else if ("doClose".equals(arg))
	{
	    setVisible(false);
	}
	else if ("doHelp".equals(arg))
	{
	    new HelpFrame("Preview");
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
