/* $Id: Server.java,v 1.12 2003/05/20 21:11:27 flefloch Exp $ */

/*
 * Copyright (C) 1996-2000 Mark R. Boyns <boyns@doit.org>
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

import java.net.InetAddress;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;
import java.io.DataOutputStream;
import org.doit.util.*;

/**
 * @author Mark Boyns
 * @author Fabien Le Floc'h
 */
public class Server implements Runnable
{
    ServerSocket server = null;
    Monitor monitor = null;
    FilterManager manager = null;
    Options options = null;
    boolean running = false;
    boolean shutdown = false;
    private ServerSocketCreator creator = null;

    public Server(
        ServerSocketCreator creator,
        int port,
        Monitor m,
        FilterManager manager,
        Options options)
    {
        this.manager = manager;
        this.options = options;
        this.monitor = m;
        this.creator = creator;
        
        try
        {
	        server = creator.createServerSocket(port);
        }
        catch (IOException e)
        {
            System.out.println(e);
            throw new RuntimeException(e.toString());
        }

    }
    
    public void setFilterManager(FilterManager manager)
    {
    	this.manager = manager;	
    }
    
    /**
     * @deprecated
     */
    Server(int port, Monitor m, FilterManager manager, Options options)
    {
    	this(new DefaultServerSocketCreator(options),port, m, manager, options);
    }

    public synchronized void suspend()
    {
        running = false;
    }

    public synchronized void resume()
    {
        running = true;
    }

    public synchronized void shutdown() throws IOException
    {
    	shutdown = true;
    	server.close();	
    }
	
    public void run()
    {
        Thread.currentThread().setName("Muffin Server "+this.creator.getClass().getName());
        running = true;
		shutdown = false;
        for (;!shutdown;)
        {
            Socket socket = null;

            try
            {
                socket = server.accept();
            }
            catch (IOException e)
            {
                System.out.println(e);
                if (!shutdown) {
                	continue;
                }
                else {
                	break;
                }
            }

            if (!options.hostAccess(socket.getInetAddress()))
            {
                System.out.println(
                    socket.getInetAddress().getHostAddress()
                        + ": access denied");
                error(socket, 403, "No muffins for you!");
            }
            else if (running)
            {
                Handler h = creator.createHandler(monitor, manager, options, socket);
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
            DataOutputStream out =
                new DataOutputStream(socket.getOutputStream());
            out.writeBytes((HttpErrorFactory.getFactory().createError(code, message)).toString());
            out.close();
            socket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
