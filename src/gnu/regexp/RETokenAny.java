/*
 *  gnu/regexp/RETokenAny.java
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

class RETokenAny extends REToken implements java.io.Serializable
{
  private boolean m_newline; // true if '.' can match a newline (RE_DOT_NEWLINE)
  private boolean m_null;    // true if '.' can't match a null (RE_DOT_NOT_NULL)

  public RETokenAny(boolean f_newline, boolean f_null)
  { 
    m_newline = f_newline;
    m_null = f_null;
  }

  public int match(String input, int index, int eflags, REMatch mymatch)
  {
    // ways it can fail:
    if ((index >= input.length())
	|| (!m_newline && (input.charAt(index)=='\n'))
	|| (m_null && (input.charAt(index)==0)))
      return -1;

    return next(input,index+1,eflags,mymatch);
  }
}

