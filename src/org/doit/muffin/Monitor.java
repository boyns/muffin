/* $Id: Monitor.java,v 1.6 2000/01/24 04:02:14 boyns Exp $ */

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

/**
 * Interface to monitoring handlers.
 *
 * @see muffin.CanvasMonitor
 * @see muffin.TextMonitor
 * @author Mark Boyns
 */
interface Monitor
{
    /**
     * Register a hander.
     */
    void register(Handler h);
    
    /**
     * Unregister a hander.
     */
    void unregister(Handler h);
    
    /**
     * Update a handler.
     */
    void update(Handler h);
    
    /**
     * Supend the monitor.
     */
    void suspend();
    
    /**
     * Resume the monitor.
     */
    void resume();

    /**
     * Return a list of handlers being monitored.
     */
    Enumeration enumerate();

    /**
     * Minimize the monitor.
     */
    void minimize(boolean enable);
}
