package org.doit.util;

import java.util.StringTokenizer;


/**
 * @author Fabien Le Floc'h
 *
 */
public class StringUtil
{
    /**
     *
     * @param input comma separated list
     * @return
     */
    public static String[] getList(String input)
    {
        return getList(input, ",");
    }

    public static String[] getList(String input, String sep)
    {
        if (input == null)
        {
            return new String[0];
        }

        StringTokenizer st   = new StringTokenizer(input, sep);
        String[]        list = new String[st.countTokens()];

        for (int i = 0; st.hasMoreTokens(); i++)
        {
            list[i] = new String((String) st.nextToken());
        }

        return list;
    }

}
