/* $Id: BigList.java,v 1.5 2000/01/24 04:02:13 boyns Exp $ */

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

import java.awt.Dimension;
import java.awt.List;

/**
 * Create a list with fixed dimensions.
 *
 * @see java.awt.Dimension
 * @see java.awt.List
 * @author Mark Boyns
 */
public class BigList extends List
{
    int width = 200;
    int height = 150;
    
    public BigList(int rows, boolean mult)
    {
	super(rows, mult);
    }

    public void setHeight(int h)
    {
	height = h;
    }

    public void setWidth(int w)
    {
	width = w;
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
