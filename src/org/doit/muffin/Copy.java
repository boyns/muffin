/* $Id: Copy.java,v 1.7 2003/01/08 16:51:51 dougporter Exp $ */

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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

class Copy implements Runnable
{
    InputStream in = null;
    OutputStream out = null;
    
    Copy(InputStream in, OutputStream out)
    {
	this.in = in;
	this.out = out;
    }

    public void run()
    {
	int n;
	byte buffer[] = new byte[8192];

	try
	{
	    while ((n = in.read(buffer, 0, buffer.length)) > 0)
	    {
		out.write(buffer, 0, n);
		out.flush();
	    }
	    out.flush();
	}
	catch (IOException e)
	{
	    String s = e.toString();
	    // ignore socket closed exceptions
	    if (s.toLowerCase().indexOf("socket closed") == -1)
	    {
		e.printStackTrace();
	    }
	}
    }
}
