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
import org.doit.muffin.*;

public class DecafFrame extends AbstractFrame
{

    /**
     * @see org.doit.muffin.filter.AbstractFrame#AbstractFrame(AbstractFilterFactory)
     */
    public DecafFrame(Decaf factory)
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
    
    private Panel makeConfigPanel()
    {
        Panel panel = new Panel();
        GridBagLayout layout = new GridBagLayout();
        panel.setLayout(layout);
        GridBagConstraints c;
    
        noJavaScript = new Checkbox(getFactory().getString(Decaf.NOJAVASCRIPT));
        noJavaScript.setState(getFactory().getPrefsBoolean(Decaf.NOJAVASCRIPT));
        noJava = new Checkbox(getFactory().getString(Decaf.NOJAVA));
        noJava.setState(getFactory().getPrefsBoolean(Decaf.NOJAVA));
    
        c = new GridBagConstraints();
        layout.setConstraints(noJavaScript, c);
        panel.add(noJavaScript);
    
        c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        layout.setConstraints(noJava, c);
        panel.add(noJava);
        return panel;
    }

    /**
     * @see org.doit.muffin.filter.AbstractFrame#doApply()
     */
    protected void doApply(){
        getFactory().putPrefsBoolean(Decaf.NOJAVASCRIPT, noJavaScript.getState());
        getFactory().putPrefsBoolean(Decaf.NOJAVA, noJava.getState());
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

    
    private Decaf fFactory;
    private Checkbox noJava, noJavaScript;
}
