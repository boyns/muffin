package rcm.snapshot;

import org.doit.muffin.*;
import java.io.File;

public class Snapshot implements FilterFactory {
    FilterManager manager;
    SnapshotMap map;
    Prefs prefs;
    SnapshotFrame frame;

    public void setManager (FilterManager manager) {
        this.manager = manager;
    }

    public void setPrefs (Prefs prefs) {
        this.prefs = prefs;

        boolean o = prefs.getOverride ();
        prefs.setOverride (false);
        prefs.putString ("Snapshot.directory", "snapshot");
        prefs.putBoolean ("Snapshot.replaying", false);
        prefs.setOverride (o);

        loadMap ();
    }

    public Prefs getPrefs () {
        return prefs;
    }

    public void viewPrefs () {
        if (frame == null)
            frame = new SnapshotFrame (prefs, this);
        frame.setVisible (true);
    }

    public void shutdown () {
        if (map != null)
            map.close ();

        if (frame != null)
            frame.dispose ();
    }

    public void save () {
        manager.save (this);
    }

    public synchronized Filter createFilter () {
        if (map == null)
            loadMap ();
        Filter f = new SnapshotFilter (map);
        f.setPrefs (prefs);
        return f;
    }

    void clearSnapshot () {
        if (map != null) {
            map.close ();
            map = null;
        }
        
	// LocalFile
	UserFile file = prefs.getUserFile(prefs.getString ("Snapshot.directory"));
        File dir = new File(file.getName());
        String[] files = dir.list ();
        for (int i = 0; i < files.length; ++i)
            new File (dir, files[i]).delete ();
    }

    void loadMap () {
        // if directory has changed...
	// LocalFile
	UserFile file = prefs.getUserFile(prefs.getString ("Snapshot.directory"));
	String dir = file.getName();
        if (map == null || !dir.equals (map.getDirectory ())) {
            // directory has changed or just been initialized.
            // make sure the directory exists and load its file map if any
            prefs.checkDirectory (dir);
            map = new SnapshotMap (dir);
        }
    }

}
