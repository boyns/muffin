/*
 * Copyright (C) 2003 Fabien Le Floc'h <fabien@31416.org>
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



/**
 * 
 * @author Fabien Le Floc'h
 */
public abstract class AbstractHttpError implements HttpError
{
        protected String content = null;
        protected Reply reply = null;

        public AbstractHttpError(int code, Exception e)
        {
            this(code, e.toString());
        }
        
        protected abstract void createContent(int code, String error, String message);
        
        
        public AbstractHttpError(int code, String message)
        {
            String error;
            switch (code)
            {
                case 400 :
                    error = "Bad Request";
                    break;

                case 403 :
                    error = "Forbidden";
                    break;

                case 404 :
                    error = "Not found";
                    break;

                case 503 :
                    error = "Service Unavailable";
                    break;

                default :
                    error = "Error";
                    break;
            }

            reply = new Reply();
            reply.setStatusLine("HTTP/1.0 " + code + " " + error);
            reply.setHeaderField("Content-type", "text/html");
            reply.setHeaderField("Server", "Muffin/" + Main.getMuffinVersion());
			createContent(code,error,message);
        }

        public Reply getReply()
        {
            return reply;
        }

        public String getContent()
        {
            return content;
        }

        public String toString()
        {
            StringBuffer buf = new StringBuffer();
            if (reply != null)
            {
                buf.append(reply.toString());
            }
            if (content != null)
            {
                buf.append(content);
            }
            return buf.toString();
        }		
		

}
