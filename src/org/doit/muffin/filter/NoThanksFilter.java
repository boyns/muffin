/* $Id: NoThanksFilter.java,v 1.7 2000/01/25 06:14:52 boyns Exp $ */

/*
 * Copyright (C) 1996-2000 Mark R. Boyns <boyns@doit.org>
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
package org.doit.muffin.filter;

import org.doit.muffin.*;
import org.doit.io.*;
import org.doit.html.*;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.*;

public class NoThanksFilter implements ContentFilter, RequestFilter, ReplyFilter
{
    NoThanks factory;
    Prefs prefs;
    InputObjectStream in = null;
    OutputObjectStream out = null;
    Request request = null;

    public NoThanksFilter(NoThanks factory)
    {
	this.factory = factory;
    }
    
    public void setPrefs(Prefs prefs)
    {
	this.prefs = prefs;
    }

    public void filter(Request request) throws FilterException
    {
	String url = request.getURL();

	/* Check for redirect */
	String location = factory.redirect(url);
	if (location != null)
	{
	    factory.report(request, "redirect to " + location);
	    request.setURL(location);
	    return;
	}

	/* Check for killed URL */
	if (factory.isKilled(url))
	{
	    factory.report(request, "rejected " + url);
	    throw new FilterException("NoThanks URL " + url + " rejected");
	}

	factory.processHeaders(request, request);
    }

    public void filter(Reply reply) throws FilterException
    {
	String content = reply.getContentType();
	if (content != null && factory.killContent(content))
	{
	    factory.report(request, "rejected " + content);
	    throw new FilterException("NoThanks content-type " + content + " rejected");
	}

	factory.processHeaders(request, reply);
    }
    
    public boolean needsFiltration(Request request, Reply reply)
    {
	this.request = request;
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
	Thread.currentThread().setName("NoThanks");

	try
	{
	    String endTag = null;
	    boolean killingComment = false;

	    Object obj;
            while ((obj = in.read()) != null)
            {
		Token token = (Token) obj;
		if (killingComment && token.getType() != Token.TT_COMMENT)
		{
		    continue;
		}

		switch (token.getType())
		{
		case Token.TT_COMMENT:
		    String s = token.toString();
		    killingComment = factory.killComment(s);
		    if (killingComment)
		    {
			factory.report(request, "removed comment " + s);
		    }
		    // AJP modification: Ignore everything up to endTag, if set
		    else if (endTag == null)
		    {
			out.write(token);
		    }
		    break;

		case Token.TT_SCRIPT:
		    token = factory.processScript(request, token);
		    if (token != null)
		    {
			out.write(token);
		    }
		    break;
		    
		case Token.TT_TAG:
		    boolean output = true;
		    Tag tag = token.createTag();

		    // AJP modification: Ignore everything up to endTag, if set
		    if (endTag != null)
		    {
			if (tag.is(endTag))
			{
			    endTag = null;
			}
			continue;
		    }
		    
		    if (factory.stripTag(tag.name()))
		    {
			factory.report(request, "stripped " + tag.name());
			endTag = factory.stripUntil(tag.name());
			output = false;
		    }
		    if (output && factory.replaceTag(tag.name()))
		    {
			Tag newTag = factory.replaceTagWith(tag.name());
			factory.report(request, "replaced " + tag.name()
				       + " with " + newTag.name());
			tag = newTag;
		    }
		    if (output && factory.checkTagAttributes(tag))
		    {
			if (factory.processTagAttributes(request, tag))
			{
			    output = false;
			    if (factory.hasEnd(tag.name()))
			    {
				endTag = "/" + tag.name();
			    }
			}
		    }
		    if (output && factory.checkTag(tag.name()) && tag.attributeCount() > 0)
		    {
			Enumeration e = tag.enumerate();
			while (e.hasMoreElements())
			{
			    String attr = (String) e.nextElement();
			    if (factory.checkAttr(attr))
			    {
				String link = tag.get(attr);
				if (link != null && factory.isKilled(link))
				{
				    /* Can't kill tags like body and head.  Instead
				       remove the offending attribute. */
				    if (factory.isRequired(tag.name()))
				    {
					factory.report(request, "removed "
						       + attr + "=" + link);
					tag.remove(attr);
				    }
				    /* Kill the tag completely. */
				    else
				    {
					factory.report(request, "killed "
						       + tag.name()
						       + " with "
						       + attr + "=" + link);
					output = false;
					if (factory.hasEnd(tag.name()))
					{
					    endTag = "/" + tag.name();
					}
					break; /* tag is killed */
				    }
				}
			    }
			}
		    }
		    if (output)
		    {
			token.importTag(tag);
			out.write(token);
		    }
		    break;
		    
		default:
		    if (endTag == null)
		    {
			out.write(token);
		    }
		    break;
		}
	    }
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

