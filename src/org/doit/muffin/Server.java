/* $Id: Server.java,v 1.4 1999/03/12 15:47:41 boyns Exp $ */

/*
 * Copyright (C) 1996-99 Mark R. Boyns <boyns@doit.org>
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

/**
 * @author Mark Boyns
 */
class Server
{
    static ThreadGroup handlers = null;
    ServerSocket server = null;
    Monitor monitor = null;
    FilterManager manager = null;
    Options options = null;
    boolean running = false;
    Thread thread = null;

    Server(int port, Monitor m, FilterManager manager, Options options)
    {
	try
	{
	    server = new ServerSocket(port, 512);
	}
	catch (IOException e)
	{
	    System.out.println(e);
	    System.exit(0);
	}
	handlers = new ThreadGroup("Muffin Handlers");
	monitor = m;
	this.manager = manager;
	this.options = options;

	thread = Thread.currentThread();

	/* Initialize internal Httpd */
	Httpd.init(options, manager, monitor, this);
    }

    synchronized void suspend()
    {
	running = false;

	Thread list[] = getThreads();
	if (list == null)
	{
	    return;
	}
	for (int i = 0; i < list.length; i++)
	{
	    list[i].suspend(); // DEPRECATION: can't suspend anymore - deadlock
	}
    }

    synchronized void resume()
    {
	running = true;

	Thread list[] = getThreads();
	if (list == null)
	{
	    return;
	}
	for (int i = 0; i < list.length; i++)
	{
	    list[i].resume(); // DEPRECATION: can't resume anymore - deadlock
	}
    }

    synchronized void stop()
    {
	Thread list[] = getThreads();
	if (list == null)
	{
	    return;
	}
	for (int i = 0; i < list.length; i++)
	{
	    if (list[i] instanceof Handler)
	    {
		Handler h = (Handler) list[i];
		h.flush();
		h.close();
		h.kill();
	    }
	    else
	    {
		list[i].stop(); // DEPRECATION: can't stop anymore - deadlock
	    }
	}
    }

    static void clean()
    {
	Thread list[] = getThreads();
	if (list == null)
	{
	    return;
	}
	long now = System.currentTimeMillis();
	for (int i = 0; i < list.length; i++)
	{
	    if (list[i] instanceof Handler)
	    {
		Handler h = (Handler) list[i];
		if (h.idle > 0 && now - h.idle > (1000 * 60 * 5)) /* 5 minutes */
		{
		    h.close();
		    h.kill();
		}
	    }
	}
    }

    static synchronized Thread[] getThreads()
    {
	int n = handlers.activeCount();
	if (n < 0)
	{
	    return null;
	}
	Thread list[] = new Thread[n];
	handlers.enumerate(list);
	return list;
    }

    void run()
    {
	thread.setName("Muffin Server");
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
		Handler h = new Handler(handlers, thread, monitor,
					manager, options);
		h.doit(socket);
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
