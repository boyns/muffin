/* $Id: Decaf.java,v 1.6 2003/05/19 23:06:54 forger77 Exp $ */

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

public class Decaf implements FilterFactory
{
    FilterManager manager;
    Prefs prefs;
    DecafFrame frame = null;
    MessageArea messages = null;

    private Pattern javaScriptTags = null;
    private Pattern javaScriptAttrs = null;

	/**
	 * 	 * @see org.doit.muffin.FilterFactory#setManager(FilterManager)	 */
    public void setManager(FilterManager manager) {
		this.manager = manager;
	
	    javaScriptTags = Factory.instance().getPattern("^(a|input|body|form|area|select|frameset|label|textarea|button|applet|base|basefont|bdo|br|font|frame|head|html|iframe|isindex|meta|param|script|style|title)$");
	    javaScriptAttrs = Factory.instance().getPattern("^(onload|onunload|onclick|ondblclick|onmousedown|onmouseup|onmouseover|onmousemove|onmouseout|onfocus|onblur|onkeypress|onkeydown|onkeyup|onsubmit|onreset|onselect|onchange)$");
    }
    
    public void setPrefs(Prefs prefs)
    {
	this.prefs = prefs;

	boolean o = prefs.getOverride();
	prefs.setOverride(false);
	prefs.putBoolean("Decaf.noJavaScript", true);
	prefs.putBoolean("Decaf.noJava", false);
	prefs.setOverride(o);

	messages = new MessageArea();
    }

    public Prefs getPrefs()
    {
	return prefs;
    }

    public void viewPrefs()
    {
	if (frame == null)
	{
	    frame = new DecafFrame(prefs, this);
	}
	frame.setVisible(true);
    }
    
    public Filter createFilter()
    {
	Filter f = new DecafFilter(this);
	f.setPrefs(prefs);
	return f;
    }

    public void shutdown()
    {
	if (frame != null)
	{
	    frame.dispose();
	}
    }

    public boolean isJavaScriptTag(String pattern)
    {
	return javaScriptTags.matches(pattern);
    }

    public boolean isJavaScriptAttr(String pattern)
    {
	return javaScriptAttrs.matches(pattern);
    }
    
    void save()
    {
	manager.save(this);
    }

    void report(Request request, String message)
    {
	request.addLogEntry("Decaf", message);
	messages.append(message + "\n");
    }
}

