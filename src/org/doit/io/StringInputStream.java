/* $Id: StringInputStream.java,v 1.1 2003/01/08 18:59:51 boyns Exp $ */

/*
 * Copyright (C) 2003 Mark R. Boyns <boyns@doit.org>
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

import java.io.*;

public class StringInputStream
    extends InputStream
{
    private String      in;
    private int         pos;
    private int         length;

    public StringInputStream(String in)
    {
        this.in = in;
        this.pos = 0;
        this.length = in.length();
    }

    public int read()
        throws IOException
    {
        return (pos < length) ? (in.charAt(pos++) & 0xff) : -1;
    }
}
