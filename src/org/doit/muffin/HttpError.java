/* $Id: HttpError.java,v 1.6 2000/01/24 04:02:13 boyns Exp $ */

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

class HttpError
{
    StringBuffer content = null;
    Reply reply = null;
    
    HttpError(Options options, int code, String message)
    {
	String error;
	switch (code)
	{
	case 400:
	    error = "Bad Request";
	    break;

	case 403:
	    error = "Forbidden";
	    break;

	case 404:
	    error = "Not found";
	    break;

	case 503:
	    error = "Service Unavailable";
	    break;

	default:
	    error = "Error";
	    break;
	}

	reply = new Reply();
	reply.statusLine = "HTTP/1.0 " + code + " " + error;
	reply.setHeaderField("Content-type", "text/html");
	reply.setHeaderField("Server", "Muffin/" + Main.getMuffinVersion());

	content = new StringBuffer();
	content.append(Httpd.head(error));
	content.append(message);
	content.append(Httpd.tail());
    }

    Reply getReply()
    {
	return reply;
    }

    String getContent()
    {
	if (content == null)
	{
	    return null;
	}
	return content.toString();
    }

    public String toString()
    {
	StringBuffer buf = new StringBuffer();
	if (reply != null)
	{
	    buf.append(reply.toString());
	}
	if (content != null)
	{
	    buf.append(content.toString());
	}
	return buf.toString();
    }
}
