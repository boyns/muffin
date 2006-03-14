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


/** Determines whether a filters wants to process a request itself, rather than let that request be sent to a server.
 *
 * Classes which want to process requests should implement this interface and also the HttpRelay interface.
 *
 * Muffin will call your <B>wantRequest</B> method for each request. If <B>wantRequest</B> returns true,
 * then Muffin will call your filter's sendRequest and recvReply (part of HttpRelay) for this request instead of 
 * sending the request to a server. If <B>wantRequest</B> returns false, Muffin will send the request to a server as 
 * usual.
 *
 * Generally the request is intended for an HTTP server.
 *
 * @author Mark Boyns
 */
public interface HttpFilter extends HttpRelay, Filter
{
    /** Returns whether this filter wants to process this request itself.
     * @param request Request
     * @return Whether this filter wants to process the request itself.
     */    
    public boolean wantRequest(Request request);
}
