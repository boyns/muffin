/* $Id: Handler.java,v 1.11 2000/03/08 15:18:00 boyns Exp $ */

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

import java.io.*;
import java.util.zip.*;
import java.net.Socket;
import java.net.URL;
import org.doit.io.*;
import org.doit.util.*;

/**
 * HTTP transaction handler.  A handler is created by muffin.Server for
 * each HTTP transaction.  Given a socket, the handler will deal with
 * the request, reply, and invoke request, reply, and content filters.
 *
 * @see muffin.Server
 * @author Mark Boyns
 */
class Handler implements Runnable
{
    static final boolean DEBUG = false;
    
    Monitor monitor = null;
    FilterManager manager = null;
    Options options = null;
    Client client = null;
    Socket socket = null;
    Request request = null;
    Reply reply = null;
    HttpRelay http = null;
    int currentLength = -1;
    int contentLength = -1;
    Filter filterList[];
    long idle = 0;
    double bytesPerSecond = 0;

    /**
     * Create a Handler.
     */
    Handler(Monitor m, FilterManager manager, Options options, Socket socket)
    {
	this.monitor = m;
	this.manager = manager;
	this.options = options;
	this.socket = socket;
    }

    /**
     * Close all connections associated with this handler.
     */
    synchronized void close()
    {
	if (client != null)
	{
	    client.close();
	    client = null;
	}
	if (http != null)
	{
	    http.close();
	    http = null;
	}
    }

    /**
     * Flush all data to the client.
     */
    void flush()
    {
	if (client != null)
	{
	    try
	    {
		client.getOutputStream().flush();
	    }
	    catch (IOException e)
	    {
		e.printStackTrace();
	    }
	}
    }

    public void run()
    {
	boolean keepAlive = false;
	Exception reason = null;

	Thread.currentThread().setName("Handler("
					 + socket.getInetAddress().getHostAddress()
					 + ")");

	try
	{
	    client = new Client(socket);
	    client.setTimeout(options.getInteger("muffin.readTimeout"));
	}
	catch (IOException e)
	{
	    e.printStackTrace();
	    return;
	}

	try
	{
	    monitor.register(this);

	    do
	    {
		request = null;
		reply = null;
		filterList = null;
		idle = System.currentTimeMillis();
		monitor.update(this);

		try
		{
		    request = client.read();
		}
		catch (IOException e)
		{
		    e.printStackTrace();
		    break;
		}

		idle = 0;
		monitor.update(this);

		try
		{
		    keepAlive = processRequest();
		}
		catch (IOException ioe)
		{
		    reason = ioe;
		    keepAlive = false;
		}
		catch (FilterException fe)
		{
		    reason = fe;
		    keepAlive = false;
		}

		if (request != null && reply != null)
		{
		    // XXX insert the number of bytes read into the
		    // reply content-length for logging.
		    if (reply != null && currentLength > 0)
		    {
			reply.setHeaderField("Content-length", currentLength);
		    }

		    LogFile logFile = Main.getLogFile();
		    if (logFile != null)
		    {
			logFile.log(request, reply);
		    }
		}
	    }
	    while (keepAlive);
	}
	finally
	{
	    monitor.unregister(this);
	}

	if (reason != null && reason.getMessage().indexOf("Broken pipe") == -1)
	{
	    if (client != null && request != null)
	    {
		error(client.getOutputStream(), reason, request);
	    }

	    // don't print filter exceptions
	    if (! (reason instanceof FilterException))
	    {
		reason.printStackTrace();
	    }
	}
	
	close();
    }

    boolean processRequest() throws IOException, FilterException
    {
	boolean keepAlive = false;
	
	while (reply == null)
	{
	    boolean secure = false;
	    boolean uncompress = false;

	    filterList = manager.createFilters(request.getURL());

	    if (request.getCommand().equals("CONNECT"))
	    {
		secure = true;
	    }
	    else if (request.getURL().startsWith("/"))
	    {
		request.setURL("http://" + Main.getMuffinHost()
				+ ":" + options.getString("muffin.port")
				+ request.getURL());
	    }
	    else if (request.getURL().startsWith("https://"))
	    {
		System.out.println("Netscape keep-alive bug: " + request.getURL());
		return false;
	    }
	    else if (! request.getURL().startsWith("http://"))
	    {
		System.out.println("Unknown URL: " + request.getURL());
		return false;
	    }

	    /* Client wants Keep-Alive */
	    if (options.getBoolean("muffin.proxyKeepAlive"))
	    {
		keepAlive = (request.containsHeaderField("Proxy-Connection")
			     && request.getHeaderField("Proxy-Connection").equals("Keep-Alive"));
	    }

	    /* Filter the request. */
	    if (!options.getBoolean("muffin.passthru"))
	    {
		/* Redirect the request if necessary */
		String location = redirect(request);
		if (location != null)
		{
		    Reply r = Reply.createRedirect(location);
		    client.write(r);
		    return keepAlive;
		}

		filter(request);
	    }

	    /* First look for any HttpFilters */
	    http = createHttpFilter(request);
	    if (http == null)
	    {
		/* None found.  Use http or https relay. */
		if (secure)
		{
		    http = createHttpsRelay();
		}
		else
		{
		    http = createHttpRelay();
		}
	    }

	    try
	    {
		http.sendRequest(request);
		if (http instanceof Http)
		{
		    ((Http)http).setTimeout(options.getInteger("muffin.readTimeout"));
		}
		reply = http.recvReply(request);
	    }
	    catch (RetryRequestException e)
	    {
		http.close();
		http = null;
		continue; /* XXX */
	    }

	    /* Guess content-type if there aren't any headers.
	       Probably an upgraded HTTP/0.9 reply. */
	    if (reply.headerCount() == 0)
	    {
		String url = request.getURL();
		if (url.endsWith("/")
		    || url.endsWith(".html") || url.endsWith(".htm"))
		{
		    reply.setHeaderField("Content-type", "text/html");
		}
	    }

	    /* update reply */
	    monitor.update(this);

	    /* Filter the reply. */
	    if (!options.getBoolean("muffin.passthru"))
	    {
		/* uncompress gzip encoded html so it can be filtered */
		if (!options.getBoolean("muffin.dontUncompress")
		    && "text/html".equals(reply.getHeaderField("Content-type")))
		{
		    String encoding = reply.getHeaderField("Content-Encoding");
		    if (encoding != null && encoding.indexOf("gzip") != -1)
		    {
			reply.removeHeaderField("Content-Encoding");
			reply.removeHeaderField("Content-length");
			uncompress = true;
		    }
		}

		filter(reply);
	    }

	    reply.removeHeaderField("Proxy-Connection");
	    if (keepAlive && reply.containsHeaderField("Content-length"))
	    {
		reply.setHeaderField("Proxy-Connection", "Keep-Alive");
	    }
	    else
	    {
		keepAlive = false;
	    }

	    currentLength = -1;
	    contentLength = -1;
	    try
	    {
		contentLength = Integer.parseInt(reply.getHeaderField("Content-length"));
	    }
	    catch (NumberFormatException e)
	    {
	    }

	    /* update content-length reply */
	    monitor.update(this);

	    if (secure)
	    {
		Https https = (Https) http;
		int timeout = options.getInteger("muffin.readTimeout");
		
		client.write(reply);

		try
		{
		    client.setTimeout(timeout);
		    https.setTimeout(timeout);
		
		    Copy cp = new Copy(client.getInputStream(), https.getOutputStream());
		    ReusableThread thread = Main.getThread();
		    thread.setRunnable(cp);
		    flushCopy(https.getInputStream(), client.getOutputStream(), -1, true);
		    client.close();
		}
		catch (InterruptedIOException iioe)
		{
		    // ignore socket timeout exceptions
		}
	    }
	    else if (reply.hasContent())
	    {
		try
		{
		    processContent(uncompress);
		}
		catch (IOException e)
		{
		    if (http instanceof Http)
		    {
			((Http)http).reallyClose();
		    }
		    else
		    {
			http.close();
		    }
		    http = null;

		    client.close();
		    client = null;

		    throw e;
		    //return false; /* XXX */
		}

		/* Document contains no data. */
		if (contentLength == 0)
		{
		    client.close();
		}
	    }
	    else
	    {
		client.write(reply);
	    }

	    http.close();
	}

	return keepAlive;
    }

    HttpRelay createHttpsRelay() throws IOException
    {
	HttpRelay http;

	if (options.useHttpsProxy())
	{
	    http = new Https(options.getString("muffin.httpsProxyHost"),
			      options.getInteger("muffin.httpsProxyPort"),
			      true);
	}
	else
	{
	    http = new Https(request.getHost(), request.getPort());
	}

	return http;
    }
    
    HttpRelay createHttpRelay() throws IOException
    {
	HttpRelay http;
	
	if (Httpd.sendme(request))
	{
	    http = new Httpd(socket);
	}
	else if (options.useHttpProxy())
	{
	    http = Http.open(options.getString("muffin.httpProxyHost"),
			      options.getInteger("muffin.httpProxyPort"),
			      true);
	}
	else
	{
	    http = Http.open(request.getHost(), request.getPort());
	}

	return http;
    }

    HttpRelay createHttpFilter(Request request)
    {
	for (int i = 0; i < filterList.length; i++)
	{
	    if (filterList[i] instanceof HttpFilter)
	    {
		HttpFilter filter = (HttpFilter) filterList[i];
		if (filter.wantRequest(request))
		{
		    return filter;
		}
	    }
	}

	return null;
    }

    InputStream readChunkedTransfer(InputStream in) throws IOException
    {
	ByteArrayOutputStream chunks = new ByteArrayOutputStream(8192);
	int size = 0;

	contentLength = 0;
	while ((size = reply.getChunkSize(in)) > 0)
	{
	    contentLength += size;
	    copy(in, chunks, size, true);
	    reply.readLine(in);
	}
	reply.getChunkedFooter(in);

	reply.removeHeaderField("Transfer-Encoding");
	reply.setHeaderField("Content-length", contentLength);

	return new ByteArrayInputStream(chunks.toByteArray());
    }
    
    void processContent(boolean uncompress) throws IOException
    {
	InputStream in;
	boolean chunked = false;
	
	if (reply.containsHeaderField("Transfer-Encoding")
	    && reply.getTransferEncoding().equals("chunked"))
	{
	    in = readChunkedTransfer(reply.getContent());
	    chunked = true;
	}
	else
	{
	    in = reply.getContent();
	}

	if (in == null)
	{
	    System.out.println("No inputstream");
	    return;
	}
	else if (uncompress)
	{
	    in = new GZIPInputStream(in);
	}

	if (options.getBoolean("muffin.passthru"))
	{
	    client.write(reply);
	    copy(in, client.getOutputStream(), contentLength, true);
	}
	else if (contentNeedsFiltration())
	{
	    if (options.getBoolean("muffin.proxyKeepAlive"))
	    {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream(8192);
		filter(in, buffer, contentLength, chunked ? false : true);
		reply.setHeaderField("Content-length", buffer.size());
		client.write(reply);
		copy(new ByteArrayInputStream(buffer.toByteArray()),
		     client.getOutputStream(), buffer.size(), false);
	    }
	    else
	    {
		reply.removeHeaderField("Content-length");
		client.write(reply);
		filter(in, client.getOutputStream(), -1, chunked ? false : true);
	    }
	}
	else
	{
	    client.write(reply);
	    copy(in, client.getOutputStream(), contentLength, true);
	}
    }

    /**
     * Pass a request through the redirect filters.
     *
     * @param r a request
     */
    String redirect(Request r)
    {
	for (int i = 0; i < filterList.length; i++)
	{
	    if (filterList[i] instanceof RedirectFilter)
	    {
		RedirectFilter rf = (RedirectFilter) filterList[i];
		if (rf.needsRedirection(r))
		{
		    String location = rf.redirect(r);
		    return location;
		}
	    }
	}
	return null;
    }
	    
    /**
     * Pass a reply through the filters.
     *
     * @param r a reply
     */
    void filter(Reply r) throws FilterException
    {
	for (int i = 0; i < filterList.length; i++)
	{
	    if (filterList[i] instanceof ReplyFilter)
	    {
		((ReplyFilter)(filterList[i])).filter(r);
	    }
	}
    }

    /**
     * Pass a request through the filters.
     *
     * @param r a request
     */
    void filter(Request r) throws FilterException
    {
	for (int i = 0; i < filterList.length; i++)
	{
	    if (filterList[i] instanceof RequestFilter)
	    {
		((RequestFilter)(filterList[i])).filter(r);
	    }
	}
    }

    boolean contentNeedsFiltration()
    {
	for (int i = 0; i < filterList.length; i++)
	{
	    if (filterList[i] instanceof ContentFilter)
	    {
		ContentFilter filter = (ContentFilter) filterList[i];
		if (filter.needsFiltration(request, reply))
		{
		    return true;
		}
	    }
	}
	return false;
    }

    void filter(InputStream in, OutputStream out, int length, boolean monitored)
	throws IOException
    {
	InputObjectStream inputObjects = new InputObjectStream();
	SourceObjectStream srcObjects;

	if (reply.containsHeaderField("Content-type") &&
	    reply.getContentType().equals("text/html"))
	{
	    srcObjects = new HtmlObjectStream(inputObjects);
	}
	else
	{
	    srcObjects = new SourceObjectStream(inputObjects);
	}
	
	for (int i = 0; i < filterList.length; i++)
	{
	    if (filterList[i] instanceof ContentFilter)
	    {
		ContentFilter filter = (ContentFilter) filterList[i];
		if (filter.needsFiltration(request, reply))
		{
		    OutputObjectStream oo = new OutputObjectStream();
		    InputObjectStream io = new InputObjectStream(oo);

		    filter.setInputObjectStream(inputObjects);
		    filter.setOutputObjectStream(oo);

		    ReusableThread rt = Main.getThread();
		    rt.setPriority(Thread.MIN_PRIORITY);
		    rt.setRunnable(filter);

		    inputObjects = io;
		}
	    }
	}

	srcObjects.setSourceInputStream(in);
	srcObjects.setSourceLength(length);

	ReusableThread srcThread = Main.getThread();
	srcThread.setName("ObjectStream Source("
			   + socket.getInetAddress().getHostAddress()
			   + ")");
	srcThread.setPriority(Thread.MIN_PRIORITY);
	srcThread.setRunnable(srcObjects);
	    
	Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
	copy(inputObjects, out, monitored);
    }

    /**
     * Return the content length.
     */
    int getTotalBytes()
    {
	return contentLength > 0 ? contentLength : 0;
    }

    /**
     * Return the number of bytes read so far.
     */
    int getCurrentBytes()
    {
	return currentLength > 0 ? currentLength : 0;
    }

    /**
     * Send a error message to the client.
     *
     * @param out client
     * @param e exception that occurred
     * @param r request
     */
    void error(OutputStream out, Exception e, Request r)
    {
	StringBuffer buf = new StringBuffer();
	buf.append("While trying to retrieve the URL: <a href=\""+r.getURL()+"\">"+r.getURL()+"</a>\r\n");
	buf.append("<p>\r\nThe following error was encountered:\r\n<p>\r\n");
	buf.append("<ul><li>" + e.toString() + "</ul>\r\n");
	String s = new HttpError(options, 400, buf.toString()).toString();
	try
	{
	    out.write(s.getBytes(), 0, s.length());
	    out.flush();
	}
	catch (Exception ex)
	{
	}
    }

    /**
     * Copy in to out.
     *
     * @param in InputStream
     * @param out OutputStream
     * @param monitored Update the Monitor
     */
    void copy(InputStream in, OutputStream out, int length, boolean monitored)
	throws IOException
    {
	if (length == 0)
	{
	    return;
	}

	int n;
	byte buffer[] = new byte[8192];
	long start = System.currentTimeMillis();
	long now = 0, then = start;
	
	bytesPerSecond = 0;

	if (monitored)
	{
	    currentLength = 0;
	}
	
	for (;;)
	{
	    n = (length > 0) ? Math.min(length, buffer.length) : buffer.length;
	    n = in.read(buffer, 0, n);
	    if (n < 0)
	    {
		break;
	    }
	    
	    out.write(buffer, 0, n);

	    if (monitored)
	    {
		currentLength += n;
		monitor.update(this);
	    }

	    now = System.currentTimeMillis();
	    bytesPerSecond = currentLength / ((now - start) / 1000.0);

	    // flush after 1 second
	    if (now - then > 1000)
	    {
		out.flush();
	    }

	    if (length != -1)
	    {
		length -= n;
		if (length == 0)
		{
		    break;
		}
	    }

	    then = now;
	}

	out.flush();

	if (DEBUG)
	{
	    System.out.println(currentLength + " bytes processed in "
			       + ((System.currentTimeMillis() - start)
				  / 1000.0) + " seconds "
				+ ((int)bytesPerSecond / 1024) + " kB/s");
	}
    }

    /**
     * Copy in to out.
     *
     * @param in InputStream
     * @param out OutputStream
     * @param monitored Update the Monitor
     */
    void flushCopy(InputStream in, OutputStream out, int length, boolean monitored)
	throws IOException
    {
	if (length == 0)
	{
	    return;
	}

	int n;
	byte buffer[] = new byte[8192];
	long start = System.currentTimeMillis();
	bytesPerSecond = 0;
	
	if (monitored)
	{
	    currentLength = 0;
	}
	
	for (;;)
	{
	    n = (length > 0) ? Math.min(length, buffer.length) : buffer.length;
	    n = in.read(buffer, 0, n);
	    if (n < 0)
	    {
		break;
	    }

	    out.write(buffer, 0, n);
	    out.flush();
	    if (monitored)
	    {
		currentLength += n;
		monitor.update(this);
	    }
	    bytesPerSecond = currentLength / ((System.currentTimeMillis() - start) / 1000.0);
	    if (length != -1)
	    {
		length -= n;
		if (length == 0)
		{
		    break;
		}
	    }
	}
	out.flush();

	if (DEBUG)
	{
	    System.out.println(currentLength + " bytes processed in "
				+ ((System.currentTimeMillis() - start) / 1000.0) + " seconds "
				+ ((int)bytesPerSecond / 1024) + " kB/s");
	}
    }

    /**
     * Copy in to out.
     *
     * @param in InputObjectStream
     * @param out OutputStream
     * @param monitored Update the Monitor
     */
    void copy(InputObjectStream in, OutputStream out, boolean monitored)
	throws IOException
    {
	Object obj;
	long start = System.currentTimeMillis();
	long now = 0, then = start;

	bytesPerSecond = 0;

	if (monitored)
	{
	    currentLength = 0;
	}
	
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
		currentLength += bytes.length();
	    }
	    else if (obj instanceof Byte)
	    {
		Byte b = (Byte) obj;
		out.write(b.byteValue());
		currentLength++;
	    }
	    else
	    {
		System.out.println("Unknown object: " + obj.toString());
	    }

	    if (monitored)
	    {
		monitor.update(this);
		Thread.currentThread().yield();
	    }

	    now = System.currentTimeMillis();
	    bytesPerSecond = currentLength / ((now - start) / 1000.0);

	    // flush after 1 second
	    if (now - then > 1000)
	    {
		out.flush();
	    }

	    then = now;
	}
	out.flush();

	if (DEBUG)
	{
	    System.out.println(currentLength + " bytes filtered in "
				+ ((System.currentTimeMillis() - start) / 1000.0) + " seconds "
				+ ((int)bytesPerSecond / 1024) + " kB/s");
	}
    }

    /**
     * Return a string represenation of the hander's state.
     */
    public String toString()
    {
	StringBuffer str = new StringBuffer();
	str.append("CLIENT ");
	str.append(socket.getInetAddress().getHostAddress());
	str.append(":");
	str.append(socket.getPort());
	str.append(" - ");
	if (request == null)
	{
	    str.append("idle " + ((System.currentTimeMillis() - idle) / 1000.0) + " sec");
	}
	else
	{
	    if (reply != null && currentLength > 0)
	    {
		str.append("(");
		str.append(currentLength);
		if (contentLength > 0)
		{
		    str.append("/");
		    str.append(contentLength);
		}
		str.append(" ");
		str.append(((int)bytesPerSecond / 1024) + " kB/s");
		str.append(") ");
	    }
	    str.append(request.getURL());
	}
	return str.toString();
    }
}
