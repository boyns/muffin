/* $Id: NoThanks.java,v 1.13 2000/03/29 15:14:00 boyns Exp $ */

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
package org.doit.muffin.filter;

import org.doit.muffin.*;
import org.doit.html.*;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import java.io.*;
import gnu.regexp.*;

public class NoThanks implements FilterFactory
{
    FilterManager manager;
    Prefs prefs;
    NoThanksFrame frame = null;
    MessageArea messages = null;

    private RE kill = null;
    private RE comment = null;
    private RE content = null;
    private Hashtable strip = null;
    private Vector redirectPatterns = null;
    private Vector redirectLocations = null;
    private Hashtable replace = null;
    private Hashtable tagattrTags = null;
    private Hashtable tagattrStrip = null;
    private Hashtable tagattrRemove = null;
    private Hashtable tagattrReplace = null;
    private Hashtable tagattrReplaceValue = null;
    private StringBuffer killBuffer = null;
    private StringBuffer commentBuffer = null;
    private StringBuffer contentBuffer = null;
    private Vector scriptPatterns = null;
    private Vector scriptReplace = null;
    private Vector scriptStrip = null;
    private Hashtable headerStrip = new Hashtable();
    private Hashtable headerReplace = new Hashtable();
    private Hashtable headerReplaceValue = null;

    private RE hyperTags = null;
    private RE hyperAttrs = null;
    private RE hyperEnd = null;
    private RE requiredTags = null;

    // RE compilation cflags
    private int cflags = 0;

    public NoThanks()
    {
	try
	{
	    /* tags and attributes based on HTML 4.0 spec */
	    hyperTags = new RE("^(a|img|body|form|iframe|frame|layer|object|applet|area|link|base|head|script|input)$");
	    hyperAttrs = new RE("^(action|archive|background|base|cite|classdid|codebase|data|href|longdesc|profile|src)$");
	    hyperEnd = new RE("^(a|body|form|iframe|layer|object|applet|head|script)$");
	    requiredTags = new RE("^(body|head)$");
	}
	catch (REException e)
	{
	    e.printStackTrace();
	}
    }

    public void setManager(FilterManager manager)
    {
	this.manager = manager;
    }
    
    public void setPrefs(Prefs prefs)
    {
	this.prefs = prefs;
	boolean o = prefs.getOverride();
	prefs.setOverride(false);
	String filename = "killfile";
	prefs.putString("NoThanks.killfile", filename);
	prefs.setOverride(o);
	messages = new MessageArea();
	load();
    }

    public Prefs getPrefs()
    {
	return prefs;
    }

    public void viewPrefs()
    {
	if (frame == null)
	{
	    frame = new NoThanksFrame(prefs, this);
	}
	frame.setVisible(true);
    }
    
    public Filter createFilter()
    {
	Filter f = new NoThanksFilter(this);
	f.setPrefs(prefs);
	return f;
    }

    public void shutdown()
    {
	if (frame != null)
	{
	    frame.dispose();
	}
    }

    boolean isKilled(String pattern)
    {
	if (kill == null)
	{
	    return false;
	}

	return kill.getMatch(pattern) != null;
    }

    boolean killComment(String pattern)
    {
	if (comment == null)
	{
	    return false;
	}

	return comment.getMatch(pattern) != null;
    }

    boolean killContent(String pattern)
    {
	if (content == null)
	{
	    return false;
	}

	return content.getMatch(pattern) != null;
    }

    boolean stripTag(String pattern)
    {
	if (strip == null)
	{
	    return false;
	}
	
	return strip.containsKey(pattern);
    }

    String stripUntil(String pattern)
    {
	if (strip == null)
	{
	    return null;
	}

	String s = (String) strip.get(pattern);
	return(s.length() == 0) ? null : s;
    }

    boolean replaceTag(String pattern)
    {
	if (replace == null)
	{
	    return false;
	}
	
	return replace.containsKey(pattern);
    }

    Tag replaceTagWith(String pattern)
    {
	if (replace == null)
	{
	    return null;
	}

	return(Tag) replace.get(pattern);
    }
    
    String redirect(String pattern)
    {
	if (redirectPatterns == null)
	{
	    return null;
	}
	
	for (int i = 0; i < redirectPatterns.size(); i++)
	{
	    RE re = (RE) redirectPatterns.elementAt(i);
	    if (re.getMatch(pattern) != null)
	    {
		return(String) redirectLocations.elementAt(i);
	    }
	}
	return null;
    }

    boolean checkTag(String pattern)
    {
	return hyperTags.getMatch(pattern) != null;
    }

    boolean checkAttr(String pattern)
    {
	return hyperAttrs.getMatch(pattern) != null;
    }

    boolean hasEnd(String pattern)
    {
	return hyperEnd.getMatch(pattern) != null;
    }

    boolean isRequired(String pattern)
    {
	return requiredTags.getMatch(pattern) != null;
    }
    
    boolean compare(String pattern, RE re)
    {
	if (pattern == null)
	{
	    pattern = "";
	}

	return re.getMatch(pattern) != null;
    }

    boolean checkTagAttributes(Tag tag)
    {
	if (tagattrTags == null)
	{
	    return false;
	}
	
	return tagattrTags.containsKey(tag.name());
    }
    
    boolean processTagAttributes(Request request, Tag tag)
    {
	Enumeration attrs = tag.enumerate();
	if (attrs == null)
	{
	    return false;
	}
	
	while (attrs.hasMoreElements())
	{
	    String name = (String) attrs.nextElement();
	    String key = tag.name() + "." + name;
	    if (tagattrStrip.containsKey(key))
	    {
		if (compare(tag.get(name), (RE) tagattrStrip.get(key)))
		{
		    if (isRequired(tag.name()))
		    {
			report(request, "tagattr removed* " + name + " from " + tag.name());
			tag.remove(name);
		    }
		    else
		    {
			report(request, "tagattr stripped " + tag.toString());
			return true;
		    }
		}
	    }
	    if (tagattrRemove.containsKey(key))
	    {
		if (compare(tag.get(name), (RE) tagattrRemove.get(key)))
		{
		    report(request, "tagattr removed " + name + " from " + tag.name());
		    tag.remove(name);
		}
	    }
	    if (tagattrReplace.containsKey(key) && tag.get(name) != null)
	    {
		String pattern = tag.get(name);
		Vector v = (Vector) tagattrReplace.get(key);
		Vector vv = (Vector) tagattrReplaceValue.get(key);
		for (int i = 0; i < v.size(); i++)
		{
		    RE re = (RE) v.elementAt(i);
		    REMatch match = re.getMatch(pattern);
		    if (match != null)
		    {
			String replace = (String) vv.elementAt(i);
			replace = match.substituteInto(replace);
			report(request, "tagattr replaced \""
			       + pattern + "\" with \"" + replace + "\"");
			tag.put(name, replace);
		    }
		}
	    }
	}
	return false;
    }

    Token processScript(Request request, Token token)
    {
	RE re;
	String replace;
	String script;
	int size;

	script = token.toString();

	//
	// See if any of the scriptStrip patterns match.
	//
	if (scriptStrip.size() > 0)
	{
	    for (Enumeration e = scriptStrip.elements(); e.hasMoreElements(); )
	    {
		re = (RE) e.nextElement();
		if (re.getMatch(script) != null)
		{
		    // strip this script
		    report(request, "script stripped " + re.toString());
		    return null;
		}
	    }
	}

	//
	// Perform regexp-based script substitution.
	//
	if ((size = scriptPatterns.size()) > 0)
	{
	    for (int i = 0; i < size; i++)
	    {
		re = (RE) scriptPatterns.elementAt(i);
		replace = (String)scriptReplace.elementAt(i);
		script = re.substituteAll(script, replace);
	    }

	    token = new Token(token.getType());
	    token.append(script);
	}

	return token;
    }

    void processHeaders(Request request, Message m)
    {
	// try to optimize for the case where there are no
	// header rules
	if (headerStrip.size() == 0 && headerReplace.size() == 0)
	{
	    return;
	}
	
	String name, value;
	RE re;
	REMatch match;

	for (Enumeration e = m.getHeaders(); e.hasMoreElements(); )
	{
	    name = ((String) e.nextElement()).toLowerCase();

	    if (headerStrip.containsKey(name))
	    {
		re = (RE) headerStrip.get(name);
		for (int i = 0, n = m.getHeaderValueCount(name); i < n; i++)
		{
		    value = m.getHeaderField(name, i);
		    if (compare(value, re))
		    {
			report(request, "header "+name+"="+value+" stripped");
			m.removeHeaderField(name);
			break;
		    }
		}
	    }
	    if (headerReplace.containsKey(name))
	    {
		Vector v = (Vector) headerReplace.get(name);
		Vector vv = (Vector) headerReplaceValue.get(name);
		for (int index = 0; index < v.size(); index++)
		{
		    re = (RE) v.elementAt(index);

		    for (int i = 0, n = m.getHeaderValueCount(name); i < n; i++)
		    {
			value = m.getHeaderField(name, i);
			match = re.getMatch(value);
			if (match != null)
			{
			    String replace = (String) vv.elementAt(index);
			    replace = match.substituteInto(replace);
			    report(request, "header " + name + " replaced \""
				   + value + "\" with \"" + replace + "\"");
			    m.setHeaderField(name, replace, i);
			}
		    }
		}
	    }
	}
    }

    void save()
    {
	manager.save(this);
    }

    RE createRE(Vector v) throws Exception
    {
	StringBuffer buf = new StringBuffer();
	buf.append("(");
	for (int i = 0; i < v.size(); i++)
	{
	    buf.append(v.elementAt(i));
	    if (i != v.size() - 1)
	    {
		buf.append("|");
	    }
	}
	buf.append(")");
	return new RE(buf.toString(), cflags);
    }

    void load()
    {
	InputStream in = null;

	try
	{
	    UserFile file = prefs.getUserFile(prefs.getString("NoThanks.killfile"));
	    in = file.getInputStream();
	    load(new InputStreamReader(in));
	}
	catch (FileNotFoundException e)
	{
	}
	catch (IOException e)
	{
	    System.out.println(e);
	}
	finally
	{
	    if (in != null)
	    {
		try
		{
		    in.close();
		}
		catch (IOException e)
		{
		}
	    }
	}
    }

    void load(Reader reader)
    {
	cflags = 0;
	
	strip = new Hashtable(33);
	redirectPatterns = new Vector();
	redirectLocations = new Vector();
	replace = new Hashtable(33);
	tagattrTags = new Hashtable(33);
	tagattrStrip = new Hashtable(33);
	tagattrRemove = new Hashtable(33);
	tagattrReplace = new Hashtable(33);
	tagattrReplaceValue = new Hashtable(33);
	killBuffer = new StringBuffer();
	commentBuffer = new StringBuffer();
	contentBuffer = new StringBuffer();
	scriptPatterns = new Vector();
	scriptReplace = new Vector();
	scriptStrip = new Vector();
	headerStrip = new Hashtable();
	headerReplace = new Hashtable();
	headerReplaceValue = new Hashtable();

	include(reader);
	
	try
	{
	    kill = (killBuffer.length() > 0) ?
		new RE("(" + killBuffer.toString() + ")", cflags) : null;
	    comment = (commentBuffer.length() > 0) ?
		new RE("(" + commentBuffer.toString() + ")", cflags) : null;
	    content = (contentBuffer.length() > 0) ?
		new RE("(" + contentBuffer.toString() + ")", cflags) : null;

	    /* Build regular expressions for tagattr */
	    Enumeration e = tagattrStrip.keys();
	    while (e.hasMoreElements())
	    {
		String key = (String) e.nextElement();
		tagattrStrip.put(key, createRE((Vector) tagattrStrip.get(key)));
	    }
	    e = tagattrRemove.keys();
	    while (e.hasMoreElements())
	    {
		String key = (String) e.nextElement();
		tagattrRemove.put(key, createRE((Vector) tagattrRemove.get(key)));
	    }

	    /* Build regular expressions for header */
	    e = headerStrip.keys();
	    while (e.hasMoreElements())
	    {
		String key = (String) e.nextElement();
		headerStrip.put(key, createRE((Vector) headerStrip.get(key)));
	    }
	}
	catch (REException e)
	{
	    System.out.println("NoThanks REException: " + e.getMessage());
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    void include(Reader reader)
    {
	try
	{
	    String s;
	    int token;
	    BufferedReader in = new BufferedReader(reader);
	    while ((s = in.readLine()) != null)
	    {
		StreamTokenizer st = new StreamTokenizer(new StringReader(s));
		st.resetSyntax();
		st.whitespaceChars(0, 32);
		st.wordChars(33, 126);
		st.quoteChar('"');
		st.eolIsSignificant(true);

		token = st.nextToken();
		if (token != StreamTokenizer.TT_WORD)
		{
		    continue;
		}

		if (st.sval.startsWith("#"))
		{
		    if (st.sval.equals("#include"))
		    {
			token = st.nextToken();
			if (token != StreamTokenizer.TT_WORD && token != '"')
			{
			    break;
			}

			InputStream inc = null;
			try
			{
			    UserFile file = prefs.getUserFile(st.sval);
			    inc = file.getInputStream();
			    include(new InputStreamReader(inc));
			}
			catch (IOException e)
			{
			    System.out.println(e);
			}
			finally
			{
			    if (inc != null)
			    {
				try
				{
				    inc.close();
				}
				catch (IOException e)
				{
				}
			    }
			}
		    }
		    continue;
		}
		
		if (st.sval.equals("kill"))
		{
		    token = st.nextToken();
		    if (token != StreamTokenizer.TT_WORD && token != '"')
		    {
			break;
		    }
		    if (killBuffer.length() > 0)
		    {
			killBuffer.append("|");
		    }
		    killBuffer.append(st.sval);
		}
		else if (st.sval.equals("comment"))
		{
		    token = st.nextToken();
		    if (token != StreamTokenizer.TT_WORD && token != '"')
		    {
			break;
		    }
		    if (commentBuffer.length() > 0)
		    {
			commentBuffer.append("|");
		    }
		    commentBuffer.append(st.sval);
		}
		else if (st.sval.equals("strip"))
		{
		    token = st.nextToken();
		    if (token != StreamTokenizer.TT_WORD && token != '"')
		    {
			break;
		    }
		    String start = new String(st.sval);
		    String end = "";
		    token = st.nextToken();
		    if (token == StreamTokenizer.TT_WORD || token == '"')
		    {
			end = new String(st.sval);
		    }
		    strip.put(start.toLowerCase(), end.toLowerCase());
		}
		else if (st.sval.equals("content"))
		{
		    token = st.nextToken();
		    if (token != StreamTokenizer.TT_WORD && token != '"')
		    {
			break;
		    }
		    if (contentBuffer.length() > 0)
		    {
			contentBuffer.append("|");
		    }
		    contentBuffer.append(st.sval); 		
		}
		else if (st.sval.equals("redirect"))
		{
		    token = st.nextToken();
		    if (token != StreamTokenizer.TT_WORD && token != '"')
		    {
			break;
		    }
		    String pattern = new String(st.sval);
		    String location = "";
		    token = st.nextToken();
		    if (token == StreamTokenizer.TT_WORD || token == '"')
		    {
			location = new String(st.sval);
		    }
		    try
		    {
			RE re = new RE(pattern, cflags);
			redirectPatterns.addElement(re);
			redirectLocations.addElement(location);
		    }
		    catch (REException e)
		    {
			System.out.println(pattern + " " + e.getMessage());
		    }
		}
		else if (st.sval.equals("replace"))
		{
		    token = st.nextToken();
		    if (token != StreamTokenizer.TT_WORD && token != '"')
		    {
			break;
		    }
		    String oldtag = new String(st.sval);
		    token = st.nextToken();
		    if (token != StreamTokenizer.TT_WORD && token != '"')
		    {
			break;
		    }
		    String newtag = new String(st.sval);
		    String name = null;
		    String data = null;
		    int i = newtag.indexOf(" \t");
		    if (i == -1)
		    {
			name = newtag;
		    }
		    else
		    {
			name = newtag.substring(i);
			data = newtag.substring(i+1);
		    }
		    Tag tag = new Tag(name, data);
		    replace.put(oldtag.toLowerCase(), tag);
		}
		else if (st.sval.equals("tagattr"))
		{
		    token = st.nextToken();
		    if (token != StreamTokenizer.TT_WORD && token != '"')
		    {
			break;
		    }
		    int i = st.sval.indexOf('.');
		    if (i == -1)
		    {
			break;
		    }
		    String tag = st.sval.substring(0, i);
		    tag = tag.toLowerCase();
		    String attr = st.sval.substring(i+1);
		    attr = attr.toLowerCase();
		    String key = tag + "." + attr;
		    
		    token = st.nextToken();
		    if (token != StreamTokenizer.TT_WORD && token != '"')
		    {
			break;
		    }
		    String command = new String(st.sval);

		    Vector list = (Vector) tagattrTags.get(tag);
		    if (list == null)
		    {
			list = new Vector();
			tagattrTags.put(tag, list);
		    }
		    list.addElement(attr);

		    String pattern;
		    token = st.nextToken();
		    if (token != StreamTokenizer.TT_WORD && token != '"')
		    {
			pattern = ".*";
		    }
		    else
		    {
			pattern = new String(st.sval);
		    }

		    RE re = new RE(pattern, cflags);
		
		    if (command.equals("strip"))
		    {
			/* Build a vector of Strings */
			Vector v;
			if (tagattrStrip.containsKey(key))
			{
			    v = (Vector) tagattrStrip.get(key);
			}
			else
			{
			    v = new Vector();
			    tagattrStrip.put(key, v);
			}
			v.addElement(pattern);
		    }
		    else if (command.equals("remove"))
		    {
			/* Build a vector of Strings */
			Vector v;
			if (tagattrRemove.containsKey(key))
			{
			    v = (Vector) tagattrRemove.get(key);
			}
			else
			{
			    v = new Vector();
			    tagattrRemove.put(key, v);
			}
			v.addElement(pattern);
		    }
		    else if (command.equals("replace"))
		    {
			token = st.nextToken();
			if (token != StreamTokenizer.TT_WORD && token != '"')
			{
			    System.out.println("tagattr replace missing value");
			    break;
			}
			String value = new String(st.sval);

			/* Build a vector of REs and replacement Strings */
			Vector v;
			Vector vv;
			if (tagattrReplace.containsKey(key))
			{
			    v = (Vector) tagattrReplace.get(key);
			    vv = (Vector) tagattrReplaceValue.get(key);
			}
			else
			{
			    v = new Vector();
			    vv = new Vector();
			    tagattrReplace.put(key, v);
			    tagattrReplaceValue.put(key, vv);
			}
			v.addElement(re);
			vv.addElement(value);
		    }
		    else
		    {
			System.out.println("tagattr " + command + " unknown command");
		    }
		}
		else if (st.sval.equals("options"))
		{
		    token = st.nextToken();
		    if (token != StreamTokenizer.TT_WORD && token != '"')
		    {
			break;
		    }

		    if ("reg-case".equals(st.sval))
		    {
			cflags = 0;
		    }
		    else if ("reg-icase".equals(st.sval))
		    {
			cflags = RE.REG_ICASE;
		    }
		}
		else if (st.sval.equals("script"))
		{
		    token = st.nextToken();
		    if (token != StreamTokenizer.TT_WORD && token != '"')
		    {
			break;
		    }
		    
		    if ("replace".equals(st.sval))
		    {
			String pattern;

			token = st.nextToken();
			if (token != StreamTokenizer.TT_WORD && token != '"')
			{
			    pattern = ".*";
			}
			else
			{
			    pattern = new String(st.sval);
			}

			RE re = new RE(pattern, cflags);
			scriptPatterns.addElement(re);

			token = st.nextToken();
			if (token != StreamTokenizer.TT_WORD && token != '"')
			{
			    System.out.println("script replace missing replacement");
			}
			else
			{
			    scriptReplace.addElement(st.sval);
			}
		    }
		    else if ("strip".equals(st.sval))
		    {
			String pattern;

			token = st.nextToken();
			if (token != StreamTokenizer.TT_WORD && token != '"')
			{
			    pattern = ".*";
			}
			else
			{
			    pattern = new String(st.sval);
			}

			RE re = new RE(pattern, cflags);
			scriptStrip.addElement(re);
		    }
		    else
		    {
			System.out.println("script " + st.sval + " unknown command");
		    }
		}
		else if (st.sval.equals("header"))
		{
		    // header
		    token = st.nextToken();
		    if (token != StreamTokenizer.TT_WORD && token != '"')
		    {
			break;
		    }
		    String key = st.sval.toLowerCase();

		    // command
		    token = st.nextToken();
		    if (token != StreamTokenizer.TT_WORD && token != '"')
		    {
			break;
		    }
		    String command = new String(st.sval);

		    // value pattern
		    String pattern;
		    token = st.nextToken();
		    if (token != StreamTokenizer.TT_WORD && token != '"')
		    {
			pattern = ".*";
		    }
		    else
		    {
			pattern = new String(st.sval);
		    }

		    RE re = new RE(pattern, cflags);

		    if (command.equals("strip"))
		    {
			/* Build a vector of Strings */
			Vector v;
			if (headerStrip.containsKey(key))
			{
			    v = (Vector) headerStrip.get(key);
			}
			else
			{
			    v = new Vector();
			    headerStrip.put(key, v);
			}
			v.addElement(pattern);
		    }
		    else if (command.equals("replace"))
		    {
			token = st.nextToken();
			if (token != StreamTokenizer.TT_WORD && token != '"')
			{
			    System.out.println("header replace missing value");
			    break;
			}
			String value = new String(st.sval);

			/* Build a vector of REs and replacement Strings */
			Vector v;
			Vector vv;
			if (headerReplace.containsKey(key))
			{
			    v = (Vector) headerReplace.get(key);
			    vv = (Vector) headerReplaceValue.get(key);
			}
			else
			{
			    v = new Vector();
			    vv = new Vector();
			    headerReplace.put(key, v);
			    headerReplaceValue.put(key, vv);
			}
			v.addElement(re);
			vv.addElement(value);
		    }
		    else
		    {
			System.out.println("header " + command + " unknown command");
		    }
		}
		else
		{
		    System.out.println("NoThanks: " + st.sval + " unknown command");
		}
	    }
 	    in.close();
	}
	catch (REException e)
	{
	    System.out.println("NoThanks REException: " + e.getMessage());
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    void report(Request request, String message)
    {
	request.addLogEntry("NoThanks", message);
	messages.append(message + "\n");
    }
}

