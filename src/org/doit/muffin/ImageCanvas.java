/* $Id: ImageCanvas.java,v 1.5 2000/01/24 04:02:14 boyns Exp $ */

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
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.ImageProducer;
import java.net.URL;

/**
 * Display a fixed size image.
 *
 * @author Mark Boyns
 */
public class ImageCanvas extends java.awt.Canvas
{
    private Image image = null;
    private Color bg = null;
    private boolean border = false;

    /**
     * Create an ImageCanvas.
     */
    public ImageCanvas(String imageFile, Color bg, boolean border)
    {
	try
	{
	    MediaTracker tracker = new MediaTracker(this);
	    URL url = getClass().getResource(imageFile);
	    if (url != null)
	    {
		image = Toolkit.getDefaultToolkit().createImage((ImageProducer) url.getContent());
		tracker.addImage(image, 1);
		tracker.waitForAll();
	    }
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
	
	this.bg = bg;
	this.border = border;

	if (bg != null)
	{
	    setBackground(bg);
	}
    }

    public ImageCanvas(Image image)
    {
	this.image = image;
    }
    
    public Dimension getPreferredSize()
    {
	return new Dimension(image.getWidth(this), image.getHeight(this));
    }

    public Dimension getPreferredSize(int rows)
    {
	return new Dimension(image.getWidth(this), image.getHeight(this));
    }

    public Dimension getMinimumSize()
    {
	return new Dimension(image.getWidth(this), image.getHeight(this));
    }

    public Dimension getMinimumSize(int rows)
    {
	return new Dimension(image.getWidth(this), image.getHeight(this));
    }

    public void paint(Graphics g)
    {
	if (image != null)
	{
	    g.drawImage(image, 0, 0, this);
	}
	if (border)
	{
	    g.setColor(Color.black);
	    g.drawRect(0, 0, image.getWidth(this)-1, image.getHeight(this)-1);
	}
    }
}
