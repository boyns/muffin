package org.doit.muffin.regexp;

/**
 * @author bw@xmlizer.biz
 *
 */
public class FactoryException extends Exception {
	
	FactoryException(Exception exception){
		fException = exception;
	}
	
	public Exception getOriginator(){
		return fException;
	}

	private Exception fException;
}
