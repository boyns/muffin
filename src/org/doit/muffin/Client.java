/* $Id: Client.java,v 1.7 2003/05/10 01:01:23 flefloch Exp $ */

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

import org.doit.io.*;
import java.io.*;
import java.net.Socket;

/**
 * Client which reads a Request and writes a Reply.
 *
 * @see muffin.Request
 * @see muffin.Reply
 * @author Mark Boyns
 */
public class Client extends Connection
{
    /**
     * Create a Client from a Socket.
     */
    protected Client(Socket s) throws IOException
    {
        super(s);
        in = new BufferedInputStream(in);
        //out = new DebugOutputStream(new BufferedOutputStream(out));
        out = new BufferedOutputStream(out);
    }

	protected Request createRequest()
	{
		return new Request(this);
	}
    /**
     * Read a Request.
     *
     * @returns a Request.
     * @see muffin.Request
     */
    Request read() throws IOException
    {
        Request request = createRequest();
        request.read(getInputStream());
        return request;
    }

    /**
     * Write a Reply
     *
     * @see muffin.Reply
     */
    void write(Reply reply) throws IOException
    {
        reply.write(getOutputStream());
    }
}
