/* $Id: HistoryDataCountComparer.java,v 1.3 1998/12/19 21:24:18 boyns Exp $ */

/*
 * Copyright (C) 1996-98 Mark R. Boyns <boyns@doit.org>
 *
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
package org.doit.muffin.filter;

import sdsu.compare.Comparer;

class HistoryDataCountComparer extends Comparer
{
    private static Comparer singleInstance;
	
    private HistoryDataCountComparer() {}	
	
    public static Comparer getInstance()
    {
	if (singleInstance == null)
	{
	    singleInstance = new HistoryDataCountComparer();
	}
	return singleInstance;
    }
		
    public final boolean lessThan(Object leftOperand, Object rightOperand)  
	throws ClassCastException
    {
	return((HistoryData)leftOperand).count > ((HistoryData)rightOperand).count;
    }

    public final boolean greaterThan(Object leftOperand, Object rightOperand) 
	throws ClassCastException
    {
	return((HistoryData)leftOperand).count < ((HistoryData)rightOperand).count;
    }

    public final boolean equals(Object leftOperand, Object rightOperand) 
	throws ClassCastException
    {
	return((HistoryData)leftOperand).count == ((HistoryData)rightOperand).count;
    }
}


