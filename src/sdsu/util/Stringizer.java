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

import java.util.Date;

/**
 * This class aids in creating parsable strings. This is useful in producing a ascii 
 * representations of objects. Strings produced via Stringizer can be parsed by SimpleTokenizer.
 * A string contains tokens, comments, and separators. 
 * Comments start with the comment character and continue to the next newline (\n ) character.
 * A token can be any object convertable to a 
 * string via toString(). If string representation of the token contains the 
 * separator, whitespace, or comment character it automatically quoted.
 * Quote characters and escape characters in the  string representation are automatically 
 * escaped (preceeded by the escape character.
 * Whitespace characters are used only for quoting tokens.
 * The separator given with the token is added to string only when the next token is appended. 
 *
 * @see		SimpleTokenizer
 * @version 1.0 2 February 1997  
 * @author Roger Whitney (<a href=mailto:whitney@cs.sdsu.edu>whitney@cs.sdsu.edu</a>)
 */

public class Stringizer 
	{

	protected TokenCharacters parser; 	
	protected String header = null;
	protected String previousSeparator = null;
	protected StringBuffer body;
	
	
	/**
	 * Create a Stringizer 
	 */
	public Stringizer( TokenCharacters parser )
		{
		this( 10, parser );
		}
	

	/**
	 * Create a Stringizer with initial room for size tokens
	 * @param	commentChar	character used to indicate start of a comment
	 * @param	beginQuoteChar	character used to start a quote of a string containing special characters
	 * @param	endQuoteChar	character used to end a quote of a string containing special characters
	 * @param	quotableChars	characters that need to be quoted. Use null or empty string 
	 *         for none
	 */
	public Stringizer( int size, TokenCharacters parser)
		{
		// Each token requires two enteries plus a header
		body = new StringBuffer( 2 * size + 2);
		this.parser = parser;
		}
		
	/**
	 * Message is used as a comment at the begining of the string.
	 * Comment character is added to message.
	 */
	 public void setHeader( String message )
	 	{
	 	this.header = message;
	 	}
	 
	/**
	 * Appends message as a comment at current location in string.
	 * Comment character and newline added to message so message can be parsed.
	 */
	 public void appendComment( String message )
	 	{
	 	body.append( parser.getCommentChar() + message + "\n" );
	 	}
	 
	/**
	 * Appends token to the end of the string. Token object is converted to a 
	 * string via toString(). If string representation contains the separator, quotableChars,
	 * comment character it automatically quoted.
	 * Separator is appended to string only
	 * if this is not the last token to be added to the string. Separator is to used 
	 * to parse string back into tokens.
	 */
	 public void appendToken( Object token, char separator )
	 	{
	 	appendToken( token, String.valueOf( separator ) );
	 	}

	/**
	 * Appends token to the end of the string. Token object is converted to a 
	 * string via toString(). If string representation contains the separator, quotableChars,
	 * comment character it automatically quoted.
	 * Separator is appended to string only
	 * if this is not the last token to be added to the string. Separator is to used 
	 * to parse string back into tokens.
	 * Whitespace can be added to separator to make string more readable.
	 */
	 public void appendToken( Object token, String separator )
	 	{
	 	if ( previousSeparator != null )
	 		body.append( previousSeparator );
	 	
	 	previousSeparator =  separator;
	 	
	 	String tokenString = token.toString();
	 	
	 	if ( parser.containsEscapeableChar( tokenString)  )
	 		{
	 		tokenString = parser.escapeToken( tokenString );
	 		tokenString = parser.quoteToken( tokenString );
	 		}
	 	else if ( parser.containsTokenTerminator( tokenString)  )
	 		tokenString = parser.quoteToken( tokenString );
	 	body.append( tokenString );
	 	}
	 	
	/**
	 * Returns the string built up via the Stringizer.
	 * If present, a header is added.
	 */
	public String toString()
		{
		char commentChar = parser.getCommentChar();
		
		if ( ( header != null ) && ( header.length() > 0 ) )
			{
			String fullHeader = (char) commentChar + header + "\n"  + 
								(char) commentChar + new Date() + "\n";
			return fullHeader + body.toString();
			}
		else
			return body.toString();
		}
		
	
	} // class Stringizer