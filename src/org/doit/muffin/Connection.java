/* $Id: Connection.java,v 1.7 2000/01/24 04:02:13 boyns Exp $ */

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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

/**
 * Create a TCP connection from a Socket or hostname/port
 * with buffered IO.
 *
 * @see java.net.Socket
 * @author Mark Boyns
 */
class Connection
{
    Socket socket = null;
    InputStream in = null;
    OutputStream out = null;

    /**
     * Create a Connection from a Socket.
     *
     * @param socket a socket
     */
    Connection(Socket socket) throws IOException
    {
	this.socket = socket;
	in = socket.getInputStream();
	out = socket.getOutputStream();
    }

    /**
     * Create a Connection from a hostname and port.
     *
     * @param host remote hostname
     * @param port remote port
     */
    Connection(String host, int port) throws IOException
    {
	this(new Socket(MuffinResolver.getByName(host), port));
    }

    Connection()
    {
    }

    /**
     * Return the input stream.
     */
    InputStream getInputStream()
    {
	return in;
    }

    /**
     * Return the output stream.
     */
    OutputStream getOutputStream()
    {
	return out;
    }

    void setInputStream(InputStream in)
    {
	this.in = in;
    }
    
    void setOutputStream(OutputStream out)
    {
	this.out = out;
    }

    /**
     * Close the connection.
     */
    void close()
    {
	if (socket != null)
	{
	    try
	    {
		socket.close();
	    }
	    catch (IOException e)
	    {
		System.out.println("Connection: " + e);
	    }
	}
    }

    public Socket getSocket()
    {
	return socket;
    }

    public InetAddress getInetAddress()
    {
	return socket.getInetAddress();
    }
    
    public int getPort()
    {
	return socket.getPort();
    }

    public String toString()
    {
	return getInetAddress().getHostAddress() + ":" + getPort();
    }

    public void setTimeout(int timeout)
	throws SocketException
    {
	socket.setSoTimeout(timeout);
    }
}
