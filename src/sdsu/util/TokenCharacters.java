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
 * This class maintains special characters used in parsing strings into tokens. It keeps 
 * track of whitespace, characters to indicate the start of a comment, quote characters 
 * used to quote tokens that contain special characters, an escape character, and characters
 * that separate tokens.
 *
 * @see		SimpleTokenizer
 * @see		Stringizer
 * @version 0.95 2 February 1997 
 * @author Roger Whitney (<a href=mailto:whitney@cs.sdsu.edu>whitney@cs.sdsu.edu</a>)
 */

public class TokenCharacters
	{
	// Implementation notes
	// Should escapeToken & quoteToken be moved to Stringizer?
	// Should tests isX be renamed isXChar?
	
	/**
	 * Contains EOL characters for Mac, Unix , and PC platforms. That is ascii 10 and 13
	 */
	private static final String EOL = "\n\015"; //Mac EOL = 13, Unix EOL = \n, PC uses both

	/**
	 * Default characters treated as whitespace. Default is space, tab, line feed 
	 * (ascii 10, \n in unix)
	 * and carriage return ( ascii 13, usd as newline char on PC and Macs )
	 */
	public static final String WHITESPACE = " \t" + EOL;

	/**
	 * Character (/) used to preceed a quote character or escape character in a quoted token.
	 */
	public static final char ESCAPE_CHAR = '\\';

	/**
	 * Default character (#) used to indicate start of comment.
	 */
	public static final String COMMENT_CHAR = "#";

	/**
	 * Default character (') used to delineate the start and end of a quoted token.
	 * Tokens are quoted when they contain special characters.
	 */
	public static final char QUOTE_CHAR = '\'';

	/**
	 * Characters treated as whitespace
	 */
	private String whitespace;

	/**
	 * Any one of the characters in commentChars start  a comment. 
	 * Comments end with EOL
	 */
	private String commentChars;

	/**
	 *  Any one of the characters in beginQuoteChars start a quoted token. 
	 */
	private String beginQuoteChars;

	/**
	 * Characters indicating end of a quoted token. Begin and ending quotes come
	 * in pairs
	 */
	private String endQuoteChars;

	/**
	 * Any one of the characters in separators will separate two tokens 
	 */
	private String separators;


	private static final int NOT_FOUND = -1;

	/**
	 * Create TokenCharacters with default values. You must set separators before using
	 * the new object.
	 */
	public TokenCharacters( )
		{
		this( "" );
		}

	/**
	 * Create TokenCharacters with given characters for token separators and default values
	 * for the rest of parameters.
	 */
	public TokenCharacters( String separators )
		{
		this( separators, COMMENT_CHAR, QUOTE_CHAR, QUOTE_CHAR, WHITESPACE  );
		}
	
	/**
	 * Create a TokenCharacters object with given values 
	 * @param	commentChar	character used to indicate start of a comment
	 * @param	beginQuoteChar	character used to start a quote of a string containing special characters
	 * @param	endQuoteChar	character used to end a quote of a string containing special characters
	 * @param	whitespace	characters used for whitespace. Use null or empty string 
	 *         for no whitespace characters
	 */
	public TokenCharacters(  String separators,
							String commentChars, 
							char beginQuoteChar,
							char endQuoteChar, 
							String whitespace 
						  )
		{
		this.separators =  nullFilter( separators );
		this.commentChars =  nullFilter( commentChars );
		this.whitespace = nullFilter( whitespace);
		this.beginQuoteChars =  String.valueOf( beginQuoteChar );
		this.endQuoteChars =  String.valueOf( endQuoteChar );
		}


	/**
	 * Add the quote pair beginQuote-endQuote to the pairs recognized as char pairs
	 * to quote a token.
	 */
	public void addQuoteChars( char beginQuote, char endQuote )
		{
		beginQuoteChars = beginQuoteChars + beginQuote;
		endQuoteChars = endQuoteChars + endQuote;
		}

	/**
	 * Returns a character that indicates start of a comment.
	 */
	public char getCommentChar()
		{
		return commentChars.charAt( 0 );
		}
		
	/**
	 * Set the current set of separators to newSeparators
	 */
	public void setSeparatorChars( String newSeparators )
		{
		this.separators =  nullFilter( newSeparators );
		}
		
	/**
	 * Returns true if c is Mac, Unix, or PC EOL character
	 */
	public boolean isEOL( char c )
		{
		return containsChar( EOL, c  );
		}

	/**
	 * Returns true if c is an escape character
	 */
	public boolean isEscape( char c )
		{
		return c == ESCAPE_CHAR;
		}

	/**
	 * Returns true if c is a whitespace character
	 */
	public boolean isWhitespace( char c )
		{
		return containsChar( whitespace, c  );
		}

	/**
	 * Returns true if c is a separator character
	 */
	public boolean isSeparator( char c )
		{
		return containsChar( separators, c  );
		}

	/**
	 * Returns true if c indicates the start of a quoted token
	 */
	public boolean isBeginQuote( char c )
		{
		return containsChar( beginQuoteChars, c  );
		}

	/**
	 * Returns true if c indicates the end of a quoted token
	 */
	public boolean isEndQuote( char c )
		{
		return containsChar( endQuoteChars, c  );
		}

	/**
	 * Returns true if c indicates the start of a comment
	 */
	public boolean isComment( char c )
		{
		return containsChar( commentChars, c  );
		}

	/**
	 * Returns true if c indicates the end of an unquoted token
	 * IE c is a whitespace, separator or comment character
	 */
	public boolean isTokenTerminator( char c )
		{
		return containsChar( whitespace + separators + commentChars, c  );
		}

	/**
	 * Returns true if beginQuote and endQuote are matching begin ending 
	 * quote characters
	 */
	public boolean isQuotePair( char beginQuote, char endQuote )
		{		
		int pairLocation = beginQuoteChars.indexOf( beginQuote );
		if (pairLocation == NOT_FOUND )
			return false;
			
		if ( endQuoteChars.charAt( pairLocation ) == endQuote )
			return true;
		else
			return false;
		}

	/**
	 * Returns true if c needs to be escaped in a quoted token.
	 *	That is if c is a quote character or the escape character
	 */
	public boolean requiresEscaping( char c )
		{
		return containsChar( beginQuoteChars + endQuoteChars + ESCAPE_CHAR, c  );
		}

	/**
	 * Returns true if c needs to be escaped in a quoted token.
	 *	That is if c is a quote character or the escape character
	 */
	public boolean containsEscapeableChar( String token )
		{
		String metaChars = beginQuoteChars + endQuoteChars + ESCAPE_CHAR;
		return containsChar( token, metaChars );
		}

	/**
	 * Places escape character before any quote character or the escape character
	 *	Returns the modified token
	 */
	public String escapeToken( String token )
		{
		StringBuffer newToken = new StringBuffer( token.length() + 10 );
		String metaChars = beginQuoteChars + endQuoteChars + ESCAPE_CHAR;

		for ( int k = 0; k < token.length(); k++ )
			{
			char c = token.charAt( k );
			
			if ( containsChar( metaChars, c ) )
				newToken.append( ESCAPE_CHAR );
			newToken.append( c );
			}
		return newToken.toString();	
		}

	/**
	 * Returns true if contains a character that indicates the end of a token.
	 * That is whitespace, comment char or a separator
	 */
	public boolean containsTokenTerminator( String token )
		{
		String quotableChars = whitespace + commentChars + separators;
		return containsChar( token, quotableChars );
		}

	/**
	 * Surrounds token with begin-end quote pair
	 *	Returns the quoted token
	 */
	public String quoteToken( String token )
		{
		return beginQuoteChars.charAt( 0 ) + token + endQuoteChars.charAt( 0 ) ;
		}

	/**
	 * Convert null string to empty string
	 */
	 private String nullFilter( String text )
	 	{
	 	if ( text == null )
	 		return "";
	 	else
	 		return text;
	 	}
	 
	 private static final boolean containsChar( String text, char pattern )
	 	{
	 	if ( text.indexOf( pattern ) == NOT_FOUND )
			return false;
		else
			return true;
		}

	 private static final boolean containsChar( String text, String pattern )
	 	{
		for ( int k = 0; k < pattern.length(); k++ )
			if ( text.indexOf( pattern.charAt( k ) ) != NOT_FOUND )
				return true; 
				
		return false;
		}

	}
