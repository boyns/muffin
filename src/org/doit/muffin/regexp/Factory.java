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
package org.doit.muffin.regexp;

import java.util.*;
import java.lang.Class;
//import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * @author bernhard.wagner
 *
 */
public class Factory {

    /**
     * Retrieves a Pattern for the given pattern as String.
     * @param pattern The pattern as String for which to retrieve a Pattern.
     *
     * @return Pattern The Pattern for the given pattern as String.
     */
    public Pattern getPattern(String pattern) {
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
    public Pattern getPattern(String pattern, boolean ignoreCase) {
        return fRegexType.getPattern(pattern, ignoreCase);
    }

//    /**
//     * Retrieves a Pattern from the factory.
//     * FIXME: this is a temporary solution mainly to prove the concept.
//     * Later, we will decide by other means which implementation of regex
//     * to instantiate. For now you can choose the implementation via the method
//     * @see setRegexType.
//     *
//     * @param pattern The pattern as String for which to retrieve a Pattern.
//     * @param ignoreCase Whether to ignore case. True means ignore case.
//     * @return Pattern The Pattern for the given pattern as String.
//     */
//    public Pattern getPatternViaReflection(String pattern, boolean ignoreCase) {
//            try {
//                    return (Pattern)createObject(
//                            fRegexType,
//                            new Object[] {pattern, b4b(ignoreCase)}
//                    );
//            } catch (FactoryException e){
//                    System.out.println("Exception occurred when invoking getPattern with <" + pattern + "> " + ignoreCase);
//                    return null;
//            }
//    }
//
//    /**
//     * This method is needed in
//     * <a href="http://java.sun.com/j2se/1.3/docs/api/java/lang/Boolean.html#method_summary">jdk1.3</a>
//     * only whose Boolean API does not provide a constructor
//     * for Boolean accepting a boolean.
//     * <a href="http://java.sun.com/j2se/1.4.1/docs/api/java/lang/Boolean.html#method_summary">jdk1.4</a> does.
//     * @param value boolean for which to construct a Boolean.
//     * @return Boolean The Boolean object representing the given boolean value.
//     */
//    private static final Boolean b4b(boolean value){
//            return Boolean.valueOf(value ? "true" : "false");
//    }

    /**
     * Allows to set the regex implementation.
     * FIXME: this is a temporary solution mainly to prove the concept.
     * Later, we will decide by other means which implementation of regex
     * to instantiate. So far you can choose between REGEX_GNU and REGEX_JDK14.
     *
     * @param type
     */
    public void setRegexType(String className){
//            if(gcImplMap.containsKey(className)){
//                    fRegexType = (Constructor)gcImplMap.get(className);
        if(gcFactoryMap.containsKey(className)){
            fRegexType = (PatternFactory)gcFactoryMap.get(className);
        } else {
            System.out.println("setRegexType: not changing because inexistant:"+className);
        }
    }

//    /**
//     * Adds the given class name as String to the list of implementors.
//     * This should actually only be package visible since it is not intended for clients'
//     * usage. But to do that I would need to move the Matchers and Adapters from the
//     * implementors in subpackages into this package.
//     * Would be nice if java would allow stating packages not only for import but also
//     * for access with package visibility
//     * Or if subpackages were allowed package access into ancestor packages.
//     *
//     * @param className
//     */
//    private void addImplementation(Class className){
//            fImplementations.add(className);
//    }
//
//    public static Map getImplementors(){
//            return gcImplMap;
//    }

    /**
     * Retrieves the Map with PatternFactories.
     * @return Map The available PatternFactories.
     */
    public static Map getFactoryMap(){
        return gcFactoryMap;
    }

    /*
     * Returns the one and only Factory. Singleton Design Pattern
     * @return The one and only Factory.
     */
    public static Factory instance(){
        return gcInstance;
    }

    /**
     * Default ctor private. Singleton Design Pattern.
     */
    private Factory(){
//            fRegexType = (Constructor)gcImplMap.values().iterator().next();
        fRegexType = (PatternFactory)gcFactoryMap.values().iterator().next();
    }

//    /**
//     * This utility function creates an Object using the given parameters applying
//     * java reflection.
//     *
//     * @param constructor The Constructor to use for creation of an Object.
//     * @param arguments The arguments to use for creation of the Object.
//     * @return Object The newly created Object.
//     * @throws FactoryException The common wrapper for potential Exceptions.
//     */
//    private static Object createObject(
//            Constructor constructor,
//            Object[] arguments)  throws FactoryException  {
//
//            Object object = null;
//
//            try {
//                    object = constructor.newInstance(arguments);
//                    return object;
//            } catch (InstantiationException e) {
//                    throw new FactoryException(e);
//            } catch (IllegalAccessException e) {
//                    throw new FactoryException(e);
//            } catch (IllegalArgumentException e) {
//                    throw new FactoryException(e);
//            } catch (InvocationTargetException e) {
//                    throw new FactoryException(e);
//            }
//    }
//
//    private Constructor fRegexType;
    private PatternFactory fRegexType;

    // These are held as strings to remove static dependency of these implementations.
    // I'd love to learn about a more elegant way, e.g. some mechanism in java lang
    // reflection that allows me to find all classes implementing a specific interface.
//    private static final String[] gcPotentialImplementations = { ""
//            ,"org.doit.muffin.regexp.gnu.PatternAdapter"
//            ,"org.doit.muffin.regexp.jdk14.PatternAdapter"
//            ,"org.doit.muffin.regexp.jakarta.regexp.PatternAdapter"
////          ,"org.doit.muffin.regexp.jakarta.oro.PatternAdapter"
//    };

    private static final String[] gcPotentialFactories = { ""
                                                           ,"org.doit.muffin.regexp.gnu.PatternFactory"
                                                           ,"org.doit.muffin.regexp.jdk14.PatternFactory"
                                                           ,"org.doit.muffin.regexp.jakarta.regexp.PatternFactory"
//            ,"org.doit.muffin.regexp.jakarta.oro.PatternFactory"
    };

    private static final void getFactories(String[] classNames, Map implMap){
        for (int i = 0; i < classNames.length; i++) {
            if(classNames[i].length()==0) { continue; }
            try {
                Class classDefinition =
                    Class.forName(classNames[i]);
                Method m = classDefinition.getMethod("instance", null);
                gcFactoryMap.put(classNames[i], m.invoke(null, new Object[0]));
            } catch (NoClassDefFoundError e) {
                System.out.println(classNames[i]+":This class is not available"+e);
            } catch (ClassNotFoundException e) {
                System.out.println(classNames[i]+":This class is not available"+e);
            } catch (NoSuchMethodException e) {
                System.out.println(classNames[i]+":This class does not have the requested constructor."+e);
            } catch (IllegalAccessException e) {
                System.out.println(classNames[i]+" "+e);
            } catch (InvocationTargetException e) {
                System.out.println(classNames[i]+" "+e);
            }
        }
    }

//    private static final void getConstructors(String[] classNames, Map implMap){
//            final Class[] argsClass = new Class[] {String.class, boolean.class};
//
//            for (int i = 0; i < classNames.length; i++) {
//                    if(classNames[i].length()==0) { continue; }
//                    try {
//                            Class classDefinition =
//                                    Class.forName(classNames[i]);
//                            Constructor constr = classDefinition.getConstructor(argsClass);
//                            createObject(constr, new Object[] {"dummy", Boolean.valueOf("false")});
//                            gcImplMap.put(classNames[i], constr);
//                    } catch (NoClassDefFoundError e) {
//                            System.out.println(classNames[i]+":This class is not available"+e);
//                    } catch (ClassNotFoundException e) {
//                            System.out.println(classNames[i]+":This class is not available"+e);
//                    } catch (NoSuchMethodException e) {
//                            System.out.println(classNames[i]+":This class does not have the requested constructor."+e);
//                    } catch (FactoryException e) {
//                            System.out.println(classNames[i]+" "+e);
//                    }
//            }
//    }

//    private static final Map gcImplMap = new HashMap();
    private static final Map gcFactoryMap = new HashMap();


    static {
        getFactories(gcPotentialFactories, gcFactoryMap);
//                    getConstructors(gcPotentialImplementations, gcImplMap);
    }

    // must come AFTER static initializer since static initialization happens in
    // the order of declaration in the source code. See
    // http://java.sun.com/docs/books/vmspec/2nd-edition/html/Concepts.doc.html#32316
    private static Factory gcInstance = new Factory();

}
