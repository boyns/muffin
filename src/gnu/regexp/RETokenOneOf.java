/*
 *  gnu/regexp/RETokenOneOf.java
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

class RETokenOneOf extends REToken implements java.io.Serializable
{
  private Vector options;
  private boolean negative;

  // This constructor is used for convenience when we know the set beforehand,
  // e.g. \d --> new RETokenOneOf("0123456789",false, ..)
  //      \D --> new RETokenOneOf("0123456789",true, ..)


  public RETokenOneOf(String f_options,boolean f_negative,boolean f_insens)
  {
    options = new Vector();
    negative = f_negative;
    for (int i=0; i<f_options.length(); i++)
      options.addElement(new RETokenChar(f_options.charAt(i),f_insens));
  }

  public RETokenOneOf(Vector f_options,boolean f_negative)
  {
    options = f_options;
    negative = f_negative;
  }

  public int match(String input, int index, int eflags, REMatch mymatch)
  {
    if (index >= input.length()) return -1;
    int newIndex;
    for (int i=0; i < options.size(); i++)
      {
	newIndex = ((REToken) options.elementAt(i)).match(input,index,eflags,mymatch);
	if (newIndex != -1) // match was successful
	  return (negative) ? -1 : next(input,newIndex,eflags,mymatch);
      }
    return (negative) ? next(input,index+1,eflags,mymatch) : -1;
  }
}

