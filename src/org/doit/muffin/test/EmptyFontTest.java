/*
 * Copyright (C) 2006 Bernhard Wagner <muffinsrc@xmlizer.biz>
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
import junit.framework.TestCase;

import org.doit.muffin.*;
import org.doit.muffin.regexp.*;

import org.doit.muffin.filter.EmptyFont;

/**
 * @author Bernhard Wagner <muffinsrc@xmlizer.biz>
 * 
 * TestCase testing the EmptyFont.
 *
 */
public class EmptyFontTest extends TestCase
{

    /**
     * Constructor for EmptyFontTest.
     * @param arg0
     */
    public EmptyFontTest(String arg0)
    {
        super(arg0);
        //		System.out.println("-----");
        //		System.out.println(IMPLS);
        //		System.out.println("-----");
    }

    public void setUp()
    {
        fEmptyFont = new EmptyFont();
        fEmptyFontFilter = (ContentFilter) fEmptyFont.createFilter();
        fReply = Utils.makeReply(SAMPLE_RESPONSE);
    }

    public void testEmptyFontFilterPresence() throws IOException
    {
        assertNotNull(fEmptyFontFilter);

        assertNotNull(fReply);

        assertTrue(fEmptyFontFilter.needsFiltration(null, fReply));
    }

    public void testReplacing()
    {
        String result = Utils.filter(
            fEmptyFontFilter,
            SAMPLE_RESPONSE.length(),
            fReply);

        Pattern pat =
            org.doit.muffin.regexp.Factory.instance().getPattern(
                "<font>([ \t]*)</font>",
                true);
        String expected = pat.substituteAll(SAMPLE_PAGE, "$1");

        assertEquals(expected, result);
    }

    private static final String SAMPLE_PAGE = ""
            + "<head><title>Test Page</title></head>\n"
            + "<body><h1><font>\t</font>Test Page</h1>\n"
            + "muffin<font></font> is<font> </font> a <font>funky</font> java project.\n"
            + "</body>\n"
            + "";

    private static final String SAMPLE_RESPONSE =
        "HTTP/1.0 302 Found\n"
            + "Content-Type: text/html\n"
            + "Location: http://xmlizer.biz:8080/index.html\n"
            + "Content-Length: 176\n"
            + "Servlet-Engine: Tomcat Web Server/3.2 beta 3 (JSP 1.1; Servlet 2.2; Java 1.2.2; Linux 2.2.24-7.0.3smp i386; java.vendor=Blackdown Java-Linux Team)\n"
            + "\n"
            + SAMPLE_PAGE
            + "";

    private EmptyFont fEmptyFont;
    private ContentFilter fEmptyFontFilter;
    private Reply fReply;

}
