/*
 * Copyright (C) 2001 Doug Porter
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
import java.awt.Frame;
import java.util.*;
import java.io.*;

public class Junkbuster implements FilterFactory
{
    static final String BlockfileLocation = "Junkbuster.blockfileLocation";
    
    private FilterManager manager;
    private Prefs prefs;
    private Frame frame = null;
    MessageArea messages = new MessageArea();
    private long blocklistLastModified = 0;
    private Vector blocklist = new Vector ();

    public void setManager(FilterManager manager)
    {
	this.manager = manager;
    }
    
    public void setPrefs(Prefs prefs)
    {
	this.prefs = prefs;
    }

    public Prefs getPrefs()
    {
	return prefs;
    }

    public void viewPrefs()
    {
	if (frame == null)
	{
	    frame = new JunkbusterFrame (prefs, this);
	}
	frame.setVisible(true);
    }
    
    public Filter createFilter()
    {
	Filter f = new JunkbusterFilter(this);
	f.setPrefs (prefs);
	return f;
    }

    public void shutdown()
    {
	if (frame != null)
	{
	    frame.dispose ();
	}
    }

    public void save()
    {
	manager.save (this);
    }
    
    String getBypassUrlPrefix () {
        return "http://" + getClass().getName();
    }
    
    Vector getBlocklist () {
        loadBlocklist ();
        return blocklist;
    }
    
    private synchronized void loadBlocklist () {
	try
	{
            final String CommentPrefix = "#";
            final String BuggyPatternPrefix = "/*.*/";
            final String FixedPatternPrefix = "/";
            
            String blockfileLocation = prefs.getString(Junkbuster.BlockfileLocation);
            if (blockfileLocation != null) {

                // report ("Blocklist is " + blockfileLocation);
                // report ("Blocklist last modified " + blocklistLastModified);
                
                File file = new File (blockfileLocation);
                if (file.exists ()) {
                    
                    if (file.lastModified () != blocklistLastModified) {

                        report ("Reading blocklist from " + blockfileLocation);
                        blocklistLastModified = file.lastModified ();

                        blocklist.clear ();

                        UserFile userFile = prefs.getUserFile(blockfileLocation);
                        BufferedReader in = new BufferedReader(new InputStreamReader(userFile.getInputStream()));
                        String s;
                        while ((s = in.readLine()) != null)
                        {
                            
                            // handle buggy "/*.*" prefix
                            if (s.startsWith (BuggyPatternPrefix)) {
                                s = FixedPatternPrefix + s.substring (BuggyPatternPrefix.length ());
                            }
                            
                            // trim comments and white space
                            int i = s.indexOf (CommentPrefix);
                            if (i >= 0) {
                                s = s.substring (0, i);
                            }
                            s = s.trim ();

                            if (s.length () > 0) {

                                // this was blocklist.add, but kaffe didn't support it
                                blocklist.addElement (s);

                            }

                        }
                        in.close();
                        report ("Blocklist size " + blocklist.size ());

                    }
                }
                else {
                    report ("Blocklist not found at " + blockfileLocation);
                }
            }
            else {
                report ("No blocklist specified");
            }
	}
	catch (FileNotFoundException e)
	{
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    void report (Request request, String message)
    {
	request.addLogEntry (getClass().getName(), message);
	report (message);
    }

    void report (String message)
    {
	messages.append (message + "\n");
    }
}
