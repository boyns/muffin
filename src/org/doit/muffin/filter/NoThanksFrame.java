/* $Id: NoThanksFrame.java,v 1.8 2000/03/29 15:17:43 boyns Exp $ */

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
import java.io.*;
import org.doit.muffin.*;
import org.doit.util.*;

public class NoThanksFrame extends MuffinFrame implements ActionListener, WindowListener
{
    Prefs prefs;
    NoThanks parent;
    TextField input = null;
    TextArea text = null;
    
    public NoThanksFrame(Prefs prefs, NoThanks parent)
    {
	super("Muffin: NoThanks");

	this.prefs = prefs;
	this.parent = parent;

	GridBagLayout layout = new GridBagLayout();
        setLayout(layout);
	GridBagConstraints c;
	Label l;
	
	add(new Label("Kill File:", Label.RIGHT));

	input = new TextField(40);
	input.setText(prefs.getString("NoThanks.killfile"));
	c = new GridBagConstraints();
	c.anchor = GridBagConstraints.NORTHWEST;
	c.gridwidth = 2;
	layout.setConstraints(input, c);
	add(input);

	Button browse = new Button("Browse...");
	browse.setActionCommand("doBrowse");
	browse.addActionListener(this);
	c = new GridBagConstraints();
	c.anchor = GridBagConstraints.NORTHWEST;
	c.gridwidth = 1;//GridBagConstraints.RELATIVE;
	layout.setConstraints(browse, c);
	add(browse);

	
	text = new TextArea();
	c = new GridBagConstraints();
	c.anchor = GridBagConstraints.NORTHWEST;
	c.fill = GridBagConstraints.BOTH;
	c.gridx = 0;
	c.gridy = 1;
	c.gridwidth = 4;
	c.gridheight = 4;
	c.weightx = 1.0;
	c.weighty = 1.0;
	c.insets = new Insets(0, 10, 5, 10);
	layout.setConstraints(text, c);
	add(text);

	Button b;
	b = new Button("Apply");
	b.setActionCommand("doApply");
	b.addActionListener(this);
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	c.anchor = GridBagConstraints.NORTHWEST;
	c.gridy = 1;
	layout.setConstraints(b, c);
	add(b);

	b = new Button("Load");
	b.setActionCommand("doLoad");
	b.addActionListener(this);
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	c.anchor = GridBagConstraints.NORTHWEST;
	layout.setConstraints(b, c);
	add(b);

	b = new Button("Save");
	b.setActionCommand("doSave");
	b.addActionListener(this);
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	c.anchor = GridBagConstraints.NORTHWEST;
	layout.setConstraints(b, c);
	add(b);

	l = new Label("Message Log");
	c = new GridBagConstraints();
	c.insets = new Insets(0, 10, 5, 10);
	c.gridx = 0;
	c.gridwidth = GridBagConstraints.REMAINDER;
	c.anchor = GridBagConstraints.NORTHWEST;
	layout.setConstraints(l, c);
	add(l);
	
	c = new GridBagConstraints();
	c.insets = new Insets(0, 10, 5, 10);
	c.gridwidth = GridBagConstraints.REMAINDER;
	c.anchor = GridBagConstraints.NORTHWEST;
	c.fill = GridBagConstraints.BOTH;
	c.gridwidth = 4;
	c.gridheight = 4;
	c.weightx = 1.0;
	c.weighty = 1.0;
	layout.setConstraints(parent.messages, c);
	parent.messages.setEditable(false);
	add(parent.messages);

	Panel buttonPanel = new Panel();
	buttonPanel.setLayout(new GridLayout(1, 3));
	b = new Button("Clear");
	b.setActionCommand("doClear");
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

	c = new GridBagConstraints();
	c.insets = new Insets(0, 10, 5, 10);
	c.gridx = 0;
	c.gridwidth = 5;
	c.fill = GridBagConstraints.HORIZONTAL;
	layout.setConstraints(buttonPanel, c);
	add(buttonPanel);

	addWindowListener(this);
	
	pack();
	setSize(getPreferredSize());

	loadFile();

	show();
    }

    void loadFile()
    {
	text.setText("");

	UserFile file = prefs.getUserFile(prefs.getString("NoThanks.killfile"));
	InputStream in = null;
	
	try
	{
	    in = file.getInputStream();

	    BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    String s;
	    while ((s = br.readLine()) != null)
	    {
		text.append(s + "\n");
	    }
	    br.close();
	    in.close();
	    in = null;
	    text.setCaretPosition(0);
	}
	catch (FileNotFoundException e)
	{
	}
	catch (IOException e)
	{
	    System.out.println(e);
	}
	finally
	{
	    try
	    {
		if (in != null)
		{
		    in.close();
		}
	    }
	    catch (IOException e)
	    {
	    }
	}
	
    }

    void saveFile()
    {
	try
	{
	    UserFile file = prefs.getUserFile(prefs.getString("NoThanks.killfile"));
	    if (file instanceof LocalFile)
	    {
		LocalFile f = (LocalFile) file;
		f.delete();
		OutputStream out = file.getOutputStream();
		Writer writer = new OutputStreamWriter(out);
		writer.write(text.getText());
		writer.close();
		out.close();
	    }
	    else
	    {
		Dialog d = new ErrorDialog(this, "Can't save to " + file.getName());
		d.show();
		d.dispose();
	    }
	}
	catch (Exception e)
	{
	    System.out.println(e);
	}
    }

    public void actionPerformed(ActionEvent event)
    {
	String arg = event.getActionCommand();
	
	if ("doApply".equals(arg))
	{
	    prefs.putString("NoThanks.killfile", input.getText());
	    parent.load(new StringReader(text.getText()));
	}
	else if ("doSave".equals(arg))
	{
	    parent.save();
	    saveFile();
	}
	else if ("doLoad".equals(arg))
	{
	    parent.load();
	    loadFile();
	}
	else if ("doClear".equals(arg))
	{
	    parent.messages.clear();
	}
	else if ("doClose".equals(arg))
	{
	    setVisible(false);
	}
	else if ("doBrowse".equals(arg))
	{
	    FileDialog dialog = new FileDialog(this, "NoThanks Load");
	    dialog.show();
	    if (dialog.getFile() != null)
	    {
		input.setText(dialog.getDirectory() + dialog.getFile());
	    }
	}
	else if ("doHelp".equals(arg))
	{
	    new HelpFrame("NoThanks");
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
