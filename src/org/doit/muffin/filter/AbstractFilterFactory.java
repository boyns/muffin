/* $Id:$ */

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
package org.doit.muffin.filter;

import java.awt.*;

import org.doit.muffin.*;
import org.doit.util.Strings;

public abstract class AbstractFilterFactory implements FilterFactory {
	
	public void setManager(FilterManager manager) {
		this.fManager = manager;
	}

	/**
	 * Accessor for Prefs	 * @see org.doit.muffin.FilterFactory#getPrefs()	 */
	public final Prefs getPrefs() {
		return fPrefs;
	}
	
	/**
	 * Sets the Prefs of this FilterFactory.
	 * It makes shure that after setting the Prefs at least the default prefs
	 * are still there in case the given Prefs do not contain all necessary entries.
	 * 
	 * Note to subclass implementors: You define what the necessary defaults 
	 * are by overriding the method
	 * @see org.doit.muffin.AbstractFilterFactor#doSetDefaultPrefs().
	 * 
	 * @param prefs The Prefs to set.
	 * 	 * @see org.doit.muffin.FilterFactory#setPrefs(Prefs)	 */
	public final void setPrefs(Prefs prefs){
		fPrefs = prefs;
		boolean o = fPrefs.getOverride();	// save previous override settings
		fPrefs.setOverride(false);			// do not override existing prefs.
		doSetDefaultPrefs();
		fPrefs.setOverride(o);				// reactivate previous override settings
		doLoad();
		fMessages = new MessageArea();
	}
	
	/**
	 * Sets a boolean value for a given key in Prefs.
	 * The key will be prepended by the name of the concrete FilterFactory.	 * @param key The key, prepended by the name of the concrete FilterFactory, under which
	 * the boolean value is to be stored in Prefs.	 * @param value The boolean value to set.	 */
    final void putPrefsBoolean(String key, boolean value){
    	fPrefs.putBoolean(makeNameSpace(key), value);
    }
    
	/**
	 * Gets a boolean value for a given key in Prefs.
	 * The key will be prepended by the name of the concrete FilterFactory.
	 * @param key The key, prepended by the name of the concrete FilterFactory, for which
	 * to retrieve the boolean value from Prefs.
	 * @return The boolean value retrieved.
	 */
    final boolean getPrefsBoolean(String key){
    	return fPrefs.getBoolean(makeNameSpace(key));
    }
    
	/**
	 * Sets a integer value for a given key in Prefs.
	 * The key will be prepended by the name of the concrete FilterFactory.
	 * @param key The key, prepended by the name of the concrete FilterFactory, under which
	 * the integer value is to be stored in Prefs.
	 * @param value The integer value to set.
	 */
    final void putPrefsInteger(String key, int value){
    	fPrefs.putInteger(makeNameSpace(key), value);
    }
    
	/**
	 * Gets an int value for a given key in Prefs.
	 * The key will be prepended by the name of the concrete FilterFactory.
	 * @param key The key, prepended by the name of the concrete FilterFactory, for which
	 * to retrieve the int value from Prefs.
	 * @return The int value retrieved.
	 */
    final int getPrefsInteger(String key){
    	return fPrefs.getInteger(makeNameSpace(key));
    }
    
	/**
	 * Sets a String value for a given key in Prefs.
	 * The key will be prepended by the name of the concrete FilterFactory.
	 * @param key The key, prepended by the name of the concrete FilterFactory, under which
	 * the String value is to be stored in Prefs.
	 * @param value The String value to set.
	 */
    final void putPrefsString(String key, String value){
    	fPrefs.putString(makeNameSpace(key), value);
    }
    
	/**
	 * Gets a String value for a given key in Prefs.
	 * The key will be prepended by the name of the concrete FilterFactory.
	 * @param key The key, prepended by the name of the concrete FilterFactory, for which
	 * to retrieve the String value from Prefs.
	 * @return The String value retrieved.
	 */
    final String getPrefsString(String key){
    	return fPrefs.getString(makeNameSpace(key));
    }
    
	/**
	 * Gets a UserFile for a given key in Prefs.
	 * The key will be prepended by the name of the concrete FilterFactory.
	 * @param key The key, prepended by the name of the concrete FilterFactory, for which
	 * to retrieve the UserFile from Prefs.
	 * @return The UserFile retrieved.
	 */
    final UserFile getPrefsUserFile(String key){
		UserFile uf = fPrefs.getUserFile(makeNameSpace(key));
    	return fPrefs.getUserFile(makeNameSpace(key));
    }
    
	/**
	 * Gets a String value for a given key in Strings.
	 * The key will be prepended by the name of the concrete FilterFactory.
	 * @param key The key, prepended by the name of the concrete FilterFactory, for which
	 * to retrieve the String value from Strings.
	 * @return The String value retrieved.
	 */
    final String getString(String key){
    	return Strings.getString(makeNameSpace(key));
    }
    
    /**
     * Utility method to prepend the given key with the name of the concrete FilterFactory.     * @param key The string to be prepended with the name of the concrete FilterFactory.     * @return String The given String prependend with the name of the concrete FilterFactory.     */
    final String makeNameSpace(String key){
    	StringBuffer sb = new StringBuffer();
    	sb.append(getName());
    	sb.append(".");
    	sb.append(key);
    	return sb.toString();
    }
    
	/**
	 * Accessor for the MessageArea associated with this FilterFactory.	 * @return MessageArea	 */
	final MessageArea getMessages(){
		return fMessages;
	}

	/**
	 * Saves the prefs of this FilterFactory.	 */
	public final void save() {
		fManager.save(this);
	}
	
	/**
	 * 	 * @see org.doit.muffin.FilterFactory#shutdown()	 */
	public final void shutdown() {
		if (fFrame != null) {
			fFrame.dispose();
		}
	}

	/**
	 * 	 * @see org.doit.muffin.FilterFactory#viewPrefs()	 */
	public final void viewPrefs() {
		if (fFrame == null) {
			fFrame = makeFrame();
		}
		fFrame.setVisible(true);
	}

	/**
	 * Factory method to construct the frame and consolidate it.	 * @return AbstractFrame The constructed AbstractFrame.	 */
	private AbstractFrame makeFrame(){
		AbstractFrame frame = doMakeFrame();
		frame.consolidate();
		return frame;
	}
	
	/**
	 * 	 * @see org.doit.muffin.FilterFactory#createFilter()	 */
	public final Filter createFilter() {
		return doMakeFilter();
	}

	/**
	 * Reports a message to the given Requests logger and to the MessageArea.	 * @param request The Request to whose logger to report the given message.	 * @param message The message to report.	 */
	protected void report(Request request, String message) {
		if(request!=null) {
			request.addLogEntry(getName(), message);
		}
		report(message);
	}

	/**
	 * Reports a message to the MessageArea.	 * @param message	 */
	protected void report(String message) {
		if(fMessages != null) {
			fMessages.append(message + "\n");
		}
	}

	/**
	 * Voluntary hook method for concrete FilterFactories for defining the 
	 * default values for its preferences. An example usage can be found in 
	 * @see org.doit.muffin.filter.GlossaryFilter.	 */
	protected void doSetDefaultPrefs(){
		// voluntary
	}

	/** 
	 * Voluntary hook method for concrete FilterFactories. Will be called after 
	 * calling setPrefs.
	 * 	 */
	protected void doLoad(){
		// voluntary
	}

	/** 
	 * Mandatory hook method for concrete FilterFactories to create the specific Frame-Type
	 * associated with this FilterFactory.	 * @return AbstractFrame The created Frame.	 */
	protected abstract AbstractFrame doMakeFrame();
	
	/**
	 * Mandatory hook method for concrete FilterFactories to return their name.	 * @return String The name of this FilterFactory.	 */
	public abstract String getName();
	
	/**
	 * Mandatory hook method for concrete FilterFactories to create the specific Filter-Type
	 * associated with this FilterFactory.	 * @return Filter	 */
	protected abstract Filter doMakeFilter();

	private AbstractFrame fFrame;
    private FilterManager fManager;
    private Prefs fPrefs;
    private MessageArea fMessages;
}
