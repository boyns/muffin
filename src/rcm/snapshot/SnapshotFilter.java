package rcm.snapshot;

import org.doit.muffin.*;
import java.io.*;

public class SnapshotFilter implements RequestFilter, ReplyFilter, HttpFilter {
    SnapshotMap map;
    Prefs prefs;
    Request request;
    boolean isCacheHit;

    public SnapshotFilter (SnapshotMap map) {
        this.map = map;
    }
            
    public void setPrefs (Prefs prefs) {
        this.prefs = prefs;
    }

    public void filter (Request request) {
        this.request = request;
        isCacheHit = prefs.getBoolean ("Snapshot.replaying") 
                     && map.get (request.getURL ()) != null;
    }

    public void filter (Reply reply) {
        if (!isCacheHit 
            && !DefaultHttpd.sendme(request)) // don't snapshot muffin web administration 
        {
            String url = request.getURL ();
            File cacheFile = map.put (url);

            System.err.println ("Snapshot: storing " + url
                                + " to " + cacheFile);
            
            try {
                OutputStream out = new FileOutputStream (cacheFile);

                // write reply headers to cache file
                reply.write (out);

                // create a tee stream that writes to file
                InputStream tee = new TeeInputStream (reply.getContent (),
                                                      out);
                reply.setContent (tee);
            } catch (Exception e) {
                e.printStackTrace ();
            }
        }
    }

    public boolean wantRequest (Request request) {
        return isCacheHit && !DefaultHttpd.sendme(request); // don't snapshot muffin web administration
    }

    public void sendRequest (Request request) {
    }

    public Reply recvReply (Request request) {
        String url = request.getURL ();
        File cacheFile = map.get (url);
        System.err.println ("Snapshot: satisfying " + url
                            + " from " + cacheFile);
        
        Reply reply = new Reply ();

        // FIX: get from cached file
        reply.setStatusLine ("HTTP/1.0 200 Ok");
        reply.setHeaderField ("Content-type", "text/html");

        try {
            reply.setContent (new FileInputStream (cacheFile));
            reply.read ();
        } catch (Exception e) {
            e.printStackTrace ();
            reply.setContent (new ByteArrayInputStream (new byte[0]));
        }

        return reply;
    }

    public void close () {
    }
}

class TeeInputStream extends FilterInputStream {
    OutputStream out;

    public TeeInputStream (InputStream in, OutputStream out) {
        super (in);
        this.out = out;
    }

    public void close () throws IOException {
        out.close ();
        super.close ();
    }

    public int read () throws IOException {
        int c = super.read ();
        out.write (c);
        return c;
    }

    public int read (byte[] b) throws IOException {
        int n = super.read (b);
        if (n < 0)
            return n;
        out.write (b, 0, n);
        return n;
    }

    public int read (byte[] b, int off, int len) throws IOException {
        int n = super.read (b, off, len);
        if (n < 0)
            return n;
        out.write (b, off, n);
        return n;
    }
}
