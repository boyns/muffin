/*
 *  gnu/regexp/REToken.java
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

abstract class REToken
{
  private REToken m_next;
  
  abstract int match(String input, int index, int eflags, REMatch mymatch);
  
  protected int next(String input, int index, int eflags, REMatch mymatch)
  {
    return (m_next == null) ? index : m_next.match(input,index,eflags,mymatch);
  }
  
  boolean chain(REToken next)
  {
    m_next = next;
    return true; // Token was accepted
  }
}
