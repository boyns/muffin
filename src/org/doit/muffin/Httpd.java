/* $Id: Httpd.java,v 1.7 2000/03/08 15:18:36 boyns Exp $ */

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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Vector;
import java.net.Socket;
import org.doit.util.Base64;

class Httpd extends HttpConnection
{
    static Options options = null;
    static FilterManager manager = null;
    static Monitor monitor = null;
    static Server server = null;
    
    Request request = null;

    Httpd(Socket socket) throws IOException
    {
	super(socket);
    }

    public void sendRequest(Request request) throws IOException, RetryRequestException
    {
	this.request = request;
    }

    String decode(String str)
    {
	StringBuffer buf = new StringBuffer();
	for (int i = 0; i < str.length(); i++)
	{
	    int ch = str.charAt(i);
	    if (ch == '+')
	    {
		buf.append(" ");
	    }
	    else if (ch == '%')
	    {
		try
		{
		    int val = Integer.parseInt(str.substring(i+1, i+3), 16);
		    buf.append((char)val);
		}
		catch (NumberFormatException e)
		{
		}
		i+=2;
	    }
	    else
	    {
		buf.append((char)ch);
	    }
	}
	return buf.toString();
    }

    String getDateString()
    {
	String str;
	
	SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US); 
	TimeZone tz = TimeZone.getDefault();
	format.setTimeZone(tz.getTimeZone("GMT"));
	str = format.format(new Date());
	
	return str;
    }

    static String head(String title)
    {
	StringBuffer html = new StringBuffer();
	html.append("<title>" + title + "</title><body bgcolor="
		     + options.getString("muffin.bg") + " text="
		     + options.getString("muffin.fg") + ">\n");
	html.append("<h1>" + title + "</h1>\n");
	html.append("<hr size=4 noshade>\n");
	return html.toString();
    }

    static String tail()
    {
	StringBuffer html = new StringBuffer();
	html.append("<p><hr size=4 noshade>\n");
	html.append(getGenerated());
	html.append("</body>\n");
	return html.toString();
    }

    String admin()
    {
	StringBuffer html = new StringBuffer();
	html.append(head("Muffin: Remote Admin"));
	
	html.append("<ul>\n");
	html.append("<li><a href=/admin/configs>Configurations</a>\n");
	html.append("<li><a href=/admin/connections>Connections</a>\n");
	html.append("<li><a href=/admin/vm>Virtual Machine</a>\n");
	html.append("</ul>\n");

	html.append(tail());
	return html.toString();
    }

    String configs()
    {
	StringBuffer html = new StringBuffer();
	html.append(head("Muffin: Configurations"));

	html.append("<ul>\n");
	Enumeration e = manager.configs.sortedKeys();
	while (e.hasMoreElements())
	{
	    String name = (String) e.nextElement();
	    html.append("<li>");
	    html.append("<a href=/admin/filters?config=" + name + ">");
	    html.append(name);
	    html.append("</a>");
	    html.append("\n");
	}
	html.append("</ul>\n");

	html.append("<form method=POST action=/admin/createConfig>\n");
	html.append("<input name=config type=text size=10>\n");
	html.append("<input type=submit value=New>\n");
	html.append("</form>\n");

	html.append("<p><hr><b>Auto Configuration</b></b><br>\n");
	html.append("<form method=POST action=/admin/autoConfig>\n");
	html.append("<textarea name=text rows=10 cols=50>\n");
	for (int i = 0; i < manager.configs.autoConfigPatterns.size(); i++)
	{
	    html.append(manager.configs.autoConfigPatterns.elementAt(i).toString());
	    html.append("\t");
	    html.append(manager.configs.autoConfigNames.elementAt(i).toString());
	    html.append("\n");
	}
	html.append("</textarea>\n");
	html.append("<br><input type=submit value=Apply>\n");
	html.append("</form>\n");

	html.append(tail());
	return html.toString();
    }
	
    String connections()
    {
	StringBuffer html = new StringBuffer();
	html.append(head("Muffin: Connections"));

	Enumeration e;

	e = monitor.enumerate();
	while (e.hasMoreElements())
	{
	    Object obj = e.nextElement();
	    html.append(obj.toString());
	    html.append("<br>\n");
	}

	e = Http.enumerate();
	while (e.hasMoreElements())
	{
	    Object obj = (Object) e.nextElement();
	    html.append(obj.toString());
	    html.append("<br>\n");
	}

	html.append(tail());
	return html.toString();
    }
    
    String filters(String config)
    {
	StringBuffer html = new StringBuffer();
	html.append(head("Muffin: Filters " + config));

	Vector supported = manager.getSupportedFilters(config);
	Vector enabled = manager.getEnabledFilters(config);

	html.append("<table><tr><td>\n");
	html.append("<h2>Supported Filters</h2>\n");
	html.append("<form method=POST action=/admin/enable>\n");
	html.append("<input type=hidden name=config value=\"" + config + "\">\n");
	html.append("<select size=10 name=filter>\n");
	for (int i = 0; i < supported.size(); i++)
	{
	    html.append("<option>" + (String) supported.elementAt(i) + "\n");
	}
	html.append("</select>\n");
	html.append("<br><input type=submit value=Enable>\n");
	html.append("</form>\n");
	
	html.append("</td><td>\n");

	html.append("<h2>Enabled Filters</h2>\n");
	html.append("<form method=POST action=/admin/disable>\n");
	html.append("<input type=hidden name=config value=\"" + config + "\">\n");
	html.append("<select size=10 name=index>\n");
	for (int i = 0; i < enabled.size(); i++)
	{
	    FilterFactory ff = (FilterFactory) enabled.elementAt(i);
	    String filter = manager.shortName((ff.getClass()).getName());
	    html.append("<option value=" + i + ">" + filter + "\n");
	}
	html.append("</select>\n");
	html.append("<br>");
	html.append("<input type=submit value=Disable>\n");
	html.append("</form>\n");

	html.append("</td></tr></table>\n");

	for (int i = 0; i < enabled.size(); i++)
	{
	    FilterFactory ff = (FilterFactory) enabled.elementAt(i);
	    String filter = manager.shortName((ff.getClass()).getName());
	    Prefs prefs = ff.getPrefs();

	    html.append("<hr>\n");
	    html.append("<h2>" + filter + "</h2>");
	    html.append("<a href=/doc/"
			 + filter.substring(filter.lastIndexOf(".") + 1)
			 + ".txt>[Help]</a><br>\n");
	    
	    if (prefs.isEmpty())
	    {
		html.append("No Preferences.\n");
		continue;
	    }
	    
	    html.append("<form method=POST action=/admin/set>\n");
	    html.append("<input type=hidden name=config value=\"" + config + "\">\n");
	    html.append("<input type=hidden name=filter value=\"" + filter + "\">\n");
	    html.append("<table>\n");
	    Enumeration e = prefs.sortedKeys();
	    while (e.hasMoreElements())
	    {
		String key = (String) e.nextElement();
		String value = (String) prefs.get(key);
		html.append("<tr><td>" + key + "</td>");
		html.append("<td><input name=\"" + key + "\" size=" + value.length() + " type=text value='" + value + "'></input></td></tr>\n");
	    }
	    html.append("</table>\n");
	    html.append("<br><input type=submit value=Submit><input type=reset value=Reset>\n");
	    html.append("</form>");
	}
	html.append(tail());
	return html.toString();
    }

    String vm()
    {
	Runtime rt = Runtime.getRuntime();
	
	StringBuffer html = new StringBuffer();
	html.append(head("Muffin: Virtual Machine"));

	html.append("<table>\n");
	html.append("<tr><td>free memory</td><td>" + rt.freeMemory() + "</td></tr>\n");
	html.append("<tr><td>total memory</td><td>" + rt.totalMemory() + "</td></tr>\n");
	html.append("<tr><td>java.version</td><td>" + System.getProperty("java.version") + "</td></tr>\n");
	html.append("<tr><td>java.class.version</td><td>" + System.getProperty("java.class.version") + "</td></tr>\n");
	html.append("<tr><td>java.class.path</td><td>" + System.getProperty("java.class.path") + "</td></tr>\n");
	html.append("<tr><td>java.home</td><td>" + System.getProperty("java.home") + "</td></tr>\n");
	html.append("<tr><td>java.vendor</td><td>" + System.getProperty("java.vendor") + "</td></tr>\n");
	html.append("<tr><td>os.version</td><td>" + System.getProperty("os.version") + "</td></tr>\n");
	html.append("<tr><td>os.arch</td><td>" + System.getProperty("os.arch") + "</td></tr>\n");
	html.append("<tr><td>os.name</td><td>" + System.getProperty("os.name") + "</td></tr>\n");
	html.append("<tr><td>user.name</td><td>" + System.getProperty("user.name") + "</td></tr>\n");
	html.append("<tr><td>user.dir</td><td>" + System.getProperty("user.dir") + "</td></tr>\n");
	html.append("<tr><td>user.home</td><td>" + System.getProperty("user.home") + "</td></tr>\n");
	html.append("</table>\n");

	html.append(tail());
	return html.toString();
    }

    Hashtable cgi(Request request)
    {
	Hashtable attrs = new Hashtable(13);

	String query = request.getQueryString();
	String data = request.getData();

	if (query != null)
	{
	    StringTokenizer st = new StringTokenizer(decode(query), "&");
	    while (st.hasMoreTokens())
	    {
		String token = st.nextToken();
		String key = token.substring(0, token.indexOf('='));
		String value = token.substring(token.indexOf('=') + 1);
		attrs.put(key, value);
	    }
	}
	if (data != null)
	{
	    StringTokenizer st = new StringTokenizer(decode(data), "&");
	    while (st.hasMoreTokens())
	    {
		String token = st.nextToken();
		String key = token.substring(0, token.indexOf('='));
		String value = token.substring(token.indexOf('=') + 1);
		attrs.put(key, value);
	    }
	}
	
	return attrs;
    }

    boolean authenticated(Request request)
    {
	String auth = request.getHeaderField("Authorization");
	if (auth == null || auth.length() == 0)
	{
	    return false;
	}
	StringTokenizer st = new StringTokenizer(auth, " ");
	String type = st.nextToken();
	if (!type.equalsIgnoreCase("basic"))
	{
	    return false;
	}
	String decode = Base64.base64Decode(st.nextToken());
	st = new StringTokenizer(decode, ":");
	String user = st.nextToken();
	String pass = st.nextToken();
	if (user != null && user.equals(options.getString("muffin.adminUser"))
	    && pass != null && pass.equals(options.getString("muffin.adminPassword")))
	{
	    return true;
	}
	return false;
    }
    
    public Reply recvReply(Request request) throws IOException, RetryRequestException
    {
	Reply reply = new Reply(getInputStream());

	reply.setHeaderField("Server", "Muffin/" + Main.getMuffinVersion());
	reply.setHeaderField("Date", getDateString());
	
	if (request.getPath().startsWith("/images/"))
	{
	    String path = request.getPath(); //.substring(1);
	    InputStream content = getClass().getResourceAsStream(path);
	    reply.statusLine = "HTTP/1.0 200 Ok";
	    reply.setHeaderField("Content-type", "image/jpeg");
	    reply.setContent(content);
	}
	else if (request.getPath().startsWith("/doc/"))
	{
	    String path = request.getPath(); //.substring(1);
	    InputStream content = getClass().getResourceAsStream(path);
	    reply.statusLine = "HTTP/1.0 200 Ok";
	    if (path.endsWith(".html"))
	    {
		reply.setHeaderField("Content-type", "text/html");
	    }
	    else
	    {
		reply.setHeaderField("Content-type", "text/plain");
	    }
	    reply.setContent(content);
	}
	else if (!options.adminInetAccess(getInetAddress()))
	{
	    HttpError error = new HttpError(options, 403, "Administrative access denied");
	    reply = error.getReply();
	    reply.setContent((InputStream) new ByteArrayInputStream(error.getContent().getBytes()));
	}
	else if (options.getString("muffin.adminUser").length() > 0
		 && options.getString("muffin.adminPassword").length() > 0
		 && !authenticated(request))
	{
	    HttpError error = new HttpError(options, 401, "Administrative access unauthorized");
	    reply = error.getReply();
	    reply.setHeaderField("WWW-Authenticate", "Basic realm=\"MuffinAdmin\"");
	    reply.setContent((InputStream) new ByteArrayInputStream(error.getContent().getBytes()));
	}
	else if (request.getPath().equals("/"))
	{
	    byte buf[] = admin().getBytes();
	    reply.statusLine = "HTTP/1.0 200 Ok";
	    reply.setHeaderField("Content-type", "text/html");
	    reply.setHeaderField("Content-length", Integer.toString(buf.length));
	    reply.setContent((InputStream) new ByteArrayInputStream(buf));
	}
	else if (request.getPath().equals("/admin/autoConfig"))
	{
	    Hashtable attrs = cgi(request);
	    String text = (String) attrs.get("text");
	    System.out.println("text=" + text);
	    if (text != null)
	    {
		manager.configs.load(new StringReader(text));
	    }

	    reply = Reply.createRedirect("/admin/configs");
 	    byte buf[] = new String("Document Moved").getBytes();
 	    reply.setContent((InputStream) new ByteArrayInputStream(buf));
	}
	else if (request.getPath().equals("/admin/configs"))
	{
	    byte buf[] = configs().getBytes();
	    reply.statusLine = "HTTP/1.0 200 Ok";
	    reply.setHeaderField("Content-type", "text/html");
	    reply.setHeaderField("Content-length", Integer.toString(buf.length));
	    reply.setContent((InputStream) new ByteArrayInputStream(buf));
	}
	else if (request.getPath().equals("/admin/connections"))
	{
	    byte buf[] = connections().getBytes();
	    reply.statusLine = "HTTP/1.0 200 Ok";
	    reply.setHeaderField("Content-type", "text/html");
	    reply.setHeaderField("Content-length", Integer.toString(buf.length));
	    reply.setContent((InputStream) new ByteArrayInputStream(buf));
	}
	else if (request.getPath().equals("/admin/createConfig"))
	{
	    Hashtable attrs = cgi(request);
	    String config = (String) attrs.get("config");
	    if (config != null)
	    {
		config.trim();
		manager.configs.createConfig(config);
	    }

	    reply = Reply.createRedirect("/admin/configs");
 	    byte buf[] = new String("Document Moved").getBytes();
 	    reply.setContent((InputStream) new ByteArrayInputStream(buf));
	}
	else if (request.getPath().startsWith("/admin/filters"))
	{
	    Hashtable attrs = cgi(request);
	    String config = (String) attrs.get("config");
	    if (config != null)
	    {
		config.trim();
		manager.configs.createConfig(config);
	    }

	    byte buf[] = filters(config).getBytes();
	    reply.statusLine = "HTTP/1.0 200 Ok";
	    reply.setHeaderField("Content-type", "text/html");
	    reply.setHeaderField("Content-length", Integer.toString(buf.length));
	    reply.setContent((InputStream) new ByteArrayInputStream(buf));
	}
	else if (request.getPath().equals("/admin/vm"))
	{
	    byte buf[] = vm().getBytes();
	    reply.statusLine = "HTTP/1.0 200 Ok";
	    reply.setHeaderField("Content-type", "text/html");
	    reply.setHeaderField("Content-length", Integer.toString(buf.length));
	    reply.setContent((InputStream) new ByteArrayInputStream(buf));
	}
	else if (request.getPath().equals("/admin/enable"))
	{
	    Hashtable attrs = cgi(request);
	    String config = (String) attrs.get("config");
	    String filter = (String) attrs.get("filter");
	    if (config != null && filter != null)
	    {
		manager.enable(config, filter);
	    }
	    
	    reply = Reply.createRedirect("/admin/filters?config=" + config);
 	    byte buf[] = new String("Document Moved").getBytes();
 	    reply.setContent((InputStream) new ByteArrayInputStream(buf));
	}
	else if (request.getPath().equals("/admin/disable"))
	{
	    Hashtable attrs = cgi(request);
	    String config = (String) attrs.get("config");
	    String index = (String) attrs.get("index");
	    int i = -1;

	    try
	    {
		i = Integer.parseInt(index);
	    }
	    catch (NumberFormatException e)
	    {
	    }

	    if (config != null && i != -1)
	    {
		manager.disable(config, i);
	    }
	    
	    reply = Reply.createRedirect("/admin/filters?config=" + config);
 	    byte buf[] = new String("Document Moved").getBytes();
 	    reply.setContent((InputStream) new ByteArrayInputStream(buf));
	}
	else if (request.getPath().equals("/admin/set"))
	{
	    Hashtable attrs = cgi(request);
	    String config = (String) attrs.get("config");
	    String filter = (String) attrs.get("filter");

	    attrs.remove("config");
	    attrs.remove("filter");
	    
	    if (config != null && filter != null && attrs.size() > 0)
	    {
		Vector enabled = manager.getEnabledFilters(config);
		FilterFactory factory = null;
		for (int i = 0; i < enabled.size(); i++)
		{
		    FilterFactory ff = (FilterFactory) enabled.elementAt(i);
		    String name = manager.shortName((ff.getClass()).getName());
		    if (name.equals(filter))
		    {
			factory = ff;
			break;
		    }
		}
		if (factory != null)
		{
		    Prefs prefs = factory.getPrefs();
		    Enumeration e = attrs.keys();
		    while (e.hasMoreElements())
		    {
			String key = (String) e.nextElement();
			prefs.put(key, (String) attrs.get(key));
		    }
		}
	    }
	    
	    reply = Reply.createRedirect("/admin/filters?config=" + config);
 	    byte buf[] = new String("Document Moved").getBytes();
 	    reply.setContent((InputStream) new ByteArrayInputStream(buf));
	}
	else
	{
	    HttpError error = new HttpError(options, 404, request.getPath() + " not found");
	    reply = error.getReply();
	    reply.setContent((InputStream) new ByteArrayInputStream(error.getContent().getBytes()));
	}

	return reply;
    }

    static void init(Options o, FilterManager m, Monitor mon, Server s)
    {
	options = o;
	manager = m;
	monitor = mon;
	server = s;
    }

    static boolean sendme(Request request)
    {
	return request.getHost().equalsIgnoreCase(Main.getMuffinHost())
	    && request.getPort() == options.getInteger("muffin.port");
    }
    
    static String getLocation()
    {
	StringBuffer buf = new StringBuffer();
	buf.append("http://");
	buf.append(Main.getMuffinHost());
	buf.append(":");
	buf.append(options.getString("muffin.port"));
	return buf.toString();
    }

    static String getGenerated()
    {
	StringBuffer buf = new StringBuffer();
	buf.append("<a href=\"" + Main.getMuffinUrl() + "\">");
	buf.append("<i><img border=0 alt=\"\" src=\"" + Httpd.getLocation() + "/images/mufficon.jpg\">");
	buf.append("Generated by Muffin " + Main.getMuffinVersion() + "</a></i>");
	buf.append("</a>\n");
	return buf.toString();
    }
}
