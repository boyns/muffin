/* $Id: PainterFrame.java,v 1.5 2000/01/24 04:02:20 boyns Exp $ */

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
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import org.doit.muffin.*;
import org.doit.util.ColorSample;

public class PainterFrame extends MuffinFrame implements ActionListener, WindowListener, ItemListener
{
    Prefs prefs;
    Painter parent;
    TextField bgcolor, link, alink, vlink, background, text;
    ColorSample bgcolorSample, linkSample, alinkSample, vlinkSample, textSample;
    Hashtable styleTable = null;
    String styles[] = { "None", "Dark", "Light", "Christmas" };
	
    public PainterFrame(Prefs prefs, Painter parent)
    {
	super("Muffin: Painter");

	this.prefs = prefs;
	this.parent = parent;

	Panel panel = new Panel();
	GridBagLayout layout = new GridBagLayout();
	panel.setLayout(layout);
	GridBagConstraints c;
	Label l;

	l = new Label("Sample Colors:", Label.RIGHT);
	c = new GridBagConstraints();
	layout.setConstraints(l, c);
	panel.add(l);

	createStyleTable();

	Choice choice = new Choice();
	choice.addItemListener(this);
	for (int i = 0; i < styles.length; i++)
	{
	    choice.addItem(styles[i]);
	}
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	layout.setConstraints(choice, c);
	panel.add(choice);

	l = new Label("Background Image:", Label.RIGHT);
	c = new GridBagConstraints();
	layout.setConstraints(l, c);
	panel.add(l);

	background = new TextField(32);
	background.setText(prefs.getString("Painter.background"));
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	layout.setConstraints(background, c);
	panel.add(background);


	l = new Label("Background Color:", Label.RIGHT);
	c = new GridBagConstraints();
	layout.setConstraints(l, c);
	panel.add(l);

	bgcolor = new TextField(7);
	bgcolor.setText(prefs.getString("Painter.bgcolor"));
	bgcolor.addActionListener(this);
	c = new GridBagConstraints();
	layout.setConstraints(bgcolor, c);
	panel.add(bgcolor);

	bgcolorSample = new ColorSample(prefs.getString("Painter.bgcolor"));
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	layout.setConstraints(bgcolorSample, c);
	panel.add(bgcolorSample);
	
	
	l = new Label("Text Color:", Label.RIGHT);
	c = new GridBagConstraints();
	layout.setConstraints(l, c);
	panel.add(l);

	text = new TextField(7);
	text.setText(prefs.getString("Painter.text"));
	text.addActionListener(this);
	c = new GridBagConstraints();
	layout.setConstraints(text, c);
	panel.add(text);

	textSample = new ColorSample(prefs.getString("Painter.text"));
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	layout.setConstraints(textSample, c);
	panel.add(textSample);


	l = new Label("Link Color:", Label.RIGHT);
	c = new GridBagConstraints();
	layout.setConstraints(l, c);
	panel.add(l);

	link = new TextField(7);
	link.setText(prefs.getString("Painter.link"));
	link.addActionListener(this);
	c = new GridBagConstraints();
	layout.setConstraints(link, c);
	panel.add(link);

	linkSample = new ColorSample(prefs.getString("Painter.link"));
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	layout.setConstraints(linkSample, c);
	panel.add(linkSample);


	l = new Label("Visited Link Color:", Label.RIGHT);
	c = new GridBagConstraints();
	layout.setConstraints(l, c);
	panel.add(l);

	vlink = new TextField(7);
	vlink.setText(prefs.getString("Painter.vlink"));
	vlink.addActionListener(this);
	c = new GridBagConstraints();
	layout.setConstraints(vlink, c);
	panel.add(vlink);
	
	vlinkSample = new ColorSample(prefs.getString("Painter.vlink"));
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	layout.setConstraints(vlinkSample, c);
	panel.add(vlinkSample);


	l = new Label("Active Link Color:", Label.RIGHT);
	c = new GridBagConstraints();
	layout.setConstraints(l, c);
	panel.add(l);

	alink = new TextField(7);
	alink.setText(prefs.getString("Painter.alink"));
	alink.addActionListener(this);
	c = new GridBagConstraints();
	layout.setConstraints(alink, c);
	panel.add(alink);

	alinkSample = new ColorSample(prefs.getString("Painter.alink"));
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	layout.setConstraints(alinkSample, c);
	panel.add(alinkSample);

	add("Center", panel);

	Button b;
	Panel buttonPanel = new Panel();
	buttonPanel.setLayout(new GridLayout(1, 4));
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

    void createStyleTable()
    {
	Vector v;

	styleTable = new Hashtable(13);

	/* None */
	v = new Vector();
	v.addElement("None"); // bgcolor
	v.addElement("None"); // text
	v.addElement("None"); // link
	v.addElement("None"); // vlink
	v.addElement("None"); // alink
	styleTable.put(styles[0], v);

	/* Dark */
	v = new Vector();
	v.addElement("#000000"); // bgcolor
	v.addElement("#ffffff"); // text
	v.addElement("#98fb98"); // link
	v.addElement("#ffa07a"); // vlink
	v.addElement("#ff0000"); // alink
	styleTable.put(styles[1], v);

	/* Light */
	v = new Vector();
	v.addElement("#ffffff"); // bgcolor
	v.addElement("#000000"); // text
	v.addElement("#0000ee"); // link
	v.addElement("#551a8b"); // vlink
	v.addElement("#ff0000"); // alink
	styleTable.put(styles[2], v);
	
	/* Christmas */
	v = new Vector();
	v.addElement("#ffffff"); // bgcolor
	v.addElement("#000000"); // text
	v.addElement("#00ff00"); // link
	v.addElement("#ff0000"); // vlink
	v.addElement("#ff0000"); // alink
	styleTable.put(styles[3], v);
    }

    void updateSamples()
    {
	bgcolorSample.setColor(bgcolor.getText());
	textSample.setColor(text.getText());
	linkSample.setColor(link.getText());
	vlinkSample.setColor(vlink.getText());
	alinkSample.setColor(alink.getText());
    }

    public void actionPerformed(ActionEvent event)
    {
	String arg = event.getActionCommand();
	
	if ("doApply".equals(arg))
	{
	    prefs.putString("Painter.bgcolor", bgcolor.getText());
	    prefs.putString("Painter.background", background.getText());
	    prefs.putString("Painter.text", text.getText());
	    prefs.putString("Painter.link", link.getText());
	    prefs.putString("Painter.vlink", vlink.getText());
	    prefs.putString("Painter.alink", alink.getText());
	    updateSamples();
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
	    new HelpFrame("Painter");
	}
	else if (event.getSource() == bgcolor)
	{
	    bgcolorSample.setColor(bgcolor.getText());
	}
	else if (event.getSource() == text)
	{
	    textSample.setColor(text.getText());
	}
	else if (event.getSource() == link)
	{
	    linkSample.setColor(link.getText());
	}
	else if (event.getSource() == vlink)
	{
	    vlinkSample.setColor(vlink.getText());
	}
	else if (event.getSource() == alink)
	{
	    alinkSample.setColor(alink.getText());
	}
    }

    public void itemStateChanged(ItemEvent event)
    {
	Vector v = (Vector) styleTable.get(event.getItem().toString());
	bgcolor.setText((String) v.elementAt(0));
	text.setText((String) v.elementAt(1));
	link.setText((String) v.elementAt(2));
	vlink.setText((String) v.elementAt(3));
	alink.setText((String) v.elementAt(4));
	updateSamples();
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
