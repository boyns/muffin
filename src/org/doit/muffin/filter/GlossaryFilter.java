/* $Id: GlossaryFilter.java,v 1.4 1999/05/29 17:34:24 boyns Exp $ */

package org.doit.muffin.filter;

import org.doit.muffin.*;
import org.doit.io.*;
import java.util.Enumeration;
import java.io.*;
import UK.co.demon.asmodeus.util.*;

public class GlossaryFilter implements ContentFilter
{
    Prefs prefs;
    Glossary factory;
    InputObjectStream in = null;
    OutputObjectStream out = null;

    public GlossaryFilter(Glossary factory)
    {
	this.factory = factory;
    }

    public void setPrefs(Prefs prefs)
    {
	this.prefs = prefs;
    }
    
    public boolean needsFiltration(Request request, Reply reply)
    {
	String s = reply.getContentType();
	return s != null && s.startsWith("text/html");
    }

    public void setInputObjectStream(InputObjectStream in)
    {
	this.in = in;
    }

    public void setOutputObjectStream(OutputObjectStream out)
    {
	this.out = out;
    }

    public void run()
    {
	Thread.currentThread().setName("Glossary");
	
	ObjectStreamToInputStream htmlInput = new ObjectStreamToInputStream(in);
	ObjectStreamToOutputStream htmlOutput = new ObjectStreamToOutputStream(out);
	
	ByteArrayOutputStream htmlbuf = new ByteArrayOutputStream();
	try
	{
	    byte buf[] = new byte[1024];
	    int n;
	    while ((n = htmlInput.read(buf, 0, buf.length)) > 0)
	    {
		htmlbuf.write(buf, 0, n);
	    }
	    htmlbuf.close();
	}
	catch (Exception e)
	{
	}

	ByteArrayInputStream html = new ByteArrayInputStream(htmlbuf.toByteArray());
	MultiSearchReader root;
	int start;
	BufferedReader cookedSource=new BufferedReader(new InputStreamReader(html));
	ByteArrayOutputStream buffer=new ByteArrayOutputStream();
	Writer sink=new OutputStreamWriter(buffer);
	Enumeration terms=factory.keys();
	root=new MultiSearchReader(terms,false,sink);

	try 
	{
	    MultiSearchResult match=new MultiSearchResult(-1," ");
	    start=0;
	    while (match!=null)
	    {
		match=root.search(match.getOffset()+match.getMatch().length(),cookedSource);
		if (root.getTagStatus()==MultiSearchReader.INSIDE_TAG && match!=null)
		{
		    sink.write(match.getMatch());
		    continue;
		}
		if (match!=null)
		{
		    sink.write("<a href=\"");
		    sink.write(factory.lookup(match.getMatch()));
		    sink.write("\">");
		    sink.write(match.getMatch());
		    sink.write("</a>");
		}
	    }
	    sink.close();
	    buffer.writeTo(htmlOutput);
	    htmlOutput.flush();
	    htmlOutput.close();
	} 
	catch (IOException ioe)
	{
	    ioe.printStackTrace();
	}
	finally
	{
	    try
	    {
		out.flush();
		out.close();
	    }
	    catch (IOException ioe)
	    {
	    }
	}
    }
}


