/* $Id: Main.java,v 1.36 2003/06/05 19:57:45 flefloch Exp $ */

/*
 * Copyright (C) 1996-2003 Mark R. Boyns <boyns@doit.org>
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
package org.doit.muffin;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

import org.doit.util.ReusableThread;
import org.doit.util.Strings;
import org.doit.util.ThreadPool;

/**
 * Startup interface to Muffin.  Parses command line options, loads user
 * preferences, and creates the GUI interface if necessary.
 *
 * @author Mark Boyns
 * @author Fabien Le Floc'h
 */
public class Main
    implements ActionListener, WindowListener, ConfigurationListener
{
    private final static String version = "0.9.4";
    private final static String url = "http://muffin.doit.org/";

    private static InetAddress host;
    private static Options options;
    private static Configuration configs;
    private static FilterManager manager;
    private static LogFile logfile;
    private static ThreadPool pool;

    static {
        Strings.addBundle(
            ResourceBundle.getBundle("org.doit.muffin.LocalStrings"));
    }

    private Server server;
    private String infoString;
    private Button suspendButton;
    private MenuBar menuBar;
    private Monitor monitor;
    private Label infoLabel;
    private Panel controlPanel;
    private MuffinFrame frame;

    /**
     * Create Main.
     */
    public Main()
    {
        pool = new ThreadPool("Muffin Threads");

        String hostName =
            options.getString("muffin.bindaddress") == null
                ? getMuffinHost().toString()
                : options.getString("muffin.bindaddress");

        infoString =
            Strings.getString(
                "muffin.status",
                new Object[] {
                    getMuffinVersion(),
                    hostName,
                    options.getString("muffin.port")});

        manager = new FilterManager(options, configs);

        if (options.getBoolean("muffin.noWindow"))
        {
            TextMonitor tm = new TextMonitor(infoString);
            monitor = tm;
        }
        else
        {
            frame = new MuffinFrame(Strings.getString("muffin.title"));
            monitor = new CanvasMonitor(this);
            gui();
        }

        InetAddress bindAddr =
            options.getString("muffin.bindaddress") == null
                ? null
                : getMuffinHost();
        server =
            new Server(
                new DefaultServerSocketCreator(),
                options.getInteger("muffin.port"),
                bindAddr,
                monitor,
                manager,
                options);

        /* Initialize internal Httpd */

        HttpErrorFactory.init(options);
        HttpdFactory.init(options, manager, monitor);
        /* Startup the Janitor */
        Janitor j = new Janitor();
        j.add(pool);
        getThread().setRunnable(j);

        System.out.println(infoString);
    }

    public static String getMuffinUrl()
    {
        return url;
    }

    public static String getMuffinVersion()
    {
        return version;
    }

    public static InetAddress getMuffinHost()
    {
        return host;
    }

    public static boolean isMuffinAddress(InetAddress address)
    {
        return address.equals(getMuffinHost())
            || (address.getAddress()[0] == 127
                && options.getString("muffin.bindaddress") == null);
    }

    public static ReusableThread getThread()
    {
        return pool.get();
    }

    /**
     * Initialize the GUI interface.
     */
    void gui()
    {
        menuBar = new MenuBar();

        Menu menu = new Menu(Strings.getString("menu.file"));
        //menu.setFont(new Font("Helvetica", Font.BOLD, 12));
        MenuItem item;

        if (!options.getBoolean("muffin.passthru"))
        {
            item = new MenuItem(Strings.getString("menu.file.disable"));
            item.setActionCommand("doDisable");
        }
        else
        {
            item = new MenuItem(Strings.getString("menu.file.enable"));
            item.setActionCommand("doEnable");
        }

        item.addActionListener(this);
        menu.add(item);
        item = new MenuItem(Strings.getString("menu.file.suspend"));
        item.setActionCommand("doSuspend");
        item.addActionListener(this);
        menu.add(item);
        item = new MenuItem(Strings.getString("menu.file.quit"));
        item.setActionCommand("doQuit");
        item.addActionListener(this);
        menu.add(item);
        menuBar.add(menu);

        menu = new Menu(Strings.getString("menu.edit"));
        item = new MenuItem(Strings.getString("menu.edit.configs"));
        item.setActionCommand("doConfigs");
        item.addActionListener(this);
        menu.add(item);
        item = new MenuItem(Strings.getString("menu.edit.filters"));
        item.setActionCommand("doFilters");
        item.addActionListener(this);
        menu.add(item);
        item = new MenuItem(Strings.getString("menu.edit.options"));
        item.setActionCommand("doOptions");
        item.addActionListener(this);
        menu.add(item);
        menuBar.add(menu);

        menu = new Menu(Strings.getString("menu.view"));
        item = new MenuItem(Strings.getString("menu.view.connections"));
        item.setActionCommand("doConnections");
        item.addActionListener(this);
        menu.add(item);
        item = new MenuItem(Strings.getString("menu.view.regex"));
        item.setActionCommand("doRegex");
        item.addActionListener(this);
        menu.add(item);
        item = new MenuItem(Strings.getString("menu.view.threads"));
        item.setActionCommand("doThreads");
        item.addActionListener(this);
        menu.add(item);
        menuBar.add(menu);

        menu = new Menu(Strings.getString("menu.help"));
        item = new MenuItem(Strings.getString("menu.help.about"));
        item.setActionCommand("doAbout");
        item.addActionListener(this);
        menu.add(item);
        item = new MenuItem(Strings.getString("menu.help.license"));
        item.setActionCommand("doLicense");
        item.addActionListener(this);
        menu.add(item);
        menuBar.setHelpMenu(menu);

        frame.setMenuBar(menuBar);

        if (monitor instanceof Canvas)
        {
            Canvas canvas = (Canvas) monitor;
            frame.add("Center", canvas);
        }

        infoLabel = new Label(infoString);
        infoLabel.setFont(options.getFont("muffin.smallfont"));
        frame.add("South", infoLabel);

        frame.addWindowListener(this);
        configs.addConfigurationListener(this);

        frame.updateGeometry(options.getString("muffin.geometry"));

        frame.show();
    }

    public void configurationChanged(String name)
    {
        infoLabel.setText(infoString + " (" + name + ")");
    }

    /**
     * Handle Button events.
     *
     * @param event some event.
     */
    public void actionPerformed(ActionEvent event)
    {
        String arg = event.getActionCommand();

        if ("doQuit".equals(arg))
        {
            closeApplication();
        }
        else if ("doDisable".equals(arg))
        {
            MenuItem item = (MenuItem) event.getSource();
            item.setActionCommand("doEnable");
            item.setLabel(Strings.getString("menu.file.enable"));
            options.putBoolean("muffin.passthru", true);
        }
        else if ("doEnable".equals(arg))
        {
            MenuItem item = (MenuItem) event.getSource();
            item.setActionCommand("doDisable");
            item.setLabel(Strings.getString("menu.file.disable"));
            options.putBoolean("muffin.passthru", false);
        }
        else if ("doSuspend".equals(arg))
        {
            MenuItem item = (MenuItem) event.getSource();
            item.setActionCommand("doResume");
            item.setLabel(Strings.getString("menu.file.resume"));
            server.suspend();
            monitor.suspend();
        }
        else if ("doResume".equals(arg))
        {
            MenuItem item = (MenuItem) event.getSource();
            item.setActionCommand("doSuspend");
            item.setLabel(Strings.getString("menu.file.suspend"));
            server.resume();
            monitor.resume();
        }
        else if ("doConnections".equals(arg))
        {
            new ConnectionsFrame(monitor);
        }
        else if ("doThreads".equals(arg))
        {
            new ThreadsFrame(Thread.currentThread());
        }
        else if ("doRegex".equals(arg))
        {
            new RegexFrame();
        }
        else if ("doAbout".equals(arg))
        {
            new About(options);
        }
        else if ("doLicense".equals(arg))
        {
            new HelpFrame("COPYING");
        }
        // 	else if ("doStop".equals(arg))
        // 	{
        // 	    server.stop();
        // 	}
        else if ("doFilters".equals(arg))
        {
            manager.createFrame();
        }
        else if ("doOptions".equals(arg))
        {
            options.createFrame();
        }
        else if ("doConfigs".equals(arg))
        {
            configs.createFrame();
        }
        else
        {
            minimize(false);
        }
    }

    void minimize(boolean enable)
    {
        if (enable)
        {
            infoLabel.setVisible(false);
            //controlPanel.setVisible(false);
            frame.remove(menuBar);
            monitor.minimize(true);
        }
        else
        {
            infoLabel.setVisible(true);
            //controlPanel.setVisible(true);
            frame.setMenuBar(menuBar);
            monitor.minimize(false);
        }

        if (options.exists("muffin.geometry"))
        {
            frame.updateGeometry(options.getString("muffin.geometry"));
        }
        else
        {
            frame.hide();
            frame.setSize(frame.getPreferredSize());
            frame.pack();
            frame.show();
        }
    }

    public void windowActivated(WindowEvent e)
    {
    }

    public void windowDeactivated(WindowEvent e)
    {
    }

    public void windowClosing(WindowEvent e)
    {
        closeApplication();
    }

    public void windowClosed(WindowEvent e)
    {
    }

    public void windowIconified(WindowEvent e)
    {
    }

    public void windowDeiconified(WindowEvent e)
    {
    }

    public void windowOpened(WindowEvent e)
    {
    }

    void closeApplication()
    {
        /* Disable all enabled filters */
        // 	manager.disableAll();
        // 	setVisible(false);
        // 	System.exit(0);

        Main.stopMuffin(this);
        System.exit(0);
    }

    static void systemInfo()
    {
        System.out.println("Muffin:");
        System.out.println("-------");
        System.out.println("muffin.version " + version);
        System.out.println();
        System.out.println("Java Virtual Machine:");
        System.out.println("---------------------");
        System.out.println(
            "java.version " + System.getProperty("java.version"));
        System.out.println(
            "java.class.version " + System.getProperty("java.class.version"));
        System.out.println(
            "java.class.path " + System.getProperty("java.class.path"));
        System.out.println("java.home " + System.getProperty("java.home"));
        System.out.println("java.vendor " + System.getProperty("java.vendor"));
        System.out.println();
        System.out.println("Operating System:");
        System.out.println("-----------------");
        System.out.println("os.version " + System.getProperty("os.version"));
        System.out.println("os.arch " + System.getProperty("os.arch"));
        System.out.println("os.name " + System.getProperty("os.name"));
        System.out.println();
        System.out.println("User:");
        System.out.println("-----");
        System.out.println("user.name " + System.getProperty("user.name"));
        System.out.println("user.dir " + System.getProperty("user.dir"));
        System.out.println("user.home " + System.getProperty("user.home"));
    }

    static String copyleft()
    {
        StringBuffer buf = new StringBuffer();
        buf.append(
            "Muffin version "
                + version
                + ", Copyright (C) 1996-2003 Mark R. Boyns <boyns@doit.org>\n");
        buf.append(
            "Muffin comes with ABSOLUTELY NO WARRANTY; for details see Help/License.\n");
        buf.append(
            "This is free software, and you are welcome to redistribute it\n");
        buf.append("under certain conditions; see Help/License for details.\n");
        return buf.toString();
    }

    public static Options getOptions()
    {
        return options;
    }

    public static FilterManager getFilterManager()
    {
        return manager;
    }

    public static LogFile getLogFile()
    {
        return logfile;
    }

    public static String getBaseUrl()
    {
        StringBuffer buf = new StringBuffer();
        buf.append("http://");
        buf.append(Main.getMuffinHost().getHostName());
        buf.append(":");
        buf.append(HttpdFactory.getOptions().getString("muffin.port"));
        return buf.toString();
    }

    public static void stopMuffin(Main theMuffin)
    {
        theMuffin.manager.disableAll();
        if (theMuffin.frame != null)
        {
            theMuffin.frame.setVisible(false);
        }
    }

    public static Main startMuffin(String[] argv)
    {
        try
        {
            processArgs(argv);
            final Main theMuffin = new Main();
            Thread t = new Thread(theMuffin.server);
            t.start();
            return theMuffin;
        }
        catch (Throwable exc)
        {
            exc.printStackTrace();
            return null;
        }
    }

    public static void processArgs(String[] argv) throws NumberFormatException
    {
        int c;
        String arg;
        LongOpt longopts[] = new LongOpt[16];

        longopts[0] = new LongOpt("port", LongOpt.REQUIRED_ARGUMENT, null, 'p');
        longopts[1] = new LongOpt("conf", LongOpt.REQUIRED_ARGUMENT, null, 'c');
        longopts[2] = new LongOpt("dir", LongOpt.REQUIRED_ARGUMENT, null, 'd');
        longopts[3] =
            new LongOpt("httpProxyHost", LongOpt.REQUIRED_ARGUMENT, null, 2);
        longopts[4] =
            new LongOpt("httpProxyPort", LongOpt.REQUIRED_ARGUMENT, null, 3);
        longopts[5] =
            new LongOpt("httpsProxyHost", LongOpt.REQUIRED_ARGUMENT, null, 4);
        longopts[6] =
            new LongOpt("httpsProxyPort", LongOpt.REQUIRED_ARGUMENT, null, 5);
        longopts[7] = new LongOpt("nw", LongOpt.NO_ARGUMENT, null, 6);
        longopts[8] = new LongOpt("font", LongOpt.REQUIRED_ARGUMENT, null, 7);
        longopts[9] =
            new LongOpt("smallfont", LongOpt.REQUIRED_ARGUMENT, null, 8);
        longopts[10] =
            new LongOpt("bigfont", LongOpt.REQUIRED_ARGUMENT, null, 9);
        longopts[11] = new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h');
        longopts[12] = new LongOpt("version", LongOpt.NO_ARGUMENT, null, 'v');
        longopts[13] =
            new LongOpt("geometry", LongOpt.REQUIRED_ARGUMENT, null, 'g');
        longopts[14] =
            new LongOpt("bindaddress", LongOpt.REQUIRED_ARGUMENT, null, 'b');
        longopts[15] =
            new LongOpt("props", LongOpt.REQUIRED_ARGUMENT, null, 10);

        Prefs args = new Prefs();
        Getopt g = new Getopt("Muffin", argv, "v", longopts, true);
        while ((c = g.getopt()) != -1)
        {
            switch (c)
            {
                case 0 :
                    /* do nothing */
                    break;

                case 'p' : /* --port */
                    try
                    {
                        args.putInteger(
                            "port",
                            Integer.parseInt(g.getOptarg()));
                    }
                    catch (NumberFormatException e)
                    {
                        System.out.println("invalid port: " + g.getOptarg());
                        throw e;
                    }
                    break;

                case 'b' : /* --bindaddress */
                    args.putString("bindaddress", g.getOptarg());
                    break;

                case 'c' : /* --conf */
                    args.putString("conf", g.getOptarg());
                    break;

                case 'd' : /* --dir */
                    args.putString("dir", g.getOptarg());
                    break;

                case 2 : /* httpProxyHost */
                    args.putString("httpProxyHost", g.getOptarg());
                    break;

                case 3 : /* httpProxyPort */
                    try
                    {
                        args.putInteger(
                            "httpProxyPort",
                            Integer.parseInt(g.getOptarg()));
                    }
                    catch (NumberFormatException e)
                    {
                        System.out.println(
                            "invalid httpProxyPort: " + g.getOptarg());
                        throw e;
                    }
                    break;

                case 4 : /* httpsProxyHost */
                    args.putString("httpsProxyHost", g.getOptarg());
                    break;

                case 5 : /* httpsProxyPort */
                    try
                    {
                        args.putInteger(
                            "httpsProxyPort",
                            Integer.parseInt(g.getOptarg()));
                    }
                    catch (NumberFormatException e)
                    {
                        System.out.println(
                            "invalid httpsProxyPort: " + g.getOptarg());
                        throw e;
                    }
                    break;

                case 6 : /* --nw */
                    args.putBoolean("noWindow", true);
                    break;

                case 'v' : /* --version */
                    systemInfo();
                    throw new ThreadDeath();

                case 7 : /* --font */
                    args.putString("font", g.getOptarg());
                    break;

                case 8 : /* --smallfont */
                    args.putString("smallfont", g.getOptarg());
                    break;

                case 9 : /* --bigfont */
                    args.putString("bigfont", g.getOptarg());
                    break;

                case 10 : /* --props */
                    args.putString("props", g.getOptarg());
                    break;

                case 'g' : /* --geometry */
                    args.putString("geometry", g.getOptarg());
                    break;

                case 'h' : /* --help */
                    System.out.println(
                        "usage: java Muffin [options]\n\n"
                            + "-conf NAME            Default configuration (default.conf)\n"
                            + "-dir DIR              Preferences directory (~/Muffin)\n"
                            + "-font FONT            Default font\n"
                            + "-smallfont FONT       Font used for small text\n"
                            + "-bigfont FONT         Font used for large text\n"
                            + "-help                 This useful message\n"
                            + "-httpProxyHost HOST   Use HOST as the HTTP proxy\n"
                            + "-httpProxyPort PORT   Use PORT as the HTTP proxy port\n"
                            + "-httpsProxyHost HOST  Use HOST as the SSL proxy\n"
                            + "-httpsProxyPort PORT  Use PORT as the SSL proxy port\n"
                            + "-nw                   Don't create any windows\n"
                            + "-port PORT            Listen on PORT for browser requests (51966)\n"
                            + "-props NAME           Default muffin properties file (muffin.props)\n"
                            + "-bindaddress IPADDR   Only bind to IPADDR\n"
                            + "-v                    Display muffin version\n");
                    throw new ThreadDeath();

                case '?' :
                    throw new RuntimeException("Unknown option");

                default :
                    break;
            }
        }

        configs = new Configuration();
        if (args.exists("dir"))
        {
            configs.setUserDirectory(args.getString("dir"));
        }

        /* Create Muffin dir if it doesn't exist */
        configs.checkUserDirectory();
        configs.scan();

        String defaultConfig = "default.conf";
        if (args.exists("conf"))
        {
            defaultConfig = args.getString("conf");
        }
        configs.setDefault(defaultConfig);
        configs.setCurrent(defaultConfig);

        /* Create muffin run-time options */
        String defaultProps = "muffin.props";
        if (args.exists("props"))
        {
            defaultProps = args.getString("props");
        }

        options = new Options(defaultProps);

        if (args.exists("port"))
        {
            options.putInteger("muffin.port", args.getInteger("port"));
        }
        if (args.exists("httpProxyHost"))
        {
            options.putString(
                "muffin.httpProxyHost",
                args.getString("httpProxyHost"));
        }
        if (args.exists("httpProxyPort"))
        {
            options.putInteger(
                "muffin.httpProxyPort",
                args.getInteger("httpProxyPort"));
        }
        if (args.exists("httpsProxyHost"))
        {
            options.putString(
                "muffin.httpsProxyHost",
                args.getString("httpsProxyHost"));
        }
        if (args.exists("httpsProxyPort"))
        {
            options.putInteger(
                "muffin.httpsProxyPort",
                args.getInteger("httpsProxyPort"));
        }
        if (args.exists("noWindow"))
        {
            options.putBoolean("muffin.noWindow", args.getBoolean("noWindow"));
        }
        if (args.exists("font"))
        {
            options.putString("muffin.font", args.getString("font"));
        }
        if (args.exists("smallfont"))
        {
            options.putString("muffin.smallfont", args.getString("smallfont"));
        }
        if (args.exists("bigfont"))
        {
            options.putString("muffin.bigfont", args.getString("bigfont"));
        }
        if (args.exists("geometry"))
        {
            options.putString("muffin.geometry", args.getString("geometry"));
        }
        if (args.exists("bindaddress"))
        {
            options.putString(
                "muffin.bindaddress",
                args.getString("bindaddress"));
        }

        String bindaddr = options.getString("muffin.bindaddress");
        try
        {
            if (bindaddr == null || bindaddr.length() <= 0)
            {
                // bind to all local addresses
                host = InetAddress.getLocalHost();
            }
            else
            {
                // bind to specified address
                host = InetAddress.getByName(bindaddr);
            }
        }
        catch (UnknownHostException e)
        {
            try
            {
                // bind to 127.0.0.1
                host = InetAddress.getByName("127.0.0.1");
            }
            catch (UnknownHostException uhe)
            {
                System.err.println("can not start muffin: " + uhe.getMessage());
            }
        }

        options.sync();

        UserFile f = options.getUserFile(options.getString("muffin.logfile"));
        if (f instanceof LocalFile)
        {
            logfile = new LogFile(f.getName());
        }
        else
        {
            logfile = null;
        }

        configs.setAutoConfigFile(options.getString("muffin.autoconfig"));
        configs.load();
    }

    public static void main(String[] argv)
    {
        System.out.println(copyleft());
        try
        {
            startMuffin(argv);
        }
        catch (ThreadDeath e)
        {
            e.printStackTrace();
            System.exit(0);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }
}