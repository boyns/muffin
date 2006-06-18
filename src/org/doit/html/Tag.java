/* $Id: Tag.java,v 1.11 2006/06/18 23:25:52 forger77 Exp $ */

/*
 * Copyright (C) 1996-2000 Mark R. Boyns <boyns@doit.org>
 * Copyright (C) 2003 Christian Mallwitz <christian@mallwitz.com>
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
import org.doit.muffin.regexp.Factory;

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
            attributes = TagTokenizer.parse(data);
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

    public boolean matches(String pattern) {
        return (Factory.instance().getPattern(pattern).matches(name));
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

    public static class TagTokenizer
    {
        final static int BEFORE_KEY_STATE      = 1;
        final static int IN_KEY_STATE          = 2;
        final static int IN_QUOTED_KEY_STATE   = 3;
        final static int AFTER_KEY_STATE       = 4;
        final static int BEFORE_VALUE_STATE    = 5;
        final static int IN_VALUE_STATE        = 6;
        final static int IN_QUOTED_VALUE_STATE = 7;
        final static int AFTER_VALUE_STATE     = 8;

        public static Hashtable parse(String s)
        {
            Hashtable tag = new Hashtable();

            String key = null, value = null;

            int state = BEFORE_KEY_STATE;
            int len   = s.length();
            int start = -1;

            char quote_char = (char) 0;

            char c = (char) 0;

            for(int i = 0; i < len; i++)
            {
                c = s.charAt(i);

                switch (state)
                {
                    case BEFORE_KEY_STATE: // 1
                    {
                        if (Character.isWhitespace(c))
                        {
                            // remain in BEFORE_KEY_STATE
                        }
                        else if (c == '"' || c == '\'')
                        {
                            state = IN_QUOTED_KEY_STATE;
                            start = i;
                            quote_char = c;
                        }
                        else
                        {
                            state = IN_KEY_STATE;
                            start = i;
                        }
                        break;
                    }

                    case IN_KEY_STATE: // 2
                    {
                        if (c == '=')
                        {
                            key = s.substring(start, i);
                            // System.out.println("key=" + key);

                            state = BEFORE_VALUE_STATE;
                            start = -1;
                        }
                        else if (Character.isWhitespace(c))
                        {
                            key = s.substring(start, i);
                            // System.out.println("key=" + key);

                            state = AFTER_KEY_STATE;
                            start = -1;
                        }
                        else
                        {
                            // remain in IN_KEY_STATE
                        }
                        break;
                    }

                    case IN_QUOTED_KEY_STATE: // 3
                    {
                        if (c == quote_char)
                        {
                            key = s.substring(start+1, i);
                            // System.out.println("key=" + key);

                            state = AFTER_KEY_STATE;
                            start = -1;
                        }
                        else
                        {
                            // remain in IN_QUOTED_KEY_STATE
                        }
                        break;
                    }

                    case AFTER_KEY_STATE: // 4
                    {
                        if (c == '=')
                        {
                            state = BEFORE_VALUE_STATE;
                            start = -1;
                        }
                        else if (Character.isWhitespace(c))
                        {
                            // remain in AFTER_KEY_STATE
                        }
                        else
                        {
                            // put previous key without value in tag map
                            tag.put(key.toLowerCase(), new NoValue());

                            state = IN_KEY_STATE;
                            start = i;
                        }
                        break;
                    }

                    case BEFORE_VALUE_STATE: // 5
                    {
                        if (c == '"' || c == '\'')
                        {
                            state = IN_QUOTED_VALUE_STATE;
                            start = i;
                            quote_char = c;
                        }
                        else if (Character.isWhitespace(c))
                        {
                            // remain in BEFORE_VALUE_STATE
                        }
                        else
                        {
                            state = IN_VALUE_STATE;
                            start = i;
                        }
                        break;
                    }

                    case IN_VALUE_STATE: // 6
                    {
                        if (Character.isWhitespace(c))
                        {
                            value = s.substring(start, i);
                            // System.out.println("value=" + value);

                            // put key=value in tag map
                            tag.put(key.toLowerCase(), value);

                            state = AFTER_VALUE_STATE;
                        }
                        else
                        {
                            // remain in IN_VALUE_STATE
                        }
                        break;
                    }

                    case IN_QUOTED_VALUE_STATE: // 7
                    {
                        if (c == quote_char)
                        {
                            value = s.substring(start+1, i);
                            // System.out.println("value=" + value);

                            // put key=value in tag map
                            tag.put(key.toLowerCase(), value);

                            state = AFTER_VALUE_STATE;
                            start = -1;
                        }
                        else
                        {
                            // remain in IN_QUOTED_VALUE_STATE
                        }
                        break;
                    }

                    case AFTER_VALUE_STATE: // 8
                    {
                        if (Character.isWhitespace(c))
                        {
                            // remain in AFTER_VALUE_STATE
                        }
                        else
                        {
                            state = IN_KEY_STATE;
                            start = i;
                        }
                        break;
                    }

                    default:
                    {
                        throw new IllegalStateException("Unknown state: " + s);
                        // break;
                    }
                }

                // System.out.println("i=" + i + " c=" + c + " state=" + state + " start=" + start);
            }

            switch (state)
            {
                case BEFORE_KEY_STATE: // 1
                {
                    // string is empty ?
                    break;
                }

                case IN_KEY_STATE: // 2
                {
                    // string ended with key without value

                    key = s.substring(start, len);
                    // System.out.println("key=" + key);

                    tag.put(key.toLowerCase(), new NoValue()); // put previous key without value in tag map
                    break;
                }

                case IN_QUOTED_KEY_STATE: // 3
                {
                    // string ended with key without value

                    try
                    {
                        key = s.substring(start+1, (c == quote_char) ? len-1 : len);
                        // System.out.println("key=" + key);

                        tag.put(key.toLowerCase(), new NoValue()); // put previous key without value in tag map
                        break;
                    }
                    catch (StringIndexOutOfBoundsException e)
                    {
                        // tag may look like this: <a ">
                    }
                    break;
                }

                case AFTER_KEY_STATE: // 4
                {
                    // there was some trailing white space
                    // string ended with key without value

                    tag.put(key.toLowerCase(), new NoValue()); // put previous key without value in tag map
                    break;
                }

                case BEFORE_VALUE_STATE: // 5
                {
                    // there was a key followed by a equals sign but nothing after it

                    tag.put(key.toLowerCase(), new NoValue()); // put previous key without value in tag map
                    break;
                }

                case IN_VALUE_STATE: // 6
                {
                    // string ended with last key of value

                    value = s.substring(start, len);
                    // System.out.println("value=" + value);

                    tag.put(key.toLowerCase(), value); // put key=value in tag map
                    break;
                }

                case IN_QUOTED_VALUE_STATE: // 7
                {
                    // string ended with last key of value

                    try
                    {
                        value = s.substring(start+1, (c == quote_char) ? len-1 : len);
                        // System.out.println("value=" + value);

                        tag.put(key.toLowerCase(), value); // put key=value in tag map
                    }
                    catch (StringIndexOutOfBoundsException e)
                    {
                        tag.put(key.toLowerCase(), new NoValue()); // tag may look like this: <a "href"=">
                    }
                    break;
                }

                case AFTER_VALUE_STATE: // 8
                {
                    // there was some trailing white space
                    break;
                }

                default:
                {
                    throw new IllegalStateException("Unknown state: " + s);
                    // break;
                }
            }

            return tag;
        }
    }
}
