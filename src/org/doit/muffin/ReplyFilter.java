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

/** Filters a Reply.
 *
 * Classes which want to see or make changes to Replies before they are returned to the client should 
 * implement this interface.
 *
 * Muffin will call your <B>filter</B> method for each Reply before it is returned to the client.
 *
 * Generally the reply is from an HTTP server.
 *
 * If you read the reply's data stream from getContent () in filter (), be sure to make a copy of the 
 * stream and call setContent () with the copy.
 *
 * @author Mark Boyns
 */
public interface ReplyFilter extends Filter
{
    /** Filter, i.e. make changes to or get information from, all Replies before they are returned to the client.
     * @param r The reply. Any changes should be made to this instance.
     * @throws FilterException Thrown if there are any errors.
     */    
    public void filter(Reply r) throws FilterException;
}
