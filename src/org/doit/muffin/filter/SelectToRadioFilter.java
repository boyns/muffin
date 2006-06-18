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

public class SelectToRadioFilter extends AbstractContentFilter
{

    /**
     * @param factory
     */
    SelectToRadioFilter(SelectToRadio factory)
    {
        super(factory);
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

        Object obj;
        
        String name = "";
        boolean multiple = false;

        while ((obj = getInputObjectStream().read()) != null)
        {
            Token token = (Token) obj;
            if (token.getType() == Token.TT_TAG)
            {
                Tag tag = token.createTag();
                if (tag.is("select"))
                {
                    name = tag.get("name");
                    multiple = tag.has("multiple");
                    continue;
                } else if (tag.is("option")){
                    String value = tag.get("value");
                    boolean selected = tag.has("selected");
                    Tag myTag = new Tag("input",
                        "type=\""+(multiple ? "checkbox" : "radio")+"\" name=\""+name+
                        "\" value=\""+value+"\""+
                        (selected ? " checked" : ""));
                    token.importTag(myTag);
                } else if (tag.is("/select")) {
                    continue;
                } else if (tag.is("/option")) {
                    continue;
               }
            }
            getOutputObjectStream().write(token);
        }
    }
}
