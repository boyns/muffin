/* $Id: AnimationKiller.java,v 1.6 2003/05/30 16:21:37 forger77 Exp $ */

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

public class AnimationKiller extends AbstractFilterFactory {

	/**
	 * @see org.doit.muffin.filter.AbstractFilterFactory#doSetDefaultPrefs()	 */
	protected void doSetDefaultPrefs() {
		putPrefsInteger("maxLoops", 1);
		putPrefsBoolean("break", false);
	}

	/**
	 * @see org.doit.muffin.filter.AbstractFilterFactory#doMakeFilter()	 */
	protected Filter doMakeFilter() {
		return new AnimationKillerFilter(this);
	}
	
	/**
	 * @see org.doit.muffin.filter.AbstractFilterFactory#doMakeFrame()	 */
	protected AbstractFrame doMakeFrame() {
		return new AnimationKillerFrame(this);
	}

	/**
	 * @see org.doit.muffin.filter.AbstractFilterFactory#getName()	 */
	public String getName(){
		return "AnimationKiller";
	}
}
