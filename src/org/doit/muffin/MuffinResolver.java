/* $Id: MuffinResolver.java,v 1.5 2003/06/27 21:51:47 flefloch Exp $ */

/*
 * Copyright (C) 1996-2003 Mark R. Boyns <boyns@doit.org>
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
package org.doit.muffin;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

import org.doit.util.InetAddressHelper;
import org.xbill.DNS.Address;
import org.xbill.DNS.ExtendedResolver;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.dns;

/**
 * @author Mark R. Boyns
 * @author Fabien Le Floc'h
 */

public class MuffinResolver
{
    private static Resolver resolver = null;

    /**
     * @param servers a list of name server to use
     * @param useXbillDns if true and servers is empty then we will
     * still use xbill but name servers will be retrieved from the system.
     */
    static void init(String[] servers, boolean useXbillDns)
    {
        resolver = null;

        try
        {
            if (servers != null && servers.length > 0)
            {
                resolver = new ExtendedResolver(servers);
                dns.setResolver(resolver);
            }
            else if (useXbillDns) 
            {
                resolver = new ExtendedResolver();
                dns.setResolver(resolver);
            }
        }
        catch (UnknownHostException uhe)
        {
            uhe.printStackTrace();
        }
    }

    public static InetAddress getByName(String host)
        throws UnknownHostException
    {
        InetAddress addr;

        addr = convertObscureAddress(host);

        if (addr == null)
        {
            if (resolver == null)
            {
                // use the system resolver
                addr = InetAddress.getByName(host);
            }
            else
            {
                // use dnsjava
                addr = Address.getByName(host);
            }
        }

        return addr;
    }


	
    // check if the given address is double-word decimal
    // or hexadecimal
    // unfortunately JDK 1.4- are not interpreting "obscure" ips.
    private static InetAddress convertObscureAddress(String host)
        throws UnknownHostException
    {
        InetAddress addr = null;
        if (Character.isDigit(host.charAt(0)))
        {
            byte[] b = new byte[4];

            StringTokenizer st = new StringTokenizer(host, ".");
            int segmentsNumber = st.countTokens();
            try
            {
                if (segmentsNumber == 1)
                {
                    //check if this is double-word decimal address
                    long decimalAddress = convertSegment(host);

                    b[0] = (byte) ((decimalAddress) >> 24);
                    b[1] = (byte) ((decimalAddress % 16777216) >> 16);
                    b[2] = (byte) ((decimalAddress % 65536) >> 8);
                    b[3] = (byte) ((decimalAddress % 256));
                    addr = InetAddressHelper.getByAddress(b);
                }
                else if (segmentsNumber == 4)
                {
                    for (int i = 0; i < 4; i++)
                    {
                        b[i] = (byte) (convertSegment(st.nextToken()) % 256);
                    }

                    addr = InetAddressHelper.getByAddress(b);
                }
            }
            catch (NumberFormatException nfe)
            {
                //don't do anything, this is just not a decimal ip	
            }
        }
        return addr;
    }

    private static long convertSegment(String segment)
        throws NumberFormatException
    {
        int l = segment.length();
        long segmentLong;
        if ((l > 1) && (segment.charAt(0) == '0'))
        {
            if ((l > 2) && (segment.charAt(1) == 'x'))
            {
                segmentLong = Long.parseLong(segment.substring(2), 16);
            }
            else
            {
                segmentLong = Long.parseLong(segment.substring(1), 8);
            }
        }
        else
        {
            segmentLong = Long.parseLong(segment);
        }
        return segmentLong;
    }
}
