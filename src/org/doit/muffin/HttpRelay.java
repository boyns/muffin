/* $Id: HttpRelay.java,v 1.7 2006/03/14 17:00:04 flefloch Exp $ */

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

import java.io.IOException;

/** Used by a filter to process a request directly, instead of letting that request be sent to a server.
 *
 * Classes which want to process requests should implement this interface and also the HttpFilter interface.
 *
 * Muffin will call your sendRequest and recvReply methods for selected requests instead of 
 * sending the request to a server. Filters must also implement the HttpFilter interface to select requests.
 *
 * Generally the request is intended for an HTTP server.
 *
 * @author Mark Boyns
 */
public interface HttpRelay
{
    /** Implements sending a request.
     *
     * A filter may not actually send the request anywhere in this method, if it is just providing cache replies, for example.
     *
     * @param request Request to send.
     * @throws IOException Returned if there is an IO error.
     * @throws RetryRequestException Returned if the request can not be sent now, but should be retried later. (!!!!! check this)
     */    
    void sendRequest(Request request) throws IOException, RetryRequestException;
    /** Implements receiving a request.
     *
     * @throws IOException Returned if there is an IO error.
     * @throws RetryRequestException Returned if the request can not be sent now, but should be retried later. (!!!!! check this)
     * @param request Request
     * @return The reply to the request.
     */ 
    Reply recvReply(Request request) throws IOException, RetryRequestException;
    /** Closes the server connection, if any.
     */    
    void close();
}
