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

import java.util.Properties;
import java.util.Enumeration;
import java.io.StringBufferInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * This class is a hashtable with two additional features. <B>First</B>, it can parse command line
 * arguments.
 * Flags are indicated in command line via 
 * "-" before the flag. Supported syntax:<br>
 * <B> -flag=value</B> 
 * <br><B> -flag value</B> The string "flag" (without the '-' and without the "'s)  
 * is stored as key. The string "value" is the associated value in the hashtable. If 
 * -flag is the last argument or the argument after -flag starts with a '-', 
 * the string "NO_VALUE" is stored as value for the flag.
 * <br><B> --flagChars</B> Individual characters after the -- are treated as separate
 * flags. The string "NO_VALUE" is stored as value for each flag.
 * <br><B>--</B> ignore rest of the command line arguments.
 * <br><br>
 * <B>Second</B>, a LabeledData object can conver itself to a string and sort of "recreate" itself from 
 * that string. The original LabeledData object can contain any objects as keys and values. 
 * However, objects other than strings, numbers, DataPairs, and Lists may cause problems (the string
 * representations of other objects may cause parsing error). 
 * A recreated LabeledData object will only contain string representations of the original
 * keys and values. In a LabeledData object string representation (Losr) the key and values are
 * separated by a keyValueSeparatorChar, which defaults to '='. In a Losr 
 * key-value pairs are separated by pairSeparatorChars,which defaults to ";".
 * If the string representation of a key or value contains a special character it is quoted. 
 * Special characters include keyValueSeparatorChar, pairSeparatorChars, a comment character, and
 * white space characters. See sdsu.util.TokenCharacters for default values. White space and
 * comments can be added to a Losr for readability. Comments start with a comment character and
 * continue upto and include the next '\n' character.
 *
 * @see		Stringizer
 * @see		SimpleTokenizer
 * @see		TokenCharacters
 * @version 1.0 2 February 1997 
 * @author Roger Whitney (<a href=mailto:whitney@cs.sdsu.edu>whitney@cs.sdsu.edu</a>)
 */

public class LabeledData extends Properties
	{

	/**
	 * String used for value in key-value pair when no value is available.
	 */ 
	public static final String NO_VALUE = "";
	
	/*
	 * Character used to separate keys and values in string representation
	 */
	private char keyValueSeparatorChar 	= '=';

	/*
	 * Characters used to separate key-value pairs in string representation
	 */
	private String pairSeparatorChars	= ";";
	
	/*
	 * Make all keys lowercase. 
	 */
	private boolean lowerCaseKeys		= false;

	private boolean eatEscapeChar		= false;

	private TokenCharacters parseTable = new TokenCharacters(
		String.valueOf( keyValueSeparatorChar) + pairSeparatorChars);

	public LabeledData()
		{
		super();
		}

	/**
	 * Constructs a new, empty labeledData object with the specified 
	 * default values.
	 * @param defaultValues Properties object that is used for default values in new object.
	 */
	public LabeledData( Properties defaultValues )
		{
		super( defaultValues );
		}

	/**
	 * Store a key/value pair in the hashtable lowercasing keys if necessary.
	 */
	public Object put (String key, String value)
		{
		return super.put (lowerCaseKeys ? key.toLowerCase () : key, value);
		}

	/**
	 * Gets value with given label. If label does not exits, check defaults.
	 * Returns null if label is not found.
	 */
	public String getData( String label )
		{
		return getProperty( label );
		}

	/**
	 * Gets value with given label. If label does not exits, check defaults.
	 * Returns defaultValue if label is not found
	 */
	public String getData( String label, String defaultValue )
		{
		return getProperty( label, defaultValue );
		}
	
	/**
	 * Constructs a labeled data table from command line arguments. Keys are the flags,
	 * values are strings associated with a flag.
	 * @param arguments String array passed to main.
	 */
	public void fromCommandLine( String[] arguments )
		{
		int doesNotContain = -1;
		
		for ( int k = 0; k < arguments.length; k++ )
			{
			String argument = arguments[k];

			if ( isFlag( argument ) == false ) continue;
			
			else if ( argument.equals( "--" ) ) return;
			
			else if ( argument.startsWith( "--" ) == true )
				processSingleLetterFlag( argument );
			
			else if ( argument.indexOf( '=' ) != doesNotContain )
				processFlagWithAssigment( argument );
			
			else if ( (k + 1) == arguments.length ) 	// no value with flag
				put( argument.substring( 1 ) , NO_VALUE ); 	// remove '-' from flag			
			
			else if ( isFlag( arguments[ k+1 ] ) == true ) 	// no value with flag
				put( argument.substring( 1 ), NO_VALUE );		// remove '-' from flag	
			
			else
				put( argument.substring( 1 ), arguments[ k+1 ] );	// remove '-' from flag	
			}
		}
	
	/**
	 * Converts a string to a list. Converts strings created by a list or vector
	 * Items in string are separated by separatorChar
	 */
	public void fromString( String dataString ) throws IOException
		{
		load( new StringBufferInputStream( dataString ) );
		}
	
	/**
	 * Loads a list from an inputstream. Data in stream must be in list format.
	 */
	 public void load( InputStream in ) throws IOException
	 	{
		SimpleTokenizer parser;
		parser = new SimpleTokenizer( in, parseTable );
		parser.setEatEscapeChar( eatEscapeChar );
				
		while ( parser.hasMoreTokens() )
			{
			String value;
			String key = parser.nextToken( );
			
			if ( parser.separator() == keyValueSeparatorChar )
				value = parser.nextToken( );
			else
				value = NO_VALUE;
			//System.out.println ("key="+key+" value="+value);
			put( key, value );
			}
	 	}

    /**
     * Converts LabeledData to a String.
     */
    public String toString( ) 
    	{
    	return toString( null );
    	}

    /**
     * Converts LabeledData to a String with given header information.
     */
    public String toString( String header ) 
	    {
		Stringizer buffer = new Stringizer( parseTable );
		buffer.setHeader( header );
		Enumeration keys = keys();
		Enumeration elements = elements();

		while ( elements.hasMoreElements() )
			{
			buffer.appendToken( keys.nextElement(), keyValueSeparatorChar );
			buffer.appendToken( elements.nextElement(), pairSeparatorChars.charAt(0) );
			}

		return buffer.toString();
	    }

	/**
	 * Writes ascii representation of LabeledData to Outputstream 
	 */
	 public void save( OutputStream out, String header )
	 	{
	 	PrintStream printOut = new PrintStream( out );
	 	printOut.println( toString( header ) );
	 	printOut.flush();
	 	}

	/**
	 * Set character used to key-value pairs in String representation of LabeledData
	 */
	public void setKeyValueSeparatorChar( char keyValueSeparatorChar )
		{
		this.keyValueSeparatorChar = keyValueSeparatorChar;
		upDateParseTable();
		}

	/**
	 * Set characters used to separate keys and values in String representation of LabeledData
	 */
	public void setPairSeparatorChars( String pairSeparatorChars )
		{
		this.pairSeparatorChars = pairSeparatorChars;
		upDateParseTable();
		}

	/**
	 * Sets TokenCharacters used to convert LabeledData from/to strings/streams.
	 * Current values for pairSeparatorChars and keyValueSeparatorChar override separator 
	 * settings in TokenCharacters object.
	 */
	public void setTokenCharacters( TokenCharacters newParseTable )
		{
		parseTable = newParseTable;
		upDateParseTable();
		}

	public void setLowerCaseKeys( boolean lowerCaseKeys )
		{
		    this.lowerCaseKeys = lowerCaseKeys;
		}
	    
	public void setEatEscapeChar( boolean eatEscapeChar )
		{
		    this.eatEscapeChar = eatEscapeChar;
		}

	private void upDateParseTable()
		{
		parseTable.setSeparatorChars( String.valueOf( keyValueSeparatorChar ) + pairSeparatorChars );
		}
		
	private void processFlagWithAssigment( String flag )
		{
		int equalsLocation = flag.indexOf( "=" );
		
		put( flag.substring( 1, equalsLocation), flag.substring(  equalsLocation + 1) );
		}
	
	private void processSingleLetterFlag( String flag )
		{
		for ( int k = 2;  k < flag.length(); k++ )
			put( flag.substring( k, k+1 ), NO_VALUE );
		}
		
	private boolean isFlag( String argument)
		{
		if ( argument.startsWith( "-" ) )
			return true;
		else
			return false;
		}
		

	} //end Class DataPairs
