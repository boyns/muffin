/* $Id: PreviewDialog.java,v 1.6 2000/02/10 20:08:27 boyns Exp $ */

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
import java.io.*;

class PreviewDialog extends Dialog implements ActionListener, WindowListener
{
    byte content[] = null;
    boolean accepted = true;
    TextArea text = null;
    Image image = null;
    Frame frame = null;
    
    PreviewDialog(Frame frame, Request request, Reply reply, byte content[])
    {
	super(frame, "Muffin: Preview " + request.getURL(), true);

	this.content = content;
	this.frame = frame;
	    
	if (reply.getContentType().startsWith("text"))
	{
	    text = new TextArea();
	    text.setEditable(true);
	    text.setText(new String(content));
	    add("Center", text);
	}
	else if (reply.getContentType().startsWith("image"))
	{
	    image = Toolkit.getDefaultToolkit().createImage(content);
	    ImageCanvas ic = new ImageCanvas(image);
	    add("Center", ic);
	}

	Panel buttonPanel = new Panel();
	buttonPanel.setLayout(new GridLayout(1, 3));
	Button b;
	b = new Button("Accept");
	b.setActionCommand("doAccept");
	b.addActionListener(this);
	buttonPanel.add(b);
	b = new Button("Reject");
	b.setActionCommand("doReject");
	b.addActionListener(this);
	buttonPanel.add(b);
        // Also propose to save the content as a file:
        b = new Button("Save as");
        b.setActionCommand("doSaveAs");
        b.addActionListener(this);
	buttonPanel.add(b);
	add("South", buttonPanel);

	addWindowListener(this);
	pack();
	setSize(getPreferredSize());
    }

    public boolean accept()
    {
	return accepted;
    }

    public byte[] getContent()
    {
	return content;
    }

    public void actionPerformed(ActionEvent event)
    {
	String arg = event.getActionCommand();
	
	if ("doAccept".equals(arg))
	{
	    accepted = true;
	    if (text != null)
	    {
		content = text.getText().getBytes();
	    }
	    setVisible(false);
	}
	else if ("doReject".equals(arg))
	{
	    accepted = false;
	    setVisible(false);
	}
        else if ("doSaveAs".equals(arg))
        {
	    try
	    {
		FileDialog dialog = new FileDialog(frame, "Save Content");
		dialog.show();
		if ( dialog.getFile() != null )
		{
		    File f = new File(dialog.getDirectory(), dialog.getFile());
		    FileOutputStream fos = new FileOutputStream(f);
		    fos.write(content);
		    fos.close();
		}
	    } catch (IOException exc)
	    {
		// too bad!
	    }
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
