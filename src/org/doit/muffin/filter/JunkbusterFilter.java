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
import java.util.*;
import java.io.*;
import org.doit.muffin.regexp.Pattern;
import org.doit.muffin.regexp.Factory;

/* Implements url filtering using a Junkbuster blocklist.
 */
public class JunkbusterFilter implements RequestFilter, HttpFilter
{
    final char PortSeparator = ':';
    final char DomainSeparator = '/';
    private final Integer HashPlaceholder = new Integer (1);
    private Junkbuster factory;
    private Prefs prefs;
    private Hashtable blockedBy = new Hashtable ();
    private Hashtable bypassedBlocks = new Hashtable ();
    private boolean verboseMatch = false;

    public JunkbusterFilter (Junkbuster factory)
    {
	this.factory = factory;
    }
    
    public void setPrefs (Prefs prefs)
    {
	this.prefs = prefs;
    }

    public void filter (Request request) throws FilterException {
	String url = request.getURL ();

        // factory.report ("filtering url " + request.getURL ());
        
        if (url.startsWith (factory.getBypassUrlPrefix ())) {
            
            String path = request.getPath ();
            if (path.length () > 1) {
                url = path.substring (1);
                bypassedBlocks.put (url, HashPlaceholder);
                // factory.report ("Changing url from " + request.getURL () + " to " + url);
                request.setURL (url);
            }
            else {
                // factory.report ("Unable to parse request url " + request.getURL ());
            }
            
        }
    }
    
    public void sendRequest (Request request) {
    }
    
    public boolean wantRequest (Request request) {
	return isBlocked (request.getURL());
    }
    
    public Reply recvReply (Request request) {

        String url = request.getURL ();
	StringBuffer text = new StringBuffer ();
        /*
	text.append ("<h2>Url blocked by Muffin Junkbuster filter</h2>\n");
	text.append ("<hr>\n");
	text.append ("Continue to \n");
        String anchorTag = "<a href=\"" + 
                           factory.getBypassUrlPrefix () +
                           "/" +
                           url + 
                           "\">"; 
        String anchor = anchorTag + 
                        url +
                        "</a>";
        text.append (anchor +
                     "\n");
        text.append ("<br>Blocked by pattern " + (String) blockedBy.get (url));
	text.append ("<hr>\n");
         */
        
	Reply reply = new Reply();

	reply.setStatusLine ("HTTP/1.0 202 Accepted but not completed");
	reply.setHeaderField ("Content-type", "text/html");
        // reply.setHeaderField ("Content-type", "image/gif");
	
	byte content[] = text.toString().getBytes();
	reply.setHeaderField ("Content-length", Integer.toString (content.length));
        
	reply.setContent ( (InputStream) new ByteArrayInputStream (content));
	
	return reply;
    }
    
    public void close () {
    }
    
    private synchronized boolean isBlocked (String url) {
        final String UnblockPrefix = "~";
        boolean blocked = false;
        
        Pattern re;
        // factory.report ("Checking "+ url);
        
        if (url.startsWith (factory.getBypassUrlPrefix ())) {
            
            // factory.report ("User requested block bypass for " + url);
            blocked= false;
            
        }
        
        else if (bypassedBlocks.get (url) != null) {
            
            // factory.report ("Bypassing block for " + url);
            blocked= false;
            
        }
        
        else {
            
            // check for a match with the blocklist
            Enumeration patterns = factory.getBlocklist ().elements ();
            while (patterns.hasMoreElements ()) {
                       
                String pattern = (String) patterns.nextElement ();
                if (pattern.startsWith (UnblockPrefix)) {
                    
                    pattern = pattern.substring (1);
                    if (isMatch (url, pattern)) {
                        blocked = false;
                    }
                    
                }
                else {
                    
                    if (isMatch (url, pattern)) {
                        blocked = true;
                        // this hash will grow as long as we run if we don't limit it
                        blockedBy.put (url, pattern);
                        factory.report(url + " blocked by pattern " + pattern);
                    }
                    
                }
                
            }

        }
        
        return blocked;
    }
    
    /* Test a url for a Junkbuster blocklist pattern match.
     * See the Junkbuster docs for details.
     * @param url Url
     * @param pattern Junkbuster blocklist pattern
     */
    private synchronized boolean isMatch (String url, String pattern) {
        boolean matches= false;
        
        url = removeProtocol (url);
        
        String urlDomain = getDomain (url);
        String urlPath = getPath (url);
        String urlPort = getPort (url);
        String patternDomain = getDomain (pattern);
        String patternPort = getPort (pattern);
        String patternPath = getPath (pattern);
        
        // put test case criteria here
        verboseMatch = false; 
        // verboseMatch = urlPath !=null && urlPath.indexOf ("ads") >= 0 && patternPath != null && patternPath.indexOf ("ads") >= 0; 
        
        if (verboseMatch) {
            factory.report ("urlDomain: " + urlDomain);
            factory.report ("urlPath: " + urlPath);
            factory.report ("patternDomain: " + patternDomain);
            factory.report ("patternPath: " + patternPath);
        }
        
        matches = domainMatches (urlDomain, patternDomain) &&
                  portMatches (urlPort, patternPort) &&
                  pathMatches (urlPath, patternPath);
        if (verboseMatch) {
            factory.report ("matches: " + matches);
        }

        return matches;
    }
    
    private String removeProtocol (String url) {
        final String ProtocolDelimiter = "://";
        int i = url.indexOf (ProtocolDelimiter);
        if (i >= 0) {
            url = url.substring (i + ProtocolDelimiter.length ());
        }
        return url;
    }
    
    private String getDomain (String pattern) {
        String domain;
        int i = pattern.indexOf (PortSeparator);
        if (i == 0) {
            domain = null;
        }
        else if (i > 0) {
            domain = pattern.substring (0, i);
        }
        else {
            i = pattern.indexOf (DomainSeparator);
            if (i < 0) {
                domain = pattern;
            }
            else if (i == 0) {
                domain = null;
            }
            else {
                domain = pattern.substring (0, i);
            }
        }
        return domain;
    }
    
    private String getPort (String pattern) {
        String port;
        int i = pattern.indexOf (PortSeparator);
        if (i >= 0) {
            pattern = pattern.substring (i);
            i = pattern.indexOf (DomainSeparator);
            if (i >= 0) {
                port = pattern.substring (0, i);
            }
            else {
                port = pattern;
            }
        }
        else {
            port = null;
        }
        return port;
    }
    
    private String getPath (String pattern) {
        String path;
        int i = pattern.indexOf (DomainSeparator);
        if (i >= 0) {
            path = pattern.substring (i);
        }
        else {
            path = null;
        }
        return path;
    }
    
    private boolean domainMatches (String urlDomain, String patternDomain) {
        boolean matches;
        
        if (patternDomain == null) {
            matches= true;
        }
        else {
            String [] urlComponents = toComponentArray (urlDomain);
            String [] patternComponents = toComponentArray (patternDomain);
            if (urlComponents.length >= patternComponents.length) {
                
                int i = patternComponents.length - 1;

                matches = true;
                while (i >= 0 &&
                       matches) {
                           
                    matches = domainComponentMatches (urlComponents[i], patternComponents[i]);
                    --i;
                    
                }
                
            }
            else {
                // not enough url components to match
                matches = false;
            }
        }
        
        if (verboseMatch) {
            factory.report ("domain matches: " + matches);
        }
        return matches;
    }
    
    private boolean portMatches (String urlPort, String patternPort) {
        final String DefaultPort = "80";
        return patternPort == null ||
               (patternPort.equals (DefaultPort) && urlPort == null) ||
               patternPort.equals (urlPort);
    }

	private boolean pathMatches(String urlPath, String patternPath) {
		boolean matches = false;

		if (patternPath == null) {
			matches = true;
		} else {
			Pattern re = Factory.instance().getPattern(patternPath, true);
			//, RESyntax.RE_SYNTAX_POSIX_BASIC);
			// a partial match is enough
			matches = (re.matches(urlPath));
		}
		if (verboseMatch) {
			factory.report("path matches: " + matches);
		}
		return matches;
	}
    
    /* Break a domain up into components.
     */
    private String [] toComponentArray (String domain) {
        final String DotDelimiter = ".";
        Vector components = new Vector ();
        
        StringTokenizer tokens = new StringTokenizer (domain, DotDelimiter);
        while (tokens.hasMoreTokens ()) {
            components.addElement (tokens.nextToken ());
        }

        return (String []) components.toArray (new String [0]);
    }
    
    private boolean domainComponentMatches (String urlComponent, String patternComponent) {
        final char Wildcard = '*';
        boolean matches;
        
        int i = patternComponent.indexOf (Wildcard);
        if (i >= 0) {
            patternComponent = patternComponent.substring (0, i);
            matches = urlComponent.toLowerCase ().startsWith (patternComponent.toLowerCase ());
        }
        else {
            matches = patternComponent.equalsIgnoreCase (urlComponent);
        }
        
        return matches;
    }
    
}
