package org.doit.muffin.regexp;

//import org.doit.muffin.regexp.gnu.PatternAdapter;

/**
 * @author bernhard.wagner
 *
 */
public class Factory {
	
	public static final int REGEX_GNU = 0;
	public static final int REGEX_JDK14 = 1;
	public static final int REGEX_JAKARTA_REGEX = 2;
	public static final int REGEX_JAKARTA_ORO = 3;
	public static final int REGEX_MAX = REGEX_JAKARTA_ORO;
	
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
		switch(fRegexType){
			case REGEX_GNU:
				return new org.doit.muffin.regexp.gnu.PatternAdapter(pattern, ignoreCase);
//				break;
			case REGEX_JDK14:
				return new org.doit.muffin.regexp.jdk14.PatternAdapter(pattern, ignoreCase);
//				break;
			case REGEX_JAKARTA_REGEX:
				return new org.doit.muffin.regexp.jakarta.regexp.PatternAdapter(pattern, ignoreCase);
//				break;
			default:
				throw new RuntimeException("org.doit.muffin.regexp.Factory:"
					+"requested invalid RegexType <"+fRegexType+">"
				);
		}
	}
	
	/**
	 * Allows to set the regex implementation.	 * FIXME: this is a temporary solution mainly to prove the concept.
	 * Later, we will decide by other means which implementation of regex
	 * to instantiate. So far you can choose between REGEX_GNU and REGEX_JDK14.
	 *
	 * @param type	 */
	public void setRegexType(int type){
		fRegexType = type;
		if(type<0 || type > REGEX_MAX){
			throw new RuntimeException("org.doit.muffin.regexp.Factory:"
				+"requested invalid RegexType <"+type+">"
			);
		}

	}
	
	/*
	 * Returns the one and only Factory. Singleton Design Pattern
	 * @return The one and only Factory.	 */
	public static Factory instance(){
		return gcInstance;
	}
	
	/**
	 * Default ctor private. Singleton Design Pattern.	 */
	private Factory(){}
	private static Factory gcInstance = new Factory();
	
	private int fRegexType = REGEX_GNU;
//	private int fRegexType = REGEX_JDK14;
//	private int fRegexType = REGEX_JAKARTA_REGEX;
//	private int fRegexType = REGEX_JAKARTA_ORO;
}
