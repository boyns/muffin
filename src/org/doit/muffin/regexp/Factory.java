package org.doit.muffin.regexp;

import java.util.*;
import java.lang.Class;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author bernhard.wagner
 *
 */
public class Factory {
	
	public Pattern getPattern(String pattern) {
		return getPattern(pattern, false);
	}
	
	/**
	 * Retrieves a Pattern from the factory.
	 * FIXME: this is a temporary solution mainly to prove the concept.
	 * Later, we will decide by other means which implementation of regex
	 * to instantiate. For now you can choose the implementation via the method
	 * @see setRegexType.
	 * 	 * @param pattern	 * @param ignoreCase	 * @return Pattern	 */
	public Pattern getPattern(String pattern, boolean ignoreCase) {
		try {
			return (Pattern)createObject(
				fRegexType,
				new Object[] {pattern, b4b(ignoreCase)}
			);
		} catch (FactoryException e){
			System.out.println("Exception occurred when invoking getPattern with <" + pattern + "> " + ignoreCase);
			return null;
		}
	}
	
	/**
	 * This method is needed in 
	 * <a href="http://java.sun.com/j2se/1.3/docs/api/java/lang/Boolean.html#method_summary">jdk1.3</a>
	 * only whose Boolean API does not provide a constructor
	 * for Boolean accepting a boolean.
	 * <a href="http://java.sun.com/j2se/1.4.1/docs/api/java/lang/Boolean.html#method_summary">jdk1.4</a> does.	 * @param value boolean for which to construct a Boolean.	 * @return Boolean	 */
	private static final Boolean b4b(boolean value){
		return Boolean.valueOf(value ? "true" : "false");
	}
	
	/**
	 * Allows to set the regex implementation.	 * FIXME: this is a temporary solution mainly to prove the concept.
	 * Later, we will decide by other means which implementation of regex
	 * to instantiate. So far you can choose between REGEX_GNU and REGEX_JDK14.
	 *
	 * @param type	 */
	public void setRegexType(String className){
		if(fImplMap.containsKey(className)){
			fRegexType = (Constructor)fImplMap.get(className);
		} else {
			System.out.println("setRegexType: not changing because inexistant:"+className);
		}		
	}
	
//	/**
//	 * Adds the given class name as String to the list of implementors.
//	 * This should actually only be package visible since it is not intended for clients'
//	 * usage. But to do that I would need to move the Matchers and Adapters from the 
//	 * implementors in subpackages into this package.
//	 * Would be nice if java would allow stating packages not only for import but also
//	 * for access with package visibility
//	 * Or if subpackages were allowed package access into ancestor packages.
//	 * //	 * @param className//	 */
//	private void addImplementation(Class className){
//		fImplementations.add(className);
//	}
	
	public static Map getImplementors(){
		return fImplMap;
	}
	
	/*
	 * Returns the one and only Factory. Singleton Design Pattern
	 * @return The one and only Factory.	 */
	public static Factory instance(){
		return gcInstance;
	}
	
	/**
	 * Default ctor private. Singleton Design Pattern.	 */
	private Factory(){
		fRegexType = (Constructor)fImplMap.values().iterator().next();
	}

	private static Object createObject(
		Constructor constructor,
		Object[] arguments)  throws FactoryException  {

//		System.out.println("Constructor: " + constructor.toString());
		Object object = null;

		try {
			object = constructor.newInstance(arguments);
//			System.out.println("Object: " + object.toString());
			return object;
		} catch (InstantiationException e) {
//			System.out.println(e);
			throw new FactoryException(e);
		} catch (IllegalAccessException e) {
//			System.out.println(e);
			throw new FactoryException(e);
		} catch (IllegalArgumentException e) {
//			System.out.println(e);
			throw new FactoryException(e);
		} catch (InvocationTargetException e) {
//			System.out.println(e);
			throw new FactoryException(e);
		}
	}
	
	private Constructor fRegexType;
	
	// These are held as strings to remove static dependency of these implementations.
	// I'd love to learn about a more elegant way, e.g. some mechanism in java lang
	// reflection that allows me to find all classes implementing a specific interface.
	private static final String[] fPotentialImplementations = { ""
		,"org.doit.muffin.regexp.gnu.PatternAdapter"
		,"org.doit.muffin.regexp.jdk14.PatternAdapter"
		,"org.doit.muffin.regexp.jakarta.regexp.PatternAdapter"
//		,"org.doit.muffin.regexp.jakarta.oro.PatternAdapter"
	};

	private static final Map fImplMap = new HashMap();
	
	
	static {
		Class[] argsClass = new Class[] {String.class, boolean.class};
		
		for (int i = 0; i < fPotentialImplementations.length; i++) {
			if(fPotentialImplementations[i].length()==0) { continue; }
			try {
				Class classDefinition =
					Class.forName(fPotentialImplementations[i]);
				Constructor constr = classDefinition.getConstructor(argsClass);
				createObject(constr, new Object[] {"dummy", Boolean.valueOf("false")});
				fImplMap.put(fPotentialImplementations[i], constr);

			} catch (NoClassDefFoundError e) {
				System.out.println(fPotentialImplementations[i]+":This class is not available"+e);
			} catch (ClassNotFoundException e) {
				System.out.println(fPotentialImplementations[i]+":This class is not available"+e);
			} catch (NoSuchMethodException e) {
				System.out.println(fPotentialImplementations[i]+":This class does not have the requested constructor."+e);
			} catch (FactoryException e) {
//				System.out.println(fPotentialImplementations[i]+" "+e);
			}
		}
//		System.out.println("Available implementations:");
//		Iterator it = fImplMap.keySet().iterator();
//		while(it.hasNext()){
//			System.out.println(fImplMap.get(it.next()));
//		}
//		System.out.println("-------------");
	}
	
	// must come AFTER static initializer since static initialization happens in 
	// the order of declaration in the source code. See
	// http://java.sun.com/docs/books/vmspec/2nd-edition/html/Concepts.doc.html#32316
	private static Factory gcInstance = new Factory();

}
