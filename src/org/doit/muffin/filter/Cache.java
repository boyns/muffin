/*
 * Copyright (C) 2002 Doug Porter
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
import java.net.*;
import java.text.DateFormat;

/** Web cache.
 */
public class Cache
implements FilterFactory
{
    static final boolean Debugging = false;

    static final String CacheDirectory = "Cache.cacheDirectory";
    static final String CheckForUpdatesAlways = "Cache.checkForUpdates.always";
    static final String CheckForUpdatesOncePerSession = "Cache.checkForUpdates.oncePerSession";
    static final String CheckForUpdatesNever = "Cache.checkForUpdates.never";
    static final String NoCachePatterns = "Cache.noCachePatterns";
    static final String DefaultCacheDir = "webcache";

    /** Url property name.
     */
    static final String UrlProperty = "url";

    /** CacheDate property name.
     */
    static final String CacheDateProperty = "cache-date";

    /** GMT time zone
     */
    static final TimeZone GMT = TimeZone.getTimeZone ("GMT");

    /** End of line
     */
    final byte [] EOL = "\r\n".getBytes ();

    private FilterManager manager;
    private Prefs prefs;
    private Frame frame = null;
    MessageArea messages = new MessageArea();
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
	    frame = new CacheFrame (prefs, this);
	}
	frame.setVisible(true);
    }
    
    public Filter createFilter()
    {
	Filter f = new CacheFilter(this);
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
    
    String getCacheDirectory () {
        String dir = prefs.getString(Cache.CacheDirectory);
        
        if (dir == null ||
            dir.length () <= 0) {
                
            String slash = System.getProperty ("file.separator");
            
            try {
                
                File tryDir = new File (slash + "tmp");
                if (! tryDir.exists ()) {
                    // Windows is case insensitive and some unix installations use "/temp", so this tries both
                    tryDir = new File (slash + "temp");
                }
                
                if (tryDir.exists ()) {
                    File cacheDir = new File (tryDir, DefaultCacheDir);
                    dir = cacheDir.getAbsolutePath ();
                }
                
            }
            catch (Exception e) {
                report ("Unable to get cache directory:" + e);
            }
        }
        
        File cacheDir = new File (dir);
        cacheDir.mkdirs ();
        
        return cacheDir.getAbsolutePath ();            
    }
    
    boolean checkForUpdatesOncePerSession () {
        // default to true
        return prefs.getString(Cache.CheckForUpdatesOncePerSession) == null ||
               prefs.getBoolean(Cache.CheckForUpdatesOncePerSession);
    }
    
    boolean checkForUpdatesAlways () {
        // default to false
        return prefs.getString(Cache.CheckForUpdatesAlways) != null &&
               prefs.getBoolean(Cache.CheckForUpdatesAlways);
    }
    
    boolean checkForUpdatesNever () {
        // default to false
        return prefs.getString(Cache.CheckForUpdatesNever) != null &&
               prefs.getBoolean(Cache.CheckForUpdatesNever);
    }
    
    MessageArea getMessages() {
        return messages;
    }
    
    /** Cache the reply.
     */    
    public synchronized void cacheReply (Reply reply) {
        
        Request request = reply.getRequest ();
        String url = getURL (request);

        File tempCacheFile = getTempCacheFile (url);
        File finalCacheFile = getCacheFile (url);

        // don't interfere with a cache in progress
        // delete any old incomplete file, but if the file's open now we can't delete it
        tempCacheFile.delete ();
        if (tempCacheFile.exists ()) {

            report ("unable to cache " + url + ": "+ finalCacheFile.getAbsolutePath () + " still exists");

        }

        else {

            // report ("caching " + url + " to "+ finalCacheFile.getAbsolutePath ());
            boolean cacheComplete = false;

            InputStream in = reply.getContent ();
            if (in != null &&
                // CacheFilter should have made this input stream a BufferedInputStream
                in instanceof BufferedInputStream) {

                try {

                    FileOutputStream cacheOut = new FileOutputStream (tempCacheFile);

                    try {
                        // write the properties
                        writeProperties (cacheOut, getProperties (url));

                        // write the reply headers
                        reply.write (cacheOut);

                        // copy the reply content
                        in.reset ();
                        copy (in, cacheOut);
                        cacheOut.close ();

                        cacheComplete = true;
                        report ("cached " + url + " to "+ finalCacheFile.getAbsolutePath ());

                    }
                    catch (IOException ioe) {
                        reportCacheError (url, ioe.toString ());
                        try {
                            cacheOut.close ();
                        }
                        catch (IOException ioe2) {
                            // just ignore it
                        }
                    }
                }
                catch (IOException ioe) {
                    reportCacheError (url, ioe.toString ());
                }

            }
            else {
                // other end may have closed socket
                report ("unable to get content: " + url);
            }

            if (cacheComplete) {
                tempCacheFile.renameTo (finalCacheFile);
            }
            else {
                tempCacheFile.delete ();
            }
        }
    }

    /** Provide a cached reply to a request.
     */
    public synchronized Reply getCachedReply (String url) {

        Reply reply;

        try {

            FileInputStream in = new FileInputStream (getCacheFile (url));

            // ignore the properties
            readProperties (in);

            reply = new Reply (in);
            reply.read ();
            
        }
        catch (Exception e) {

            // tell the user  what happened
            
            reply = new Reply();

            report ("Error: Could not get " + url + " from cache: " + e);

            StringBuffer text = new StringBuffer ();
            text.append ("<h2>Cache error</h2>\n");
            text.append ("Error getting " + url + " from cache: " + e + "\n");

            // what result code do we want here?
            reply.setStatusLine ("HTTP/1.0 500"); 
            reply.setHeaderField ("Content-type", "text/html");

            byte content[] = text.toString().getBytes();
            reply.setHeaderField ("Content-length", Integer.toString (content.length));

            reply.setContent ((InputStream) new ByteArrayInputStream (content));

        }
	
	return reply;
    }
    
    public void removeFromCache (String url) {
        File cacheFile = getCacheFile (url);
        if (cacheFile.exists ()) {
            cacheFile.delete ();
            report ("removed " + url + " from the cache");
        }
        else {
            // report ("already removed or not in cache: " + url + ": " + cacheFile.getAbsolutePath ());
        }
    }
    
    public synchronized boolean isCached (String url) {
        boolean cached = getCacheFile (url).exists ();
            
        StringBuffer report = new StringBuffer ();
        if (cached) {
            report.append ("already ");
        } 
        else {
            report.append ("not ");
        }
        report.append ("cached: " + url);
        // report (report.toString ());
        
        return cached;
    }
    
    /** Get the url from a request.
     * 
     *  An explicit ":80" port in a url causes duplicates in the cache. Strip it.
     *
     */
    public static String getURL (Request request) {
        
        final int DefaultHttpPort = 80;
        final String PortString = ":" + String.valueOf (DefaultHttpPort);
        String url;
        
        if (request != null) {
            
            url = request.getURL ();
        
            try {
                URL u = new URL (url);
                if (u.getPort () == DefaultHttpPort) {

                    int i = url.indexOf (PortString);
                    if (i >= 0) { 

                        url = u.toString ();
                        i = url.indexOf (PortString);
                        if (i >= 0) {
                            url = url.substring (0, i) + url.substring (i + PortString.length ());
                        }

                    }

                }
            }
            catch (java.net.MalformedURLException mue) {
                // just leave the existing url alone
            }
            
        }
        else {
            Thread.dumpStack ();
            url = "unknown url";
        }
        
        return url;
    }
    
    /* Get the url's cache file.
     */
    public File getCacheFile (String url) {
        File hostCacheDir = getHostCacheDir (url);
        String filename = getFilename (url);
        return new File (hostCacheDir, filename);
    }
    
    /* Get the url's filename.
     * @param url Url
     */
    public String getFilename (String url) {
        // a hash collision will return the wrong page, but it's very unlikely within a single site
        String urlHash = Integer.toHexString (url.hashCode ()).toUpperCase ();
        return urlHash;
    }
    
    /* Get the url's temporary cache file.
     */
    public File getTempCacheFile (String url) {
        File hostCacheDir = getHostCacheDir (url);
        String filename = getTempFilename (url);
        return new File (hostCacheDir, filename);
    }
    
    /* Get the url's temporary filename.
     * @param url Url
     */
    public String getTempFilename (String url) {
        return getFilename (url) + ".tmp";
    }
    
    /* Get the host dir for this url.
     */
    public File getHostCacheDir (String url) {
        File cacheDir = new File (getCacheDirectory ());
        String host = getHost (url);
        File hostCacheDir = new File (cacheDir, host);
        hostCacheDir.mkdirs ();
        return hostCacheDir;
    }
    
    /* Get the url's host.
     * @param url Url
     */
    public String getHost (String url) {
        String host;
        try {
            host = new URL (url).getHost ();
        }
        catch (java.net.MalformedURLException mue) {
            host = "unknown";
        }
        return host;
    }
    
    public Properties getProperties (String url) {
                
        Properties props = new Properties ();
        
        props.setProperty (Cache.UrlProperty, url);
        
        // formating a date in Java is convoluted
        DateFormat dateFormat = DateFormat.getDateTimeInstance (DateFormat.FULL, DateFormat.FULL, Locale.ENGLISH);
        dateFormat.setTimeZone (GMT);
        String datetime = dateFormat.format (new Date ());
        props.setProperty (Cache.CacheDateProperty, datetime);
        
        return props;
    }
    
    public synchronized void writeProperties (OutputStream out,
                                              Properties props)
    throws IOException {
                                      
        // we don't use Properties.list() because it truncates lines
        
        Enumeration names = props.propertyNames ();
        while (names.hasMoreElements ()) {
            
            String name = (String) names.nextElement ();
            String value = props.getProperty (name);
            String line = name + ": " + value;
            out.write (line.getBytes ());
            out.write (EOL);

        }

        // properties end in a blank line
        out.write (EOL);
    }
    
    public Properties readProperties (InputStream in)
    throws IOException {
        
        Properties props = new Properties ();
        
        // skip lines through the first blank line
        Reply reply = new Reply (in);
        String line = reply.readLine (in);
        while (line != null &&
               line.length () > 0) {
                   
            line = reply.readLine (in);
            int i = line.indexOf (':');
            if (i > 0) {
                String attribute = line.substring (0, i).trim ();
                String value = line.substring (i + 1).trim ();
                props.put (attribute, value);
            }
            
        }
        
        return props;
    }
    
    private void skipPastBlankLine (InputStream in)
    throws IOException {
        // skip lines through the first blank line
        Reply reply = new Reply (in);
        String line = reply.readLine (in);
        while (line != null &&
               line.length () > 0) {
                   
            line = reply.readLine (in);
            
        }
    }
    
    private synchronized void copy (InputStream in, 
                                    OutputStream out)
    throws IOException {
        final int BufferSize = 10000;
        byte [] buffer  = new byte [BufferSize];
        int count = in.read (buffer);
        while (count >= 0) {
            // factory.report ("copying " + String.valueOf (count) + " bytes"); //DEBUG
            Thread.yield ();
            out.flush ();
            out.write (buffer, 0, count);
            count = in.read (buffer);
        }
        out.flush ();
    }
    
    private void reportCacheError (String url,
                                   String why) {
        report ("Warning: Unable to cache url " + url + ": " + why);
    }

    void report (Request request, String message)
    {
	request.addLogEntry (getClass().getName(), message);
	report (message);
    }

    void report (String message)
    {
	messages.append (message + "\n");
        if (Debugging) {
            System.out.println (message);
        }
    }
}
