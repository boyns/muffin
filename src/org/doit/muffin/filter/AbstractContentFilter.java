package org.doit.muffin.filter;

import java.io.IOException;

import org.doit.io.*;
import org.doit.muffin.ContentFilter;
import org.doit.muffin.Prefs;
import org.doit.muffin.Reply;
import org.doit.muffin.Request;

public class AbstractContentFilter implements ContentFilter {
	
	public AbstractContentFilter(AbstractFilterFactory factory){
		fFactory = factory;
	}

	/**
	 * Determines whether this Reply needs filtering.
	 * Note to implementors: This method is not made final in case you want to determine the
	 * need for filtering in your own way.
	 * If the need for filtering is determined via the content identifier, override
	 * @see org.doit.muffin.AbstractContentFilter#doGetContentFilter instead.
	 * @see org.doit.muffin.ContentFilter#needsFiltration(org.doit.muffin.Request, org.doit.muffin.Reply)
	 * 
	 * Ugly little side effect: We store the Request.
	 */
	public boolean needsFiltration(Request request, Reply reply) {
		this.fRequest = request;
		String s = reply.getContentType();
		return s != null && s.startsWith(doGetContentIdentifier());
	}
	
	/**
	 * Accessor for our Request.	 * @return Request Our Request.	 */
	protected final Request getRequest(){
		return fRequest;
	}
	
	/**
	 * Hook method for subclasses of AbstractContentFilter that provides the contentType
	 * identifier (e.g. "text/html", "image/gif", etc.).	 * @return String The content type identifier.	 */
	protected String doGetContentIdentifier(){
		return "";	
	}

	/**
	 * @see org.doit.muffin.ContentFilter#setInputObjectStream(org.doit.io.InputObjectStream)
	 */
	public final void setInputObjectStream(InputObjectStream in) {
		fInObjectStream = in;
	}

	/**
	 * @see org.doit.muffin.ContentFilter#setOutputObjectStream(org.doit.io.OutputObjectStream)
	 */
	public final void setOutputObjectStream(OutputObjectStream out) {
		fOutObjectStream = out;
	}

	public final InputObjectStream getInputObjectStream() {
		return fInObjectStream;
	}

	public final OutputObjectStream getOutputObjectStream() {
		return fOutObjectStream;
	}

	/**
	 * @see org.doit.muffin.Filter#setPrefs(org.doit.muffin.Prefs)
	 */
	public final void setPrefs(Prefs prefs) {
		throw new RuntimeException("You called setPrefs on a Filter instead of on the FilterFactory!");
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		Thread.currentThread().setName(getFactory().getName());
		ObjectStreamToInputStream ostis = 
			new ObjectStreamToInputStream(fInObjectStream);
		ObjectStreamToOutputStream ostos =
			new ObjectStreamToOutputStream(fOutObjectStream);
		try {
			doRun(ostis, ostos);
			ostos.flush();
			ostos.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				fOutObjectStream.flush();
				fOutObjectStream.close();
			} catch (IOException ioe) {
				//FIXME: swallowing it on purpose?
			}
		}
	}
	
	protected void doRun(ObjectStreamToInputStream ostis, ObjectStreamToOutputStream ostos)
		throws IOException {
	}
	
	public final AbstractFilterFactory getFactory(){
		return fFactory;
	}
	
	
	// FIXME:replace by FilterFactory
	private AbstractFilterFactory fFactory;
	private InputObjectStream fInObjectStream = null;
	private OutputObjectStream fOutObjectStream = null;
	private Request fRequest;

}
