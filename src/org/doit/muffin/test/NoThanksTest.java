/*
 * Copyright (C) 2003 Bernhard Wagner <bw@xmlizer.biz>
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
package org.doit.muffin.test;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;

import org.doit.io.*;
import org.doit.muffin.*;
import org.doit.muffin.filter.*;
import org.doit.muffin.regexp.*;

import org.doit.muffin.filter.NoThanks;

/**
 * @author Bernhard Wagner <bw@xmlizer.biz>
 * 
 * TestCase testing the NoThanks.
 *
 */
public class NoThanksTest extends TestCase
{

    /**
     * Constructor for NoThanksTest.
     * @param arg0
     */
    public NoThanksTest(String arg0)
    {
        super(arg0);
    }

    public void setUp() throws FilterException
    {
        fNoThanks = new NoThanks();
        fPrefs = new Prefs();
        fNoThanks.setPrefs(fPrefs);
        fNoThanksFilter = (NoThanksFilter) fNoThanks.createFilter();
        fReply = Utils.makeReply(SAMPLE_RESPONSE);
        assertNotNull(fReply);
        //since NoThanks implements ReplyFilter:
        fNoThanksFilter.filter(fReply); // needed for setting reply in filter
        
        // NoThanks implements ContentFilter, so the following statement would be needed.
        // But since it also implements ReplyFilter the above statement sets the Reply
        // already.
//        fNoThanksFilter.needsFiltration(null, fReply); // needed for setting reply in filter
    }

    public void testNoThanksFilterPresence() throws IOException
    {
        assertNotNull(fNoThanksFilter);

        assertNotNull(fReply);

        assertTrue(fNoThanksFilter.needsFiltration(null, fReply));
    }

    public void testReplacing() throws FilterException
    {
        fNoThanks.load(
            Utils.makeBufferedReaderFromString(SAMPLE_KILLFILE));
        String result = Utils.filter(
            fNoThanksFilter,
            SAMPLE_PAGE.length(),
            fReply);
        
        assertEquals(EXPECTED, result);
    }

    public void testNoReplacing() throws FilterException
    {
        fNoThanks.load(
            Utils.makeBufferedReaderFromString(SAMPLE_KILLFILE_EMPTY));
        String result = Utils.filter(
            fNoThanksFilter,
            SAMPLE_PAGE.length(),
            fReply);
        
        assertEquals(SAMPLE_PAGE, result);
    }

    private static final String SAMPLE_PAGE = ""
            + "<head><title>Test Page</title></head>\n"
            + "<body bgcolor=\"green\"><h1><font>\t</font>Test Page</h1>\n"
            + "muffin<font></font> is<font> </font> a <font>funky</font> java project.\n"
            + "<img width=\"301\" height=\"50\" src=\"blabla.gif\" >\n"
            + "</body>\n"
            + "";

    private static final String EXPECTED =
        ""
            + "<head><title>Test Page</title></head>\n"
            + "<body><h1><font>\t</font>Test Page</h1>\n"
            + "muffin<font></font> is<font> </font> a <font>funky</font> java project.\n"
            + "<img width=\"301\" height=\"50\" src=\"blabla.gif\" >\n"
            + "</body>\n"
            + "";

    private static final String SAMPLE_RESPONSE =
        "HTTP/1.0 302 Found\n"
            + "Content-Type: text/html\n"
            + "Location: http://xmlizer.biz:8080/index.html\n"
            + "Content-Length: 300\n"
            + "Servlet-Engine: Tomcat Web Server/3.2 beta 3 (JSP 1.1; Servlet 2.2; Java 1.2.2; Linux 2.2.24-7.0.3smp i386; java.vendor=Blackdown Java-Linux Team)\n"
            + "\n"
            + SAMPLE_PAGE
            + "";

    private static final String SAMPLE_KILLFILE =
        ""
            + "tagattr body.bgcolor remove\n"
            + "";

    private static final String SAMPLE_KILLFILE_EMPTY =
        ""
            + ""
            + "";

    private NoThanks fNoThanks;
    private NoThanksFilter fNoThanksFilter;
    private Reply fReply;
    private Prefs fPrefs;

}
