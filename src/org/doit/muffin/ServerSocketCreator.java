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
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Fabien Le Floc'h
 */
public interface ServerSocketCreator
{

    String DECRYPTION_CLASS_NAME =
        "org.doit.muffin.decryption.DecryptionServerSocketCreator";

    ServerSocket createServerSocket(int port, Options options)
        throws IOException;
    Handler createHandler(
        Monitor monitor,
        FilterManager manager,
        Options options,
        Socket socket);

    /**
     * allows independent compilation of muffin from decryption package
     * and other eventual ServerSocketCreator implementations packages
     */
    class LazyFactory
    
    {
        public static ServerSocketCreator getDefaultCreator()
        {
            return new DefaultServerSocketCreator();
        }

        public static ServerSocketCreator getDecryptionCreator()
        {
            ServerSocketCreator creator = null;
            try
            {
                return (ServerSocketCreator) Class
                    .forName(DECRYPTION_CLASS_NAME)
                    .newInstance();
            }
            catch (Exception e)
            {
                throw new RuntimeException(
                    "Class "
                        + DECRYPTION_CLASS_NAME
                        + " not found, https sniffing will not be available");
            }

        }
    }
}
