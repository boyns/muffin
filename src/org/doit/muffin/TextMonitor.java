/* $Id: TextMonitor.java,v 1.6 2000/01/24 04:02:14 boyns Exp $ */

/*
 * Copyright (C) 1996-2000 Mark R. Boyns <boyns@doit.org>
 *
 * This file is part of Muffin.
 *
 * Muffin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Muffin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Muffin; see the file COPYING.  If not, write to the
 * Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */

package org.doit.muffin;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * @author Mark Boyns
 */
class TextMonitor implements Monitor
{
    Vector handlers;
    boolean suspended = false;
    String infoString;
    
    TextMonitor(String infoString)
    {
	this.infoString = infoString;
	handlers = new Vector(100);
    }

    public void register(Handler h)
    {
	handlers.addElement(h);
	//System.out.println(h.request.getURL()); 
    }

    public void unregister(Handler h)
    {
	handlers.removeElement(h);
	//System.out.println(h.request.getURL());
    }
    
    public void update(Handler h)
    {
    }
    
    public void suspend()
    {
	suspended = true;
    }

    public void resume()
    {
	suspended = false;
    }

    public Enumeration enumerate()
    {
	return handlers.elements();
    }

    public void minimize(boolean enable)
    {
    }

//     void draw()
//     {
// 	//System.out.print("[H[2J");
// 	System.out.println(infoString);
// 	System.out.println("--------------------------------------------------------------------------------");
	
// 	Enumeration e = handlers.elements();
//  	while (e.hasMoreElements())
// 	{
// 	    Object obj = e.nextElement();
//  	    Handler handler = (Handler) obj;

// 	    int currentBytes = handler.getCurrentBytes();
// 	    int totalBytes = handler.getTotalBytes();
// 	    String contentType = null;
// 	    StringBuffer buf = new StringBuffer();

// 	    if (handler.reply != null)
// 	    {
// 		if (totalBytes > 0)
// 		{
// 		    int n = (int)((double)currentBytes/totalBytes*100);
// 		    int i;
// 		    buf.append("[");
// 		    for (i = 0; i < 100; i += 10)
// 		    {
// 			if (n > i)
// 			{
// 			    buf.append("-");
// 			}
// 			else if (i > n && i-10 < n)
// 			{
// 			    buf.append(">");
// 			}
// 			else
// 			{
// 			    buf.append(" ");
// 			}
// 		    }
// 		    buf.append("] ");
// 		    buf.append(currentBytes);
// 		    buf.append(" bytes ");
//                     buf.append(n);
// 		    buf.append("%");
// 		}
// 		else
// 		{
// 		    buf.append("[          ] ");
// 		    buf.append(currentBytes);
// 		    buf.append(" bytes");
// 		}
// 	    }
// 	    else
// 	    {
// 		buf.append("[contacting]");
// 	    }
// 	    buf.append(" ");
// 	    buf.append(handler.request.getURL());
// 	    buf.setLength(80);
// 	    System.out.println(buf);
// 	}
// 	System.out.println();
//     }
}
