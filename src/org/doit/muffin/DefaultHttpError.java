/* $Id: HttpError.java,v 1.6 2000/01/24 04:02:13 boyns Exp $ */

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

public class DefaultHttpError extends AbstractHttpError
{

	
    /**
     * Constructor for DefaultHttpError.
     * @param code
     * @param e
     */
    public DefaultHttpError(int code, Throwable e)
    {
        super(code, e);
    }

    /**
     * Constructor for DefaultHttpError.
     * @param code
     * @param message
     */
    public DefaultHttpError(int code, String message)
    {
        super(code, message);
    }

    /**
     * @see org.doit.muffin.AbstractHttpError#createContent(int, String, String)
     */
    protected void createContent(int code, String error, String message)
    {
        StringBuffer content = new StringBuffer();
        content.append(DefaultHttpd.head(error));
        content.append(message);
        content.append(DefaultHttpd.tail());
        this.content = content.toString();
    }

}
