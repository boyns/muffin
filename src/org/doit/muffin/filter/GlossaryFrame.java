/* $Id: GlossaryFrame.java,v 1.12 2006/03/14 17:00:03 flefloch Exp $ */

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
import org.doit.util.*;

public class GlossaryFrame extends AbstractFrame
{

    /**
     * @see org.doit.muffin.filter.AbstractFrame#AbstractFrame(AbstractFilterFactory)
     */
    public GlossaryFrame(AbstractFilterFactory parent)
    {
        super(parent);
    }

    /**
     * 
     * @see org.doit.muffin.filter.AbstractFrame#doMakeContent()
     */
    protected Panel doMakeContent()
    {

        Panel panel = new Panel(new BorderLayout());

        panel.add("Center", makeBrowseGui());

        panel.add("South", makeButtonPanel());

        return panel;

    }

    /**
     * Utility method that constructs the Panel containing the browsing GUI.
     * @return Panel The constructed Panel containing the browsing GUI.
     */
    private Panel makeBrowseGui()
    {
        Panel panel = new Panel();
        panel.setLayout(new GridBagLayout());

        panel.add(
            new Label(
                getFactory().getString(Glossary.GLOSSARY_FILE_KEY) + ":",
                Label.RIGHT));

        fInput = new TextField(50);
        fInput.setText(getFactory().getPrefsString(Glossary.GLOSSARY_FILE_KEY));
        panel.add(fInput);

        panel.add(makeButton(Strings.getString("browse") + "...", AbstractFrame.BROWSE_CMD));

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
            HELP_CMD };
    }

    /**
     * 
     * @see org.doit.muffin.filter.AbstractFrame#doMakeActions()
     */
    protected Action[] doMakeActions()
    {
        return new Action[] {
            new LoadAction(RELOAD_CMD),
            new BrowseAction("Glossary Load")
        };
    }

    /**
     * 
     * @see org.doit.muffin.filter.AbstractFrame#doApply()
     */
    protected void doApply()
    {
        getFactory().putPrefsString(
            Glossary.GLOSSARY_FILE_KEY,
            fInput.getText());
    }
    
    /**
     * @see org.doit.muffin.filter.AbstractFrame#doBrowse(String)
     */
    protected void doBrowse(String filename)
    {
        fInput.setText(filename);
    }

    private TextField fInput = null;
    protected final String RELOAD_CMD = "Glossary.reload";

}
