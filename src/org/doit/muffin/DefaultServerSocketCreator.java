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
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Fabien Le Floc'h
 */
class DefaultServerSocketCreator implements ServerSocketCreator
{
	Prefs prefs = null;
	
	public DefaultServerSocketCreator(Prefs prefs)
	{
		this.prefs = prefs;		
	}

	public DefaultServerSocketCreator() {
		this.prefs = Main.getOptions();		
	}

    public ServerSocket createServerSocket(int port, InetAddress address)
        throws IOException
    {
        return new ServerSocket(port, 512, address);
    }
    /**
     * @see org.doit.muffin.ServerSocketCreator#createServerSocket(int, String)
     */
    public ServerSocket createServerSocket(int port)
        throws IOException
    {
        ServerSocket server = null;
        server = new ServerSocket(port, 512);
        return server;
    }

    /**
     * @see org.doit.muffin.ServerSocketCreator#createHandler(Monitor, FilterManager, Options, Socket)
     */
    public Handler createHandler(
        Monitor monitor,
        FilterManager manager,
        Options options,
        Socket socket)
    {
        return new Handler(monitor,manager,options,socket);
    }

}
