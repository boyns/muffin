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

/**
 * A concrete class for comparing Strings. Comparison is case sensitve.
 * @version 1.0 4 June 1997 
 * @author Roger Whitney (<a href=mailto:whitney@cs.sdsu.edu>whitney@cs.sdsu.edu</a>)
 */
 
public class StringComparer extends Comparer
	{

	private static Comparer singleInstance;
	
	/**
	 * Force use of instance method to get object instance.
	 */  
	private StringComparer() {}	
	
	/**
	 * Returns a StringComparer object.
	 */
	public static Comparer getInstance()
		{
		if ( singleInstance == null)
			singleInstance = new StringComparer();
		
		return singleInstance;
		}
		
	
	/**
	 * Returns true if the leftOperand is less than the rightOperand.
	 * @exception ClassCastException If operand objects are not String objects.
	 *  ClassCastException is a RuntimeException, so compiler does not force you
	 *  to catch this exception.
	 */
	public final boolean lessThan( Object leftOperand, Object rightOperand ) 
		throws ClassCastException
		{		
		String left =  (String) leftOperand;

		if ( left.compareTo( (String) rightOperand ) < 0 )
			return true;
		else
			return false;
		}

	/**
	 * Returns true if the leftOperand is greater than the rightOperand.
	 * @exception ClassCastException If operand objects are not String objects.
	 *  ClassCastException is a RuntimeException, so compiler does not force you
	 *  to catch this exception.
	 */
	public final boolean greaterThan( Object leftOperand, Object rightOperand ) 
		throws ClassCastException
		{
		String left =  (String) leftOperand;

		if ( left.compareTo( (String) rightOperand ) > 0 )
			return true;
		else
			return false;
		}


	/**
	 * Returns true if the leftOperand is equal to the rightOperand.
	 * @exception ClassCastException If operand objects are not String objects.
	 *  ClassCastException is a RuntimeException, so compiler does not force you
	 *  to catch this exception.
	 */
	public final boolean equals( Object leftOperand, Object rightOperand ) 
		throws ClassCastException
		{
		String left =  (String) leftOperand;

		if ( left.equals( rightOperand ) )
			return true;
		else
			return false;
		}

	}
