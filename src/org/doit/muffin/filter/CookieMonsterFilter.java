/* $Id: CookieMonsterFilter.java,v 1.6 2003/05/24 21:04:41 cmallwitz Exp $ */

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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class CookieMonsterFilter implements RequestFilter, ReplyFilter
{
    CookieMonster factory;
    Request request = null;

    boolean filterRequestCookies             = false;
    boolean filterReplyCookies               = false;
    boolean allowSessionCookies              = true;
    int     expirePersistentCookiesInMinutes = 0;

    SimpleDateFormat sdf;

    CookieMonsterFilter(CookieMonster factory)
    {
        this.factory = factory;

        sdf = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss 'GMT'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public void setPrefs(Prefs prefs)
    {
        if (prefs.getString("CookieMonster.filterRequestCookies") == null &&
            prefs.getString("CookieMonster.filterResponseCookies") == null)
        {
            // if new prefs have not been configured simulated them based on old prefs
            if (prefs.getBoolean("CookieMonster.eatRequestCookies"))
            {
                filterRequestCookies             = true;
            }

            if (prefs.getBoolean("CookieMonster.eatReplyCookies"))
            {
                filterReplyCookies               = true;
                allowSessionCookies              = false;
                expirePersistentCookiesInMinutes = 0;
            }
        }
        else
        {
            filterRequestCookies             = prefs.getBoolean("CookieMonster.filterRequestCookies");
            filterReplyCookies               = prefs.getBoolean("CookieMonster.filterReplyCookies");
            allowSessionCookies              = prefs.getBoolean("CookieMonster.allowSessionCookies");
            expirePersistentCookiesInMinutes = prefs.getInteger("CookieMonster.expirePersistentCookiesInMinutes");
        }
    }

    public void filter(Request r) throws FilterException
    {
        this.request = r;

        if (!filterRequestCookies) { return; }

        if (r.containsHeaderField("Cookie"))
        {
            factory.report(r, "cookie \"" + r.getHeaderField("Cookie") + "\"");
            r.removeHeaderField("Cookie");
        }
    }

    public void filter(Reply r) throws FilterException
    {
        if (!filterReplyCookies) { return; }

        if (r.containsHeaderField("Set-Cookie"))
        {
            for(int i = 0; i < r.getHeaderValueCount("Set-Cookie"); i++)
            {
                Cookie cookie = new Cookie(r.getHeaderField("Set-Cookie", i), request);

                if (cookie.getExpires() != null)
                {
                    if (expirePersistentCookiesInMinutes <= 0)
                    {
                        factory.report(request, "set-cookie         " + r.getHeaderField("Set-Cookie", i));
                        r.removeHeaderField("Set-Cookie", i); i--;
                    }
                    else
                    {
                        Calendar c = Calendar.getInstance();
                        c.add(Calendar.MINUTE, expirePersistentCookiesInMinutes);
                        cookie.setExpires(sdf.format(c.getTime()));
                        r.setHeaderField("Set-Cookie", cookie.toString(), i);
                        factory.report(request, "set-cookie expire  " + r.getHeaderField("Set-Cookie", i));
                    }
                }
                else if (!allowSessionCookies)
                {
                    factory.report(request, "set-cookie session " + r.getHeaderField("Set-Cookie", i));
                    r.removeHeaderField("Set-Cookie", i); i--;
                }
                else
                {
                    factory.report(request, "set-cookie allowed " + r.getHeaderField("Set-Cookie", i));
                }
            }
        }
    }
}
