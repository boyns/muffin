/* $Id: NoThanksFrame.java,v 1.10 2003/06/07 10:44:13 forger77 Exp $ */

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

public class NoThanksFrame extends AbstractFrame
{
    /**
     * @see org.doit.muffin.filter.AbstractFrame#AbstractFrame(AbstractFilterFactory)
     */
    public NoThanksFrame(NoThanks factory)
    {
        super(factory);
        fFactory = factory;
    }


    /**
     * @see org.doit.muffin.filter.AbstractFrame#doMakeContent()
     */
    protected Panel doMakeContent()
    {

        Panel panel = new Panel(new BorderLayout());

        panel.add("North", makeConfigPanel());

        getFactory().getMessages().setEditable(false);
        panel.add("Center", getFactory().getMessages());

        panel.add("South", makeButtonPanel());

        return panel;

    }

    protected Panel makeConfigPanel(){
        
    Panel panel = new Panel();

	GridBagLayout layout = new GridBagLayout();
    panel.setLayout(layout);
	GridBagConstraints c;
	Label l;

	panel.add(new Label(getFactory().getString(NoThanks.KILLFILE)+":", Label.RIGHT));

	input = new TextField(40);
	input.setText(getFactory().getPrefsString(NoThanks.KILLFILE));
	c = new GridBagConstraints();
	c.anchor = GridBagConstraints.NORTHWEST;
	c.gridwidth = 2;
	layout.setConstraints(input, c);
	panel.add(input);

	Button browse = new Button(Strings.getString("browse")+"...");
	browse.setActionCommand(BROWSE_CMD);
	browse.addActionListener(this);
	c = new GridBagConstraints();
	c.anchor = GridBagConstraints.NORTHWEST;
	c.gridwidth = 1;//GridBagConstraints.RELATIVE;
	layout.setConstraints(browse, c);
	panel.add(browse);


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
	panel.add(text);

	l = new Label(Strings.getString("NoThanks.messages"));
	c = new GridBagConstraints();
	c.insets = new Insets(0, 10, 5, 10);
	c.gridx = 0;
	c.gridwidth = GridBagConstraints.REMAINDER;
	c.anchor = GridBagConstraints.NORTHWEST;
	layout.setConstraints(l, c);
	panel.add(l);

    return panel;

    }

    /**
     * 
     * @see org.doit.muffin.filter.AbstractFrame#doMakeButtonList()
     */
    protected String[] doMakeButtonList()
    {
        return new String[] {
            APPLY_CMD,
            SAVE_CMD,
            RELOAD_CMD,
            CLOSE_CMD,
            CLEAR_CMD,
            HELP_CMD };
    }

    void loadFile()
    {
	text.setText("");

    UserFile file =
        getFactory().getPrefs().getUserFile(getFactory().getPrefsString(NoThanks.KILLFILE));
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
        UserFile file =
            getFactory().getPrefs().getUserFile(getFactory().getPrefsString(NoThanks.KILLFILE));
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
		Dialog d = new ErrorDialog(getFrame(), "Can't save to " + file.getName());
		d.show();
		d.dispose();
	    }
	}
	catch (Exception e)
	{
	    System.out.println(e);
	}
    }
    
    /**     * @see org.doit.muffin.filter.AbstractFrame#doApply()     */
    protected void doApply(){
        getFactory().putPrefsString(NoThanks.KILLFILE, input.getText());
        fFactory.load(new StringReader(text.getText()));
    }
    
    /**
     * Since save also saves fFactory, we need to override the standard SaveAction
     */
    class SaveAction implements Action {
        /**
         * @see org.doit.muffin.filter.Action#perform()
         */
        public void perform()
        {
        doApply();
        fFactory.save();
        saveFile();
        }
        /**
         * @see org.doit.muffin.filter.Action#getName()
         */
        public String getName()
        {
            return AbstractFrame.SAVE_CMD;
        }
    }
    
    /**
     * Since load also loads fFactory, we need to override the standard LoadAction
     */
    class LoadAction implements Action {
        /**
         * @see org.doit.muffin.filter.Action#perform()
         */
        public void perform()
        {
        fFactory.doLoad();
        loadFile();
        }
        /**
         * @see org.doit.muffin.filter.Action#getName()
         */
        public String getName()
        {
            return RELOAD_CMD;
        }
    }


    /**
     * Action invoked when clicking the button
     * @see org.doit.muffin.filter.GlossaryFrame#BROWSE_CMD
     */
    class BrowseAction implements Action
    {
        public String getName()
        {
            return BROWSE_CMD;
        }
        public void perform()
        {
            FileDialog dialog = new FileDialog(getFrame(), "NoThanks Load");
            dialog.show();
            if (dialog.getFile() != null)
            {
                input.setText(dialog.getDirectory() + dialog.getFile());
            }
        }
    }

    /**
     * @see org.doit.muffin.filter.AbstractFrame#doMakeActions()
     */
    protected Action[] doOverrideActions()
    {
        return new Action[]{
            new SaveAction()
        };
    }

    /**
     * @see org.doit.muffin.filter.AbstractFrame#doMakeActions()
     */
    protected Action[] doMakeActions()
    {
        return new Action[]{
            new LoadAction(),
            new BrowseAction()
        };
    }

    protected static final String BROWSE_CMD = "browse";
    protected final String RELOAD_CMD = "NoThanks.reload";

    private NoThanks fFactory;
    private TextField input = null;
    private TextArea text = null;
}
