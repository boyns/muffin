/* $Id: RefererFrame.java,v 1.5 2000/01/24 04:02:20 boyns Exp $ */

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

public class RefererFrame extends MuffinFrame implements ActionListener, ItemListener, WindowListener
{
    Prefs prefs;
    Referer parent;
    
    TextField input = null;
    Checkbox samedomain = null;
    
    public RefererFrame(Prefs prefs, Referer parent)
    {
	super("Muffin: Referer");

	this.prefs = prefs;
	this.parent = parent;

	String sampleReferers[] =
	{
            "NONE",
	    "http://www.nowhere.com/",
            "http://www.webpagesthatsuck.com/",
            "CENSORED",
            "Uhm... no",
	};
	
	Panel panel = new Panel();
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c;
        panel.setLayout(layout);

        panel.add(new Label("Referer:", Label.RIGHT));

        input = new TextField(32);
	input.setText(prefs.getString("Referer.referer"));
        c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = GridBagConstraints.REMAINDER;
        layout.setConstraints(input, c);
        panel.add(input);

        panel.add(new Label("Sample Referers:", Label.RIGHT));

        Choice choice = new Choice();
	choice.addItemListener(this);
        for (int i = 0; i < sampleReferers.length; i++)
        {
            choice.addItem(sampleReferers[i]);
        }
        
        c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        layout.setConstraints(choice, c);
        panel.add(choice);

	samedomain = new Checkbox("Allow Same Domain");
	samedomain.setState(prefs.getBoolean("Referer.allowSameDomain"));
	c = new GridBagConstraints();
	c.anchor = GridBagConstraints.WEST;
	layout.setConstraints(samedomain, c);
	panel.add(samedomain);

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

    public void itemStateChanged(ItemEvent event)
    {
	input.setText(event.getItem().toString());
    }

    public void actionPerformed(ActionEvent event)
    {
	String arg = event.getActionCommand();
	
	if ("doApply".equals(arg))
	{
	    prefs.putString("Referer.referer", input.getText());
	    prefs.putBoolean("Referer.allowSameDomain", samedomain.getState());
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
	    new HelpFrame("Referer");
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
