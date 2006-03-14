/* $Id: ConfigurationFrame.java,v 1.10 2006/03/14 17:00:04 flefloch Exp $ */

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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Enumeration;
import java.io.*;
import org.doit.util.*;

/**
 * @author Mark Boyns
 */
class ConfigurationFrame extends MuffinFrame
    implements ActionListener, ItemListener, WindowListener, ConfigurationListener
{
    BigList configNamesList = null;
    Configuration configs = null;
    TextArea text = null;

    ConfigurationFrame(Configuration configs)
    {
	super(Strings.getString("config.title"),250,350);

	this.configs = configs;

	//setResizable(false);

	configNamesList = new BigList(10, false);

	Label l;
	Button b;
	GridBagConstraints c;

	Panel panel = new Panel( new GridBagLayout() );

	Label currentLabel = new Label();
	//currentLabel.setFont(new Font("Fixed", Font.PLAIN, 12));
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	panel.add(currentLabel, c);

	l = new Label(Strings.getString("config.available"));
	//l.setFont(new Font("Fixed", Font.BOLD, 12));
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	panel.add(l, c);

	c = new GridBagConstraints();
	c.gridheight = 4;
	c.insets = new Insets(0, 10, 5, 10);
	c.weightx = c.weighty = 1;
	c.fill = GridBagConstraints.BOTH;
	panel.add(configNamesList, c);
	
	GridBagConstraints bc = new GridBagConstraints();
	bc.gridwidth = GridBagConstraints.REMAINDER;
	bc.anchor = GridBagConstraints.NORTHWEST;
	bc.insets = new Insets( 0, 0, 1, 10 );
	bc.fill = GridBagConstraints.HORIZONTAL;

	b = new Button(Strings.getString("config.new"));
	b.setActionCommand("doNew");
	b.addActionListener(this);
	panel.add(b, bc);

	b = new Button(Strings.getString("config.select"));
	b.setActionCommand("doSelect");
	b.addActionListener(this);
	panel.add(b, bc);

	b = new Button(Strings.getString("config.scan"));
	b.setActionCommand("doRescan");
	b.addActionListener(this);
	panel.add(b, bc);

	b = new Button(Strings.getString("config.delete"));
	b.setActionCommand("doDelete");
	b.addActionListener(this);
	panel.add(b, bc);

	l = new Label(Strings.getString("config.auto"));
	//l.setFont(new Font("Fixed", Font.BOLD, 12));
	c = new GridBagConstraints();
	panel.add(l, c);

	b = new Button(Strings.getString("config.example"));
	b.setActionCommand("doExample");
	b.addActionListener(this);
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	c.insets = bc.insets;
	panel.add(b, c);

	text = new TextArea(10, 40);
	text.setEditable(true);
	//text.setFont(new Font("Fixed", Font.PLAIN, 12));
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	c.insets = new Insets(0, 10, 5, 10);
	c.weightx = c.weighty = 1;
	c.fill = GridBagConstraints.BOTH;
	panel.add(text, c);

	add("Center", panel);

	Panel buttonPanel = new Panel();
	b = new Button(Strings.getString("apply"));
	b.setActionCommand("doApply");
	b.addActionListener(this);
	buttonPanel.add(b);
	b = new Button(Strings.getString("reload"));
	b.setActionCommand("doReload");
	b.addActionListener(this);
	buttonPanel.add(b);
	b = new Button(Strings.getString("save"));
	b.setActionCommand("doSave");
	b.addActionListener(this);
	buttonPanel.add(b);
	b = new Button(Strings.getString("close"));
	b.setActionCommand("doClose");
	b.addActionListener(this);
	buttonPanel.add(b);
	add("South", buttonPanel);

	addWindowListener(this);
	updateConfigNamesList();
	loadAutoConfigFile();

	configs.addConfigurationListener(currentLabel);
	configs.addConfigurationListener(this);

 	setSize(getPreferredSize());
 	pack();
    }

    public void configurationChanged(String name)
    {
	updateConfigNamesList();
    }

    void loadAutoConfigFile()
    {
	text.setText("");

	UserFile file = configs.getAutoConfigFile();

	try
	{
	    BufferedReader in = new BufferedReader
		(new InputStreamReader(file.getInputStream()));
	    String s;
	    while ((s = in.readLine()) != null)
	    {
		text.append(s + "\n");
	    }
	    in.close();
	}
	catch (IOException e)
	{
	    System.out.println(e);
	}
    }

    void updateConfigNamesList()
    {
	if (configNamesList.getItemCount() > 0)
	{
	    configNamesList.removeAll();
	}

	Enumeration e = configs.sortedKeys();
	while (e.hasMoreElements())
	{
	    String name = (String) e.nextElement();
	    configNamesList.addItem(name); // DEPRECATION: use add()
	}
    }

    public void itemStateChanged(ItemEvent event)
    {
    }

    /**
     * Handle button events.
     *
     * @param event some event
     */
    public void actionPerformed(ActionEvent event)
    {
	String arg = event.getActionCommand();

	if ("doClose".equals(arg))
	{
	    setVisible(false);
	}
	else if ("doNew".equals(arg))
	{
	    TextDialog dialog = new TextDialog(this, Strings.getString("config.new.name") + ":");
	    dialog.show();
	    String name = dialog.getAnswer();
	    if (name != null && name.length() > 0)
	    {
		name.trim();
		configs.createConfig(name);
	    }
	    dialog.dispose();
	}
	else if ("doDelete".equals(arg))
	{
	    int i = configNamesList.getSelectedIndex();
	    if (i != -1)
	    {
		String name = configNamesList.getItem(i);
		if (configs.delete(name))
		{
		    updateConfigNamesList();

		    YesNoDialog dialog = new YesNoDialog(this, Strings.getString("config.delete.file", name));
		    dialog.show();
		    if (dialog.isYes())
		    {
			configs.deleteUserConfigFile(name);
		    }
		    dialog.dispose();
		}
	    }
	}
	else if ("doSelect".equals(arg))
	{
	    int i = configNamesList.getSelectedIndex();
	    if (i != -1)
	    {
		configs.setCurrent(configNamesList.getItem(i));
	    }
	}
	else if ("doRescan".equals(arg))
	{
	    configs.rescan();
	    updateConfigNamesList();
	}
	else if ("doReload".equals(arg))
	{
	    configs.reload();
	}
	else if ("doApply".equals(arg))
	{
	    configs.load(new StringReader(text.getText()));
	}
	else if ("doSave".equals(arg))
	{
	    try
	    {
		UserFile file = configs.getAutoConfigFile();
		if (file instanceof LocalFile)
		{
		    LocalFile f = (LocalFile) file;
		    f.delete();
		    OutputStreamWriter writer =
			new OutputStreamWriter(f.getOutputStream());
		    writer.write(text.getText());
		    writer.close();
		}
		else
		{
                    Dialog d = new ErrorDialog(this, Strings.getString("config.error.save", file.getName()));
		    d.show();
		    d.dispose();
		}
	    }
	    catch (IOException e)
	    {
		System.out.println(e);
	    }
	}
	else if ("doExample".equals(arg))
	{
	    text.setText("counter\t\t\tcounter.conf\n" +
			  "(cgi-bin|\\.cgi)\t\tcgi.conf\n" +
			  "^http://.*\\.sdsu\\.edu/\t\tsdsu.conf\n" +
			  "^http://.*\\.yahoo\\.com/\tyahoo.conf\n");
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
