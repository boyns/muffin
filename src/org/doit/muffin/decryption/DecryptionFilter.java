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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;

import org.doit.muffin.HttpFilter;
import org.doit.muffin.Https;
import org.doit.muffin.HttpsConnection;
import org.doit.muffin.Prefs;
import org.doit.muffin.Reply;
import org.doit.muffin.Request;
import org.doit.muffin.RetryRequestException;

/**
 * @author Fabien Le Floc'h
 */
public class DecryptionFilter implements HttpsConnection, HttpFilter
{

    private Prefs prefs;
    private Https https;

    public DecryptionFilter(Decryption factory)
    {
        this.prefs = factory.getPrefs();
    }
    /**
     * @see org.doit.muffin.HttpFilter#wantRequest(Request)
     */
    public boolean wantRequest(Request request)
    {
        if (request.isSecure())
        {
            try
            {
                this.https =
                    new Https(
                        prefs.getString(Decryption.HOST),
                        prefs.getInteger(Decryption.PORT),
                        false);
                return true;
            }
            catch (IOException e)
            {
                System.err.println(
                    "DECRYPTION - ERROR - could not create Filter:");
                e.printStackTrace();

            }
        }
        return false;
    }

    public Prefs getPrefs()
    {
        return this.prefs;
    }
    /**
     * @see org.doit.muffin.Filter#setPrefs(Prefs)
     */
    public void setPrefs(Prefs prefs)
    {
        this.prefs = prefs;
    }

    /**
     * @see org.doit.muffin.HttpRelay#close()
     */
    public void close()
    {
    	this.https.close();
    }

    /**
     * @see org.doit.muffin.HttpRelay#recvReply(Request)
     */
    public Reply recvReply(Request request)
        throws IOException, RetryRequestException
    {
        return this.https.recvReply(request);
    }

    /**
     * @see org.doit.muffin.HttpRelay#sendRequest(Request)
     */
    public void sendRequest(Request request)
        throws IOException, RetryRequestException
    {
    	this.https.sendRequest(request);
    }

    /**
     * @see org.doit.muffin.HttpsConnection#getInputStream()
     */
    public InputStream getInputStream()
    {
        return https.getInputStream();
    }

    /**
     * @see org.doit.muffin.HttpsConnection#getOutputStream()
     */
    public OutputStream getOutputStream()
    {
        return https.getOutputStream();
    }

    /**
     * @see org.doit.muffin.HttpsConnection#setTimeout(int)
     */
    public void setTimeout(int timeout) throws SocketException
    {
    	https.setTimeout(timeout);
    }

}
