/*
 * Copyright (C) 2002-2003 Fabien Le Floc'h <fabien@31416.org>
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

import org.doit.muffin.Prefs;
import org.doit.muffin.RedirectFilter;
import org.doit.muffin.Request;

/**
 * @author Fabien Le Floc'h
 *
 */
public class ObscureFilter implements RedirectFilter
{

    Obscure _factory;
    Prefs _prefs;

    public ObscureFilter(Obscure factory)
    {
        _factory = factory;
    }

    /**
     * @see RedirectFilter#needsRedirection(Request)
     */
    public boolean needsRedirection(Request request)
    {
        return true;
    }

    public void setPrefs(Prefs prefs)
    {
        _prefs = prefs;
    }

    /**
     * @see RedirectFilter#redirect(Request)
     */
    public String redirect(Request request)
    {
        String orig = request.getURL();
        String url = _factory.rewrite(orig);
        if (_prefs.getBoolean("Obscure.doRedirect"))
        {
            if (!url.equals(orig))
            {
                return url;
            }
            else
            {
                return null;
            }
        }
        else
        {
            request.setURL(url);
            return null;
        }
    }
}