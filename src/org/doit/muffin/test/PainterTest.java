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
import org.doit.muffin.regexp.*;

import org.doit.muffin.filter.Painter;

/**
 * @author Bernhard Wagner <bw@xmlizer.biz>
 * 
 * TestCase testing the Painter.
 *
 */
public class PainterTest extends TestCase
{

    /**
     * Constructor for PainterTest.
     * @param arg0
     */
    public PainterTest(String arg0)
    {
        super(arg0);
    }

    public void setUp()
    {
        fPainter = new Painter();
        fPrefs = new Prefs();
        fPrefs.putString("Painter.alink", "#ff0000");
        fPrefs.putString("Painter.background", "");
        fPrefs.putString("Painter.bgcolor", "#000000");
        fPrefs.putString("Painter.link", "#98fb98");
        fPrefs.putString("Painter.text", "#ffffff");
        fPrefs.putString("Painter.vlink", "#ffa07a");
        fPainter.setPrefs(fPrefs);
        fPainterFilter = (ContentFilter) fPainter.createFilter();
    }

    public void testPainterFilterPresence() throws IOException
    {
        assertNotNull(fPainterFilter);

        Reply reply = Utils.makeReply(SAMPLE_RESPONSE);
        assertNotNull(reply);

        assertTrue(fPainterFilter.needsFiltration(null, reply));
    }

    public void testReplacing()
    {
        Reply reply = Utils.makeReply(SAMPLE_RESPONSE);
        String result = Utils.filter(
            fPainterFilter,
            reply.getContent(),
            SAMPLE_PAGE.length(),
            reply);
        
        assertEquals(EXPECTED, result);
    }

    private static final String SAMPLE_PAGE = ""
            + "<head><title>Test Page</title></head>\n"
            + "<body><h1><font>\t</font>Test Page</h1>\n"
            + "muffin<font></font> is<font> </font> a <font>funky</font> java project.\n"
            + "<img width=\"301\" height=\"50\" src=\"blabla.gif\" >\n"
            + "</body>\n"
            + "";

    private static final String EXPECTED =
        ""
            + "<head><title>Test Page</title></head>\n"
            + "<body link=\"#98fb98\" text=\"#ffffff\" bgcolor=\"#000000\" alink=\"#ff0000\" vlink=\"#ffa07a\"><h1><font>\t</font>Test Page</h1>\n"
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

    private Painter fPainter;
    private ContentFilter fPainterFilter;
    private Reply fReply;
    private Prefs fPrefs;

}
