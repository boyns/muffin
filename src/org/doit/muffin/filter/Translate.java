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

/*
 * written by Mike Baroukh.
 */

package org.doit.muffin.filter;

import org.doit.muffin.*;
import org.doit.html.*;

import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Enumeration;
import gnu.regexp.*;

public class Translate implements FilterFactory
{
    FilterManager manager;
    Prefs prefs;
    TranslateFrame frame = null;
    MessageArea messages = null;

    Vector passRulesIn = null;
    Vector passRulesOut = null;
    Vector reverseRulesIn = null;
    Vector reverseRulesOut = null;

    public Translate()
    {
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
	String filename = "translate";
	prefs.putString("Translate.rules", filename);
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
	    frame = new TranslateFrame(prefs, this);
	}
	frame.setVisible(true);
    }
    
    public Filter createFilter()
    {
	Filter f = new TranslateFilter(this);
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

    void save()
    {
	manager.save(this);
    }

    Request translate(Request request)
    {
	RE re = null;
	REMatch match = null;
	Enumeration e = passRulesIn.elements();
	String url = request.getURL();

// 		System.out.println("Request -> "+url);

	int index = 0;
	while (match == null && e.hasMoreElements())
	{
	    re = (RE) e.nextElement();
	    match = re.getMatch(url);
	    index++;
	}
	    
	if (match != null)
	{
	    String s = (String) passRulesOut.elementAt(index - 1);
	    String sub = match.substituteInto(s);
	    report(request, "PASS RULE #" + index + ": " + url + " -> " + sub);

// 		System.out.println("PASS -> "+sub);

		request.setURL(sub);
	} 

	return request;

    }

    Reply translate(Reply reply)
    {
	RE re = null;
	REMatch match = null;
	Enumeration e = reverseRulesIn.elements();
	String url = reply.getHeaderField("Location");

// 		System.out.println("Reply -> "+url);

	int index = 0;
	while (match == null && e.hasMoreElements())
	{
	    re = (RE) e.nextElement();
	    match = re.getMatch(url);
	    index++;
	}
	    
	if (match != null)
	{
	    String s = (String) reverseRulesOut.elementAt(index - 1);
	    String sub = match.substituteInto(s);
	    report(reply, "PASS RULE #" + index + ": " + url + " -> " + sub);

// 		System.out.println("Reverse -> "+sub);

		reply.setHeaderField("Location",sub);
	} 

	return reply;
    }

    void load()
    {
	InputStream in = null;

	try
	{
	    UserFile file = prefs.getUserFile(prefs.getString("Translate.rules"));
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
	passRulesIn = new Vector();
	passRulesOut = new Vector();
	reverseRulesIn = new Vector();
	reverseRulesOut = new Vector();
		
	try
	{
	    BufferedReader in = new BufferedReader(reader);
	    String s;
	    RE blank = new RE("^[# \t\n]");

	    while ((s = in.readLine()) != null)
	    {
		if (blank.getMatch(s) != null)
		{
		    continue;
		}

		StringTokenizer st = new StringTokenizer(s, " \t");
		try
		{
			String type = st.nextToken();
			if (type.equalsIgnoreCase("PASS")) {
			    passRulesIn.addElement(new RE(st.nextToken()));
			    passRulesOut.addElement(st.nextToken());
			} else {
			    reverseRulesIn.addElement(new RE(st.nextToken()));
			    reverseRulesOut.addElement(st.nextToken());
			}
		}
		catch (REException e)
		{
		    System.out.println("REException: " + e);
		}
	    }
	    in.close();
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    void report(Request request, String message)
    {
	request.addLogEntry("Translate", message);
	messages.append(message + "\n");
    }

    void report(Reply reply, String message)
    {
//	reply.addLogEntry("Translate", message);
	messages.append(message + "\n");
    }
}
