/* $Id: Message.java,v 1.14 2003/05/24 21:04:41 cmallwitz Exp $ */

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
package org.doit.muffin;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;
import java.io.*;
import org.doit.io.*;

/**
 * Abstract class to represent message headers.
 *
 * @author Mark Boyns
 */
public abstract class Message
{
    /**
     * Hashtable used to store message headers.
     */
    private Hashtable headers = new Hashtable(33);

    /** Http status line.
     */
    String statusLine = null;

    /** Reads a line from the input stream.
     *
     * A line is terminated by "\r\n".
     * @return The line.
     * @param in Input stream.
     * @throws IOException Thrown if there are any errors reading from the stream.
     */
    public String readLine(InputStream in) throws IOException
    {
        char buf[] = new char[8096]; // was [128]
        int offset = 0;
        int ch;

        for (;;)
        {
            ch = in.read();
            if (ch == -1 || ch == '\n')
            {
                break;
            }
            else if (ch == '\r')
            {
                int tmpch = in.read();
                if (tmpch != '\n')
                {
                    if (! (in instanceof PushbackInputStream))
                    {
                        in = new PushbackInputStream(in);
                    }
                    ((PushbackInputStream) in).unread(tmpch);
                }
                break;
            }
            else
            {
                if (offset == buf.length)
                {
                    char tmpbuf[] = buf;
                    buf = new char[tmpbuf.length * 2];
                    System.arraycopy(tmpbuf, 0, buf, 0, offset);
                }
                buf[offset++] = (char) ch;
            }
        }
        return String.copyValueOf(buf, 0, offset);
    }

    /**
     * Read headers and store them in the hashtable.
     * @param in Input stream to read headers from.
     * @throws IOException Thrown if there are any errors reading from the stream.
     */
    public void readHeaders(InputStream in) throws IOException
    {
        int i;
        Key key = null;

        String s = readLine(in);
        while (s != null &&
               s.length () > 0)
        {
            i = s.indexOf(':');
            if (i == -1)
            {
                /* multi-line headers */
                if (key != null &&
                    (s.startsWith(" ") || s.startsWith("\t")))
                {
                    int index = getHeaderValueCount(key.toString());
                    index--;
                    Vector v = (Vector) headers.get(key);
                    v.setElementAt(v.elementAt(index) + "\n" + s, index);
                }
            }
            else
            {
                key = new Key(s.substring(0, i));
                Vector v;
                if (headers.containsKey(key))
                {
                    v = (Vector) headers.get(key);
                }
                else
                {
                    v = new Vector();
                }
                v.addElement(s.substring(i+1).trim());
                headers.put(key, v);
            }
            s = readLine(in);
        }
    }

    /** Get the number of header items.
     * @return Count of headers.
     */
    public int headerCount()
    {
        return headers.size();
    }

    /**
     * Set the Status line.
     * @param l New http status line.
     */
    public void setStatusLine(String l)
    {
        statusLine = l;
    }

    /** Get the number of values associated with a header.
     * @param name Header name.
     * @return Count of the values.
     */
    public int getHeaderValueCount(String name)
    {
        Vector v = (Vector) headers.get(new Key(name));
        return v.size();
    }

    /** Get header field.
     *
     * If the header field is multivalued, returns the first value.
     *
     * @param name Name of the header field.
     * @return Value of the header field.
     */
    public String getHeaderField(String name)
    {
        return getHeaderField(name, 0);
    }

    /** Get the specified value for this header field.
     * @param name Header field name.
     * @param index Which value to get.
     * @return Specified header field value.
     */
    public String getHeaderField(String name, int index)
    {
        Vector v = (Vector) headers.get(new Key(name));
        if (v == null)
        {
            return null;
        }
        return (String) v.elementAt(index);
    }

    /** Set the header field.
     *
     * For a multivalued header field, sets the first value.
     * @param name Header field name.
     * @param value New value.
     */
    public void setHeaderField(String name, String value)
    {
        setHeaderField(name, value, 0);
    }

    /** Set the specified value of a multivalued header field.
     * @param name Header field name.
     * @param value New value.
     * @param index Index of the value to set for this multivalued header field.
     */
    public void setHeaderField(String name, String value, int index)
    {
        Vector v;
        Key key = new Key(name);

        if (headers.containsKey(key))
        {
            v = (Vector) headers.get(key);
        }
        else
        {
            v = new Vector();
            if (index == 0)
            {
                v.addElement("");
            }
            headers.put(key, v);
        }
        v.setElementAt(value, index);
    }

    /** Set the header field to an int value.
     *
     * For a multivalued header field, sets the first value.
     * @param name Header field name.
     * @param value New value.
     */
    public void setHeaderField(String name, int value)
    {
        setHeaderField(name, value, 0);
    }

    /** Set the specified value of a multivalued header field.
     * @param name Header field name.
     * @param value New value.
     * @param index Index of the value to set for this multivalued header field.
     */
    public void setHeaderField(String name, int value, int index)
    {
        setHeaderField(name, new Integer(value).toString(), index);
    }

    /** Set all header fields with the give name to the specified value.
     * @param name Header field name.
     * @param value New value.
     */
    public void setHeaderFields(String name, String value)
    {
        Vector v;
        Key key = new Key(name);

        v = (Vector) headers.get(key);
        if (v != null)
        {
            for (int i = 0; i < v.size(); i++)
            {
                v.setElementAt(value, i);
            }
        }
    }

    /** Append to an existing header field value.
     *
     * If no value exists the field is set to the new value.
     * @param name Header field name.
     * @param value Value to append.
     */
    public void appendHeaderField(String name, String value)
    {
        appendHeaderField(name, value, 0);
    }

    /** Append to an existing header field value.
     *
     * If no value exists the field is set to the new value.
     * @param name Header field name.
     * @param value Value to append.
     * @param index Which value of a multivalued header field to append to.
     */
    public void appendHeaderField(String name, String value, int index)
    {
        setHeaderField(name, getHeaderField(name, index) + value, index);
    }

     /** Remove a header field.
     * @param name Header field name.
     */
    public void removeHeaderField(String name)
    {
        headers.remove(new Key(name));
    }

    /** Remove a nth header field (for headers occuring multiple times e.g. SetCookie)
     * @param name Header field name.
     * @param n number/position of header field.
     */
    public void removeHeaderField(String name, int n)
    {
        Vector v;
        Key key = new Key(name);

        v = (Vector) headers.get(key);
        if (v != null)
        {
            v.remove(n);
        }
    }

    /** Return whether or not a header exists.
     *
     * @param name header name
     * @return Returns true if a header exists, otherwise false.
     *
     */
    public boolean containsHeaderField(String name)
    {
        return headers.containsKey(new Key(name));
    }

    /** Get the header fields.
     * @return Header field names as an Enumeration of Strings
     */
    public Enumeration getHeaders()
    {
        Vector v = new Vector();

        for (Enumeration e = headers.keys(); e.hasMoreElements(); )
        {
            v.addElement(e.nextElement().toString());
        }

        return v.elements();
    }

    private final static byte[] COLON_SPACE = ": ".getBytes();
    private final static byte[] CRLF = "\r\n".getBytes();

    private ByteArray toByteArray(byte[] sep)
    {
        ByteArray buf = new ByteArray();
        Key key;
        String value;
        Vector v;
        int i = 0;

        buf.append(statusLine);
        buf.append(sep);

        for (Enumeration e = headers.keys(); e.hasMoreElements(); )
        {
            key = (Key) e.nextElement();
            v = (Vector) headers.get(key);
            for (i = 0; i < v.size(); i++)
            {
                buf.append(key.toString());
                buf.append(COLON_SPACE);
                buf.append(v.elementAt(i).toString());
                buf.append(sep);
            }
        }
        buf.append(sep);

        return buf;
    }

    private ByteArray toByteArray()
    {
        return toByteArray(CRLF);
    }

    private ByteArray toByteArray(String sep)
    {
        return toByteArray(sep.getBytes());
    }

    /** Convert this message to a String.
     *
     * Only the message headers are converted.
     * @return A String representation of this message.
     */
    public String toString()
    {
        return toByteArray().toString();
    }

    /** Convert this message to a String using the supplied line separator.
     *
     * Only the message headers are converted.
     * @param sep Line separator
     * @return A String representation of this message.
     */
    public String toString(String sep)
    {
        return toByteArray(sep).toString();
    }

    /** Write this message to a stream.
     *
     * Only the message headers are written.
     * @param out Stream to write to.
     * @throws IOException Thrown is there are errors writing to the stream.
     */
    public void write(OutputStream out)
        throws IOException
    {
        toByteArray().writeTo(out);
        out.flush();
    }
}
