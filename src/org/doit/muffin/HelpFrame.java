/* $Id: HelpFrame.java,v 1.5 2000/01/24 04:02:13 boyns Exp $ */

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

import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.io.InputStream;

public class HelpFrame extends MuffinFrame implements ActionListener, WindowListener
{
    public HelpFrame(String helpFile)
    {
	super("Help: " + helpFile);

	TextArea text = new TextArea();
	text.setEditable(false);

	URL url = getClass().getResource("/doc/" + helpFile + ".txt");
	if (url != null)
	{
	    try
	    {
		byte buf[] = new byte[8192];
		int n;

		InputStream in = url.openStream();
		while ((n = in.read(buf, 0, buf.length)) > 0)
		{
		    text.append(new String(buf, 0, n));
		}
		in.close();
	    }
	    catch (Exception e)
	    {
	    }
	}
	else
	{
	    text.append("No help available.");
	}

	add("Center", text);

	Button b;
	Panel buttonPanel = new Panel();
	buttonPanel.setLayout(new GridLayout(1, 1));
	b = new Button("Close");
	b.setActionCommand("doClose");
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
	
	if ("doClose".equals(arg))
	{
	    setVisible(false);
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
