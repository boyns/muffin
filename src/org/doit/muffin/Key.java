/* $Id: Key.java,v 1.5 2000/01/24 04:02:14 boyns Exp $ */

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
package org.doit.muffin;

/**
 * A wrapper around java.lang.String to have case-insensitive
 * hashCode and equals methods.
 *
 * @see java.lang.String
 * @author Mark Boyns
 */
class Key
{
    private String name = null;

    /**
     * Create a Key.
     */
    Key(String name)
    {
	this.name = name;
    }

    /**
     * Return a lowercase hashCode.
     */
    public int hashCode()
    {
	String s = name.toLowerCase();
	return s.hashCode();
    }

    /**
     * Return a lowercase equals.
     */
    public boolean equals(Object obj)
    {
	return name.equalsIgnoreCase(obj.toString());
    }

    /** 
     * Return the key.
     */
    public String toString()
    {
	return name;
    }
}
