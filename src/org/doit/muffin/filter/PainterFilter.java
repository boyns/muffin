/* $Id: PainterFilter.java,v 1.7 2003/05/19 23:06:54 forger77 Exp $ */

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
import org.doit.html.*;
import java.io.*;

public class PainterFilter implements ContentFilter
{
    private static final String PATTERN1 = "^(body|td|table)$";
    private static final String PATTERN2 = "^(tr|th)$";

    Painter factory;
    Prefs prefs;
    InputObjectStream in = null;
    OutputObjectStream out = null;
    Request request = null;

    PainterFilter(Painter factory)
    {
	this.factory = factory;
    }
    
    public void setPrefs(Prefs prefs)
    {
	this.prefs = prefs;
    }

    public boolean needsFiltration(Request request, Reply reply)
    {
	this.request = request;
	String s = reply.getContentType();
	return s != null && s.startsWith("text/html");
    }
    
    public void setInputObjectStream(InputObjectStream in)
    {
	this.in = in;
    }

    public void setOutputObjectStream(OutputObjectStream out)
    {
	this.out = out;
    }
    
	public void run() {
		Thread.currentThread().setName("Painter");

		try {
			Tag tag;
			Object obj;

			while ((obj = in.read()) != null) {
				Token token = (Token) obj;
				if (token.getType() == Token.TT_TAG) {
					tag = token.createTag();

					if (tag.matches(PATTERN1)) {
						String value;
						
						if(fixIt("bgcolor", tag)){
							tag.remove("background");
						}else{
							fixIt("background", tag);
							tag.remove("bgcolor");
						}

						fixIt("text", tag);

						fixIt("link", tag);

						fixIt("alink", tag);

						fixIt("vlink", tag);
					} else if (tag.matches(PATTERN2) && tag.has("bgcolor")) {
						fixIt("bgcolor", tag);
					} else if (tag.is("font") && tag.has("color")) {
						String value = prefs.getString("Painter.text");
						if (value.length() > 0) {
							if (value.equalsIgnoreCase("None")) {
								tag.remove("color");
							} else {
								tag.put("color", value);
							}
						}
					}

					token.importTag(tag);
				}
				out.write(token);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				out.flush();
				out.close();
			} catch (IOException ioe) {
			}
		}
	}
	
	private boolean fixIt (String key, Tag tag){
		String value = prefs.getString("Painter." + key);
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
