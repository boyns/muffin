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
import sdsu.compare.*;
import java.util.Enumeration;


/**
 * A SortedList is a list of objects kept in sorted order. A Comparer
 * object is needed to compare objects in the list, as there is no
 * standard compare operation for objects.  Comparer class for
 * standard types are found in the package sdsu.compare. For object
 * types not covered, make a sublclass of sdsu.compare.Comparer;
 *
 * @see		List
 * @see		Comparer
 * @see		SortedCollection
 * @version 1.0 5 June 1997 
 * @author Roger Whitney (<a href=mailto:whitney@cs.sdsu.edu>whitney@cs.sdsu.edu</a>) */

public class SortedList extends List implements SortedCollection
{
    protected Comparer order;
	
    /**
     * Returns a sortedList object that will sort integers.
     */
    public static SortedList integerSorter()
    {
	return new SortedList( IntegerComparer.getInstance() );
    }

    /**
     * Returns a sortedList object that will sort floats and doubles.
     */
    public static SortedList doubleSorter()
    {
	return new SortedList( DoubleComparer.getInstance() );
    }

    /**
     * Returns a sortedList object that will sort strings.
     */
    public static SortedList stringSorter()
    {
	return new SortedList( StringComparer.getInstance() );
    }
		
    /**
     * Create a new SortedList using the Comparer object listOrder to
     * define the order on elements.
     */
    public SortedList( Comparer listOrder)
    {
	this( listOrder, 10 );
    }
	
    /**
     * Create a new SortedList using the Comparer object listOrder to
     * define the order on elements. The sortedlist will have room for
     * initialSize elements before it needs to grow.
     */
    public SortedList( Comparer listOrder, int initialSize)
    {
	super(initialSize);
	order = listOrder;
    }
		
    /**
     * Returns the comparer object used to order list.
     */
    public Comparer getComparer( )
    {
	return order;
    }
		
    /**
     * Adds the specified object in proper location to keep list sorted.
     * @param elementToAdd the element to be added
     */
    public void addElement( Object elementToAdd )
    {
	super.insertElementAt( elementToAdd, insertionIndexOf( elementToAdd ));
    }
	
    /**
     * Adds the elements of a SortedCollection to a SortedList. 
     * Resulting list is sorted
     * @param elementsToAdd the elements to be added
     */
    public void addElements( SortedCollection elementsToAdd )
    {
	if (order.equals( elementsToAdd.getComparer() ) )
	{
	    Object array[] = merge( toArray(), size(), elementsToAdd.toArray(), elementsToAdd.size() );
	    replaceElements (array);
	}
	else
	    addArray( elementsToAdd.toArray() );
    }


    /**
     * Adds the elements of a Vector to a SortedList. 
     * Resulting list is sorted
     * @param elementsToAdd the elements to be added
     */
    public void addElements( Vector elementsToAdd )
    {
	Object[] rawElements = new Object[ elementsToAdd.size() ];
	elementsToAdd.copyInto( rawElements);
	addArray( rawElements );
    }

    /**
     * Adds the elements of an array to a SortedList. 
     * Resulting list is sorted
     * @param elementsToAdd the elements to be added
     */
    public void addElements( Object[] elementsToAdd )
    {
	Object[] sortable = new Object[ elementsToAdd.length ];
	System.arraycopy( elementsToAdd ,0 , sortable, 0, elementsToAdd.length );
	addArray( sortable );
    }

    /**
     * Searches for the specified object, starting at the specified 
     * position and returns an index to it.
     * @param elem the desired element
     * @param index the index where to start searching
     * @return the index of the element, or -1 if it was not found.
     */
    public final synchronized int indexOf(Object elem, int index)  
    {
	Comparer order = this.order; 			//local access is faster
	Object[] elementData = toArray ();
		
	int searchIndex = nearBinarySearch( elementData, index, size() - 1, elem );
		
	// Find first occurance of the element
	while ( searchIndex >= index) 
	{
	    if ( order.greaterThan( elem, elementData[ searchIndex ] ) ) break;
	    searchIndex--;
	}
		
	if ( order.equals( elem, elementData[ searchIndex + 1 ] ) )
	    return searchIndex + 1;
	else
	    return -1;
    }

		
    /**
     * Returns a SortedList with elements in the reverse order
     * from present SortedList
     */
    public synchronized OrderedCollection reversed()  
    {
	SortedList reverse = (SortedList) this.clone();
	reverse.resort( ReverseOrderComparer.getInstance( order ) );
	return reverse;
    }

    /**
     * Resorts the list using the new comparer to define 
     * the ordering of the elements. 
     */	
    public synchronized void resort( Comparer newOrder )
    {
	order = newOrder;
	Object array[] = toArray ();
	sort( array, 0, array.length - 1 );
	replaceElements (array);
    }

    /**
     * Clones this list. The elements are <strong>not</strong> cloned.
     */
    public synchronized Object clone()  
    {
	SortedList v = (SortedList) super.clone();
	v.order = this.order;
	return v;
    }
		
    /**
     * Adds an array of elements to the sorted list.
     * Changes the array elementsToAdd
     */
    protected void addArray( Object[] elementsToAdd )
    {
	// Insertion sort is very good on small lists
	if ( elementsToAdd.length < 10 )
	{
	    ensureCapacity( elementsToAdd.length  + size() );
	    for (int index = 0; index < elementsToAdd.length; index++ )
		addElement( elementsToAdd[ index ] );
	}
		
	else
	{
	    Object array[] = toArray ();
	    sort( array, 0, array.length - 1);
	    Object newArray[] = merge( array, array.length, elementsToAdd, elementsToAdd.length );
	    replaceElements (newArray);
	}
    }

    /**
     * Returns the result of merging the first leftLength elements of array left and the
     * first rightLength elements of array right.
     * Requires that left and right are sorted using current comparer object.
     */
    protected Object[] merge( Object left[], int leftLength, Object[] right, int rightLenght )
    {
	int leftIndex = 0;
	int rightIndex = 0;
	int mergedIndex = 0;
	Comparer order = this.order;
		
	Object[] merged = new Object[ leftLength + rightLenght ];
		
	while ( (leftIndex < leftLength ) && ( rightIndex < rightLenght ) )
	{
	    if ( order.greaterThan( left[ leftIndex ], right[ rightIndex ] ) )
		merged[ mergedIndex++ ] = right[ rightIndex++ ];
	    else
		merged[ mergedIndex++ ] = left[ leftIndex++ ];
	}
		
	if ( leftIndex >= leftLength )
	    while ( rightIndex < rightLenght )
		merged[ mergedIndex++ ] = right[ rightIndex++ ];	
	else			
	    while (leftIndex < leftLength )
		merged[ mergedIndex++ ] = left[ leftIndex++ ];	
		
	return merged;
    }
	
    /**
     * Use binary serach to get near itemToFind in the array data. Only
     * search between indexes startIndex and endIndex of data.
     */
    private int nearBinarySearch( Object[] data, int startIndex, int endIndex, Object itemToFind )
    {
	Comparer order = this.order; 		
	int lowIndex = startIndex;
	int highIndex = endIndex;
		
	//Just get close with binary search
	// Need to check for equal elements anyway and last bit
	// of binary search is slow
	while ( (highIndex - lowIndex) > 3 )
	{
	    int midPoint = (highIndex + lowIndex) /2;

	    if ( order.lessThan( itemToFind, data[midPoint] ) )
		highIndex = midPoint - 1;
	    else if ( order.greaterThan( itemToFind, data[ midPoint ] ))
		lowIndex = midPoint + 1;
	    else //found element, 
		return midPoint;
	}
	return highIndex;
    }
		
		
    /**
     * Sorts the list elements from lowIndex to highIndex
     */
    protected void sort( Object[] list, int lowIndex, int highIndex )
    {
	// Shell sort. 
	Comparer order = this.order;
	int stepSize = highIndex - lowIndex + 1;
		
	while ( stepSize > 1 )
	{
	    if ( stepSize < 5 )
		stepSize = 1; 		// perform normal insertion sort
	    else
		stepSize = (5*stepSize - 1)/ 11;
			
	    // Do linear insertion sort from bottom to top in steps
	    for ( int sortChain = highIndex - stepSize; sortChain >= lowIndex; sortChain--)
	    {
		Object insertMe = list[ sortChain ];
		int k;
		for ( k = sortChain + stepSize; 
		      k <= highIndex && order.greaterThan( insertMe, list[ k ] );
		      k += stepSize
		      )
		{
		    list[ k - stepSize] = list[ k ];
		}
				
		list[ k - stepSize ] = insertMe;
	    }
	}		
    }
			
    protected int insertionIndexOf( Object item )
    {
	int insertionPoint = super.size();
	while ( (insertionPoint > 0) && 
		( order.lessThan( item, elementAt(insertionPoint-1) ))
		)
	    insertionPoint--;
	return insertionPoint;
		 
    }
		
    public static void main (String argv[])
    {
	Vector data = new Vector ();
	data.addElement ("one");
	data.addElement ("two");
	data.addElement ("three");
	data.addElement ("four");
	data.addElement ("five");

	SortedList list = new SortedList (StringIgnoreCaseComparer.getInstance ());
	list.addElements (data);
	System.out.println (list.toString ());

	Object objs[] = { "apple", "orange", "pear", "banana", "guava" };
	list.removeAllElements ();
	list.addElements (objs);
	System.out.println (list.toString ());
    }
}
