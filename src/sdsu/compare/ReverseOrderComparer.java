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

package sdsu.compare;

import java.lang.ClassCastException;

/**
 * A comparer to reverse the order used by a Comparer. For example, an 
 * IntegerComparer will evaluate lessThen( 3, 10 ) as true. A ReverseOrder
 * on an IntegerComparer will evaluate lessThen( 3, 10 ) as false. Useful
 * for reversing the order of a sort list.
 * @version 1.0 4 June 1997 
 * @author Roger Whitney (<a href=mailto:whitney@cs.sdsu.edu>whitney@cs.sdsu.edu</a>)
 */
  
public class ReverseOrderComparer extends Comparer
	{
	Comparer comparerToReverse;
	
	private ReverseOrderComparer( Comparer toReverse )
		{
		this.comparerToReverse = toReverse;
		}
	
	/**
	 * Returns a Comparer with order reversed from the Comparer 
	 * originalOrder. If originalOrder is already aReverseOrder object, 
	 * the ReversOrder objects are not nested. The Comparer inside of
	 * the originalOrder is used directly
	 */
	public static Comparer getInstance( Comparer originalOrder )
		{
		if ( originalOrder instanceof ReverseOrderComparer )
			return ( (ReverseOrderComparer) originalOrder).comparerToReverse;
		else
			return new ReverseOrderComparer( originalOrder );
		}
		
	/**
	 * Returns true if the leftOperand is less than the rightOperand.
	 * @exception ClassCastException If operand objects are not proper type.
	 *  ClassCastException is a RuntimeException, so compiler does not force you
	 *  to catch this exception.
	 */
	public final boolean lessThan( Object leftOperand, Object rightOperand ) 
		throws ClassCastException
		{
		return comparerToReverse.greaterThan( leftOperand, rightOperand );
		}
		
	/**
	 * Returns true if the leftOperand is greater than the rightOperand.
	 * @exception ClassCastException If operand objects are not proper type.
	 *  ClassCastException is a RuntimeException, so compiler does not force you
	 *  to catch this exception.
	 */
	public final boolean greaterThan( Object leftOperand, Object rightOperand ) 
		throws ClassCastException
		{
		return comparerToReverse.lessThan( leftOperand, rightOperand );
		}



	/**
	 * Returns true if the leftOperand is equal to the rightOperand.
	 * @exception ClassCastException If operand objects are not proper type.
	 *  ClassCastException is a RuntimeException, so compiler does not force you
	 *  to catch this exception.
	 */
	public final boolean equals( Object leftOperand, Object rightOperand ) 
		throws ClassCastException
		{
		return comparerToReverse.equals( leftOperand, rightOperand );
		}



	/**
	 * Returns true if the leftOperand is less than or equal to the rightOperand.
	 * @exception ClassCastException If operand objects are not proper type.
	 *  ClassCastException is a RuntimeException, so compiler does not force you
	 *  to catch this exception.
	 */
	public final boolean lessThanOrEqual( Object leftOperand, Object rightOperand ) 
		throws ClassCastException
		{
		return comparerToReverse.greaterThanOrEqual( leftOperand, rightOperand );
		}


	/**
	 * Returns true if the leftOperand is greater than or equal the rightOperand.
	 * @exception ClassCastException If operand objects are not proper type.
	 *  ClassCastException is a RuntimeException, so compiler does not force you
	 *  to catch this exception.
	 */
	public final boolean greaterThanOrEqual( Object leftOperand, Object rightOperand ) 
		throws ClassCastException
		{
		return comparerToReverse.lessThanOrEqual( leftOperand, rightOperand );
		}

	}