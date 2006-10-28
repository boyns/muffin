/**
 * SelectToRadio.java -- transforms html selects to radio buttons
 * 
 * A client of mine was complaining about the web client of his
 * issue tracking tool ("TestTrack Pro", http://www.seapine.com/ttpro.html).
 * There are lots of selects where radio buttons would be a better
 * ui, because all options would be visible at once.
 *
 * @author  Bernhard Wagner <muffinsrc@xmlizer.biz>
 * @version 0.1
 *
 * Last update: 06/10/06 B.W.
 */

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

public class SelectToRadioFrame extends AbstractFrame
{

    /**
     * @see org.doit.muffin.filter.AbstractFrame#AbstractFrame(AbstractFilterFactory)
     */
    public SelectToRadioFrame(SelectToRadio factory)
    {
        super(factory);
    }

    /**
     * @see org.doit.muffin.filter.AbstractFrame#doMakeContent()
     */
    protected Panel doMakeContent()
    {

        Panel panel = new Panel(new BorderLayout());

        panel.add(BorderLayout.NORTH, makeConfigPanel());

        getFactory().getMessages().setEditable(false);
        panel.add(BorderLayout.CENTER, getFactory().getMessages());

        panel.add(BorderLayout.SOUTH, makeButtonPanel());

        return panel;

    }

    /**
     * Utility method that constructs the config panel.
     * @return Panel The constructed config panel.
     */
    private Panel makeConfigPanel()
    {
        Panel panel = new Panel();
        GridBagLayout layout = new GridBagLayout();
        panel.setLayout(layout);

        fTitleLabel = new Label("Replace <SELECT> by <INPUT TYPE=\"RADIO\">");
        panel.add(fTitleLabel);
        fHorizontalCheckbox = new Checkbox(SelectToRadio.HORIZONTAL, true);
        fHorizontalCheckbox.setState(getFactory().getPrefsBoolean(SelectToRadio.HORIZONTAL));
        panel.add(fHorizontalCheckbox);

        return panel;
    }

    /**
     * 
     * @see org.doit.muffin.filter.AbstractFrame#doApply()
     */
    protected void doApply()
    {
        getFactory().putPrefsBoolean(SelectToRadio.HORIZONTAL, fHorizontalCheckbox.getState());
    }

    /**
     * @see org.doit.muffin.filter.AbstractFrame#doMakeButtonList()
     */
    protected String[] doMakeButtonList()
    {
        return new String[] {
            APPLY_CMD,
            SAVE_CMD,
            CLEAR_CMD,
            CLOSE_CMD,
            HELP_CMD };
    }

    private Label fTitleLabel;
    private Checkbox fHorizontalCheckbox;

}
