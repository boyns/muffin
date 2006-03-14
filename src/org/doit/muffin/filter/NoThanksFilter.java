/* $Id: NoThanksFilter.java,v 1.12 2006/03/14 17:00:03 flefloch Exp $ */

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
import java.util.Enumeration;
import java.io.*;

public class NoThanksFilter extends AbstractContentFilter implements RequestFilter, ReplyFilter
{
    private Request request = null;
    private Reply fReply = null;

    public NoThanksFilter(NoThanks factory)
    {
	super(factory);
    fFactory = factory;
    }
    
    public void filter(Request request) throws FilterException
    {
	this.request = request;

	String url = request.getURL();

	/* Check for redirect */
	String location = fFactory.redirect(url);
	if (location != null)
	{
	    fFactory.report(request, "redirect to " + location);
	    request.setURL(location);
	    return;
	}

	/* Check for killed URL */
	if (fFactory.isKilled(url))
	{
	    fFactory.report(request, "rejected " + url);
	    throw new FilterException("NoThanks URL " + url + " rejected");
	}

	fFactory.processHeaders(request, request);
    }

    public void filter(Reply reply) throws FilterException
    {
    this.fReply = reply;

    String content = reply.getContentType();
    if (content != null && fFactory.killContent(content))
    {
        fFactory.report(request, "rejected " + content);
        throw new FilterException("NoThanks content-type " + content + " rejected");
    }

    fFactory.processHeaders(request, reply);
    }
    
    /**     * @see org.doit.muffin.filter.AbstractContentFilter#doNeedsFiltration(String)     */
    protected boolean doNeedsFiltration(String contentType){
        return contentType != null
            && (contentType.startsWith("text/html") || contentType.startsWith("application/x-javascript"));
    }
    
    /**
     * @see org.doit.muffin.filter.AbstractContentFilter#doRun(ObjectStreamToInputStream, ObjectStreamToOutputStream)
     */
    protected void doRun(
        ObjectStreamToInputStream ostis,
        ObjectStreamToOutputStream ostos)
        throws IOException
    {
	    if ("application/x-javascript".equals(fReply.getContentType()))
	    {
		filterJavascript();
	    }
	    else
	    {
		filterHtml();
	    }
	}
    
    private void filterJavascript()
	throws IOException
    {
	Object obj;
	Token script = new Token(Token.TT_SCRIPT);

	// read the javascript code into one token
	// so it can be filtered 
	while ((obj = getInputObjectStream().read()) != null)
	{
	    script.append(((ByteArray)obj));
	}

	script = fFactory.processScript(request, script);
	if (script != null)
	{
	    getOutputObjectStream().write(script);
	}
    }

    private void filterHtml()
	throws IOException
    {
	String endTag = null;
	boolean killingComment = false;

	Object obj;
	while ((obj = getInputObjectStream().read()) != null)
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
		killingComment = fFactory.killComment(s);
		if (killingComment)
		{
		    fFactory.report(request, "removed comment " + s);
		}
		// AJP modification: Ignore everything up to endTag, if set
		else if (endTag == null)
		{
		    getOutputObjectStream().write(token);
		}
		break;

	    case Token.TT_SCRIPT:
		token = fFactory.processScript(request, token);
		if (token != null)
		{
		    getOutputObjectStream().write(token);
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
		    
		if (fFactory.stripTag(tag.name()))
		{
		    fFactory.report(request, "stripped " + tag.name());
		    endTag = fFactory.stripUntil(tag.name());
		    output = false;
		}
		if (output && fFactory.replaceTag(tag.name()))
		{
		    Tag newTag = fFactory.replaceTagWith(tag.name());
		    fFactory.report(request, "replaced " + tag.name()
				   + " with " + newTag.name());
		    tag = newTag;
		}
		if (output && fFactory.checkTagAttributes(tag))
		{
		    if (fFactory.processTagAttributes(request, tag))
		    {
			output = false;
			if (fFactory.hasEnd(tag.name()))
			{
			    endTag = "/" + tag.name();
			}
		    }
		}
		if (output && fFactory.checkTag(tag.name()) && tag.attributeCount() > 0)
		{
		    Enumeration e = tag.enumerate();
		    while (e.hasMoreElements())
		    {
			String attr = (String) e.nextElement();
			if (fFactory.checkAttr(attr))
			{
			    String link = tag.get(attr);
			    if (link != null && fFactory.isKilled(link))
			    {
				/* Can't kill tags like body and head.  Instead
				   remove the offending attribute. */
				if (fFactory.isRequired(tag.name()))
				{
				    fFactory.report(request, "removed "
						   + attr + "=" + link);
				    tag.remove(attr);
				}
				/* Kill the tag completely. */
				else
				{
				    fFactory.report(request, "killed "
						   + tag.name()
						   + " with "
						   + attr + "=" + link);
				    output = false;
				    if (fFactory.hasEnd(tag.name()))
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
		    getOutputObjectStream().write(token);
		}
		break;
		    
	    default:
		if (endTag == null)
		{
		    getOutputObjectStream().write(token);
		}
		break;
	    }
	}
    }
    
    private NoThanks fFactory;
}

