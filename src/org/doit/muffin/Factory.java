/* $Id: Factory.java,v 1.5 2000/01/24 04:02:13 boyns Exp $ */

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

/**
 * @author Mark Boyns
 */
public interface Factory
{
    /**
     * Set the manager of this factory.  The manager is normally
     * used to save preferences in the main preferences file.
     */
    public void setManager(FilterManager m);

    /**
     * Set factory preferences.
     */
    public void setPrefs(Prefs p);

    /**
     * Return factory preferences.
     */
    public Prefs getPrefs();

    /**
     * View factory preferences.
     */
    public void viewPrefs();

    /**
     * Shutdown the factory.  Method called when factory is disabled.
     */
    public void shutdown();
}
