package org.doit.muffin.filter;

import org.doit.muffin.*;

public class Tester
{
    public Tester(String clazz)
    {
	try
	{
	    Prefs prefs = new Prefs();
	    FilterFactory ff = (FilterFactory) (Class.forName(clazz)).newInstance();
	    ff.setPrefs(prefs);
	    ff.viewPrefs();
	    //ff.setManager(null);
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    public static void main(String args[])
    {
	new Tester(args[0]);
    }
}
