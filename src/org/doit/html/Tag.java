/* $Id: Tag.java,v 1.7 2000/01/24 04:02:05 boyns Exp $ */

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
package org.doit.html;

import java.util.Hashtable;
import java.util.Enumeration;
import sdsu.util.TokenCharacters;
import sdsu.util.SimpleTokenizer;
import gnu.regexp.*;

public class Tag
{
    protected String name = "";
    protected String data = null;
    
    private Hashtable attributes = null;
    private boolean parsed = false;
    private boolean modified = false;

    public Tag(String name, String data)
    {
	this.name = name;
	this.data = data;
    }

    private void parse()
    {
	parsed = true;

	if (data == null
	    || name.length() <= 0
	    || name.startsWith("<!doctype"))
	{
	    return;
	}
	
	try
	{
	    boolean oldModified = modified;
		
	    //String str = new String(contents, contentsIndex, contentsEnd - contentsIndex);
	    
	    TokenCharacters chars = new TokenCharacters("", "", '"', '"', " \t\r\n");
	    chars.addQuoteChars('\'', '\'');

	    SimpleTokenizer st = new SimpleTokenizer(data, chars);
	    st.setEatEscapeChar(false);

	    while (st.hasMoreTokens())
	    {
		String key = st.nextToken("=");
		if (st.separator() == '=')
		{
		    String value = st.nextToken("");
		    put(key, value);
		}
		else
		{
		    put(key, null);
		}
	    }

	    modified = oldModified;
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    public String name()
    {
	return name;
    }

    public boolean is(String s)
    {
	return name.equals(s);
    }

    public boolean has(String key)
    {
	if (!parsed) parse();
	return attributes != null ? attributes.containsKey(key) : false;
    }

    public String get(String key)
    {
	if (!parsed) parse();
	if (attributes == null) return null;
	Object obj = attributes.get(key);
	if (obj instanceof String)
	{
	    return(String) obj;
	}
	else if (obj != null)
	{
	    /* NoValue */
	    return obj.toString();
	}
	else
	{
	    /* really no value */
	    return null;
	}
    }

    public void put(String key, String value)
    {
	if (!parsed) parse();
	if (attributes == null)
	{
	    attributes = new Hashtable(13);
	}
	attributes.put(key.toLowerCase(),
			(value == null) ? new NoValue() : (Object) value);
	modified = true;
    }

    public String remove(String key)
    {
	String value = null;
	
	if (!parsed) parse();
	if (attributes != null)
	{
	    Object obj = attributes.remove(key);
	    modified = true;
	    if (obj != null)
	    {
		value = obj.toString();
	    }
	}
	return value;
    }

    public void rename(String newName)
    {
	name = newName;
	modified = true;
    }

    public boolean isModified()
    {
	return modified;
    }

    public Enumeration enumerate()
    {
	if (!parsed) parse();
	return attributes != null ? attributes.keys() : null;
    }

    public int attributeCount()
    {
	if (!parsed) parse();
	return attributes != null ? attributes.size() : 0;
    }

    public boolean matches(RE re)
    {
	REMatch match = re.getMatch(name);
	return match != null;
    }
    
    public String toString()
    {
	StringBuffer buf = new StringBuffer();
	buf.append('<');
	buf.append(name);
	if (!modified)
	{
	    if (data != null)
	    {
		buf.append(' ');
		buf.append(data);
	    }
	}
	else
	{
	    if (attributes != null && !attributes.isEmpty())
	    {
		String key, value;
		Object obj;
		Enumeration e = attributes.keys();
		while (e.hasMoreElements())
		{
		    key = (String) e.nextElement();
		    buf.append(' ');
		    buf.append(key);
		    obj = get(key);
		    if (obj instanceof String)
		    {
			value = (String) obj;
			buf.append('=');
			boolean containsQuote = value.indexOf('"') != -1;
			buf.append(containsQuote ? '\'' : '"');
			buf.append(value);
			buf.append(containsQuote ? '\'' : '"');
		    }
		    /* obj can also be instanceof NoValue -> do nothing */
		}
	    }
	}
	buf.append('>');
	return buf.toString();
    }
}
