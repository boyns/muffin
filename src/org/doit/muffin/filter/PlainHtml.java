/*
 * Copyright 2002 Doug Porter
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

import java.util.*;

import org.doit.html.*;
import org.doit.muffin.*;

public class PlainHtml implements FilterFactory
{
    final String FilterName = "PlainHtml";

    final String [] DefaultGoodHtml = {
        "a",
        "abbr",
        "acronym",
        "address",
        "area",
        "b",
        "base",
        "basefont",
        "bdo",
        "big",
        "blockquote",
        "body",
        "br",
        "break",
        "button",
        "caption",
        "center",
        "code",
        "col",
        "colgroup",
        "dd",
        "del",
        "dir",
        "div",
        "dl",
        "dt",
        "em",
        "face",
        "fieldset",
        "font",
        "form",
        "frame",
        "frameset",
        "h1",
        "h2",
        "h3",
        "h4",
        "h5",
        "h6",
        "head",
        "hr",
        "html",
        "i",
        "iframe",
        "img",
        "input",
        "ins",
        "isindex",
        "kbd",
        "label",
        "legend",
        "li",
        "map",
        "menu",
        "meta",
        "noframes",
        "noscript",
        "ol",
        "optgroup",
        "option",
        "p",
        "pre",
        "q",
        "s",
        "samp",
        "select",
        "small",
        "span",
        "strike",
        "strong",
        "sub",
        "sup",
        "table",
        "tbody",
        "td",
        "textarea",
        "tfoot",
        "th",
        "thead",
        "title",
        "tr",
        "tt",
        "u",
        "ul",
        "var",
    };

    final String [] SkippedHtml = {
        "javascript",
        "script",
        "style",
    };

    FilterManager manager = null;
    Prefs prefs = null;
    PlainHtmlFrame frame = null;
    MessageArea messages = null;
    HashSet goodHtml = new HashSet ();
    HashSet badHtml = new HashSet ();
    HashSet skippedHtml = new HashSet ();


    public void setManager (FilterManager manager)
    {
        this.manager = manager;
    }

    public void setPrefs (Prefs prefs)
    {
        this.prefs = prefs;
        init ();
    }

    public Prefs getPrefs ()
    {
        return prefs;
    }

    public void viewPrefs ()
    {
        if (frame == null)
        {
            frame = new PlainHtmlFrame (prefs, this);
        }
        frame.setVisible (true);
    }

    public Filter createFilter ()
    {
        Filter f = new PlainHtmlFilter (this);
        f.setPrefs (prefs);
        return f;
    }

    public void shutdown ()
    {
        if (frame != null)
        {
            frame.dispose ();
        }
    }

    void init () {
	messages = new MessageArea ();
        initSet (goodHtml, DefaultGoodHtml);
        initSet (skippedHtml, SkippedHtml);
    }

    void initSet (Set set,
                  String [] defaults) {
        for (int i = 0;
             i < defaults.length;
             ++ i) {

            set.add (defaults [i].toLowerCase ());

        }
    }

    boolean isTagGood (Tag tag) {
        
        String name = getName (tag);
        boolean ok = goodHtml.contains (name);
        
        if (! ok) {
            if (! badHtml.contains (name)) {
                report ("bad tag: " + name);
                badHtml.add (name);
            }
        }
        
        // return true; //DEBUG
        return ok;
    }
    
    boolean isTagSkipped (Tag tag) {
        return skippedHtml.contains (getName (tag));
    }

    String getName (Tag tag) {
        final String Slash = "/";

        String name = tag.name ().toLowerCase ();
        if (name.startsWith (Slash)) {
            name = name.substring (Slash.length ());
        }
        return name;
    }

    void save () {
    }

    void report (Request request, String message)
    {
        request.addLogEntry (FilterName, message);
        report (message);
    }

    void report (String message)
    {
        messages.append (message + "\n");
    }
    
    String getName () {
        return FilterName;
    }
}

