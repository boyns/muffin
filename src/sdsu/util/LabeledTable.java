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

/**
 * This class is a two dimensional table that permits indexing via labels. Labels are taken from
 * the first row and column. If the contents of LabeledTable example is: <br> <br>
 * <TABLE BORDER>
 * <TR VALIGN=top><TD>Name</TD><TD>Pay</TD><TD>Department</TD></TR>
 * <TR VALIGN=top><TD>Joe</TD><TD>$100.00</TD><TD>Math</TD></TR>
 * <TR VALIGN=top><TD>Sally</TD><TD>$500.00</TD><TD>Physics</TD></TR>
 * </TABLE> <br> <br>
 * then example.elementAt( "Sally", "Pay") returns $500.00. 
 * If row (column) labels contain duplicates, only the first instance is accessed by label indexing.
 * When creating subtables (via rowAt, rowsAt, columnAt
 * columnsAt) labels from original table are copied to subtable. While changing individual elements 
 * via labels is supported, adding additional rows and columns via labels is not yet supported. Use addRow and
 * addColumn from Table superclass.
 * 
 * @see		SimpleTokenizer
 * @see		Stringizer
 * @see		TokenCharacters
 * @see		Table
 * @version 0.8 1 March 1997 
 * @author Roger Whitney (<a href=mailto:whitney@cs.sdsu.edu>whitney@cs.sdsu.edu</a>)
 */
public class LabeledTable extends Table
	{
	// The class was designed to allow the column/row used as index to change, but 
	// this feature is not yet implemented
	
	private int columnLabelIndex = 0;  		// the row used as labels for column access
	private int rowLabelIndex = 0;			// the column used as labels for row access

	/**
	 * Create a new LabeledTable with no rows and no columns
	 */
	public LabeledTable( )
		{
		this( 0, 0 );
		}

	/**
	 * Create a new LabeledTable with given number of rows and columns. All elements are 
	 * set to null.
	 */
	public LabeledTable(  int  initNumberOfRows, int initNumberOfColumns )
		{
		super( initNumberOfRows, initNumberOfColumns );
		}
		
	/**
	 * Create a LabeledTable from a table
	 */	
	public LabeledTable( Table contents )
		{
		super( contents.numberOfRows(), contents.numberOfColumns() );
		for ( int row = 0; row < contents.numberOfRows(); row++ )
			System.arraycopy( contents.tableElements[row], 0, 
										tableElements[row], 0, 
										contents.numberOfColumns() );
		}
		
   /**
     * Clones this labledTable. The elements are <strong>not</strong> cloned.
     */
	public synchronized Object clone()
		{
		return (LabeledTable) super.clone();
		}

	/**
	 * Returns the object in table at given row and column. Column is indicated by label object.
	 * Returns null if column label is not found
	 * @exception java.lang.ArrayIndexOutOfBoundsException thrown if row or columnLabel is not
	 * a valid index
	 */
	public synchronized  Object  elementAt( int row,  Object columnLabel ) throws ArrayIndexOutOfBoundsException 
		{
		return  super.elementAt( row, columnLabelToIndex( columnLabel ) );
		}

	/**
	 * Returns the object in table at given row and column. row is indicated by label object.
	 * Returns null if row label is not found
	 * @exception java.lang.ArrayIndexOutOfBoundsException thrown if rowlabel or columnLabel is not
	 * a valid index
	 */
	public synchronized  Object  elementAt( Object rowLabel,  Object columnLabel ) throws ArrayIndexOutOfBoundsException 
		{
		return elementAt( rowLabelToIndex( rowLabel ), columnLabel );
		}

	/**
	 * Returns the object in table at given row and column. row is indicated by label object.
	 * Returns null if row label is not found
	 * @exception java.lang.ArrayIndexOutOfBoundsException thrown if rowlabel or column is not
	 * a valid index
	 */
	public synchronized  Object  elementAt( Object rowLabel,  int column ) throws ArrayIndexOutOfBoundsException 
		{
		return  super.elementAt( rowLabelToIndex( rowLabel ), column );
		}


	/**
	 * Returns the column in the table indicated by the label
	 * Returns null if column label is not found
	 *	Rows labels of current labeledTable are copied to new LabeledTable
	 * @exception java.lang.ArrayIndexOutOfBoundsException thrown if is not
	 * a valid index
	 */
	public synchronized  LabeledTable  columnAt( Object columnLabel ) throws ArrayIndexOutOfBoundsException 
		{
		Table column;

		if ( columnLabelToIndex( columnLabel ) != rowLabelIndex )
			column = super.columnsAt( rowLabelIndex, rowLabelIndex );
		else
			column = new Table();
		
		Vector columnVector = super.columnAt( columnLabelToIndex( columnLabel ) );

		column.addColumn( columnVector );	
		return new LabeledTable( column );
		}

	/**
	 * Creates a new LabeledTable containing all the columns that have the 
	 * value key in the indicated row. 
	 *	Rows labels of current labeledTable are copied to new LabeledTable
	 * @exception java.lang.ArrayIndexOutOfBoundsException thrown if rowlabel is not
	 * a valid index
	 */
	public synchronized LabeledTable columnsAt( Object key, Object rowLabel ) throws ArrayIndexOutOfBoundsException
		{
		Table columns;

		//Get row labels if they are not the column selective
		Object label = elementAt( rowLabelToIndex( rowLabel ), rowLabelIndex );
		if ( !label.equals( key ) )
			columns = super.columnsAt( rowLabelIndex, rowLabelIndex );
		else
			columns = new Table();
		
		Table columnsSelected = super.columnsAt( key, rowLabelToIndex( rowLabel ));

		columns.addColumns( columnsSelected );	
		return new LabeledTable( columns );
		}

	/**
	 * Creates a new labeledTable with the specified columns of current labeledTable
	 *	Rows labels of current labeledTable are copied to new LabeledTable
	 * @return a labeledTable, inheritance forces to declare return type as table
	 * @exception java.lang.ArrayIndexOutOfBoundsException thrown if startColumn or endColumn is not
	 * a valid index
	 */
	public synchronized Table columnsAt( int startColumn, int endColumn ) throws ArrayIndexOutOfBoundsException
		{
		Table columns;

		//Get column labels if they are not the rows selectived
		if ( ( startColumn <= rowLabelIndex) && ( rowLabelIndex <= endColumn) )
			columns = new Table();	
		else
			columns = super.columnsAt( rowLabelIndex, rowLabelIndex );
			
		Table columnsSelected = super.columnsAt( startColumn, endColumn );

		columns.addColumns( columnsSelected );	
		return new LabeledTable( columns );
		}

	/**
	 * Insert table "newColumns" in the Table. "newColumns" is inserted at column "startColumnLabel" of
	 * current table. The column "startIndex" prior to the insert will follow the newly added columns
	 * Table grows to include all elements of newColumns
	 * @exception java.lang.ArrayIndexOutOfBoundsException thrown ifstartColumnLabel is not
	 * a valid index
	 */
	public synchronized void insertColumnsAt(Table  newColumns, Object startColumnLabel ) throws ArrayIndexOutOfBoundsException
		{
		super.insertColumnsAt( newColumns, columnLabelToIndex( startColumnLabel ) );
		}
		
	/**
	 * Removes the indicated column from the table
	 * @exception java.lang.ArrayIndexOutOfBoundsException thrown if columnLabel is not
	 * a valid index
	 */
	public synchronized void removeColumnAt( Object columnLabel ) throws ArrayIndexOutOfBoundsException
		{
		super.removeColumnAt( columnLabelToIndex( columnLabel ) );
		}

	/**
	 * Removes the indicated columns that have key in the indicated row
	 * @param key the key to be searched for
	 * @param row row in table to search for the key
	 * @exception java.lang.ArrayIndexOutOfBoundsException thrown if 
	 * row is not a valid index 
	 * @exception java.lang.NullPointerException thrown if key is null
	 */
	public synchronized void removeColumnsAt( Object key, Object rowLabel ) 
										throws ArrayIndexOutOfBoundsException, NullPointerException

		{
		super.removeColumnsAt( key, rowLabelToIndex( rowLabel ) );
		}

	/**
	 * Insert table "newRows" in the Table. "newRows" is inserted at row "startRowLabel" of
	 * current table. The row "startIndex" prior to the insert will follow the newly added rows
	 * Table grows to include all elements of newRows
	 * @exception java.lang.ArrayIndexOutOfBoundsException thrown if rowlabel is not
	 * a valid index
	 */
	public synchronized void insertRowsAt(Table  newRows, Object rowLabel ) throws ArrayIndexOutOfBoundsException
		{
		super.insertRowsAt( newRows, rowLabelToIndex( rowLabel ) );
		}
		
	/**
	 * Removes the indicated row from the table
	 * @exception java.lang.ArrayIndexOutOfBoundsException thrown if 
	 * rowlabel is not a valid index
	 */
	public synchronized void removeRowAt( Object rowLabel ) throws ArrayIndexOutOfBoundsException
		{
		super.removeRowAt( rowLabelToIndex( rowLabel ));
		}

	/**
	 * Removes the indicated rows that have key in the indicated column
	 * @param key the key to be searched for
	 * @param column column in table to search for the key
	 * @exception java.lang.ArrayIndexOutOfBoundsException thrown if 
	 * columnLabel is not a valid index
	 * @exception java.lang.NullPointerException thrown if key is null
	 */
	public synchronized void removeRowsAt( Object key, Object columnLabel ) 
										throws ArrayIndexOutOfBoundsException, NullPointerException
		{
		super.removeRowsAt( key, columnLabelToIndex( columnLabel ));
		}

	/**
	 * Returns the column in the table indicated by the label
	 * Returns null if column label is not found
	 * @exception java.lang.ArrayIndexOutOfBoundsException thrown if rowLabel is not a valid index
	 */
	public synchronized  LabeledTable  rowAt( Object rowLabel ) throws ArrayIndexOutOfBoundsException 
		{
		Table row;

		//Get column labels if they are not the row selective
		if ( rowLabelToIndex( rowLabel ) != columnLabelIndex )
			row = super.rowsAt( columnLabelIndex, columnLabelIndex );
		else
			row = new Table();
		
		Vector rowVector = super.rowAt(  rowLabelToIndex( rowLabel )  );

		row.addRow( rowVector );	
		return new LabeledTable( row );
		}

	/**
	 * Creates a new table with the specified rows of current table
	 * @exception java.lang.ArrayIndexOutOfBoundsException thrown if row or column is not
	 * a valid index
	 */
	public synchronized Table rowsAt( int startRow, int endRow ) throws ArrayIndexOutOfBoundsException
		{
		Table rows;

		//Get column labels if they are not the rows selectived
		if ( ( startRow <= columnLabelIndex) && ( columnLabelIndex <= endRow) )
			rows = new Table();	
		else
			rows = super.rowsAt( columnLabelIndex, columnLabelIndex );
			
		Table rowsSelected = super.rowsAt( startRow, endRow );

		rows.addRows( rowsSelected );	
		return new LabeledTable( rows );
		}
		

	/**
	 * Returns a new table containing all the rows that have the value key in the indicated column
	 * @exception java.lang.ArrayIndexOutOfBoundsException thrown if columnLabel is not
	 * a valid index
	 */
	public synchronized LabeledTable rowsAt( Object key, Object columnLabel ) throws ArrayIndexOutOfBoundsException
		{
		Table rows;

		//Get column labels if they are not the row selective
		Object label = elementAt( columnLabelIndex, columnLabelToIndex( columnLabel ) );
		if ( !label.equals( key ) )
			rows = super.rowsAt( columnLabelIndex, columnLabelIndex );
		else
			rows = new Table();
		
		Table rowsSelected = super.rowsAt( key, columnLabelToIndex( columnLabel ));

		rows.addRows( rowsSelected );	
		return new LabeledTable( rows );
		}

	/**
	 * Places the object in table at given row and column
	 * @exception java.lang.ArrayIndexOutOfBoundsException thrown if rowLabel or column is not
	 * a valid index
	 */
	public synchronized  void  setElementAt( Object data,  Object rowLabel,  int column ) 
															throws ArrayIndexOutOfBoundsException 
		{
		super.setElementAt( data, rowLabelToIndex( rowLabel ), column );
		}

	/**
	 * Places the object in table at given row and column
	 * @exception java.lang.ArrayIndexOutOfBoundsException thrown if row or columnLabel is not
	 * a valid index
	 */
	public synchronized  void  setElementAt( Object data, int row,  Object columnLabel ) 
																throws ArrayIndexOutOfBoundsException 
		{
		super.setElementAt( data, row, columnLabelToIndex( columnLabel ) );
		}

	/**
	 * Places the object in table at given row and column
	 * @exception java.lang.ArrayIndexOutOfBoundsException thrown if rowLabel or columnLabel is not
	 * a valid index
	 */
	public synchronized  void  setElementAt( Object data, Object rowLabel,  Object columnLabel ) 
																throws ArrayIndexOutOfBoundsException 
		{
		setElementAt( data, rowLabel, columnLabelToIndex( columnLabel ) );
		}


	/**
	 * Convert column label to actual column index
	 */
	private int columnLabelToIndex( Object label )
		{
		if ( label == null ) 
			throw new ArrayIndexOutOfBoundsException( "Null labels not allowed");
		
		for ( int column = 0; column < numberOfColumns(); column++ )
			if ( label.equals( elementAt( columnLabelIndex, column) ))
				return column;
		
		throw new ArrayIndexOutOfBoundsException( "Column Label " + label + 
																"is not valid column label" );
		}		


	/**
	 * Convert row label to actual row index
	 */
	private int rowLabelToIndex( Object label )
		{
		if ( label == null ) 
			throw new ArrayIndexOutOfBoundsException( "Null labels not allowed");
		
		for ( int row = 0; row < numberOfRows(); row++ )
			if ( label.equals( elementAt( row, rowLabelIndex) ))
				return row;
		
		throw new ArrayIndexOutOfBoundsException( "Row Label " + label + 
																"is not valid row label" );
		}		

	}