/*
 *  gnu/regexp/RETokenStart.java
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

class RETokenStart extends REToken implements java.io.Serializable
{
  private boolean newline;

  public RETokenStart(boolean f_newline)
  {
    newline = f_newline;
  }
  
  public int match(String input, int index, int eflags, REMatch mymatch)
  {
    if (newline && (index>0) && (input.charAt(index-1) == '\n')) return next(input,index,eflags,mymatch);
    if ((eflags & RE.REG_NOTBOL)>0) return -1;

    if ((eflags & RE.REG_ANCHORINDEX)>0) 
      return (index==mymatch.index) ? next(input,index,eflags,mymatch) : -1;
    else
      return (index==0) ? next(input,index,eflags,mymatch) : -1;
  }
}
