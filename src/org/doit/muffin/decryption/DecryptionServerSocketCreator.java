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
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import org.doit.muffin.FilterManager;
import org.doit.muffin.Handler;
import org.doit.muffin.Monitor;
import org.doit.muffin.Options;
import org.doit.muffin.ServerSocketCreator;
import org.doit.muffin.SocketCreator;

import com.sun.net.ssl.SSLContext;

/**
 * @author Fabien Le Floc'h
 */
class DecryptionServerSocketCreator implements ServerSocketCreator
{

    private SSLServerSocketFactory factory;
    private SocketCreator socketCreator;

    DecryptionServerSocketCreator(SSLContext context)
    {
        this.factory = context.getServerSocketFactory();
        this.socketCreator = new SSLSocketCreator(context);
    }
    /**
     * @see org.doit.muffin.ServerSocketCreator#createServerSocket(int, String)
     */
    public ServerSocket createServerSocket(int port) throws IOException
    {
        // Socket to me
        SSLServerSocket serverSocket =
            (SSLServerSocket) factory.createServerSocket(port);
        // Authenticate the client?
        serverSocket.setNeedClientAuth(false);
        // Return a ServerSocket on the desired port (443)
        return serverSocket;

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
        return new DecryptionHandler(
            monitor,
            manager,
            options,
            socket,
            socketCreator);
    }

}
