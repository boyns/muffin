/* Base64 is a trimmed version of Codecs.java written by Ronald
   Tschalaer.  Base64 was created to suit the needs of Muffin. */

/*
 * @(#)Codecs.java					0.3 30/01/1998
 *
 *  This file is part of the HTTPClient package
 *  Copyright (C) 1996-1998  Ronald Tschalaer
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or(at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public
 *  License along with this library; if not, write to the Free
 *  Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 *  MA 02111-1307, USA
 *
 *  For questions, suggestions, bug-reports, enhancement-requests etc.
 *  I may be contacted at:
 *
 *  ronald@innovation.ch
 *  Ronald.Tschalaer@psi.ch
 * */

package org.doit.util;

public class Base64
{
    private static byte[] Base64DecMap;

    // Class Initializer
    static
    {
	// rfc-2045: Base64 Alphabet
	byte[] map = {
	    (byte)'A', (byte)'B', (byte)'C', (byte)'D', (byte)'E', (byte)'F',
	    (byte)'G', (byte)'H', (byte)'I', (byte)'J', (byte)'K', (byte)'L',
	    (byte)'M', (byte)'N', (byte)'O', (byte)'P', (byte)'Q', (byte)'R',
	    (byte)'S', (byte)'T', (byte)'U', (byte)'V', (byte)'W', (byte)'X',
	    (byte)'Y', (byte)'Z',
	    (byte)'a', (byte)'b', (byte)'c', (byte)'d', (byte)'e', (byte)'f',
	    (byte)'g', (byte)'h', (byte)'i', (byte)'j', (byte)'k', (byte)'l',
	    (byte)'m', (byte)'n', (byte)'o', (byte)'p', (byte)'q', (byte)'r',
	    (byte)'s', (byte)'t', (byte)'u', (byte)'v', (byte)'w', (byte)'x',
	    (byte)'y', (byte)'z',
	    (byte)'0', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'5',
	    (byte)'6', (byte)'7', (byte)'8', (byte)'9', (byte)'+', (byte)'/' };
	Base64DecMap = new byte[128];
	for (int idx=0; idx<map.length; idx++)
	    Base64DecMap[map[idx]] = (byte) idx;
    }

    /**
     * This class isn't meant to be instantiated.
     */
    private Base64() {}
    
    /**
     * This method decodes the given string using the base64-encoding
     * specified in RFC-2045(Section 6.8).
     *
     * @param  str the base64-encoded string.
     * @return the decoded <var>str</var>.
     */
    public final static String base64Decode(String str)
    {
	if (str == null)  return  null;

// 	byte data[] = new byte[str.length()];
// 	str.getBytes(0, str.length(), data, 0);
	byte data[] = str.getBytes();

	return new String(base64Decode(data));
    }

    /**
     * This method decodes the given byte[] using the base64-encoding
     * specified in RFC-2045(Section 6.8).
     *
     * @param  data the base64-encoded data.
     * @return the decoded <var>data</var>.
     */
    public final static byte[] base64Decode(byte[] data)
    {
        if (data == null)  return  null;

        int tail = data.length;
        while (data[tail-1] == '=')  tail--;

        byte dest[] = new byte[tail - data.length/4];

        // ascii printable to 0-63 conversion
        for (int idx = 0; idx <data.length; idx++)
            data[idx] = Base64DecMap[data[idx]];

        // 4-byte to 3-byte conversion
        int sidx, didx;
        for (sidx = 0, didx=0; didx < dest.length-2; sidx += 4, didx += 3)
        {
            dest[didx]   = (byte) ( ((data[sidx] << 2) & 255) |
                            ((data[sidx+1] >>> 4) & 003) );
            dest[didx+1] = (byte) ( ((data[sidx+1] << 4) & 255) |
                            ((data[sidx+2] >>> 2) & 017) );
            dest[didx+2] = (byte) ( ((data[sidx+2] << 6) & 255) |
                            (data[sidx+3] & 077) );
        }
        if (didx < dest.length)
            dest[didx]   = (byte) ( ((data[sidx] << 2) & 255) |
                            ((data[sidx+1] >>> 4) & 003) );
        if (++didx < dest.length)
            dest[didx]   = (byte) ( ((data[sidx+1] << 4) & 255) |
                            ((data[sidx+2] >>> 2) & 017) );

        return dest;
    }
}
