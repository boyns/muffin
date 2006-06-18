/* $Id$ */
/*
 * Copyright (C) 2003 Bernhard Wagner <muffinsrc@xmlizer.biz>
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

import java.io.IOException;
import java.io.StreamCorruptedException;

import org.doit.io.*;
import org.doit.muffin.ContentFilter;
import org.doit.muffin.Prefs;
import org.doit.muffin.Reply;
import org.doit.muffin.Request;

public class AbstractContentFilter implements ContentFilter
{

    public AbstractContentFilter(AbstractFilterFactory factory)
    {
        fFactory = factory;
    }

    /**
     * Determines whether this Reply needs filtering.
     * Note to implementors: This method is not made final in case you want to determine the
     * need for filtering in your own way.
     * If the need for filtering is determined via the content identifier, override
     * @see org.doit.muffin.AbstractContentFilter#doGetContentFilter instead.
     * @see org.doit.muffin.ContentFilter#needsFiltration(org.doit.muffin.Request, org.doit.muffin.Reply)
     *
     * Ugly little side effect: We store the Request.
     */
    public final boolean needsFiltration(Request request, Reply reply)
    {
//        System.out.println("++++ "+getFactory().getName()+".needsFiltration("+request+", "+reply+")");
        this.fRequest = request;
        return doNeedsFiltration(reply.getContentType());
    }

    /**
     * Accessor for our Request.
     * @return Request Our Request.
     */
    protected final Request getRequest()
    {
        return fRequest;
    }

    /**
     * Hook method for subclasses of AbstractContentFilter that decides whether the given
     * Content Type needs Filtration. Default behaviour is to check whether the the given
     * contentType starts with the String returned by
     * @see org.doit.muffin.AbstractContentFilter#doGetContentIdentifier() which itself
     * should be overriden by subclasses.
     * @param contentType To check whether filtration is needed.
     * @return boolean True if filtration is needed.
     */
    protected boolean doNeedsFiltration(String contentType){
        return contentType != null && contentType.startsWith(doGetContentIdentifier());
    }

    /**
     * Hook method for subclasses of AbstractContentFilter that provides the contentType
     * identifier (e.g. "text/html", "image/gif", etc.).
     * @return String The content type identifier.
     */
    protected String doGetContentIdentifier()
    {
        return "";
    }

    /**
     * @see org.doit.muffin.ContentFilter#setInputObjectStream(org.doit.io.InputObjectStream)
     */
    public final void setInputObjectStream(InputObjectStream in)
    {
        fInObjectStream = in;
    }

    /**
     * @see org.doit.muffin.ContentFilter#setOutputObjectStream(org.doit.io.OutputObjectStream)
     */
    public final void setOutputObjectStream(OutputObjectStream out)
    {
        fOutObjectStream = out;
    }

    public final InputObjectStream getInputObjectStream()
    {
        return fInObjectStream;
    }

    public final OutputObjectStream getOutputObjectStream()
    {
        return fOutObjectStream;
    }

    /**
     * @see org.doit.muffin.Filter#setPrefs(org.doit.muffin.Prefs)
     * This is a legacy method. It is here to make AbstractContentFilter
     * implement the @see org.doit.muffin.filter.ContentFilter interface.
     * Once all Filters have been adapted to the new architecture
     * this method will be removed from the interface and all implementors.
     */
    public final void setPrefs(Prefs prefs)
    {
        throw new RuntimeException("You called setPrefs on a Filter instead of on the FilterFactory!");
    }

    /**
     * @see java.lang.Runnable#run()
     */
    public void run()
    {
        Thread.currentThread().setName(getFactory().getName());
        ObjectStreamToInputStream ostis =
        new ObjectStreamToInputStream(fInObjectStream);
        ObjectStreamToOutputStream ostos =
        new ObjectStreamToOutputStream(fOutObjectStream);
        try
        {
            doRun(ostis, ostos);
            ostos.flush();
            ostos.close();
        }
        catch (ObjectStreamException ise)
        {
            // don't mention it
        }
        catch (StreamCorruptedException sce)
        {
            System.out.println(sce + " " + fRequest.getURL());
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
        finally
        {
            try
            {
                fOutObjectStream.flush();
                fOutObjectStream.close();
            }
            catch (IOException ioe)
            {
                //FIXME: swallowing it on purpose?
            }
        }
    }

    protected void doRun(
        ObjectStreamToInputStream ostis,
        ObjectStreamToOutputStream ostos)
        throws IOException
    {}

    public final AbstractFilterFactory getFactory()
    {
        return fFactory;
    }

    // FIXME:replace by FilterFactory
    private AbstractFilterFactory fFactory;
    private InputObjectStream fInObjectStream = null;
    private OutputObjectStream fOutObjectStream = null;
    private Request fRequest;
//    private Reply fReply;

}
