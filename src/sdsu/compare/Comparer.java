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
 * This abstract class defines an interface for comparing objects.
 * You only need to override the compare method, as the default 
 * implementation of all other methods use compare.
 * However, for effeciency reasons you may wish to override more than compare.
 * @version 1.0 4 June 1997 
 * @author Roger Whitney (<a href=mailto:whitney@cs.sdsu.edu>whitney@cs.sdsu.edu</a>)
 */
  
public abstract class Comparer
	{
	
	/**
	 * Returns true if the leftOperand is less than the rightOperand.
	 * @exception ClassCastException If operand objects are not proper type.
	 *  ClassCastException is a RuntimeException, so compiler does not force you
	 *  to catch this exception.
	 */
	public abstract boolean lessThan( Object leftOperand, Object rightOperand ) 
		throws ClassCastException;
		
	/**
	 * Returns true if the leftOperand is greater than the rightOperand.
	 * @exception ClassCastException If operand objects are not proper type.
	 *  ClassCastException is a RuntimeException, so compiler does not force you
	 *  to catch this exception.
	 */
	public abstract  boolean greaterThan( Object leftOperand, Object rightOperand ) 
		throws ClassCastException;


	/**
	 * Returns true if the leftOperand is equal to the rightOperand.
	 * @exception ClassCastException If operand objects are not proper type.
	 *  ClassCastException is a RuntimeException, so compiler does not force you
	 *  to catch this exception.
	 */
	public abstract boolean equals( Object leftOperand, Object rightOperand ) 
		throws ClassCastException;


	/**
	 * Returns true if the leftOperand is less than or equal to the rightOperand.
	 * @exception ClassCastException If operand objects are not proper type.
	 *  ClassCastException is a RuntimeException, so compiler does not force you
	 *  to catch this exception.
	 */
	public boolean lessThanOrEqual( Object leftOperand, Object rightOperand ) 
		throws ClassCastException
		{		
		if ( lessThan( leftOperand, rightOperand) || equals( leftOperand, rightOperand) )
			return true;
		else
			return false;
		}


	/**
	 * Returns true if the leftOperand is greater than or equal the rightOperand.
	 * @exception ClassCastException If operand objects are not proper type.
	 *  ClassCastException is a RuntimeException, so compiler does not force you
	 *  to catch this exception.
	 */
	public boolean greaterThanOrEqual( Object leftOperand, Object rightOperand ) 
		throws ClassCastException
		{				
		if ( greaterThan( leftOperand, rightOperand) || equals( leftOperand, rightOperand) )
			return true;
		else
			return false;
		}

	}