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
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import org.doit.muffin.Prefs;

import com.sun.net.ssl.KeyManagerFactory;
import com.sun.net.ssl.SSLContext;
import com.sun.net.ssl.TrustManager;
/**
 * @author Fabien Le Floc'h <fabien@31416.org>
 */
public class SSLContextFactory
{
    public static SSLContext createSSLContext(Prefs prefs)
    {
        String keystoreFileName = prefs.getString(Decryption.CERTIFICATE);
        char[] keystorepw =
            prefs.getString(Decryption.KEYSTORE_PASSWORD).toCharArray();
        char[] keypw = prefs.getString(Decryption.KEY_PASSWORD).toCharArray();
        boolean dummyTrust = prefs.getBoolean(Decryption.DUMMY_TRUST);
        boolean requireClientAuthentication =
            prefs.getBoolean(Decryption.CLIENT_AUTH);

        // Make sure that JSSE is available
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        // A keystore is where keys and certificates are kept
        // Both the keystore and individual private keys should be password protected
        try
        {
            KeyStore keystore = KeyStore.getInstance("JKS");
            keystore.load(
                DecryptionServerSocketCreator.class.getResourceAsStream(
                    keystoreFileName),
                keystorepw);
            // A KeyManagerFactory is used to create key managers
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            // Initialize the KeyManagerFactory to work with our keystore
            kmf.init(keystore, keypw);

            TrustManager[] trustManagers = null;
            if (dummyTrust)
                trustManagers = new TrustManager[] { new DummyTrustManager()};
            // An SSLContext is an environment for implementing JSSE
            // It is used to create a ServerSocketFactory
            SSLContext sslc = SSLContext.getInstance("TLS");
            // Initialize the SSLContext to work with our key managers
            sslc.init(kmf.getKeyManagers(), trustManagers, null);
            return sslc;
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
        catch (UnrecoverableKeyException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

}
