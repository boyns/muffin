/*
 *  gnu/regexp/RETokenRepeated.java
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
import java.util.Vector;

class RETokenRepeated extends RETokenStingifiable implements java.io.Serializable
{
  private REToken token;

  public RETokenRepeated(REToken f_token, int f_min, int f_max)
  {
    token = f_token;
    min = f_min;
    max = f_max;
  }

  public int match(String input, int index,int eflags,REMatch mymatch)
  {
    int numRepeats = 0;
    Vector positions = new Vector();

    do
      {
	// positions.elementAt(i) == position in input after <<i>> matches
	positions.addElement(new Integer(index));

	// Check for stingy match
	if (stingy && (numRepeats >= min))
	  {
	    int s = next(input,index,eflags,mymatch);
	    if (s != -1) return s;
	  }
	
	if ((index = token.match(input,index,eflags,mymatch))==-1) break;
      }
    while (numRepeats++ < max);

    // If there aren't enough repeats, then fail
    if (numRepeats < min) return -1;
    
    /* We're greedy, but ease off until a true match is found */
    int posIndex = positions.size();
    
    // At this point we've either got too many or just the right amount.
    // See if this numRepeats works with the rest of the regexp.
    
    while (--posIndex >= min)
      {
	index = ((Integer) positions.elementAt(posIndex)).intValue();
	
	// Run match algorithm for each remaining token in this RE
	
	// If rest of pattern matches
	if ((index = next(input,index,eflags,mymatch)) != -1)
	  return index;
	
	// else did not match rest of the tokens, try again on smaller sample
      }
    return -1;
  }
}

