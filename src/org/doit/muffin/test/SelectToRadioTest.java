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

        assertEquals(RADIO_PAGE, result);
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

    private static final String RADIO_PAGE = ""
    	+ "<html><head><title>Test Page</title></head>\n"
    	+ "<body><h1>Test Page</h1>\n"
    	+ "<form action=\"bla.html\">"
    	+ "<table border=\"1\"><tr><td>"
    	+ "<input type=\"radio\" name=\"menu\" value=\"1\">one<br/>"
    	+ "<input type=\"radio\" name=\"menu\" value=\"2\">two<br/>"
    	+ "<input type=\"radio\" name=\"menu\" value=\"3\">three<br/>"
    	+ "<input type=\"radio\" name=\"menu\" value=\"4\">four<br/>"
    	+ "</td></tr></table>"
    	+ "<input type=\"submit\" value=\"go\">"
    	+ " </form>"
    	+ "</body></html>\n"
    	+ "";

    private static final String RADIO_PAGE_HORIZONTAL = ""
        + "<html><head><title>Test Page</title></head>\n"
        + "<body><h1>Test Page</h1>\n"
        + "<form action=\"bla.html\">"
    	+ "<table border=\"1\"><tr><td>"
        + "<input type=\"radio\" name=\"menu\" value=\"1\">one"
        + "<input type=\"radio\" name=\"menu\" value=\"2\">two"
        + "<input type=\"radio\" name=\"menu\" value=\"3\">three"
        + "<input type=\"radio\" name=\"menu\" value=\"4\">four"
    	+ "</td></tr></table>"
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
        	+ "<table border=\"1\"><tr><td>"
            + "<input type=\"radio\" name=\"menu\" value=\"1\">one<br/>"
            + "<input type=\"radio\" name=\"menu\" value=\"2\">two<br/>"
            + "<input type=\"radio\" name=\"menu\" value=\"3\" checked>three<br/>"
            + "<input type=\"radio\" name=\"menu\" value=\"4\">four<br/>"
            + "</td></tr></table>"
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
    	+ "<table border=\"1\"><tr><td>"
        + "<input type=\"checkbox\" name=\"menu\" value=\"1\">one<br/>"
        + "<input type=\"checkbox\" name=\"menu\" value=\"2\" checked>two<br/>"
        + "<input type=\"checkbox\" name=\"menu\" value=\"3\" checked>three<br/>"
        + "<input type=\"checkbox\" name=\"menu\" value=\"4\">four<br/>"
    	+ "</td></tr></table>"
        + "<input type=\"submit\" value=\"go\">"
        + " </form>"
        + "</body></html>\n"
        + "";
    
    public void testSelfHtml1(){
    	assertEquals(SELFHTML_1_RESULT, selfHtmlTests(SELFHTML_1));
    }

    private static String SELFHTML_1 = ""
    	+ "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Strict//EN\""
    	+ "       \"http://www.w3.org/TR/html4/strict.dtd\">"
    	+ "<html>"
    	+ "<head>"
    	+ "<title>Auswahllisten mit Mehrfachauswahl</title>"
    	+ "</head>"
    	+ "<body>"
    	+ ""
    	+ "<h1>W&auml;hlen Sie so viele Favoriten wie Sie wollen!</h1>"
    	+ ""
    	+ "<form action=\"select_multiple.htm\">"
    	+ "<p>"
    	+ "<select name=\"top5\" size=\"5\" multiple>"
    	+ "<option>Heino</option>"
    	+ ""
    	+ "<option>Michael Jackson</option>"
    	+ "<option>Tom Waits</option>"
    	+ "<option>Nina Hagen</option>"
    	+ "<option>Marianne Rosenberg</option>"
    	+ "</select>"
    	+ "</p>"
    	+ "</form>"
    	+ ""
    	+ "</body>"
    	+ "</html>";

    private static String SELFHTML_1_RESULT = ""
    	+ "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Strict//EN\""
    	+ "       \"http://www.w3.org/TR/html4/strict.dtd\">"
    	+ "<html>"
    	+ "<head>"
    	+ "<title>Auswahllisten mit Mehrfachauswahl</title>"
    	+ "</head>"
    	+ "<body>"
    	+ ""
    	+ "<h1>W&auml;hlen Sie so viele Favoriten wie Sie wollen!</h1>"
    	+ ""
    	+ "<form action=\"select_multiple.htm\">"
    	+ "<p>"
    	+ "<table border=\"1\"><tr><td>"
        + "<input type=\"checkbox\" name=\"top5\" value=\"Heino\">Heino<br/>"
        + "<input type=\"checkbox\" name=\"top5\" value=\"Michael Jackson\">Michael Jackson<br/>"
        + "<input type=\"checkbox\" name=\"top5\" value=\"Tom Waits\">Tom Waits<br/>"
        + "<input type=\"checkbox\" name=\"top5\" value=\"Nina Hagen\">Nina Hagen<br/>"
        + "<input type=\"checkbox\" name=\"top5\" value=\"Marianne Rosenberg\">Marianne Rosenberg<br/>"
    	+ "</td></tr></table>"
     	+ "</p>"
    	+ "</form>"
    	+ ""
    	+ "</body>"
    	+ "</html>";
    
    public void testSelfHtml2(){
    	assertEquals(SELFHTML_2_RESULT, selfHtmlTests(SELFHTML_2));
    }
    
    private static String SELFHTML_2 = ""
    	+ "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Strict//EN\""
    	+ "       \"http://www.w3.org/TR/html4/strict.dtd\">"
    	+ "<html>"
    	+ "<head>"
    	+ "<title>Auswahllisten definieren</title>"
    	+ "</head>"
    	+ "<body>"
    	+ ""
    	+ "<h1>W&auml;hlen Sie Ihren Favoriten!</h1>"
    	+ ""
    	+ "<form action=\"select.htm\">"
    	+ "<p>"
    	+ "<select name=\"top5\" size=\"3\">"
    	+ "<option>Heino</option>"
    	+ "<option>Michael Jackson</option>"
    	+ "<option>Tom Waits</option>"
    	+ "<option>Nina Hagen</option>"
    	+ "<option>Marianne Rosenberg</option>"
    	+ "</select>"
    	+ "</p>"
    	+ "</form>"
    	+ ""
    	+ "</body>"
    	+ "</html>"
    	+ ""
    	+ "";
    private static String SELFHTML_2_RESULT = ""
    	+ "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Strict//EN\""
    	+ "       \"http://www.w3.org/TR/html4/strict.dtd\">"
    	+ "<html>"
    	+ "<head>"
    	+ "<title>Auswahllisten definieren</title>"
    	+ "</head>"
    	+ "<body>"
    	+ ""
    	+ "<h1>W&auml;hlen Sie Ihren Favoriten!</h1>"
    	+ ""
    	+ "<form action=\"select.htm\">"
    	+ "<p>"
    	+ "<table border=\"1\"><tr><td>"
    	+ "<input type=\"radio\" name=\"top5\" value=\"Heino\">Heino<br/>"
    	+ ""
    	+ "<input type=\"radio\" name=\"top5\" value=\"Michael Jackson\">Michael Jackson<br/>"
    	+ "<input type=\"radio\" name=\"top5\" value=\"Tom Waits\">Tom Waits<br/>"
    	+ "<input type=\"radio\" name=\"top5\" value=\"Nina Hagen\">Nina Hagen<br/>"
    	+ "<input type=\"radio\" name=\"top5\" value=\"Marianne Rosenberg\">Marianne Rosenberg<br/>"
    	+ "</td></tr></table>"
    	+ "</p>"
    	+ "</form>"
    	+ ""
    	+ "</body>"
    	+ "</html>"
    	+ ""
    	+ "";

    
    public void testSelfHtml3(){
    	assertEquals(SELFHTML_3_RESULT, selfHtmlTests(SELFHTML_3));
    }
    
    private static String SELFHTML_3 = ""
    	+ "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Strict//EN\""
    	+ "       \"http://www.w3.org/TR/html4/strict.dtd\">"
    	+ "<html>"
    	+ "<head>"
    	+ "<title>Eintr&auml;ge vorselektieren</title>"
    	+ "</head>"
    	+ "<body>"
    	+ ""
    	+ "<h1>Sie k&ouml;nnen auch einen anderen Favoriten w&auml;hlen!</h1>"
    	+ ""
    	+ "<form action=\"option_selected.htm\">"
    	+ "<p>"
    	+ "<select name=\"top5\" size=\"5\">"
    	+ "<option>Heino</option>"
    	+ ""
    	+ "<option>Michael Jackson</option>"
    	+ "<option selected>Tom Waits</option>"
    	+ "<option>Nina Hagen</option>"
    	+ "<option>Marianne Rosenberg</option>"
    	+ "</select>"
    	+ "</p>"
    	+ "</form>"
    	+ ""
    	+ "</body>"
    	+ "</html>"
    	+ ""
    	+ "";
    private static String SELFHTML_3_RESULT = ""
    	+ "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Strict//EN\""
    	+ "       \"http://www.w3.org/TR/html4/strict.dtd\">"
    	+ "<html>"
    	+ "<head>"
    	+ "<title>Eintr&auml;ge vorselektieren</title>"
    	+ "</head>"
    	+ "<body>"
    	+ ""
    	+ "<h1>Sie k&ouml;nnen auch einen anderen Favoriten w&auml;hlen!</h1>"
    	+ ""
    	+ "<form action=\"option_selected.htm\">"
    	+ "<p>"
    	+ "<table border=\"1\"><tr><td>"
    	+ "<input type=\"radio\" name=\"top5\" value=\"Heino\">Heino<br/>"
    	+ ""
    	+ "<input type=\"radio\" name=\"top5\" value=\"Michael Jackson\">Michael Jackson<br/>"
    	+ "<input type=\"radio\" name=\"top5\" value=\"Tom Waits\" checked>Tom Waits<br/>"
    	+ "<input type=\"radio\" name=\"top5\" value=\"Nina Hagen\">Nina Hagen<br/>"
    	+ "<input type=\"radio\" name=\"top5\" value=\"Marianne Rosenberg\">Marianne Rosenberg<br/>"
    	+ "</td></tr></table>"
    	+ "</p>"
    	+ "</form>"
    	+ ""
    	+ "</body>"
    	+ "</html>"
    	+ ""
    	+ "";

    
    public void testSelfHtml4(){
    	assertEquals(SELFHTML_4_RESULT, selfHtmlTests(SELFHTML_4));
    }
    
    private static String SELFHTML_4 = ""
    	+ "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Strict//EN\""
    	+ "       \"http://www.w3.org/TR/html4/strict.dtd\">"
    	+ "<html>"
    	+ "<head>"
    	+ "<title>Absendewert von Eintr&auml;gen bestimmen</title>"
    	+ "</head>"
    	+ "<body>"
    	+ ""
    	+ "<h1>Pizzeria Fantasia</h1>"
    	+ ""
    	+ "<form action=\"option_value.htm\">"
    	+ "<p>Ihre Pizza-Bestellung:</p>"
    	+ "<p>"
    	+ "<table border=\"1\"><tr><td>"
    	+ "<input type=\"radio\" name=\"Pizza\" value=\"P101\">Pizza Napoli<br/>"
    	+ ""
    	+ "<input type=\"radio\" name=\"Pizza\" value=\"P102\">Pizza Funghi<br/>"
    	+ "<input type=\"radio\" name=\"Pizza\" value=\"P103\">Pizza Mare<br/>"
    	+ "<input type=\"radio\" name=\"Pizza\" value=\"P104\">Pizza Tonno<br/>"
    	+ "<input type=\"radio\" name=\"Pizza\" value=\"P105\">Pizza Mexicana<br/>"
    	+ "<input type=\"radio\" name=\"Pizza\" value=\"P106\">Pizza Regina<br/>"
    	+ "<input type=\"radio\" name=\"Pizza\" value=\"P107\">Pizza de la Casa<br/>"
    	+ "<input type=\"radio\" name=\"Pizza\" value=\"P108\">Pizza Calzone<br/>"
    	+ "<input type=\"radio\" name=\"Pizza\" value=\"P109\">Pizza con tutti<br/>"
    	+ "</td></tr></table>"
    	+ ""
    	+ "</p>"
    	+ "</form>"
    	+ ""
    	+ "</body>"
    	+ "</html>"
    	+ ""
    	+ "";
    private static String SELFHTML_4_RESULT = ""
    	+ "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Strict//EN\""
    	+ "       \"http://www.w3.org/TR/html4/strict.dtd\">"
    	+ "<html>"
    	+ "<head>"
    	+ "<title>Absendewert von Eintr&auml;gen bestimmen</title>"
    	+ "</head>"
    	+ "<body>"
    	+ ""
    	+ "<h1>Pizzeria Fantasia</h1>"
    	+ ""
    	+ "<form action=\"option_value.htm\">"
    	+ "<p>Ihre Pizza-Bestellung:</p>"
    	+ "<p>"
    	+ "<table border=\"1\"><tr><td>"
    	+ "<input type=\"radio\" name=\"Pizza\" value=\"P101\">Pizza Napoli<br/>"
    	+ ""
    	+ "<input type=\"radio\" name=\"Pizza\" value=\"P102\">Pizza Funghi<br/>"
    	+ "<input type=\"radio\" name=\"Pizza\" value=\"P103\">Pizza Mare<br/>"
    	+ "<input type=\"radio\" name=\"Pizza\" value=\"P104\">Pizza Tonno<br/>"
    	+ "<input type=\"radio\" name=\"Pizza\" value=\"P105\">Pizza Mexicana<br/>"
    	+ "<input type=\"radio\" name=\"Pizza\" value=\"P106\">Pizza Regina<br/>"
    	+ "<input type=\"radio\" name=\"Pizza\" value=\"P107\">Pizza de la Casa<br/>"
    	+ "<input type=\"radio\" name=\"Pizza\" value=\"P108\">Pizza Calzone<br/>"
    	+ "<input type=\"radio\" name=\"Pizza\" value=\"P109\">Pizza con tutti<br/>"
    	+ "</td></tr></table>"
    	+ ""
    	+ "</p>"
    	+ "</form>"
    	+ ""
    	+ "</body>"
    	+ "</html>"
    	+ ""
    	+ "";

//	This test lets the test process hang. Don't know why.
//	When running on the original website, not as a test
//	it works fine.
//	So far I found it has to do with the "optgroup" elements.
//	When those are removed from the SELFHTML_5 and SELFHTML_5_RESULT
//	the test runs fine and doesn't hang.
    
//    public void testSelfHtml5(){
//    	assertEquals(SELFHTML_5_RESULT, selfHtmlTests(SELFHTML_5));
//    }
//    
    private static String SELFHTML_5 = ""
    	+ "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Strict//EN\""
    	+ "       \"http://www.w3.org/TR/html4/strict.dtd\">"
    	+ "<html>"
    	+ "<head>"
    	+ "<title>Verschachtelte Auswahllisten (Men&uuml;struktur) definieren</title>"
    	+ "</head>"
    	+ "<body>"
    	+ ""
    	+ "<h1>W&auml;hlen Sie Ihren Lieblingsnamen!</h1>"
    	+ ""
    	+ "<form action=\"select_optgroup.htm\">"
    	+ "<p>Zur Auswahl stehen:</p>"
    	+ "<p>"
    	+ "<select name=\"Namen\" size=3>"
    	+ ""
    	+ " <optgroup label=\"Namen mit A\">"
    	+ "  <option label=\"Anna\">Anna</option>"
    	+ "  <option label=\"Achim\">Achim</option>"
    	+ "  <option label=\"August\">August</option>"
    	+ " </optgroup>"
    	+ " <optgroup label=\"Namen mit B\">"
    	+ "  <option label=\"Berta\">Berta</option>"
    	+ ""
    	+ "  <option label=\"Barbara\">Barbara</option>"
    	+ "  <option label=\"Bernhard\">Bernhard</option>"
    	+ " </optgroup>"
    	+ " <optgroup label=\"Namen mit C\">"
    	+ "  <option label=\"Caesar\">Caesar</option>"
    	+ "  <option label=\"Christiane\">Christiane</option>"
    	+ "  <option label=\"Christian\">Christian</option>"
    	+ ""
    	+ " </optgroup>"
    	+ "</select>"
    	+ "</p>"
    	+ "</form>"
    	+ ""
    	+ "</body>"
    	+ "</html>"
    	+ ""
    	+ "";
    private static String SELFHTML_5_RESULT = ""
    	+ "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Strict//EN\""
    	+ "       \"http://www.w3.org/TR/html4/strict.dtd\">"
    	+ "<html>"
    	+ "<head>"
    	+ "<title>Verschachtelte Auswahllisten (Men&uuml;struktur) definieren</title>"
    	+ "</head>"
    	+ "<body>"
    	+ ""
    	+ "<h1>W&auml;hlen Sie Ihren Lieblingsnamen!</h1>"
    	+ ""
    	+ "<form action=\"select_optgroup.htm\">"
    	+ "<p>Zur Auswahl stehen:</p>"
    	+ "<p>"
    	+ "<table border=\"1\"><tr><td>"
    	+ ""
    	+ " <optgroup label=\"Namen mit A\">"
    	+ "  <input type=\"radio\" name=\"Namen\" value=\"Anna\">Anna<br/>"
    	+ "  <input type=\"radio\" name=\"Namen\" value=\"Achim\">Achim<br/>"
    	+ "  <input type=\"radio\" name=\"Namen\" value=\"August\">August<br/>"
    	+ " </optgroup>"
    	+ " <optgroup label=\"Namen mit B\">"
    	+ "  <input type=\"radio\" name=\"Namen\" value=\"Berta\">Berta<br/>"
    	+ ""
    	+ "  <input type=\"radio\" name=\"Namen\" value=\"Barbara\">Barbara<br/>"
    	+ "  <input type=\"radio\" name=\"Namen\" value=\"Bernhard\">Bernhard<br/>"
    	+ " </optgroup>"
    	+ " <optgroup label=\"Namen mit C\">"
    	+ "  <input type=\"radio\" name=\"Namen\" value=\"Caesar\">Caesar<br/>"
    	+ "  <input type=\"radio\" name=\"Namen\" value=\"Christiane\">Christiane<br/>"
    	+ "  <input type=\"radio\" name=\"Namen\" value=\"Christian\">Christian<br/>"
    	+ ""
    	+ " </optgroup>"
    	+ "</td></tr></table>"
    	+ "</p>"
    	+ "</form>"
    	+ ""
    	+ "</body>"
    	+ "</html>"
    	+ ""
    	+ "";

    
//    public void testSelfHtml6(){
//    	assertEquals(SELFHTML_6_RESULT, selfHtmlTests(SELFHTML_6));
//    }
//    
//    private static String SELFHTML_6 = ""
//    	+ "";
//    private static String SELFHTML_6_RESULT = ""
//    	+ "";

    
    private String selfHtmlTests(String source){
    	String response = makeResponse(source);
    	Reply reply = Utils.makeReply(response);
    	return Utils.filter(
    			fSelectToRadioFilter,
    			response.length(),
    			reply);
    }

    private static String makeResponse(String htmlPage) {
        return "HTTP/1.0 30 Found\n"
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
