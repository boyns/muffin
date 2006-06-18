/* $Id$ */

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
package org.doit.muffin.regexp;

import java.util.*;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * @author bernhard.wagner
 *
 */
public class Factory
{

    /**
     * Retrieves a Pattern for the given pattern as String.
     * @param pattern The pattern as String for which to retrieve a Pattern.
     *
     * @return Pattern The Pattern for the given pattern as String.
     */
    public Pattern getPattern(String pattern)
    {
        return getPattern(pattern, false);
    }

    /**
     * Retrieves a Pattern from the factory.
     * FIXME: this is a temporary solution mainly to prove the concept.
     * Later, we will decide by other means which implementation of regex
     * to instantiate. For now you can choose the implementation via the method
     * @see setRegexType.
     *
     * @param pattern The pattern as String for which to retrieve a Pattern.
     * @param ignoreCase Whether to ignore case. True means ignore case.
     * @return Pattern The Pattern for the given pattern as String.
     */
    public Pattern getPattern(String pattern, boolean ignoreCase)
    {
        return fRegexType.getPattern(pattern, ignoreCase);
    }

    /**
     * Allows to set the regex implementation.
     * FIXME: this is a temporary solution mainly to prove the concept.
     * Later, we will decide by other means which implementation of regex
     * to instantiate. So far you can choose between REGEX_GNU and REGEX_JDK14.
     *
     * @param type
     */
    public void setRegexType(String className)
    {
        if (gcFactoryMap.containsKey(className))
        {
            fRegexType = (PatternFactory) gcFactoryMap.get(className);
        } else
        {
            System.out.println(
                "setRegexType: not changing because inexistent: " + className);
        }
    }

    /**
     * Retrieves the Map with PatternFactories.
     * @return Map The available PatternFactories.
     */
    public static Map getFactoryMap()
    {
        return gcFactoryMap;
    }

    /*
     * Returns the one and only Factory. Singleton Design Pattern
     * @return The one and only Factory.
     */
    public static Factory instance()
    {
        return gcInstance;
    }

    /**
     * Default ctor private. Singleton Design Pattern.
     */
    private Factory()
    {
        fRegexType =
            (PatternFactory) gcFactoryMap.get(
                gcFactoryList.iterator().next());
        System.out.println(
            "Factory is using "
                + fRegexType.getClass().getName()
                + " by default");
    }


    // this is the pattern factory which will be used to run regexp pattern matching
    private PatternFactory fRegexType;

    // define desired order of regexp pattern factories
    private static final String[] gcPotentialFactories =
        {
            "org.doit.muffin.regexp.jdk14.PatternFactory",
            "org.doit.muffin.regexp.jakarta.regexp.PatternFactory",
            "org.doit.muffin.regexp.gnu.PatternFactory"
        // ,"org.doit.muffin.regexp.jakarta.oro.PatternFactory"
    };

    private static final void getFactories(String[] classNames, Map implMap)
    {
        for (int i = 0; i < classNames.length; i++)
        {
            if (classNames[i].length() == 0)
            {
                continue;
            }

            try
            {
                Class classDefinition = Class.forName(classNames[i]);
                Method m = classDefinition.getMethod("instance", null);
                PatternFactory pf =
                    (PatternFactory) m.invoke(null, new Object[0]);

                // try getting of a dummy pattern...
                // if 3rd party classes/jars where available at compile time but are not at runtime
                // this will throw a java.lang.NoClassDefFoundError while loading the PatternAdapter impl
                pf.getPattern("", true);

                gcFactoryMap.put(classNames[i], pf);
                gcFactoryList.add(classNames[i]);
            } catch (NoClassDefFoundError e)
            {
                System.out.println(
                    classNames[i] + ": This class is not available " + e);
            } catch (ClassNotFoundException e)
            {
                System.out.println(
                    classNames[i] + ": This class is not available " + e);
            } catch (NoSuchMethodException e)
            {
                System.out.println(
                    classNames[i]
                        + ": This class does not have the requested constructor. "
                        + e);
            } catch (IllegalAccessException e)
            {
                System.out.println(classNames[i] + " " + e);
            } catch (InvocationTargetException e)
            {
                System.out.println(classNames[i] + " " + e);
            }
        }
    }

    // we want to make sure while iterating through pattern factories they are in proper order
    // Note: LinkedHashMap works perfectly but is only available with JDK 1.4 :-(
    private static final Map gcFactoryMap = new HashMap();
    private static final List gcFactoryList = new ArrayList();

    static {
        getFactories(gcPotentialFactories, gcFactoryMap);
    }

    // must come AFTER static initializer since static initialization happens in
    // the order of declaration in the source code. See
    // http://java.sun.com/docs/books/vmspec/2nd-edition/html/Concepts.doc.html#32316
    private static Factory gcInstance = new Factory();
}
