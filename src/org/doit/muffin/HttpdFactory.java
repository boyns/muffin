/*
 * Copyright (C) 2003 Fabien Le Floc'h <fabien@31416.org>
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

import java.io.IOException;
import java.net.Socket;

/**
 * 
 * @author Fabien Le Floc'h
 */
public abstract class HttpdFactory
{
    private static HttpdFactory instance;
    private static Monitor monitor;
    private static Options options;
    private static FilterManager manager;

    public static synchronized void configure(Options options)
    {
        boolean useVelocity = false;
        if (options != null)
            useVelocity = options.getBoolean("muffin.velocity");
        if (useVelocity)
        {
            try
            {
                instance =
                    (HttpdFactory) Class
                        .forName("org.doit.muffin.webadmin.WebAdminHttpdFactory")
                        .newInstance();
            }
            catch (Throwable t)
            {
                instance = new Default();
            }
        }
        else
        {
            instance = new Default();
        }
    }

    public static HttpdFactory getFactory()
    {
        return instance;
    }

    public abstract HttpConnection createHttpd(Socket socket)
        throws IOException;

    public static boolean sendme(Request request)
    
    {
        return DefaultHttpd.sendme(request);
    }

    public static void init(Options o, FilterManager m, Monitor mon)
    {
        configure(o);
        monitor = mon;
        manager = m;
        options = o;
    }

    public static Options getOptions()
    {
        return options;
    }

    public static Monitor getMonitor()
    {
        return monitor;
    }

    public static FilterManager getManager()
    {
        return manager;
    }

    static class Default extends HttpdFactory
    {
        public HttpConnection createHttpd(Socket socket) throws IOException
        {
            return new DefaultHttpd(socket);
        }
    }

}
