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
package org.doit.muffin.decryption;

import org.doit.muffin.Client;
import org.doit.muffin.Request;

/**
 * @author Fabien Le Floc'h
 * 
 * Default port is 443.
 * Host and port are retrieved from headers instead
 * of URL. As this request content was forwarded to our
 * internal fake HTTPS server, we don't have an
 * absolute URL.
 */
public class DecryptionRequest extends Request
{
	private int defaultPort;
	
	{
		defaultPort = 443;	
	}
	
	DecryptionRequest(Client c)
	{
		super(c);		
	}
	
	public String getHost()
	{
        String s = getHeaderField("Host");
        if (s.indexOf(':') != -1)
        {
            return s.substring(0, s.indexOf(':'));
        }
		return s;
	}
	
	public int getPort()
	{
		int port = this.defaultPort;
		String s = getHeaderField("Host");
		if (s != null && s.indexOf(':') != -1)
        {

            s = s.substring(s.indexOf(':') + 1);
            try
            {
                port = Integer.parseInt(s);
            }
            catch (NumberFormatException e)
            {
                System.out.println("Invalid port in " + s);
            }
        }
		return port;
	}
	
	public String getPath()
	{
		return getURL();	
	}
}
