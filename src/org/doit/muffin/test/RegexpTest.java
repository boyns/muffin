package org.doit.muffin.test;

import junit.framework.TestCase;
import org.doit.muffin.regexp.Factory;
import org.doit.muffin.regexp.Pattern;
import org.doit.muffin.regexp.Matcher;

/**
 * @author bernhard.wagner
 *
 */
public class RegexpTest extends TestCase {

	/**
	 * Constructor for RegexpTest.
	 * @param arg0
	 */
	public RegexpTest(String arg0) {
		super(arg0);
	}
	
	public void testFactory(){
		for(int i=0;i<REGEXP_ENGINES.length;i++){
			Factory.instance().setRegexType(REGEXP_ENGINES[i]);
			Pattern pattern = Factory.instance().getPattern("(a|b)");
			assertEquals(REGEXP_NAMES[i], pattern.getClass().getName());			
		}
	}

	public void testRegexpNull(){
		for(int i=0;i<REGEXP_ENGINES.length;i++){
			testRegexpNull(REGEXP_ENGINES[i]);
		}
	}
	
	public void testRegexpNotNull(){
		for(int i=0;i<REGEXP_ENGINES.length;i++){
			testRegexpNotNull(REGEXP_ENGINES[i]);
		}
	}
	
	public void testRegexpMatch(){
		for(int i=0;i<REGEXP_ENGINES.length;i++){
			testRegexpMatch(REGEXP_ENGINES[i]);
		}
	}
	
	public void testRegexpMatchN(){
		for(int i=0;i<REGEXP_ENGINES.length;i++){
			testRegexpMatchN(REGEXP_ENGINES[i]);
		}
	}
	
	public void testRegexpMatchCase(){
		for(int i=0;i<REGEXP_ENGINES.length;i++){
			testRegexpMatchCase(REGEXP_ENGINES[i]);
		}
	}
	
	public void testSubstituteInto(){
		for(int i=0;i<REGEXP_ENGINES.length;i++){
			testSubstituteInto(REGEXP_ENGINES[i]);
		}
	}
	
	public void testStartEndIndex(){
		for(int i=0;i<REGEXP_ENGINES.length;i++){
			testStartEndIndex(REGEXP_ENGINES[i]);
		}
	}
	
	private void testRegexpNotNull(int regexpType){
		Factory.instance().setRegexType(regexpType);
		Pattern pattern = Factory.instance().getPattern("(a|b)");
		Matcher matcher = pattern.getMatch("but");
		assertNotNull(matcher);
	}
	
	private void testRegexpNull(int regexpType){
		Factory.instance().setRegexType(regexpType);
		Pattern pattern = Factory.instance().getPattern("(a|b)");
		Matcher matcher = pattern.getMatch("shure");
		assertNull(matcher);
	}
	
	private void testRegexpMatch(int regexpType){
		Factory.instance().setRegexType(regexpType);
		Pattern pattern = Factory.instance().getPattern("(a|b)");
		assertTrue(pattern.matches("but"));
		assertTrue(!pattern.matches("shure"));
	}

	private void testRegexpMatchN(int regexpType){
		Factory.instance().setRegexType(regexpType);
		Pattern pattern = Factory.instance().getPattern("(a|b)");
		Matcher matcher = pattern.getMatch("bubbles",1);
		assertEquals("regextype:<"+REGEXP_NAMES[regexpType]+">",2, matcher.getStartIndex());
		matcher = pattern.getMatch("bubbles",2);
		assertEquals("regextype:<"+REGEXP_NAMES[regexpType]+">",2, matcher.getStartIndex());
		matcher = pattern.getMatch("bubbles",3);
		assertEquals("regextype:<"+REGEXP_NAMES[regexpType]+">",3, matcher.getStartIndex());
		matcher = pattern.getMatch("bubbles",4);
		assertNull("regextype:<"+REGEXP_NAMES[regexpType]+">", matcher);
	}
	
	private void testRegexpMatchCase(int regexpType){
		Factory.instance().setRegexType(regexpType);
		Pattern pattern = Factory.instance().getPattern("(A|B)");
		assertTrue(!pattern.matches("but"));
		assertTrue(!pattern.matches("shure"));
		pattern = Factory.instance().getPattern("(A|B)", true);
		assertTrue(pattern.matches("but"));
		assertTrue(!pattern.matches("shure"));
		pattern = Factory.instance().getPattern("(a|b)", true);
		assertTrue(pattern.matches("BUT"));
		assertTrue(!pattern.matches("SHURE"));
		pattern = Factory.instance().getPattern("(a|b)");
		assertTrue(!pattern.matches("BUT"));
		assertTrue(!pattern.matches("SHURE"));
	}

	private void testSubstituteAll(int regexpType){
		Factory.instance().setRegexType(regexpType);
		final String INPUT = "google is the door to iinformation.";
		final String REPLACEMENT_STR = "_$1_";
		final String EXPECTED = "g_o_gle is the d_o_r to _i_nformation.";
		Pattern pattern = Factory.instance().getPattern("(.)\\1");
		String got = pattern.substituteAll(INPUT, REPLACEMENT_STR);
		assertEquals(EXPECTED, got);
	}

	private void testSubstituteInto(int regexpType){
		Factory.instance().setRegexType(regexpType);
		final String INPUT = "google is the door to iinformation.";
		final String REPLACEMENT_STR = "_$1_";
		final String EXPECTED = "_o_";
		Pattern pattern = Factory.instance().getPattern("(.)\\1");
		Matcher matcher = pattern.getMatch(INPUT);
		String got = matcher.substituteInto(REPLACEMENT_STR);
		assertEquals(EXPECTED, got);
	}

	/**
	 * 
	 * 
	 * This method uses the implicit knowledge that gnu regexp engine has number 0 and
	 * jdk14 regexp engin has number 1 in Factory.
	 * 	 * @param regexpType	 */
	private void testStartEndIndex(int regexpType){
		Factory.instance().setRegexType(regexpType);
        final String INPUT = "google is the door to iinformation.";
//                             01234567890123456789012345678901234
//                             0         1         2         3

		Pattern pattern = Factory.instance().getPattern("(.)\\1(.)");
		Matcher matcher = pattern.getMatch(INPUT);
		assertEquals(1, matcher.getStartIndex());
		assertEquals(4, matcher.getEndIndex());
		assertEquals("regextype:<"+REGEXP_NAMES[regexpType]+">",1, matcher.getStartIndex(1));
		assertEquals("regextype:<"+REGEXP_NAMES[regexpType]+">",2, matcher.getEndIndex(1));
		assertEquals("regextype:<"+REGEXP_NAMES[regexpType]+">",3, matcher.getStartIndex(2));
		assertEquals("regextype:<"+REGEXP_NAMES[regexpType]+">",4, matcher.getEndIndex(2));
	}

	private static final int[] REGEXP_ENGINES = {
		Factory.REGEX_GNU
		,Factory.REGEX_JDK14
		,Factory.REGEX_JAKARTA_REGEX
	};

	private static final String[] REGEXP_NAMES = {
		"org.doit.muffin.regexp.gnu.PatternAdapter"
		,"org.doit.muffin.regexp.jdk14.PatternAdapter"
		,"org.doit.muffin.regexp.jakarta.regexp.PatternAdapter"
	};

}
