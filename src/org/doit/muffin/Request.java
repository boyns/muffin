/* $Id: Request.java,v 1.14 2003/06/11 20:57:36 flefloch Exp $ */

/*
 * Copyright (C) 1996-2003 Mark R. Boyns <boyns@doit.org>
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
import java.io.OutputStream;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.doit.muffin.regexp.Factory;
import org.doit.muffin.regexp.Pattern;
import org.doit.muffin.regexp.Matcher;
import org.doit.io.*;

/** Http/https request.
 * @author Mark Boyns
 */
public class Request extends Message
{
    private static Pattern httpRegex;

    private static final String HttpPrefix = "http://";
    private static final String HttpsPrefix = "https://";
    private static final int HttpPrefixLength = HttpPrefix.length();
    private static final int HttpsPrefixLength = HttpsPrefix.length();

    private int defaultPort;
    private String command = null;
    private String url = null;
    private String protocol = null;
    private byte[] data = null;
    private Client client = null;
    private Hashtable log;
    private Vector logHeaders;

    static {
		httpRegex = Factory.instance().getPattern("^(http|https):", true);
    }

    {
        defaultPort = 80;
    }
    /** Creates an http/https request.
     * @param c The client making the request.
     */
    protected Request(Client c)
    {
        client = c;
    }

    /** Read a request from an input stream.
     *
     * The input stream must consist of an http or https status line, 
     * followed by the http header lines, finally followed by any data.
     * @param in The input stream.
     * @throws IOException Thrown if there is an error reading the request.
     */
    void read(InputStream in) throws IOException
    {
        statusLine = readLine(in);
        if (statusLine == null || statusLine.length() == 0)
        {
            throw new IOException("Empty request");
        }

        StringTokenizer st = new StringTokenizer(statusLine);
        try
        {
            command = (String) st.nextToken();
            url = (String) st.nextToken();
            protocol = (String) st.nextToken();
        }
        catch(NoSuchElementException nsee)
        {
            throw new IOException("Malformed request, bad status line: "+statusLine);
        }

        if (!url.startsWith("http"))
        {
            Matcher match = httpRegex.getMatch(url);
            if (match != null)
            {
                url =
                    url
                        .substring(match.getStartIndex(), match.getEndIndex())
                        .toLowerCase()
                        + url.substring(match.getEndIndex());
            }
        }

        readHeaders(in);

        if ("POST".equals(command) || "PUT".equals(command))
        {
            try
            {
                int n = Integer.parseInt(getHeaderField("Content-length"));
                data = new byte[n];
                int offset = 0;
                while (offset < data.length)
                {
                    n = in.read(data, offset, data.length - offset);
                    if (n < 0)
                    {
                        throw new IOException(
                            "Not enough " + command + " data");
                    }
                    offset += n;
                }
            }
            catch (NumberFormatException e)
            {
                System.out.println(
                    "Malformed or missing " + command + " Content-length");
            }
        }
    }

    /** Write request headers to an output stream.
     *
     * This data written does not include the protocol's status line or data.
     * @param out The output stream.
     * @throws IOException Thrown is there is an error reading the stream.
     */
    public void write(OutputStream out) throws IOException
    {
        super.write(out);
        if (data != null)
        {
            out.write(data);
            out.flush();
        }
    }

    /** Get the status line for the request.
     *
     * This line is from the raw http protocol, e.g. "GET http://org.doit.muffin/ HTTP".
     *
     * @return The status line.
     */
    public String getRequest()
    {
        return statusLine;
    }

    /** Get the command for the request, e.g. "GET", "HEAD", "POST", or "PUT".
     * @return The command.
     */
    public String getCommand()
    {
        return command;
    }

    /**
     * Is this a SSL tunneling request?
     * @return true if command is "CONNECT"
     */
    public boolean isSecure()
    {
        return getCommand().equals("CONNECT");
    }

    /** Set the command for a request, e.g. "GET", "HEAD", "POST", or "PUT".
     * @param command The command.
     */
    public void setCommand(String command)
    {
        this.command = command;
    }

    /** Get the url.
     * @return Url.
     */
    public String getURL()
    {
        return url;
    }

    /** Set the url.
     * @param url Url.
     */
    public void setURL(String url)
    {
        this.url = url;
    }

    /** Get the protocol, e.g. "HTTP" or "HTTPS".
     * @return Protocol.
     */
    public String getProtocol()
    {
        return protocol;
    }

    /** Set the protocol.
     * @param protocol The protocol.
     */
    public void setProtocol(String protocol)
    {
        this.protocol = protocol;
    }

    /** Get the host to which the request should be sent.
     * @return The host.
     */
    public String getHost()
    {
        String s = stripUrl(getURL());
        int at = s.indexOf('@');
        if (at != -1)
        {
            s = s.substring(at + 1);
        }

        if (s.indexOf(':') != -1)
        {
            return s.substring(0, s.indexOf(':'));
        }

        return s;
    }

    private static String stripUrl(String url)
    {
        String s = null;
        if (url.startsWith(HttpPrefix))
        {
            s =
                url.substring(
                    HttpPrefixLength,
                    url.indexOf('/', HttpPrefixLength));
        }
        else if (url.startsWith(HttpsPrefix))
        {
            s =
                url.substring(
                    HttpsPrefixLength,
                    url.indexOf('/', HttpsPrefixLength));

        }
        else
        {
            s = url;
        }
        return s;
    }

    /** Get the port.
     * @return The port.
     */
    public int getPort()
    {
        String s = stripUrl(getURL());
        int port = defaultPort;
        int at = s.indexOf('@');
        if (at != -1)
        {
            s = s.substring(at + 1);
        }

        if (s.indexOf(':') != -1)
        {

            s = s.substring(s.indexOf(':') + 1);
            try
            {
                port = Integer.parseInt(s);
            }
            catch (NumberFormatException e)
            {
                System.out.println("Invalid port in " + s);
            }
        }
        return port;

    }

    /** Get the data.
     *
     * Only POST and PUT requests will have data.
     *
     * @return The data.
     */
    public String getData()
    {
        if (data == null)
        {
            return null;
        }
        return new String(data);
    }

    /** Get the path.
     *
     * This is an http path, e.g. "/index.html".
     *
     * @return The path.
     */
    public String getPath()
    {
        String str = getURL();
        int pos = 0;
        for (int i = 0; i < 3; i++)
        {
            pos = str.indexOf('/', pos);
            pos++;
        }
        pos--;
        if (pos >= 0)
            return str.substring(pos);
        else
            return "/";
    }

    /** Get the document, i.e. the last component of the path, after any '/' characters.
     * @return The document.
     */
    public String getDocument()
    {
        String path = getPath();
        int n = path.lastIndexOf('/');
        if (n == path.length() - 1)
        {
            n = path.lastIndexOf('/', n - 1);
        }
        if (n < 0)
        {
            return "/";
        }
        else
        {
            return path.substring(n + 1);
        }
    }

    /** Get the client connection.
     * @return The client.
     */
    public Client getClient()
    {
        return client;
    }

    /** Get the query string, i.e. the part of the url after any '?'.
     * @return The query string.
     */
    public String getQueryString()
    {
        String path = getPath();
        int n = path.indexOf('?');
        if (n < 0)
        {
            return null;
        }
        return path.substring(n + 1);
    }

    /** Add a log entry for this request.
     * @param header Header for the log entry.
     * @param message Log message.
     */
    public synchronized void addLogEntry(String header, String message)
    {
        if (log == null)
        {
            log = new Hashtable();
            logHeaders = new Vector();
        }

        Vector v = (Vector) log.get(header);
        if (log.get(header) == null)
        {
            v = new Vector();
            log.put(header, v);
            logHeaders.addElement(header);
        }
        v.addElement(message);
    }

    /** Get the log headers.
     * @return All headers for log entries.
     */
    public Enumeration getLogHeaders()
    {
        return logHeaders != null ? logHeaders.elements() : null;
    }

    /** Get the entries for a specified header.
     * @param  The header for which to get log entries.
     * @return The log entries for this header.
     */
    public Enumeration getLogEntries(String header)
    {
        return log != null ? ((Vector) log.get(header)).elements() : null;
    }
    /**
     * Returns the defaultPort.
     * @return int
     */
    public int getDefaultPort()
    {
        return defaultPort;
    }

    /**
     * Sets the defaultPort.
     * @param defaultPort The defaultPort to set
     */
    public void setDefaultPort(int defaultPort)
    {
        this.defaultPort = defaultPort;
    }

}
