/* $Id: Strings.java,v 1.1 2003/01/08 18:59:53 boyns Exp $ */

/*
 * Copyright (C) 2003 Mark R. Boyns <boyns@doit.org>
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
package org.doit.util;

import java.text.*;
import java.util.*;

public class Strings
{
    private static Vector       bundles = new Vector();

    private Strings()
    {
    }

    public static void addBundle(ResourceBundle bundle)
    {
        bundles.addElement(bundle);
    }

    public static void removeBundle(ResourceBundle bundle)
    {
        bundles.removeElement(bundle);
    }

    public static String getString(String key)
    {
        ResourceBundle  bundle;

        for (Enumeration e = bundles.elements(); e.hasMoreElements(); )
        {
            bundle = (ResourceBundle)e.nextElement();

            try
            {
                return bundle.getString(key);
            }
            catch (MissingResourceException ex)
            {
            }
        }

        System.err.println(Strings.class + " key not found: " + key);

        return key;
    }

    public static String getString(String key, Object arg)
    {
        return MessageFormat.format(getString(key), new Object[]{arg});
    }

    public static String getString(String key, Object[] args)
    {
        return MessageFormat.format(getString(key), args);
    }
}
