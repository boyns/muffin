/* $Id: HtmlObjectStream.java,v 1.5 2000/01/24 04:02:09 boyns Exp $ */

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
package org.doit.io;

import org.doit.html.*;
import org.doit.io.FixedBufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class HtmlObjectStream extends SourceObjectStream
{
    public HtmlObjectStream(InputObjectStream in) throws IOException
    {
	super(in);
    }

    public void run()
    {
	boolean debug = false;
	
	try
	{
	    HtmlTokenizer html = new HtmlTokenizer(new FixedBufferedInputStream(src, sourceLength));
	    Token token;
	    while ((token = html.getToken()) != null)
	    {
		if (debug)
		{
		    System.out.println("---START TOKEN---");
		    System.out.println(token);
		    System.out.println("---END TOKEN---");
		}
		write(token);
	    }
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}

	try
	{
	    close();
	}
	catch (IOException e)
	{
	}
    }
}
