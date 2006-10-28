/**
 * SelectToRadio.java -- transforms html selects to radio buttons
 * 
 * A client of mine was complaining about the web client of his
 * issue tracking tool ("TestTrack Pro", http://www.seapine.com/ttpro.html).
 * There are lots of selects where radio buttons would be a better
 * ui, because all options would be visible at once.
 * 
 * TODO:
 * - javascript attributes of element 
 *    select       input (type = radio or checkbox)   option     optgroup
 *  - onBlur       yes                                yes        no
 *  - onChange     yes                                no         no
 *  - onClick      yes                                yes        yes
 *  - onDblClick   yes                                yes        yes
 *  - onFocus      yes                                no         no
 *  - onKeyDown    yes                                yes        yes
 *  - onKeyPress   yes                                yes        yes
 *  - onKeyUp      yes                                yes        yes
 *  - onMouseDown  yes                                yes        yes
 *  - onMouseMove  yes                                yes        yes
 *  - onMouseOut   yes                                yes        yes
 *  - onMouseOver  yes                                yes        yes
 *  - onMouseUp    yes                                yes        yes
 *
 * - properly treat optgroup elements (@see org.doit.muffin.test.SelectToRadioTest#testSelfHtml5())
 * - properly treat all possible attributes of the involved elements
 *               // http://www.htmlhelp.com/reference/html40/forms/select.html
 *               // http://www.w3.org/TR/html401/index/attributes.html
 *               - name -> ok
 *               - size -> ignore
 *               - multiple -> ok
 *               - disabled -> map to which element?
 *               - tabindex -> map to which element?
 *               - onfocus
 *               - onblur
 *               - onchange
 *               - id
 *               - class
 *               
 * 
 * - introduce properties that can be saved: Horizontal layout of generated UI-components.
 *
 * @author  Bernhard Wagner <muffinsrc@xmlizer.biz>
 * @version 0.1
 *
 * Last update: 28/10/06 B.W.
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
//                System.out.println(tag);
                if (tag.is("select"))
                {
                    name = tag.get("name");
                    multiple = tag.has("multiple");
                    // FIXME: get all other attributes of select tag
                    // http://www.htmlhelp.com/reference/html40/forms/select.html
                    // http://www.w3.org/TR/html401/index/attributes.html
                  	injectTag(new Tag("table border=\"1\""));
                  	injectTag(new Tag("tr"));
                  	injectTag(new Tag("td"));
                    continue;
                } else if (tag.is("option")){
                	// A bit of a hack here...
                	// if the tag has no value attribute, we have to read what's
                	// between <option> and </option>
                	// see http://www.selfhtml.net/html/formulare/auswahl.htm
                    String value = null;
                    Token tmp = null;
                    if (tag.has("value")) {
                    	value = tag.get("value");
                    } else {
                    	tmp = (Token) getInputObjectStream().read();
                    	value = tmp.toString();
                    }
                    Tag myTag = new Tag("input",
                        "type=\""+(multiple ? "checkbox" : "radio")+"\" name=\""+name+
                        "\" value=\""+value+"\""+
                        (tag.has("selected") ? " checked" : ""));
                    if(tag.has("value")) {
                        token.importTag(myTag);                    	
                    } else {
                    	injectTag(myTag);
                    	token = tmp;
                    }
                } else if (tag.is("/select")) {
                  	injectTag(new Tag("/td"));
                  	injectTag(new Tag("/tr"));
                  	injectTag(new Tag("/table"));
                    continue;
                } else if (tag.is("/option")) {
//                	if(!getFactory().getPrefsBoolean(SelectToRadio.HORIZONTAL)) {
                    if(true) {
	                  	injectTag(new Tag("br/"));
                	}
                    continue;
               }
            }
            getOutputObjectStream().write(token);
        }
    }

	private void injectTag(Tag tag) throws IOException {
		Token token = new Token(Token.TT_TAG);
		token.importTag(tag);
		getOutputObjectStream().write(token);
	}
}
