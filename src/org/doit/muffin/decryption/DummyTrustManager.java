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

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import com.sun.net.ssl.X509TrustManager;

/**
 * A Trust Manager accepting every certificate.
 * 
 * @author Fabien Le Floc'h <fabien@31416.org>
 */
public class DummyTrustManager implements X509TrustManager
{


    /**
     * @see com.sun.net.ssl.X509TrustManager#getAcceptedIssuers()
     */
    public X509Certificate[] getAcceptedIssuers()
    {
        return new X509Certificate[0];
    }

    /**
     * @see com.sun.net.ssl.X509TrustManager#isClientTrusted(X509Certificate[])
     */
    public boolean isClientTrusted(X509Certificate[] arg0)
    {
        return true;
    }

    /**
     * @see com.sun.net.ssl.X509TrustManager#isServerTrusted(X509Certificate[])
     */
    public boolean isServerTrusted(X509Certificate[] arg0)
    {
        return true;
    }

}
