/* ByteArray.java */

/*
 * Copyright (C) 1996-98 Mark R. Boyns <boyns@doit.org>
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

/**
 * Class used to represent an array of bytes as an Object.
 *
 * @author Mark Boyns
 */
public class ByteArray
{
    public byte bytes[];
    public int offset = 0;

    /**
     * Create a ByteArray with the default size.
     */
    public ByteArray ()
    {
	this (512);
    }

    /**
     * Create a ByteArray with a specific default size.
     */
    public ByteArray (int size)
    {
	bytes = new byte[size];
    }

    /**
     * Create a ByteArray from a String.
     */
    public ByteArray (String s)
    {
	this (s.length ());
	append (s);
    }

    /**
     * Create a ByteArray from an array of bytes.
     */
    public ByteArray (byte b[])
    {
	this (b.length);
	append (b);
    }
    
    /**
     * Append a byte.
     */
    public void append (byte ch)
    {
        if (offset == bytes.length)
        {
            byte tmpbytes[] = bytes;
            bytes = new byte[tmpbytes.length * 2];
            System.arraycopy (tmpbytes, 0, bytes, 0, offset);
        }
        bytes[offset++] = ch;
    }

    /**
     * Append a ByteArray.
     */
    public void append (ByteArray b)
    {
	if (bytes.length - offset < b.length ())
	{
	    byte tmpbytes[] = bytes;
	    bytes = new byte[tmpbytes.length + b.length ()];
	    System.arraycopy (tmpbytes, 0, bytes, 0, offset);
	}
	System.arraycopy (b.bytes, 0, bytes, offset, b.length ());
	offset += b.length ();
    }

    /**
     * Append an array of bytes.
     */
    public void append (byte b[])
    {
	if (bytes.length - offset < b.length)
	{
	    byte tmpbytes[] = bytes;
	    bytes = new byte[tmpbytes.length + b.length];
	    System.arraycopy (tmpbytes, 0, bytes, 0, offset);
	}
	System.arraycopy (b, 0, bytes, offset, b.length);
	offset += b.length;
    }

    /**
     * Append a String.
     */
    public void append (String s)
    {
	append (s.getBytes ());
    }

    /**
     * Convert to String.
     */
    public String toString ()
    {
	return new String (bytes, 0, offset);
    }

    /**
     * Return the bytes.
     */
    public byte getBytes () []
    {
	return bytes;
    }

    public byte get (int i)
    {
	return bytes[i];
    }

    /**
     * Return the number of bytes.
     */
    public int length ()
    {
	return offset;
    }

    public void erase ()
    {
	offset = 0;
    }
    
    public void chop ()
    {
	chop (1);
    }

    public void chop (int i)
    {
	offset -= i;
	if (offset < 0)
	{
	    offset = 0;
	}
    }

    public static void main (String args[])
    {
	ByteArray b = new ByteArray (3);
	b.append ("foo");
	b.append ("bar");
	b.append ("joe");
	System.out.println (b.toString ());
	
	ByteArray tmp = new ByteArray (1);
	tmp.append ("test");
	b.append (tmp);
	System.out.println (b.toString ());
    }
}
