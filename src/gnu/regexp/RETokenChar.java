/*
 *  gnu/regexp/RETokenChar.java
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

class RETokenChar extends REToken implements java.io.Serializable
{
  private char[] ch;
  private boolean insens;

  public RETokenChar(char c, boolean ins)
  {
    ch = new char [1];
    ch[0] = (insens = ins) ? Character.toLowerCase(c) : c;
  }
  
  public int match(String input, int index, int eflags, REMatch mymatch)
  {
    int z = ch.length;
    char c;
    if (index+z > input.length()) return -1;
    // we could go forward or backward, doesn't matter
    while (--z >= 0)
      {
	if (( (insens) ? Character.toLowerCase(input.charAt(index+z)) : input.charAt(index+z) ) != ch[z]) return -1;
      }
    return next(input,index+ch.length,eflags,mymatch);
  }

  // Overrides REToken.chain() to optimize for strings
  boolean chain(REToken next)
  {
    if (next instanceof RETokenChar)
      {
	RETokenChar cnext = (RETokenChar) next;
	// assume for now that next can only be one character
	int newsize = ch.length + cnext.ch.length;

	char[] chTemp = new char [newsize];

	System.arraycopy(ch,0,chTemp,0,ch.length);
	System.arraycopy(cnext.ch,0,chTemp,ch.length,cnext.ch.length);

	ch = chTemp;
	return false;
      }
    else return super.chain(next);
  }

}


