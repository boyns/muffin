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

/** Filters a Request.
 *
 * Classes which want to see or make changes to Requests should implement this interface.
 *
 * Muffin will call your <B>filter</B> method for each Request before it is sent to the server.
 *
 * Generally the request is to an HTTP server.
 *
 * @author Mark Boyns
 */
public interface RequestFilter extends Filter
{
    /** Filter, i.e. make changes to or get information from, all Requests before they are sent to the server.
     * @param r The request. Any changes should be made to this instance.
     * @throws FilterException Thrown if there are any errors.
     */    
    public void filter(Request r) throws FilterException;
}
