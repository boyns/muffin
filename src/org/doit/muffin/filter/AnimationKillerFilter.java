/* $Id: AnimationKillerFilter.java,v 1.11 2006/03/14 17:00:03 flefloch Exp $ */

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

import org.doit.muffin.*;
import org.doit.io.*;
import java.io.IOException;

import haui.gif.*;

public class AnimationKillerFilter
    extends AbstractContentFilter
    implements RequestFilter, ReplyFilter
{
    Request request;

    AnimationKillerFilter(AnimationKiller factory)
    {
        super(factory);
    }

    /**
     * @see org.doit.muffin.RequestFilter#filter(Request)
     */
    public void filter(Request request) throws FilterException
    {
        this.request = request;
    }

    /**
     * @see org.doit.muffin.ReplyFilter#filter(Reply)
     */
    public void filter(Reply reply) throws FilterException
    {}

    /**
     * @see org.doit.muffin.filter.AbstractContentFilter#doGetContentIdentifier()
     */
    protected String doGetContentIdentifier()
    {
        return "image/gif";
    }

    /**
     * @see java.lang.Runnable#run()
     */
    protected void doRun(
        ObjectStreamToInputStream ostis,
        ObjectStreamToOutputStream ostos)
        throws IOException
    {

        AnimationFilter filter;
        if (getFactory().getPrefsBoolean(AnimationKiller.BREAK))
        {
            filter = new AnimationFilter(AnimationFilter.MODE_WIPE_OUT);
        } else
        {
            switch (getFactory().getPrefsInteger(AnimationKiller.MAXLOOPS))
            {
                case 0 :
                    filter =
                        new AnimationFilter(AnimationFilter.MODE_SHOW_FIRST);
                    break;

                case 1 :
                    filter =
                        new AnimationFilter(AnimationFilter.MODE_SHOW_LAST);
                    break;

                case 2 :
                    filter =
                        new AnimationFilter(AnimationFilter.MODE_INTERACTIVE);
                    break;

                default :
                    filter =
                        new AnimationFilter(AnimationFilter.MODE_ANIMATION);
                    break;
            }
        }
        filter.filter(ostis, ostos);
    }
}
