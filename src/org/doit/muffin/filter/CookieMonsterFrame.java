/* $Id: CookieMonsterFrame.java,v 1.5 2000/01/24 04:02:19 boyns Exp $ */

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

public class CookieMonsterFrame extends MuffinFrame implements ActionListener, WindowListener
{
    Prefs prefs;
    CookieMonster parent;
    Checkbox eatReply, eatRequest;
    
    public CookieMonsterFrame(Prefs prefs, CookieMonster parent)
    {
	super("Muffin: Cookie Monster");

	this.prefs = prefs;
	this.parent = parent;

	Panel panel;
	GridBagLayout layout;
	GridBagConstraints c;

	panel = new Panel();
	layout = new GridBagLayout();
	panel.setLayout(layout);
	
	eatRequest = new Checkbox("Eat Request Cookies");
	eatRequest.setState(prefs.getBoolean("CookieMonster.eatRequestCookies"));
	eatReply = new Checkbox("Eat Reply Cookies");
	eatReply.setState(prefs.getBoolean("CookieMonster.eatReplyCookies"));
	
	c = new GridBagConstraints();
	layout.setConstraints(eatRequest, c);
	panel.add(eatRequest);
	
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	layout.setConstraints(eatReply, c);
	panel.add(eatReply);

	add("North", panel);
	
	parent.messages.setEditable(false);
	//parent.messages.setFont(new Font("Fixed", Font.PLAIN, 10));
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
	    prefs.putBoolean("CookieMonster.eatReplyCookies", eatReply.getState());
	    prefs.putBoolean("CookieMonster.eatRequestCookies", eatRequest.getState());
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
	    new HelpFrame("CookieMonster");
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
