/* $Id: FilterManagerFrame.java,v 1.11 2003/06/07 23:24:56 forger77 Exp $ */

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

import java.awt.Button;
import java.awt.Color;
import java.awt.Choice;
import java.awt.Event;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.*;
import java.util.*;
import sdsu.compare.StringIgnoreCaseComparer;
import sdsu.util.SortedList;
import org.doit.util.*;

/**
 * GUI interface to the FilterManager.
 *
 * @see muffin.FilterManager
 * @author Mark Boyns
 */
class FilterManagerFrame
    extends MuffinFrame
    implements ActionListener, ItemListener, WindowListener, ConfigurationListener
{
    private FilterManager manager;
    private BigList supportedFiltersList = null;
    private BigList enabledFiltersList = null;
    private Choice configurationChoice = null;
    
    private static final String ENABLE_CMD = "doPerform";
    private static final String PREFS_CMD = "doPrefs";
    private static final String HELP_CMD = "doHelp";
    private static final String DISABLE_CMD = "doDisable";

    /**
     * Create the FilterManagerFrame.
     *
     * @param manager the filter manager
     */
    FilterManagerFrame(FilterManager manager)
    {
	super(Strings.getString("fm.title"));

	this.manager = manager;

	//setResizable(false);

	supportedFiltersList = new BigList(10, false);
    supportedFiltersList.addMouseListener(new MyMouseListener(ENABLE_CMD));
    supportedFiltersList.addKeyListener(new MyKeyListener());
	enabledFiltersList = new BigList(10, false);
    enabledFiltersList.addMouseListener(new MyMouseListener(PREFS_CMD));
    enabledFiltersList.addKeyListener(new MyKeyListener());

	Label l;
	Button b;
	GridBagConstraints c;
	Panel p;

	p = new Panel();
	l = new Label(Strings.getString("fm.config"));
	//l.setFont(new Font("Fixed", Font.BOLD, 12));
	p.add(l);

	configurationChoice = new Choice();
	updateConfigurationChoice();
	configurationChoice.addItemListener(this);
	manager.configs.addConfigurationListener(this);
	p.add(configurationChoice);
	add("North", p);

	Panel panel = new Panel();
	GridBagLayout layout = new GridBagLayout();
	panel.setLayout(layout);

	l = new Label(Strings.getString("fm.available"));
	//l.setFont(new Font("Fixed", Font.BOLD, 12));
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	layout.setConstraints(l, c);
	panel.add(l);

	c = new GridBagConstraints();
	c.gridheight = 4;
	c.insets = new Insets(0, 10, 5, 10);
	layout.setConstraints(supportedFiltersList, c);
	panel.add(supportedFiltersList);

	b = new Button(Strings.getString("fm.enable"));
	b.setActionCommand("doEnable");
	b.addActionListener(this);
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	c.anchor = GridBagConstraints.NORTHWEST;
	layout.setConstraints(b, c);
	panel.add(b);

	b = new Button(Strings.getString("fm.new"));
	b.setActionCommand("doNewFilter");
	b.addActionListener(this);
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	c.anchor = GridBagConstraints.NORTHWEST;
	layout.setConstraints(b, c);
	panel.add(b);

	b = new Button(Strings.getString("fm.delete"));
	b.setActionCommand("doDelete");
	b.addActionListener(this);
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	c.anchor = GridBagConstraints.NORTHWEST;
	layout.setConstraints(b, c);
	panel.add(b);

	b = new Button(Strings.getString("fm.help"));
	b.setActionCommand("doHelp");
	b.addActionListener(this);
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	c.anchor = GridBagConstraints.NORTHWEST;
	layout.setConstraints(b, c);
	panel.add(b);

	l = new Label(Strings.getString("fm.enabled"));
	//l.setFont(new Font("Fixed", Font.BOLD, 12));
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	layout.setConstraints(l, c);
	panel.add(l);

	c = new GridBagConstraints();
	c.gridheight = 4;
	c.insets = new Insets(0, 10, 5, 10);
	layout.setConstraints(enabledFiltersList, c);
	panel.add(enabledFiltersList);

	b = new Button(Strings.getString("fm.prefs"));
	b.setActionCommand("doPrefs");
	b.addActionListener(this);
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	c.anchor = GridBagConstraints.NORTHWEST;
	layout.setConstraints(b, c);
	panel.add(b);

	b = new Button(Strings.getString("fm.up"));
	b.setActionCommand("doUp");
	b.addActionListener(this);
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	c.anchor = GridBagConstraints.NORTHWEST;
	layout.setConstraints(b, c);
	panel.add(b);

	b = new Button(Strings.getString("fm.down"));
	b.setActionCommand("doDown");
	b.addActionListener(this);
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	c.anchor = GridBagConstraints.NORTHWEST;
	layout.setConstraints(b, c);
	panel.add(b);

	b = new Button(Strings.getString("fm.disable"));
	b.setActionCommand("doDisable");
	b.addActionListener(this);
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	c.anchor = GridBagConstraints.NORTHWEST;
	layout.setConstraints(b, c);
	panel.add(b);

	add("Center", panel);

	Panel buttonPanel = new Panel();
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

	updateSupportedFiltersList();
	updateEnabledFiltersList();

 	setSize(getPreferredSize());
 	pack();
    }

    public void configurationChanged(String name)
    {
	updateSupportedFiltersList();
	updateEnabledFiltersList();
	updateConfigurationChoice();
    }

    void updateConfigurationChoice()
    {
	configurationChoice.removeAll();
	Enumeration e = manager.configs.sortedKeys();
	while (e.hasMoreElements())
	{
	    String name = (String) e.nextElement();
	    configurationChoice.addItem(name);
	}
	configurationChoice.select(manager.configs.getCurrent());
    }

    /**
     * Update the list of supported filters on the screen.
     */
    void updateSupportedFiltersList()
    {
	if (supportedFiltersList.getItemCount() > 0)
	{
	    supportedFiltersList.removeAll();
	}
	Enumeration e = manager.supportedFilters.elements();
	SortedList sorter = new SortedList(StringIgnoreCaseComparer.getInstance());
	while (e.hasMoreElements())
	{
	    sorter.addElement((String) e.nextElement());
	}
	e = sorter.elements();
 	while (e.hasMoreElements())
 	{
	    String s = (String) e.nextElement();
	    supportedFiltersList.add(s);
	}
    }

    /**
     * Update the list of enabled filters on the screen.
     */
    void updateEnabledFiltersList()
    {
	if (enabledFiltersList.getItemCount() > 0)
	{
	    enabledFiltersList.removeAll();
	}
	Enumeration e = manager.enabledFilters.elements();
 	while (e.hasMoreElements())
 	{
	    FilterFactory ff = (FilterFactory) e.nextElement();
	    enabledFiltersList.add(manager.shortName((ff.getClass()).getName()));
	}
    }

    /**
     * Hide/show this frame.
     */
    void hideshow()
    {
	if (isShowing())
	{
	    setVisible(false);
	}
	else
	{
	    show();
	}
    }

    /**
     * Tell the manager to enable a filter.
     *
     * @param clazz the java filter class name
     */
    void enable(String clazz)
    {
	manager.enable(clazz);
	updateEnabledFiltersList();
    }

    void disable(int i)
    {
	manager.disable(i);
	updateEnabledFiltersList();
    }

    /**
     * View the preferences for a filter.
     *
     * @param clazz the java filter class name
     */
    void viewPrefs(String clazz)
    {
	Enumeration e = manager.enabledFilters.elements();
	while (e.hasMoreElements())
	{
	    FilterFactory f = (FilterFactory) e.nextElement();
	    if (clazz.equals(manager.shortName((f.getClass()).getName())))
	    {
		f.viewPrefs();
	    }
	}
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
	else if ("doSave".equals(arg))
	{
	    manager.save();
	}
	else if (ENABLE_CMD.equals(arg))
	{
	    int i = supportedFiltersList.getSelectedIndex();
	    if (i != -1)
	    {
		enable(supportedFiltersList.getItem(i));
	    }
	}
	else if (DISABLE_CMD.equals(arg))
	{
	    int i = enabledFiltersList.getSelectedIndex();
	    if (i != -1)
	    {
		disable(i);
	    }
	}
	else if ("doNewFilter".equals(arg))
	{
	    TextDialog dialog = new TextDialog(this, Strings.getString("fm.new.prompt") + ":");
	    dialog.show();
	    String name = dialog.getAnswer();
	    if (name != null && name.length() > 0)
	    {
		name.trim();
		manager.append(name);
	    }
	    dialog.dispose();
	}
	else if ("doDelete".equals(arg))
	{
	    int i = supportedFiltersList.getSelectedIndex();
	    if (i != -1)
	    {
		manager.remove(supportedFiltersList.getItem(i));
	    }
	}
	else if (HELP_CMD.equals(arg))
	{
	    int i = supportedFiltersList.getSelectedIndex();
	    if (i != -1)
	    {
		new HelpFrame(supportedFiltersList.getItem(i));
	    }
	}
	else if (PREFS_CMD.equals(arg))
	{
	    int i = enabledFiltersList.getSelectedIndex();
	    if (i != -1)
	    {
		viewPrefs(enabledFiltersList.getItem(i));
	    }
	}
	else if ("doUp".equals(arg))
	{
	    int i = enabledFiltersList.getSelectedIndex();
	    if (i > 0)
	    {
		Object prev = manager.enabledFilters.elementAt(i-1);
		Object curr = manager.enabledFilters.elementAt(i);
		manager.enabledFilters.setElementAt(curr, i-1);
		manager.enabledFilters.setElementAt(prev, i);
		updateEnabledFiltersList();
		enabledFiltersList.select(i-1);
	    }
	}
	else if ("doDown".equals(arg))
	{
	    int i = enabledFiltersList.getSelectedIndex();
	    if (i != -1 && i < manager.enabledFilters.size() - 1)
	    {
		Object next = manager.enabledFilters.elementAt(i+1);
		Object curr = manager.enabledFilters.elementAt(i);
		manager.enabledFilters.setElementAt(curr, i+1);
		manager.enabledFilters.setElementAt(next, i);
		updateEnabledFiltersList();
		enabledFiltersList.select(i+1);
	    }
	}
    }

    public void itemStateChanged(ItemEvent e)
    {
	manager.configs.setCurrent(e.getItem().toString());
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
    
    class MyMouseListener extends MouseAdapter
    {
        
        MyMouseListener(String cmd)
        {
            fCmd = cmd;
        }
        /**
         * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
         */
        public void mouseClicked(MouseEvent event)
        {
            if(event.getClickCount() > 1)
            {
                actionPerformed(new ActionEvent(this, 0, fCmd));
            }
        }
        
        private String fCmd;
    }
    
    class MyKeyListener extends KeyAdapter {
        public void keyPressed(KeyEvent e){
            int keyCode = e.getKeyCode();
            ActionEvent ae = (ActionEvent)gcActionMap.get(new Integer(keyCode));
            if(ae != null) actionPerformed(ae);
        }
    }
    
    private static Map gcActionMap = new HashMap();
    static {
        Object dummy = new Object();
        ActionEvent enable  = new ActionEvent(dummy, 0, ENABLE_CMD);
        ActionEvent disable = new ActionEvent(dummy, 0, DISABLE_CMD);
        ActionEvent help    = new ActionEvent(dummy, 0, HELP_CMD);
        gcActionMap.put(new Integer(KeyEvent.VK_INSERT),     enable);
        gcActionMap.put(new Integer(KeyEvent.VK_ENTER),      enable);
        gcActionMap.put(new Integer(KeyEvent.VK_DELETE),     disable);
        gcActionMap.put(new Integer(KeyEvent.VK_BACK_SPACE), disable);
        gcActionMap.put(new Integer(KeyEvent.VK_F1),         help);
    }
}
