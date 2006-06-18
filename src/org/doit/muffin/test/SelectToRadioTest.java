/*
 * Copyright (C) 2003 Bernhard Wagner <muffinsrc@xmlizer.biz>
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
import org.doit.muffin.filter.SelectToRadio;

/**
 * @author Bernhard Wagner <muffinsrc@xmlizer.biz>
 * 
 * TestCase testing the SelectToRadio.
 *
 */
public class SelectToRadioTest extends TestCase
{

    /**
     * Constructor for SelectToRadioTest.
     * @param arg0
     */
    public SelectToRadioTest(String arg0)
    {
        super(arg0);
    }

    public void setUp()
    {
        fSelectToRadioFilter = (ContentFilter) new SelectToRadio().createFilter();
    }

    public void testSelectToRadioFilterPresence() throws IOException
    {
        assertNotNull(fSelectToRadioFilter);

        Reply reply = Utils.makeReply(makeResponse(SELECT_PAGE));
        assertNotNull(reply);

        assertTrue(fSelectToRadioFilter.needsFiltration(null, reply));
    }

    public void testReplacing()
    {
       String response = makeResponse(SELECT_PAGE);
       Reply reply = Utils.makeReply(response);
        String result = Utils.filter(
            fSelectToRadioFilter,
            response.length(),
            reply);

        assertEquals(RADIO_PAGE_HORIZONTAL, result);
    }

    private static final String SELECT_PAGE = ""
        + "<html><head><title>Test Page</title></head>\n"
        + "<body><h1>Test Page</h1>\n"
        + "<form action=\"bla.html\">"
        + "<select name=\"menu\">"
        + "<option value=\"1\">one</option>"
        + "<option value=\"2\">two</option>"
        + "<option value=\"3\">three</option>"
        + "<option value=\"4\">four</option>"
        + "</select>"
        + "<input type=\"submit\" value=\"go\">"
        + " </form>"
        + "</body></html>\n"
        + "";

    private static final String RADIO_PAGE_HORIZONTAL = ""
        + "<html><head><title>Test Page</title></head>\n"
        + "<body><h1>Test Page</h1>\n"
        + "<form action=\"bla.html\">"
        + "<input type=\"radio\" name=\"menu\" value=\"1\">one"
        + "<input type=\"radio\" name=\"menu\" value=\"2\">two"
        + "<input type=\"radio\" name=\"menu\" value=\"3\">three"
        + "<input type=\"radio\" name=\"menu\" value=\"4\">four"
        + "<input type=\"submit\" value=\"go\">"
        + " </form>"
        + "</body></html>\n"
        + "";

    public void testReplacingSelected()
    {
        String response = makeResponse(SELECT_PAGE_SELECTED);
        Reply reply = Utils.makeReply(response);
        String result = Utils.filter(
            fSelectToRadioFilter,
            response.length(),
            reply);

        assertEquals(RADIO_PAGE_SELECTED, result);
    }

    private static final String RADIO_PAGE_SELECTED = ""
            + "<html><head><title>Test Page</title></head>\n"
            + "<body><h1>Test Page</h1>\n" + "<form action=\"bla.html\">"
            + "<input type=\"radio\" name=\"menu\" value=\"1\">one"
            + "<input type=\"radio\" name=\"menu\" value=\"2\">two"
            + "<input type=\"radio\" name=\"menu\" value=\"3\" checked>three"
            + "<input type=\"radio\" name=\"menu\" value=\"4\">four"
            + "<input type=\"submit\" value=\"go\">" + " </form>"
            + "</body></html>\n" + "";

    private static final String SELECT_PAGE_SELECTED = ""
        + "<html><head><title>Test Page</title></head>\n"
        + "<body><h1>Test Page</h1>\n"
        + "<form action=\"bla.html\">"
        + "<select name=\"menu\">"
        + "<option value=\"1\">one</option>"
        + "<option value=\"2\">two</option>"
        + "<option value=\"3\" selected>three</option>"
        + "<option value=\"4\">four</option>"
        + "</select>"
        + "<input type=\"submit\" value=\"go\">"
        + " </form>"
        + "</body></html>\n"
        + "";

    public void testReplacingMultipleSelected()
    {
        String response = makeResponse(SELECT_PAGE_MULTIPLE);
        Reply reply = Utils.makeReply(response);
        String result = Utils.filter(
            fSelectToRadioFilter,
            response.length(),
            reply);

        assertEquals(CHECK_PAGE_SELECTED, result);
    }

    private static final String SELECT_PAGE_MULTIPLE = ""
        + "<html><head><title>Test Page</title></head>\n"
        + "<body><h1>Test Page</h1>\n"
        + "<form action=\"bla.html\">"
        + "<select name=\"menu\" multiple>"
        + "<option value=\"1\">one</option>"
        + "<option value=\"2\" selected>two</option>"
        + "<option value=\"3\" selected>three</option>"
        + "<option value=\"4\">four</option>"
        + "</select>"
        + "<input type=\"submit\" value=\"go\">"
        + " </form>"
        + "</body></html>\n"
        + "";

    private static final String CHECK_PAGE_SELECTED = ""
        + "<html><head><title>Test Page</title></head>\n"
        + "<body><h1>Test Page</h1>\n"
        + "<form action=\"bla.html\">"
        + "<input type=\"checkbox\" name=\"menu\" value=\"1\">one"
        + "<input type=\"checkbox\" name=\"menu\" value=\"2\" checked>two"
        + "<input type=\"checkbox\" name=\"menu\" value=\"3\" checked>three"
        + "<input type=\"checkbox\" name=\"menu\" value=\"4\">four"
        + "<input type=\"submit\" value=\"go\">"
        + " </form>"
        + "</body></html>\n"
        + "";

    private static final String RADIO_PAGE_VERTICAL = ""
        + "<html><head><title>Test Page</title></head>\n"
        + "<body><h1>Test Page</h1>\n"
        + "<form action=\"bla.html\">"
        + "<input type=\"radio\" name=\"menu\" value=\"1\">one<br>"
        + "<input type=\"radio\" name=\"menu\" value=\"2\">two<br>"
        + "<input type=\"radio\" name=\"menu\" value=\"3\">three<br>"
        + "<input type=\"radio\" name=\"menu\" value=\"4\">four<br>"
        + "<input type=\"submit\" value=\"go\">"
        + " </form>"
        + "</body></html>\n"
        + "";

    private static String makeResponse(String htmlPage) {
        return "HTTP/1.0 302 Found\n"
        + "Content-Type: text/html\n"
        + "Location: http://xmlizer.biz:8080/index.html\n"
        + "Content-Length: 300\n"
        + "Servlet-Engine: Tomcat Web Server/3.2 beta 3 (JSP 1.1; Servlet 2.2; Java 1.2.2; Linux 2.2.24-7.0.3smp i386; java.vendor=Blackdown Java-Linux Team)\n"
        + "\n"
        + htmlPage
        + "";
    }

    private ContentFilter fSelectToRadioFilter;

}
