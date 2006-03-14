/* $Id: Rewrite.java,v 1.10 2006/03/14 17:00:03 flefloch Exp $ */

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

import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Enumeration;
import org.doit.muffin.regexp.Factory;
import org.doit.muffin.regexp.Pattern;
import org.doit.muffin.regexp.Matcher;

public class Rewrite implements FilterFactory
{
    private FilterManager manager;
    private Prefs prefs;
    private RewriteFrame frame = null;
    MessageArea messages = null;

    private Vector rules = null;
    private Vector rewrite = null;

    public Rewrite()
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
	String filename = "rewrite";
	prefs.putString("Rewrite.rules", filename);
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
	    frame = new RewriteFrame(prefs, this);
	}
	frame.setVisible(true);
    }
    
    public Filter createFilter()
    {
	Filter f = new RewriteFilter(this);
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

    String rewrite(Request request, String url)
    {
	Pattern re = null;
	Matcher match = null;
	Enumeration e = rules.elements();

	int index = 0;
	while (match == null && e.hasMoreElements())
	{
	    re = (Pattern) e.nextElement();
	    match = re.getMatch(url);
	    index++;
	}
	    
	if (match != null)
	{
	    String s = (String) rewrite.elementAt(index - 1);
	    String sub = match.substituteInto(s);
	    report(request, "RULE #" + index + ": " + url + " -> " + sub);
	    url = sub;
	}
	
	return url;
    }

    void load()
    {
	InputStream in = null;

	try
	{
	    UserFile file = prefs.getUserFile(prefs.getString("Rewrite.rules"));
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

	void load(Reader reader) {
		rules = new Vector();
		rewrite = new Vector();

		try {
			BufferedReader in = new BufferedReader(reader);
			String s;
			Pattern blank = Factory.instance().getPattern("^[# \t\n]");

			while ((s = in.readLine()) != null) {
				if (s.length() > 0 && blank.matches(s)) {
					StringTokenizer st = new StringTokenizer(s, " \t");
					String re = st.nextToken();
					String rew = st.nextToken();
					if (re.length() > 0 && rew.length() > 0) {

						rules.addElement(Factory.instance().getPattern(re));
						rewrite.addElement(rew);

					} else {

						report("invalid rule: " + s);

					}
				}
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    void report(Request request, String message)
    {
	request.addLogEntry("Rewrite", message);
	report (message);
    }

    void report(String message)
    {
	messages.append(message + "\n");
    }
}
