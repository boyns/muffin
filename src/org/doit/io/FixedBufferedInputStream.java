/* $Id: FixedBufferedInputStream.java,v 1.5 2000/01/24 04:02:09 boyns Exp $ */

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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Mark Boyns
 */
public class FixedBufferedInputStream extends FilterInputStream
{
    private final int BUFSIZE = 8192;
    
    private int maxBytes = 0;
    private int byteCount = 0;

    private byte bytes[] = null;
    private int blength = 0;
    private int index = 0;
    private boolean eof = false;

    public FixedBufferedInputStream(InputStream in, int maxBytes)
    {
	super(in);
	this.maxBytes = maxBytes;
    }

    public int read() throws IOException
    {
	if (eof)
	{
	    return -1;
	}
	
	if (bytes == null)
	{
	    int n = (maxBytes > 0) ? Math.min(maxBytes - byteCount, BUFSIZE) : BUFSIZE;
	    bytes = new byte[n];

	    n = read(bytes);
	    if (n <= 0)
	    {
		eof = true;
		return -1;
	    }

	    blength = n;
	    byteCount += n;
	}

	int b = bytes[index++];
	if (index == blength)
	{
	    bytes = null;
	    index = 0;
	    blength = 0;

	    if (byteCount == maxBytes)
	    {
		eof = true;
	    }
	}

	return b & 0xff;
    }
}
