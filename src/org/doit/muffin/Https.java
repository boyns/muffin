/* $Id: Https.java,v 1.6 2000/01/24 04:02:14 boyns Exp $ */

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

/**
 * @author Mark Boyns
 */
class Https extends HttpConnection
{
    boolean proxy = false;
    
    Https(String host, int port) throws IOException
    {
	super(host, port);
    }

    Https(String host, int port, boolean isProxy) throws IOException
    {
	this(host, port);
	proxy = isProxy;
    }

    public void sendRequest(Request request)
	throws java.io.IOException, RetryRequestException
    {
	if (proxy)
	{
	    super.sendRequest(request);
	}
	else
	{
	    /* nothing */
	}
    }
    
    public Reply recvReply(Request request)
	throws java.io.IOException, RetryRequestException
    {
	Reply reply = new Reply(getInputStream());
	if (proxy)
	{
	    reply.read();
	}
	else
	{
	    reply.statusLine = "HTTP/1.0 200 Connection established";
	    reply.setHeaderField("Proxy-agent", "Muffin");
	}
        return reply;
    }
}
