/* $Id: AnimationKillerFrame.java,v 1.11 2006/10/28 19:12:40 forger77 Exp $ */
package org.doit.muffin.filter;

import java.awt.*;

public class AnimationKillerFrame extends AbstractFrame
{

    //	FIXME: use this constructor:
    //	public AnimationKillerFrame(String name, FilterFactory parent) {
    public AnimationKillerFrame(AbstractFilterFactory parent)
    {
        super(parent);
    }

    /**
     * 
     * @see org.doit.muffin.filter.AbstractFrame#doMakeContent()
     */
    protected Panel doMakeContent()
    {
        Panel p = new Panel(new BorderLayout());
        p.add(BorderLayout.NORTH, makeTopPanel());
        getFactory().getMessages().setEditable(false);
        p.add(BorderLayout.CENTER, getFactory().getMessages());
        p.add(BorderLayout.SOUTH, makeButtonPanel());
        return p;
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
            CLEAR_CMD,
            CLOSE_CMD,
            HELP_CMD };
    }

    /**
     * Utility method that constructs the top panel.
     * @return Panel The constructed top panel.
     */
    private Panel makeTopPanel()
    {
        Panel panel = new Panel();
        GridBagLayout layout = new GridBagLayout();
        panel.setLayout(layout);
        GridBagConstraints c;

        fBreakem = new Checkbox(getFactory().getString(AnimationKiller.BREAK));
        fBreakem.setState(getFactory().getPrefsBoolean(AnimationKiller.BREAK));
        c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        layout.setConstraints(fBreakem, c);
        panel.add(fBreakem);

        Label label =
            new Label(getFactory().getString(AnimationKiller.MAXLOOPS) + ":", Label.RIGHT);
        c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        layout.setConstraints(label, c);
        panel.add(label);

        fMaxLoops = new TextField(2);
        fMaxLoops.setText(getFactory().getPrefsString(AnimationKiller.MAXLOOPS));
        c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.anchor = GridBagConstraints.WEST;
        layout.setConstraints(fMaxLoops, c);
        panel.add(fMaxLoops);
        return panel;
    }

    /**
     * 
     * @see org.doit.muffin.filter.AbstractFrame#doApply()
     */
    protected void doApply()
    {
        getFactory().putPrefsString(AnimationKiller.MAXLOOPS, fMaxLoops.getText());
        getFactory().putPrefsBoolean(AnimationKiller.BREAK, fBreakem.getState());
    }

    private Checkbox fBreakem;
    private TextField fMaxLoops;

}
