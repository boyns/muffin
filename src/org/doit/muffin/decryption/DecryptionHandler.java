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
import java.net.Socket;

import org.doit.muffin.Client;
import org.doit.muffin.DefaultHttpd;
import org.doit.muffin.FilterManager;
import org.doit.muffin.Handler;
import org.doit.muffin.Http;
import org.doit.muffin.HttpRelay;
import org.doit.muffin.HttpdFactory;
import org.doit.muffin.Monitor;
import org.doit.muffin.Options;
import org.doit.muffin.Request;
import org.doit.muffin.SocketCreator;

/**
 * @author Fabien Le Floc'h
 */
class DecryptionHandler extends Handler
{

	private SocketCreator socketCreator;
    /**
     * Constructor for DecryptionHandler.
     * @param m
     * @param manager
     * @param options
     * @param socket
     */
    DecryptionHandler(
        Monitor m,
        FilterManager manager,
        Options options,
        Socket socket,
        SocketCreator creator)
    {
        super(m, manager, options, socket);
        this.socketCreator = creator;
        //System.out.println("DECRYPTION - DecryptionHandler constructed");
    }

    /**
     * @see org.doit.muffin.Handler#createClient(Socket)
     */
    public Client createClient(Socket socket) throws IOException
    {
        return new DecryptionClient(socket);
    }

    /**
     * @see org.doit.muffin.Handler#createHttpRelay()
     */
    public HttpRelay createHttpRelay(Request request, Socket socket)
        throws IOException
    {
        HttpRelay http;
        System.out.println("DECRYPTION - DecryptionHandler request:");
        System.out.println(request.toString());
        //System.out.println(
        //    "host=" + request.getHost() + " port=" + request.getPort());
        if (DefaultHttpd.sendme(request))
        {
            http = HttpdFactory.getFactory().createHttpd(socket);
        }
        else
        {
            //System.out.println("---creating SSL socket");
            http =
                Http.open(
                    this.socketCreator,
                    request.getHost(),
                    request.getPort());
            //System.out.println("---created SSL socket");
        }

        return http;
    }

    /**
     * @see org.doit.muffin.Handler#verifyUrlSyntax(String)
     */
    public boolean verifyUrlSyntax(Request request)
    {
        String url = request.getURL();
        if (url.startsWith("/"))
        {
            // do nothing.
        }

        else if (!url.startsWith("https://"))
        {
            System.out.println("Unknown URL: " + url);
            return false;
        }
        return true;

    }

}
