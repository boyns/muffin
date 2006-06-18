/* $Id: PainterFilter.java,v 1.10 2006/06/18 23:25:51 forger77 Exp $ */

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
import java.io.*;

public class PainterFilter extends AbstractContentFilter
{
    private static final String PATTERN1 = "^(body|td|table)$";
    private static final String PATTERN2 = "^(tr|th)$";

    PainterFilter(Painter factory)
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
    public void doRun(
        ObjectStreamToInputStream ostis,
        ObjectStreamToOutputStream ostos)
        throws IOException
    {
        Tag tag;
        Object obj;

        while ((obj = getInputObjectStream().read()) != null) {
            Token token = (Token) obj;
            if (token.getType() == Token.TT_TAG) {
                tag = token.createTag();

                if (tag.matches(PATTERN1)) {
                    if(fixIt(Painter.BGCOLOR, tag)){
                        tag.remove(Painter.BACKGRND);
                    }else{
                        fixIt(Painter.BACKGRND, tag);
                        tag.remove(Painter.BGCOLOR);
                    }

                    fixIt(Painter.TEXT, tag);

                    fixIt(Painter.LINK, tag);

                    fixIt(Painter.ALINK, tag);

                    fixIt(Painter.VLINK, tag);
                } else if (tag.matches(PATTERN2) && tag.has(Painter.BGCOLOR)) {
                    fixIt(Painter.BGCOLOR, tag);
                } else if (tag.is(Painter.FONT) && tag.has(Painter.COLOR)) {
                    String value = getFactory().getPrefsString(Painter.TEXT);
                    if (value.length() > 0) {
                        if (value.equalsIgnoreCase("None")) {
                            tag.remove(Painter.COLOR);
                        } else {
                            tag.put(Painter.COLOR, value);
                        }
                    }
                }

                token.importTag(tag);
            }
            getOutputObjectStream().write(token);
        }
    }
    
    private boolean fixIt (String key, Tag tag){
        String value = getFactory().getPrefsString(key);
        if (value.length() > 0) {
            if (value.equalsIgnoreCase("None")) {
                tag.remove(key);
            } else {
                tag.put(key, value);
            }
            return true;
        }
        return false;
    }
}

