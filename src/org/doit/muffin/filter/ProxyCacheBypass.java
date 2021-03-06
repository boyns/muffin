/* $Id: ProxyCacheBypass.java,v 1.1 2003/05/25 02:51:50 cmallwitz Exp $ */

/*
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

/*

  Insert "Pragma: no-cache" and "Cache-Control: no-cache" headers into
  HTTP requests. This forces any downstream proxies to ignore cached
  content, which is useful when the proxies are poorly configured and would
  return stale content otherwise. Of course this should be corrected by
  your friendly network admin...

  No configurable preferences.

 */

package org.doit.muffin.filter;

import org.doit.muffin.*;

public class ProxyCacheBypass implements FilterFactory
{
    FilterManager manager;
    Prefs prefs;

    public void setManager(FilterManager manager)
    {
        this.manager = manager;
    }

    public void setPrefs(Prefs prefs)
    {
        this.prefs = prefs;
    }

    public Prefs getPrefs()
    {
        return prefs;
    }

    public void viewPrefs()
    {
    }

    public Filter createFilter()
    {
        Filter f = new ProxyCacheBypassFilter(this);
        f.setPrefs(prefs);
        return f;
    }

    public void shutdown()
    {
    }
}
