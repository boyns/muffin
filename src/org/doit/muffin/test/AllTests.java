package org.doit.muffin.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author bernhard.wagner
 *
 */
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.doit.muffin.test");
		//$JUnit-BEGIN$
		suite.addTest(new TestSuite(RegexpTest.class));
		//$JUnit-END$
		return suite;
	}
}
