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

/**
 * 
 * @author Fabien Le Floc'h
 */
public abstract class HttpErrorFactory
{
    private static HttpErrorFactory instance;

    public static synchronized void init(Options options)
    {
        String webadmin = null;
        if (options != null)
            webadmin = options.getString("muffin.webadmin");
        try
        {
            if ("velocity".equals(webadmin))
            {
                instance =
                    (HttpErrorFactory) Class
                        .forName("org.doit.muffin.webadmin.velocity.VelocityHttpErrorFactory")
                        .newInstance();

            }
            else if ("webmacro".equals(webadmin))
            {
                instance =
                    (HttpErrorFactory) Class
                        .forName("org.doit.muffin.webadmin.webmacro.WebMacroHttpErrorFactory")
                        .newInstance();
            }
            else
            {
                instance = new Default();
            }
        }
        catch (Throwable t)
        {
            instance = new Default();
        }
    }

    public static HttpErrorFactory getFactory()
    {
        return instance;
    }
    public abstract HttpError createError(int code, String message);

    public abstract HttpError createError(int code, Exception e);

    public static class Default extends HttpErrorFactory
    {
        public HttpError createError(int code, String message)
        {
            return new DefaultHttpError(code, message);
        }

        public HttpError createError(int code, Exception e)
        {
            return new DefaultHttpError(code, e.toString());
        }
    }
}
