/* MuffinFrame.java */

/*
 * Copyright (C) 1996-98 Mark R. Boyns <boyns@doit.org>
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
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.*;
import java.net.URL;
import java.util.Vector;

public class MuffinFrame extends Frame
{
    static Vector frames = new Vector ();
    
    public MuffinFrame (String title)
    {
	super (title);

	if (! Main.getOptions ().getBoolean ("muffin.noWindow"))
	{
	    setFont (Main.getOptions ().getFont ("muffin.font"));
	    setIcon ();
	    configColors (this);
	}
	
	frames.addElement (this);
    }

    void setIcon ()
    {
	try
	{
	    Image image;
	    MediaTracker tracker = new MediaTracker (this);
	    URL url = getClass ().getResource ("images/mufficon.jpg");
	    if (url != null)
	    {
		image = Toolkit.getDefaultToolkit ().createImage ((ImageProducer) url.getContent ());
		tracker.addImage (image, 1);
		tracker.waitForAll ();
		setIconImage (image);
	    }
	}
	catch (Exception e)
	{
	    e.printStackTrace ();
	}
    }

    public void dispose ()
    {
	frames.removeElement (this);
	super.dispose ();
    }

    static void configColors (Frame frame)
    {
	frame.setBackground (Main.getOptions ().getColor ("muffin.bg"));
	frame.setForeground (Main.getOptions ().getColor ("muffin.fg"));
    }

    static void repaintFrames ()
    {
	for (int i = 0; i < frames.size (); i++)
	{
	    Frame frame = (Frame) frames.elementAt (i);
	    configColors (frame);
	    frame.repaint ();
	}
    }
}
