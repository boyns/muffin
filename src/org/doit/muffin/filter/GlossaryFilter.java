/* $Id: GlossaryFilter.java,v 1.9 2006/06/18 23:25:51 forger77 Exp $ */

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

import org.doit.io.*;
import java.util.Enumeration;
import java.io.*;
import UK.co.demon.asmodeus.util.*;

public class GlossaryFilter extends AbstractContentFilter
{

    public GlossaryFilter(Glossary factory)
    {
        super(factory);
        fGlossary = factory;
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

        ByteArrayOutputStream htmlbuf = new ByteArrayOutputStream();
        try
        {
            byte buf[] = new byte[1024];
            int n;
            while ((n = ostis.read(buf, 0, buf.length)) > 0)
            {
                htmlbuf.write(buf, 0, n);
            }
            htmlbuf.close();
        } catch (Exception e)
        {
            //FIXME: swallowing it on purpose?
        }

        ByteArrayInputStream html =
            new ByteArrayInputStream(htmlbuf.toByteArray());
        BufferedReader cookedSource =
            new BufferedReader(new InputStreamReader(html));
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        Writer sink = new OutputStreamWriter(buffer);
        Enumeration terms = fGlossary.keys();
        MultiSearchReader root = new MultiSearchReader(terms, false, sink);

        MultiSearchResult match = new MultiSearchResult(-1, " ");
        while (match != null)
        {
            match =
                root.search(
                    match.getOffset() + match.getMatch().length(),
                    cookedSource);
            if (root.getTagStatus() == MultiSearchReader.INSIDE_TAG
                && match != null)
            {
                sink.write(match.getMatch());
                continue;
            }
            if (match != null)
            {
                sink.write("<a href=\"");
                sink.write(fGlossary.lookup(match.getMatch()));
                sink.write("\">");
                sink.write(match.getMatch());
                sink.write("</a>");
            }
        }
        sink.close();
        buffer.writeTo(ostos);
    }

    private Glossary fGlossary;
}
