/* $Id: DocumentInfoFrame.java,v 1.5 2000/01/24 04:02:19 boyns Exp $ */

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

public class DocumentInfoFrame extends MuffinFrame implements ActionListener, ItemListener, WindowListener
{
    Prefs prefs;
    DocumentInfo parent;
    
    TextField info = null;
    TextField htmlBefore = null;
    TextField htmlAfter = null;
    Choice locationChoice = null;
    Choice alignChoice = null;
    
    public DocumentInfoFrame(Prefs prefs, DocumentInfo parent)
    {
	super("Muffin: DocumentInfo");

	this.prefs = prefs;
	this.parent = parent;

	Panel panel = new Panel();
	GridBagLayout layout = new GridBagLayout();
	GridBagConstraints c;
        panel.setLayout(layout);

	panel.add(new Label("Location:"));
	locationChoice = new Choice();
	locationChoice.addItemListener(this);
	locationChoice.addItem("top");
	locationChoice.addItem("bottom");
	locationChoice.select(prefs.getString("DocumentInfo.location"));

	c = new GridBagConstraints();
	c.anchor = GridBagConstraints.WEST;
	c.gridwidth = GridBagConstraints.REMAINDER;
	layout.setConstraints(locationChoice, c);
 	panel.add(locationChoice);

	panel.add(new Label("Align:"));
	alignChoice = new Choice();
	alignChoice.addItemListener(this);
	alignChoice.addItem("left");
	alignChoice.addItem("right");
	alignChoice.select(prefs.getString("DocumentInfo.align"));

	c = new GridBagConstraints();
	c.anchor = GridBagConstraints.WEST;
	c.gridwidth = GridBagConstraints.REMAINDER;
	layout.setConstraints(alignChoice, c);
	panel.add(alignChoice);

	panel.add(new Label("Info:"));
	info = new TextField(50);
	info.setText(prefs.getString("DocumentInfo.info"));
	c = new GridBagConstraints();
	c.anchor = GridBagConstraints.WEST;
	c.gridwidth = GridBagConstraints.REMAINDER;
	layout.setConstraints(info, c);
	panel.add(info);
	
	panel.add(new Label("HTML Before:"));
	htmlBefore = new TextField(50);
	htmlBefore.setText(prefs.getString("DocumentInfo.htmlBefore"));
	c = new GridBagConstraints();
	c.anchor = GridBagConstraints.WEST;
	c.gridwidth = GridBagConstraints.REMAINDER;
	layout.setConstraints(htmlBefore, c);
	panel.add(htmlBefore);

	panel.add(new Label("HTML After:"));
	htmlAfter = new TextField(50);
	htmlAfter.setText(prefs.getString("DocumentInfo.htmlAfter"));
	c = new GridBagConstraints();
	c.anchor = GridBagConstraints.WEST;
	c.gridwidth = GridBagConstraints.REMAINDER;
	layout.setConstraints(htmlBefore, c);
	panel.add(htmlAfter);

	add("Center", panel);

	Panel buttonPanel = new Panel();
	buttonPanel.setLayout(new GridLayout(1, 4));
	Button b;
	b = new Button("Apply");
	b.setActionCommand("doApply");
	b.addActionListener(this);
	buttonPanel.add(b);
	b = new Button("Save");
	b.setActionCommand("doSave");
	b.addActionListener(this);
	buttonPanel.add(b);
	b = new Button("Close");
	b.setActionCommand("doClose");
	b.addActionListener(this);
	buttonPanel.add(b);
	b = new Button("Help");
	b.setActionCommand("doHelp");
	b.addActionListener(this);
	buttonPanel.add(b);
	add("South", buttonPanel);

	addWindowListener(this);
	
	pack();
	setSize(getPreferredSize());

	show();
    }

    public void itemStateChanged(ItemEvent event)
    {
// 	Object obj = event.getItem();
// 	if (obj == locationChoice)
// 	{
// 	    prefs.putString("DocumentInfo.location", obj.toString());
// 	}
// 	else if (obj == alignChoice)
// 	{
// 	    prefs.putString("DocumentInfo.align", obj.toString());
// 	}
    }

    public void actionPerformed(ActionEvent event)
    {
	String arg = event.getActionCommand();
	
	if ("doApply".equals(arg))
	{
	    prefs.putString("DocumentInfo.location", locationChoice.getSelectedItem());
	    prefs.putString("DocumentInfo.align", alignChoice.getSelectedItem());
	    prefs.putString("DocumentInfo.info", (info.getText()).trim());
	    prefs.putString("DocumentInfo.htmlBefore", htmlBefore.getText());
	    prefs.putString("DocumentInfo.htmlAfter", htmlAfter.getText());
	}
	else if ("doSave".equals(arg))
	{
	    parent.save();
	}
	else if ("doClose".equals(arg))
	{
	    setVisible(false);
	}
	else if ("doHelp".equals(arg))
	{
	    new HelpFrame("DocumentInfo");
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
