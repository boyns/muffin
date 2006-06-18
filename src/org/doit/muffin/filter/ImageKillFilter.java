/**
 * ImageKillFilter.java -- kill images that match a certain size ratio
 *
 * @author  Heinrich Opgenoorth <opgenoorth@gmd.de>
 * @version 0.2
 *
 * Last update: 98/11/30 H.O.
 */

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

import org.doit.io.*;
import org.doit.html.*;
import java.io.IOException;

public class ImageKillFilter extends AbstractContentFilter
{

    /**
     * @param factory
     */
    ImageKillFilter(ImageKill factory)
    {
        super(factory);
        this.fFactory = factory;
    }

    /**
     * @see org.doit.muffin.filter.AbstractContentFilter#doGetContentIdentifier()
     */
    protected String doGetContentIdentifier()
    {
        return "text/html";
    }

    /**
     * @see org.doit.muffin.filter.AbstractContentFilter#doRun(ObjectStreamToInputStream, ObjectStreamToOutputStream)
     */
    protected void doRun(
        ObjectStreamToInputStream ostis,
        ObjectStreamToOutputStream ostos)
        throws IOException
    {

        final int MINHEIGHT =
            getFactory().getPrefsInteger(ImageKill.MINHEIGHT_PREF);
        final int MINWIDTH =
            getFactory().getPrefsInteger(ImageKill.MINWIDTH_PREF);
        final int RATIO = getFactory().getPrefsInteger(ImageKill.RATIO_PREF);
        final boolean KEEPMAPS =
            getFactory().getPrefsBoolean(ImageKill.KEEPMAPS_PREF);
        final boolean REPLACE =
            getFactory().getPrefsBoolean(ImageKill.REPLACE_PREF);
        final String REPLACEURL =
            getFactory().getPrefsString(ImageKill.REPLACEURL_PREF);

        Object obj;

        while ((obj = getInputObjectStream().read()) != null)
        {
            Token token = (Token) obj;
            if (token.getType() == Token.TT_TAG)
            {
                Tag tag = token.createTag();
                if (tag.is("img")
                    && tag.has("width")
                    && tag.has("height")
                    && !(KEEPMAPS && tag.has("usemap"))
                    && !fFactory.isExcluded(tag.get("src")))
                {
                	int w = 0;
                	int h = 0;
                    try
                    {
                        w = Integer.parseInt(tag.get("width"));
                        h = Integer.parseInt(tag.get("height"));
                    } catch (NumberFormatException e)
                    {
                        getFactory().report(
                            getRequest(),
                            "malformed image size: " + tag);
                        getOutputObjectStream().write(token);
                        continue;
                    }
                    if (fFactory.inRemoveSizes(w, h)
                        || ((h > MINHEIGHT) && (w > MINWIDTH) && (w / h > RATIO)))
                    {
                        if (REPLACE)
                        {
                            getFactory().report(
                                getRequest(),
                                "tag replaced: " + tag);
                            tag.put("src", REPLACEURL);
                            token.importTag(tag);
                        } else
                        {
                            getFactory().report(
                                getRequest(),
                                "tag removed: " + tag);
                            continue;
                        }
                    }
                }
            }
            getOutputObjectStream().write(token);
        }
    }
    private ImageKill fFactory;
}
