/**
 * ImageKillFrame.java -- kill images that match a certain size ratio
 *
 * @author  Heinrich Opgenoorth <opgenoorth@gmd.de>
 * @version 0.2
 *
 * Last update: 98/11/30 H.O.
 */

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

public class ImageKillFrame extends MuffinFrame 
  implements ActionListener, WindowListener
{
    Prefs prefs;
    ImageKill parent;
    TextField t_minheight, t_minwidth, t_ratio, t_exclude,
	t_rmSizes, t_replaceURL;
    Label l_title, l_wider, l_higher, l_ratio, l_ex, l_fixed;
    Checkbox cb_keepmaps, cb_replace;

    public ImageKillFrame (Prefs prefs, ImageKill parent)
    {
	super ("Muffin: ImageKill");

	this.prefs = prefs;
	this.parent = parent;

	Panel panel = new Panel ();
	GridBagLayout layout = new GridBagLayout ();
	panel.setLayout (layout);
	GridBagConstraints c;

	l_title = new Label ("Remove images if:");
	l_wider = new Label ("width >", Label.RIGHT);
	l_higher = new Label ("and height >", Label.RIGHT);
	l_ratio = new Label ("and w/h ratio >", Label.RIGHT);
	l_ex = new Label ("but not if img.src contains ");
	l_fixed = new Label ("Fixed sizes to remove ");

	t_minheight = new TextField (5);
	t_minwidth = new TextField (5);
	t_ratio = new TextField (5);
	t_exclude = new TextField (30);
	t_rmSizes = new TextField (30);
	t_replaceURL = new TextField (30);
	cb_keepmaps = new Checkbox ("Don't remove image maps");
	cb_replace = new Checkbox ("Replace with URL ");

	t_minheight.setText (Integer.toString
			     (prefs.getInteger ("ImageKill.minheight")));
	t_minwidth.setText (Integer.toString
			    (prefs.getInteger ("ImageKill.minwidth")));
	t_ratio.setText (Integer.toString
			 (prefs.getInteger ("ImageKill.ratio")));
	t_exclude.setText (prefs.getString ("ImageKill.exclude"));
	t_rmSizes.setText (prefs.getString ("ImageKill.rmSizes"));
	t_replaceURL.setText (prefs.getString ("ImageKill.replaceURL"));
	cb_keepmaps.setState (prefs.getBoolean ("ImageKill.keepmaps"));
	cb_replace.setState (prefs.getBoolean ("ImageKill.replace"));

	c = new GridBagConstraints ();
	c.insets = new Insets(0, 2, 0, 2);
	c.gridx = 0;
	c.gridy = 0;
	c.anchor = GridBagConstraints.WEST;
	layout.setConstraints (l_title, c);
	panel.add (l_title);

	Panel p2 = new Panel ();
	p2.add (l_wider);
	p2.add (t_minwidth);
	p2.add (l_higher);
	p2.add (t_minheight);
	p2.add (l_ratio);
	p2.add (t_ratio);

	c.gridy = 1;
	c.gridwidth = 2;
	layout.setConstraints (p2, c);
	panel.add (p2);

	c.gridy = 2;
	c.gridwidth = 1;
	c.anchor = GridBagConstraints.EAST;
	layout.setConstraints (l_ex, c);
	panel.add (l_ex);

	c.gridy = 3;
	layout.setConstraints (l_fixed, c);
	panel.add (l_fixed);

	c.gridy = 4;
	layout.setConstraints (cb_replace, c);
	panel.add (cb_replace);

	c.gridy = 5;
	c.anchor = GridBagConstraints.WEST;
	layout.setConstraints (cb_keepmaps, c);
	panel.add (cb_keepmaps);

	c.gridx = 1;
	c.gridy = 2;
	layout.setConstraints (t_exclude, c);
	panel.add (t_exclude);

	c.gridy = 3;
	layout.setConstraints (t_rmSizes, c);
	panel.add (t_rmSizes);

	c.gridy = 4;
	layout.setConstraints (t_replaceURL, c);
	panel.add (t_replaceURL);

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
	prefs.putString ("ImageKill.rmSizes", t_rmSizes.getText ());
	prefs.putString ("ImageKill.replaceURL", t_replaceURL.getText ());
	prefs.putBoolean ("ImageKill.replace", cb_replace.getState ());
	parent.setExclude ();
	parent.setRemoveSizes ();
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
