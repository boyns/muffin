/* $Id: Cookie.java,v 1.6 2003/05/24 21:04:41 cmallwitz Exp $ */

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
package org.doit.muffin;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

public class Cookie extends Hashtable
{
    // maybe the Hashtable is no longed needed but I'm not sure if I cover all possible cookie entries ...

    private String expires = null;
    private String domain  = null;
    private String path    = null;
    private String version = null;
    private String secure  = null;
    private String comment = null;
    private String maxage  = null;

    public Cookie(String cookie, Request request)
    {
        parse(cookie, request);
    }

    private void parse(String cookie, Request request)
    {
        StringTokenizer st = new StringTokenizer(cookie, ";");
        while (st.hasMoreTokens())
        {
            String token = st.nextToken();
            token = token.trim();
            String name;
            String value;
            int i = token.indexOf('=');
            if (i != -1)
            {
                name = token.substring(0, i);
                value = token.substring(i+1);
            }
            else
            {
                name = token;
                value = "";
            }

            if      ("expires".equalsIgnoreCase(name)) { expires = value; }
            else if ("domain". equalsIgnoreCase(name)) { domain  = value; }
            else if ("path".   equalsIgnoreCase(name)) { path    = value; }
            else if ("version".equalsIgnoreCase(name)) { version = value; }
            else if ("secure". equalsIgnoreCase(name)) { secure  = value; }
            else if ("comment".equalsIgnoreCase(name)) { comment = value; }
            else if ("max-age".equalsIgnoreCase(name)) { maxage  = value; }
            else
            {
                put(name, value);
            }
        }

        if (domain == null) { domain = request.getHost(); }
        if (path   == null) { path   = request.getPath(); }
    }

    public String getDomain()
    {
        return domain;
    }

    public String getPath()
    {
        return path;
    }

    public boolean compare(Request request)
    {
        return request.getHost().endsWith(getDomain())
            && request.getPath().startsWith(getPath());
    }

    public String getExpires()
    {
        return expires;
    }

    public void setExpires(String _expires)
    {
        expires = _expires;
    }

    public String toString()
    {
        StringBuffer buf = new StringBuffer();

        Enumeration e = keys();
        while (e.hasMoreElements())
        {
            if (buf.length() > 0)
            {
                buf.append("; ");
            }

            String key = (String) e.nextElement();
            String value = (String) get(key);

            buf.append(key);

            if (value.length() > 0)
            {
                buf.append("=");
                buf.append(value);
            }
        }

        if (expires != null) { buf.append("; expires=").append(expires); }
        if (domain  != null) { buf.append("; domain=") .append(domain);  }
        if (path    != null) { buf.append("; path=")   .append(path);    }
        if (version != null) { buf.append("; version=").append(version); }
        if (secure  != null) { buf.append("; secure=") .append(secure);  }
        if (comment != null) { buf.append("; comment=").append(comment); }
        if (maxage  != null) { buf.append("; max-age=").append(maxage);  }

        return buf.toString();
    }
}
