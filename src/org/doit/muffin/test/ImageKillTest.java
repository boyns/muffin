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

import org.doit.muffin.filter.ImageKill;

/**
 * @author Bernhard Wagner <bw@xmlizer.biz>
 * 
 * TestCase testing the ImageKill.
 *
 */
public class ImageKillTest extends TestCase
{

    /**
     * Constructor for ImageKillTest.
     * @param arg0
     */
    public ImageKillTest(String arg0)
    {
        super(arg0);
    }

    public void setUp()
    {
        fImageKill = new ImageKill();
        fImageKillFilter = (ContentFilter) fImageKill.createFilter();
        fPrefs = new Prefs();
        // parameters:
        fPrefs.putInteger("ImageKill.minheight", 49);
        fPrefs.putInteger("ImageKill.minwidth", 300);
        fPrefs.putInteger("ImageKill.ratio", 5);
        fPrefs.putBoolean("ImageKill.keepmaps", true);
        fPrefs.putString("ImageKill.exclude", "(button|map)");
        fPrefs.putString("ImageKill.rmSizes", "468x60,450x40");
        fPrefs.putString(
            "ImageKill.replaceURL",
            "file:/usr/local/images/empty.gif");
        fPrefs.putBoolean("ImageKill.replace", false);
        fImageKill.setPrefs(fPrefs);
    }

    public void testException()
    {
        try
        {
            fImageKillFilter.setPrefs(fPrefs);
        } catch (RuntimeException e)
        {
            return;
        }
        fail("Should have thrown RuntimeException.");
    }

    public void testImageKillFilterPresence() throws IOException
    {
        assertNotNull(fImageKillFilter);

        Reply reply = Utils.makeReply(SAMPLE_RESPONSE);
        assertNotNull(reply);

        assertTrue(fImageKillFilter.needsFiltration(null, reply));
    }

    public void testReplacing()
    {
        Reply reply = Utils.makeReply(SAMPLE_RESPONSE);
        String result = Utils.filter(
            fImageKillFilter,
            SAMPLE_RESPONSE.length(),
            reply);

        Pattern pat =
            org.doit.muffin.regexp.Factory.instance().getPattern(
                "<img.*>",
                true);
        String expected = pat.substituteAll(SAMPLE_PAGE, "");

        assertEquals(expected, result);
    }

    private static final String SAMPLE_PAGE =
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

    private ImageKill fImageKill;
    private ContentFilter fImageKillFilter;
    private Reply fReply;
    private Prefs fPrefs;

}
