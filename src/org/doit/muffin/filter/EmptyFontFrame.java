/* $Id: EmptyFontFrame.java,v 1.11 2006/10/28 19:12:40 forger77 Exp $ */

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

public class EmptyFontFrame extends AbstractFrame
{

    //	FIXME: use this constructor:
    //	public EmptyFontFrame(String name, FilterFactory parent) {
    /**
     * @see org.doit.muffin.filter.AbstractFrame#AbstractFrame(String, AbstractFilterFactory)
     */
    public EmptyFontFrame(AbstractFilterFactory parent)
    {
        super(parent);
    }

    /**
     * @see org.doit.muffin.filter.AbstractFrame#doMakeContent()
     */
    protected Panel doMakeContent()
    {

        Panel panel = new Panel(new BorderLayout());

        panel.add(BorderLayout.NORTH, makeDebugPanel());

        getFactory().getMessages().setEditable(false);
        panel.add(BorderLayout.CENTER, getFactory().getMessages());

        panel.add(BorderLayout.SOUTH, makeButtonPanel());

        return panel;

    }

    /**
     * Utility method that constructs the Panel containing the debug Button.
     * @return Panel The constructed Panel containing the debug Button.
     */
    private Panel makeDebugPanel()
    {
        Panel panel = new Panel();
        GridBagLayout layout = new GridBagLayout();
        panel.setLayout(layout);
        GridBagConstraints c;

        fDebug = new Checkbox(getFactory().getString(EmptyFont.DEBUG));
        fDebug.setState(getFactory().getPrefsBoolean(EmptyFont.DEBUG));

        c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        layout.setConstraints(fDebug, c);
        panel.add(fDebug);
        return panel;
    }

    /**
     * @see org.doit.muffin.filter.AbstractFrame#doMakeButtonList()
     */
    protected String[] doMakeButtonList()
    {
        return new String[] { APPLY_CMD, SAVE_CMD, CLEAR_CMD, CLOSE_CMD };
    }

    /**
     * @see org.doit.muffin.filter.AbstractFrame#doApply()
     */
    protected void doApply()
    {
        getFactory().putPrefsBoolean(EmptyFont.DEBUG, fDebug.getState());
    }

    private Checkbox fDebug;

}
