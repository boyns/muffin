/* $Id: MuffinResolver.java,v 1.2 2000/01/24 04:02:14 boyns Exp $ */

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

import org.xbill.DNS.*;
import java.util.*;
import java.net.*;

public class MuffinResolver
{
    private static Resolver resolver = null;

    static void init(String[] servers)
    {
	resolver = null;

	try
	{
	    if (servers != null && servers.length > 0)
	    {
		resolver = new ExtendedResolver(servers);
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

	return addr;
    }
}
