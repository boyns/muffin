/* $Id$ */

/*
 * Copyright (C) 2003 Bernhard Wagner <bw@xmlizer.biz>
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
package org.doit.muffin.test;

import java.io.*;

import org.doit.io.*;
import org.doit.muffin.*;
import org.doit.muffin.regexp.*;

public class Utils
{
    /**
     * Basically a copy of 
     * @see org.doit.muffin.Handler#filter(InputStream,OutputStream,int length,boolean)
     * Stripped of the Thread stuff.
     * @param filter
     * @param in
     * @param out
     * @param length
     * @param reply
     */
    static void filter(
        ContentFilter filter,
        InputStream in,
        OutputStream out,
        int length,
        Reply reply)
    {
        InputObjectStream inputObjects = new InputObjectStream();
        SourceObjectStream srcObjects = null;

        OutputObjectStream oo = null;
        InputObjectStream io = null;

        try
        {
            if (reply.containsHeaderField("Content-type")
                && reply.getContentType().equals("text/html"))
            {
                srcObjects = new HtmlObjectStream(inputObjects);
            } else
            {
                srcObjects = new SourceObjectStream(inputObjects);
            }

            oo = new OutputObjectStream();
            io = new InputObjectStream(oo);
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        filter.setInputObjectStream(inputObjects);
        filter.setOutputObjectStream(oo);

        inputObjects = io;

        srcObjects.setSourceInputStream(in);
        srcObjects.setSourceLength(length);

        // When running non-threaded:
        // srcObjects.run() must happen BEFORE filter.run()
        // because srcObjects is the Producer and filter is the Consumer!

        srcObjects.run();
        filter.run();
        copy(inputObjects, out);
    }

    /**
     * Basically a copy of @see
     * org.doit.muffin.Handler#copy(InputObjectStream, OutputStream, boolean)
     * @param in
     * @param out
     */
    static void copy(InputObjectStream in, OutputStream out)
    {
        Object obj;
        long start = System.currentTimeMillis();
        long now = 0, then = start;

        try
        {
            for (;;)
            {
                obj = in.read();
                if (obj == null)
                {
                    break;
                }

                if (obj instanceof ByteArray)
                {
                    ByteArray bytes = (ByteArray) obj;
                    bytes.writeTo(out);
                } else if (obj instanceof Byte)
                {
                    Byte b = (Byte) obj;
                    out.write(b.byteValue());
                } else
                {
                    System.out.println("Unknown object: " + obj.toString());
                }

                now = System.currentTimeMillis();

                // flush after 1 second
                if (now - then > 1000)
                {
                    out.flush();
                }

                then = now;
            }
            out.flush();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static InputStream makeInputStreamFromString(String str)
    {
        byte[] bytes = str.getBytes();
        return new ByteArrayInputStream(bytes);
    }

    public static BufferedReader makeBufferedReaderFromString(String str)
    {
        return new BufferedReader(
            new InputStreamReader(makeInputStreamFromString(str)));
    }

    /** 
     * Constructs a Reply object containing a simple Web-Response.
     * @return Reply The constructed Reply object.
     */
    public static Reply makeReply(String str)
    {
        Reply reply = null;
        try
        {
            reply = new Reply(makeInputStreamFromString(str));
            reply.read();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return reply;
    }

}
