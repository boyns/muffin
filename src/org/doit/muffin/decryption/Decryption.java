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
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.doit.muffin.Filter;
import org.doit.muffin.FilterFactory;
import org.doit.muffin.FilterManager;
import org.doit.muffin.Main;
import org.doit.muffin.Prefs;
import org.doit.muffin.Server;
import org.doit.muffin.TextMonitor;

import com.sun.net.ssl.SSLContext;

/**
 * @author Fabien Le Floc'h
 */
public class Decryption implements FilterFactory
{
    public static final String PORT = "Decryption.port";
    public static final String HOST = "Decryption.host";
    public static final String CERTIFICATE = "Decryption.certificate";
    public static final String KEYSTORE_PASSWORD = "Decryption.keystorePassword";
    public static final String KEY_PASSWORD = "Decryption.keyPassword";
    public static final String DUMMY_TRUST = "Decryption.dummyTrust";
    public static final String CLIENT_AUTH = "Decryption.clientAuthentication";
    private Prefs prefs;
    private Server decryptionServer;
    private FilterManager manager;
	
    public Decryption()
    {
    }

    public void stopServer()
    {
        if (this.decryptionServer != null)
        {
            try
            {
                this.decryptionServer.shutdown();
            }
            catch (IOException e)
            {
                //this is normal.	
            }
        }
    }

    public void restartServer()
    {
        System.out.println("Restarting Decryption Server...");
        stopServer();
        startServer();
    }

    public void startServer()
    {
    	SSLContext context = SSLContextFactory.createSSLContext(this.prefs);
        InetAddress address;
        if (this.prefs.getString(HOST) == null)
        {
            this.prefs.putString(HOST, "127.0.0.1");
        }
        try
        {
            address = InetAddress.getByName(this.prefs.getString(HOST));
        
            this.decryptionServer =
                new Server(
                    new DecryptionServerSocketCreator(context),
                    this.prefs.getInteger(PORT),
                    address,
                    new TextMonitor("Decryption Server Monitor"),
                    this.manager,
                    Main.getOptions());
            System.out.println(
                "Decryption Server started on port " + this.prefs.getInteger(PORT));
            
            Thread t = new Thread(this.decryptionServer);
            t.start();
        }
        catch (UnknownHostException uhe)
        {
            System.err.println("can not start decryption server: "+uhe.getMessage());
        }
    }
    /**
     * @see org.doit.muffin.FilterFactory#setManager(FilterManager)
     */
    public void setManager(FilterManager m)
    {
    	this.manager = m;
    	restartServer();
    }

    /**
     * @see org.doit.muffin.FilterFactory#setPrefs(Prefs)
     */
    public void setPrefs(Prefs p)
    {
        boolean o = p.getOverride();
        p.setOverride(false);
        /* Internal HTTPS decryption server defaults */
        p.putInteger(PORT, 4443);
        p.putString(HOST, "127.0.0.1");
        p.putString(CERTIFICATE, "certs");
        p.putString(KEY_PASSWORD, "serverpw");
        p.putString(KEYSTORE_PASSWORD, "serverkspw");
        p.putBoolean(DUMMY_TRUST,true);
        p.putBoolean(CLIENT_AUTH,false);
        p.setOverride(o);
        this.prefs = p;
        if (this.manager != null) restartServer();
    }

    /**
     * @see org.doit.muffin.FilterFactory#getPrefs()
     */
    public Prefs getPrefs()
    {
        return this.prefs;
    }

    /**
     * @see org.doit.muffin.FilterFactory#viewPrefs()
     */
    public void viewPrefs()
    {
    }

    /**
     * @see org.doit.muffin.FilterFactory#shutdown()
     */
    public void shutdown()
    {
        stopServer();
    }

    /**
     * @return null if it was not able to create the Filter.
     */
    public Filter createFilter()
    {
        DecryptionFilter filter = new DecryptionFilter(this);
        return filter;
    }

}
