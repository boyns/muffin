package UK.co.demon.asmodeus.util;

import java.lang.*;
import java.io.*;
import java.util.*;

//
// $Log: FileLineEnumerator.java,v $
// Revision 1.1.1.1  1998/07/14 22:53:24  mrb
// start
//
// Revision 2.1  1997/06/13 11:34:26  joel
// Added more documentation
//
// Revision 2.0  1997/06/02 21:40:26  joel
// First version after moving over to the parallel version.
//
//

/**
 * Class FileLineEnumerator implements an Enumeration for reading each line
 * from a text file in sequence.
 * @author Joel Crisp
 * @version $Id: FileLineEnumerator.java,v 1.1.1.1 1998/07/14 22:53:24 mrb Exp $
 * @see java.util.Enumeration
 */

public class FileLineEnumerator extends LineNumberReader implements Enumeration
  {
  /**
   * Version tag. Internally accessable, RCS ID format. 
   */
  public static String RCSID="$Id: FileLineEnumerator.java,v 1.1.1.1 1998/07/14 22:53:24 mrb Exp $";

  /**
   * Constructor for a FileLineEnumerator which needs a Reader format source.
   * @param source Reader from which to get the file lines. Need not be a 
   *               LineNumberReader.
   */ 
  public FileLineEnumerator(Reader source)
    {
    super(source);
    }
    
   /**
    * Implementation of the hasMoreElements method from java.util.Eumeration
    * @see java.util.Enumeration#hasMoreElements
    * @return true if the end of the file has not yet been reached.
    */
   public boolean hasMoreElements() 
      {
      try 
        {
        return(ready());
        } 
      catch(IOException e)
        {
        return false; 
        }
      }

   /**
    * Implementation of the nextElement method from java.util.Enumeration
    * @see java.util.Enumeration#nextElement
    * @return An Object which is a String containing the next file line.
    */
   public Object nextElement() 
      {  
      try 
        {
        return(readLine());
        }
      catch(IOException e)
        {
        return null; 
        }
      
      }
  }
