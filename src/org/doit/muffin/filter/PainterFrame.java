/* $Id: PainterFrame.java,v 1.7 2003/06/28 15:03:58 forger77 Exp $ */

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
import java.awt.event.*;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import org.doit.muffin.*;
import org.doit.util.*;

public class PainterFrame extends AbstractFrame implements ItemListener
{
    TextField bgcolor, link, alink, vlink, background, text;
    ColorSample bgcolorSample, linkSample, alinkSample, vlinkSample, textSample;
    Hashtable styleTable = null;
    String styles[] = { getFactory().getString("none"),
                        getFactory().getString("dark"),
                        getFactory().getString("light"),
                        getFactory().getString("xmas") };

    /**
     * @see org.doit.muffin.filter.AbstractFrame#AbstractFrame(AbstractFilterFactory)
     */
    public PainterFrame(AbstractFilterFactory parent)
    {
        super(parent);
    }

    /**
     * 
     * @see org.doit.muffin.filter.AbstractFrame#doMakeContent()
     */
    protected Panel doMakeContent()
    {

        Panel panel = new Panel(new BorderLayout());

        Panel gui = makeGui();
        
        panel.add("Center", gui);
        

        panel.add("South", makeButtonPanel());

        return panel;

    }
    
    private Panel makeGui(){

    Panel panel = new Panel();
    GridBagLayout layout = new GridBagLayout();
    panel.setLayout(layout);
    GridBagConstraints c;
    Label l;

    l = new Label(getFactory().getString("samples")+":", Label.RIGHT);
    c = new GridBagConstraints();
    layout.setConstraints(l, c);
    panel.add(l);

    createStyleTable();

    Choice choice = new Choice();
    choice.addItemListener(this);
    for (int i = 0; i < styles.length; i++)
    {
        choice.addItem(styles[i]);
    }
    c = new GridBagConstraints();
    c.gridwidth = GridBagConstraints.REMAINDER;
    layout.setConstraints(choice, c);
    panel.add(choice);

    l = new Label(getFactory().getString(Painter.BACKGRND)+":", Label.RIGHT);
    c = new GridBagConstraints();
    layout.setConstraints(l, c);
    panel.add(l);

    background = new TextField(32);
    background.setText(getFactory().getPrefsString(Painter.BACKGRND));
    c = new GridBagConstraints();
    c.gridwidth = GridBagConstraints.REMAINDER;
    layout.setConstraints(background, c);
    panel.add(background);

    l = new Label(getFactory().getString(Painter.BGCOLOR)+":", Label.RIGHT);
    c = new GridBagConstraints();
    layout.setConstraints(l, c);
    panel.add(l);

    bgcolor = new TextField(7);
    bgcolor.setText(getFactory().getPrefsString(Painter.BGCOLOR));
    bgcolor.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent event) {
            bgcolorSample.setColor(bgcolor.getText());
        }
    });
    c = new GridBagConstraints();
    layout.setConstraints(bgcolor, c);
    panel.add(bgcolor);

    bgcolorSample = new ColorSample(getFactory().getPrefsString(Painter.BGCOLOR));
    c = new GridBagConstraints();
    c.gridwidth = GridBagConstraints.REMAINDER;
    layout.setConstraints(bgcolorSample, c);
    panel.add(bgcolorSample);


    l = new Label(getFactory().getString(Painter.TEXT)+":", Label.RIGHT);
    c = new GridBagConstraints();
    layout.setConstraints(l, c);
    panel.add(l);

    text = new TextField(7);
    text.setText(getFactory().getPrefsString(Painter.TEXT));
    text.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent event) {
            textSample.setColor(text.getText());
        }
    });
    c = new GridBagConstraints();
    layout.setConstraints(text, c);
    panel.add(text);

    textSample = new ColorSample(getFactory().getPrefsString(Painter.TEXT));
    c = new GridBagConstraints();
    c.gridwidth = GridBagConstraints.REMAINDER;
    layout.setConstraints(textSample, c);
    panel.add(textSample);


    l = new Label(getFactory().getString(Painter.LINK)+":", Label.RIGHT);
    c = new GridBagConstraints();
    layout.setConstraints(l, c);
    panel.add(l);

    link = new TextField(7);
    link.setText(getFactory().getPrefsString(Painter.LINK));
    link.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent event) {
            linkSample.setColor(link.getText());
        }
    });
    c = new GridBagConstraints();
    layout.setConstraints(link, c);
    panel.add(link);

    linkSample = new ColorSample(getFactory().getPrefsString(Painter.LINK));
    c = new GridBagConstraints();
    c.gridwidth = GridBagConstraints.REMAINDER;
    layout.setConstraints(linkSample, c);
    panel.add(linkSample);


    l = new Label(getFactory().getString(Painter.VLINK)+":", Label.RIGHT);
    c = new GridBagConstraints();
    layout.setConstraints(l, c);
    panel.add(l);

    vlink = new TextField(7);
    vlink.setText(getFactory().getPrefsString(Painter.VLINK));
    vlink.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent event) {
            vlinkSample.setColor(vlink.getText());
        }
    });
    c = new GridBagConstraints();
    layout.setConstraints(vlink, c);
    panel.add(vlink);

    vlinkSample = new ColorSample(getFactory().getPrefsString(Painter.VLINK));
    c = new GridBagConstraints();
    c.gridwidth = GridBagConstraints.REMAINDER;
    layout.setConstraints(vlinkSample, c);
    panel.add(vlinkSample);


    l = new Label(getFactory().getString(Painter.ALINK)+":", Label.RIGHT);
    c = new GridBagConstraints();
    layout.setConstraints(l, c);
    panel.add(l);

    alink = new TextField(7);
    alink.setText(getFactory().getPrefsString(Painter.ALINK));
    alink.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent event) {
            alinkSample.setColor(alink.getText());
        }
    });
    c = new GridBagConstraints();
    layout.setConstraints(alink, c);
    panel.add(alink);

    alinkSample = new ColorSample(getFactory().getPrefsString(Painter.ALINK));
    c = new GridBagConstraints();
    c.gridwidth = GridBagConstraints.REMAINDER;
    layout.setConstraints(alinkSample, c);
    panel.add(alinkSample);
    return panel;
    
    }
    
    /**
     * 
     * @see org.doit.muffin.filter.AbstractFrame#doMakeButtonList()
     */
    protected String[] doMakeButtonList()
    {
        return new String[] {
            APPLY_CMD,
            SAVE_CMD,
            CLOSE_CMD,
            HELP_CMD };
    }

    void createStyleTable()
    {
    Vector v;

    styleTable = new Hashtable(13);

    /* None */
    v = new Vector();
    v.addElement("None"); // bgcolor
    v.addElement("None"); // text
    v.addElement("None"); // link
    v.addElement("None"); // vlink
    v.addElement("None"); // alink
    styleTable.put(styles[0], v);

    /* Dark */
    v = new Vector();
    v.addElement("#000000"); // bgcolor
    v.addElement("#ffffff"); // text
    v.addElement("#98fb98"); // link
    v.addElement("#ffa07a"); // vlink
    v.addElement("#ff0000"); // alink
    styleTable.put(styles[1], v);

    /* Light */
    v = new Vector();
    v.addElement("#ffffff"); // bgcolor
    v.addElement("#000000"); // text
    v.addElement("#0000ee"); // link
    v.addElement("#551a8b"); // vlink
    v.addElement("#ff0000"); // alink
    styleTable.put(styles[2], v);

    /* Christmas */
    v = new Vector();
    v.addElement("#ffffff"); // bgcolor
    v.addElement("#000000"); // text
    v.addElement("#00ff00"); // link
    v.addElement("#ff0000"); // vlink
    v.addElement("#ff0000"); // alink
    styleTable.put(styles[3], v);
    }

    void updateSamples()
    {
    bgcolorSample.setColor(bgcolor.getText());
    textSample.setColor(text.getText());
    linkSample.setColor(link.getText());
    vlinkSample.setColor(vlink.getText());
    alinkSample.setColor(alink.getText());
    }

    /**
     * 
     * @see org.doit.muffin.filter.AbstractFrame#doApply()
     */
    protected void doApply()
    {
        getFactory().putPrefsString(
            Painter.BGCOLOR,
            bgcolor.getText());
        getFactory().putPrefsString(
            Painter.BACKGRND,
            background.getText());
        getFactory().putPrefsString(
            Painter.TEXT,
            text.getText());
        getFactory().putPrefsString(
            Painter.LINK,
            link.getText());
        getFactory().putPrefsString(
            Painter.VLINK,
            vlink.getText());
        getFactory().putPrefsString(
            Painter.ALINK,
            alink.getText());
    }

    public void itemStateChanged(ItemEvent event)
    {
    Vector v = (Vector) styleTable.get(event.getItem().toString());
    bgcolor.setText((String) v.elementAt(0));
    text.setText((String) v.elementAt(1));
    link.setText((String) v.elementAt(2));
    vlink.setText((String) v.elementAt(3));
    alink.setText((String) v.elementAt(4));
    updateSamples();
    }
    
}

