/*
 *  gnu/regexp/util/REApplet.java
 *  Copyright (C) 1998 Wes Biggs
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published
 *  by the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package gnu.regexp.util;
import java.applet.*;
import java.awt.*;
import gnu.regexp.*;

/**
 * This is a simple applet to demonstrate the capabilities of gnu.regexp.
 * To run it, use appletviewer on the reapplet.html file included in the
 * documentation directory.
 *
 * @author <A HREF="mailto:wes@cacas.org">Wes Biggs</A>
 * @version 1.01
 */
public class REApplet extends Applet {
  private Label l1, l2, l3;
  private Button b;
  private TextField tf;
  private TextArea input, output;
  private Checkbox insens;

  /** Creates an REApplet. */
  public REApplet() { super(); }

  /** Initializes the applet and constructs GUI elements. */
  public void init() {
    // test run RE stuff to cache gnu.regexp.* classes.
    try {
      RE x = new RE("^.*(w[x])\1$");
      REMatchEnumeration xx = x.getMatchEnumeration("wxwx");
      while (xx.hasMoreMatches()) xx.nextMatch().toString();
    } catch (REException arg) { }

    setBackground(Color.lightGray);

    GridBagLayout gbag = new GridBagLayout();
    setLayout(gbag);
    GridBagConstraints c = new GridBagConstraints();

    Panel p = new Panel();
    GridBagLayout gbag2 = new GridBagLayout();
    p.setLayout(gbag2);

    c.weightx = 1.0;
    l1 = new Label("Regular Expression");
    gbag2.setConstraints(l1,c);
    p.add(l1);

    tf = new TextField(getParameter("regexp"),30);
    gbag2.setConstraints(tf,c);
    p.add(tf);
    
    b = new Button("Match");
    c.gridwidth = GridBagConstraints.REMAINDER;
    gbag2.setConstraints(b,c);
    p.add(b);

    int z = c.gridx;
    c.gridx = 1;
    c.anchor = GridBagConstraints.WEST;
    insens = new Checkbox("Ignore case",false);
    gbag2.setConstraints(insens,c);
    p.add(insens);
    c.gridx = z;
    c.anchor = GridBagConstraints.CENTER;

    // Add the panel itself.
    c.gridwidth = GridBagConstraints.REMAINDER;
    gbag.setConstraints(p,c);
    add(p);

    l2 = new Label("Input Text");
    c.gridwidth = 1;
    gbag.setConstraints(l2,c);
    add(l2);
    
    l3 = new Label("Matches Found");
    c.gridwidth = GridBagConstraints.REMAINDER;
    gbag.setConstraints(l3,c);
    add(l3);
    
    input = new TextArea(getParameter("input"),5,30);
    c.gridwidth = 1;
    gbag.setConstraints(input,c);
    add(input);

    output = new TextArea(5,30);
    output.setEditable(false);
    c.gridwidth = GridBagConstraints.REMAINDER;
    gbag.setConstraints(output,c);
    add(output);
  }

  /**
   * Handles events in the applet.  Returns true if the indicated event
   * was handled, false for all other events.
   */
  public boolean action(Event e, Object arg) {
    Object target = e.target;

    if (target == b) { // match
      String expr = tf.getText();
      RE reg = null;
      try {
	reg = new RE(expr,insens.getState() ? RE.REG_ICASE : 0);
	REMatchEnumeration en = reg.getMatchEnumeration(input.getText());
	StringBuffer sb = new StringBuffer();
	int matchNum = 0;
	while (en.hasMoreMatches()) {
	  sb.append(String.valueOf(++matchNum));
	  sb.append(". ");
	  sb.append(en.nextMatch().toString());
	  sb.append('\n');
	}
	output.setText(sb.toString());
      } catch (REException err) { 
	output.setText(err.getMessage());
      }
      return true;
    } else return false;
  }
}
