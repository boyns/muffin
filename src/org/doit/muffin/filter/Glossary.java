/* $Id: Glossary.java,v 1.8 2003/05/30 16:21:37 forger77 Exp $ */

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
import org.doit.html.*;

import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

public class Glossary extends AbstractFilterFactory {

	public Glossary() {
		fGlossaryHash = new Hashtable(33);
	}
	
	/**
	 * 	 * @see org.doit.muffin.filter.AbstractFilterFactory#doSetDefaultPrefs()	 */
	protected void doSetDefaultPrefs(){
		putPrefsString(GLOSSARY_FILE_KEY, "glossary");
	}

	/**
	 * 	 * @see org.doit.muffin.filter.AbstractFilterFactory#doMakeFilter()	 */
	public Filter doMakeFilter() {
		return new GlossaryFilter(this);
	}

	/**
	 * 	 * @see org.doit.muffin.filter.AbstractFilterFactory#doMakeFrame()	 */
	public AbstractFrame doMakeFrame() {
		return new GlossaryFrame(this);
	}

	/**
	 * 	 * @see org.doit.muffin.filter.AbstractFilterFactory#getName()	 */
	public String getName() {
		return "Glossary";
	}

	/**
	 * 	 * @see org.doit.muffin.filter.AbstractFilterFactory#doLoad()	 */
	protected void doLoad() {
		try {
			UserFile file =
				getPrefs().getUserFile(getPrefsString(GLOSSARY_FILE_KEY));
			BufferedReader in =
				new BufferedReader(
					new InputStreamReader(file.getInputStream()));
			String s;
			while ((s = in.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(s, " \t");
				String term = st.nextToken();
				String url = st.nextToken();
				fGlossaryHash.put(term.toLowerCase(), url);
			}
			in.close();
		} catch (FileNotFoundException e) {
			//FIXME: is it on purpose that we don't complain ?
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the Enumeration for the keys in the contained Hashtable.	 * @return Enumeration for the keys in the contained Hashtable	 */
	public Enumeration keys() {
		return fGlossaryHash.keys();
	}

	/**
	 * The main functionality of this class: looks up a value for the given term.
	 * The term is lowercased first.	 * @param term The term to look up a value for.	 * @return String The "translated" term.	 */
	String lookup(String term) {
		return (String) fGlossaryHash.get(term.toLowerCase());
	}

	static final String GLOSSARY_FILE_KEY = "glossaryfile";
	private Hashtable fGlossaryHash = null;
}
