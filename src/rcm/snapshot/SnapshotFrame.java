/* $Id: SnapshotFrame.java,v 1.1 2000/10/10 04:51:09 boyns Exp $ */
package rcm.snapshot;

import java.awt.*;
import java.awt.event.*;
import org.doit.muffin.*;

public class SnapshotFrame extends MuffinFrame implements ActionListener, WindowListener
{
    Prefs prefs;
    Snapshot parent;
    TextField directory;
    Checkbox capturing;
    Checkbox replaying;
    
    public SnapshotFrame(Prefs prefs, Snapshot parent)
    {
	super("Muffin: Snapshot");

	this.prefs = prefs;
	this.parent = parent;

	GridBagLayout layout = new GridBagLayout();
        setLayout(layout);
	GridBagConstraints c;
	Label l;
	
	add(new Label("Directory:", Label.RIGHT));

	directory = new TextField(40);
	directory.setText(prefs.getString("Snapshot.directory"));
	c = new GridBagConstraints();
	c.anchor = GridBagConstraints.NORTHWEST;
	c.gridwidth = 2;
	layout.setConstraints(directory, c);
	add(directory);

	Button browse = new Button("Browse...");
	browse.setActionCommand("doBrowse");
	browse.addActionListener(this);
	c = new GridBagConstraints();
	c.anchor = GridBagConstraints.NORTHWEST;
	c.gridwidth = 1;//GridBagConstraints.RELATIVE;
	layout.setConstraints(browse, c);
	add(browse);
	
	c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
	Button b = new Button("Clear Snapshot");
	b.setActionCommand("doClear");
	b.addActionListener(this);
	add(b, c);

	c.gridx = 0;
        c.gridy = 2;
	add(new Label("Mode:", Label.RIGHT), c);

        CheckboxGroup group = new CheckboxGroup ();
        boolean replayMode = prefs.getBoolean ("Snapshot.replaying");
        capturing = new Checkbox ("Capturing", !replayMode, group);
        replaying = new Checkbox ("Replaying", replayMode, group);
        c.gridx = 1;
        add (capturing, c);
        c.gridy = 3;
        add (replaying, c);

	Panel buttonPanel = new Panel();
	buttonPanel.setLayout(new GridLayout(1, 4));
	b = new Button("Apply");
	b.setActionCommand("doApply");
	b.addActionListener(this);
	buttonPanel.add(b);
	b = new Button("Save");
	b.setActionCommand("doSave");
	b.addActionListener(this);
	buttonPanel.add(b);
	b = new Button("Close");
	b.setActionCommand("doClose");
	b.addActionListener(this);
	buttonPanel.add(b);
	b = new Button("Help");
	b.setActionCommand("doHelp");
	b.addActionListener(this);
	buttonPanel.add(b);
	add("South", buttonPanel);

	c = new GridBagConstraints();
	c.insets = new Insets(0, 10, 5, 10);
	c.gridx = 0;
	c.gridwidth = 5;
	c.fill = GridBagConstraints.HORIZONTAL;
	layout.setConstraints(buttonPanel, c);
	add(buttonPanel);

	addWindowListener(this);
	
	pack();
	setSize(getPreferredSize());

	show();
    }

    public void actionPerformed(ActionEvent event)
    {
	String arg = event.getActionCommand();
	
	if ("doApply".equals(arg))
	{
	    prefs.putString("Snapshot.directory", directory.getText());
	    prefs.putBoolean("Snapshot.replaying", replaying.getState ());
            parent.loadMap ();
	}
	else if ("doSave".equals(arg))
	{
	    parent.save();
	}
	else if ("doClose".equals(arg))
	{
	    setVisible(false);
	}
	else if ("doClear".equals(arg))
	{
	    parent.clearSnapshot ();
	}
	else if ("doBrowse".equals(arg))
	{
	    FileDialog dialog = new FileDialog(this, "Snapshot Directory");
	    dialog.show();
	    if (dialog.getFile() != null)
	    {
		directory.setText(dialog.getDirectory());
	    }
	}
	else if ("doHelp".equals(arg))
	{
	    new HelpFrame("Snapshot");
	}
    }

    public void windowActivated(WindowEvent e)
    {
    }
  
    public void windowDeactivated(WindowEvent e)
    {
    }
  
    public void windowClosing(WindowEvent e)
    {
	setVisible(false);
    }
  
    public void windowClosed(WindowEvent e)
    {
    }
  
    public void windowIconified(WindowEvent e)
    {
    }
  
    public void windowDeiconified(WindowEvent e)
    {
    }
  
    public void windowOpened(WindowEvent e)
    {
    }
}
