/* $Id: PainterFrame.java,v 1.8 2003/06/29 16:17:33 forger77 Exp $ */

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

//import java.awt;
import java.awt.Choice;
import java.awt.Panel;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.*;
import java.util.Hashtable;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.Map;
import java.util.HashMap;
import org.doit.muffin.*;
import org.doit.util.*;

public class PainterFrame extends AbstractFrame
{

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
    choice.addItemListener(new ItemListener(){
        public void itemStateChanged(ItemEvent event)
        {
        java.util.List styleList = (java.util.List) fStyleTable.get(event.getItem().toString());
        Iterator sli = styleList.iterator();
        int i = 0;
        while(sli.hasNext()){
            Sample sample = (Sample)fSamples.get(SAMPLE_KEYS[i]);
            sample.setText((String)sli.next());
            i++;
        }
        }
    });
    
    Iterator styleIt = fStyles.iterator();
    while(styleIt.hasNext()){
        choice.addItem((String)styleIt.next());
    }
 
    c = new GridBagConstraints();
    c.gridwidth = GridBagConstraints.REMAINDER;
    layout.setConstraints(choice, c);
    panel.add(choice);

    l = new Label(getFactory().getString(Painter.BACKGRND)+":", Label.RIGHT);
    c = new GridBagConstraints();
    layout.setConstraints(l, c);
    panel.add(l);

    TextField background = new TextField(32);
    background.setText(getFactory().getPrefsString(Painter.BACKGRND));
    c = new GridBagConstraints();
    c.gridwidth = GridBagConstraints.REMAINDER;
    layout.setConstraints(background, c);
    panel.add(background);
            
    for(int i = 0; i< SAMPLE_KEYS.length; i++){
        fSamples.put(SAMPLE_KEYS[i], makeSample(layout, panel, SAMPLE_KEYS[i]));
    }
    
    return panel;
    
    }
    
    private Sample makeSample(GridBagLayout layout, Panel panel, String key){
    Label l = new Label(getFactory().getString(key)+":", Label.RIGHT);
    GridBagConstraints c = new GridBagConstraints();
    layout.setConstraints(l, c);
    panel.add(l);

    TextField textField = new TextField(7);
    textField.setText(getFactory().getPrefsString(key));
    c = new GridBagConstraints();
    layout.setConstraints(textField, c);
    panel.add(textField);

    ColorSample colorSample = new ColorSample(getFactory().getPrefsString(key));
    c = new GridBagConstraints();
    c.gridwidth = GridBagConstraints.REMAINDER;
    layout.setConstraints(colorSample, c);
    panel.add(colorSample);
    Sample sample = new Sample(textField, colorSample);
    textField.addActionListener(sample);
    textField.addFocusListener(sample);
    
    return sample;
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

    private void createStyleTable()
    {

    fStyles = new ArrayList();
    fStyleTable = new HashMap(fStyles.size());
    
    java.util.List colors;

    String style;
    
    style = "none";
    style = getFactory().getString(style);
    fStyles.add(style);
    colors = new ArrayList();
    colors.add("None"); // fBgcolor
    colors.add("None"); // fText
    colors.add("None"); // fLink
    colors.add("None"); // fVlink
    colors.add("None"); // fAlink
    fStyleTable.put(style, colors);

    style = "dark";
    style = getFactory().getString(style);
    fStyles.add(style);
    colors = new ArrayList();
    colors.add("#000000"); // fBgcolor
    colors.add("#ffffff"); // fText
    colors.add("#98fb98"); // fLink
    colors.add("#ffa07a"); // fVlink
    colors.add("#ff0000"); // fAlink
    fStyleTable.put(style, colors);

    style = "light";
    style = getFactory().getString(style);
    fStyles.add(style);
    colors = new ArrayList();
    colors.add("#ffffff"); // fBgcolor
    colors.add("#000000"); // fText
    colors.add("#0000ee"); // fLink
    colors.add("#551a8b"); // fVlink
    colors.add("#ff0000"); // fAlink
    fStyleTable.put(style, colors);

    style = "xmas";
    style = getFactory().getString(style);
    fStyles.add(style);
    colors = new ArrayList();
    colors.add("#ffffff"); // fBgcolor
    colors.add("#000000"); // fText
    colors.add("#00ff00"); // fLink
    colors.add("#ff0000"); // fVlink
    colors.add("#ff0000"); // fAlink
    fStyleTable.put(style, colors);
    }

    /**
     * 
     * @see org.doit.muffin.filter.AbstractFrame#doApply()
     */
    protected void doApply()
    {
        for(int i=0;i<SAMPLE_KEYS.length;i++){
            Sample su = (Sample)fSamples.get(SAMPLE_KEYS[i]);
            getFactory().putPrefsString(SAMPLE_KEYS[i],su.getText());
        }
    }

    class Sample extends FocusAdapter implements ActionListener {
        Sample(TextField tf, ColorSample cs){
            fTextField = tf;
            fColorSample = cs;
        }
        public void actionPerformed(ActionEvent event) {
            fColorSample.setColor(fTextField.getText());
        }
        public void focusLost(FocusEvent e){
            fColorSample.setColor(fTextField.getText());
        }
        public TextField getTextField(){
            return fTextField;
        }
        public void setTextField(TextField tf){
            fTextField = tf;
        }
        public ColorSample getColorSample(){
            return fColorSample;
        }
        public void setText(String text){
            fTextField.setText(text);
            syncSampleWithText();
        }
        public String getText(){
            return fTextField.getText();
        }
        private void syncSampleWithText(){
            fColorSample.setColor(fTextField.getText());
        }
        private TextField fTextField;
        private ColorSample fColorSample;
    }
    
    private static final String[] SAMPLE_KEYS = new String[] {
            Painter.BGCOLOR,
            Painter.TEXT,
            Painter.LINK,
            Painter.VLINK,
            Painter.ALINK
    };
    private Map fSamples = new HashMap(SAMPLE_KEYS.length);
    private HashMap fStyleTable = null;
    private List fStyles;

}

