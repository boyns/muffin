
 
package sdsu.util;

import java.util.Vector;
import sdsu.compare.Comparer;
import java.util.Enumeration;


/**
 * A List object is a vector that can convert itself to a string and "recreate" itself from 
 * that string. The original List object can contain any objects, but
 * the recreated List object will only contain string representations of the original
 * elements in the list.
 * 
 * In a List object string representation (Losr) the list elements are
 * separated by a separatorChar, which defaults to ',', but can be changed. 
 * If the string representation of a list element contains a special character it is quoted. 
 * Special characters include separatorChar, a comment character, and
 * white space characters. See sdsu.util.TokenCharacters for default values. White space and
 * comments can be added to a Losr for readability. Comments start with a comment character and
 * continue upto and include the next '\n' character.
 *
 * @see		List
 * @version 1.0 6 June 1997 
 * @author Roger Whitney (<a href=mailto:whitney@cs.sdsu.edu>whitney@cs.sdsu.edu</a>)
 */

public interface SortedCollection extends OrderedCollection
	{
	
	/**
	 * Returns the comparer object used to order list.
	 */
	public Comparer getComparer( );
	
	public void addElements( SortedCollection elements );

	public void resort( Comparer newOrder);
	
	//public void removeElementsBetween( Object first, Object second);
		
	}