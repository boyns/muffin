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

import java.util.Vector;
import java.util.NoSuchElementException;
import java.util.Enumeration;
import java.util.Random;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringBufferInputStream;

/**
 * A List object is a vector that can convert itself to a string and
 * "recreate" itself from that string. The original List object can
 * contain any objects, but the recreated List object will only
 * contain string representations of the original elements in the
 * list.
 * 
 * In a List object string representation (Losr) the list elements are
 * separated by a separatorChar, which defaults to ',', but can be
 * changed.  If the string representation of a list element contains a
 * special character it is quoted.  Special characters include
 * separatorChar, a comment character, and white space characters. See
 * sdsu.util.TokenCharacters for default values. White space and
 * comments can be added to a Losr for readability. Comments start
 * with a comment character and continue upto and include the next
 * '\n' character.
 *
 * @see		Stringizer
 * @see		SimpleTokenizer
 * @see		TokenCharacters
 * @version 2.0 4 June 1997 
 * @author Roger Whitney (<a href=mailto:whitney@cs.sdsu.edu>whitney@cs.sdsu.edu</a>) */

public class List implements OrderedCollection, Stringizable
{
    protected Vector internalVector = null;

    private char separatorChar = ',';

    private TokenCharacters parseTable = new TokenCharacters(
							     String.valueOf( separatorChar) );

    /**
     * Create a new list
     */
    public List()
    {
	this( 10 );
    }

    /**
     * Create a list with initial given size
     */
    public List( int initialSize)
    {
	internalVector = new Vector (initialSize, 0);
    }

    /**
     * Create a list initial items from a list
     */
    public List( Vector initialElements )
    {
	internalVector = new Vector ();
	addElements (initialElements);
    }

    /**
     * Create a list initial items from a list
     */
    public List( Object list[] )
    {
	internalVector = new Vector ();
	addElements (list);
    }

    /**
     * Converts a string to a list. Converts strings created by a list or vector
     * Items in string are separated by separatorChar
     */
    public void fromString( String listString  ) throws ConversionException
    {
	load( new StringBufferInputStream( listString ) );
    }

    /**
     * Converts a string to a list. Converts strings created by a list or vector
     * Items in string are separated by separatorChar
     */
    public void load( InputStream in  ) throws ConversionException
    {
	try
	{
	    SimpleTokenizer parser;
	    parser = new SimpleTokenizer( in, parseTable );
			
	    while ( parser.hasMoreTokens() )
		addElement( parser.nextToken( ) );
	}
	catch (IOException unlikelyToOcurr )
	{
	    throw new ConversionException( "IO Exception thrown in converting object");
	}
    }
			
    /**
     * Converts the list to a string
     */
    public String toString( ) 
    {
	return toString( null );
    }

    /**
     * Converts the list to a string with given header information.
     */
    public String toString( String header ) 
    {
	Stringizer buffer = new Stringizer( parseTable );
	buffer.setHeader( header );
		
	Enumeration elements = elements();

	while ( elements.hasMoreElements() )
	    buffer.appendToken( elements.nextElement(), separatorChar );

	return buffer.toString();
    }

    /**
     * Converts the list to a vector.
     */
    public Vector toVector()
    {
	Vector newCopy = new Vector( internalVector.capacity () );
	Enumeration elementsToCopy = internalVector.elements();
	while ( elementsToCopy.hasMoreElements() )
	    newCopy.addElement( elementsToCopy.nextElement() );
	return newCopy;
    }

    /**
     * Converts the list to an array.
     */
    public Object[] toArray()
    {
	Object[] theArray = new Object[ internalVector.size() ];
	internalVector.copyInto (theArray);
	return theArray;
    }

    /**
     * Writes ascii representation of List to Outputstream 
     */
    public void save( OutputStream out, String header )
    {
	PrintStream printOut = new PrintStream( out );
	printOut.println( toString( header ) );
	printOut.flush();
    }

    /**
     * Set character used to separate elements of list in String representation of List
     */
    public void setSeparatorChar( char separatorChar )
    {
	this.separatorChar = separatorChar;
	upDateParseTable();
    }

    /**
     * Sets TokenCharacters used to convert List from/to strings/streams.
     * Current values for separatorChar override separator 
     * settings in TokenCharacters object.
     */
    public void setTokenCharacters( TokenCharacters newParseTable )
    {
	parseTable = newParseTable;
	upDateParseTable();
    }

    private void upDateParseTable()
    {
	parseTable.setSeparatorChars( String.valueOf( separatorChar ) );
    }

    /**
     * Returns an enumeration of the elements inrevese order. 
     * That is starts at the back of the list and goes toward the front
     * of the list.
     */
    public final synchronized Enumeration elementsReversed()  
    {
	return new ReverseListEnumerator(this);
    }
	
	    
    /**
     * Returns the int at the specified index.
     * @param index the index of the desired element
     * @exception ArrayIndexOutOfBoundsException If an invalid 
     * index was given.
     * @exception ClassCastException If element at index is not really
     * a numeric type which can be converted to an int.
     */
    public final synchronized int intAt(int index)
    {
	Number element = (Number) elementAt( index);
	return element.intValue();
    }  


    /**
     * Returns the double at the specified index.
     * @param index the index of the desired element
     * @exception ArrayIndexOutOfBoundsException If an invalid 
     * index was given.
     * @exception ClassCastException If element at index is not really
     * a numeric type which can be converted to a double.
     */
    public final synchronized double doubleAt(int index)
    {
	Number element = (Number) elementAt( index);
	return element.doubleValue();
    }  

    /**
     * Adds the specified int as the last element of the list.
     * @param anInt the element to be added
     */
    public final synchronized void addElement(int anInt)  
    {
	addElement( new Integer( anInt ) );
    }

    /**
     * Adds the specified double as the last element of the list.
     * @param aFloat the element to be added
     */
    public final synchronized void addElement(double aDouble)  
    {
	addElement( new Double( aDouble ) );
    }

    /**
     * Adds the array to the end of the list.
     */
    public void addElements( Object[] elementsToAdd )
    {
	for (int i = 0; i < elementsToAdd.length; i++)
	{
	    internalVector.addElement (elementsToAdd[i]);
	}
    }

    /**
     * Adds the vector to the end of the list.
     */
    public void addElements( Vector elementsToAdd )
    {
	for (int i = 0; i < elementsToAdd.size (); i++)
	{
	    internalVector.addElement (elementsToAdd.elementAt (i));
	}
    }

    /**
     * Returns a list with elements in the reverse order
     * from present list
     */
    public synchronized OrderedCollection reversed()  
    {
	List reverse = new List( this.size() );
	Enumeration backwords = elementsReversed();
	while ( backwords.hasMoreElements() )
	    reverse.addElement( backwords.nextElement() );
		
	return reverse;
    }

    /**
     * Returns a list with elements from present list,
     * but in random order.
     */
    public synchronized OrderedCollection shuffled()  
    {
	List shuffled = new List( this.size() );
		
	Object[] clone = new Object[ this.size() ];
	internalVector.copyInto (clone);
	
	Random number = new Random();
		
	for ( int end =  this.size(); end > 0; end-- )
	{
	    // value from 0 to end-1, including endpoints
	    int randomIndex = Math.abs(number.nextInt()) % end;
	    shuffled.addElement( clone[randomIndex] );
			
	    // remove element from list
	    clone[randomIndex] = clone[end-1];
	}
	return shuffled;
    }

    /* java.util.Vector operations */

    public Enumeration elements ()
    {
	return internalVector.elements ();
    }

    public void trimToSize ()
    {
	internalVector.trimToSize ();
    }

    public void ensureCapacity(int minCapacity)
    {
	internalVector.ensureCapacity (minCapacity);
    }

    public void setSize (int size)
    {
	internalVector.setSize (size);
    }

    public int capacity ()
    {
	return internalVector.capacity ();
    }
    
    public int size ()
    {
	return internalVector.size ();
    }

    public boolean isEmpty ()
    {
	return internalVector.isEmpty ();
    }

    public boolean contains (Object obj)
    {
	return internalVector.contains (obj);
    }

    public int indexOf (Object obj)
    {
	return internalVector.indexOf (obj);
    }

    public int indexOf (Object obj, int i)
    {
	return internalVector.indexOf (obj, i);
    }

    public int lastIndexOf (Object obj)
    {
	return internalVector.lastIndexOf (obj);
    }

    public int lastIndexOf (Object obj, int i)
    {
	return internalVector.lastIndexOf (obj, i);
    }

    public Object elementAt (int i)
    {
	return internalVector.elementAt (i);
    }

    public Object firstElement ()
    {
	return internalVector.firstElement ();
    }

    public Object lastElement ()
    {
	return internalVector.lastElement ();
    }

    public void setElementAt (Object obj, int i)
    {
	internalVector.setElementAt (obj, i);
    }
    
    public void removeElementAt (int i)
    {
	internalVector.removeElementAt (i);
    }

    public void insertElementAt (Object obj, int i)
    {
	internalVector.insertElementAt (obj, i);
    }

    public void addElement (Object obj)
    {
	internalVector.addElement (obj);
    }
    
    public boolean removeElement (Object obj)
    {
	return internalVector.removeElement (obj);
    }

    public void removeAllElements ()
    {
	internalVector.removeAllElements ();
    }

    public void replaceElements (Object objs[])
    {
	internalVector.removeAllElements ();
	addElements (objs);
    }

    public synchronized Object clone ()
    {
	try
	{ 
            List list = (List) super.clone ();
	    list.internalVector = (Vector) internalVector.clone ();
            return list;
        }
	catch (CloneNotSupportedException e)
	{ 
            throw new InternalError();
        }
    }

    public static void main (String argv[])
    {
	Vector data = new Vector ();
	data.addElement ("one");
	data.addElement ("two");
	data.addElement ("three");

	List list = new List (data);
	System.out.println (list.toString ());

	List revlist = (List) list.reversed ();
	System.out.println (revlist.toString ());

	list = new List ();
	try
	{
	    list.fromString ("foo,bar,joe");
	    System.out.println (list.toString ());
	}
	catch (Exception e)
	{
	    System.out.println (e);
	}
    }

} //class List

final class ReverseListEnumerator implements Enumeration 
{
    List enumerated;
    int count;

    ReverseListEnumerator (List v) 
    {
	enumerated = v;
	count = v.size () - 1;
    }

    public boolean hasMoreElements () 
    {
	return count >= 0;
    }

    public Object nextElement () 
    {
	synchronized (enumerated) 
	{
	    if (count >= 0) 
	    {
		return enumerated.elementAt (count--);
	    }
	}
	throw new NoSuchElementException ("ReverseListEnumerator");
    }
} //ReverseListEnumeratorListEnumerator
