/* $Id: MessageArea.java,v 1.6 2000/01/24 04:02:14 boyns Exp $ */

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

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.*;
import java.util.*;

public class MessageArea extends java.awt.TextArea
{
    private int maxLines;
    private int lines;

    public MessageArea()
    {
	this(500);
    }

    public MessageArea(int maxLines)
    {
	this.maxLines = maxLines;
	lines = 0;
    }

    public void append(String message)
    {
	lines++;
	if (lines > maxLines)
	{
	    String text = getText();
	    int offset = 0;
	    offset = text.indexOf('\n', offset);
	    offset++;
	    lines--;
	    replaceRange("", 0, offset);
	}
	super.append(message);
    }

    public void appendln(String message)
    {
	append(message + "\n");
    }

    public void clear()
    {
	setText("");
	lines = 0;
    }
}
