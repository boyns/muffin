package org.doit.muffin.test;

import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.reflect.*;
import junit.framework.*;

import org.doit.io.*;
import org.doit.muffin.*;
import org.doit.muffin.filter.*;

/**
 * test ContentFilter processing when the client (browser) stream is
 * unexpectingly closed. This is what happens when a user clicks on
 * the "Stop" button of his browser.
 * IMPORTANT: stop your muffin before launching this test.
 *
 * @author Fabien Le Floc'h
 *
 */

/* TODO:
 *   - clean up the code
 *   - less exception stacktraces
 *   - test for 1 to 3 enabled filters (currently it is a fixed 2 filters).
 *   - IOException instead of RuntimeException
 */

public class ContentFilterTest extends TestCase
{
    private Filter filterList[];
    private Reply reply;
    private String sampleResponse;
    
    public ContentFilterTest(String arg0)
    {
        super(arg0);
    }

    public void setUp()
    {
        filterList = new Filter[2];

        ImageKill imageKill = new ImageKill();
        ContentFilter imageKillFilter = (ContentFilter)
            imageKill.createFilter();
        Prefs prefs = new Prefs();
        // parameters:
        prefs.putInteger("ImageKill.minheight", 49);
        prefs.putInteger("ImageKill.minwidth", 300);
        prefs.putInteger("ImageKill.ratio", 5);
        prefs.putBoolean("ImageKill.keepmaps", true);
        prefs.putString("ImageKill.exclude", "(button|map)");
        prefs.putString("ImageKill.rmSizes", "468x60,450x40");
        prefs.putString("ImageKill.replaceURL",
                        "file:/usr/local/images/empty.gif");
        prefs.putBoolean("ImageKill.replace", false);
        imageKill.setPrefs(prefs);

        NoThanks noThanks = new NoThanks();
        ContentFilter noThanksFilter = (ContentFilter) noThanks.createFilter();
        prefs = new Prefs();
        noThanks.setPrefs(prefs);
        filterList[0] = imageKillFilter;
        filterList[1] = noThanksFilter;
        sampleResponse = createResponse().toString();
        reply = new Reply(new ByteArrayInputStream(sampleResponse.getBytes()));
        try
        {
            reply.read();
            ((NoThanksFilter)noThanksFilter).filter(reply);
        }
        catch (Exception e)
        {
            fail("could not setup test properly");
            e.printStackTrace();
        }
    }

    private StringBuffer createResponse()
    {
        // create a long response
        StringBuffer buf = new StringBuffer(
            "HTTP/1.0 302 Found\n"
            + "Content-Type: text/html\n"
            + "Location: http://xmlizer.biz:8080/index.html\n"
            //+ "Content-Length: 50300\n"
            + "Servlet-Engine: Tomcat Web Server/3.2 beta 3 (JSP 1.1; Servlet 2.2; Java 1.2.2; Linux 2.2.24-7.0.3smp i386; java.vendor=Blackdown Java-Linux Team)\n\n");
        buf.append(
            ""
            + "<head><title>Test Page</title></head>\n"
            + "<body><h1><font>\t</font>Test Page</h1>\n"
            + "muffin<font></font> is<font> </font> a <font>funky</font> java project.\n");
        for (int i=0; i<5000;i++)
        {
            buf.append(
                "<img width=\"301\" height=\"50\" src=\"blabla.gif\" >\n");
        }
        buf.append( "</body>\n"
                    + "");
        return buf;
    }

	private static class BreakableOutputStream extends ByteArrayOutputStream
	{
		private boolean breakNow = false;		

		public void breakNow()
		{
			breakNow = true;	
		}		

        public void write(byte[] b, int off, int len)
        {
        	if (breakNow) throw new RuntimeException("break now");
            super.write(b, off, len);
        }

        public void write(int b)
        {
			if (breakNow) throw new RuntimeException("break now");
            super.write(b);
        }

	}
	private static class BreakableInputStream extends ByteArrayInputStream
	{
		
		private boolean breakNow = false;		
        public BreakableInputStream(byte[] buf)
        {
            super(buf);
        }

        public BreakableInputStream(byte[] buf, int offset, int length)
        {
            super(buf, offset, length);
        }
        
		public void breakNow()
		{
			breakNow = true;	
		}

		public int read()
		{
			if (breakNow) throw new RuntimeException("break now");
			return super.read();
		}

        public int read(byte[] b, int off, int len)
        {
        	if (breakNow) throw new RuntimeException("break now");
            return super.read(b, off, len);
        }

	}

    /**
     *invoke Handler.filter method with the right parameters
     * and break the client stream after 3s of transfer.
     */
    public void testBreakStream()
    {
        try
        {
            Main.processArgs(new String[] {"-nw"});
            Main main = new Main();
            Constructor handlerConstructor =
                Handler.class.getDeclaredConstructor(
                    new Class[] { Monitor.class,
                                  FilterManager.class,
                                  Options.class,
                                  Socket.class });
            handlerConstructor.setAccessible(true);
            Object handler = handlerConstructor.newInstance(
                new Object[] {
                    null, null, null, new Socket("localhost",51966) });
            Field replyField = Handler.class.getDeclaredField("reply");
            replyField.setAccessible(true);
            replyField.set(handler, reply);
            Field filterListField =
                Handler.class.getDeclaredField("filterList");
            filterListField.setAccessible(true);
            filterListField.set(handler,filterList);
            Method filterMethod = Handler.class.getDeclaredMethod(
                "filter", 
                new Class[] { InputStream.class, 
                              OutputStream.class, 
                              Integer.TYPE, 
                              Boolean.TYPE});
            filterMethod.setAccessible(true);
            final BreakableOutputStream out = new BreakableOutputStream();
            System.out.println(System.currentTimeMillis());
            new Thread(new Runnable() {
                    public void run()
                    {
                        try
                        {
                            Thread.currentThread().sleep(3000);
                            out.breakNow();
                        }
                        catch (Exception e)
                        {
                        }
                    }	
            	
                }).start();
            
            filterMethod.invoke(handler, new Object[] {
                reply.getContent(),
                out,
                new Integer(sampleResponse.length()),
                new Boolean(false)});
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
		finally 
		{
            printThreads();
		}
    }

    private void printThreads()
    {
    	try
    	{
            Thread.currentThread().sleep(2000);
    	} catch(Exception e)
    	{
    	}
        Thread list[] = new Thread[1024];
        int count = Thread.currentThread().enumerate(list);
        for (int i=0;i<count;i++)
        {
        	String threadName = list[i].getName();
            System.out.println(threadName);
            assertTrue("A remaining ContentFilter thread was detected: "+
                       threadName,
                       threadName.indexOf("ImageKill") == -1 &&
                       threadName.indexOf("NoThanks") == -1);
        }
    }

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(new TestSuite(ContentFilterTest.class));
        System.exit(0);
    }
}    
