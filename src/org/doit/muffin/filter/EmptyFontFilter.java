/* $Id: EmptyFontFilter.java,v 1.8 2003/06/01 01:01:10 forger77 Exp $ */

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

public class EmptyFontFilter extends AbstractContentFilter {

	EmptyFontFilter(EmptyFont factory) {
		super(factory);
	}

	/**
	 * @see org.doit.muffin.filter.AbstractContentFilter#doGetContentIdentifier()
	 */
	protected String doGetContentIdentifier(){
		return "text/html";	
	}

	/**	 * @see org.doit.muffin.filter.AbstractContentFilter#doRun(ObjectStreamToInputStream, ObjectStreamToOutputStream)	 */
	protected void doRun(ObjectStreamToInputStream ostis, ObjectStreamToOutputStream ostos)
		throws IOException {

		Tag tag;
		Token fontTag = null;

		Object obj;
		while ((obj = getInputObjectStream().read()) != null) {
			Token token = (Token) obj;
			if (token.getType() == Token.TT_TAG) {
				tag = token.createTag();

				/* <font> */
				if (tag.is("font")) {
					// Got a font tag. If we have just seen a font tag,
					// we ought to save them both, but just write out
					// the previous one for now.
					if (fontTag != null)
						getOutputObjectStream().write(fontTag);
					// Save the FONT tag until we get more details
					// fontTag = new Token(token);
					fontTag = token;
					// factory.debug("Got a FONT tag\n");
				} else if (tag.is("/font") && (fontTag != null)) {
					// Empty <font...></font>, eat them both!
					fontTag = null;
					// factory.debug("Empty FONT tag - eat it\n");
				} else {
					if (fontTag != null) {
						getOutputObjectStream().write(fontTag);
						fontTag = null;
						// factory.debug("FONT tag not empty - issue it and the tag\n");
					}
					getOutputObjectStream().write(token);
				}
			} else if (token.getType() == Token.TT_COMMENT) {
				// Treat comments as "white-space" - do not put out the
				// FONT tag(if any) yet
				// if (fontTag != null)
				//	factory.debug("Got a comment - keep FONT tag for now\n");
				getOutputObjectStream().write(token);
			} else {
				if (fontTag != null) {
					// Check if all whitespace
					int nb;
					boolean whitespace = true;
					byte bytes[] = token.getBytes();
					// factory.debug("Got text - check if white-space....\n");
					for (nb = 0;
						(nb < token.length()) && whitespace;
						nb++) {
						if (bytes[nb] > ' ')
							whitespace = false;
					}
					// If whitespace, do not put out the FONT tag yet
					if (!whitespace) {
						// factory.debug("FONT tag not empty - output it\n");
						getOutputObjectStream().write(fontTag);
						fontTag = null;
					}
					// factory.debug("Now output the text: \"" + token.toString() + "\"\n");
				}
				getOutputObjectStream().write(token);
			}
		}
	}
}
