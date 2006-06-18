package org.doit.io;

public class ObjectStreamException extends java.io.IOException
{
    /**
	 * Seriarlizable class should declare this:
	 */
	private static final long serialVersionUID = 1L;

	public ObjectStreamException()
    {
        super();
    }

    public ObjectStreamException(String message)
    {
        super(message);
    }
}
