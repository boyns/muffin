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
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * A Template is an document containing variables.  
 * This class does the variable substitution and 
 * replaces with given values. 
 * If a variable is not given a value, it and the variable marker is removed from the template.
 * Variables in denoted in the template by preceeding and following them by a special string,
 * the variableMarker. The default value for the variableMarker is <B>@@@</B>. The creator
 * of the template must insure this string does not occur as text in the template.
 * @version 1.0.1 18 July 1997 
 * @author Roger Whitney (<a href=mailto:whitney@cs.sdsu.edu>whitney@cs.sdsu.edu</a>)
 */
public class Template
	{
	
	String textTemplate;
	Properties variables = new Properties();
	String variableMarker = "@@@";
	
	/**
	 * Reads the text for a template from the given file.
	 * @exception FileNotFoundException if fileName can not be found
	 * @exception IOException on read error when reading file
	 */
	public static Template fromFile( String fileName ) throws FileNotFoundException, IOException

		{
		FileInputStream templateFile = new FileInputStream( fileName );
		BufferedInputStream bufferedIn = new BufferedInputStream( templateFile );
		StringBuffer templateChars = new  StringBuffer();
		int nextChar;
		
		while ( (nextChar = bufferedIn.read() ) != -1 )
			templateChars.append( (char) nextChar );
		
		bufferedIn.close();
		Template templateFromFile = new Template( templateChars.toString() );
		return templateFromFile;
		}
		
	/**
	 * Creates a template object using the given text
	 */
	public Template( String templateText )
		{
		textTemplate = templateText;
		}
	
	/**
	 * Clears all variables. Any values given to them are removed. 
	 */
	public void clear()
		{
		variables.clear();
		}
		
	/**
	 * Replaces all occurances of the given variable with given string.
	 */
	public void replace( String variableName, String variableValue )
		{
		variables.put( variableName, variableValue) ;
		}

	/**
	 * Replaces all occurances of the given variables with given values.
	 * @param variablesAndValues a hashtable whose keys are used as the variables
	 * and values are used as values of the variables. Keys and values are
	 * converted to strings before using.
	 */
	public void replace( Hashtable variablesAndValues )
		{
		Enumeration newVariables = variablesAndValues.keys();
		
		while ( newVariables.hasMoreElements() )
			{
			Object variable = newVariables.nextElement();
			Object value = variablesAndValues.get( variable ); 
			variables.put( variable.toString(), value.toString() );
			}
		}
		
	/**
	 * Changes the value of the variable marker.
	 * Variables in the template are preceeded and followed by a special string,
	 * the variable marker. Default value is <B>@@@</B>.
	 * @param  newMarker the new value for the variable marker
	 */
	public void setVariableMarker( String newMarker )
		{
		variableMarker = newMarker;
		}
		
	/**
	 * Returns the template with all variables subsituted for the given values.
	 * If a variable is not given a value, it is removed from the template string
	 */
	public String toString()
		{
		return replaceVariables();
		}
	
	protected String replaceVariables()
		{
		// Local copies of field members to reduce access time
		String template = textTemplate;
		String variableMarker = this.variableMarker;
		int markerLength = variableMarker.length();
		
		
		StringBuffer finalHtml = new StringBuffer( template.length() + 50 );
		String foundVariable;
		int index = 0;
		int beginMarker;
		int endMarker;
		
		while ( ( beginMarker = template.indexOf( variableMarker, index ) ) != -1 )
			{
			finalHtml.append( template.substring( index, beginMarker));

			endMarker = template.indexOf( variableMarker, beginMarker + markerLength);
					
			if ( endMarker == -1 )
				{
				// markers are supposed to come in pairs. Should never reach here.
				// remove the last marker and continue
				index =  beginMarker + markerLength;
				break; 
				}
				
			foundVariable = template.substring( beginMarker + markerLength, endMarker);
			foundVariable = foundVariable;
			
			finalHtml.append( variables.getProperty( foundVariable, "" ) );
			
			index =  endMarker + markerLength;
			}
		
		finalHtml.append( template.substring( index ));
		return finalHtml.toString();
		}
	}