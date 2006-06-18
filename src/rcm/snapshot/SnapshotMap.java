package rcm.snapshot;

import java.util.*;
import java.io.*;

class SnapshotMap {
    String directory;
    Hashtable map = new Hashtable ();
    PrintStream mapOut;
    Random random;

    public SnapshotMap (String directory) {
        this.directory = directory;
        System.err.println ("loading map from " + directory);

        File mapFile = new File (directory, "map");

        try {
            // Read in URL->file map from disk
            loadMap (mapFile);

            // Start appending to map file
            mapOut = 
                new PrintStream 
                    (new FileOutputStream (mapFile.toString (), true));

        } catch (IOException e) {
            e.printStackTrace ();

            try {
                // Reopen map file
                mapOut = new PrintStream (new FileOutputStream (mapFile));
            } catch (IOException e2) {
                e2.printStackTrace ();

                // can't deal without a map file
                throw new RuntimeException (e2.toString ());
            }
        }

        // create the random number generator
        random = new Random ();
    } 

    void loadMap (File f) throws IOException {
        InputStream in = new FileInputStream (f);

        int c;
        StringBuffer filename = new StringBuffer ();
        StringBuffer url = new StringBuffer ();
        StringBuffer target = filename;

    loop:
        while (true) {
            c = in.read ();
            switch (c) {
            case ' ':
            case '\t':
                // separates snapshot filename from its URL;
                // switch target
                if (target == filename)
                    target = url;
                break;
            case '\r':
            case '\n':
            case -1: // EOF
                if (url.length () > 0 && filename.length () > 0) {
                    System.err.println (url + " => " + filename);
                    map.put (url.toString (), filename.toString ());
                    url.setLength (0);
                    filename.setLength (0);
                    target = filename;
                }
                if (c == -1)
                    break loop;
                break;
            default:
                target.append ((char) c);
                break;
            }
        }
    }

    public File get (String url) {
        String code = (String) map.get (url);
        if (code == null)
            return null;

        File f = new File (directory, code);
        if (!f.exists ())
            return null;

        return f;
    }

    public File put (String url) {
        // first check whether url is already mapped to a file -- if so,
        // return that one
        File f = get (url);
        if (f != null)
            return f;

        // otherwise pick random codes until finding one that isn't in use
        String code;
        int tries = 0;
        do {
            if (++tries > 10)
                throw new RuntimeException ("internal error: couldn't find free code");
            code = Integer.toHexString (random.nextInt ());
        } while (map.contains (code));

        // store the code in the map, both in memory and on disk
        synchronized (map) {
            map.put (url, code);
            mapOut.println (code + " " + url);
            mapOut.flush ();
        }

        return new File (directory, code);
    }

    public String getDirectory () {
        return directory;
    }

    public void close () {
        mapOut.close ();
    }
    
    public Map getMap()
    {
    	return map;	
    }
}
