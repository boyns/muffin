package UK.co.demon.asmodeus.util;

import java.lang.*;
import java.io.*;
import java.util.*;

//
// $Log: MultiSearch.java,v $
// Revision 1.1.1.1  1998/07/14 22:53:24  mrb
// start
//
// Revision 2.4  1997/06/18 07:04:17  joel
// Now parseable with javacc Java1.1.1 grammar.
//
// Revision 2.3  1997/06/13 11:34:11  joel
// Added more documentation
//
// Revision 2.2  1997/06/02 21:44:18  joel
// Really fixed it this time. So much for late night work !
//
// Revision 2.1  1997/06/02 21:42:17  joel
// Fixed the missing quote at the end of the RCS ID.
//
// Revision 2.0  1997/06/02 21:38:37  joel
// First version after moving over to the parallel version.
//
//

/**
 * This class implements an engine to search a string for lots of different
 * sub-strings simultaneously. It is much faster than repeatedly searching
 * for each string, and should be at least as fast (and simpler) than a
 * hashtable search.
 *
 * @author: Joel Crisp
 * @version: $Id: MultiSearch.java,v 1.1.1.1 1998/07/14 22:53:24 mrb Exp $
 * @see MultiSearchResult 
 */  

public class MultiSearch extends Object 
  {
  /**
   * Version tag. Internally accessable, RCS ID format. 
   */
  public static String RCSID="$Id: MultiSearch.java,v 1.1.1.1 1998/07/14 22:53:24 mrb Exp $";

  /** 
   * term holds zero or more characters in this node which form part of
   * a term to search for. If term is zero, this is a terminal node
   * which is also a leaf node.
   */
  protected String term=null;

  /**
   * next is the array of nodes which form the branches of the tree from
   * here down. It is indexed from the charIndex array
   */
  protected MultiSearch next[]=null;

  /**
   * charIndex forms the index to the branches of the tree from here on down
   */
  protected char charIndex[]=null;

  /**
   * leaf is a flag to say whether this node is a candidate for matching.
   * I'm not sure if it is redundent, but until I decide for certain I'll
   * keep it.
   */
  protected boolean leaf=false;

  /**
   * maxLength holds the maximum length of all strings under this node. It
   * is only relevent for the root node. 
   */
  protected int maxLength=0;
 
  /**
   * Is this a case sensitive tree ?
   */
  protected boolean isSensitive=true;

  /**
   * debug just a flag to turn on more diagnostic info 
   */
  private static final boolean debug=false;
 
  /**
   * constructor for an empty node. Use build to fill the tree.
   * @see build
   */
  public MultiSearch() { return; }

  /**
   * Constructor for a node with a term. Only call if you know what you
   * are doing. Should this be public ?
   * @param term String to initialise this node with.
   */
  public MultiSearch(String term) 
    {
    this.term=term;
    leaf=true;
    }

  /**
   * Constructor for a tree of terms. 'terms' must be an enumeration of 
   * <EMP>String</EMP>s
   * @param terms Enumeration of Strings to use as the search terms.
   * @param caseSensitive true if the search is to match exactly for case.
   */
  public MultiSearch(Enumeration terms,boolean caseSensitive)
    {
    while(terms.hasMoreElements())
       {
       String term=(String)terms.nextElement();  
       if(caseSensitive) insert(term,0);
       else insert(term.toLowerCase(),0);
       }
    isSensitive=caseSensitive;
    }

  /**
   * This function adds the text from the term starting at the location
   * index and as far as needed into the tree at this point.
   * @param term String to add to this node's array
   * @param index Offset into term at which this node begins
   * @return none
   */
  protected void addToArray(String term,int index)
    {
    if(charIndex==null)
      {
      // Need to create the arrays
      charIndex=new char[1];
      next=new MultiSearch[1];
      charIndex[0]=term.charAt(index);

      // Create the next node down the branch
      next[0]=new MultiSearch(term.substring(index+1,term.length()));  
      return;
      }

    // Expanding the arrays
    char temp[]=charIndex;
    charIndex=new char[temp.length+1];
    System.arraycopy(temp,0,charIndex,0,temp.length);
    charIndex[temp.length]=term.charAt(index);
    MultiSearch tempTree[]=next;
    next=new MultiSearch[tempTree.length+1];
    System.arraycopy(tempTree,0,next,0,tempTree.length);
    // Create the next node down the branch.
    next[tempTree.length]=new MultiSearch(term.substring(index+1,term.length()));
    }

  /**
   * This function should be called on the root node of the tree to install
   * a new search term in the tree. Index shold be zero.
   * Normally called from the build method
   * @see build
   * @param term New string to insert into the tree
   * @param index offset into the new string to start inserting from.
   */
  protected void insert(String term,int index)
    {
    if(term.length()>maxLength) maxLength=term.length();

    if(index==term.length()) return; // Terminal node.

    if(debug) 
      {
      System.err.print("Inserting "+term+" from "+index);
      System.err.println(" [ Existing term "+this.term+"]");
      }

    // index is number of characters already matched to get to this
    // this tree node.
    if(this.term!=null) 
      {
      int I=0;

      // There already exists a part of a term in this node. Expand the node
      // to include the new term as well.

      // First, skip common characters in the two terms
      while(this.term.charAt(I)==term.charAt(index)) 
        {
        I++;
        index++;
        if(index==term.length()) 
          {
          // Already in tree at this node
          leaf=true; 
          return; 
          } 
        if(I==this.term.length()) break;
        }

      // Now, split the tree to give a branch for the original term and a
      // new branch for the new term
      if(I<this.term.length())
        {
        // Need to split this.term at I
        if(debug) System.err.println("Splitting at "+I);

        // Initialise a new node for the new term.
        MultiSearch newNext=new MultiSearch(this.term.substring(I+1,this.term.length()));
        newNext.charIndex=charIndex;
        newNext.next=next;
        newNext.leaf=leaf;
        leaf=false;

        // Initialise array for tree branches from here on down
        next=new MultiSearch[1];
        next[0]=newNext;

        // Initialise index array for tree branch array
        charIndex=new char[1];
        charIndex[0]=this.term.charAt(I);
        addToArray(term,index);

        // Truncate this node's term to the common part of the original and
        // new terms
        if(I==0) this.term=null;
        else this.term=this.term.substring(0,I);

        return;
        }
      }

    // If this node already has branch nodes, seek along the index of 
    // branch nodes to see if we can match one
    if(charIndex!=null)
      {
      int J;
      for(J=0;J<charIndex.length;J++)
        {
        if(term.charAt(index)==charIndex[J]) 
          { 
          // Match found, add the new term to the branch
          next[J].insert(term,index+1);
          return;
          }
        }

      // Not found. Add to the array.
      addToArray(term,index);
      return;
      }

   // We were formarly a trivial branch node. Become a non-trivial one.
   this.term=term.substring(index,term.length());
   leaf=true;
   return;
   }

  /**
   * Override this function to provide your own source for the characters
   * to match. It takes an arbitrary object reference holding the text
   * and an offset for the next character. The default treats the reference
   * as a text string and just fetches the 'offset'th character. When the
   * offset exceeds the length of the data, throw an IndexOutOfBoundsException
   * @param source Object from which we are getting characters
   * @param offset Current offset from first character in source to current
   * @return the character at offset in source
   * @exception java.io.IOException thrown if there is an io error in the source
   * @exception java.lang.IndexOutOfBoundsException thrown if there is an attempt to read more buffered characters than exist
   * @see backtrack
   * @see search
   */
  protected char getNextCandidate(Object source,int offset) throws IOException,IndexOutOfBoundsException
    {
    if(offset==((String)source).length()) throw new IndexOutOfBoundsException();
    return (((String)source).charAt(offset));
    }

  /**
   * This function is called whenever the algorithm backtracks. It must
   * ensure that the next character returned from getNextCandidate is 
   * at the 'offsetDelta' from the current offset (offsetDelta is always
   * negative, and never greater in magnitude than this.maxLength).
   * The default action is to do nothing.
   * @param offsetDelta the difference between the current and desired offset (always negative)
   * @see getNextCandidate
   * @see search
   */
  protected void backtrack(int offsetDelta)
    {
    }   

  /**
   * This function is a diagnostic one to dump the state of the tree.
   * Leaf nodes are marked with an asterisk (*), node transitions with 
   * a full stop '.'
   * @param indent current indentation level
   */
  public void dump(int indent)
    {
    // Dump the part of the term in this node.
    if(term!=null) 
      {
      System.err.print(term);
      indent+=term.length();
      }

    // Mark if we are a leaf node
    if(leaf) { System.err.print("*"); indent++; }

    // Return if this is the end of the tree   
    if(charIndex==null) { System.err.println(); return; } // leaf node

    // Dump the branches
    int I;
    for(I=0;I<charIndex.length;I++)  
      {
      if(I>0) System.err.print("                                          ".substring(0,indent));
      System.err.print("."+charIndex[I]);
      next[I].dump(indent+2);
      }
    }

  /**
   * Carry out a parallel search on the text string for all of the terms
   * set up by the tree constructor
   * @param offset the current offset into the search 
   * @param text an opaque object of the type expected by getNextCandidate
   * @see getNextCandidate
   * @see backtrack
   * @return a result descriptor or null
   * @see MultiSearchResult
   * @exception java.io.IOException thrown if getNextCandidate fails
   */
  public MultiSearchResult search(int offset,Object text) throws IOException
    {
    MultiSearch seekptr=this;
    int nodeOffset=0;
    char currentMatch[]=new char[maxLength];
    int matchSize=0; 
    int bestMatch=0;
    int lastMatchOffset=0;

    while(true)
      {
      if(debug) System.err.print(".");

      // Fetch next candidate
      char candidate;   
      char trueCandidate;
      try
        {
        trueCandidate=getNextCandidate(text,offset);
        if(isSensitive) candidate=trueCandidate;
        else candidate=Character.toLowerCase(trueCandidate);
        }
      catch(IndexOutOfBoundsException e)
        {
        // No more data to match
        if(debug) System.err.println("Data exhausted");
        break;
        }

      if(seekptr.term==null || nodeOffset==seekptr.term.length())
        {
        // Exhausted all characters in this node's term
        if(seekptr.leaf) 
          {
          if(debug) System.err.print("*");

          // This node is a candidate for a match.
          if(seekptr.charIndex==null)
            {
            // Ok, the index is also exhausted so there are no more nodes.
            // We now know we are the longest match
            return new MultiSearchResult(offset-matchSize,new String(currentMatch,0,matchSize)); 
            }

          // We have a match, but it might not be the longest
          // Remember it and continue.
          lastMatchOffset=nodeOffset;
          bestMatch=matchSize;
          }
        }
      else 
        { 
        if(seekptr.term!=null)
          {
          // We are comparing in the term
          if(seekptr.term.charAt(nodeOffset)==candidate) 
            {
            // Matched a character. Add it to the match and continue
            currentMatch[matchSize++]=trueCandidate;
            if(debug) System.err.print(candidate);
            nodeOffset++; // Move to next character or off end of term
            offset++; // Move on to compare next character
            continue;
            }

          // Failed to match. Backtrack if we have a shorter match
          if(debug) if(matchSize>0) System.err.print('!');
          backtrack(matchSize-bestMatch);
          offset-=(matchSize-bestMatch);
          matchSize=bestMatch;
          if(matchSize>0) 
            {
            // We had already found a match and it must be the longest
            return new MultiSearchResult(offset,new String(currentMatch,0,matchSize)); 
            }

          offset++; // Move onto next character to start matching again
          seekptr=this; // Reset to start of search tree
          nodeOffset=0; // Start of first node.
          continue;
          }
        }

      if(seekptr.charIndex!=null)
        {
        // We are comparing in the index 
        int J;
        int arrayLength;

        if(debug) 
          {
          if(seekptr==this) System.err.print("#");
          else System.err.print("'");
          }

        arrayLength=seekptr.charIndex.length;
        for(J=0;J<arrayLength;J++)
          {
          if(candidate==seekptr.charIndex[J]) 
            { 
            if(debug) System.err.print(candidate);
            seekptr=seekptr.next[J];
            offset++;
            nodeOffset=0; // Start of a new node
            currentMatch[matchSize++]=trueCandidate;
            if(seekptr.leaf && seekptr.term==null) 
              {
              // Terminal node. We have a match
              return new MultiSearchResult(offset,new String(currentMatch,0,matchSize));
              }
            break;
            }
          }

        if(J!=arrayLength) continue;

        // Match failed 
        if(debug) if(matchSize>0) System.err.print('!');
        backtrack(matchSize-bestMatch);
        offset-=(matchSize-bestMatch);
        matchSize=bestMatch;
        nodeOffset=lastMatchOffset;

        if(matchSize>0) 
          {
          // Drop back to the previous successful match
          return new MultiSearchResult(offset,new String(currentMatch,0,matchSize)); 
          }

        offset++;
        seekptr=this; // Reset to start of search tree
        }
      }  

    if((seekptr.term==null || nodeOffset==seekptr.term.length()) && seekptr.leaf)
      {
      if(seekptr.charIndex==null)
        {
        // Ok, the index is also exhausted so there are no more nodes.
        // We now know we are the longest match
        return new MultiSearchResult(offset-matchSize,new String(currentMatch,0,matchSize)); 
        }

      // We have a match, but it might not be the longest
      // Remember it and continue.
      lastMatchOffset=nodeOffset;
      bestMatch=matchSize;
      }

    if(bestMatch==0) return(null);   

    backtrack(matchSize-bestMatch);
    offset-=(matchSize-bestMatch);
    matchSize=bestMatch;

    return new MultiSearchResult(offset,new String(currentMatch,0,matchSize)); 
    }

  /**
   * Test and debug method. Reads in some 'interesting' terms and searches
   * and 'interesting' string for them. 'Interesting' is taken to mean
   * data which exercises the functionality of this class. 
   * @param argv String array of arguments. Ignored. 
   */
  public static void main(String argv[]) throws IOException
    {
    // Test function for multi-search
    Vector terms=new Vector();
    String test="If I had a badge I mean a badger which was foo";

    terms.addElement("fooargle");
    terms.addElement("foowopple");
    terms.addElement("bADGER");
    terms.addElement("foowhip");
    terms.addElement("BADger");
    terms.addElement("foowhiq");
    terms.addElement("adge");
    terms.addElement("foo");    
    MultiSearch root=new MultiSearch(terms.elements(),false);
    root.dump(0);
    MultiSearchResult match=new MultiSearchResult(-1," ");
    while(match!=null)
      {
      System.err.println(match=root.search(match.getOffset()+match.getMatch().length(),test));
      }
    } 
} 

