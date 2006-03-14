/* $Id: FilterManagerFrame.java,v 1.13 2006/03/14 17:00:04 flefloch Exp $ */

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
import java.awt.event.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.doit.util.Strings;
import org.doit.util.TextDialog;

import sdsu.compare.StringIgnoreCaseComparer;
import sdsu.util.SortedList;

/**
 * GUI interface to the FilterManager.
 *
 * @see FilterManager
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
    
    private static final String CLOSE_CMD = "doClose";
    private static final String DELETE_CMD = "doDelete";
    private static final String DISABLE_CMD = "doDisable";
    private static final String DOWN_CMD = "doDown";
    private static final String ENABLE_CMD = "doPerform";
    private static final String HELP_CMD = "doHelp";
    private static final String NEW_CMD = "doNewFilter";
    private static final String PREFS_CMD = "doPrefs";
    private static final String SAVE_CMD = "doSave";
    private static final String UP_CMD = "doUp";

    private final static int MIN_WIDTH = 300;
    private final static int MIN_HEIGHT = 320;

    private final Button btnPrefs;
    private final Button btnUp;
    private final Button btnDown;
    private final Button btnDisable;

    /**
     * Create the FilterManagerFrame.
     *
     * @param manager the filter manager
     */
    FilterManagerFrame(FilterManager manager)
    {
	super(Strings.getString("fm.title"), MIN_WIDTH, MIN_HEIGHT);

	this.manager = manager;

	//setResizable(false);

	supportedFiltersList = new BigList(10, false);
    supportedFiltersList.addMouseListener(new MyMouseListener(ENABLE_CMD));
    supportedFiltersList.addKeyListener(new MyKeyListener());
	enabledFiltersList = new BigList(10, false);
    enabledFiltersList.addMouseListener(new MyMouseListener(PREFS_CMD));
    enabledFiltersList.addKeyListener(new MyKeyListener());

	enabledFiltersList.addItemListener( 
	    new ItemListener()
	    {
		public void itemStateChanged (ItemEvent e)
		{
		    enableEnabledFiltersListButtons( enabledFiltersList.getSelectedIndex() >= 0 );
		}
	    }
	);

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

	Panel panel = new Panel(new GridBagLayout());

	l = new Label(Strings.getString("fm.available"));
	//l.setFont(new Font("Fixed", Font.BOLD, 12));
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	panel.add(l, c);

	c = new GridBagConstraints();
	c.gridheight = 4;
	c.insets = new Insets(0, 10, 5, 10);
	c.weightx = c.weighty = 1;
	c.fill = GridBagConstraints.BOTH;
	panel.add(supportedFiltersList, c);
	
	GridBagConstraints bc = new GridBagConstraints();
	bc.gridwidth = GridBagConstraints.REMAINDER;
	bc.anchor = GridBagConstraints.NORTHWEST;
	bc.insets = new Insets( 0, 0, 1, 10 );
	bc.fill = GridBagConstraints.HORIZONTAL;

	b = new Button(Strings.getString("fm.enable"));
	b.setActionCommand(ENABLE_CMD);
	b.addActionListener(this);
	panel.add(b, bc);

	b = new Button(Strings.getString("fm.new"));
	b.setActionCommand(NEW_CMD);
	b.addActionListener(this);
	panel.add(b, bc);

	b = new Button(Strings.getString("fm.delete"));
	b.setActionCommand(DELETE_CMD);
	b.addActionListener(this);
	panel.add(b, bc);

	b = new Button(Strings.getString("fm.help"));
	b.setActionCommand(HELP_CMD);
	b.addActionListener(this);
	panel.add(b, bc);

	l = new Label(Strings.getString("fm.enabled"));
	//l.setFont(new Font("Fixed", Font.BOLD, 12));
	c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	panel.add(l, c);

	c = new GridBagConstraints();
	c.gridheight = 4;
	c.insets = new Insets(0, 10, 5, 10);
	c.weightx = c.weighty = 1;
	c.fill = GridBagConstraints.BOTH;
	panel.add(enabledFiltersList, c);

	btnPrefs = b = new Button(Strings.getString("fm.prefs"));
	b.setActionCommand(PREFS_CMD);
	b.addActionListener(this);
	panel.add(b, bc);

	btnUp = b = new Button(Strings.getString("fm.up"));
	b.setActionCommand(UP_CMD);
	b.addActionListener(this);
	panel.add(b, bc);

	btnDown = b = new Button(Strings.getString("fm.down"));
	b.setActionCommand(DOWN_CMD);
	b.addActionListener(this);
	panel.add(b, bc);

	btnDisable = b = new Button(Strings.getString("fm.disable"));
	b.setActionCommand(DISABLE_CMD);
	b.addActionListener(this);
	panel.add(b, bc);

	enableEnabledFiltersListButtons( false );

	add("Center", panel);

	Panel buttonPanel = new Panel();
	b = new Button(Strings.getString("save"));
	b.setActionCommand(SAVE_CMD);
	b.addActionListener(this);
	buttonPanel.add(b);
	b = new Button(Strings.getString("close"));
	b.setActionCommand(CLOSE_CMD);
	b.addActionListener(this);
	buttonPanel.add(b);
	add("South", buttonPanel);

	addWindowListener(this);

	updateSupportedFiltersList();
	updateEnabledFiltersList();

 	setSize(getPreferredSize());
 	pack();
    }

    private void enableEnabledFiltersListButtons( boolean enabled )
    {
	btnPrefs.setEnabled( enabled );
	btnUp.setEnabled( enabled );
	btnDown.setEnabled( enabled );
	btnDisable.setEnabled( enabled );
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

	if (CLOSE_CMD.equals(arg))
	{
	    setVisible(false);
	}
	else if (SAVE_CMD.equals(arg))
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
	else if (NEW_CMD.equals(arg))
	{
	    TextDialog dialog = new TextDialog(this, Strings.getString("fm.new.prompt") + ":");
	    dialog.show();
	    String name = dialog.getAnswer();
	    if (name != null && name.length() > 0)
	    {
		manager.append(name.trim());
	    }
	    dialog.dispose();
	}
	else if (DELETE_CMD.equals(arg))
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
	else if (UP_CMD.equals(arg))
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
	else if (DOWN_CMD.equals(arg))
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

	enableEnabledFiltersListButtons( enabledFiltersList.getSelectedIndex() >= 0 );
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
