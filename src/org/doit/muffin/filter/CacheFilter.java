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

import java.util.*;
import java.io.*;
import java.net.*;
import java.text.DateFormat;

import org.doit.muffin.*;

/** Filter to cache web content.
 *
 * Caching helps some, but it isn't very effective. Too much web content is dynamic, and too many
 * web hosts explicitly defeat caches. Ad revenue, tracking, etc.
 *
 * Cache file format is
 *
 *     <properties>
 *     BLANKLINE
 *     <http status line>
 *     <http headers>
 *     BLANKLINE
 *     <content>
 *
 * Read and write a cached message's properties using readProperties() and writeProperties().
 *
 * CacheFilter implements the RequestFilter, ReplyFilter, and HttpFilter interfaces. Note that it also
 * implements HttpRelay, a superinterface of HttpFilter.
 */
public class CacheFilter
implements RequestFilter, ReplyFilter, HttpFilter
{
    /* This was java.lang.Integer.MAX_VALUE, but kaffe ran out of memory
    */
    final int MaxCachedObjectSize = 1000000;

    /** Pragma attribute name.
     */
    final String PragmaAttribute = "Pragma";

    /** Cache-control attribute name.
     */
    final String CacheControlAttribute = "Cache-control";

    /** Authorization attribute name.
     */
    final String AuthorizationAttribute = "Authorization";

    /** Expires attribute name.
     */
    final String ExpiresAttribute = "Expires";

    /** Location attribute name.
     */
    final String LocationAttribute = "Location";

    /** Etag attribute name.
     */
    final String EtagAttribute = "Etag";

    /** Last-modified attribute name.
     */
    final String LastModifiedAttribute = "Last-Modified";

    /** If-Modified-Since attribute name.
     */
    final String IfModifiedSinceAttribute = "If-Modified-Since";

    /** Date attribute name.
     */
    final String DateAttribute = "Date";

    /** Content-length attribute name.
     */
    final String ContentLengthAttribute = "Content-length";
    
    /** X-Muffin-From-cache attribute
     */
    final String FromCacheAttribute = "X-Muffin-From-cache";

    /** No-cache value.
     */
    final String NoCache = "no-cache";

    /** Cache factory, i.e. generator for Cache instances.
     */    
    Cache factory;
    
    /** Preferences for this filter.
     */    
    Prefs prefs;
    
    /** Set of urls we've checked this session
     */
    HashSet checkedUrls = new HashSet ();

    /** Create an instance of CacheFilter.
     */    
    public CacheFilter (Cache factory)
    {
	this.factory = factory;
    }

    /** Set the preferences for this filter.
     */
    public void setPrefs (Prefs prefs)
    {
	this.prefs = prefs;
    }

    /** Filter a request.
     */
    public synchronized void filter (Request request)
    throws FilterException {
        /*
        if (Cache.Debugging) {
            factory.report ("filtering request: " + Cache.getURL (request)); //DEBUG
            reportHeaders (factory, request); //DEBUG
            Thread.dumpStack (); //DEBUG
        }
        */
    }

    /** If the reply's not already cached, cache it.
     */
    public synchronized void filter (Reply reply)
    throws FilterException {

        if (reply != null) {

            if (reply.getRequest () != null) {

                // factory.report ("filtering reply: " + Cache.getURL (reply.getRequest ())); //DEBUG
                // reportHeaders (factory, reply); //DEBUG

                if (isReplyCachable (reply)) {

                    class CacheInputStream
                    extends BufferedInputStream {

                        Reply r;

                        public CacheInputStream (Reply r) {

                            super (r.getContent ());
                            this.r = r;
                            r.setContent (this);
                            // mark the start of the stream for the later call to reset
                            mark (MaxCachedObjectSize);

                        }

                        // this only works if Handler calls reply.getContent().close(), so now it does
                        public void close ()
                        throws IOException {

                            // cache the reply
                            // to maintain a local copy we also cache urls which are not officially "cachable"
                            factory.cacheReply (r);
                            // Thread.dumpStack (); //DEBUG

                        }

                    }

                    reply.setContent (new CacheInputStream (reply));
            
                    String url = Cache.getURL (reply.getRequest ());
                    checkedUrls.add (url); 

                }
            }
            else {
                
                factory.report ("null request in filter (Reply)");

            }
        }
        else {

            factory.report ("null reply in filter");

        }
    }

    /** Returns whether this filter wants to provide a reply to this request.
     *  We do if we have a cached reply.
     */
    public synchronized boolean wantRequest (Request request) {

        String url = Cache.getURL (request);
        // factory.report ("checking whether want request: " + url); //DEBUG

        boolean weWantToProcessThisRequest;

        if (isGET (request)) {

            weWantToProcessThisRequest = isReplyCached (request);

            if (weWantToProcessThisRequest) {
                String pathname = factory.getCacheFile (url).getAbsolutePath ();
                factory.report ("from cache: " + url + ": " + pathname);
            }
            else {
                factory.report ("from net: " + url);
                // Thread.dumpStack (); //DEBUG
            }

        }
        else {

            weWantToProcessThisRequest = false;

        }

        return weWantToProcessThisRequest;
    }

    /** Send a request
     *
     * Only called if wantRequest returns true.
     *
     * We don't need to send the request since the reply is in the cache.
     */    
    public void sendRequest (Request request) {
    }
    
    /** Provide a cached reply to a request.
     *
     * Only called if wantRequest returns true.
     */    
    public synchronized Reply recvReply (Request request) {

        factory.report ("receiving a cached reply: " + Cache.getURL (request)); //DEBUG
        String url = Cache.getURL (request);
        Reply reply = factory.getCachedReply (url);
        reply.setRequest (request);
        reply.setHeaderField (FromCacheAttribute, Boolean.TRUE.toString ());

        return reply;

    }

    /** Close the http relay.
     */
    public void close () {
    }

    private boolean isRequestCachable (Request request) {

        String pragmaValue = request.getHeaderField (PragmaAttribute);
        String cacheControlValue = request.getHeaderField (CacheControlAttribute);
        String authorizationValue = request.getHeaderField (AuthorizationAttribute);
        String url = Cache.getURL (request);
        // factory.report ("checking whether request is cachable: " + url); //DEBUG

        // "Pragma:private" and "Cache-control:private" both mean that the reply should not be
        // cached on *shared* storage. We ignore them.

        boolean cachable = true;
        String reason = "";

        if (NoCache.equalsIgnoreCase (pragmaValue)) {
            cachable = false;
            reason = PragmaAttribute + " is " + pragmaValue;
        }

        else if (NoCache.equalsIgnoreCase (cacheControlValue)) {
            cachable = false;
            reason = CacheControlAttribute + " is " + cacheControlValue;
        }

        else if (authorizationValue != null) {
            cachable = false;
            reason = AuthorizationAttribute + " supplied";
        }

        // only "GET", not "POST" or "HEAD", etc. is cached
        // dynamic content such as cgi and servlets are never cached
        else if (! isGET (request)) {
            cachable = false;
            reason = "not GET (" + request + ")";
        }

        else if (url == null) {
            cachable = false;
            reason = "url is null";
        }

        else if (url.indexOf ('?') >= 0) {
            cachable = false;
            reason = "url contains '?'";
        }

        // !!!!! this should be an explicit "never-cache" pattern
        else if (url.toLowerCase ().indexOf ("cgi") >= 0) {
            cachable = false;
            reason = "url contains \"cgi\"";
        }

        // !!!!! this should be an explicit "never-cache" pattern
        else if (url.toLowerCase ().indexOf ("servlet") >= 0) {
            cachable = false;
            reason = "url contains \"servlet\"";
        }

        if (cachable) {
            // factory.report ("request cachable: " + url);
        }
        else {
            factory.report ("request not cachable: " + url + ": " + reason);
        }

        return cachable;
    }

    private boolean isReplyCachable (Reply reply) {

        boolean cachable = false;
        String reason = null;

        // if it's not cached already, and the result code was ok, and it's a GET request
        String alreadyCachedValue = reply.getHeaderField (FromCacheAttribute);
        if (alreadyCachedValue == null ||
            ! Boolean.getBoolean (alreadyCachedValue)) {
                
            int statusCode = reply.getStatusCode ();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                
                if (isGET (reply.getRequest ())) {

                    // to maintain a local copy we also cache urls which are not officially "cachable"
                    cachable = true;

                }
                else {
                    reason = "not GET";
                    
                }
                
            }
            else {

                reason = "status code is "+ String.valueOf (statusCode);

            }
        }
        else {

            reason = "already cached";

        }
        
        if (! cachable) {
            
            String url = Cache.getURL (reply.getRequest ());
            factory.report ("reply not cachable: " + url + ": " + reason);
            // reportHeaders (factory, reply);
            
        }
        
        return cachable;
    }

    /** Returns whether we have a cached reply to this request.
     */
    public boolean isReplyCached (Request request) {

        String url = Cache.getURL (request);

        // we cache everything to keep a local copy, but not everything is officially cacheable
        return isRequestCachable (request) &&
               factory.isCached (url) &&
               ! needRefresh (url);

    }

    private boolean needRefresh (String url) {

        boolean refresh = false;
        String reason = "";

        // if we're always supposed to check, or we check once per session and haven't checked this url yet
        if (factory.checkForUpdatesAlways() ||
            (factory.checkForUpdatesOncePerSession () &&
             ! checkedUrls.contains (url))) {

            try {

                Reply cachedCopy = factory.getCachedReply (url);

                // check for expiration
                Date now = new Date ();
                Date expirationDate = parseDate (cachedCopy.getHeaderField (ExpiresAttribute));
                if (expirationDate != null &&
                    now.after (expirationDate)) {

                    refresh = true;
                    reason = "url expired";

                }

                else {

                    try {

                        /* UNUSED
                         * if we ever use this fix fieldChanged so it doesn't use Handler.GzipContentLengthAttribute
                        // get reply via Muffin
                        Request request = new Request ();
                        HttpRelay http;
                        if (url.toLowerCase ().startsWith ("https")) {
                            http = new Https(request.getHost(), request.getPort());
                        }
                        else {
                            http = new Http(request.getHost(), request.getPort());
                        }
                        http.sendRequest(request);
                        if (http instanceof Http)
                        {
                            ((Http)http).setTimeout(options.getInteger("muffin.readTimeout"));
                        }
                        Reply reply = http.recvReply(request);
                        reply.setRequest (request);
                        end UNUSED */

                        // not all servers support If-Modified-Since or If-None-Match, but all support HEAD
                        // In fact If-Modified-Since is usually ignored with HEAD, but it doesn't matter since
                        // checking the Last-modified field does the same thing

                        int responseCode = 0;
                        HttpURLConnection netCopy = getNetHead (url);
                        if (netCopy != null) {
                            responseCode = netCopy.getResponseCode ();
                        }

                        // follow redirects
                        /*
                        HashSet redirects = new HashSet ();
                        while (responseCode == HttpURLConnection.HTTP_MOVED_PERM ||
                               responseCode == HttpURLConnection.HTTP_MOVED_PERM) {

                            url = netCopy.getHeaderField (LocationAttribute);

                            if (redirects.contains (url)) {
                                reason = "Loop in redirects";
                                responseCode = HttpURLConnection.HTTP_CONFLICT;
                            }
                            else {
                                redirects.add (url);
                                netCopy = getNetHead (url);
                            }

                        }
                        */

                        if (responseCode == HttpURLConnection.HTTP_NOT_MODIFIED) {

                            reason = "not modified";
                            refresh = false;

                        }

                        else if (responseCode == HttpURLConnection.HTTP_OK) {

                            // check for changed header field values
                            if (fieldChanged (cachedCopy, netCopy, EtagAttribute)) {

                                refresh = true;
                                reason = EtagAttribute + " changed";

                            }
                            else if (fieldChanged (cachedCopy, netCopy, LastModifiedAttribute)) {

                                refresh = true;
                                reason = LastModifiedAttribute + " changed";

                            }
                            else if (fieldChanged (cachedCopy, netCopy, ContentLengthAttribute)) {

                                refresh = true;
                                reason = ContentLengthAttribute + " changed";

                            }
                            else {

                                // if there's nothing better, check the Date field
                                if (netCopy.getHeaderField (EtagAttribute) == null &&
                                    netCopy.getHeaderField (LastModifiedAttribute) == null &&
                                    fieldChanged (cachedCopy, netCopy, DateAttribute)) {

                                    refresh = true;
                                    reason = DateAttribute + " changed";

                                }
                            }
                        }

                        else {

                            // if anything went wrong reading the net, use the cached version
                            if (reason==null) {
                                reason = "bad HEAD result code: " + String.valueOf (responseCode);
                            }
                            refresh = false;

                        }

                    }
                    catch (Exception e) {
                        // if anything went wrong reading the net, use the cached version
                        reason = "need refresh: exception: " + e;
                        e.printStackTrace (); //DEBUG
                        refresh = false;
                    }

                }
            }
            catch (Exception e) {
                // if anything went wrong reading the cache, use the net version
                reason = "need refresh: " + e;
                e.printStackTrace (); //DEBUG
                refresh = true;
            }

            if (Cache.Debugging) {
                if (refresh) {
                    String message = "need refresh: " + url + ": " + reason;
                    factory.report (message);
                }
            }

            checkedUrls.add (url);
        }

        return refresh;
    }

    private boolean isGET (Request request) {

        final String HttpGet = "GET";
        boolean is = false;
        if (request != null) {
            if (request.getRequest () != null) {

                is = request.getRequest ().toUpperCase ().startsWith (HttpGet);

            }
            else {

                factory.report ("null request type in isGet");

            }

        }
        else {

            factory.report ("null request in isGet");

        }
        return is;
    }

    /** 
     * @returns parsed Date, or null if not parseable.
     */
    private Date parseDate (String dateString) {
        Date date;

        try {
            // parsing a date in Java is convoluted
            DateFormat dateFormat = DateFormat.getDateTimeInstance (DateFormat.FULL, 
                                                                    DateFormat.FULL,
                                                                    Locale.ENGLISH);
            dateFormat.setTimeZone (Cache.GMT);
            dateFormat.setLenient (true);
            date = dateFormat.parse (dateString);
        }
        catch (Exception e) {
            date = null;
        }
        
        return date;
    }
    
    private boolean fieldChanged (Reply cachedCopy, 
                                  HttpURLConnection netCopy, 
                                  String attribute) {

        String cachedValue = cachedCopy.getHeaderField (attribute);
        String netValue = netCopy.getHeaderField (attribute);
        
        // handle gzipped content
        if (attribute.equalsIgnoreCase (ContentLengthAttribute)) {
            String gzipLength = cachedCopy.getHeaderField (Reply.GzipContentLengthAttribute);
            if (gzipLength != null) {
                cachedValue = gzipLength;
            }
        }

        boolean changed = (cachedValue == null && netValue != null) ||
                          (cachedValue != null && netValue == null) ||
                          (cachedValue != null && netValue != null && (! cachedValue.equals (netValue)));
        
        if (changed) {
            // /*
            factory.report ("need refresh: "+
                            netCopy.getURL () +
                            ": field " +
                            attribute +
                            " changed from '" +
                            cachedValue +
                            "' to '" +
                            netValue +
                            "'");
            // */
        }
        
        return changed;
    }

    // this is static and synchronized so different instances don't cause race conditions
    public static synchronized void reportHeaders (Cache factory,
                                                   Message message) {
        
        final String Indent = "    ";
        
        if (message instanceof Request) {
            Request r = (Request) message;
            factory.report ("request: " + r.getRequest ());
        }
        else if (message instanceof Reply) {
            Reply r = (Reply) message;
            Request request = r.getRequest ();
            if (request != null) {
                factory.report ("reply: " + Cache.getURL (request));
            }
            else {
               factory.report ("cache:");
            }
            factory.report (Indent + r.getStatusLine ());
        }
        else {
            factory.report ("unknown message type:");
        }
        
        Enumeration keys = message.getHeaders ();
        while (keys.hasMoreElements ()) {
            
            String name = keys.nextElement ().toString ();
            String value = message.getHeaderField (name);
            String line = Indent + name + ":" + value;
            factory.report (line);
        }
        
        // factory.report ("-----------------------------------");
    }
    
    // this is static and synchronized so different instances don't cause race conditions
    public static synchronized void reportURLConnectionHeaders (Cache factory, 
                                                                HttpURLConnection http) {
        
        final String Indent = "    ";
        
        factory.report ("net: " + http.getRequestMethod () + " " + http.getURL ());
        try {
            factory.report (Indent + String.valueOf (http.getResponseCode ()) + " " + http.getResponseMessage ());
        }
        catch (IOException ioe) {
            // just skip it
        }
        
        int i = 1;
        String key = http.getHeaderFieldKey (i);
        while (key != null) {
            
            String value = http.getHeaderField (key);
            String line = Indent + key + ":" + value;
            factory.report (line);
            
            ++ i;
            key = http.getHeaderFieldKey (i);
        }

        // factory.report ("-----------------------------------");
    }

    private HttpURLConnection getNetHead (String url)
    throws IOException {
        HttpURLConnection netCopy = null;
        HttpURLConnection.setFollowRedirects (true);
        final String HeadRequestMethod = "HEAD";
        try {
            netCopy = (HttpURLConnection) new URL (url).openConnection ();
            if (Cache.Debugging) {
                factory.report ("got net head for " + url);
            }
        }
        catch (Exception e) {
            factory.report ("unable to get net head for " +
                            url +
                            ": " +
                            e);
        }
        netCopy.setRequestMethod (HeadRequestMethod);
        /*
        String lastModified = cachedCopy.getHeaderField (LastModifiedAttribute);
        if (lastModified != null) {
            // we don't use setLastModified because some servers only recognize their own date formats
            netCopy.setRequestProperty (IfModifiedSinceAttribute, lastModified);
        }
        */
        netCopy.connect ();

        if (Cache.Debugging) {
            // factory.report ("comparing cache and net: " + url);
            // reportHeaders (factory, cachedCopy);
            // reportURLConnectionHeaders (factory, netCopy);
        }

        return netCopy;
    }

}
