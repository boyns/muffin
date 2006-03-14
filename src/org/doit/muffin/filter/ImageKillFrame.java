/**
 * ImageKillFrame.java -- kill images that match a certain size ratio
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

import java.awt.*;

public class ImageKillFrame extends AbstractFrame
{

    /**
     * @see org.doit.muffin.filter.AbstractFrame#AbstractFrame(AbstractFilterFactory)
     */
    public ImageKillFrame(ImageKill factory)
    {
        super(factory);
        fFactory = factory;
    }

    /**
     * @see org.doit.muffin.filter.AbstractFrame#doMakeContent()
     */
    protected Panel doMakeContent()
    {

        Panel panel = new Panel(new BorderLayout());

        panel.add("North", makeConfigPanel());

        getFactory().getMessages().setEditable(false);
        panel.add("Center", getFactory().getMessages());

        panel.add("South", makeButtonPanel());

        return panel;

    }

    /**
     * Utility method that constructs the config panel.
     * @return Panel The constructed config panel.
     */
    private Panel makeConfigPanel()
    {
        Panel panel = new Panel();
        GridBagLayout layout = new GridBagLayout();
        panel.setLayout(layout);

        l_title = new Label("Remove images if:");
        l_wider = new Label("width >", Label.RIGHT);
        l_higher = new Label("and height >", Label.RIGHT);
        l_ratio = new Label("and w/h ratio >", Label.RIGHT);
        l_ex = new Label("but not if img.src contains ");
        l_fixed = new Label("Fixed sizes to remove ");

        t_minheight = new TextField(5);
        t_minwidth = new TextField(5);
        t_ratio = new TextField(5);
        t_exclude = new TextField(30);
        t_rmSizes = new TextField(30);
        t_replaceURL = new TextField(30);
        cb_keepmaps = new Checkbox("Don't remove image maps");
        cb_replace = new Checkbox("Replace with URL ");

        t_minheight.setText(
            Integer.toString(
                getFactory().getPrefsInteger(ImageKill.MINHEIGHT_PREF)));
        t_minwidth.setText(
            Integer.toString(
                getFactory().getPrefsInteger(ImageKill.MINWIDTH_PREF)));
        t_ratio.setText(
            Integer.toString(
                getFactory().getPrefsInteger(ImageKill.RATIO_PREF)));
        t_exclude.setText(getFactory().getPrefsString(ImageKill.EXCLUDE_PREF));
        t_rmSizes.setText(getFactory().getPrefsString(ImageKill.RMSIZES_PREF));
        t_replaceURL.setText(
            getFactory().getPrefsString(ImageKill.REPLACEURL_PREF));
        cb_keepmaps.setState(
            getFactory().getPrefsBoolean(ImageKill.KEEPMAPS_PREF));
        cb_replace.setState(
            getFactory().getPrefsBoolean(ImageKill.REPLACE_PREF));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0, 2, 0, 2);
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        layout.setConstraints(l_title, c);
        panel.add(l_title);

        Panel p2 = new Panel();
        p2.add(l_wider);
        p2.add(t_minwidth);
        p2.add(l_higher);
        p2.add(t_minheight);
        p2.add(l_ratio);
        p2.add(t_ratio);

        c.gridy = 1;
        c.gridwidth = 2;
        layout.setConstraints(p2, c);
        panel.add(p2);

        c.gridy = 2;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.EAST;
        layout.setConstraints(l_ex, c);
        panel.add(l_ex);

        c.gridy = 3;
        layout.setConstraints(l_fixed, c);
        panel.add(l_fixed);

        c.gridy = 4;
        layout.setConstraints(cb_replace, c);
        panel.add(cb_replace);

        c.gridy = 5;
        c.anchor = GridBagConstraints.WEST;
        layout.setConstraints(cb_keepmaps, c);
        panel.add(cb_keepmaps);

        c.gridx = 1;
        c.gridy = 2;
        layout.setConstraints(t_exclude, c);
        panel.add(t_exclude);

        c.gridy = 3;
        layout.setConstraints(t_rmSizes, c);
        panel.add(t_rmSizes);

        c.gridy = 4;
        layout.setConstraints(t_replaceURL, c);
        panel.add(t_replaceURL);

        return panel;
    }

    /**
     * @see org.doit.muffin.filter.AbstractFrame#doMakeButtonList()
     */
    protected String[] doMakeButtonList()
    {
        return new String[] {
            APPLY_CMD,
            SAVE_CMD,
            CLEAR_CMD,
            CLOSE_CMD,
            HELP_CMD };
    }

    /**
     * @see org.doit.muffin.filter.AbstractFrame#doApply()
     */
    protected void doApply()
    {
        getFactory().putPrefsInteger(
            ImageKill.MINHEIGHT_PREF,
            Integer.parseInt(t_minheight.getText()));
        getFactory().putPrefsInteger(
            ImageKill.MINWIDTH_PREF,
            Integer.parseInt(t_minwidth.getText()));
        getFactory().putPrefsInteger(
            ImageKill.RATIO_PREF,
            Integer.parseInt(t_ratio.getText()));
        getFactory().putPrefsBoolean(
            ImageKill.KEEPMAPS_PREF,
            cb_keepmaps.getState());
        getFactory().putPrefsString(
            ImageKill.EXCLUDE_PREF,
            t_exclude.getText());
        getFactory().putPrefsString(
            ImageKill.RMSIZES_PREF,
            t_rmSizes.getText());
        getFactory().putPrefsString(
            ImageKill.REPLACEURL_PREF,
            t_replaceURL.getText());
        getFactory().putPrefsBoolean(
            ImageKill.REPLACE_PREF,
            cb_replace.getState());
        fFactory.setExclude();
        fFactory.setRemoveSizes();
    }

    private TextField t_minheight,
        t_minwidth,
        t_ratio,
        t_exclude,
        t_rmSizes,
        t_replaceURL;
    private Label l_title, l_wider, l_higher, l_ratio, l_ex, l_fixed;
    private Checkbox cb_keepmaps, cb_replace;

    private ImageKill fFactory;
}
