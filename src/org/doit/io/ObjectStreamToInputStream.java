/* $Id: ObjectStreamToInputStream.java,v 1.5 2000/01/24 04:02:09 boyns Exp $ */

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

/**
 * Convert and InputObjectStream to an InputStream.
 *
 * @author Mark Boyns
 */
public class ObjectStreamToInputStream extends InputStream
{
    protected InputObjectStream in;
    protected ByteArray array = null;
    protected int index = 0;

    public ObjectStreamToInputStream(InputObjectStream in)
    {
	this.in = in;
    }

    public int read() throws IOException
    {
	if (array == null)
	{
	    Object obj = (Object) in.read();
	    if (obj == null)
	    {
		return -1;
	    }
	    
	    if (obj instanceof ByteArray)
	    {
		array = (ByteArray) obj;
	    }
	    else if (obj instanceof String)
	    {
		array = new ByteArray((String)obj);
	    }
	    else
	    {
		throw new IOException("Unknown Object " + obj.toString());
	    }
	    
	    index = 0;
	}

	if (array.length() == 0)
	{
	    return -1;
	}

	int b = array.bytes[index++];
	if (index == array.length())
	{
	    array = null;
	    index = 0;
	}
	return b & 0xff;
    }
}
