/* $Id: ContentFilter.java,v 1.5 2000/01/24 04:02:13 boyns Exp $ */

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

import org.doit.io.InputObjectStream;
import org.doit.io.OutputObjectStream;

/**
 * Filter interface for content filtering.
 *
 * @see muffin.RequestFilter
 * @see muffin.ReplyFilter
 * @see muffin.Filter
 * @see muffin.io.InputObjectStream
 * @see muffin.io.OutputObjectStream
 * @author Mark Boyns
 */
public interface ContentFilter extends Filter, Runnable
{
    /**
     * Specify whether or not this filter is interested
     * in filtering the content related to request and reply.
     *
     * @param request the request
     * @param reply the reply
     */
    public boolean needsFiltration(Request request, Reply reply);

    /**
     * Set the input stream to be used.
     *
     * @param in the input stream
     */
    public void setInputObjectStream(InputObjectStream in);
    
    /**
     * Set the output stream to be used.
     *
     * @param out the output stream
     */
    public void setOutputObjectStream(OutputObjectStream out);
}

