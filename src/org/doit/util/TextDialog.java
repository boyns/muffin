/* $Id: TextDialog.java,v 1.5 2000/01/24 04:02:26 boyns Exp $ */

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

public class TextDialog extends Dialog implements ActionListener, WindowListener
{
    TextField text;
    
    public TextDialog(Frame frame, String question)
    {
	super(frame, "Muffin: Question", true);

	setFont(new Font("Helvetica", Font.BOLD, 12));

	add("North", new Label(question));
	text = new TextField(32);
	add("Center", text);

	Panel buttonPanel = new Panel();
	buttonPanel.setLayout(new GridLayout(1, 2));
	Button b;
	b = new Button("Ok");
	b.setActionCommand("doOk");
	b.addActionListener(this);
	buttonPanel.add(b);
	b = new Button("Cancel");
	b.setActionCommand("doCancel");
	b.addActionListener(this);
	buttonPanel.add(b);
	add("South", buttonPanel);

	addWindowListener(this);
	pack();
	setSize(getPreferredSize());
    }

    public String getAnswer()
    {
	return text.getText();
    }

    public void actionPerformed(ActionEvent event)
    {
	String arg = event.getActionCommand();
	
	if ("doCancel".equals(arg))
	{
	    text.setText("");
	    setVisible(false);
	}
	else if ("doOk".equals(arg))
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
