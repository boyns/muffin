package UK.co.demon.asmodeus.util;

import java.lang.*;
import java.io.*;
import java.util.*;

//
// $Log: MultiSearchResult.java,v $
// Revision 1.1.1.1  1998/07/14 22:53:24  mrb
// start
//
// Revision 2.2  1997/06/13 11:34:41  joel
// Added more documentation.
//
// Revision 2.1  1997/06/02 21:32:50  joel
// Removed 'final' from the RCS ID to allow subclasses under RCS control.
//
// Revision 2.0  1997/06/02 21:27:50  joel
// This is the first version after the rewrite for the parallel search.
//
//

/**
 * This class is the result returned by the MultiSearch.search function 
 * It consists of the term matched and the position in the string at which
 * the match occured 
 * @author Joel Crisp   
 * @version $Id: MultiSearchResult.java,v 1.1.1.1 1998/07/14 22:53:24 mrb Exp $
 * @see MultiSearch
 */
public class MultiSearchResult 
  {
  public static String RCSID="$Id: MultiSearchResult.java,v 1.1.1.1 1998/07/14 22:53:24 mrb Exp $";

  /**
   * the offset into the search data where this match starts
   */
  protected int offset; 

  /**
   * the String representation of the matched characters 
   */   
  protected String match; 
 
  /** 
   * Constructor for a MultiSearchResult with the offset and the match 
   * @param offset the location of the match in the input stream
   * @param match the String representation of the match
   */
  public MultiSearchResult(int offset,String match) 
    {
    this.offset=offset;
    this.match=match;
    }
  
  /**
   * Formatter function for this object 
   * @return a String representation of this object's data
   */
  public String toString() { return match+"@"+offset; }
 
  /**
   * accessor function for the offset of this result in the string
   * @returns the offset of the result in the input stream
   */
  public int getOffset() { return offset; }
  
  /**
   * accessor function for the match itself
   * @return the String representation of the match
   */
  public String getMatch() { return match; }
  }
