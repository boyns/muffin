/* $Id: ConnectionsFrame.java,v 1.5 2000/01/24 04:02:13 boyns Exp $ */

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
package org.doit.muffin;

import java.awt.Button;
import java.awt.Color;
import java.awt.Event;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.util.Enumeration;

/**
 * @author Mark Boyns
 */
class ConnectionsFrame extends MuffinFrame implements ActionListener, WindowListener
{
    Monitor monitor;
    TextArea text;

    ConnectionsFrame(Monitor monitor)
    {
	super("Muffin: Connections");

	this.monitor = monitor;

	text = new TextArea();
	text.setEditable(false);
	add("Center", text);

	Button b;
	Panel buttonPanel = new Panel();
	buttonPanel.setLayout(new GridLayout(1, 2));
	b = new Button("Update");
	b.setActionCommand("doUpdate");
	b.addActionListener(this);
	buttonPanel.add(b);
	b = new Button("Close");
	b.setActionCommand("doClose");
	b.addActionListener(this);
	buttonPanel.add(b);
	add("South", buttonPanel);

	addWindowListener(this);

	pack();
	setSize(getPreferredSize());
	show();
	update();
    }
    
    void update()
    {
	text.setText("");
	Enumeration e;
	int count = 0;

	e = monitor.enumerate();
	while (e.hasMoreElements())
	{
	    Object obj = (Object) e.nextElement();
	    text.append(obj.toString() + "\n");
	    count++;
	}
	
	e = Http.enumerate();
	while (e.hasMoreElements())
	{
	    Object obj = (Object) e.nextElement();
	    text.append(obj.toString() + "\n");
	    count++;
	}

	if (count == 0)
	{
	    text.append("No connections\n");
	}
    }

    public void actionPerformed(ActionEvent event)
    {
	String arg = event.getActionCommand();

	if ("doClose".equals(arg))
	{
	    setVisible(false);
	}
	else if ("doUpdate".equals(arg))
	{
	    update();
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
