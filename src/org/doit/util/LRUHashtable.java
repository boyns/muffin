/* $Id: LRUHashtable.java,v 1.1 2003/05/25 03:03:59 cmallwitz Exp $ */

/*
 * This file is part of Muffin.
 *
 * Muffin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Muffin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Muffin; see the file COPYING.  If not, write to the
 * Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */

package org.doit.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * This class represents a hashtable with limited size. If the maximum size is
 * reached the LRU item is removed from the hashtable. The LRUHashtable keys
 * are equivalent to that of java.util.Hashtable. The values are wrapped in
 * ListNode objects to ensure LRU handling. NOTE: The behavior of functions
 * that have the same notation as functions of Hashtable is similar to that
 * functions, i.e. NullPointerException are thrown if
 * keys or values are null.
 *
 * @see java.util.Hashtable
 */

public class LRUHashtable implements Serializable
{
    /**
     * This member contains the maximum number of items.
     */

    private transient int maxSize = 1000;

    /**
     * The hashtable that contains the items.
     */

    private transient HashMap delegate = null;

    /**
     * The LRU list node.
     */

    private transient ListNode lruNode = null;

    /**
     * The MRU list node.
     */

    private transient ListNode mruNode = null;

    /**
     * The default constructor. Initializes the hashtable using the defaults.
     */

    public LRUHashtable()
    {
        delegate = new HashMap();
    }

    /**
     * The constructor. Initializes the hashtable using the given parameters.
     *
     * @param anInitialCapacity the initial capacity of the hashtable.
     * @param aLoadFactor a number between 0.0 and 1.0.
     *
     * @exception java.lang.IllegalArgumentException if the initial capacity is less
     *            than or equal to zero, or if the load factor is less than
     *            or equal to zero.
     */

    public LRUHashtable(int anInitialCapacity, float aLoadFactor)
    {
        delegate = new HashMap(anInitialCapacity, aLoadFactor);
    }

    /**
     * The constructor. Initializes the hashtable using the defaults
     * and sets the maximum size to the specified value.
     *
     * @param aMaxSize the maximum size of hashtable items.
     * @exception java.lang.IllegalArgumentException if the maximum size is less
     *            than or equal to zero.
     */

    public LRUHashtable(int aMaxSize)
    {
        if (aMaxSize < 1)
        {
            throw new IllegalArgumentException(
                "Maximum size must be greater than zero!");
        }

        delegate = new HashMap();
        maxSize = aMaxSize;
    }

    /**
     * The constructor. Initializes the hashtable using the given parameters
     * and sets the maximum size to the specified value.
     *
     * @param anInitialCapacity the initial capacity of the hashtable.
     * @param aMaxSize the maximum size of hashtable items.
     *
     * @exception java.lang.IllegalArgumentException if the initial capacity is less
     *            than or equal to zero, or if the maximum size is less than
     *            or equal to zero.
     */

    public LRUHashtable(int anInitialCapacity, int aMaxSize)
    {
        if (aMaxSize < 1)
        {
            throw new IllegalArgumentException(
                "Maximum size must be greater than zero!");
        }

        delegate = new HashMap(anInitialCapacity);
        maxSize = aMaxSize;
    }

    /**
     * The Initializes the hashtable using the given parameters
     * and sets the maximum size to the specified value.
     *
     * @param anInitialCapacity the initial capacity of the hashtable.
     * @param aLoadFactor a number between 0.0 and 1.0.
     * @param aMaxSize the maximum size of hashtable items.
     *
     * @exception java.lang.IllegalArgumentException if the initial capacity is less
     *            than or equal to zero, or if the load factor is less than
     *            or equal to zero, or if the maximum size is less than
     *            or equal to zero.
     */

    public LRUHashtable(int anInitialCapacity, float aLoadFactor, int aMaxSize)
    {
        if (aMaxSize < 1)
        {
            throw new IllegalArgumentException(
                "Maximum size must be greater than zero!");
        }

        delegate = new HashMap(anInitialCapacity, aLoadFactor);
        maxSize = aMaxSize;
    }

    /**
     * Returns the maximum number of items in this hashtable.
     *
     * @return the maximum number of items in this hashtable.
     */

    public int getMaxSize()
    {
        return maxSize;
    }

    /**
     * Sets the maximum number of items in this hashtable. If the new size
     * is smaller than the current size, the appropriate number of LRU items
     * is removed from the list.
     *
     * @param aNewSize The new maximum number of items in this hashtable.
     *        Must be a positive number.
     *
     * @exception java.lang.IllegalArgumentException if the maximum size is less
     *            than or equal to zero.
     */

    public synchronized void setMaxSize(int aNewSize)
    {
        if (aNewSize < 1)
        {
            throw new IllegalArgumentException(
                "Maximum size must be greater than zero!");
        }

        // shrink the size of the list
        for (int i = size(); i > aNewSize; i--)
        {
            // remove the LRU from the LRU list
            ListNode node = removeLRU();
            // remove the according element from the hashtable
            removeFromDelegate(node.getKey());
        }

        // assign the new size
        maxSize = aNewSize;
    }

    /**
     * Returns the number of keys in this hashtable.
     *
     * @return the number of keys in this hashtable.
     *
     * @see java.util.Hashtable#size
     */

    public synchronized int size()
    {
        return delegate.size();
    }

    /**
     * Tests if this hashtable maps no keys to values.
     *
     * @return true if this hashtable maps no keys to values; false otherwise.
     *
     * @see java.util.Hashtable#isEmpty
     */

    public synchronized boolean isEmpty()
    {
        return delegate.isEmpty();
    }

    /**
     * Returns an enumeration of the keys in this hashtable.
     *
     * @return an enumeration of the keys in this hashtable.
     *
     * @see java.util.Hashtable#keys
     */

    public synchronized Enumeration keys()
    {
        return Collections.enumeration(delegate.keySet());
    }

    /**
     * Returns an enumeration of the values in this hashtable. Use the
     * Enumeration methods on the returned object to fetch the elements
     * sequentially.
     *
     * @return an enumeration of the values in this hashtable.
     *
     * @see java.util.Hashtable#elements
     */

    public synchronized Enumeration elements()
    {
        ListNodeElementEnumerator enum =
            new ListNodeElementEnumerator(delegate.values().iterator());
        return enum;
    }

    /**
     * Tests if some key maps into the specified value in this hashtable.
     *
     * @param aValue - a value to search for.
     * @return true if some key maps to the value argument in this hashtable;
     *         false otherwise.
     *
     * @see java.util.Hashtable#contains
     */

    public synchronized boolean contains(Object aValue)
    {
        return delegate.containsValue(aValue);
    }

    /**
     * Returns true if this Hashtable maps one or more keys to this value.<p>
     *
     * Note that this method is identical in functionality to contains
     * (which predates the Map interface).
     *
     * @param value value whose presence in this Hashtable is to be tested.
     * @return <tt>true</tt> if this map maps one or more keys to the
     *         specified value.
     * @throws NullPointerException  if the value is <code>null</code>.
     */

    public synchronized boolean containsValue(Object value)
    {
        return contains(value);
    }

    /**
     * Returns the value to which the specified key is mapped in this hashtable.
     *
     * @param aKey - a key in the hashtable.
     * @return the value to which the key is mapped in this hashtable;
     *         null if the key is not mapped to any value in this hashtable.
     *
     * @see java.util.Hashtable#get
     */

    public synchronized Object get(Object aKey)
    {
        ListNode node = (ListNode)getFromDelegate(aKey);
        if (node != null)
        {
            setNodeMRU(node);
            return node.getElement();
        }
        return null;
    }

    /**
     * Maps the specified key to the specified value in this hashtable.
     * Neither the key nor the value can be null. The value can be retrieved
     * by calling the get method with a key that is equal to the original key.
     *
     * @param aKey - the hashtable key.
     * @param aValue - the value.
     * @return the previous value of the specified key in this hashtable,
     *         or null if it did not have one.
     *
     * @see java.util.Hashtable#put
     */

    public synchronized Object put(Object aKey, Object aValue)
    {
        Object result = null;
        // reuse the ListNode if already used, saves one new operation
        ListNode node = (ListNode)getFromDelegate(aKey);
        if (node != null)
        {
            result = node.getElement();

            // assign the new value
            node.setElement(aValue);
        }
        else
        {
            // create a new node
            node = new ListNode(aValue, aKey);
            // check if maxSize is reached and remove LRU

            if (delegate.size() == maxSize)
            {
                ListNode oldLRU = removeLRU();
                removeFromDelegate(oldLRU.getKey());
            }

            addToDelegate(aKey, node);
        }
        setNodeMRU(node);
        return result;
    }

    /**
     * Removes the key (and its corresponding value) from this hashtable.
     * This method does nothing if the key is not in the hashtable.
     *
     * @param aKey - the key that needs to be removed.
     * @return the value to which the key had been mapped in this hashtable,
     *         or null if the key did not have a mapping.
     *
     * @see java.util.Hashtable#remove
     */

    public synchronized Object remove(Object aKey)
    {
        // get the Node
        ListNode node = (ListNode)removeFromDelegate(aKey);
        if (node != null)
        {
            // remove the node from list and return the node data element
            removeNodeFromList(node);
            return node.getElement();
        }
        return null;
    }

    /**
     * Clears this hashtable so that it contains no keys.
     *
     * @see java.util.Hashtable#clear
     */

    public synchronized void clear()
    {
        clearDelegate();
        clearList();
    }

    // protected members. can be used for simple hooking in derived classes

    /**
     * Implement this function in derived classes if you want to add
     * some special behavior on putting objects into the hashtable.
     * NOTE: Don't forget to call the super function in derived classes!
     *
     * @param aKey - the hashtable key.
     * @param aValue - the value.
     * @return the previous value of the specified key in this hashtable,
     *         or null if it did not have one.
     */

    protected Object addToDelegate(Object aKey, Object aValue)
    {
        return delegate.put(aKey, aValue);
    }


    /**
     * Implement this function in derived classes if you want to add
     * some special behavior on getting objects from the hashtable.
     * NOTE: Don't forget to call the super function in derived classes!
     *
     * @param aKey - the hashtable key.
     * @return the value to which the key is mapped in this hashtable;
     *         null if the key is not mapped to any value in this hashtable.
     */

    protected Object getFromDelegate(Object aKey)
    {
        return delegate.get(aKey);
    }


    /**
     * Implement this function in derived classes if you want to add
     * some special behavior on removing objects from the hashtable,
     * e.g. special cleanup.
     * NOTE: Don't forget to call the super function in derived classes!
     *
     * @param aKey - the key that needs to be removed.
     * @return the value to which the key had been mapped in this hashtable,
     *         or null if the key did not have a mapping.
     */

    protected Object removeFromDelegate(Object aKey)
    {
        return delegate.remove(aKey);
    }


    /**
     * Implement this function in derived classes if you want to add
     * some special behavior on clearing the hashtable, e.g. special cleanup.
     * NOTE: Don't forget to call the super function in derived classes!
     */

    protected void clearDelegate()
    {
        delegate.clear();
    }


    // private members

    /**
     * Internal function that clears the references between the ListNode
     * classes. This is necessary to ensure the objects to be garbage collected.
     */

    private void clearList()
    {
        while (lruNode != null)
        {
            ListNode oldLRU = lruNode;
            lruNode = lruNode.getNext();
            if (lruNode != null)
            {
                lruNode.setPrev(null);
            }
            oldLRU.setNext(null);
        }
        mruNode = null;
    }

    /**
     * Internal function that removes a ListNode from the list.
     *
     * @param aNode the node that has to be removed
     */

    private synchronized void removeNodeFromList(ListNode aNode)
    {
        if (aNode != null)
        {
            if (aNode.getPrev() != null)
            {
                // node has prev, connect to next
                aNode.getPrev().setNext(aNode.getNext());
            }
            else
            {
                // node has no prev -> must be LRU
                lruNode = aNode.getNext();
                if( lruNode != null )
                {
                    lruNode.setPrev(null);
                }
            }

            if (aNode.getNext() != null)
            {
                // node has next, connect to prev
                aNode.getNext().setPrev(aNode.getPrev());
            }
            else
            {
                // node has no next -> must be MRU
                mruNode = aNode.getPrev();
                if( mruNode != null )
                {
                    mruNode.setNext(null);
                }
            }

            // clear node references
            aNode.setPrev(null);
            aNode.setNext(null);
        }
    }

    /**
     * Internal function to remove the given node from the current
     * position and set the node as MRU.
     *
     * @param aNode the node that has to be set MRU
     */

    private synchronized void setNodeMRU(ListNode aNode)
    {
        if (aNode != null)
        {
            if (mruNode != null)
            {
                // aNode is already mruNode
                if( aNode == mruNode )
                {
                    return;
                }
                // aNode is lruNode
                else if( aNode == lruNode )
                {
                    // set new lruNode
                    if( lruNode.getNext() != null )
                    {
                        lruNode = aNode.getNext();
                        lruNode.setPrev(null);
                    }
                }
                // aNode is between two others
                else if( aNode.getPrev() != null && aNode.getNext() != null )
                {
                    // connect previous and next node
                    aNode.getPrev().setNext( aNode.getNext() );
                    aNode.getNext().setPrev( aNode.getPrev() );
                }

                mruNode.setNext(aNode);
                aNode.setPrev(mruNode);
            }
            else
            {
                // no MRU means no LRU too
                lruNode = aNode;
            }
            mruNode = aNode;
            mruNode.setNext(null);
        }
    }

    /**
     * This internal function is called to remove the LRU.
     *
     * @return the LRU list node.
     */

    private synchronized ListNode removeLRU()
    {
        ListNode oldLRU = lruNode;
        if (oldLRU != null)
        {
            lruNode = oldLRU.getNext();
            oldLRU.setNext(null);
            if (lruNode == null)
            {
                // list is empty, set MRU also null
                mruNode = null;
            }
            else
            {
                lruNode.setPrev(null);
            }
        }

        return oldLRU;
    }

    /**
     * A customized serialization method. Adds the linked ListNodes to a Vector
     * and serializes the Vector.
     *
     * @param       out     the object output stream for the serialization
     * @exception   java.io.IOException  if a write error occured
     * @see         java.io.Serializable
     */

    private void writeObject(ObjectOutputStream out) throws IOException
    {
        // write items from linked list, starting from lruNode
        ListNode next = lruNode;
        Vector   vector = new Vector(this.size());
        while (next != null)
        {
            vector.add(next);
            next = next.getNext();
        }

        // write max size
        out.writeInt(maxSize);

        // write vector
        out.writeObject(vector);
    }

    /**
     * A customized deserialization method. Reads maxsize & re-initializes the
     * linked list in the correct order.
     *
     * @param       in      the object input stream for the deserialization
     * @exception   java.io.IOException  if a read error occured
     * @exception   java.lang.ClassNotFoundException    if a child class was
     not found
     * @see         java.io.Serializable
     */

    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException
    {
        // read max size
        maxSize = in.readInt();

        // read vector
        Vector vector = (Vector) in.readObject();

        // Initialize delegate
        delegate = new HashMap(maxSize);

        // first read is lruNode, if only one is also mruNode
        int         count, i;
        ListNode    current, prev;

        lruNode = mruNode = null;
        count   = vector.size();
        current = prev = null;
        for (i=0; i<count; i++)
        {
            current = (ListNode) vector.elementAt(i);

            // first time, set lruNode
            if (lruNode == null)
            {
                lruNode = current;
            }

            // if we had a previous, link now
            if (prev != null)
            {
                prev.setNext(current);
                current.setPrev(prev);
            }

            // set prev for next time
            prev = current;

            // add current to delegate
            delegate.put(current.getKey(), current);
        }

        // last time, set mruNode
        mruNode = current;
    }

    /**
     * Outputs the LRUHashtable contents as a string, useful for debugging
     *
     * @return key and element as a string, useful for debugging
     */

    public String toString()
    {
        StringBuffer buff        = new StringBuffer();

        buff.append("[maxSize=");
        buff.append(maxSize);
        buff.append(", size=");
        buff.append(this.size());
        buff.append(':');

        // write items from linked list, starting from lruNode
        ListNode prev = mruNode;
        while (prev != null)
        {
            buff.append(prev.toString());
            buff.append(", ");
            prev = prev.getPrev();
        }

        buff.append(']');

        return buff.toString();
    }

/**
 * This class represents a double linked list node.
 */

    public static class ListNode implements Serializable
    {
        /**
         * This member holds a reference to the node's data element.
         */

        private Object element = null;

        /**
         * The next list element.
         */

        private ListNode next = null;

        /**
         * The previous list element.
         */

        private ListNode prev = null;

        /**
         * This member holds a backward reference to the key element in case of
         * using the node in a hashtable
         */

        private Object key = null;

        /**
         * The default constructor
         */

        public ListNode()
        {
        }

        /**
         * The constructor.
         *
         * @param anObject The data element.
         */

        public ListNode(Object anObject)
        {
            element = anObject;
        }

        /**
         * The constructor.
         *
         * @param anObject The data element.
         * @param aKey The corresponding key element.
         */

        public ListNode(Object anObject, Object aKey)
        {
            element = anObject;
            key = aKey;
        }

        /**
         * Call this function to get the reference to the next list node.
         *
         * @return The next list node or null if there is no next node.
         */

        public ListNode getNext()
        {
            return next;
        }

        /**
         * Call this function to set the reference to the next list node.
         *
         * @param aNode The new next node.
         */

        public void setNext(ListNode aNode)
        {
            next = aNode;
        }

        /**
         * Call this function to get the reference to the previous list node.
         *
         * @return The previous list node or null if there is no previous node.
         */

        public ListNode getPrev()
        {
            return prev;
        }

        /**
         * Call this function to set the reference to the previous list node.
         *
         * @param aNode The new previous node.
         */

        public void setPrev(ListNode aNode)
        {
            prev = aNode;
        }

        /**
         * Call this function to get the reference to the node's element data.
         *
         * @return The list node's element data or null if not set.
         */

        public Object getElement()
        {
            return element;
        }

        /**
         * Call this function to set the reference to the list node's element data.
         *
         * @param anObject The new element data.
         */

        public void setElement(Object anObject)
        {
            element = anObject;
        }

        /**
         * Call this function to get the reference to the node's element key.
         *
         * @return The list node's element key or null if not set.
         */

        public Object getKey()
        {
            return key;
        }

        /**
         * Call this function to set the reference to the list node's element key.
         *
         * @param anObject The new element key.
         */

        public void setKey(Object anObject)
        {
            key = anObject;
        }

        /**
         * A customized serialization method. Writes only key & element to the
         * output stream. To serialize a linked list the container object such as
         * the LRUHashtable is responsible for storing the order in which ListNodes
         * appear.
         *
         * <BR>This method will throw IOException subclass NotSerializableException
         * if either key or element do not implement Seriablizable.
         *
         * @param       out     the object output stream for the serialization
         * @exception   java.io.IOException  if a write error occured
         * @see         java.io.Serializable
         */

        private void writeObject(ObjectOutputStream out) throws IOException
        {
            // write key and element only
            out.writeObject(key);
            out.writeObject(element);
        }

        /**
         * A customized deserialization method. Reads key and element only. To
         * serialize a linked list the container object such as
         * the LRUHashtable is responsible for storing the order in which ListNodes
         * appear.
         *
         * @param       in      the object input stream for the deserialization
         * @exception   java.io.IOException  if a read error occured
         * @exception   java.lang.ClassNotFoundException    if a child class was
         not found
         * @see         java.io.Serializable
         */

        private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException
        {
            // read key and element only
            key     = in.readObject();
            element = in.readObject();
        }

        /**
         * Outputs the key and element as a string, useful for debugging
         *
         * @return key and element as a string, useful for debugging
         */

        public String toString()
        {
            StringBuffer buff        = new StringBuffer();
            Object       safeKey     = (key == null)     ? "null" : key;
            Object       safeElement = (element == null) ? "null" : element;

            buff.append(safeKey.toString());
            buff.append('=');
            buff.append(safeElement.toString());

            return buff.toString();
        }
    }

/**
 * This class takes an enumeration of ListNode classes and changes the
 * functionality so that nextElement() does return the ListNode's data
 * element instead of the ListNode.
 *
 * @author      J. Grabs
 * @version     1.00, 03/09/99
 *
 * @see java.util.Enumeration
 */

    public static class ListNodeElementEnumerator implements Enumeration
    {
        /**
         * This member hols a reference to the original ListNode enumeration.
         */

        private Iterator listNodes;

        /**
         * Constructor. Assigns the given enumeration to the internal member.
         *
         * @param aNodesEnumeration an enumeration of ListNode classes
         */

        public ListNodeElementEnumerator(Iterator aNodesEnumeration)
        {
            listNodes = aNodesEnumeration;
        }


        /**
         * Tests if this enumeration contains more elements.
         *
         * @return true if this enumeration contains more elements; false otherwise.
         */

        public boolean hasMoreElements()
        {
            return listNodes.hasNext();
        }

        /**
         * Returns the next data element of this enumeration of ListNode classes.
         * NOTE: An error will occur in this function if the original elements are
         * not of type ListNode!
         *
         * @return the next data element of this enumeration.
         *
         * @see java.util.Enumeration#nextElement
         */

        public Object nextElement()
        {
            return ((ListNode)listNodes.next()).getElement();
        }
    }
}
