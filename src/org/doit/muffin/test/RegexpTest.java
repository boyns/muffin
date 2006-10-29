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

import java.util.*;
import junit.framework.TestCase;
import org.doit.muffin.regexp.Factory;
import org.doit.muffin.regexp.Pattern;
import org.doit.muffin.regexp.Matcher;

/**
 * @author Bernhard Wagner <muffinsrc@xmlizer.biz>
 * 
 * TestCase testing the Regexes.
 *
 */
public class RegexpTest extends TestCase
{

    /**
     * Constructor for RegexpTest.
     * @param arg0
     */
    public RegexpTest(String arg0)
    {
        super(arg0);
        //		System.out.println("-----");
        //		System.out.println(IMPLS);
        //		System.out.println("-----");
    }

    public void testFactory()
    {
        Iterator it = IMPLS.keySet().iterator();
        while (it.hasNext())
        {
            String patternAdapter = (String) it.next();
            //			System.out.println(patternAdapter);
            Factory.instance().setRegexType(patternAdapter);
            Pattern pattern = Factory.instance().getPattern("(a|b)");
            patternAdapter =
                patternAdapter.substring(0, patternAdapter.lastIndexOf("."));
            String checkName = pattern.getClass().getName();
            checkName = checkName.substring(0, checkName.lastIndexOf("."));
            assertEquals(patternAdapter, checkName);
        }
    }

    public void testRegexpNull()
    {
        Iterator it = IMPLS.keySet().iterator();
        while (it.hasNext())
        {
            testRegexpNull((String) it.next());
        }
    }

    public void testRegexpNotNull()
    {
        Iterator it = IMPLS.keySet().iterator();
        while (it.hasNext())
        {
            testRegexpNotNull((String) it.next());
        }
    }

    public void testRegexpMatch()
    {
        Iterator it = IMPLS.keySet().iterator();
        while (it.hasNext())
        {
            testRegexpMatch((String) it.next());
        }
    }

    public void testRegexpMatchN()
    {
        Iterator it = IMPLS.keySet().iterator();
        while (it.hasNext())
        {
            testRegexpMatchN((String) it.next());
        }
    }

    public void testRegexpMatchCase()
    {
        Iterator it = IMPLS.keySet().iterator();
        while (it.hasNext())
        {
            testRegexpMatchCase((String) it.next());
        }
    }

    public void testSubstituteInto()
    {
        Iterator it = IMPLS.keySet().iterator();
        while (it.hasNext())
        {
            testSubstituteInto((String) it.next());
        }
    }

    public void testSubstituteAllAt0()
    {
        Iterator it = IMPLS.keySet().iterator();
        while (it.hasNext())
        {
            testSubstituteAll((String) it.next());
        }
    }

    public void testSubstituteAllAtX()
    {
        Iterator it = IMPLS.keySet().iterator();
        while (it.hasNext())
        {
            testSubstituteAllX((String) it.next());
        }
    }

    public void testStartEndIndex()
    {
        Iterator it = IMPLS.keySet().iterator();
        while (it.hasNext())
        {
            testStartEndIndex((String) it.next());
        }
    }

    public void testAA()
    {}

    private void testRegexpNotNull(String regexpType)
    {
        Factory.instance().setRegexType(regexpType);
        Pattern pattern = Factory.instance().getPattern("(a|b)");
        Matcher matcher = pattern.getMatch("but");
        assertNotNull(matcher);
    }

    private void testRegexpNull(String regexpType)
    {
        Factory.instance().setRegexType(regexpType);
        Pattern pattern = Factory.instance().getPattern("(a|b)");
        Matcher matcher = pattern.getMatch("sure");
        assertNull(matcher);
    }

    private void testRegexpMatch(String regexpType)
    {
        Factory.instance().setRegexType(regexpType);
        Pattern pattern = Factory.instance().getPattern("(a|b)");
        assertTrue(pattern.matches("but"));
        assertTrue(!pattern.matches("sure"));
        pattern = Factory.instance().getPattern("(.)\\1");
        assertTrue(!pattern.matches("but"));
        assertTrue(pattern.matches("google"));
        assertTrue(pattern.matches("butter"));
    }

    private void testRegexpMatchN(String regexpType)
    {
        Factory.instance().setRegexType(regexpType);
        Pattern pattern = Factory.instance().getPattern("(.)\\1");
        //		 	groogle is the door to iinformation.";
        //		 	01234567890123456789012345678901234
        //          0         1         2         3
        final String INPUT = "groogle is the door to iinformation.";
        Matcher matcher = pattern.getMatch(INPUT, 0);
        assertEquals(
            "regextype:<" + regexpType + ">",
            2,
            matcher.getStartIndex());
        matcher = pattern.getMatch(INPUT, 2);
        assertEquals(
            "regextype:<" + regexpType + ">",
            2,
            matcher.getStartIndex());
        matcher = pattern.getMatch(INPUT, 3);
        assertEquals(
            "regextype:<" + regexpType + ">",
            16,
            matcher.getStartIndex());
        matcher = pattern.getMatch(INPUT, 24);
        assertNull("regextype:<" + regexpType + ">", matcher);
    }

    private void testRegexpMatchCase(String regexpType)
    {
        Factory.instance().setRegexType(regexpType);
        Pattern pattern = Factory.instance().getPattern("(A|B)");
        assertTrue(!pattern.matches("but"));
        assertTrue(!pattern.matches("sure"));
        pattern = Factory.instance().getPattern("(A|B)", true);
        assertTrue(pattern.matches("but"));
        assertTrue(!pattern.matches("sure"));
        pattern = Factory.instance().getPattern("(a|b)", true);
        assertTrue(pattern.matches("BUT"));
        assertTrue(!pattern.matches("sure"));
        pattern = Factory.instance().getPattern("(a|b)");
        assertTrue(!pattern.matches("BUT"));
        assertTrue(!pattern.matches("sure"));
    }

    private void testSubstituteAll(String regexpType)
    {
        Factory.instance().setRegexType(regexpType);
        final String PATTERN = "(.)\\1";
        final String INPUT = "ggroogle is the door to iinformation.";
        final String REPLACEMENT_STR = "_$1_";
        final String EXPECTED = "_g_r_o_gle is the d_o_r to _i_nformation.";
        Pattern pattern = Factory.instance().getPattern(PATTERN);
        String got = pattern.substituteAll(INPUT, REPLACEMENT_STR);
        assertEquals(EXPECTED, got);
    }

    private void testSubstituteAllX(String regexpType)
    {
        Factory.instance().setRegexType(regexpType);
        final String PATTERN = "(.)\\1";
        final String INPUT = "groogle is the door to iinformation.";
        final String REPLACEMENT_STR = "_$1_";
        final String EXPECTED = "gr_o_gle is the d_o_r to _i_nformation.";
        Pattern pattern = Factory.instance().getPattern(PATTERN);
        String got = pattern.substituteAll(INPUT, REPLACEMENT_STR);
        assertEquals(EXPECTED, got);
    }

    private void testSubstituteInto(String regexpType)
    {
        Factory.instance().setRegexType(regexpType);
        final String PATTERN = "(.)\\1";
        final String INPUT = "google is the door to iinformation.";
        final String REPLACEMENT_STR = "_$1_";
        final String EXPECTED = "_o_";
        Pattern pattern = Factory.instance().getPattern(PATTERN);
        Matcher matcher = pattern.getMatch(INPUT);
        String got = matcher.substituteInto(REPLACEMENT_STR);
        assertEquals(EXPECTED, got);
    }

    public void testb4b()
    {
        boolean value = true;
        assertTrue(Boolean.valueOf(value ? "true" : "false").booleanValue());
        value = false;
        assertTrue(!Boolean.valueOf(value ? "true" : "false").booleanValue());
    }

    private void testStartEndIndex(String regexpType)
    {
        Factory.instance().setRegexType(regexpType);
        final String INPUT = "google is the door to iinformation.";
        //                             01234567890123456789012345678901234
        //                             0         1         2         3

        Pattern pattern = Factory.instance().getPattern("(.)\\1(.)");
        Matcher matcher = pattern.getMatch(INPUT);
        assertEquals(1, matcher.getStartIndex());
        assertEquals(4, matcher.getEndIndex());
        assertEquals(
            "regextype:<" + regexpType + ">",
            1,
            matcher.getStartIndex(1));
        assertEquals(
            "regextype:<" + regexpType + ">",
            2,
            matcher.getEndIndex(1));
        assertEquals(
            "regextype:<" + regexpType + ">",
            3,
            matcher.getStartIndex(2));
        assertEquals(
            "regextype:<" + regexpType + ">",
            4,
            matcher.getEndIndex(2));
    }

    private static Map IMPLS = Factory.getFactoryMap();

}
