/* $Id: GlossaryFilter.java,v 1.5 2003/06/01 01:01:09 forger77 Exp $ */

package org.doit.muffin.filter;

import org.doit.muffin.*;
import org.doit.io.*;
import java.util.Enumeration;
import java.io.*;
import UK.co.demon.asmodeus.util.*;

public class GlossaryFilter extends AbstractContentFilter {

	public GlossaryFilter(Glossary factory) {
		super(factory);
		fGlossary = (Glossary)factory;
	}

	/**
	 * @see org.doit.muffin.filter.AbstractContentFilter#doGetContentIdentifier()
	 */
	protected String doGetContentIdentifier(){
		return "text/html";	
	}

	/**
	 * @see org.doit.muffin.filter.AbstractContentFilter#doRun(ObjectStreamToInputStream, ObjectStreamToOutputStream)
	 */
	protected void doRun(ObjectStreamToInputStream ostis, ObjectStreamToOutputStream ostos)
		throws IOException {

		ByteArrayOutputStream htmlbuf = new ByteArrayOutputStream();
		try {
			byte buf[] = new byte[1024];
			int n;
			while ((n = ostis.read(buf, 0, buf.length)) > 0) {
				htmlbuf.write(buf, 0, n);
			}
			htmlbuf.close();
		} catch (Exception e) {
			//FIXME: swallowing it on purpose?
		}

		ByteArrayInputStream html =
			new ByteArrayInputStream(htmlbuf.toByteArray());
		MultiSearchReader root;
		int start;
		BufferedReader cookedSource =
			new BufferedReader(new InputStreamReader(html));
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		Writer sink = new OutputStreamWriter(buffer);
		Enumeration terms = fGlossary.keys();
		root = new MultiSearchReader(terms, false, sink);

		MultiSearchResult match = new MultiSearchResult(-1, " ");
		start = 0;
		while (match != null) {
			match =
				root.search(
					match.getOffset() + match.getMatch().length(),
					cookedSource);
			if (root.getTagStatus() == MultiSearchReader.INSIDE_TAG
				&& match != null) {
				sink.write(match.getMatch());
				continue;
			}
			if (match != null) {
				sink.write("<a href=\"");
				sink.write(fGlossary.lookup(match.getMatch()));
				sink.write("\">");
				sink.write(match.getMatch());
				sink.write("</a>");
			}
		}
		sink.close();
		buffer.writeTo(ostos);
	}
	
	private Glossary fGlossary;
}
