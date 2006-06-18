/* $Id: DecafFilter.java,v 1.10 2006/06/18 23:25:51 forger77 Exp $ */

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
package org.doit.muffin.filter;

import org.doit.muffin.*;
import org.doit.io.*;
import org.doit.html.*;
import java.util.Enumeration;
import java.io.IOException;

public class DecafFilter extends AbstractContentFilter implements ReplyFilter
{

    DecafFilter(Decaf factory)
    {
    super(factory);
	this.fFactory = factory;
    }
    
    public void filter(Reply reply) throws FilterException
    {
	if (getFactory().getPrefsBoolean(Decaf.NOJAVASCRIPT))
	{
	    String content = reply.getContentType();
	    if (content != null && content.equalsIgnoreCase("application/x-javascript"))
	    {
		fFactory.report(getRequest(), "rejecting " + content);
		throw new FilterException(getFactory().getName() + " " + content + " rejected");
	    }
	}
    }

    /**
     * @see org.doit.muffin.filter.AbstractContentFilter#doGetContentIdentifier()
     */
    protected String doGetContentIdentifier()
    {
        return "text/html";
    }
    
    /**
     * @see org.doit.muffin.filter.AbstractContentFilter#doRun(ObjectStreamToInputStream, ObjectStreamToOutputStream)
     */
    protected void doRun(
        ObjectStreamToInputStream ostis,
        ObjectStreamToOutputStream ostos)
        throws IOException
    {
        boolean eatingJavaScript = false;
        boolean eatingJava = false;
        Tag tag;
        Object obj;
        while ((obj = getInputObjectStream().read()) != null)
            {
        Token token = (Token) obj;
        if (token.getType() == Token.TT_TAG)
        {
            tag = token.createTag();

            if (eatingJavaScript && tag.is("/script"))
            {
            eatingJavaScript = false;
            continue;
            }
            if (eatingJava && tag.is("/applet"))
            {
            eatingJava = false;
            continue;
            }
            
            if (getFactory().getPrefsBoolean(Decaf.NOJAVASCRIPT))
            {
            if (tag.is("script"))
            {
                eatingJavaScript = true;
                fFactory.report(getRequest(), "removed <script>");
            }
            else if (Decaf.isJavaScriptTag(tag.name()) && tag.attributeCount() > 0)
            {
                StringBuffer str = new StringBuffer();
                String value;

                Enumeration e = tag.enumerate();
                while (e.hasMoreElements())
                {
                String attr = (String) e.nextElement();
                if (Decaf.isJavaScriptAttr(attr))
                {
                    value = tag.remove(attr);
                    if (value != null)
                    {
                    str.append("<");
                    str.append(tag.name());
                    str.append("> ");
                    str.append(attr);
                    str.append("=\"");
                    str.append(value);
                    str.append("\" ");
                    }
                }
                }

                if (tag.has("href")
                && ((value = tag.get("href")) != null)
                && "javascript:".startsWith(value))
                {
                value = tag.remove("href");
                str.append("<");
                str.append(tag.name());
                str.append("> ");
                str.append("href=\"");
                str.append(value);
                str.append("\" ");
                }

                if (tag.has("language")
                && tag.get("language").equalsIgnoreCase("javascript"))
                {
                value = tag.remove("language");
                str.append("<");
                str.append(tag.name());
                str.append("> ");
                str.append("language=\"");
                str.append(value);
                str.append("\" ");
                }

                if (str.length() > 0)
                {
                fFactory.report(getRequest(), "removed " + str.toString());
                }
            }
            }
            if (getFactory().getPrefsBoolean(Decaf.NOJAVA))
            {
            if (tag.is("applet"))
            {
                eatingJava = true;
                fFactory.report(getRequest(), "removed <applet>");
            }
            }
            if (!eatingJavaScript && !eatingJava)
            {
            token.importTag(tag);
            getOutputObjectStream().write(token);
            }
        }
        else if (!eatingJavaScript && !eatingJava)
        {
            getOutputObjectStream().write(token);
        }
        }
    }
    
    private Decaf fFactory;
}

