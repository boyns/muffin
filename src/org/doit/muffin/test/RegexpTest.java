package org.doit.muffin.test;

import java.util.*;
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
//		System.out.println("-----");
//		System.out.println(IMPLS);
//		System.out.println("-----");
	}
	
	public void testFactory(){
		Iterator it = IMPLS.keySet().iterator();
		while(it.hasNext()){
			String patternAdapter = (String)it.next();
			System.out.println(patternAdapter);
			Factory.instance().setRegexType(patternAdapter);
			Pattern pattern = Factory.instance().getPattern("(a|b)");
			assertEquals(patternAdapter, pattern.getClass().getName());			
		}
	}

	public void testRegexpNull(){
		Iterator it = IMPLS.keySet().iterator();
		while(it.hasNext()){
			testRegexpNull((String)it.next());
		}
	}
	
	public void testRegexpNotNull(){
		Iterator it = IMPLS.keySet().iterator();
		while(it.hasNext()){
			testRegexpNotNull((String)it.next());
		}
	}
	
	public void testRegexpMatch(){
		Iterator it = IMPLS.keySet().iterator();
		while(it.hasNext()){
			testRegexpMatch((String)it.next());
		}
	}
	
	public void testRegexpMatchN(){
		Iterator it = IMPLS.keySet().iterator();
		while(it.hasNext()){
			testRegexpMatchN((String)it.next());
		}
	}
	
	public void testRegexpMatchCase(){
		Iterator it = IMPLS.keySet().iterator();
		while(it.hasNext()){
			testRegexpMatchCase((String)it.next());
		}
	}
	
	public void testSubstituteInto(){
		Iterator it = IMPLS.keySet().iterator();
		while(it.hasNext()){
			testSubstituteInto((String)it.next());
		}
	}
	
	public void testStartEndIndex(){
		Iterator it = IMPLS.keySet().iterator();
		while(it.hasNext()){
			testStartEndIndex((String)it.next());
		}
	}
	
	public void testAA(){
	}
	
	private void testRegexpNotNull(String regexpType){
		Factory.instance().setRegexType(regexpType);
		Pattern pattern = Factory.instance().getPattern("(a|b)");
		Matcher matcher = pattern.getMatch("but");
		assertNotNull(matcher);
	}
	
	private void testRegexpNull(String regexpType){
		Factory.instance().setRegexType(regexpType);
		Pattern pattern = Factory.instance().getPattern("(a|b)");
		Matcher matcher = pattern.getMatch("shure");
		assertNull(matcher);
	}
	
	private void testRegexpMatch(String regexpType){
		Factory.instance().setRegexType(regexpType);
		Pattern pattern = Factory.instance().getPattern("(a|b)");
		assertTrue(pattern.matches("but"));
		assertTrue(!pattern.matches("shure"));
	}

	private void testRegexpMatchN(String regexpType){
		Factory.instance().setRegexType(regexpType);
		Pattern pattern = Factory.instance().getPattern("(a|b)");
		Matcher matcher = pattern.getMatch("bubbles",1);
		assertEquals("regextype:<"+regexpType+">",2, matcher.getStartIndex());
		matcher = pattern.getMatch("bubbles",2);
		assertEquals("regextype:<"+regexpType+">",2, matcher.getStartIndex());
		matcher = pattern.getMatch("bubbles",3);
		assertEquals("regextype:<"+regexpType+">",3, matcher.getStartIndex());
		matcher = pattern.getMatch("bubbles",4);
		assertNull("regextype:<"+regexpType+">", matcher);
	}
	
	private void testRegexpMatchCase(String regexpType){
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

	private void testSubstituteAll(String regexpType){
		Factory.instance().setRegexType(regexpType);
		final String INPUT = "google is the door to iinformation.";
		final String REPLACEMENT_STR = "_$1_";
		final String EXPECTED = "g_o_gle is the d_o_r to _i_nformation.";
		Pattern pattern = Factory.instance().getPattern("(.)\\1");
		String got = pattern.substituteAll(INPUT, REPLACEMENT_STR);
		assertEquals(EXPECTED, got);
	}

	private void testSubstituteInto(String regexpType){
		Factory.instance().setRegexType(regexpType);
		final String INPUT = "google is the door to iinformation.";
		final String REPLACEMENT_STR = "_$1_";
		final String EXPECTED = "_o_";
		Pattern pattern = Factory.instance().getPattern("(.)\\1");
		Matcher matcher = pattern.getMatch(INPUT);
		String got = matcher.substituteInto(REPLACEMENT_STR);
		assertEquals(EXPECTED, got);
	}
	
	public void testb4b(){
		boolean value = true;
		assertTrue(Boolean.valueOf(value ? "true" : "false").booleanValue());
		value = false;
		assertTrue(!Boolean.valueOf(value ? "true" : "false").booleanValue());
	}


	/**
	 * 
	 * 
	 * This method uses the implicit knowledge that gnu regexp engine has number 0 and
	 * jdk14 regexp engin has number 1 in Factory.
	 * 	 * @param regexpType	 */
	private void testStartEndIndex(String regexpType){
		Factory.instance().setRegexType(regexpType);
        final String INPUT = "google is the door to iinformation.";
//                             01234567890123456789012345678901234
//                             0         1         2         3

		Pattern pattern = Factory.instance().getPattern("(.)\\1(.)");
		Matcher matcher = pattern.getMatch(INPUT);
		assertEquals(1, matcher.getStartIndex());
		assertEquals(4, matcher.getEndIndex());
		assertEquals("regextype:<"+regexpType+">",1, matcher.getStartIndex(1));
		assertEquals("regextype:<"+regexpType+">",2, matcher.getEndIndex(1));
		assertEquals("regextype:<"+regexpType+">",3, matcher.getStartIndex(2));
		assertEquals("regextype:<"+regexpType+">",4, matcher.getEndIndex(2));
	}

	private static Map IMPLS = Factory.instance().getImplementors();
	private static int NOF_IMPLS = IMPLS.size();

}
