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
package org.doit.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Fabien Le Floc'h
 */
public class InetAddressHelper
{
    private static InetAddressHelper impl;

    static {
        try
        {
            Class clazz =
                Class.forName("org.doit.muffin.util.Jdk14InetAddressHelper");
            impl = (InetAddressHelper) clazz.newInstance();
        }
        catch (Exception e)
        {
            impl = new InetAddressHelper();
        }
    }

    protected InetAddress getInetAddress(byte[] b) throws UnknownHostException
    {
        return InetAddress.getByName(
            new StringBuffer().append(b[0] & 0xFF)
                .append('.')
                .append(b[1] & 0xFF)
                .append('.')
                .append(b[2] & 0xFF)
                .append('.')
                .append(b[3] & 0xFF)
                .toString());
    }

    public static InetAddress getByAddress(byte[] b)
        throws UnknownHostException
    {
        //JDK 1.4 : return InetAddress.getByAddress(b);
        //JDK 1.3-: default InetAddress constructor is BAD BAD BAD
        return impl.getInetAddress(b);
    }
}
