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

import java.io.InputStream;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.io.SequenceInputStream;
import java.util.Hashtable;
import java.util.StringTokenizer;

/** Reply to an http/https request.
 * @author Mark Boyns
 */
public class Reply extends Message
{
    public static final String GzipContentLengthAttribute = "X-Gzip-Content-length";

    private final int NoContentStatusCode = 204;
    private final int NotModifiedStatusCode = 304;

    private InputStream in = null;
    private Request request = null;
    private int statusCode = -1;

    /** Construct an empty http/https reply.
     */
    public Reply()
    {
    }

    /** Construct an http/https reply with an input stream.
     *
     * This just sets the input stream. You must explicitly the read() method.
     *
     * The input stream must consist of a status line, then http headers, followed by any content.
     *
     * @see read
     * @param in Input stream.
     */
    public Reply(InputStream in)
    {
        setContent(in);
    }

    /** Set the request associated with this reply.
     * @param request Request
     */
    public void setRequest (Request request)
    {
        this.request = request;
    }

    /** Get the request associatd with this reply.
     * @return The request.
     */
    public Request getRequest ()
    {
        return request;
    }

    /** Set the input stream which contains the content for this reply.
     * @param in Input stream.
     */
    public void setContent(InputStream in)
    {
        this.in = in;
    }

    /** Get the input stream containing the content for this reply.
     *
     * Any filter which reads this content must wrap this InputStream must reset the stream after reading.
     * For example:
     *
     * <CODE>
     *    in = new BufferedInputStream (reply.getContent ());
     *    in.mark (java.lang.Integer.MAX_VALUE);
     *    readContent (in); // your own processing
     *    in.reset ();
     *    reply.setContent (in);
     * </CODE>
     * @return Content stream.
     */
    public InputStream getContent()
    {
        return in;
    }

    /** Read the status line and headers from the input stream supplied to the constructor.
     *
     * The setContent and getContent methods set and get this input stream.
     * @throws IOException Thrown if there are any errors reading the stream.
     */
    public void read() throws IOException
    {
        if (in != null)
        {
            read(in);
        }
    }

    /** Read the status line and headers from the input stream.
     *
     * The setContent and getContent methods set and get this input stream.
     *
     * Replies with status code 204 "No Content" and 304 "Not Modified" are not allowed to have content,and any existing content is cleared.
     *
     * @param in Input stream. Used in place of any stream supplied in the constructor or by calling the setContent method.
     * @throws IOException Thrown if there are any errors reading the stream.
     */
    public void read(InputStream in) throws IOException
    {
        statusLine = readLine(in);
        if (statusLine == null || statusLine.length() == 0)
        {
            throw new IOException("Missing HTTP status line");
        }

        /* Look for HTTP/0.9 */
        if (!statusLine.startsWith("HTTP"))
        {
            /* Put back the line */
            if (this.in != null)
            {
                String putback = new String(statusLine + "\n");
                this.in = new SequenceInputStream(new StringBufferInputStream(putback), in);
            }
            /* Fake a status line and upgrade to HTTP/1.0 */
            statusLine = "HTTP/1.0 200 OK";
            return;
        }

        readHeaders(in);
        int code = getStatusCode();

        /* RFC 2616: 204 and 304 MUST NOT contain a message body. */
        switch (code)
        {
        case NoContentStatusCode:
        case NotModifiedStatusCode:
            /* Ignore the message body if it exists */
            if (containsHeaderField("Content-length") && !getHeaderField("Content-length").equals("0"))
            {
                System.out.println("RFC 2616: Ignoring message-body from " + code +
                                   " response - length " + getHeaderField("Content-length") + " " +
                                   getHeaderField("Server"));

                int contentLength = 0;
                try
                {
                    contentLength = Integer.parseInt(getHeaderField("Content-length"));
                }
                catch (NumberFormatException e)
                {
                }
                int n;
                byte buffer[] = new byte[8192];
                while ((n = in.read(buffer, 0, buffer.length)) > 0)
                {
                    /* ignore */
                }
                removeHeaderField("Content-length");
            }
            break;
        }
    }

    /** Returns whether the reply has any content.
     *
     * Replies with status code 204 "No Content" and 304 "Not Modified" are considered to have no content.
     * @return Returns false if the status code is 204 or 304, true otherwise.
     *
     */
    public boolean hasContent()
    {
        switch (getStatusCode())
        {
        case NoContentStatusCode:
        case NotModifiedStatusCode:
            return false;

        default:
            return true;
        }
    }

    /** Get the http protocol, i.e. "GET", "HEAD", "POST" or "PUT".
     * @return Protocol.
     */
    public String getProtocol()
    {
        StringTokenizer st = new StringTokenizer(statusLine);
        String protocol = (String) st.nextToken();
        return protocol;
    }

    /** Get the http status code.
     * @return Status code.
     */
    public int getStatusCode()
    {
        if (statusCode == -1)
        {
            StringTokenizer st = new StringTokenizer(statusLine);
            String protocol = (String) st.nextToken();
            String status = (String) st.nextToken();

            try
            {
                statusCode = Integer.parseInt(status);
            }
            catch (NumberFormatException e)
            {
                System.out.println("Malformed or missing status code");
                statusCode = 0;
            }
        }

        return statusCode;
    }

    private Hashtable headerParser(String header)
    {
        Hashtable table = new Hashtable();
        String type = getHeaderField(header);
        if (type == null)
        {
            return table;
        }

        StringTokenizer st = new StringTokenizer(type, ";");
        int count = 0;
        while (st.hasMoreTokens())
        {
            String token = st.nextToken();
            token = token.trim();
            String name;
            String value;
            int i = token.indexOf('=');
            if (i != -1)
            {
                name = token.substring(0, i);
                value = token.substring(i+1);
            }
            else
            {
                name = token;
                value = "";
            }

            if (count == 0)
            {
                table.put(header, name);
            }
            else
            {
                table.put(name, value);
            }

            count++;
        }

        return table;
    }

    /** Get the content type.
     * @return Content type.
     */
    public String getContentType()
    {
        Hashtable table = headerParser("Content-type");
        return(String) table.get("Content-type");
    }

    /** Get the boundary parameter from the Content-type header.
     * @return Boundary string.
     */
    public String getBoundary()
    {
        Hashtable table = headerParser("Content-type");
        return(String) table.get("boundary");
    }

    /** Get the value of the Transfer-Encoding header field.
     * @return Transfer encoding
     */
    public String getTransferEncoding()
    {
        Hashtable table = headerParser("Transfer-Encoding");
        return(String) table.get("Transfer-Encoding");
    }

    /** Get the chunk size from the input stream.
     *
     * The first line of the input stream must be an integer representing the chunk size.
     * @param in Input stream with the chunk size line.
     * @throws IOException Thrown if there are any errors reading the input stream.
     * @return Chunk size.
     */
    public int getChunkSize(InputStream in) throws IOException
    {
        String line = readLine(in);
        line = line.trim(); /* apache can have trailing spaces */
        int size = -1;
        try
        {
            size = Integer.valueOf(line, 16).intValue();
        }
        catch (NumberFormatException e)
        {
            System.out.println(e);
        }
        return size;
    }

    /** Read past the chunked footer.
     *
     * Reads lines from the input stream until end of stream or a line containing a ':' has been read.
     * @param in Input stream
     * @throws IOException Thrown if there are any errors reading from the stream.
     */
    public void getChunkedFooter(InputStream in) throws IOException
    {
        for (;;)
        {
            String line = readLine(in);
            if (line == null)
            {
                break;
            }
            int i = line.indexOf(':');
            if (i == -1)
            {
                break;
            }
        }
    }

    /** Get the http status line.
     * @return Status line.
     */
    public String getStatusLine()
    {
        return statusLine;
    }

    /** Set the http status line.
     * @param line Status line.
     */
    public void setStatusLine(String line)
    {
        this.statusLine = line;
    }

    /** Create a reply which redirects to the url.
     * @param url Url to redirect to.
     * @return Redirect reply.
     */
    public static Reply createRedirect(String url)
    {
        Reply r = new Reply();
        r.setStatusLine("HTTP/1.0 302 Moved Temporarily");
        r.setHeaderField("Location", url);
        return r;
    }
}
