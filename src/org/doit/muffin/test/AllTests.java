/*
 * Copyright (C) 2003 Bernhard Wagner <muffinsrc@xmlizer.biz>
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
package org.doit.muffin.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author bernhard.wagner
 *
 */
public class AllTests
{

    public static Test suite()
    {
        TestSuite suite = new TestSuite("Test for org.doit.muffin.test");
        //$JUnit-BEGIN$
        suite.addTest(new TestSuite(RegexpTest.class));
        suite.addTest(new TestSuite(GlossaryTest.class));
        suite.addTest(new TestSuite(EmptyFontTest.class));
        suite.addTest(new TestSuite(ImageKillTest.class));
        suite.addTest(new TestSuite(DecafTest.class));
        suite.addTest(new TestSuite(NoCodeTest.class));
        suite.addTest(new TestSuite(NoThanksTest.class));
        suite.addTest(new TestSuite(PainterTest.class));
        
        //$JUnit-END$
        return suite;
    }
}
