/* $Id: Decaf.java,v 1.8 2003/06/05 15:33:13 forger77 Exp $ */

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
import org.doit.html.*;
import java.util.Hashtable;
import org.doit.muffin.regexp.Pattern;
import org.doit.muffin.regexp.Factory;

public class Decaf extends AbstractFilterFactory
{
    
    static final String NOJAVA = "noJava";
    static final String NOJAVASCRIPT = "noJavaScript";

    private static Pattern javaScriptTags = Factory.instance().getPattern("^(a|input|body|form|area|select|frameset|label|textarea|button|applet|base|basefont|bdo|br|font|frame|head|html|iframe|isindex|meta|param|script|style|title)$");
    private static Pattern javaScriptAttrs = Factory.instance().getPattern("^(onload|onunload|onclick|ondblclick|onmousedown|onmouseup|onmouseover|onmousemove|onmouseout|onfocus|onblur|onkeypress|onkeydown|onkeyup|onsubmit|onreset|onselect|onchange)$");

    /**
     * @see org.doit.muffin.filter.AbstractFilterFactory#doSetDefaultPrefs()     */
    protected void doSetDefaultPrefs()
    {
	putPrefsBoolean(NOJAVASCRIPT, true);
	putPrefsBoolean(NOJAVA, false);
    }

    /**
     * @see org.doit.muffin.filter.AbstractFilterFactory#doMakeFrame()
     */
    protected AbstractFrame doMakeFrame()
    {
        return new DecafFrame(this);
    }
    
    /**
     * @see org.doit.muffin.filter.AbstractFilterFactory#doMakeFilter()
     */
    protected Filter doMakeFilter()
    {
        return new DecafFilter(this);
    }

    /**
     * @see org.doit.muffin.filter.AbstractFilterFactory#getName()
     */
    public String getName()
    {
        return "Decaf";
    }

    static boolean isJavaScriptTag(String pattern)
    {
	return javaScriptTags.matches(pattern);
    }

    static boolean isJavaScriptAttr(String pattern)
    {
	return javaScriptAttrs.matches(pattern);
    }
    
}

