/* $Id: SourceObjectStream.java,v 1.5 2000/01/24 04:02:09 boyns Exp $ */

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

import java.io.InputStream;
import java.io.IOException;

public class SourceObjectStream extends OutputObjectStream implements Runnable
{
    InputStream src;
    int sourceLength = 0;

    public SourceObjectStream(InputObjectStream in) throws IOException
    {
	super(in);
    }
    
    public void setSourceInputStream(InputStream src)
    {
	this.src = src;
    }

    public void setSourceLength(int length)
    {
	sourceLength = length;
    }

    public void run()
    {
	try
	{
	    int maxLength = 8192;
	    int n;

	    do
	    {
		n = (sourceLength > 0) ? Math.min(sourceLength, maxLength) : maxLength;
		ByteArray array = new ByteArray(n);
		n = src.read(array.bytes);
		
		if (n > 0)
		{
		    array.offset = n;
		    write(array);
		    if (sourceLength > 0)
		    {
			sourceLength -= n;
			if (sourceLength == 0)
			{
			    break;
			}
		    }
		}
	    }
	    while (n > 0);
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
