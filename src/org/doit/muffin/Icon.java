/* $Id: Icon.java,v 1.5 2000/01/24 04:02:14 boyns Exp $ */

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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.ImageProducer;
import java.net.URL;

/**
 * Cute little muffin icon. 
 *
 * @author Mark Boyns
 */
  
class Icon extends java.awt.Canvas implements MouseListener
{
    private boolean raised = true;
    private Image icon = null;
    private Image nomuff = null;
    private Options options = null;

    /**
     * Create an Icon.
     */
    Icon(Options options)
    {
	this.options = options;
	
	try
	{
	    MediaTracker tracker = new MediaTracker(this);
	    URL url;
	    url = getClass().getResource("/images/mufficon.jpg");
	    if (url != null)
	    {
		icon = Toolkit.getDefaultToolkit().createImage((ImageProducer) url.getContent());
		tracker.addImage(icon, 1);
	    }
	    url = getClass().getResource("/images/nomuff.jpg");
	    if (url != null)
	    {
		nomuff = Toolkit.getDefaultToolkit().createImage((ImageProducer) url.getContent());
		tracker.addImage(nomuff, 2);
	    }
	    tracker.waitForAll();
	    addMouseListener(this);
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    public Dimension getPreferredSize()
    {
	return new Dimension(36, 36);
    }

    public Dimension getPreferredSize(int rows)
    {
	return new Dimension(36, 36);
    }

    public Dimension getMinimumSize()
    {
	return new Dimension(36, 36);
    }

    public Dimension getMinimumSize(int rows)
    {
	return new Dimension(36, 36);
    }

    public void mouseReleased(MouseEvent e)
    {
	raised = !raised;
	options.putBoolean("muffin.passthru", !raised);
	repaint();
    }
    
    public void mousePressed(MouseEvent e)
    {
    }
    
    public void mouseClicked(MouseEvent e)
    {
    }
    
    public void mouseEntered(MouseEvent e)
    {
    }
    
    public void mouseExited(MouseEvent e)
    {
    }

    /**
     * Paint the muffin.
     */
    public void paint(Graphics g)
    {
	int i;

	g.setColor(Color.lightGray);
	for (i = 0; i < 2; i++)
	{
	    g.draw3DRect(i, i, 36 - i*2, 36 - i*2, raised);
	}
	
	if (icon != null)
	{
	    g.drawImage(icon, 2, 2, this);
	}
	if (!raised && nomuff != null)
	{
	    g.drawImage(nomuff, 6, 6, this);
	}
	
	g.setColor(Color.lightGray);
	g.drawRect(2, 2, 32, 32);
    }
}
