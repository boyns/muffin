/* $Id: CanvasMonitor.java,v 1.7 2000/01/24 04:02:13 boyns Exp $ */

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

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

/**
 * Graphical display of what Muffin is doing.
 *
 * @author Mark Boyns
 */
class CanvasMonitor extends Canvas implements Monitor, MouseListener, Runnable
{
    static final Dimension normalSize = new Dimension(300, 150);
    static final Dimension smallSize = new Dimension(64, 64);

    Main parent;
    Vector handlers;
    Font font;
    FontMetrics fontMetrics;
    boolean suspended = false;
    Hashtable colorTable;
    boolean minimized = false;
    boolean painting = false;

    /**
     * Create the CanvasMonitor.
     */
    CanvasMonitor(Main parent)
    {
	this.parent = parent;
	
	handlers = new Vector(100);

	font = Main.getOptions().getFont("muffin.smallfont");
	fontMetrics = getFontMetrics(font);
	setFont(font);

	colorTable = new Hashtable();
	colorTable.put("text/html", Color.cyan);
	colorTable.put("text/plain", Color.cyan);
	
	colorTable.put("image/gif", Color.green);
	colorTable.put("image/jpeg", Color.green);
	colorTable.put("image/jpg", Color.green);
	colorTable.put("image/tiff", Color.green);
	colorTable.put("image/x-png", Color.green);

	colorTable.put("audio/basic", Color.orange);
	colorTable.put("audio/x-wav", Color.orange);
	colorTable.put("audio/x-aiff", Color.orange);
	colorTable.put("audio/x-realaudio", Color.orange);
	
	colorTable.put("secure", Color.yellow);
	colorTable.put("default", Color.white);

	addMouseListener(this);

	Main.getThread().setRunnable(this);
    }

    public void run()
    {
	Thread.currentThread().setName("Muffin Monitor");

	for (;;)
	{
	    painting = false;

	    synchronized (this)
	    {
		try
		{
		    wait();
		}
		catch (InterruptedException ie)
		{
		    continue;
		}
		painting = true;
	    }
		
	    while (handlers.size() > 0)
	    {
		try
		{
		    Thread.sleep(500);
		    repaint();
		}
		catch (Exception e)
		{
		}
	    }
	}
    }

    /**
     * Register a handler to be monitored.
     *
     * @param h handler to be monitored
     */
    public void register(Handler h)
    {
	handlers.addElement(h);
    }

    /**
     * Unregister a handler being monitored.
     *
     * @param h handler to be unregistered.
     */
    public void unregister(Handler h)
    {
	handlers.removeElement(h);
    }

    /**
     * Update the status of a handler.
     *
     * @param h handler that needs updating
     */
    public void update(Handler h)
    {
	if (!painting)
	{
	    synchronized (this)
	    {
		notify();
	    }
	}
    }

    /**
     * Suspend monitoring.
     */
    public void suspend()
    {
	suspended = true;
	repaint();
    }

    /**
     * Resume monitoring.
     */
    public void resume()
    {
	suspended = false;
	repaint();
    }

    public Enumeration enumerate()
    {
	return handlers.elements();
    }

    public void minimize(boolean enable)
    {
	minimized = enable;
	setSize(minimized ? smallSize : normalSize);
    }

    /**
     * Make sure the canvas is a fixed size.
     */
    public Dimension getPreferredSize()
    {
	return getMinimumSize();
    }

    /**
     * Make sure the canvas is a fixed size.
     */
    public Dimension getMinimumSize()
    {
	return minimized ? smallSize : normalSize;
    }

    public void update(Graphics g)
    {
	paint(g);
    }
    
    public void paint(Graphics g)
    {
        Dimension d = getSize();
	Image dbuf = createImage(d.width, d.height);
	draw(dbuf.getGraphics());
	g.drawImage(dbuf, 0, 0, this);
    }
    
    /**
     * Draw the status of all handlers using colored
     * progress bars.
     *
     * @param g graphics object
     */
    void draw(Graphics g)
    {
	Insets insets = new Insets(5, 5, 5, 5);
	Dimension d = getSize();
	Color c;

	c = Main.getOptions().getColor("muffin.bg");

	if (suspended)
	{
	    c = c.darker();
	    g.setColor(c);
	    g.fill3DRect(2, 2, d.width-4, d.height-4, false);
	}
	else
	{
	    g.setColor(c);
	    g.draw3DRect(2, 2, d.width-4, d.height-4, false);
	}
	
	int y = insets.top;
	
	Enumeration e = handlers.elements();
 	while (e.hasMoreElements())
 	{
	    Object obj = e.nextElement();
 	    Handler handler = (Handler) obj;
	    Reply reply = handler.reply;
	    Request request = handler.request;

	    int h = minimized ? 5 : fontMetrics.getHeight();
	    int currentBytes = handler.getCurrentBytes();
	    int totalBytes = handler.getTotalBytes();
	    int meterLength = 0;
	    int meterMax = d.width - insets.left - insets.right;
	    String contentType = null;
	    StringBuffer buf = new StringBuffer();

	    if (reply != null)
	    {
		contentType = reply.getContentType();
		if (contentType != null)
		{
		    buf.append(contentType);
		    buf.append(" ");
		}
		
		if (totalBytes > 0)
		{
		    double percentComplete = (double)currentBytes/totalBytes;
		    /* filters can increase content-length */
		    if (percentComplete > 1.0)
		    {
			percentComplete = 1.0;
		    }
		    meterLength = (int)(percentComplete*meterMax);
		    buf.append((int)(percentComplete*100));
		    buf.append("% of ");
		    if (totalBytes >= 1024)
		    {
			buf.append(totalBytes/1024);
			buf.append("k");
		    }
		    else
		    {
			buf.append(totalBytes);
			buf.append(" bytes");
		    }
		}
		else
		{
		    if (currentBytes >= 1024)
		    {
			buf.append(currentBytes/1024);
			buf.append("k");
		    }
		    else
		    {
			buf.append(currentBytes);
			buf.append(" bytes");
		    }
		}

		if (request.getCommand().equals("CONNECT"))
		{
		    buf.append(" - ");
		    buf.append(request.getHost());
		}
		else
		{
		    String url = request.getURL();
		    String path = request.getPath();
		    String doc = request.getDocument();

		    if (fontMetrics.stringWidth(buf.toString() + url + " - ") < meterMax - 5)
		    {
			buf.append(" - ");
			buf.append(url);
		    }
		    else if (fontMetrics.stringWidth(buf.toString() + path + " - ") < meterMax - 5)
		    {
			buf.append(" - ");
			buf.append(path);
		    }
		    else if (fontMetrics.stringWidth(buf.toString() + doc + " - ") < meterMax - 5)
		    {
			buf.append(" - ");
			buf.append(doc);
		    }
		}
	    }
	    else if (request != null)
	    {
		buf.append("Contacting ");
		String url = request.getURL();

		if (request.getCommand().equals("CONNECT"))
		{
		    buf.append(request.getHost());
		    buf.append(" ");
		    buf.append(request.getPort());
		}
		else if (fontMetrics.stringWidth(buf.toString() + url) < meterMax - 5)
		{
		    buf.append(url);
		}
		else
		{
		    buf.append(request.getHost());
		    String path = request.getPath();
		    String doc = request.getDocument();
		    if (fontMetrics.stringWidth(buf.toString() + path) < meterMax - 5)
		    {
			buf.append(path);
		    }
		    else if (fontMetrics.stringWidth(buf.toString() + doc + "...") < meterMax - 5)
		    {
			buf.append("...");
			buf.append(doc);
		    }
		}
		if (fontMetrics.stringWidth(buf.toString() + " ...") < meterMax - 5)
		{
		    buf.append(" ...");
		}
	    }
	    else
	    {
		continue;
	    }

	    boolean isSecure = request.getCommand().equals("CONNECT");
	    
	    if (minimized)
	    {
		g.setColor(Main.getOptions().getColor("muffin.fg"));
		g.drawRect(insets.left, y, meterMax, h);
	    }
	    else
	    {
		g.setColor(Main.getOptions().getColor("muffin.bg"));
		g.fill3DRect(insets.left, y, meterMax, h, true);
	    }
	    
	    if (isSecure)
	    {
		g.setColor((Color) colorTable.get("secure"));
	    }
	    else if (contentType == null || !colorTable.containsKey(contentType))
	    {
		g.setColor((Color) colorTable.get("default"));
	    }
	    else
	    {
		g.setColor((Color) colorTable.get(contentType));
	    }

	    if (isSecure)
	    {
		g.fillRect(insets.left + 1, y + 1, meterMax - 2, h - 2);
	    }
	    else
	    {
		g.fillRect(insets.left + 1, y + 1, meterLength - 2, h - 2);
	    }

	    if (!minimized)
	    {
		g.setColor(Color.black);
		g.drawString(buf.toString(), insets.left + 5, y+h-fontMetrics.getMaxDescent());
	    }
	    
	    y += h + (minimized ? 2 : 3);
	}
    }

    public void mouseClicked(MouseEvent e)
    {
	if (e.getClickCount() == 2)
	{
	    minimized = !minimized;
	    parent.minimize(minimized);
	}
    }
    
    public void mouseEntered(MouseEvent e)
    {
    }

    public void mouseExited(MouseEvent e)
    {
    }

    public void mousePressed(MouseEvent e)
    {
    }
          
    public void mouseReleased(MouseEvent e)
    {
    }
}

