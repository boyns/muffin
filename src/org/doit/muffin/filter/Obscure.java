/*
 * Copyright (C) 2002-2003 Fabien Le Floc'h <fabien@31416.org>
 *
 * This file is part of Muffin.
 *
 * Muffin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Muffin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Muffin; see the file COPYING.  If not, write to the
 * Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */
package org.doit.muffin.filter;

import org.doit.muffin.*;

import java.util.Random;
import java.util.StringTokenizer;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

//import org.apache.log4j.Logger;
//import org.apache.log4j.Level;

/**
 * @author Fabien Le Floc'h
 *
 * <a href="http://www.searchlores.org/obscure.htm">obscure</a> urls.
 * during your surf with configurable obscuring options. 
 * 
 * Obscure Filter Configuration:
 * <BR>
 * <BR># decimal,octal or hexacimal or random ip obscuring
 * <BR>Obscure.ipFormat=decimal
 * <BR># 1 or 4 segments ip?
 * <BR>Obscure.ipSegments=1
 * <BR>#true or false to obscure the path part of the url
 * <BR>Obscure.doPath=true
 * <BR>#true or false to obscure automatically on redirection
 * <BR>Obscure.doRedirect=true
 */
public class Obscure implements FilterFactory
{

    private static Random RANDOM = new Random();

    //private static Logger LOGGER = Logger.getLogger(Obscure.class.getName());

    FilterManager _manager;
    Prefs _prefs;
    IpObscurantist _ipObscurantist;
    int _segments;
    boolean _obscureUrl;

    public Obscure()
    {
        //System.out.println("OBSCURE - constructor");
    }

    /**
     * @see FilterFactory#setManager(FilterManager)
     */
    public void setManager(FilterManager m)
    {
        _manager = m;
    }

    /**
     * @see FilterFactory#setPrefs(Prefs)
     */
    public void setPrefs(Prefs p)
    {
        _prefs = p;
        boolean o = _prefs.getOverride();
        _prefs.setOverride(false);
        _prefs.putString("Obscure.ipFormat", "decimal");
        _prefs.putInteger("Obscure.ipSegments", 1);
        _prefs.putBoolean("Obscure.doPath", true);
        _prefs.putBoolean("Obscure.doRedirect", true);
        _prefs.setOverride(o);
        _segments = _prefs.getInteger("Obscure.ipSegments");
        _obscureUrl = _prefs.getBoolean("Obscure.doPath");

        String ipFormatString = _prefs.getString("Obscure.ipFormat");
        if (ipFormatString.equals("decimal"))
        {
            _ipObscurantist = new DecimalIpObscurantist();
        }
        else if (ipFormatString.equals("octal"))
        {
            _ipObscurantist = new OctalIpObscurantist();
        }
        else if (ipFormatString.equals("hexadecimal"))
        {
            _ipObscurantist = new HexadecimalIpObscurantist();
        }
        else if (ipFormatString.equals("random"))
        {
            _ipObscurantist = new RandomIpObscurantist();
        }
        else
        {
            //            LOGGER.log(
            //                Level.WARN,
            //                "property Obscure.ipFormat"
            //                    + "contains an unrecognized value,"
            //                    + " decimal format will be used");
            _ipObscurantist = new DecimalIpObscurantist();
        }
        //messages = new MessageArea();
    }

    /**
     * @see FilterFactory#getPrefs()
     */
    public Prefs getPrefs()
    {
        return _prefs;
    }

    /**
     * @see FilterFactory#viewPrefs()
     */
    public void viewPrefs()
    {
    }

    /**
     * @see FilterFactory#shutdown()
     */
    public void shutdown()
    {
    }

    /**
     * @see FilterFactory#createFilter()
     */
    public Filter createFilter()
    {
        Filter f = new ObscureFilter(this);
        f.setPrefs(_prefs);
        return f;
    }

    String rewrite(String urlString)
    {
        //System.out.println("OBSCURE - rewrite");
        // currently don't care about prefs
        //, just do double-word decimal
        URL url = null;
        try
        {
            url = new URL(urlString);
        }
        catch (MalformedURLException mue)
        {
            return urlString;
        }

        String host = url.getHost();

        host = _ipObscurantist.obscure(host);
        //System.out.println("OBSCURE - obscured host="+host);

        String path = url.getPath();
        int port = url.getPort();
        String protocol = url.getProtocol();
        String query = url.getQuery();
        String userInfo = url.getUserInfo();

        StringBuffer buf = new StringBuffer(protocol);
        buf.append("://");
        if (userInfo != null)
            buf.append("userInfo").append("%40");
        buf.append(host);
        if (port > 0)
            buf.append(":").append(port);
        if (path != null)
        {
            if (_obscureUrl)
            {
                buf.append(obscurePath(path));
            }
            else
                buf.append(path);
        }
        if (query != null)
            buf.append("?").append(query);
        //LOGGER.log(Level.DEBUG, "new address=" + buf.toString());
        return buf.toString();
    }

    private String obscurePath(String path)
    {
        StringBuffer buf = new StringBuffer();
        StringTokenizer st = new StringTokenizer(path, "/", true);
        while (st.hasMoreTokens())
        {
            String token = st.nextToken();
            if (token.indexOf('%') >= 0)
            {
                buf.append(token); //was already encoded
            }
            else
            {
                int l = token.length();
                for (int i = 0; i < l; i++)
                {
                    char c = token.charAt(i);
                    if (i % 2 == 0)
                    {
                        if (c == '/')
                        {
                            buf.append(c);
                        }
                        else
                        {
                            buf.append('%');
                            String hexString = Integer.toHexString(c);
                            if (hexString.length() < 2)
                                buf.append("0");
                            buf.append(hexString);
                        }
                    }
                    else
                    {
                        buf.append(c);
                    }

                }

            }
        }

        return buf.toString();
    }

    private abstract class IpObscurantist
    {

        String obscure(String host)
        {
            try
            {
                byte[] b = MuffinResolver.getByName(host).getAddress();
                if (_segments == 1)
                {
                    long longAddr = 0;
                    for (int i = 0; i < 4; i++)
                    {
                        long l = ((long) b[3 - i] & 0xFF);
                        longAddr = longAddr + (l << (8 * i));
                    }

                    return convertLong(longAddr);

                }
                else // if (_segments = 4)
                    {
                    StringBuffer buf = new StringBuffer();
                    for (int i = 0; i < 4; i++)
                    {
                        long l = ((long) b[i] & 0xFF);
                        buf.append(convertLong(l)).append(".");
                    }
                    return buf.substring(0, buf.length() - 1);
                }
            }
            catch (UnknownHostException e)
            {
                //                LOGGER.log(Level.WARN, "could not obscure ip", e);
                return host;
            }

        }
        abstract String convertLong(long longAddr);
    }

    private class DecimalIpObscurantist extends IpObscurantist
    {

        String convertLong(long longAddr)
        {
            return Long.toString(longAddr);
        }
    }

    private class OctalIpObscurantist extends IpObscurantist
    {

        String convertLong(long longAddr)
        {

            return "0" + Long.toOctalString(longAddr);
        }
    }

    private class HexadecimalIpObscurantist extends IpObscurantist
    {

        String convertLong(long longAddr)
        {
            String value = Long.toHexString(longAddr);
            if (value.length() < 2)
                value = "0" + value;
            return "0x" + value;
        }
    }

    private class RandomIpObscurantist extends IpObscurantist
    {

        String convertLong(long longAddr)
        {
            int r = Math.abs(RANDOM.nextInt() % 3);
            //            LOGGER.log(Level.DEBUG,"random="+r);
            if (r == 0)
            {
                return "0x" + Long.toHexString(longAddr);
            }
            else if (r == 1)
            {
                return "0" + Long.toOctalString(longAddr);
            }
            else
            {
                return Long.toString(longAddr);
            }
        }
    }

}