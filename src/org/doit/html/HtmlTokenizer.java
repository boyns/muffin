/* $Id: HtmlTokenizer.java,v 1.5 2000/01/24 04:02:05 boyns Exp $ */

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

import java.io.InputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import org.doit.io.ByteArray;

public class HtmlTokenizer extends PushbackInputStream
{
    private final int badPattern[] = { '>', '<', '>' };
    private final int scriptPattern[] = { '<', ' ', '/', 's', 'c', 'r', 'i', 'p', 't', ' ', '>' };
    private InputStream in;
    private int nextTokenType = Token.TT_NONE;

    public HtmlTokenizer(InputStream in)
    {
	super(in, 32);
    }

    public Token getToken() throws IOException
    {
	int ch;
	boolean quoted = false;
	int quoteChar = 0;
	int badIndex = 0;
	int scriptIndex = 0;
	ByteArray undo = new ByteArray();

	Token token = new Token(nextTokenType);
	nextTokenType = Token.TT_NONE;

	while ((ch = read()) != -1)
	{
	    switch (token.type)
	    {
	    case Token.TT_COMMENT:
		break;
		
	    case Token.TT_SCRIPT:
		if (scriptPattern[scriptIndex] == ' ')
		{
		    if (Character.isWhitespace((char)ch))
		    {
			undo.append((byte)ch);
			break; /* XXX */
		    }
		    scriptIndex++;
		}
		if ((char)scriptPattern[scriptIndex] == Character.toLowerCase((char)ch))
		{
		    undo.append((byte)ch);
		    scriptIndex++;
		    if (scriptIndex == scriptPattern.length)
		    {
			/* put back the end tag for next time */
			for (int i = undo.length() - 1; i >= 0; i--)
			{
			    unread(undo.get(i));
			}
			token.chop(undo.length() - 1);
			return token;
		    }
		}
		else if (scriptIndex > 0)
		{
		    scriptIndex = 0;
		    undo.erase();
		}
		break;

	    default:
		/* look for end quote */
		if (quoted)
		{
		    if (ch == quoteChar)
		    {
			quoted = false;
		    }
		    // AJP modification - Allow for tags inside a tag!
		    // Example: <img src="......" alt="<B>Title</B>" ...>
		    else if (ch == badPattern[(badIndex < 0) ? 0 : badIndex])
		    {
			badIndex++;
			if (badIndex == badPattern.length)
			{
			    badIndex = 0;
			    quoted = false;
			    System.out.println("HTML: Missing start or end quote");
			    System.out.println();
			    System.out.println(new String(token.bytes));
			    System.out.println();
			}
		    }
		    // AJP modification - Allow for tags inside a tag!
		    else if (ch == '<') /* Start tag */
		    {
			badIndex = -1;
                    }
		}
		/* look for start tag */
		else if (ch == '<')
		{
		    if (token.type != Token.TT_NONE)
		    {
			unread(ch);
			return token;
		    }
		    token.type = Token.TT_TAG;
		}
		/* look for start quote */
		else if (token.type == Token.TT_TAG && (ch == '"' || ch == '\''))
		{
		    quoted = true;
		    quoteChar = ch;
		}
		/* otherwise it's text */
		else if (token.type == Token.TT_NONE)
		{
		    token.type = Token.TT_TEXT;
		}
	    }

	    token.append((byte)ch);

	    /* see if the tag is really a comment */
	    if (token.type == Token.TT_TAG && token.offset == 4)
	    {
		if (token.bytes[0] == '<'
		    && token.bytes[1] == '!'
		    && token.bytes[2] == '-'
		    && token.bytes[3] == '-')
		{
		    token.type = Token.TT_COMMENT;
		}
	    }

	    /* look for end tag */
	    if (ch == '>' && !quoted)
	    {
	        if (token.type == Token.TT_COMMENT
		    && token.bytes[token.offset-1] == '>'
		    && token.bytes[token.offset-2] == '-'
		    && token.bytes[token.offset-3] == '-')
		{
		    break;
		}
		else if (token.type == Token.TT_TAG)
		{
		    break;
		}
	    }
	}

	if (token.type == Token.TT_TAG)
	{
	    /* force the tag to be parsed */
	    Tag tag = token.createTag();
	    if (tag.is("script"))
	    {
		nextTokenType = Token.TT_SCRIPT;
	    }
	}

	return token.type == Token.TT_NONE ? null : token;
    }
}
