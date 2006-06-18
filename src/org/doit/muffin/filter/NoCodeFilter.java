/* Based upon DecafFilter by Mark R. Boyns so here is his copyright notice: */

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

/* Modifications by Neil Hodgson <neilh@hare.net.au> 5/December/1998
 * The modifications are also licensed under the terms of the GNU General
 * Public License as described above.
 */

package org.doit.muffin.filter;

import org.doit.muffin.*;
import org.doit.io.*;
import org.doit.html.*;
import java.util.Enumeration;
import java.io.IOException;
import org.doit.muffin.regexp.Pattern;
import org.doit.muffin.regexp.Factory;

/**
 * FIXME: This class duplicates code from Decaf. The two should be joined or
 * derived from a common Baseclass.
 *
 */
public class NoCodeFilter extends AbstractContentFilter implements ReplyFilter
{

    NoCodeFilter(NoCode factory)
    {
    super(factory);
    }
    
    /**     * @see org.doit.muffin.ReplyFilter#filter(Reply)     */
    public void filter(Reply reply) throws FilterException
    {
	if (getFactory().getPrefsBoolean(NoCode.NOJAVASCRIPT))
	{
	    String content = reply.getContentType();
	    if (content != null && content.equalsIgnoreCase("application/x-javascript"))
	    {
		getFactory().report(getRequest(), "rejecting " + content);
		throw new FilterException(getFactory().getName() + " " + content + " rejected");
	    }
	}
    }

    /**
     * @see org.doit.muffin.filter.AbstractContentFilter#doGetContentIdentifier()
     */
    protected String doGetContentIdentifier()
    {
        return "text/html";
    }
    
    /**
     * Utility function duplicating the functionality of java.lang.String#startsWith(String)
     * but ignoring case.
     *      * @param target The string to check whether it starts with matchString     * @param matchString The String to check whether target starts with it.     * @return boolean True if target starts with matchString.     */
    private static boolean startsWithIgnoreCase(String target, String matchString)
    {
	//String startBit = target.substring(0, matchString.length());
	// fix from Joerg Schneider
	String startBit = target.substring(0,Math.min(target.length(),matchString.length()));
	return startBit.equalsIgnoreCase(matchString);
    }
    
    /**
     * Normalizes Language aliases.
     * 
     * @param slang The language to be normalized     */    private static String normalizeLanguage(String slang) {
	// Use startsWith rather than eqauls because there can be
	// version numbers at end of language name like JavaScript1.2.

	// .Encode languages must come first so as not to return non-.Encode name
	if (startsWithIgnoreCase(slang,"JScript.Encode"))	return "JavaScript.Encode";
	if (startsWithIgnoreCase(slang,"EcmaScript.Encode"))	return "JavaScript.Encode";
	if (startsWithIgnoreCase(slang,"JavaScript.Encode"))	return "JavaScript.Encode";
	if (startsWithIgnoreCase(slang,"LiveScript.Encode"))	return "JavaScript.Encode";
	if (startsWithIgnoreCase(slang,"VBScript.Encode"))	return "VBScript.Encode";
	if (startsWithIgnoreCase(slang,"VBS.Encode"))		return "VBScript.Encode";

	if (startsWithIgnoreCase(slang,"JScript"))		return "JavaScript";
	if (startsWithIgnoreCase(slang,"EcmaScript"))		return "JavaScript";
	if (startsWithIgnoreCase(slang,"JavaScript"))		return "JavaScript";
	if (startsWithIgnoreCase(slang,"LiveScript"))		return "JavaScript";
	if (startsWithIgnoreCase(slang,"VBScript"))		return "VBScript";
	if (startsWithIgnoreCase(slang,"VBS"))			return "VBScript";
	return slang;
    }

    public boolean IsBadLanguage(String slang) {

	final boolean noJavaScript = getFactory().getPrefsBoolean(NoCode.NOJAVASCRIPT);
	final boolean noVBScript = getFactory().getPrefsBoolean(NoCode.NOVBSCRIPT);
	final boolean noEncodedScript = getFactory().getPrefsBoolean(NoCode.NOENCODEDSCRIPT);
	final boolean noOtherScript = getFactory().getPrefsBoolean(NoCode.NOOTHERSCRIPT);

	if (slang.equals("JavaScript")) {
	    if (noJavaScript)
		return true;
	} else if (slang.equals("VBScript")) {
	    if (noVBScript)
		return true;
	} else if (slang.equals("JavaScript.Encode")) {
	    if (noJavaScript || noEncodedScript)
		return true;
	} else if (slang.equals("VBScript.Encode")) {
	    if (noVBScript || noEncodedScript)
		return true;
	} else {    // Language is not a known language - could be Python, Perl, ...
	    if (noOtherScript)
		return true;
	}
	return false;
    }

    /**
     * @see org.doit.muffin.filter.AbstractContentFilter#doRun(ObjectStreamToInputStream, ObjectStreamToOutputStream)
     */
    protected void doRun(
        ObjectStreamToInputStream ostis,
        ObjectStreamToOutputStream ostos)
        throws IOException
    {
	    boolean eatingJavaScript = false;
	    boolean inScript = false;
	    boolean inVBScript = false;
	    boolean eatingJava = false;
	    final boolean noJavaScript = getFactory().getPrefsBoolean(NoCode.NOJAVASCRIPT);
	    final boolean noVBScript = getFactory().getPrefsBoolean(NoCode.NOVBSCRIPT);
	    final boolean noOtherScript = getFactory().getPrefsBoolean(NoCode.NOOTHERSCRIPT);
	    final boolean noEncodedScript = getFactory().getPrefsBoolean(NoCode.NOENCODEDSCRIPT);
	    final boolean removeSomeLanguage = 
		noJavaScript || noVBScript || noOtherScript || noEncodedScript;
	    final boolean noEvalInScript = getFactory().getPrefsBoolean(NoCode.NOEVALINSCRIPT);
	    final boolean noJava = getFactory().getPrefsBoolean(NoCode.NOJAVA);

	    Tag tag;
	    Object obj;
	    while ((obj = getInputObjectStream().read()) != null)
        {
		org.doit.html.Token token = (org.doit.html.Token) obj;
		if (token.getType() == org.doit.html.Token.TT_TAG)
		{
		    tag = token.createTag();

		    if (inScript && tag.is("/script"))
		    {
				inScript = false;
				inVBScript = false;
		    }
		    if (eatingJavaScript && tag.is("/script"))
		    {
				eatingJavaScript = false;
				continue;
		    }
		    if (eatingJava && tag.is("/applet"))
		    {
				eatingJava = false;
				continue;
		    }
		    
		    if (tag.is("script"))
		    {
			    // TODO check language mimetype as well as language attribute
			    // For now, just reject any script without a language attribute 
			    // if any languages rejected.
			    inScript = true;
			    if (tag.has("language")) {
				    String baseLang = normalizeLanguage(tag.get("language"));
				    if (IsBadLanguage(baseLang)) {
					    eatingJavaScript = true;
					    getFactory().report(getRequest(),
							   "Removed <script bad language>");
				    }
				    if (baseLang.equals("VBScript") || baseLang.equals("VBScript.Encode")) {
					inVBScript = true;
				    }
			    } else if (removeSomeLanguage) {
				    eatingJavaScript = true;
				    getFactory().report(getRequest(),
						   "Removed <script without language>");
			    }
		    }
		    else if (NoCode.isJavaScriptTag(tag.name()) && tag.attributeCount() > 0)
		    {
		      // Should be less restrictive here, allowing actions written in permitted 
		      // languages but how can it be worked out which language an action is in?
		      if (removeSomeLanguage)
			  {
			    StringBuffer str = new StringBuffer();
			    String value;

			    Enumeration e = tag.enumerate();
			    while (e.hasMoreElements())
			    {
				String attr = (String) e.nextElement();
				if (NoCode.isJavaScriptAttr(attr))
				{
				    value = tag.remove(attr);
				    if (value != null)
				    {
					str.append("<");
					str.append(tag.name());
					str.append("> ");
					str.append(attr);
					str.append("=\"");
					str.append(value);
					str.append("\" ");
				    }
				}
			    }

			    if (tag.has("href")) 
			    {
				if (
				    (startsWithIgnoreCase(tag.get("href"), "javascript:") && noJavaScript) ||
				    (startsWithIgnoreCase(tag.get("href"), "vbscript:") && noVBScript)
				   ) 
				{
				    value = tag.remove("href");
				    str.append("<");
				    str.append(tag.name());
				    str.append("> ");
				    str.append("href=\"");
				    str.append(value);
				    str.append("\" ");
				}
			    }

			    if (tag.has("language")
				&& IsBadLanguage(tag.get("language")))
			    {
				value = tag.remove("language");
				str.append("<");
				str.append(tag.name());
				str.append("> ");
				str.append("language=\"");
				str.append(value);
				str.append("\" ");
			    }

			    if (str.length() > 0)
			    {
 				getFactory().report(getRequest(), str.toString());
			    }
			}
		    }
		    if (noJava)
		    {
			if (tag.is("applet"))
			{
			    eatingJava = true;
			    getFactory().report(getRequest(), "Removed <applet>");
			}
		    }
		    if (!eatingJavaScript && !eatingJava)
		    {
			token.importTag(tag);
			getOutputObjectStream().write(token);
		    }
		} else if (!eatingJavaScript && !eatingJava) {
			if (inScript && noEvalInScript) {
				// Change any code that allows dynamically generating code into something
				// that does not generate code thus denying the use of self modifying 
				// code to obscure the intent of code.
				// Since displaying the string avoids executing it and also helps 
				// us work out what the code is doing, this is an OK substitution.
				String substFunction = inVBScript ? "MsgBox" : "alert";
				String outScript = token.toString();
				// eval is available both in javascript and VBScript 5.0
				Pattern expression = Factory.instance().getPattern("eval", true);
				outScript =
					expression.substituteAll(outScript, substFunction);
				// executeglobal is available in VBScript 5.0
				expression = Factory.instance().getPattern("executeglobal", true);
				outScript =
					expression.substituteAll(outScript, substFunction);
				// execute is available in VBScript 5.0
				expression = Factory.instance().getPattern("execute", true);
				outScript =
					expression.substituteAll(outScript, substFunction);
				token.erase();
				token.append(outScript);
			}
			getOutputObjectStream().write(token);
		}
	}
	}
}

