/* $Id: Token.java,v 1.6 2000/01/24 04:02:05 boyns Exp $ */

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
package org.doit.html;

import org.doit.io.*;

public class Token extends ByteArray
{
    public static final byte TT_NONE = 0;
    public static final byte TT_EOF = 1;
    public static final byte TT_TAG = 2;
    public static final byte TT_TEXT = 3;
    public static final byte TT_COMMENT = 4;
    public static final byte TT_SCRIPT = 5;

    protected int type = TT_NONE;

    private Tag cachedTag = null;

    public Token()
    {
    }

    public Token(int type)
    {
	this.type = type;
    }

    public Token(Token token)
    {
	super(token.toString());
	this.type = token.type;
    }

    public int getType()
    {
	return type;
    }

    public Tag createTag()
    {
	if (cachedTag != null)
	{
	    return cachedTag;
	}
	
	int start = 0, end = 0, rest = 0;

	while (start < offset)
	{
	    switch ((char)bytes[++start])
	    {
	    case ' ':
	    case '\t':
	    case '\r':
	    case '\n':
	    case '>':
		continue;
	    }
	    break;
	}

	end = start;
 loop:  while (end < offset)
	{
	    switch ((char)bytes[++end])
	    {
	    case ' ':
	    case '\t':
	    case '\r':
	    case '\n':
		rest = end+1;
		break loop;
		
	    case '>':
		rest = -1;
		break loop;
	    }
	}

	String name = new String(bytes, start, end - start).toLowerCase();
	String data = null;
	if (rest > 0 && offset - rest -1 > 0)
	{
	    data = new String(bytes, rest, offset - rest - 1);
	}

	cachedTag = new Tag(name, data);
	return cachedTag;
    }


    public void importTag(Tag newTag)
    {
	// AJP modification: newTag may be a replacement tag but may not be
	// marked as modified so check tag "name" is the same(also do check
	// for cachedTag, just in case)
	if (newTag.isModified()
	    || cachedTag == null
	    || !cachedTag.name().equals(newTag.name()))
	{
	    bytes = newTag.toString().getBytes();
	    offset = bytes.length;
	    cachedTag = newTag;
	}
    }
}
