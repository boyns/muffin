/* $Id: ProxyCacheBypassFilter.java,v 1.1 2003/05/25 02:51:50 cmallwitz Exp $ */

/*
 * Copyright (C) 2003 Bernhard Wagner <bw@xmlizer.biz>
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.Frame;
import java.awt.Panel;

import java.util.Map;
import java.util.HashMap;

import org.doit.muffin.FilterFactory;
import org.doit.muffin.HelpFrame;
import org.doit.muffin.MuffinFrame;
import org.doit.muffin.Prefs;
import org.doit.util.Strings;


public abstract class AbstractFrame implements ActionListener, WindowListener {

//	FIXME: use this constructor:
//    public AbstractFrame(String name, FilterFactory parent){
	/**
	 * Constructor for AbstractFrame
	 * @param name The name of the FilterFactory associated with this Frame.
	 * @param parent The FilterFactory associated with this Frame.	 */
    public AbstractFrame(AbstractFilterFactory parent){
    	fName = new StringBuffer(parent.getName());
    	fFactory = parent;
    	fFrame = new MuffinFrame(Strings.getString(fFactory.makeNameSpace("title")));
		fFrame.addWindowListener(this);
		appendAction(new ApplyAction());
		appendAction(new SaveAction());
		appendAction(new ClearAction());
		appendAction(new CloseAction());
		appendAction(new HelpAction());
	}
    
    final void setVisible(boolean visible){
    	fFrame.setVisible(visible);
    }
    
    final void dispose(){
    	fFrame.dispose();
    }
    
    protected final Frame getFrame(){
    	return fFrame;
    }
    
    protected abstract Panel doMakeContent();
    
//    protected FilterFactory getParent(){
    protected final AbstractFilterFactory getFactory(){
    	return fFactory;
    }

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public final void actionPerformed(ActionEvent event) {
		String arg = event.getActionCommand();
		if(fActions.containsKey(arg)){
			((Action)fActions.get(arg)).perform();
		} else {
			throw new RuntimeException("actionPerformed invoked with inexistant"+
			" action:"+arg);
		}
	}

	public void windowOpened(WindowEvent arg0) {}

	/**
	 * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
	 */
	public void windowClosing(WindowEvent arg0) {
		fFrame.setVisible(false);
	}

	public void windowClosed(WindowEvent arg0) {}

	public void windowIconified(WindowEvent arg0) {}

	public void windowDeiconified(WindowEvent arg0) {}

	public void windowActivated(WindowEvent arg0) {}

	public void windowDeactivated(WindowEvent arg0) {}
	
	final void consolidate(){

		appendActions();
		
		fFrame.add("Center", doMakeContent());

		fFrame.pack();
		fFrame.setSize(fFrame.getPreferredSize());

		fFrame.show();
	}
	
	/**
	 * Hook method to be overridden by subclasses. This method gets called when the Button
	 * @see org.doit.muffin.filter.AbstractFrame#APPLY_CMD gets clicked.	 */
	protected void doApply(){}
	
	class ApplyAction implements Action {
		public String getName(){
			return APPLY_CMD;
		}
		public void perform(){
			doApply();
		}
	}
	
	class SaveAction implements Action {
		public String getName(){
			return SAVE_CMD;
		}
		public void perform(){
			doApply();
			fFactory.save();
		}
	}
	
	class ClearAction implements Action {
		public String getName(){
			return CLEAR_CMD;
		}
		public void perform(){
			if(fFactory.getMessages() != null) fFactory.getMessages().clear();
		}
	}
	
	class CloseAction implements Action {
		public String getName(){
			return CLOSE_CMD;
		}
		public void perform(){
			setVisible(false);
		}
	}

	class HelpAction implements Action {
		public String getName(){
			return HELP_CMD;
		}
		public void perform(){
			new HelpFrame(fName.toString());
		}
	}
	
	/**
	 * Note that this is the only inner Action-class that will not be added by default.
	 */
	class LoadAction implements Action {
		/**
		 * Default Ctor for LoadAction. Name will be @see 
		 * org.doit.muffin.filter.AbstractFrame#LOAD_ACTION		 * 		 */
		LoadAction(){}

		/**
		 * Constructor for LoadAction. Name will be what is supplied in the name parameter.		 * @param name Name under which this LoadAction is to be registered.		 */
		LoadAction(String name){
			fName = name;
		}
		public String getName() {
			return fName;
		}
		public void perform() {
			fFactory.doLoad();
		}
		private String fName = LOAD_CMD;
	}
	
	/**
	 * Utility method provided for implementors to build a Panel of Buttons.
	 * To be used in conjunction with
	 * @see org.doit.muffin.filter.AbstractFrame#doMakeButtonList()	 * @return Panel	 */
	protected final Panel makeButtonPanel(){
		Button b;
		Panel buttonPanel = new Panel();
		String[] cmds = doMakeButtonList();
		buttonPanel.setLayout(new GridLayout(1, cmds.length));
		for(int i = 0; i< cmds.length;i++){
			buttonPanel.add(makeButton(Strings.getString(cmds[i]), cmds[i]));
		}
		return buttonPanel;
	}
	
	/**
	 * Utility method for creating a Button and provide for treating of events.
	 * 	 * @param label The label of the Button to be created.	 * @param command The name of the command to be invoked when the button gets clicked.	 * @return Button The Button created.	 */
	protected final Button makeButton(String label, String command) {
		Button button = new Button(label);
		button.setActionCommand(command);
		button.addActionListener(this);
		return button;
	}

	/**
	 * Override this method to supply your own list of Buttons.
	 * This list will be the key for Lookup of the actual caption of the 
	 * Button name via static Strings.getString(key).
	 * To add your own Button you need to create a corresponding Action-class.
	 * The key of a Button must correspond with geName() of the Action you provide.
	 * Note: You don't need and shouldn't add Actions for:
	 * <UL>
	 * <li> @see org.doit.muffin.AbstractFrame.HELP_CMD </li>
	 * <li> @see org.doit.muffin.AbstractFrame.CLOSE_CMD </li>
	 * <li> @see org.doit.muffin.AbstractFrame.CLEAR_CMD </li>
	 * <li> @see org.doit.muffin.AbstractFrame.SAVE_CMD </li>
	 * <li> @see org.doit.muffin.AbstractFrame.APPLY_CMD </li>
	 * </UL>
	 * since these are already in place.
	 * @see org.doit.muffin.AbstractFrame.LOAD_CMD is the only inner Action-class
	 * that is not instantiated by default. But it is provided for implementors, e.g.
	 * @see org.doit.muffin.GlossaryFrame.
	 * 	 * @return String[] The list of keys for the Buttons	 */
	protected String[] doMakeButtonList(){
		return new String[0];
	}
		
	/**
	 * Override this method to supply your own list of Actions.
	 * These will be used in 
	 * @see org.doit.muffin.filter.AbstractFrame#actionPerformed(ActionEvent)
	 * to determine what Action to invoke.
	 * Some actions are already present. See description for 
	 * @see org.doit.muffin.filter.AbstractFrame#doMakeButtonList()
	 * 
	 * @return String[] The list of Actions
	 */
	protected Action[] doMakeActions(){
		return new Action[0];
	}
	
	/**
	 * Utility method to append an array of Actions to the List of Actions.
	 * @see org.doit.muffin.filter.AbstractFrame#doMakeActions()
	 * 
	 * @param action The Action array to append ot the List of Actions.
	 */
	private void appendActions(){
		Action[] actions = doMakeActions();
		for(int i= 0;i<actions.length;i++){
			appendAction(actions[i]);
		}
	}

	/**
	 * Utility method to append an Action to the List of Actions.
	 * @see org.doit.muffin.filter.AbstractFrame#doMakeActions()
	 * 	 * @param action The Action to append ot the List of Actions.	 */
    private void appendAction(Action action){
    	if(fActions.containsKey(action.getName())){
    		throw new RuntimeException("AbstractFrame.appendAction: "+
				"trying to add action that already exists:"+action.getName());
    	}
    	fActions.put(action.getName(), action);
    }

	protected static final String HELP_CMD  = "help";
	protected static final String CLOSE_CMD = "close";
	protected static final String CLEAR_CMD = "clear";
	protected static final String SAVE_CMD  = "save";
	protected static final String APPLY_CMD = "apply";
	protected static final String LOAD_CMD  = "load";
	
	private Frame fFrame;
//	private FilterFactory fFactory;
	private AbstractFilterFactory fFactory; // FIXME: replace FilterFactory by AbstractFilterFactory
	private StringBuffer fName;
	private Map fActions = new HashMap();

}
