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
import org.doit.muffin.filter.Decaf;

/**
 * @author Bernhard Wagner <muffinsrc@xmlizer.biz>
 * 
 * TestCase testing the Decaf.
 *
 */
public class DecafTest extends TestCase
{

    /**
     * Constructor for DecafTest.
     * @param arg0
     */
    public DecafTest(String arg0)
    {
        super(arg0);
    }

    public void setUp()
    {
        fDecaf = new Decaf();
        fPrefs = new Prefs();
        fPrefs.putBoolean("Decaf.noJava", true);
        fPrefs.putBoolean("Decaf.noJavaScript", true);
        fDecaf.setPrefs(fPrefs);
        fDecafFilter = (ContentFilter) fDecaf.createFilter();
    }

//    public void testException()
//    {
//        try
//        {
//            fDecafFilter.setPrefs(fPrefs);
//        } catch (RuntimeException e)
//        {
//            return;
//        }
//        fail("Should have thrown RuntimeException.");
//    }
//
    public void testDecafFilterPresence() throws IOException
    {
        assertNotNull(fDecafFilter);

        Reply reply = Utils.makeReply(SAMPLE_RESPONSE);
        assertNotNull(reply);

        assertTrue(fDecafFilter.needsFiltration(null, reply));
    }

    public void testReplacing()
    {
        Reply reply = Utils.makeReply(SAMPLE_RESPONSE);
        String result = Utils.filter(
            fDecafFilter,
            SAMPLE_PAGE.length(),
            reply);
        
        assertEquals(EXPECTED, result);
    }

    public void testReplacingJs()
    {
        Reply reply = Utils.makeReply(SAMPLE_RESPONSEJS);
        String result = Utils.filter(
            fDecafFilter,
            SAMPLE_PAGEJS.length(),
            reply);
        
        assertEquals(EXPECTED, result);
    }

    // apparently the test hangs if dealing with more than 15 child nodes??
    // When uncommenting one of the params below the test hangs!
    private static final String SAMPLE_PAGE = ""
            + "<head><title>Test Page</title></head>\n"
            + "<body><h1><font>\t</font>Test Page</h1>\n"
            + "muffin<font></font> is<font> </font> a <font>funky</font> java project.\n"
            + "<img width=\"301\" height=\"50\" src=\"blabla.gif\" >\n"
            + "<applet code     = \"de.cinderella.CindyApplet\\n"
            +         "archive = \"cindyrun.jar\\n"
            +         "width    = 813\n"
            +         "height   = 428>\n"
            + "<param  name=\"kernelID\"  value=\"1053475830853\">\n"
            + "<param  name     = \"mover\"\n"
            +         "value    = \"P6\">\n"
            + "<param  name     = \"road\"\n"
            +         "value    = \"C1\">\n"
            + "<param  name     = \"animspeed\"\n"
            +         "value    = \"400\">\n"
            + "<param  name=\"viewport\" value=\"de.cinderella.ports.EuclideanPort\">\n"
            + "<param  name=\"filename\" value=\"persp.cdy\">\n"
            + "<param  name=\"polar\" value= \"false\">\n"
            + "<param  name=\"width\" value= \"813\">\n"
            + "<param  name=\"height\" value=\"428\">\n"
            + "<param  name=\"doublebuffer\" value= \"true\">\n"
            + "<param  name=\"mesh\" value=\"false\">\n"
            + "<param  name=\"axes\" value=\"false\">\n"
            + "<param  name=\"snap\" value=\"false\">\n"
            + "<param  name=\"scale\" value=\"18.58334741950032\">\n"
            + "<param  name=\"originx\" value=\"216\">\n"
//            + "<param  name=\"originy\" value=\"265\">\n"
//            + "<param  name=\"originy\" value= \"265\">\n"
//            + "<param  name=\"deltafactor\" value= \"0\">\n"
            + "No Java.\n"
            + "</applet>\n"
            + "</body>\n"
            + "";

    private static final String SAMPLE_PAGEJS = ""
            + "<head><title>Test Page</title><script>"
            + "<!--"
            + "function sf(){document.f.q.focus();}"
            + "function c(p,l,e){var f=document.f;if (f.action && document.getElementById) {var hf=document.getElementById(\"hf\");if (hf) {var t = \"<input type=hidden name=tab value=\"+l+\">\";hf.innerHTML=t;}f.action = 'http://'+p;e.cancelBubble=true;f.submit();return false;}return true;}"
            + "// -->"
            + "</script>"
            +"</head>\n"
            + "<body><h1><font>\t</font>Test Page</h1>\n"
            + "muffin<font></font> is<font> </font> a <font>funky</font> java project.\n"
            + "<img width=\"301\" height=\"50\" src=\"blabla.gif\" >\n\n"
            + "</body>\n"
            + "";


    private static final String EXPECTED =
        ""
            + "<head><title>Test Page</title></head>\n"
            + "<body><h1><font>\t</font>Test Page</h1>\n"
            + "muffin<font></font> is<font> </font> a <font>funky</font> java project.\n"
            + "<img width=\"301\" height=\"50\" src=\"blabla.gif\" >\n\n"
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

    private static final String SAMPLE_RESPONSEJS =
        "HTTP/1.0 302 Found\n"
            + "Content-Type: text/html\n"
            + "Location: http://xmlizer.biz:8080/index.html\n"
            + "Content-Length: 300\n"
            + "Servlet-Engine: Tomcat Web Server/3.2 beta 3 (JSP 1.1; Servlet 2.2; Java 1.2.2; Linux 2.2.24-7.0.3smp i386; java.vendor=Blackdown Java-Linux Team)\n"
            + "\n"
            + SAMPLE_PAGEJS
            + "";

    private Decaf fDecaf;
    private ContentFilter fDecafFilter;
    private Prefs fPrefs;

}
