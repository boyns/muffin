package org.doit.io;

import java.io.*;

public class DebugOutputStream
    extends FilterOutputStream
{
    public DebugOutputStream(OutputStream out)
    {
	super(out);
    }

    public void write(int ch)
	throws IOException
    {
	System.out.println("DebugOutputStream.write("+(byte)ch+")");
	out.write(ch);
    }

    public void write(byte[] b)
	throws IOException
    {
	write(b, 0, b.length);
    }

    public void write(byte[] b, int offset, int length)
	throws IOException
    {
	System.out.println("DebugOutputStream.write("+new String(b, offset, length)+")");
	out.write(b, offset, length);
    }

    public void close()
	throws IOException
    {
	System.out.println("DebugOutputStream.close()");
    }
    
    public void flush()
	throws IOException
    {
	System.out.println("DebugOutputStream.flush()");
    }
}
