/* $Id: ObjectStreamToOutputStream.java,v 1.5 2000/01/24 04:02:09 boyns Exp $ */

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

import java.io.OutputStream;
import java.io.IOException;

/**
 * Convert and OutputObjectStream to an OutputStream.
 *
 * @author Mark Boyns
 */
public class ObjectStreamToOutputStream extends OutputStream
{
    protected final int maxLength = 8192;
    protected OutputObjectStream out;
    protected ByteArray array = null;

    public ObjectStreamToOutputStream(OutputObjectStream out)
    {
	this.out = out;
    }

    public void write(int b) throws IOException
    {
	if (array == null)
	{
	    array = new ByteArray(maxLength);
	}
	array.append((byte)b);
	if (array.length() == maxLength)
	{
	    out.write(array);
	    array = null;
	}
    }

    public void flush() throws IOException
    {
	if (array != null)
	{
	    out.write(array);
	}
    }
}
