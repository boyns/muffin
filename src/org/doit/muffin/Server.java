/* $Id: Server.java,v 1.8 2000/04/03 05:06:19 boyns Exp $ */

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

import java.net.InetAddress;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;
import java.io.DataOutputStream;
import org.doit.util.*;

/**
 * @author Mark Boyns
 */
class Server
{
    ServerSocket server = null;
    Monitor monitor = null;
    FilterManager manager = null;
    Options options = null;
    boolean running = false;

    Server(int port, Monitor m, FilterManager manager, Options options)
    {
	this.manager = manager;
	this.options = options;
	this.monitor = m;
	
	try
	{
	    String bindaddr = options.getString("muffin.bindaddress");
	    if (bindaddr != null && bindaddr.length() > 0)
	    {
		server = new ServerSocket(port, 512,
					  InetAddress.getByName(bindaddr));
	    }
	    else
	    {
		server = new ServerSocket(port, 512);
	    }
	}
	catch (IOException e)
	{
	    System.out.println(e);
	    System.exit(0);
	}

	/* Initialize internal Httpd */
	Httpd.init(options, manager, monitor, this);
    }

    synchronized void suspend()
    {
	running = false;
    }

    synchronized void resume()
    {
	running = true;
    }

//     synchronized void stop()
//     {
//     }

    void run()
    {
	Thread.currentThread().setName("Muffin Server");
	running = true;
	for (;;)
	{
	    Socket socket;

	    try
	    {
		socket = server.accept();
	    }
	    catch (IOException e)
	    {
		System.out.println(e);
		continue;
	    }

	    if (!options.hostAccess(socket.getInetAddress()))
	    {
		System.out.println(socket.getInetAddress().getHostAddress()
				    + ": access denied");
		error(socket, 403, "No muffins for you!");
	    }
	    else if (running)
	    {
		Handler h = new Handler(monitor, manager, options, socket);
		ReusableThread rt = Main.getThread();
		rt.setRunnable(h);
	    }
	    else
	    {
		error(socket, 503, "Muffin proxy service is suspended.");
	    }
	}
    }

    void error(Socket socket, int code, String message)
    {
	try
	{
	    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
	    out.writeBytes((new HttpError(options, code, message)).toString());
	    out.close();
	    socket.close();
	}
	catch (IOException e)
	{
	    e.printStackTrace();
	}
    }
}
