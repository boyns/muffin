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
import java.util.Enumeration;


/**
 * An OrderedCollection is a collection of objects where the objects
 * have a location in the collection. The objects can be accessed by their
 * location ( or index) in the collection. The first object is in location (index) 0 (zero).
 * @version 1.0 6 June 1997 
 * @author Roger Whitney (<a href=mailto:whitney@cs.sdsu.edu>whitney@cs.sdsu.edu</a>)
 */

public interface OrderedCollection extends Cloneable
	{

	/**
	 * Converts the OrderedCollection to a vector.
	 */
	public Vector toVector();


	/**
	 * Converts the OrderedCollection to an array.
	 */
	public Object[] toArray();


	/**
	 * Trims the OrderedCollection's capacity down to size. Use this operation to
	 * minimize the storage of a OrderedCollection. Subsequent insertions will
	 * cause reallocation.
	 */
	public void trimToSize();  

	/**
	 * Ensures that the OrderedCollection has at least the specified capacity.
	 * @param minCapacity the desired minimum capacity
	 */
	public void ensureCapacity(int minCapacity);  

	/**
	 * Sets the size of the OrderedCollection. If the size shrinks, the extra elements
	 * are lost; if the size increases, the
	 * new elements are set to null.
	 * @param newSize the new size of the OrderedCollection
	 */
	public void setSize(int newSize);  

	/**
	 * Returns the current capacity of the OrderedCollection.
	 */
	public int capacity();  

	/**
	 * Returns the number of elements in the OrderedCollection.
	 * Note that this is not the same as the OrderedCollection's capacity.
	 */
	public int size();  

	/**
	 * Returns true if the collection contains no values.
	 */
	public boolean isEmpty();  

	/**
	 * Returns an enumeration of the elements. Use the Enumeration methods on
	 * the returned object to fetch the elements sequentially.
	 */
	public Enumeration elements();  

	/**
	 * Returns an enumeration of the elements inrevese order. 
	 * That is starts at the back of the OrderedCollection and goes toward the front
	 * of the OrderedCollection.
	 */
	public Enumeration elementsReversed();  
	
	/**
	 * Returns true if the specified object is a value of the 
	 * collection.
	 * @param elem the desired element
	 */
	public boolean contains(Object elem);  

	/**
	 * Searches for the specified object, starting from the first position
	 * and returns an index to it.
	 * @param elem the desired element
	 * @return the index of the element, or -1 if it was not found.
	 */
	public int indexOf(Object elem);  

	/**
	 * Searches for the specified object, starting at the specified 
	 * position and returns an index to it.
	 * @param elem the desired element
	 * @param index the index where to start searching
	 * @return the index of the element, or -1 if it was not found.
	 */
	public int indexOf(Object elem, int index);  

	/**
	 * Searches backwards for the specified object, starting from the last
	 * position and returns an index to it. 
	 * @param elem the desired element
	 * @return the index of the element, or -1 if it was not found.
	 */
	public int lastIndexOf(Object elem);  

	/**
	 * Searches backwards for the specified object, starting from the specified
	 * position and returns an index to it. 
	 * @param elem the desired element
	 * @param index the index where to start searching
	 * @return the index of the element, or -1 if it was not found.
	 */
	public int lastIndexOf(Object elem, int index);  

	/**
	 * Returns the element at the specified index.
	 * @param index the index of the desired element
	 * @exception ArrayIndexOutOfBoundsException If an invalid 
	 * index was given.
	 */
	public Object elementAt(int index);  

	/**
	 * Returns the int at the specified index.
	 * @param index the index of the desired element
	 * @exception ArrayIndexOutOfBoundsException If an invalid 
	 * index was given.
	 * @exception ClassCastException If element at index is not really
	 * a numeric type which can be converted to an int.
	 */
	public int intAt(int index);


	/**
	 * Returns the double at the specified index.
	 * @param index the index of the desired element
	 * @exception ArrayIndexOutOfBoundsException If an invalid 
	 * index was given.
	 * @exception ClassCastException If element at index is not really
	 * a numeric type which can be converted to a double.
	 */
	public double doubleAt(int index);

	/**
	 * Returns the first element of the sequence.
	 * @exception NoSuchElementException If the sequence is empty.
	 */
	public Object firstElement();  

	/**
	 * Returns the last element of the sequence.
	 * @exception NoSuchElementException If the sequence is empty.
	 */
	public Object lastElement();  

	/**
	 * Sets the element at the specified index to be the specified object.
	 * The previous element at that position is discarded.
	 * @param obj what the element is to be set to
	 * @param index the specified index
	 * @exception ArrayIndexOutOfBoundsException If the index was 
	 * invalid.
	 */
	public void setElementAt(Object obj, int index);  

	/**
	 * Deletes the element at the specified index. Elements with an index
	 * greater than the current index are moved down.
	 * @param index the element to remove
	 * @exception ArrayIndexOutOfBoundsException If the index was invalid.
	 */
	public void removeElementAt(int index);  

	/**
	 * Inserts the specified object as an element at the specified index.
	 * Elements with an index greater or equal to the current index 
	 * are shifted up.
	 * @param obj the element to insert
	 * @param index where to insert the new element
	 * @exception ArrayIndexOutOfBoundsException If the index was invalid.
	 */
	public void insertElementAt(Object obj, int index);  

	/**
	 * Adds the specified object as the last element of the OrderedCollection.
	 * @param obj the element to be added
	 */
	public void addElement(Object obj);  

	/**
	 * Adds the specified int as the last element of the OrderedCollection.
	 * @param anInt the element to be added
	 */
	public void addElement(int anInt);  

	/**
	 * Adds the specified double as the last element of the OrderedCollection.
	 * @param aFloat the element to be added
	 */
	public void addElement(double aDouble);  

	/**
	 * Adds the elements of a Vector to the end of the collection.
	 * @param elementsToAdd the elements to be added
	 */
	public void addElements(Vector elementsToAdd);  

	/**
	 * Adds the elements of an array to the end of the collection.
	 * @param elementsToAdd the elements to be added
	 */

	public void addElements(Object[] elementsToAdd);  

	/**
	 * Removes the element from the OrderedCollection. If the object occurs more
	 * than once, only the first is removed. If the object is not an
	 * element, returns false.
	 * @param obj the element to be removed
	 * @return true if the element was actually removed; false otherwise.
	 */
	public boolean removeElement(Object obj);  

	/**
	 * Removes all elements of the OrderedCollection. The OrderedCollection becomes empty.
	 */
	public void removeAllElements();  

	/**
	 * Returns a OrderedCollection with elements in the reverse order
	 * from present OrderedCollection
	 */
	public OrderedCollection reversed();  

	/**
	 * Returns a OrderedCollection with elements from present OrderedCollection,
	 * but in random order.
	 */
	public OrderedCollection shuffled();  

	} 

