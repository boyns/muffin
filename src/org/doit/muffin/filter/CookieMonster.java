/* $Id: CookieMonster.java,v 1.6 2003/05/24 21:04:41 cmallwitz Exp $ */

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

import org.doit.muffin.*;

public class CookieMonster implements FilterFactory
{
    FilterManager manager;
    Prefs prefs;
    CookieMonsterFrame frame = null;
    MessageArea messages = null;

    public void setManager(FilterManager manager)
    {
        this.manager = manager;
    }

    public void setPrefs(Prefs prefs)
    {
        this.prefs = prefs;

        boolean o = prefs.getOverride();
        prefs.setOverride(false);
        prefs.putBoolean("CookieMonster.allowSessionCookies", true);
        prefs.putInteger("CookieMonster.expirePersistentCookiesInMinutes", 0);
        prefs.putBoolean("CookieMonster.filterReplyCookies", true);
        prefs.putBoolean("CookieMonster.filterRequestCookies", false);
        prefs.setOverride(o);

        messages = new MessageArea();
    }

    public Prefs getPrefs()
    {
        return prefs;
    }

    public void viewPrefs()
    {
        if (frame == null)
        {
            frame = new CookieMonsterFrame(prefs, this);
        }
        frame.setVisible(true);
    }

    public Filter createFilter()
    {
        Filter f = new CookieMonsterFilter(this);
        f.setPrefs(prefs);
        return f;
    }

    public void shutdown()
    {
        if (frame != null)
        {
            frame.dispose();
        }
    }

    void save()
    {
        manager.save(this);
    }

    void report(Request request, String message)
    {
        request.addLogEntry("CookieMonster", message);
        messages.append(message + "\n");
    }
}
