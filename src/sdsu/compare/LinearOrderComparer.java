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
 * A abstract class for comparing Linear ordered objects (Numeric).
 * Subclass needs to implement equal, lessThan, greaterThan.
 */
 
public abstract class LinearOrderComparer extends Comparer
	{
	/**
	 * Returns an integer that is less than, equal to, 
	 * or greater than zero. The integer's value depends on whether 
	 * leftOperand is less than, equal to, or greater than rightOperand. 
	 * @exception ClassCastException If operand objects are not proper type.
	 *  ClassCastException is a RuntimeException, so compiler does not force you
	 *  to catch this exception.
	 */
	final public int compare( Object leftOperand, Object rightOperand )
		throws ClassCastException
		{
		if ( lessThan( leftOperand, rightOperand ) )
			return -1;
		else if ( greaterThan( leftOperand, rightOperand ) )
			return 1;
		else
			return 0;
		}
	
	}