/**
 * ImageKill.java -- kill images that match a certain size ratio
 *
 * @author  Heinrich Opgenoorth <opgenoorth@gmd.de>
 * @version 0.2
 *
 * Last update: 98/11/30 H.O.
 */

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
package org.doit.muffin.filter;

import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import org.doit.muffin.Filter;
import org.doit.muffin.regexp.Factory;
import org.doit.muffin.regexp.Pattern;

public class ImageKill extends AbstractFilterFactory
{

    final static String MINHEIGHT_PREF = "minheight";
    final static String MINWIDTH_PREF = "minwidth";
    final static String RATIO_PREF = "ratio";
    final static String KEEPMAPS_PREF = "keepmaps";
    final static String EXCLUDE_PREF = "exclude";
    final static String RMSIZES_PREF = "rmSizes";
    final static String REPLACE_PREF = "replace";
    final static String REPLACEURL_PREF = "replaceURL";

    /**
     * @see org.doit.muffin.filter.AbstractFilterFactory#doSetDefaultPrefs()
     */
    protected void doSetDefaultPrefs()
    {
        putPrefsInteger(MINHEIGHT_PREF, 49);
        putPrefsInteger(MINWIDTH_PREF, 49);
        putPrefsInteger(RATIO_PREF, 6);
        putPrefsBoolean(KEEPMAPS_PREF, true);
        putPrefsString(EXCLUDE_PREF, "(button|map)");
        putPrefsString(RMSIZES_PREF, "468x60,450x40");
        putPrefsBoolean(REPLACE_PREF, false);
        putPrefsString(REPLACEURL_PREF, "file:/usr/local/images/empty.gif");
        setExclude();
        setRemoveSizes();
    }

    /**
     * @see org.doit.muffin.filter.AbstractFilterFactory#doMakeFrame()
     */
    protected AbstractFrame doMakeFrame()
    {
        return new ImageKillFrame(this);
    }

    /**
     * @see org.doit.muffin.filter.AbstractFilterFactory#doMakeFilter()
     */
    protected Filter doMakeFilter()
    {
        return new ImageKillFilter(this);
    }

    /**
     * @see org.doit.muffin.filter.AbstractFilterFactory#getName()
     */
    public String getName()
    {
        return "ImageKill";
    }

    /********************************************************************
     * Creates (from the preferences) a regexp to match images that
     * we should <b>not</b> try to remove.
     */

    void setExclude()
    {
        exclude = null;
        String ex = getPrefsString(EXCLUDE_PREF);
        if (ex != null && !ex.equals(""))
        {
            exclude = Factory.instance().getPattern(ex);
        }
    }

    /********************************************************************
     * Checks if a given string (image source attribute) matches the
     * regexp for exclusion from removal.
     *
     * @return   True if the string matches the exclusion regexp.
     */

    boolean isExcluded(String s)
    {
        return (s != null && exclude != null && exclude.matches(s));
    }

    /********************************************************************
     * Fetches (from the preferences) the list of image geometries
     * to be removed, and converts it from string format to a
     * hashtable (for easy matching later). The width is used as
     * a key, with all heights for that key stored in a vector.
     */

    void setRemoveSizes()
    {
        String size;
        Integer w, h;
        Vector hlist;

        try
        {
            removeSizes = new Hashtable();
            String rm = getPrefsString(RMSIZES_PREF);
            if (rm == null || rm.equals(""))
            {
                return;
            }
            StringTokenizer st = new StringTokenizer(rm, ",");
            while (st.hasMoreTokens())
            {
                size = st.nextToken();
                StringTokenizer st2 = new StringTokenizer(size, "x");
                w = new Integer(Integer.parseInt(st2.nextToken()));
                h = new Integer(Integer.parseInt(st2.nextToken()));
                /* If this is the first entry for that width, we have
                 * to create the vector to store the height. If not,
                 * we just need to add a new element to that vector. */
                hlist = (Vector) removeSizes.get(w);
                if (hlist == null)
                {
                    hlist = new Vector();
                    removeSizes.put(w, hlist);
                }
                hlist.addElement(h);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /********************************************************************
     * Checks if a given image geometry is contained in the list of
     * geometries that should be removed/replaced.
     *
     * @return  True if the given geometry is found in the list of
     *          geometries to remove, false otherwise.
     */

    boolean inRemoveSizes(int width, int height)
    {
        Vector hlist;
        Integer w;

        if (removeSizes != null)
        {
            try
            {
                w = new Integer(width);
                hlist = (Vector) removeSizes.get(w);
                if (hlist != null && hlist.contains(new Integer(height)))
                {
                    return true;
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return false;
    }
    private Pattern exclude = null;
    private Hashtable removeSizes = null;
}
