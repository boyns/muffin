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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import org.doit.muffin.FilterManager;
import org.doit.muffin.Handler;
import org.doit.muffin.Monitor;
import org.doit.muffin.Options;
import org.doit.muffin.ServerSocketCreator;

import com.sun.net.ssl.KeyManagerFactory;
import com.sun.net.ssl.SSLContext;

/**
 * @author Fabien Le Floc'h
 */
public class DecryptionServerSocketCreator implements ServerSocketCreator
{

    /**
     * @see org.doit.muffin.ServerSocketCreator#createServerSocket(int, String)
     */
    public ServerSocket createServerSocket(int port, Options options)
        throws IOException
    {
        String keystoreFileName =
            options.getString("muffin.decryptionServer.certificate");
        char[] keystorepw =
            options
                .getString("muffin.decryptionServer.keystorePassword")
                .toCharArray();
        char[] keypw =
            options
                .getString("muffin.decryptionServer.keyPassword")
                .toCharArray();

        boolean requireClientAuthentication;

        try
        {
            // Make sure that JSSE is available
            Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            // A keystore is where keys and certificates are kept
            // Both the keystore and individual private keys should be password protected
            KeyStore keystore = KeyStore.getInstance("JKS");
            keystore.load(
                DecryptionServerSocketCreator.class.getResourceAsStream(
                    keystoreFileName),
                keystorepw);
            // A KeyManagerFactory is used to create key managers
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            // Initialize the KeyManagerFactory to work with our keystore
            kmf.init(keystore, keypw);
            // An SSLContext is an environment for implementing JSSE
            // It is used to create a ServerSocketFactory
            SSLContext sslc = SSLContext.getInstance("SSLv3");
            // Initialize the SSLContext to work with our key managers
            sslc.init(kmf.getKeyManagers(), null, null);
            // Create a ServerSocketFactory from the SSLContext
            SSLServerSocketFactory ssf = sslc.getServerSocketFactory();
            // Socket to me
            SSLServerSocket serverSocket =
                (SSLServerSocket) ssf.createServerSocket(port);
            // Authenticate the client?
            serverSocket.setNeedClientAuth(false);
            // Return a ServerSocket on the desired port (443)
            return serverSocket;
        }
        catch (KeyManagementException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        catch (KeyStoreException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        catch (CertificateException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        catch (UnrecoverableKeyException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }

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
        return new DecryptionHandler(monitor, manager, options, socket);
    }

}
