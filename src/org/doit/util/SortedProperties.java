/* $Id: SortedProperties.java,v 1.7 2006/06/18 23:25:52 forger77 Exp $ */

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
package org.doit.util;

import java.util.Enumeration;
import java.util.Properties;
import sdsu.compare.StringIgnoreCaseComparer;
import sdsu.util.SortedList;

public class SortedProperties extends Properties
{
    /**
	 * Serializable should define this:
	 */
	private static final long serialVersionUID = 1L;

    public Enumeration keys()
    {
	Enumeration e = super.keys();
	SortedList sorter = new SortedList(StringIgnoreCaseComparer.getInstance());
	while (e.hasMoreElements())
	{
	    sorter.addElement((String) e.nextElement());
	}
	return sorter.elements();
    }
}
