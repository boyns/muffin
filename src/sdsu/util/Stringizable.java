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


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * An interface for objects that can convert itself to a string and "recreate" itself from 
 * that string. This interface is much more limited than Serializable. The few advantages 
 * Stringizable has over Serializable are: Stringizable works with Java 1.0.2, 
 * the string version of an object is readable and parsable by humans, and the 
 * string version of an object is ligher weight than in Serializable. The main use
 * of Stringizable is for use in Java 1.0.2, motivate for students
 * the utility of Serializable with less overhead, and for use in client-server
 * programing where the strings must satisfy a protocol. 
 * 
 *
 * @see		Stringizer
 * @see		SimpleTokenizer
 * @see		TokenCharacters
 * @version 1.0 6 June 1997 
 * @author Roger Whitney (<a href=mailto:whitney@cs.sdsu.edu>whitney@cs.sdsu.edu</a>)
 */

public interface Stringizable
	{
	
	/**
	 * Converts a string to an object. String must be created from object of same
	 * Class you are trying to recreate.
	 * @exception ConversionException If there is a problem converting to object.
	 */
	public void fromString( String objectString  ) throws ConversionException;
			
	/**
	 * Converts the object to a string
	 */
	public String toString( );

	/**
	 * Converts the object to a string with given header information.
	 */
	public String toString( String header );
	
	/**
	 * Set character used to separate elements in String representation of object
	 */
	public void setSeparatorChar( char separatorChar );

	/**
	 * Sets TokenCharacters used to convert object from/to strings/streams.
	 * Current values for separatorChar override separator 
	 * settings in TokenCharacters object.
	 */
	public void setTokenCharacters( TokenCharacters newParseTable );

	}