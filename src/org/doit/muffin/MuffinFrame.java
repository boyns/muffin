/* $Id: MuffinFrame.java,v 1.11 2000/03/30 06:34:28 boyns Exp $ */

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
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.*;
import java.net.URL;
import java.util.Vector;
import gnu.regexp.*;

public class MuffinFrame extends Frame
{
    static Vector frames = new Vector();

    public MuffinFrame(String title)
    {
	super(title);

	if (!Main.getOptions().getBoolean("muffin.noWindow"))
	{
	    setFont(Main.getOptions().getFont("muffin.font"));
	    setIcon();
	    configColors(this);
	}
	
	frames.addElement(this);
    }

    void moveNearMuffin()
    {
	if (getTitle().equals("Muffin"))
	{
	    return;
	}

	Dimension screenSize = getToolkit().getScreenSize();
	MuffinFrame muffin = getFrame("Muffin");
	Dimension muffinSize = muffin.getSize();
	Point muffinLocation = muffin.getLocationOnScreen();
	Dimension size = getSize();
	int x = 0, y = 0;

	x = muffinLocation.x + muffinSize.width;
	y = muffinLocation.y;

	if (x + size.width > screenSize.width)
	{
	    x = muffinLocation.x - size.width;
	    if (x < 0) x = 0;
	}
	if (y + size.height > screenSize.height)
	{
	    y -= (y + size.height) - screenSize.height;
	    if (y < 0) y = 0;
	}

	setLocation(x, y);
    }

    void setIcon()
    {
	try
	{
	    Image image;
	    MediaTracker tracker = new MediaTracker(this);
	    URL url = getClass().getResource("/images/mufficon.jpg");
	    Object obj;
	    if (url != null && ((obj = url.getContent()) instanceof ImageProducer))
	    {
		image = Toolkit.getDefaultToolkit().createImage((ImageProducer) obj);
		tracker.addImage(image, 1);
		tracker.waitForAll();
		setIconImage(image);
	    }
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    public void show()
    {
	moveNearMuffin();
	super.show();
    }

    public void dispose()
    {
	frames.removeElement(this);
	super.dispose();
    }

    public String getGeometry()
    {
	Dimension size = getSize();
	Point location = getLocationOnScreen();
	StringBuffer buf = new StringBuffer();
	buf.append(size.width);
	buf.append("x");
	buf.append(size.height);
	buf.append("+");
	buf.append(location.x);
	buf.append("+");
	buf.append(location.y);
	return buf.toString();
    }

    public void updateGeometry(String geometry)
    {
	if (geometry == null && geometry.length() <= 0)
	{
	    return;
	}
	
	Dimension loc = null;
	Point pos = null;
	
	try
	{
	    RE re = new RE("([0-9]+x[0-9]+)?([\\+\\-][0-9]+[\\+\\-][0-9]+)?");
	    REMatch match = re.getMatch(geometry);

	    if (match != null)
	    {
		int i, j;

		i = match.getSubStartIndex(1);
		j = match.getSubEndIndex(1);
		if (i != -1 && j != -1 && i != j)
		{
		    String s = geometry.substring(i, j);
		    int n = s.indexOf('x');
		    if (n != -1)
		    {
			loc = new Dimension(Integer.parseInt(s.substring(0, n)),
					    Integer.parseInt(s.substring(n+1)));
		    }
		}

		i = match.getSubStartIndex(2);
		j = match.getSubEndIndex(2);
		if (i != -1 && j != -1 && i != j)
		{
		    Dimension screenSize = getToolkit().getScreenSize();
		    String s = geometry.substring(i, j);
		    int n = s.lastIndexOf('+');
		    if (n == -1 || n == 0)
			n = s.lastIndexOf('-');

		    int x = Integer.parseInt(s.substring(s.charAt(0) == '+' ? 1 : 0, n));
		    int y = Integer.parseInt(s.substring(s.charAt(n) == '+' ? n+1 : n));

		    if (x < 0)
			x = screenSize.width + x;
		    if (y < 0)
			y = screenSize.height + y;
		    
		    pos = new Point(x, y);
		}
	    }
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}

	hide();
	pack();
	setSize(loc != null ? loc : getPreferredSize());
	if (pos != null)
	{
	    setLocation(pos);
	}
	show();
    }

    static void configColors(Frame frame)
    {
	frame.setBackground(Main.getOptions().getColor("muffin.bg"));
	frame.setForeground(Main.getOptions().getColor("muffin.fg"));
    }

    static void repaintFrames()
    {
	for (int i = 0; i < frames.size(); i++)
	{
	    Frame frame = (Frame) frames.elementAt(i);
	    configColors(frame);
	    frame.repaint();
	}
    }

    static MuffinFrame getFrame(String title)
    {
	for (int i = 0; i < frames.size(); i++)
	{
	    MuffinFrame frame = (MuffinFrame) frames.elementAt(i);
	    if (frame.getTitle().equals(title))
	    {
		return frame;
	    }
	}
	return null;
    }
}
