/* $Id: ProxyCacheBypassFilter.java,v 1.1 2003/05/25 02:51:50 cmallwitz Exp $ */

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

import org.doit.muffin.filter.Glossary;

/**
 * @author Bernhard Wagner <bw@xmlizer.biz>
 * 
 * TestCase testing the Glossary.
 *
 */
public class GlossaryTest extends TestCase {

	/**
	 * Constructor for GlossaryTest.
	 * @param arg0
	 */
	public GlossaryTest(String arg0) {
		super(arg0);
//		System.out.println("-----");
//		System.out.println(IMPLS);
//		System.out.println("-----");
	}
	
	
	public void setUp() {
		fGlossary = new Glossary();
		fGlossary.loadFromStream(makeReader());
		fGlossaryFilter = (ContentFilter)fGlossary.createFilter();
	}
	
	public void testGlossaryFilterPresence() throws IOException {
		assertNotNull(fGlossaryFilter);
		
		Reply reply = makeReply();
		assertNotNull(reply);
		
		assertTrue(fGlossaryFilter.needsFiltration(null, reply));
	}
	
	public void testReplacing(){
		Reply reply = makeReply();
		OutputStream os = new ByteArrayOutputStream();
		Utils.filter(fGlossaryFilter, reply.getContent(), os, 200, reply);
		String result = os.toString();
		
		Pattern pat = org.doit.muffin.regexp.Factory.instance().getPattern("java");
		String expected = pat.substituteAll(SAMPLE_PAGE, "<a href=\"http://java.sun.com/\">java</a>");
		pat = org.doit.muffin.regexp.Factory.instance().getPattern("muffin");
		expected = pat.substituteAll(expected, "<a href=\"http://muffin.doit.org/\">muffin</a>");
		
		assertEquals(expected, result);
	}
	
	/** 
	 * Constructs a Reply object containing a simple Web-Response.	 * @return Reply The constructed Reply object.	 */
	private Reply makeReply() {
		Reply reply = null;
		try {
			reply = new Reply(makeInputStream());
			reply.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return reply;
	}
	
	private InputStream makeInputStream(){
		return makeInputStream(SAMPLE_RESPONSE);
	}
	
	private InputStream makeInputStream(String str) {
		byte[] bytes = str.getBytes();
		return new ByteArrayInputStream(bytes);
	}
	
	private BufferedReader makeReader(){
		return new BufferedReader(
			new InputStreamReader(
				makeInputStream(SAMPLE_GLOSSARY)
			)
		);
	}

	private static final String SAMPLE_PAGE = ""
		+ "<head><title>Test Page</title></head>\n"
		+ "<body><h1>Test Page</h1>\n"
		+ "muffin is a funky java project.\n"
		+ "</body>\n"
		+ "";

	private static final String SAMPLE_RESPONSE = "HTTP/1.0 302 Found\n"
		+ "Content-Type: text/html\n"
		+ "Location: http://chewie.somewhere.com:8080/index.html\n"
		+ "Content-Length: 176\n"
		+ "Servlet-Engine: Tomcat Web Server/3.2 beta 3 (JSP 1.1; Servlet 2.2; Java 1.2.2; Linux 2.2.24-7.0.3smp i386; java.vendor=Blackdown Java-Linux Team)\n"
		+ "\n"
		+ SAMPLE_PAGE
		+ "";
			
	private static final String SAMPLE_GLOSSARY = ""
		+ "muffin http://muffin.doit.org/\n"
		+ "java http://java.sun.com/\n"
		+ "";
	
	private Glossary fGlossary;
	private ContentFilter fGlossaryFilter;
	private Reply fReply;

}
