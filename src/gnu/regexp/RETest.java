/*
 *  gnu/regexp/RETest.java
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package gnu.regexp;

public class RETest
{
  // Redefined so javadoc doesn't generate an entry...
  RETest()
  { }
  
  /**
   *  RETest provides a simple way to test regular expressions.
   *  It runs from the command line using the Java interpreter.
   *  To use it, enter the following from a command prompt (provided
   *  that the Java system knows where to find the RETest bytecodes):
   *  <BR><CODE>java gnu.regexp.RETest [regExp] [inputString]</CODE><BR>
   *  where <i>regExp</i> is a regular expression (you'll probably have
   *  to escape shell meta-characters) and <i>inputString</i> is the string
   *  to match against (again, put it in quotes or escape any shell meta-
   *  characters.
   *  <P>
   *  The test function will report the package version number, whether
   *  the expression matches the input string, what the match it found was,
   *  and the contents of any subexpressions, if applicable.
   *
   *  @param args
   *  The command line arguments (an array of Strings).
   *
   *  @exception REException
   *  There was an error compiling or executing the regular expression.
   **/
  
  public static void main(String args[]) throws REException
  {
    System.out.println("gnu.regexp package Version "+RE.version());
    
    int numRepeats = 1;
    if (args.length == 3)
      numRepeats = Integer.parseInt(args[2]);
    if (args.length < 2)
      {
	System.out.println("usage: java RETest regExp inputString [numRepeats]");
	System.exit(1);
      }
    
    // Construct the regular expression

    RE expression = null;
    long begin = System.currentTimeMillis();

    for (int rpt = 0; rpt < numRepeats; rpt++)
      expression = new RE(args[0]);

    long end = System.currentTimeMillis();

    if (numRepeats>1)
      {
	System.out.println("Compiling "+numRepeats+" times took "+(end-begin)+" ms");
	System.out.println("Average compile time: "+((end-begin)/numRepeats)+" ms");
      }

    // Is the input in its entirety a match?
    
    System.out.println("isMatch: "+expression.isMatch(args[1]));
    
    // How many matches are possible (these might overlap)
    
    REMatch[] matches = expression.getAllMatches(args[1]);
    System.out.println("Total possible matches found: "+matches.length);
    
    // Get the first match
    
    REMatch match = null;

    begin = System.currentTimeMillis();

    for (int rpt = 0; rpt < numRepeats; rpt++)
      match = expression.getMatch(args[1]);

    end = System.currentTimeMillis();

    if (numRepeats>1)
      {
	System.out.println("Matching "+numRepeats+" times took "+(end-begin)+" ms");
	System.out.println("Average match time: "+((end-begin)/numRepeats)+" ms");
      }

    if (match == null)
      System.out.println("Expression did not find a match.");
    else
      {
	// Report the full match indices

	System.out.println("Match found from position "
			   + match.getStartIndex() + " to position "
			   + match.getEndIndex());

	// Take advantage of REMatch.toString() to print match text

	System.out.println("Match was: '" + match + "'");

	// Report subexpression positions

	for (int i=1; i <= expression.getNumSubs(); i++)
	  {
	    System.out.println("Subexpression #" + i + ": from position "
			   + match.getSubStartIndex(i) + " to position "
			   + match.getSubEndIndex(i));

	    // Note how the $n is constructed for substituteInto

	    System.out.println(match.substituteInto("The subexpression matched this text: '$"+i+"'"));
	  }
      }
  }
}

