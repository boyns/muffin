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

import java.io.StringBufferInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PushbackInputStream;

/**
 * This class performs some simple parsing of strings or streams. The input is a sequence of 
 * ascii characters. The sequence is divided into tokens, whitespace, and comments.
 * Comments start with the comment character and continue to the next newline (\n ) character.
 * Comments are removed from the input characters and not returned as part of a token.
 * A token is string from the current location to the next separator or whitespace character.
 * Characters defined as whitespace (tab, newline, and space default values ) help delineate tokens
 * but are not part of tokens. That is whitespace characters are removed after finding a token.
 * If a token must contain whitespace character, a possible separator, or comment character,  
 * the token must be placed between two quote characters.  A quoted token can 
 * contain a quote character.
 *
 * @see		Stringizer
 * @see		TokenCharacters
 * @version 1.0 2 February 1997 
 * @author Roger Whitney (<a href=mailto:whitney@cs.sdsu.edu>whitney@cs.sdsu.edu</a>)
 */

public class SimpleTokenizer 
	{
	/*
	 * Class invariants: 
	 *		a call to nextToken always consumes at least one character
	 *		tokenizer is always left at the next non-whitespace or non-comment
	 */

	private TokenCharacters charTable;
	private char lastFoundSeparator;

	/**
	 * Inputstream that is the internal source for tokens
	 */
	private PushbackInputStream input;
	
	private static final int EOF = -1;

	private boolean eatEscapeChar = true;
	    
	/**
	 * Create a SimpleTokenizer on string with default settings
	 */
	public SimpleTokenizer( String parsable )
		{
		this( new StringBufferInputStream( parsable ) ) ;
		}

	/**
	 * Create a SimpleTokenizer on string
	 * @param	commentChar	character used to indicate start of a comment
	 * @param	quoteChar	character used to quote a string containing special characters
	 * @param	whitespace	characters used for whitespace. Use null or empty string for no whitespace characters
	 */
	public SimpleTokenizer( String parsable, TokenCharacters charTable )
		{
		this( new StringBufferInputStream( parsable ), charTable );
		}

	/**
	 * Create a SimpleTokenizer on tokenSource with default settings
	 */
	public SimpleTokenizer( InputStream tokenSource )
		{
		this( tokenSource, new TokenCharacters()  );
		}


	/**
	 * Create a SimpleTokenizer on tokenSource 
	 * @param	commentChar	character used to indicate start of a comment
	 * @param	beginQuoteChar	character used to start a quote of a string containing special characters
	 * @param	endQuoteChar	character used to end a quote of a string containing special characters
	 * @param	whitespace	characters used for whitespace. Use null or empty string 
	 *         for no whitespace characters
	 */
	public SimpleTokenizer( InputStream tokenSource, TokenCharacters charTable )
		{
		input = new PushbackInputStream( tokenSource );
		this.charTable = charTable;
		
		try
			{
			removeCommentAndWhitespace();
			}
		catch (IOException readError ) 
			//abstraction does not require IO here, hide IO to allow changes
			{}							
		}

	
	/**
	 * Returns true if not at end of source stream or source string
	 */
	 public boolean hasMoreTokens()
	 	{
	 	// Since whitespace has been removed, EOF is equivalent to no more tokens
	 	try
			{
		 	int c = input.read();
		 	if ( c == EOF )
		 		return false;
		 	else 
		 		{
		 		input.unread( c );
		 		return true;
		 		}
		 	}
		catch (IOException readError )
			{
			return false;
			}

	 	}

	/**
	 * Returns true if not at end of source stream or source string
	 */
	 public boolean hasMoreElements()
	 	{
	 	return hasMoreTokens();
	 	}

	/**
	 * Returns the separator found by the last call to nextToken
	 */
	public char separator()
		{
		return lastFoundSeparator;
		}

	/**
	 * Enable/Disable the eating of the escape character.
	 */
	public void setEatEscapeChar (boolean value)
		{
		eatEscapeChar = value;
		}
	    
	/**
	 * Returns string containing all characters up to the given separator, unquoted
	 * whitespace, or EOF
	 * if the separator is not found. The separator is removed from the stream, but not returned
	 * as part of token.
	 * @param separator can be any character except the current comment or quote character
	 * @exception	IOException	If separator or EOF does not follow this token 
	 */
	public String nextToken( String  newSeparators ) throws IOException
		{
		charTable.setSeparatorChars( newSeparators );
		return nextToken( );
		}
	 	
	/**
	 * Returns string containing all characters up to the given separator, unquoted
	 * whitespace, or EOF
	 * if the separator is not found. The separator is removed from the stream, but not returned
	 * as part of token.
	 * @param separator set of characters to be used as separator after token. Can be any non-null or 
	 *  nonempty string of characters
	 *  except the current comment or quote character 
	 * @exception	IOException	If separator or EOF does not follow this token 
	 */
	public String nextToken( ) throws IOException
	 	{
	 	String token;
	 	int c = input.read();
	 	
	 	//System.out.println( (char) c );
	 	if ( charTable.isBeginQuote( (char) c ) )
	 		token = getQuotedChunk( (char) c );
	 	else
	 		{
	 		input.unread( c );
	 		token = getNextChunk();
	 		}
		//System.out.println( "token " + token );
		if ( !isSeparatorNext() )
			throw new IOException( "Missing separator ");
		
		removeCommentAndWhitespace();	
		return token;
	 	}

	/**
	 * Returns next token. String terminateChars indicates end of token
	 */
	private String getNextChunk( ) throws IOException
		{
		
		int c;
		StringBuffer chunk = new StringBuffer();
		
		while ( ( c = input.read()) != EOF )
			{
			//System.out.println( "chunk " + (char) c );
			
			if ( charTable.isTokenTerminator( (char) c ) )
				{
				//System.out.println( "T Termin " + (char) c );
				break;
				}
			chunk.append( (char) c );
			}
		
		if ( c != EOF )
			input.unread( c );
		
		return chunk.toString();
		}

	/**
	 * Returns quoted token. Assumes leading comment char has been removed from stream
	 */
	private String getQuotedChunk( char beginQuoteChar ) throws IOException
		{
		int c;
		StringBuffer chunk = new StringBuffer();
		
		while ( ( c = input.read()) != EOF )
			{
			if ( charTable.isQuotePair( beginQuoteChar, (char) c ) )
				break;
				
			if ( charTable.isEscape( (char) c ) )
			{
			    if (! eatEscapeChar)
				chunk.append ((char) c); // don't eat escape character
			    c = input.read();
			}
						       
			chunk.append( (char) c );
			}
		
		return chunk.toString();
		}

	/**
	 * Remove comments and whitespace
	 */
	private void removeCommentAndWhitespace() throws IOException
		{
		int c;
		while ( (c = input.read()) != EOF )
			{
			if ( charTable.isComment( (char) c  ) )
				removeComment();
			else if ( ! charTable.isWhitespace( (char) c  )  )
				{
				input.unread( c );
				return;
				}
			}
		}

	/**
	 * If next non-whitespace or non-comment char is separator, then eat separator
	 * and return true;
	 * Otherwise unread char and return false
	 */
	private boolean isSeparatorNext( ) throws IOException
		{
		int c;
		lastFoundSeparator = ' '; // XXX
		while ( (c = input.read()) != EOF )
			{
			if ( charTable.isSeparator( (char) c ) )
				{
				lastFoundSeparator = (char) c;
				return true;
				}
			else if ( charTable.isComment( (char) c ) )
				{
				removeComment();
				}
			else if ( ! charTable.isWhitespace( (char) c ) )
				{
				input.unread( c );
				//return false; XXX
				return true;
				}
			}
		return true; // EOF is always legal seperator
		}
		
	/**
	 * Remove rest of comment, not called unless found leading comment
	 */
	private void removeComment() throws IOException
		{
		int c;
		
		do
			if (( c = input.read()) == EOF ) return;
		while (  ! charTable.isEOL( (char)c ) );
		input.unread( c ); // allow EOL to be both separator and end of commment 
		}
		
	/**
	 * Returns true if string text contains the char pattern
	 */
	private static final boolean containsChar( String text, char pattern )
		{
		int NOT_FOUND = -1;
		if ( text.indexOf( pattern ) == NOT_FOUND)
			return false;
		else
			return true;
		}
		
	}// end class SimpleTokenizer
