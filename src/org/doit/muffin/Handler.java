/* $Id: Handler.java,v 1.3 1998/12/19 21:24:14 boyns Exp $ */

/*
 * Copyright (C) 1996-98 Mark R. Boyns <boyns@doit.org>
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.Socket;
import java.net.URL;
import org.doit.io.*;

/**
 * HTTP transaction handler.  A handler is created by muffin.Server for
 * each HTTP transaction.  Given a socket, the handler will deal with
 * the request, reply, and invoke request, reply, and content filters.
 *
 * @see muffin.Server
 * @author Mark Boyns
 */
class Handler extends Thread
{
    static final boolean DEBUG = false;
    
    ThreadGroup filterGroup = null;
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
    Handler(ThreadGroup t, Runnable r, Monitor m, FilterManager manager, Options options)
    {
	super(t, r);
	this.monitor = m;
	this.manager = manager;
	this.options = options;
    }

    /**
     * Start the handler.
     *
     * @param s a socket
     */
    void doit(Socket s)
    {
	socket = s;
	start();
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
	    catch (Exception e)
	    {
	    }
	}
    }

    void kill()
    {
	monitor.unregister(this);
	stop(); // DEPRECATION: can't stop anymore
    }

    public void run()
    {
	Thread.currentThread().setName("Handler("
					 + socket.getInetAddress().getHostAddress()
					 + ")");
	filterGroup = new ThreadGroup("Filters(" +
				       socket.getInetAddress().getHostAddress()
				       + ")");

	try
	{
	    client = new Client(socket);
	}
	catch (Exception e)
	{
	    return;
	}

	boolean keepAlive = false;
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
	    catch (Exception e)
	    {
		if (client != null && request != null)
		{
		    error(client.getOutputStream(), e, request);
		}
		e.printStackTrace();
		break; /* XXX */
	    }
	}
	while (keepAlive);
	
	monitor.unregister(this);
	close();
    }

    boolean processRequest() throws Exception
    {
	boolean keepAlive = false;
	
	while (reply == null)
	{
	    boolean secure = false;

	    manager.checkAutoConfig(request.getURL());

	    /* Obtain a list of filters to use. */
	    filterList = manager.createFilters();
	    
	    if (request.getCommand().equals("CONNECT"))
	    {
		secure = true;
	    }
	    else if (request.getURL().startsWith("/"))
	    {
		request.setURL("http://" + options.getString("muffin.host")
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
	    keepAlive = (request.containsHeaderField("Proxy-Connection")
			 && request.getHeaderField("Proxy-Connection").equals("Keep-Alive"));

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
		if (url.endsWith("/") || url.endsWith(".html") || url.endsWith(".htm"))
		{
		    reply.setHeaderField("Content-type", "text/html");
		}
	    }

	    /* update reply */
	    monitor.update(this);

	    /* Filter the reply. */
	    if (!options.getBoolean("muffin.passthru"))
	    {
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
		client.write(reply);
		Copy cp = new Copy(client.getInputStream(), https.getOutputStream());
		Thread thread = new Thread(cp);
		thread.start();
		flushCopy(https.getInputStream(), client.getOutputStream(), -1, true);
		client.close();
	    }
	    else if (reply.hasContent())
	    {
		try
		{
		    processContent();
		}
		catch (IOException e)
		{
		    e.printStackTrace();
		    
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
		    return false; /* XXX */
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
    
    void processContent() throws IOException
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
	
	if (options.getBoolean("muffin.passthru"))
	{
	    client.write(reply);
	    copy(in, client.getOutputStream(), contentLength, true);
	}
	else if (contentNeedsFiltration())
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

    void filter(InputStream in, OutputStream out, int length, boolean monitored) throws java.io.IOException
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
		
		    Thread t = new Thread(filterGroup, filter);
		    try
		    {
			t.setPriority(Thread.MIN_PRIORITY);
		    }
		    catch (Exception e)
		    {
			e.printStackTrace();
		    }
		    t.start();

		    inputObjects = io;
		}
	    }
	}

	srcObjects.setSourceInputStream(in);
	srcObjects.setSourceLength(length);

	Thread srcThread = new Thread(srcObjects);
	srcThread.setName("ObjectStream Source("
			   + socket.getInetAddress().getHostAddress()
			   + ")");
	srcThread.start();
	    
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
	throws java.io.IOException
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
     * @param in InputStream
     * @param out OutputStream
     * @param monitored Update the Monitor
     */
    void flushCopy(InputStream in, OutputStream out, int length, boolean monitored)
	throws java.io.IOException
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
    void copy(InputObjectStream in, OutputStream out, boolean monitored) throws java.io.IOException
    {
	Object obj;
	long start = System.currentTimeMillis();
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
		out.write(bytes.getBytes(), 0, bytes.length());
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
	    }
	    bytesPerSecond = currentLength / ((System.currentTimeMillis() - start) / 1000.0);
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
