/**
 * ImageKillFrame.java -- kill images that match a certain size ratio
 *
 * @author  Heinrich Opgenoorth <opgenoorth@gmd.de>
 * @version 0.1
 *
 * Last update: 98/07/08 H.O.
 */

/* 
 * Copyright (C) 1996-98 Mark Boyns <boyns@sdsu.edu>
 *
 * This file is part of Muffin.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package org.doit.muffin.filter;

import java.awt.*;
import java.awt.event.*;
import org.doit.muffin.*;

public class ImageKillFrame extends MuffinFrame 
  implements ActionListener, WindowListener
{
    Prefs prefs;
    ImageKill parent;
    TextField t_minheight, t_minwidth, t_ratio, t_exclude;
    Label l1, l2, l3, l4, l5;
    Checkbox cb_keepmaps;    

    public ImageKillFrame (Prefs prefs, ImageKill parent)
    {
	super ("Muffin: ImageKill");

	this.prefs = prefs;
	this.parent = parent;

	Panel panel = new Panel ();
	GridBagLayout layout = new GridBagLayout ();
	panel.setLayout (layout);
	GridBagConstraints c;

	l1 = new Label ("Remove images if:");
	l2 = new Label ("higher than ");
	l3 = new Label ("and wider than ");
	l4 = new Label ("and width/height ratio is greater than ");
	l5 = new Label ("but not if img.src contains ");
	t_minheight = new TextField (5);
	t_minwidth = new TextField (5);
	t_ratio = new TextField (5);
	t_exclude = new TextField (30);
	cb_keepmaps = new Checkbox ("Don't remove image maps");

	t_minheight.setText (Integer.toString
			     (prefs.getInteger ("ImageKill.minheight")));
	t_minwidth.setText (Integer.toString
			    (prefs.getInteger ("ImageKill.minwidth")));
	t_ratio.setText (Integer.toString
			 (prefs.getInteger ("ImageKill.ratio")));
	t_exclude.setText (prefs.getString ("ImageKill.exclude"));
	cb_keepmaps.setState (prefs.getBoolean ("ImageKill.keepmaps"));

	c = new GridBagConstraints ();
	c.gridx = 0;
	c.gridy = 0;
	layout.setConstraints (l1, c);
	panel.add (l1);
	c.gridy = 1;
	c.anchor = GridBagConstraints.EAST;
	layout.setConstraints (l2, c);
	panel.add (l2);
	c.gridy = 2;
	layout.setConstraints (l3, c);
	panel.add (l3);
	c.gridy = 3;
	layout.setConstraints (l4, c);
	panel.add (l4);
	c.gridy = 4;
	layout.setConstraints (l5, c);
	panel.add (l5);
	c.gridy = 5;
	layout.setConstraints (cb_keepmaps, c);
	panel.add (cb_keepmaps);
	c.gridx = 1;
	c.gridy = 1;
	c.anchor = GridBagConstraints.WEST;
	layout.setConstraints (t_minheight, c);
	panel.add (t_minheight);
	c.gridy = 2;
	layout.setConstraints (t_minwidth, c);
	panel.add (t_minwidth);
	c.gridy = 3;
	layout.setConstraints (t_ratio, c);
	panel.add (t_ratio);
	c.gridy = 4;
	layout.setConstraints (t_exclude, c);
	panel.add (t_exclude);
	add ("North", panel);
	
	parent.messages.setEditable (false);
	add ("Center", parent.messages);

	Button b;
	Panel buttonPanel = new Panel ();
	buttonPanel.setLayout (new GridLayout (1, 5));
	b = new Button ("Apply");
	b.setActionCommand ("doApply");
	b.addActionListener (this);
	buttonPanel.add (b);
	b = new Button ("Save");
	b.setActionCommand ("doSave");
	b.addActionListener (this);
	buttonPanel.add (b);
	b = new Button ("Clear");
	b.setActionCommand ("doClear");
	b.addActionListener (this);
	buttonPanel.add (b);
	b = new Button ("Close");
	b.setActionCommand ("doClose");
	b.addActionListener (this);
	buttonPanel.add (b);
	b = new Button ("Help");
	b.setActionCommand ("doHelp");
	b.addActionListener (this);
	buttonPanel.add (b);

	add ("South", buttonPanel);

	addWindowListener (this);
	
	pack ();
	setSize (getPreferredSize ());

	show ();
    }

    public void actionPerformed (ActionEvent event)
    {
	String arg = event.getActionCommand ();
	
	if ("doApply".equals (arg))
	{
            applyPrefs ();
	}
	else if ("doSave".equals (arg))
	{
            applyPrefs ();
	    parent.save ();
	}
	else if ("doClose".equals (arg))
	{
	    setVisible (false);
	}
	else if ("doClear".equals (arg))
	{
	    parent.messages.clear ();
	}
	else if ("doHelp".equals (arg))
	{
	    new HelpFrame ("ImageKill");
	}
    }

    private void applyPrefs ()
    {
        prefs.putInteger ("ImageKill.minheight", 
			  Integer.parseInt (t_minheight.getText ()));
	prefs.putInteger ("ImageKill.minwidth",
			  Integer.parseInt (t_minwidth.getText ()));
	prefs.putInteger ("ImageKill.ratio",
			  Integer.parseInt (t_ratio.getText ()));
	prefs.putBoolean ("ImageKill.keepmaps", cb_keepmaps.getState ());
	prefs.putString ("ImageKill.exclude", t_exclude.getText ());
	parent.setExclude ();
    }

    public void windowActivated (WindowEvent e)
    {
    }
  
    public void windowDeactivated (WindowEvent e)
    {
    }
  
    public void windowClosing (WindowEvent e)
    {
	setVisible (false);
    }
  
    public void windowClosed (WindowEvent e)
    {
    }
  
    public void windowIconified (WindowEvent e)
    {
    }
  
    public void windowDeiconified (WindowEvent e)
    {
    }
  
    public void windowOpened (WindowEvent e)
    {
    }
}
