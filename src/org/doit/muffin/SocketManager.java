/* $Id: SocketManager.java,v 1.5 2000/01/24 04:02:14 boyns Exp $ */

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

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.net.Socket;

class SocketManager
{
    static Hashtable cache = new Hashtable(13);
    
    static Socket open(String host, int port) throws IOException
    {
	return new Socket(host, port);
	
// 	Socket socket = lookup(key(host, port));
// 	if (socket == null)
// 	{
// 	    socket = new Socket(host, port);
// 	    insert(key(host, port), socket);
// 	}
// 	return socket;
    }

    static void close(Socket socket) throws IOException
    {
	socket.close();
	
//	delete(socket);
    }

    static String key(String host, int port)
    {
	return host.toLowerCase() + ":" + port;
    }

    static Socket lookup(String key)
    {
 	Socket socket = (Socket) cache.get(key);
 	return socket;
    }

    static void insert(String key, Socket socket)
    {
	cache.put(key, socket);
    }

    static void delete(Socket socket)
    {
	Enumeration e = cache.keys();
	while (e.hasMoreElements())
	{
	    String key = (String) e.nextElement();
	    Socket s = lookup(key);
	    if (s == socket)
	    {
		cache.remove(key);
		return;
	    }
	}
    }
}
