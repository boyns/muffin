/*
 *  gnu/regexp/RETokenEnd.java
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

class RETokenEnd extends REToken implements java.io.Serializable
{
  private boolean newline;

  public RETokenEnd(boolean f_newline)
  { 
    newline = f_newline;
  }

  public int match(String input, int index, int eflags, REMatch mymatch)
  {
    if (index==input.length()) return ((eflags & RE.REG_NOTEOL)>0) ? -1 : next(input,index,eflags,mymatch);
    return (newline && (input.charAt(index)=='\n')) ? next(input,index,eflags,mymatch) : -1;
  }
}

