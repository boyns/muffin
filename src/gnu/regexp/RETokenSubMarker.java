/*
 *  gnu/regexp/RETokenSubMarker.java
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

class RETokenSubMarker extends RETokenStingifiable implements java.io.Serializable
{
  private RE sub;
  private int subIndex;

  public RETokenSubMarker(RE f_token, int f_index, int f_min, int f_max)
    {
      sub = f_token;
      sub.addLoop(this);
      subIndex = f_index;
      min = f_min;
      max = f_max;
    }
  
  public int match(String input, int index,int eflags,REMatch mymatch)
    {
      int maxNow = max - mymatch.count[subIndex];
      
      if ((mymatch.start[subIndex] == -1) || (mymatch.start[subIndex] > index))
	mymatch.start[subIndex] = index; // first time through

      if (mymatch.count[subIndex] <= 1)
	mymatch.end[subIndex] = index; // first time completed OK

      // If we've now matched maximum possible times
      if (maxNow<1) return next(input,index,eflags,mymatch);

      // If we're stingy and min has been hit, try matching the rest.
      if (stingy && (mymatch.count[subIndex] >= min))
	{
	  int s;
	  if ((s=next(input,index,eflags,mymatch)) != -1)
	    return s;
	}
      
      mymatch.count[subIndex]++;
      int z = sub.match(input,index,eflags,mymatch);
      if (z == -1)
	{
	  mymatch.count[subIndex]--; //we overestimated

	  if (mymatch.count[subIndex] < min)
	    {
	      mymatch.start[subIndex] = -1;
	      mymatch.end[subIndex] = -1;
	      return -1;
	    }
	  
	  // otherwise we've hit max on the sub
	  if (mymatch.count[subIndex] == 0)
	    mymatch.end[subIndex] = index;

	  return next(input,index,eflags,mymatch);
	}
      else return z;
    }
}
