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

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import javax.net.ssl.SSLSocketFactory;

import org.doit.muffin.SocketCreator;

import com.sun.net.ssl.SSLContext;

/**
 * @author Fabien Le Floc'h
 */
public class SSLSocketCreator extends SocketCreator
{
	
	private SSLSocketFactory factory;
	
	public SSLSocketCreator(SSLContext context)
	{
		factory = context.getSocketFactory();
	}
	
    /**
     * @see org.doit.muffin.SocketCreator#createSocket(InetAddress, int)
     */
    public Socket createSocket(InetAddress address, int port)
        throws IOException
    {
        return factory.createSocket(address,port);
    }

}
