package UK.co.demon.asmodeus.util;

import java.lang.*;
import java.io.*;
import java.util.*;

//
// $Log: MultiSearchReader.java,v $
// Revision 1.1.1.1  1998/07/14 22:53:24  mrb
// start
//
// Revision 2.3  1997/06/18 07:04:52  joel
// Now parsable with javacc Java1.1.1 grammar.
//
// Revision 2.2  1997/06/13 12:55:45  joel
// Terminated comment before flushAll.
//
// Revision 2.1  1997/06/13 11:34:00  joel
// Added more documentation, fixed two bugs which affected buffering when
// the output stream was set to null.
//
// Revision 2.0  1997/06/02 21:34:42  joel
// First version after moving over to the parallel version.
//
//

/**
 * Class which is used as a buffer for the MultiSearchReader. It is used
 * to buffer characters which might match a search term until it is decided
 * whether they do. This class is a bit mis-named as it is really a FIFO
 * buffer.
 * @see MultiSearchReader
 * @see MultiSearch
 * @author Joel Crisp
 * @version $Id: MultiSearchReader.java,v 1.1.1.1 1998/07/14 22:53:24 mrb Exp $
 */
class CharStack extends Object 
  {
  /**
   * Version tag. Internally accessable, RCS ID format. 
   */
  public static String RCSID="$Id: MultiSearchReader.java,v 1.1.1.1 1998/07/14 22:53:24 mrb Exp $";

  /**
   * Stack of buffered characters.
   */
  protected char stack[]=null;
  
  /**
   * Stream to which characters are output when the buffer is flushed.
   * If null, flushed characters are discarded.
   */
  protected Writer sink=null;
  
  /**
   * depth of current stack from the top of the stack to the start of data
   */
  protected int bottom=0; // Used whilst filling the stack 
                          
  /**
   * Create a new CharStack of the specified size using the appropriate output
   * stream as a sink for flushed data.
   * @param size maximum number of characters to be buffered.
   * @param sink Writer to which output data is flushed. If this is null, 
   *             flush discards data.
   */
  public CharStack(int size,Writer sink)
    {
    stack=new char[size+1];
    this.sink=sink;
    }

  /**
   * Push a new character onto the top of the stack
   * @param top new top character
   * @exception java.io.Exception thrown if flushing characters causes an exception
   */ 
  public void push(char top) throws IOException
    {
    int I;
    if(sink!=null && bottom==stack.length) 
      {
      sink.write(stack[0]);
      }
    else if(bottom!=stack.length) bottom++;
    for(I=0;I<stack.length-1;I++) 
      {
      stack[I]=stack[I+1];
      }
    stack[stack.length-1]=top;
    }

  /**
   * push some stacked characters to the output stream (except for the
   * topmost). If there is no output stream they are discarded.
   * This is used as there is a one character lookahead when reading from the
   * source, which is not part of the match.
   * @param number of characters to flush
   */
  public void flush(int count) throws IOException
    {
    if(sink!=null) sink.write(stack,stack.length-bottom,bottom-count-1);
    //    System.err.println("Flushing "+stack+":"+stack.length+","+(stack.length-bottom)+","+(bottom-count-1));
    bottom=1;
    }

  /**
   * Flush all buffered characters to the output stream. If there is no
   * output stream then they are discarded. Also flushes lookahead char.
   */
  public void flushAll() throws IOException
    {
    if(sink!=null) sink.write(stack,stack.length-bottom,bottom);
    bottom=0;
    }

  /**
   * Pull the bottom most character from the stack. 
   * @param count the number of characters in the stack
   * @return the oldest buffered character
   */
  public char pop(int count) 
    {
    return(stack[stack.length-count]);
    }

  }

/**
 * Class which implements a multiple parallel search on an input stream.
 * It optionally copies any non-matched data to an output stream.
 * @see MultiSearch
 * @author Joel Crisp
 * @version $Id: MultiSearchReader.java,v 1.1.1.1 1998/07/14 22:53:24 mrb Exp $ 
 */
public class MultiSearchReader extends MultiSearch 
  {
  /**
   * Version tag. Internally accessable, RCS ID format. 
   */
  public static String RCSID="$Id: MultiSearchReader.java,v 1.1.1.1 1998/07/14 22:53:24 mrb Exp $";

  /**
   * fifo buffer used to store characters while a possible match is pending
   */
  protected CharStack backtrackBuffer=null;

  /**
   * offset from the start of the source to the current character
   */
  protected int offset=0;

  /**
   * if non-zero, is the number of characters stored in the fifo buffer to
   * fetch before continuing to use the input stream 
   */
  protected int backtrackCount=0;

  /**
   * sink for non-matched data. If null, data is discarded
   */
  protected Writer sink;
 
  /**
   * Hack for SGML streams which holds a state informing whether we are in
   * an SGML tag. 
   */
  protected int inTag=OUTSIDE_TAG;

  /**
   * inTag value for state of outside a tag
   * @see inTag
   */
  public final static int OUTSIDE_TAG=0;

  /**
   * inTag value for state of having seen the starting angle bracket (<) for
   * a tag but not yet in the tag data
   * @see inTag
   */
  public final static int ENTERING_TAG=1;

  /**
   * inTag value for state of having seen the starting angle bracket (<) for
   * a tag and started to read the tag data
   * @see inTag
   */
  public final static int INSIDE_TAG=2;

  /**
   * construct a new MultiSearchReader which looks for the specified terms
   * in an input stream copying failed matches to the output stream
   * @param term Enumeration of Strings to search for
   * @param caseSensitive true if case is to be matched exactly
   * @param sink output stream or null to discard data
   */
  public MultiSearchReader(Enumeration terms,boolean caseSensitive,Writer sink)
    { 
    super(terms,caseSensitive);
    this.sink=sink;
    }
  
  /**
   * fetch the next character from the input source at the specified offset 
   * Note that offset is not allowed to increment by more than one between
   * calls, or decrement by more than the number of buffered characters. 
   * These are additional restrictions over those in MultiSearch.getNextCandidate
   * @see MultiSearch#getNextCandidate
   * @exception java.io.IOException thrown if the input stream generates an Exception
   * @exception java.lang.IndexOutOfBoundsException thrown if there is an attempt made to access more of the stream history than is stored in the buffer.
   * @return the next character to be matched
   * @param source A stream Reader from which input data will be read
   * @param offset the current location in the input stream
   */
  protected char getNextCandidate(Object source,int offset) throws IOException,IndexOutOfBoundsException
    { 
    char candidate;

    // Enforce the first restriction. Second is enforced by an Exception in 
    // CharStack  
    if(offset>this.offset+1) throw new IndexOutOfBoundsException(offset+">"+(this.offset+1));

    // We have some buffered data to read. Fetch it
    if(backtrackCount>0)
      {
      candidate=backtrackBuffer.pop(backtrackCount--);      
      return(candidate);
      }

    // Get the next character from the input stream
    this.offset++;
    try 
      {
      if(!((Reader)source).ready()) throw new IndexOutOfBoundsException();
      candidate=(char)((Reader)source).read();
      }
    catch(IOException e) 
      {
      throw new IndexOutOfBoundsException();
      }

    // Create the buffer if is does not exist. Note that it is the same
    // size as the longest string which could be matched
    if(backtrackBuffer==null) backtrackBuffer=new CharStack(maxLength,sink);
    
    // Stack the current character
    backtrackBuffer.push(candidate);

    // inform our tag state. Note that this is not a sophisticated method !
    if(inTag==ENTERING_TAG) inTag=INSIDE_TAG;
    if(candidate=='<' && inTag==OUTSIDE_TAG) inTag=ENTERING_TAG;
    if(candidate=='>' && inTag==INSIDE_TAG) inTag=OUTSIDE_TAG;

    return candidate;
    }

  /**
   * Retrive the tag status
   * @see inTag
   * @return one of OUTSIDE_TAG, ENTERING_TAG, or INSIDE_TAG
   */
  public int getTagStatus() 
    { 
    return inTag; 
    }

  /**
   * Unwind the backtrack buffer by count characters
   * @param count number of characters to unwind the buffer. 
   */
  protected void backtrack(int count) 
    {
    backtrackCount=count;
    }

  /**
   * Carry out the search operation. In addition to the actual search,
   * some buffer management is carried out.
   * @see MultiSearch#search
   * @param offset current location in the search data
   * @param text Object which must be a Reader containing search data
   * @exception java.io.IOException thrown if the input stream excepts
   * @result a result descritor or null
   * @see MultiSearchResult
   */
  public MultiSearchResult search(int offset,Object text) throws IOException
    {
    MultiSearchResult result=super.search(offset,text);
    if(result==null && backtrackBuffer==null) return result;
    if(result!=null) backtrackBuffer.flush(result.getMatch().length());
    else backtrackBuffer.flushAll(); 
    return result;
    }

  /**
   * A test function which reads the terms from the file named by its second
   * argument and looks for them in the file named by its first argument.
   * @param argv String array containing the names of the two test files.
   */
  public static void main(String argv[]) throws IOException
    {
    File testFile=new File(argv[0]);
    Reader test=new FileReader(testFile);
    File termFile=new File(argv[1]);
    FileLineEnumerator terms=new FileLineEnumerator(new FileReader(termFile));
    Writer sink=new OutputStreamWriter(System.err);
    MultiSearchReader root=new MultiSearchReader(terms,false,sink);

    MultiSearchResult match=new MultiSearchResult(-1," ");
    while(match!=null)
      {
      System.err.println(match=root.search(match.getOffset()+match.getMatch().length(),test));
      }
    sink.close();
    }
  }






