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

import java.awt.Point;
import java.util.Vector;
import java.util.Enumeration;
import java.io.StringBufferInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * This class is a two dimensional table. Indexing of rows and columns starts at 0. 
 * The table is growable. Surplus rows and columns are allocated to reduce cost of 
 * growing the table. Surplus rows/columns can not be accessed until rows or columns are 
 * added. Table can be converted from/to string/streams. In string representation default
 * column separator is ',' and default row separator is ';'.
 * The recreated table object will only contain string representations of the original
 * elements in the table.
 *
 * @see		SimpleTokenizer
 * @see		Stringizer
 * @see		TokenCharacters
 * @version 0.9 1 March 1997 
 * @author Roger Whitney (<a href=mailto:whitney@cs.sdsu.edu>whitney@cs.sdsu.edu</a>)
 */


public class Table implements Cloneable
	{
	// Implementation notes
	// Still needs indexing by labels, growing policy needs work
	// Use of java.awt.Point should be replaced
	// Add synchronization, cloning

	private static final String  NEW_LINE = "\n";

	/**
	 * Character (defaults to ';') used to separate rows in ascii representation of table.
	 */
	private char  rowSeparatorChar  	= ';';

	/**
	 * Character (defaults to ',') used to separate columns in ascii representation of table.
	 */
	private char  columnSeparatorChar  	= ',';
	Object[][]  tableElements;
	private int numberOfRows;
	private int numberOfColumns;
	private int capacityIncrement = 10;
	
	private TokenCharacters parseTable = new TokenCharacters(
		String.valueOf( rowSeparatorChar) + String.valueOf(columnSeparatorChar));


	/**
	 * Create a new table with no rows and no columns
	 */
	public Table( )
		{
		this( 0, 0 );
		}

	/**
	 * Create a new table with given number of rows and columns. All elements are 
	 * set to null.
	 */
	public Table( int  initNumberOfRows, int initNumberOfColumns )
		{
		numberOfRows = initNumberOfRows;
		numberOfColumns = initNumberOfColumns;	   
		tableElements = new Object[ numberOfRows + capacityIncrement ]
											[ numberOfColumns + capacityIncrement ];
		}

	/**
	 * Create a table using the vector row as the only row in the table
	 */ 
	public static Table fromRowVector( Vector row )
		{
		Table newTable = new Table( 1, row.size() );
		for ( int column = 0; column < row.size(); column++ )
			newTable.tableElements[0][column] = row.elementAt( column);
		return newTable;
		}

	/**
	 * Create a table using the vector column as the only column in the table
	 */ 
	public static Table fromColumnVector( Vector column )
		{
		Table newTable = new Table( column.size(), 1 );
		for ( int row = 0; row < column.size(); row++ )
			newTable.tableElements[row][0] = column.elementAt( row);
		return newTable;
		}
		
	/**
	 * Sets TokenCharacters used to convert Table from/to strings/streams.
	 * Current values for rowSeparatorChar and columnSeparatorChar override separator 
	 * settings in TokenCharacters object.
	 */
	public void setTokenCharacters( TokenCharacters newParseTable )
		{
		parseTable = newParseTable;
		upDateParseTable();
		}

	/**
	 * Set character used to separate rows in String representation of a Table
	 */
	public void setRowSeparatorChar( char rowSeparatorChar )
		{
		this.rowSeparatorChar = rowSeparatorChar;
		upDateParseTable();
		}

	/**
	 * Set character used to separate rows in String representation of a Table
	 */
	public void setColumnSeparatorChar( char columnSeparatorChar )
		{
		this.columnSeparatorChar = columnSeparatorChar;
		upDateParseTable();
		}

	/**
	 * Searches for the specified object, in row order. 
	 * @return java.awt.Point x is row location, y is column location, -1 indicates object not found
	 */
	public synchronized Point indexOf( Object element)
		{
		for ( int row = 0 ; row < numberOfRows; row++ )
			for ( int column = 0 ; column < numberOfColumns; column++ )
				if ( element.equals(tableElements[ row ][ column ] ) )
					return new Point( row, column );
		return new Point( -1, -1 );
		}

	/**
	 * Returns true if the specified object is in the table
	 */
	public boolean contains( Object element )
		{
		if ( indexOf( element ).x == -1 )
			return false;
		else
			return true;
		}

   /**
     * Clones this table. The elements are <strong>not</strong> cloned.
     */
	public synchronized Object clone()
		{
		// copied code from Vector. Don't know why it is important to call clone
		try 
			{ 
		    Table clone = (Table)super.clone();
		    int actualNumberOfRows = tableElements.length;
		    int actualNumberOfColumns = tableElements[0].length;
		    
		    clone.tableElements = new Object[actualNumberOfRows][actualNumberOfColumns];

			for ( int row = 0; row < numberOfRows; row++)
				System.arraycopy( tableElements[row], 0, 
										clone.tableElements[row], 0,
										numberOfColumns );

		    return clone;
			} 
		catch (CloneNotSupportedException impossible) 
			{ 
	    	// this shouldn't happen, since we are Cloneable
	    	throw new InternalError();
			}
		}
				
	/**
	 * Returns the object in table at given row and column. 
	 * @exception java.lang.ArrayIndexOutOfBoundsException thrown if 
	 * row or column is not a valid index
	 */
	public synchronized Object  elementAt( int row,  int column ) throws ArrayIndexOutOfBoundsException 
		{
		checkBounds( row, column );
		return  tableElements[ row ][ column ];
		}


	/**
	 * Returns the given row in the table
	 * @exception java.lang.ArrayIndexOutOfBoundsException thrown if 
	 * row is not a valid index
	 */
	public synchronized Vector  rowAt( int row )  throws ArrayIndexOutOfBoundsException 
		{
		checkBounds( row, 0 );
		Vector  rowCopy  =  new Vector();

		for  ( int column = 0;  column < numberOfColumns;  column++ ) 
			rowCopy.addElement( tableElements[ row ][ column ] );
		return  rowCopy;
		}
	
	/**
	 * Returns the given column in the table
	 * @exception java.lang.ArrayIndexOutOfBoundsException thrown if 
	 * column is not a valid index
	 */
	public synchronized Vector  columnAt( int column ) throws ArrayIndexOutOfBoundsException 
		{
		checkBounds( 0, column );
		Vector  columnCopy  =  new Vector();

		for  ( int row = 0;  row < numberOfRows();  row++ ) 
			columnCopy.addElement( tableElements[ row ][ column ] );
		return  columnCopy;
		}

	/**
	 * Creates a new table with the specified columns of current table
	 * @exception java.lang.ArrayIndexOutOfBoundsException thrown if 
	 * startColumn or endColumn is not a valid column index
	 */
	public synchronized Table columnsAt( int startColumn, int endColumn ) throws ArrayIndexOutOfBoundsException
		{
		Table slice = new Table( numberOfRows, 0 );

		for ( int column = startColumn; column <= endColumn; column++ )
			slice.addColumn( columnAt( column ));
			
		return slice;
		}

	/**
	 * Creates a new table containing all the columns that have the value key in the indicated row
	 * @exception java.lang.ArrayIndexOutOfBoundsException thrown if 
	 * row is not a valid row index
	 */
	public synchronized Table columnsAt( Object key, int row ) throws ArrayIndexOutOfBoundsException
		{
		int columnsToAdd = 0;
		
		// table growth is currently costly, determine final size
		for ( int column = 0; column < numberOfColumns; column++ )
			if ( tableElements[row][column].equals( key ) )
				columnsToAdd++;

		Table slice = new Table( 0, 0 );
		slice.insureCapacity( numberOfRows, columnsToAdd );

		for ( int column = 0; column < numberOfColumns; column++ )
			if ( tableElements[row][column].equals( key ) )
				slice.addColumn( columnAt( column ));
			
		return slice;
		}

	/**
	 * Creates a new table with the specified rows of current table
	 * @exception java.lang.ArrayIndexOutOfBoundsException thrown if 
	 * startRow or endRow is not a valid row index
	 */
	public synchronized Table rowsAt( int startRow, int endRow ) throws ArrayIndexOutOfBoundsException
		{
		Table slice = new Table( endRow - startRow + 1, numberOfColumns);

		int sliceRow = 0;
		for ( int row = startRow; row <= endRow; row++ )
			System.arraycopy( tableElements[row], 0, slice.tableElements[sliceRow++], 0, numberOfColumns );
		
		return slice;
		}

	/**
	 * Returns a new table containing all the rows that have the value key in the indicated column
	 * @exception java.lang.ArrayIndexOutOfBoundsException thrown if 
	 * column is not a valid index
	 */
	public synchronized Table rowsAt( Object key, int column ) throws ArrayIndexOutOfBoundsException
		{
		int rowsToAdd = 0;
		
		// table grow is currently costly, determine final size
		for ( int row = 0; row < numberOfRows; row++ )
			if ( tableElements[row][column].equals( key ) )
				rowsToAdd++;
				
		Table slice = new Table( rowsToAdd, numberOfColumns);

		int sliceRow = 0;
		for ( int row = 0; row < numberOfRows; row++ )
			if ( tableElements[row][column].equals( key ) )
				System.arraycopy( tableElements[row], 0, slice.tableElements[sliceRow++], 0, numberOfColumns );

		return slice;
		}
		
		
	/**
	 * Returns an enumeration of the non-null elements in row order.
	 */
	public synchronized Enumeration elements()
		{
		Vector elements = new Vector( numberOfColumns * numberOfRows);
		
		for ( int row = 0 ; row < numberOfRows; row++ )
			for ( int column = 0 ; column < numberOfColumns; column++ )
				if ( tableElements[ row ][ column ] != null )
					elements.addElement( tableElements[ row ][ column ] );
		
		return elements.elements();
		}
		

	/**
	 * Places the object in table at given row and column
	 * @exception java.lang.ArrayIndexOutOfBoundsException thrown if 
	 * row or column is not a valid index
	 */
	public synchronized void  setElementAt( Object data, int row,  int column ) throws ArrayIndexOutOfBoundsException 
		{
		checkBounds( row, column );
		tableElements[ row ][ column ] = data;
		}
	
	/**
	 * Add a new row to the end of the Table. If newRow has more
	 * elements than the table has columns, more columns are added to the table.
	 * @param newRow vector of item added to the new row
	 */
	public synchronized void addRow( Vector  newRow )
		{
		Table toAdd = Table.fromRowVector( newRow);
		addRows( toAdd );
		}

	/**
	 * Add table "newRows" at the bottom of the Table. Table grows to include
	 * all elements of newRows
	 * @param newRows table to add to bottom 
	 */
	public synchronized void addRows( Table  newRows )
		{
		insertRowsAt( newRows, numberOfRows );
		}

	/**
	 * Insert table "newRows" in the Table. "newRows" is inserted at row "startIndex" of
	 * current table. The row "startIndex" prior to the insert will follow the newly added rows
	 * Table grows to include all elements of newRows
	 * @param startIndex the first row of newRows becomes row startIndex in current table  
	 * @exception java.lang.ArrayIndexOutOfBoundsException thrown if 
	 * startIndex is not a valid row index
	 */
	public synchronized void insertRowsAt(Table  newRows, int startIndex ) throws ArrayIndexOutOfBoundsException
		{
		if ( startIndex > numberOfRows) 
			throw new ArrayIndexOutOfBoundsException("inserting rows beyond end of the table");
			
		// new rows might be longer than table
		int columnsToAdd = Math.max( newRows.numberOfColumns() - numberOfColumns, 0 );
		int rowsToAdd = newRows.numberOfRows();
		insureCapacity( rowsToAdd, columnsToAdd  );

		if ( startIndex < numberOfRows )
			{
			//Copy end down to make room
			System.arraycopy( tableElements, startIndex, 
										tableElements, startIndex + rowsToAdd, 
										numberOfRows - startIndex );

			//Remove duplicate row pointers
			int actualRowLength = tableElements[0].length;
			for ( int k = startIndex; k < startIndex + rowsToAdd; k++ )
				tableElements[k] = new Object[actualRowLength];
			}
			
		for ( int k = 0; k < newRows.numberOfRows();k++ )
			{
			System.arraycopy( newRows.tableElements[k], 0, 
									tableElements[k + startIndex], 0, 
									newRows.numberOfColumns() );
			}
		numberOfColumns = numberOfColumns + columnsToAdd;
		numberOfRows = numberOfRows + rowsToAdd;

		}

	/**
	 * Add a new column to the end of the Table. If newColumn has more
	 * elements than the table has rows, more rows are added to the table.
	 * @param newColumn vector of item added to the new column
	 */
	public synchronized void addColumn( Vector  newColumn )
		{
		Table toAdd = Table.fromColumnVector( newColumn);
		addColumns( toAdd );
		}

	/**
	 * Add a table as new columns. 	 
	 * @param newColumns table to add 
	 */
	public synchronized void addColumns( Table  newColumns )
		{
		insertColumnsAt( newColumns, numberOfColumns);
		}

	/**
	 * Insert table "newColumns" in the Table. "newColumns" is inserted at column "startIndex" of
	 * current table. The column "startIndex" prior to the insert will follow the newly added columns
	 * Table grows to include all elements of newColumns
	 * @param startIndex the first column of newColumns becomes column startIndex in current table  
	 * @exception java.lang.ArrayIndexOutOfBoundsException thrown if 
	 * startIndex is not a valid column index
	 */
	public synchronized void insertColumnsAt(Table  newColumns, int startIndex ) throws ArrayIndexOutOfBoundsException
		{
		if ( startIndex > numberOfColumns ) 
			throw new ArrayIndexOutOfBoundsException("inserting columns beyond end of the table");
			
		// new column might be taller than table
		int rowsToAdd = Math.max( newColumns.numberOfRows() - numberOfRows, 0 );
		int columnsToAdd = newColumns.numberOfColumns();
		insureCapacity( rowsToAdd, columnsToAdd  );

		if ( startIndex < numberOfColumns )
			{
			//Copy rows over
			for ( int row =0; row < numberOfRows; row++ )
				System.arraycopy( tableElements[row], startIndex, 
										tableElements[row], startIndex + columnsToAdd, 
										numberOfColumns - startIndex );
			}


		for ( int row = 0; row < numberOfRows + rowsToAdd; row++ )
			for ( int column = 0; column < columnsToAdd; column++ )
				tableElements[row][column + startIndex] = newColumns.tableElements[row][column];	
		
		numberOfColumns = numberOfColumns + columnsToAdd;
		numberOfRows = numberOfRows + rowsToAdd;

		}

	/**
	 * Removes the indicated row from the table
	 * @exception java.lang.ArrayIndexOutOfBoundsException thrown if 
	 * rowIndex is not a valid row index
	 */
	public synchronized void removeRowAt( int rowIndex ) throws ArrayIndexOutOfBoundsException
		{
		removeRowsAt( rowIndex, rowIndex);
		}

	/**
	 * Removes the indicated rows from the table
	 * @exception java.lang.ArrayIndexOutOfBoundsException thrown if 
	 * firstRow > endRow, or either firstRow or endRow are not a valid row index 
	 */
	public synchronized void removeRowsAt( int firstRow, int endRow ) throws ArrayIndexOutOfBoundsException
		{
		if (( endRow < firstRow) 		||
			 ( firstRow < 0 )				||
			 ( endRow < 0 )				||
			 ( endRow > numberOfRows )	||
			 ( firstRow > numberOfRows )
			)
			throw new ArrayIndexOutOfBoundsException();
		
		if ( endRow < numberOfRows )
			// copy bottom part over rows to remove
			System.arraycopy( tableElements, endRow + 1, 
									tableElements, firstRow, 
									numberOfRows - endRow );

		numberOfRows = numberOfRows - (endRow - firstRow) - 1;
		}

	/**
	 * Removes the indicated rows that have key in the indicated column
	 * @param key the key to be searched for
	 * @param column column in table to search for the key
	 * @exception java.lang.ArrayIndexOutOfBoundsException thrown if 
	 * column is not a valid column index 
	 * @exception java.lang.NullPointerException thrown if key is null
	 */
	public synchronized void removeRowsAt( Object key, int column ) 
										throws ArrayIndexOutOfBoundsException, NullPointerException
		{
		for ( int row = 0; row < numberOfRows; row++ )
			if ( key.equals( elementAt( row, column) ) )
				removeRowsAt( row, row );
		}
		
	/**
	 * Removes the indicated column from the table
	 * @exception java.lang.ArrayIndexOutOfBoundsException thrown if 
	 * columnIndex is not a valid column index 
	 */
	public synchronized void removeColumnAt( int columnIndex ) throws ArrayIndexOutOfBoundsException
		{
		removeColumnsAt( columnIndex, columnIndex );
		}

	/**
	 * Removes the indicated columns from the table
	 * @exception java.lang.ArrayIndexOutOfBoundsException thrown if 
	 * firstColumn > endColumn, or either firstColumn or endColumn are not a valid column index 
	 */
	public synchronized void removeColumnsAt( int firstColumn, int endColumn ) throws ArrayIndexOutOfBoundsException
		{
		if (( endColumn < firstColumn) 		||
			 ( firstColumn < 0 )				||
			 ( endColumn < 0 )				||
			 ( endColumn > numberOfColumns )	||
			 ( firstColumn > numberOfColumns )
			)
			throw new ArrayIndexOutOfBoundsException();
		
		if ( endColumn < numberOfColumns )
			// copy bottom part over columns to remove
			for ( int row = 0; row < numberOfRows; row++ )
				System.arraycopy( tableElements[row], endColumn + 1, 
										tableElements[row], firstColumn, 
										numberOfColumns - endColumn);

		numberOfColumns = numberOfColumns - (endColumn - firstColumn) - 1;
		}

	/**
	 * Removes the indicated columns that have key in the indicated row
	 * @param key the key to be searched for
	 * @param row row in table to search for the key
	 * @exception java.lang.ArrayIndexOutOfBoundsException thrown if 
	 * row is  not a valid row index 
	 * @exception java.lang.NullPointerException thrown if key is null
	 */
	public synchronized void removeColumnsAt( Object key, int row ) 
										throws ArrayIndexOutOfBoundsException, NullPointerException
		{
		for ( int column = 0; column < numberOfColumns; column++ )
			if ( key.equals( elementAt( row, column) ) )
				removeColumnsAt( column, column );
		}
		
	/**
	 * Return the number of items in the table
	 */
	public int size()
		{
		return numberOfRows * numberOfColumns;
		}
				
	/**
	 * Returns the number of columns that can currently be accessed
	 */
	public  int  numberOfColumns()
		{
		return numberOfColumns;
		}

	/**
	 * Returns the number of rows that can currently be accessed
	 */
	public  int  numberOfRows()
		{
		return numberOfRows;
		}

    /**
     * Converts a Table to a String.
     */
	public  String  toString()
		{
		return toString( null );
		}
		
    /**
     * Converts a Table to a String with given header information.
     */
	public synchronized  String  toString( String header)
		{
		Stringizer buffer = new Stringizer( parseTable );
		buffer.setHeader( header );
		String rowSeparator = String.valueOf( rowSeparatorChar ) + NEW_LINE;

		for  ( int row  =  0; row < numberOfRows;  row++ ) 
			{
			for ( int col = 0; col < numberOfColumns-1; col++ )
				buffer.appendToken( elementToString( row, col ), columnSeparatorChar );	
			buffer.appendToken( elementToString( row, numberOfColumns - 1 ), rowSeparator );
			}
		return buffer.toString();
		}


	/**
	 * Writes ascii representation of a Table to Outputstream 
	 */
	 public synchronized void save( OutputStream out, String header )
	 	{
	 	PrintStream printOut = new PrintStream( out );
	 	printOut.println( toString( header ) );
	 	printOut.flush();
	 	}

	/**
	 * Loads a Table from an inputstream. Data in stream must be in Table format.
	 * @exception java.lang.IOException thrown if there is a error in reading 
	 */
	 public synchronized void load( InputStream in ) throws IOException
	 	{
		SimpleTokenizer parser;
		parser = new SimpleTokenizer( in, parseTable );
				
		Vector row = new Vector();
		while ( parser.hasMoreTokens() )
			{
			row.addElement( parser.nextToken( ) );
			
			if ( parser.separator() == rowSeparatorChar )
				{
				addRow( row );
				row.removeAllElements();
				}
			}
		if ( row.size() > 0 )
			addRow( row );
	 	}
	 	
	/**
	 * Loads a Table from a string. Data in stream must be in Table format.
	 * @exception java.lang.IOException thrown if there is a error reading the 
	 * tableString format
	 */
	public synchronized void  fromString(  String  tableString ) throws IOException
		{
		load( new StringBufferInputStream( tableString ) );
		}


	/**
	 * Returns the number of rows that can be added before the table needs to grow.
	 */
	private  int  surplusRows()
		{
		return tableElements.length - numberOfRows;
		}


	/**
	 * Returns the number of columns that can be added before the table needs to grow.
	 */
	private  int  surplusColumns()
		{
		return  tableElements[0].length - numberOfColumns;
		}


	private  String  elementToString( int row, int column )
		{
		if ( tableElements[ row ][ column ] == null )
			return "";
		else
			return tableElements[ row ][ column ].toString();
		}


	private void insureCapacity( int rowsToAdd,  int columnsToAdd  )
		{
		if (  ( rowsToAdd  <= surplusRows() ) &&  ( columnsToAdd <= surplusColumns() ) )
			return;
		
		int newNumberOfRows;
		int newNumberOfColumns;
		
		// Need better policy, linear growth is slow
		if ( rowsToAdd  > surplusRows() )
			newNumberOfRows = numberOfRows + capacityIncrement + rowsToAdd;
		else
			newNumberOfRows = tableElements.length;//current number of rows

		if ( columnsToAdd  > surplusColumns() )
			newNumberOfColumns = numberOfColumns + capacityIncrement + columnsToAdd;
		else
			newNumberOfColumns = tableElements[0].length;//current number of columns

		growCapacityTo( newNumberOfRows, newNumberOfColumns );
		}
		
	private void growCapacityTo( int newNumberOfRows, int newNumberOfCols )
		{
		Object[][] newTable = new Object[ newNumberOfRows ][ newNumberOfCols ];
		
		for ( int row = 0; row < numberOfRows; row++)
			System.arraycopy( tableElements[row], 0, newTable[row], 0, numberOfColumns );

		tableElements = newTable;
		}
	
	private void checkBounds( int row, int column ) throws ArrayIndexOutOfBoundsException 
		{
		if ( row >= numberOfRows  )
			throw new ArrayIndexOutOfBoundsException( "Row index: " + row + " >= " + numberOfRows);

		if ( column >= numberOfColumns )
			throw new ArrayIndexOutOfBoundsException( "Column index: " + column +
																				 " >= " + numberOfColumns );
		}

	private void upDateParseTable()
		{
		parseTable.setSeparatorChars( String.valueOf( columnSeparatorChar ) +
									String.valueOf( rowSeparatorChar )
								);
		}

	} //End class
