/*
 * Copyright 2002 Doug Porter
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

import java.io.*;
import java.util.*;
import org.doit.muffin.*;
import org.doit.io.*;
import org.doit.html.*;

public class PlainHtmlFilter 
implements ContentFilter
{
    final String HtmlContentType = "text/html";
    final String JavascriptContentType = "application/x-javascript";
    final String BinaryContentType = "application/octet-stream";
    
    PlainHtml factory;
    Prefs prefs;
    InputObjectStream in = null;
    OutputObjectStream out = null;
    String contentType = null;
    String url = null;

    public PlainHtmlFilter (PlainHtml factory)
    {
        this.factory = factory;
    }

    public void setPrefs (Prefs prefs)
    {
        this.prefs = prefs;
    }

    public boolean needsFiltration (Request request, Reply reply)
    {
        contentType = reply.getContentType ();
        if (contentType != null) {
            contentType = contentType.toLowerCase ();
        }
        url = request.getURL ();

        // !!!!! we may want to filter everything except application/octet-stream
        boolean needs = contentType == null ||
                        (contentType.startsWith (HtmlContentType) ||
                         contentType.startsWith (JavascriptContentType));
        if (! needs) {
            factory.report ("no filtration: content type of " +
                            url +
                            " is " +
                            contentType);
        }

        return needs;
    }

    public void setInputObjectStream (InputObjectStream in)
    {
        this.in = in;
    }

    public void setOutputObjectStream (OutputObjectStream out)
    {
        this.out = out;
    }

    public void run ()
    {
        Thread.currentThread ().setName (factory.getName ());

        try {
            if (contentType == null ||
                contentType.startsWith (JavascriptContentType)) {

                removeContent ();

            }
            else if (contentType.startsWith (HtmlContentType)) {

                filterHtml ();

            }
            else {

                passThrough ();

            }
        }
        catch (IOException ioe) {
            ioe.printStackTrace ();
        }
        finally {
            try {
                out.flush ();
                out.close ();
            }
            catch (IOException ioe) {
                // the connection may already be gone, so just ignore errors
            }
        }
    }

    private void removeContent ()
    throws IOException
    {
        // toss it all

        factory.report ("content removed: content type of " +
                        url +
                        " is " +
                        contentType);

        StringBuffer text = new StringBuffer ();
        text.append ("\n<hr>\n");
        text.append ("<h2>Url " +
                     url +
                     "<br>blocked by Muffin PlainHtml filter</h2>\n");
        text.append ("<hr>\n");
    }

    private void filterHtml ()
    throws IOException
    {
        final String Slash = "/";

        HashSet skipping = new HashSet ();

        Object obj = in.read ();
        while (obj != null) {

            Token token = (Token) obj;

            switch (token.getType ()) {

                case Token.TT_TAG:

                    Tag tag = token.createTag ();
                    String name = factory.getName (tag);

                    if (factory.isTagGood (tag)) {

                        // if we're not skipping elements between tags
                        if (skipping.size () <= 0) {

                            token.importTag (tag);
                            out.write (token);

                        }

                    }
                    else if (factory.isTagSkipped (tag)) {

                        // if this is an end tag
                        if (tag.name ().startsWith (Slash)) {
                            skipping.remove (name);
                        }
                        else {
                            skipping.add (name);
                        }

                    }

                    break;

                case Token.TT_COMMENT:
                    // strip comments
                    break;

                default:
                    // if we're not skipping elements between tags
                    if (skipping.size () <= 0) {

                        out.write (token);

                    }
                    break;
            }

            obj = in.read ();
        }
    }

    private void passThrough ()
    throws IOException
    {
        // we just pass everything through

        factory.report ("no filtration: content type of " +
                        url +
                        " is " +
                        contentType);

        Object obj = in.read ();
        while (obj != null)
        {
            out.write (obj);
            obj = in.read ();
        }
    }
}
