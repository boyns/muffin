/* $Id: YesNoDialog.java,v 1.5 2000/01/24 04:02:26 boyns Exp $ */

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
package org.doit.util;

import java.awt.*;
import java.awt.event.*;

public class YesNoDialog extends Dialog implements ActionListener, WindowListener
{
    boolean answer = false;
    
    public YesNoDialog(Frame frame, String question)
    {
	super(frame, "Muffin: Question", true);

	setFont(new Font("Helvetica", Font.BOLD, 12));
	
	add("Center", new Label(question));

	Panel buttonPanel = new Panel();
	buttonPanel.setLayout(new GridLayout(1, 2));
	Button b;
	b = new Button("Yes");
	b.setActionCommand("doYes");
	b.addActionListener(this);
	buttonPanel.add(b);
	b = new Button("No");
	b.setActionCommand("doNo");
	b.addActionListener(this);
	buttonPanel.add(b);
	add("South", buttonPanel);

	addWindowListener(this);
	pack();
	setSize(getPreferredSize());
    }

    public boolean isYes()
    {
	return answer;
    }

    public void actionPerformed(ActionEvent event)
    {
	String arg = event.getActionCommand();
	
	if ("doYes".equals(arg))
	{
	    answer = true;
	    setVisible(false);
	}
	else if ("doNo".equals(arg))
	{
	    answer = false;
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
