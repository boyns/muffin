/* 
 * Copyright (C) 1997 Roger Whitney <whitney@cs.sdsu.edu>
 *
 * This file is part of the San Diego State University Java Library.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package sdsu.util;

/**
 * Signals that an exception has occurred in trying to convert a String 
 * to/from an object. Is subclass of IOException for historical reason only.
 *
 * @version 1.0 6 June 1997 
 * @author Roger Whitney (<a href=mailto:whitney@cs.sdsu.edu>whitney@cs.sdsu.edu</a>)
 */

public class ConversionException extends java.io.IOException 
	{
	/**
	 * Constructs an ConversionException with no detail message.
	 */
	public ConversionException() 
		{
		super();
		}

	/**
	 * Constructs an ConversionException with the specified detail message.
	 * A detail message is a String that describes this particular exception.
	 * @param s the detail message
	 */
	public ConversionException(String s) 
		{
		super(s);
		}
	}
