/* $Id: StatsFrame.java,v 1.5 2000/01/24 04:02:21 boyns Exp $ */

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
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.StringTokenizer;
import org.doit.muffin.*;

public class StatsFrame extends MuffinFrame implements ActionListener, WindowListener
{
    Prefs prefs;
    Stats parent;
    TextArea text;

    public StatsFrame(Prefs prefs, Stats parent)
    {
	super("Muffin: Stats");

	this.prefs = prefs;
	this.parent = parent;

	text = new TextArea();
        text.setEditable(false);
        add("Center", text);

	Button b;
	Panel buttonPanel = new Panel();
	buttonPanel.setLayout(new GridLayout(1, 4));
	b = new Button("Update");
	b.setActionCommand("doUpdate");
	b.addActionListener(this);
	buttonPanel.add(b);
	b = new Button("Reset");
	b.setActionCommand("doReset");
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

    void reset()
    {
	text.setText("");
	parent.reset();
    }

    void print(Hashtable h)
    {
        String key;
        Integer count;
        int total = 0;

        for (Enumeration e = h.keys(); e.hasMoreElements(); )
        {
            key = (String) e.nextElement();
            count = (Integer) h.get(key);
            text.append("    " + key + ": " + count + "\n");
            total += count.intValue();
        }
        text.append("    TOTAL: " + total + "\n");
    }

    void printUniqueServers()
    {
	String s ;
	Integer i, y;
	Hashtable servers = new Hashtable(100);
	for (Enumeration e = parent.servers.keys(); e.hasMoreElements();)
	{
	    s = (String) e.nextElement();
	    i = (Integer) parent.servers.get(s);
	    StringTokenizer st = new StringTokenizer(s, "/");
	    s = (String) st.nextToken();
	    if (s.startsWith("Netscape")) {
		s = new String("Netscape");
	    }
	    if (servers.containsKey(s))
	    {
		y = (Integer) servers.get(s);
		y = new Integer(y.intValue() + i.intValue());
		servers.put(s, y);
	    }
	    else
	    {
		servers.put(s, i);
	    }
	}

	text.append("Unique Servers: " + servers.size() + "\n");
	print(servers);
	text.append("\n");
    }
    
    void update()
    {
	String key;
        Integer count;

        text.setText("");

//         text.append("Filter started: " + startDate.toLocaleString());
//         text.append("\n");

//         text.append("Current date: " + (new Date()).toLocaleString());
//         text.append("\n");
        
        text.append("Requests: ");
        text.append(parent.requests + "\n");
	text.append("\n");
        
        text.append("Replies: ");
        text.append(parent.replies + "\n");
	text.append("\n");
        
        text.append("Hosts: " + parent.hosts.size() + "\n");
        print(parent.hosts);
	text.append("\n");
        
        text.append("Servers: " + parent.servers.size() + "\n");
        print(parent.servers);
	text.append("\n");

	printUniqueServers();
        
        text.append("Content-types:\n");
        print(parent.contentTypes);
	text.append("\n");

        text.append("Content-lengths:\n");
        print(parent.contentLengths);
	text.append("\n");
    }
     
    public void actionPerformed(ActionEvent event)
    {
	String arg = event.getActionCommand();
	
	if ("doUpdate".equals(arg))
	{
	    update();
	}
	else if ("doReset".equals(arg))
	{
	    reset();
	}
	else if ("doClose".equals(arg))
	{
	    setVisible(false);
	}
	else if ("doHelp".equals(arg))
	{
	    new HelpFrame("Stats");
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
