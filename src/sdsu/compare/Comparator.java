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

interface Comparator
	{
	/**
	 * Returns an integer that is less than, equal to, 
	 * or greater than zero. The integer's value depends on whether 
	 * leftOperand is less than, equal to, or greater than rightOperand. 
	 */
	public int compare( Object leftOperand, Object rightOperand );
	
	/**
	 * Returns true if the leftOperand is less than the rightOperand.
	 */
	public boolean lessThan( Object leftOperand, Object rightOperand );

	/**
	 * Returns true if the leftOperand is greater than the rightOperand.
	 */
	public boolean greaterThan( Object leftOperand, Object rightOperand );

	/**
	 * Returns true if the leftOperand is equal to the rightOperand.
	 */
	public boolean equal( Object leftOperand, Object rightOperand );

	/**
	 * Returns true if the leftOperand is less than or equal to the rightOperand.
	 */
	public boolean lessThanOrEqual( Object leftOperand, Object rightOperand );

	/**
	 * Returns true if the leftOperand is greater than or equal the rightOperand.
	 */
	public boolean greaterThanOrEqual( Object leftOperand, Object rightOperand );
	}
