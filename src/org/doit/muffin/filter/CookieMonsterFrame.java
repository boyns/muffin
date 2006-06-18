/* $Id: CookieMonsterFrame.java,v 1.9 2006/06/18 23:25:51 forger77 Exp $ */

/*
 * Copyright (C) 1996-2000 Mark R. Boyns <boyns@doit.org>
 * Copyright (C) 2003 Christian Mallwitz <christian@mallwitz.com>
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
import org.doit.util.*;

public class CookieMonsterFrame extends MuffinFrame implements ActionListener, WindowListener
{
    /**
	 * Serializable class should define this:
	 */
	private static final long serialVersionUID = 1L;

    Prefs prefs;
    CookieMonster parent;
    Checkbox filterReply, filterRequest, allowSessionCookies;
    TextField expire = null;

    public CookieMonsterFrame(Prefs prefs, CookieMonster parent)
    {
        super(Strings.getString("CookieMonster.title"),750,200);

        this.prefs = prefs;
        this.parent = parent;

        // if new prefs have not been configured simulated them based on old prefs
        if (prefs.getString("CookieMonster.filterRequestCookies") == null &&
            prefs.getString("CookieMonster.filterResponseCookies") == null)
        {
            if (prefs.getBoolean("CookieMonster.eatRequestCookies"))
            {
                prefs.putBoolean("CookieMonster.filterRequestCookies", true);
            }

            if (prefs.getBoolean("CookieMonster.eatReplyCookies"))
            {
                prefs.putBoolean("CookieMonster.filterReplyCookies", true);
                prefs.putBoolean("CookieMonster.allowSessionCookies", false);
                prefs.putString("CookieMonster.expirePersistentCookiesInMinutes", "0");
            }
        }

        Panel panel;
        GridBagLayout layout;
        GridBagConstraints c;

        panel = new Panel();
        layout = new GridBagLayout();
        panel.setLayout(layout);

        filterRequest = new Checkbox(Strings.getString("CookieMonster.filterRequestCookies"));
        filterRequest.setState(prefs.getBoolean("CookieMonster.filterRequestCookies"));

        c = new GridBagConstraints();
        layout.setConstraints(filterRequest, c);
        panel.add(filterRequest);

        filterReply = new Checkbox(Strings.getString("CookieMonster.filterReplyCookies"));
        filterReply.setState(prefs.getBoolean("CookieMonster.filterReplyCookies"));

        c = new GridBagConstraints();
        layout.setConstraints(filterReply, c);
        panel.add(filterReply);

        allowSessionCookies = new Checkbox(Strings.getString("CookieMonster.allowSessionCookies"));
        allowSessionCookies.setState(prefs.getBoolean("CookieMonster.allowSessionCookies"));

        c = new GridBagConstraints();
        layout.setConstraints(allowSessionCookies, c);
        panel.add(allowSessionCookies);

        Label label = new Label(Strings.getString("CookieMonster.expire"), Label.RIGHT);
        c = new GridBagConstraints();
        // c.anchor = GridBagConstraints.WEST;
        layout.setConstraints(label, c);
        panel.add(label);

        expire = new TextField(4);
        expire.setText(prefs.getString("CookieMonster.expirePersistentCookiesInMinutes"));

        c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        // c.anchor = GridBagConstraints.WEST;
        layout.setConstraints(expire, c);
        panel.add(expire);

        add("North", panel);

        parent.messages.setEditable(false);
        //parent.messages.setFont(new Font("Fixed", Font.PLAIN, 10));
        add("Center", parent.messages);

        Button b;
        Panel buttonPanel = new Panel();
        buttonPanel.setLayout(new GridLayout(1, 5));
        b = new Button(Strings.getString("apply"));
        b.setActionCommand("doApply");
        b.addActionListener(this);
        buttonPanel.add(b);
        b = new Button(Strings.getString("save"));
        b.setActionCommand("doSave");
        b.addActionListener(this);
        buttonPanel.add(b);
        b = new Button(Strings.getString("clear"));
        b.setActionCommand("doClear");
        b.addActionListener(this);
        buttonPanel.add(b);
        b = new Button(Strings.getString("close"));
        b.setActionCommand("doClose");
        b.addActionListener(this);
        buttonPanel.add(b);
        b = new Button(Strings.getString("help"));
        b.setActionCommand("doHelp");
        b.addActionListener(this);
        buttonPanel.add(b);

        add("South", buttonPanel);

        addWindowListener(this);

        pack();
        setSize(getPreferredSize());

        show();
    }

    public void actionPerformed(ActionEvent event)
    {
        String arg = event.getActionCommand();

        if ("doApply".equals(arg))
        {
            prefs.putBoolean("CookieMonster.filterReplyCookies", filterReply.getState());
            prefs.putBoolean("CookieMonster.filterRequestCookies", filterRequest.getState());
            prefs.putBoolean("CookieMonster.allowSessionCookies", allowSessionCookies.getState());
            prefs.putString("CookieMonster.expirePersistentCookiesInMinutes", expire.getText());
        }
        else if ("doSave".equals(arg))
        {
            parent.save();
        }
        else if ("doClose".equals(arg))
        {
            setVisible(false);
        }
        else if ("doClear".equals(arg))
        {
            parent.messages.clear();
        }
        else if ("doHelp".equals(arg))
        {
            new HelpFrame("CookieMonster");
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
