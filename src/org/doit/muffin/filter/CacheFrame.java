/*
 * Copyright (C) 1996-2000 Mark R. Boyns <boyns@doit.org>
 * Copyright (C) 2002 Doug Porter
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
 *
 * Based on code by Mark R. Boyns.
 */
package org.doit.muffin.filter;

import java.awt.*;
import java.awt.event.*;
import org.doit.muffin.*;
import org.doit.util.*;

public class CacheFrame
extends MuffinFrame
implements ActionListener, WindowListener
{
    static final String Name = "Cache";
    final String ApplyCommand = "doApply";
    final String SaveCommand = "doSave";
    final String CloseCommand = "doClose";
    final String HelpCommand = "doHelp";

    final int TextFieldSize = 50;

    private Prefs prefs;
    private FilterFactory factory;

    final String BrowseCacheDirCommand = "doBrowseCacheDir";
    final String BrowseRulesCommand = "doBrowseRules";

    private TextField cacheDirectory = null;
    private TextField patternsFilename = null;
    private TextArea patterns = null;
    private Checkbox checkForUpdatesOncePerSession = null;
    private Checkbox checkForUpdatesAlways = null;
    private Checkbox checkForUpdatesNever = null;

    public CacheFrame(Prefs prefs, Cache factory)
    {
	super(Strings.getString("Cache.title"));

	this.prefs = prefs;
	this.factory = factory;

	add("North", getNorthPanel ());
	add("Center", getCenterPanel ());
        add("South", getSouthPanel ());

	addWindowListener(this);

	pack();
	setSize(getPreferredSize());
	show();
    }

    public Prefs getPrefs () {
        return prefs;
    }

    public FilterFactory getFactory () {
        return factory;
    }

    protected Panel getNorthPanel () {

	Panel panel = new Panel();
	GridBagLayout layout = new GridBagLayout();
        panel.setLayout(layout);

        // this blank row is needed to keep the first real row from being trimmed from the display area
        addBlankRow (panel, layout);

        Label cacheDirectoryLabel = new Label(Strings.getString("Cache.cacheDirectory")+":", Label.RIGHT);
	layout.setConstraints(cacheDirectoryLabel, getGridBagNext ());
	panel.add(cacheDirectoryLabel);

	cacheDirectory = new TextField (TextFieldSize);
	cacheDirectory.setText (getCache(). getCacheDirectory ());
	layout.setConstraints(cacheDirectory, getGridBagEndRow ()); // getGridBagNext ());
	panel.add(cacheDirectory);

        /* !!!!! java.awt does not give a good way to browse for a dir
	Button browseCacheDirectory = new Button("Browse...");
	browseCacheDirectory.setActionCommand(BrowseCacheDirCommand);
	browseCacheDirectory.addActionListener(this);
	layout.setConstraints(browseCacheDirectory, getGridBagEndRow ());
	panel.add(browseCacheDirectory);
        */

        CheckboxGroup checkRadioButtons = new CheckboxGroup ();
        Panel checkPanel = new Panel ();
        checkPanel.setLayout(new FlowLayout ());
        checkPanel.add (new Label (Strings.getString("Cache.checkForUpdates")+":", Label.RIGHT));

	checkForUpdatesOncePerSession = new Checkbox (Strings.getString("Cache.checkForUpdates.oncePerSession"),
                                                      checkRadioButtons,
                                                      getCache(). checkForUpdatesOncePerSession ());
        checkPanel.add (checkForUpdatesOncePerSession);
	checkForUpdatesAlways = new Checkbox (Strings.getString("Cache.checkForUpdates.always"),
                                              checkRadioButtons,
                                              getCache(). checkForUpdatesAlways ());
        checkPanel.add (checkForUpdatesAlways);
	checkForUpdatesNever = new Checkbox (Strings.getString("Cache.checkForUpdates.never"),
                                             checkRadioButtons,
                                             getCache(). checkForUpdatesNever ());
        checkPanel.add (checkForUpdatesNever);

	layout.setConstraints(checkPanel, getGridBagEndRow ());
	panel.add(checkPanel);

        /*
        Label noCacheFileLabel = new Label("\"Do not cache\" patterns file:", Label.RIGHT);
	layout.setConstraints(noCacheFileLabel, getGridBagNext ());
	panel.add(noCacheFileLabel);

	patternsFilename = new TextField(TextFieldSize);
	patternsFilename.setText(prefs.getString(Cache.NoCachePatterns));
	layout.setConstraints(patternsFilename, getGridBagNext ());
	panel.add(patternsFilename);

	Button browseNoCacheFile = new Button("Browse...");
	browseNoCacheFile.setActionCommand(BrowseRulesCommand);
	browseNoCacheFile.addActionListener(this);
	layout.setConstraints(browseNoCacheFile, getGridBagEndRow ()); // getGridBagAnchor (GridBagConstraints.SOUTHEAST));
	panel.add(browseNoCacheFile);
        */

        return panel;
    }

    protected Panel getCenterPanel () {

        /*
	Panel panel = new Panel();
	panel.setLayout(new GridLayout(2, 1));

        panel.add (getNoCachePanel ());
        panel.add (getMessageLogPanel ());

        return panel;
        */
        return getMessageLogPanel (getMessages());
    }

    protected Panel getNoCachePanel () {

	Panel panel = new Panel();
	GridBagLayout layout = new GridBagLayout();
        panel.setLayout(layout);
	GridBagConstraints c;

        addBlankRow (panel, layout);

	Label l = new Label(Strings.getString("Cache.noCachePatterns"));
	c = new GridBagConstraints();
	// c.insets = new Insets(0, 10, 5, 10);
	c.gridwidth = GridBagConstraints.REMAINDER;
	c.anchor = GridBagConstraints.CENTER;
	layout.setConstraints(l, c);
	panel.add(l);

	patterns = new TextArea();
	c = new GridBagConstraints();
	c.gridheight = 3;
	c.gridwidth = GridBagConstraints.REMAINDER;
	c.anchor = GridBagConstraints.CENTER;
	// c.insets = new Insets(0, 10, 5, 10);
	layout.setConstraints(patterns, c);
	patterns.setEditable(false);
	panel.add(patterns);

        return panel;
    }

    /** Return the South panel, a standard button panel.
     */
    protected Panel getSouthPanel () {

	Panel buttonPanel = new Panel();
	buttonPanel.setLayout(new GridLayout(1, 4));

	Button b;
	b = new Button(Strings.getString("apply"));
	b.setActionCommand(ApplyCommand);
	b.addActionListener(this);
	buttonPanel.add(b);
	b = new Button(Strings.getString("save"));
	b.setActionCommand(SaveCommand);
	b.addActionListener(this);
	buttonPanel.add(b);
	b = new Button(Strings.getString("close"));
	b.setActionCommand(CloseCommand);
	b.addActionListener(this);
	buttonPanel.add(b);
	b = new Button(Strings.getString("help"));
	b.setActionCommand(HelpCommand);
	b.addActionListener(this);
	buttonPanel.add(b);

        return buttonPanel;
    }

    protected Panel getMessageLogPanel (MessageArea messages) {

	Panel panel = new Panel();
	GridBagLayout layout = new GridBagLayout();
        panel.setLayout(layout);
	GridBagConstraints c;

        addBlankRow (panel, layout);

	Label l = new Label(Strings.getString("Cache.messages"));
	c = new GridBagConstraints();
	// c.insets = new Insets(0, 10, 5, 10);
	c.gridwidth = GridBagConstraints.REMAINDER;
	c.anchor = GridBagConstraints.CENTER;
	layout.setConstraints(l, c);
	panel.add(l);

	c = new GridBagConstraints();
	// c.insets = new Insets(0, 10, 5, 10);
	c.gridwidth = GridBagConstraints.REMAINDER;
	c.anchor = GridBagConstraints.CENTER;
	layout.setConstraints(messages, c);
	messages.setEditable(false);
	panel.add(messages);

        return panel;
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

    public void actionPerformed(ActionEvent event)
    {
	String arg = event.getActionCommand();
        // System.out.println ("action:" + arg); //DEBUG

	if (BrowseCacheDirCommand.equals(arg))
	{
            browseCacheDir ();
	}
        else if (ApplyCommand.equals(arg))
	{
            apply ();
	}
	else if (SaveCommand.equals(arg))
	{
            save();
	}
	else if (CloseCommand.equals(arg))
	{
            close ();
	}
	else if (HelpCommand.equals(arg))
	{
            help ();
	}
    }

    private void browseCacheDir () {
        FileDialog dialog = new FileDialog(this, "Select cache directory");
        dialog.show();
        if (dialog.getFile() != null)
        {
            // System.out.println ("dialog.getDirectory(): " + dialog.getDirectory()); //DEBUG
            // System.out.println ("dialog.getFile(): " + dialog.getFile()); //DEBUG
            cacheDirectory.setText(dialog.getDirectory() + dialog.getFile());
        }
        // else System.out.println ("dialog is null"); //DEBUG
    }

    private void apply () {
        prefs.putString(Cache.CacheDirectory, cacheDirectory.getText());
        prefs.putBoolean(Cache.CheckForUpdatesOncePerSession, checkForUpdatesOncePerSession.getState ());
        prefs.putBoolean(Cache.CheckForUpdatesAlways, checkForUpdatesAlways.getState ());
        prefs.putBoolean(Cache.CheckForUpdatesNever, checkForUpdatesNever.getState ());
    }

    private void save () {
        apply ();
        getCache ().save();
    }

    private void close () {
	setVisible(false);
    }

    private void help () {
        new HelpFrame(Name);
    }

    public Insets getInsets () {
        return new Insets(0, 10, 5, 10);
    }

    private Cache getCache () {
        return (Cache) getFactory ();
    }

    private MessageArea getMessages() {
        return getCache ().getMessages ();
    }

    protected GridBagConstraints getGridBagNext () {
	GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        return c;
    }

    protected GridBagConstraints getGridBagEndRow () {
	GridBagConstraints c = getGridBagNext ();
	c.gridwidth = GridBagConstraints.REMAINDER;
        return c;
    }

    protected GridBagConstraints getGridBagEndColumn () {
	GridBagConstraints c = getGridBagNext ();
	c.gridheight = GridBagConstraints.REMAINDER;
        return c;
    }

    protected GridBagConstraints getGridBagAnchor (int anchor) {
	GridBagConstraints c = getGridBagNext ();
	c.anchor = GridBagConstraints.NORTHWEST;
        return c;
    }

    protected void addBlankRow (Panel panel,
                                GridBagLayout layout) {
        Label label = new Label(" ");
	GridBagConstraints c = getGridBagEndRow ();
        c.ipadx = 6;
        c.ipady = 6;
        c.weighty = 1.0;
	layout.setConstraints(label, c);
	panel.add(label);
    }

    protected void addSpacer (Panel panel,
                              GridBagLayout layout) {
        Label label = new Label(" ");
	GridBagConstraints c = getGridBagNext ();
        c.ipadx = 6;
        c.ipady = 6;
	layout.setConstraints(label, c);
	panel.add(label);
    }

}
