/* $Id: RewriteFrame.java,v 1.7 2000/03/29 15:15:03 boyns Exp $ */

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

public class RewriteFrame extends MuffinFrame implements ActionListener, WindowListener
{
    Prefs prefs;
    Rewrite parent;
    TextField input = null;
    TextArea text = null;
    
    public RewriteFrame(Prefs prefs, Rewrite parent)
    {
	super("Muffin: Rewrite");

	this.prefs = prefs;
	this.parent = parent;

	Panel panel = new Panel();
	GridBagLayout layout = new GridBagLayout();
        panel.setLayout(layout);
	GridBagConstraints c;
	
	panel.add(new Label("Rules File:", Label.RIGHT));

	input = new TextField(40);
	input.setText(prefs.getString("Rewrite.rules"));
	panel.add(input);

	Button browse = new Button("Browse...");
	browse.setActionCommand("doBrowse");
	browse.addActionListener(this);
	c = new GridBagConstraints();
	c.anchor = GridBagConstraints.NORTHWEST;
	layout.setConstraints(browse, c);
	panel.add(browse);

	add("North", panel);

	panel = new Panel();
	layout = new GridBagLayout();
        panel.setLayout(layout);

	Label l = new Label("Rules");
	c = new GridBagConstraints();
	c.insets = new Insets(0, 10, 5, 10);
	c.gridwidth = GridBagConstraints.REMAINDER;
	c.anchor = GridBagConstraints.NORTHWEST;
	layout.setConstraints(l, c);
	panel.add(l);
	
	text = new TextArea();
	c = new GridBagConstraints();
	c.gridheight = 3;
	c.insets = new Insets(0, 10, 5, 10);
	layout.setConstraints(text, c);
	panel.add(text);

	Button b;
	b = new Button("Apply");
	b.setActionCommand("doApply");
	b.addActionListener(this);
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	c.anchor = GridBagConstraints.NORTHWEST;
	layout.setConstraints(b, c);
	panel.add(b);

	b = new Button("Load");
	b.setActionCommand("doLoad");
	b.addActionListener(this);
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	c.anchor = GridBagConstraints.NORTHWEST;
	layout.setConstraints(b, c);
	panel.add(b);

	b = new Button("Save");
	b.setActionCommand("doSave");
	b.addActionListener(this);
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	c.anchor = GridBagConstraints.NORTHWEST;
	layout.setConstraints(b, c);
	panel.add(b);

	l = new Label("Message Log");
	c = new GridBagConstraints();
	c.insets = new Insets(0, 10, 5, 10);
	c.gridwidth = GridBagConstraints.REMAINDER;
	c.anchor = GridBagConstraints.NORTHWEST;
	layout.setConstraints(l, c);
	panel.add(l);
	
	c = new GridBagConstraints();
	c.insets = new Insets(0, 10, 5, 10);
	c.gridwidth = GridBagConstraints.REMAINDER;
	c.anchor = GridBagConstraints.NORTHWEST;
	layout.setConstraints(parent.messages, c);
	parent.messages.setEditable(false);
	panel.add(parent.messages);

	add("Center", panel);

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
	add("South", buttonPanel);

	addWindowListener(this);
	
	pack();
	setSize(getPreferredSize());

	loadFile();

	show();
    }

    void loadFile()
    {
	text.setText("");

	UserFile file = prefs.getUserFile(prefs.getString("Rewrite.rules"));
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
	    UserFile file = prefs.getUserFile(prefs.getString("Rewrite.rules"));
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
	    prefs.putString("Rewrite.rules", input.getText());
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
	    FileDialog dialog = new FileDialog(this, "Rewrite Load");
	    dialog.show();
	    if (dialog.getFile() != null)
	    {
		input.setText(dialog.getDirectory() + dialog.getFile());
	    }
	}
	else if ("doHelp".equals(arg))
	{
	    new HelpFrame("Rewrite");
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
