/* $Id: ColorSample.java,v 1.5 2000/01/24 04:02:26 boyns Exp $ */

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

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

public class ColorSample extends Canvas
{
    Color color = null;
    final int width = 60;
    final int height = 20;
    
    public ColorSample(String color)
    {
	setColor(color);
    }

    public void setColor(String newColor)
    {
	Color c;
	
	try
	{
	    c = Color.decode(newColor);
	}
	catch (Exception e)
	{
	    c = Color.black;
	}

	this.color = c;

	repaint();
    }

    public void paint(Graphics g)
    {
	g.setColor(color);
	g.fill3DRect(0, 0, width, height, true);
    }

    public Dimension getPreferredSize()
    {
	return new Dimension(width, height);
    }

    public Dimension getPreferredSize(int rows)
    {
	return new Dimension(width, height);
    }

    public Dimension getMinimumSize()
    {
	return new Dimension(width, height);
    }

    public Dimension getMinimumSize(int rows)
    {
	return new Dimension(width, height);
    }
}
