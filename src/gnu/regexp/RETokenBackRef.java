/*
 *  gnu/regexp/RETokenBackRef.java
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

class RETokenBackRef extends REToken implements java.io.Serializable
{
private int num;
private boolean insens;
  
public RETokenBackRef(int mynum, boolean ins)
  {
    insens = ins;
    num = mynum;
  }
  
public int match(String input, int index, int eflags, REMatch mymatch)
  {
    if (index > input.length()) return -1;
    int b,e;
    b = mymatch.start[num];
    e = mymatch.end[num];
    if ((b==-1)||(e==-1)) return -1; // this shouldn't happen, but...

    if ((index+e-b)>input.length()) return -1; // couldn't fit.

    return (input.regionMatches(insens,index,input,b,e-b)) ?
      next(input,index+e-b,eflags,mymatch) : -1;
  }
}


