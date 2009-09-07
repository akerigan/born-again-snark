/*
 * SnarkGnome - Main snark program startup class which uses a Gnome UI.
 * Copyright (C) 2003 Mark J. Wielaard
 * 
 * This file is part of Snark.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package org.klomp.snark.gtk;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.gnu.glib.Fireable;
import org.gnu.glib.Timer;
import org.gnu.gnome.App;
import org.gnu.gnome.AppBar;
import org.gnu.gnome.PreferencesType;
import org.gnu.gnome.Program;
import org.gnu.gnome.UIInfo;
import org.gnu.gtk.AboutDialog;
import org.gnu.gtk.Gtk;
import org.gnu.gtk.HBox;
import org.gnu.gtk.Justification;
import org.gnu.gtk.Label;
import org.gnu.gtk.ProgressBar;
import org.gnu.gtk.SizeGroup;
import org.gnu.gtk.SizeGroupMode;
import org.gnu.gtk.VBox;
import org.gnu.gtk.Widget;
import org.gnu.gtk.event.LifeCycleEvent;
import org.gnu.gtk.event.LifeCycleListener;
import org.gnu.gtk.event.MenuItemEvent;
import org.gnu.gtk.event.MenuItemListener;

import org.klomp.snark.CoordinatorListener;
import org.klomp.snark.Peer;
import org.klomp.snark.PeerCoordinator;
import org.klomp.snark.Snark;
import org.klomp.snark.SnarkShutdown;
import org.klomp.snark.ShutdownListener;
import org.klomp.snark.Storage;
import org.klomp.snark.StorageListener;
import org.klomp.snark.cmd.SnarkApplication;

/**
 * Main Snark program startup class that uses a Gnome UI.
 * 
 * @author Mark Wielaard (mark@klomp.org)
 */
public class SnarkGnome implements Runnable, StorageListener,
    CoordinatorListener, ShutdownListener, Fireable, LifeCycleListener,
    MenuItemListener
{
    private static final String VERSION = "0.6";

    private static final String AUTHOR1 = "Mark J. Wielaard <mark@klomp.org>";

    private static final String AUTHOR2 = "Elizabeth Fong <elizabeth@threerings.net>";

    private static final String COPYING = "Copyright 2003, Mark J. Wielaard, "
        + "2006 Three Rings Design" + "\n"
        + "Distributed under the terms of the "
        + "GNU General Public License (GPL)";

    private static final String COMMENT = "A BitTorrent client, torrent creator and tracker"
        + " that makes sharing files with your buddies as easy as possible."
        + "\n\n"
        + "Snark comes with ABSOLUTELY NO WARRANTY.  "
        + "This is free software, and you are welcome to redistribute it "
        + "under certain conditions; read the COPYING file for details.";

    // How often to call the update time which calls fire() per second.
    static int UPDATE_TIMER = 2;

    private static SnarkShutdown snarkhook;

    private String[] args;

    private String name;

    private final App app;

    private final AppBar appbar;

    private final Widget propertiesItem;

    private final Widget quitItem;

    private final Widget aboutItem;

    private final Label torrentName;

    private final Label downloadRate;

    private final Label uploadRate;

    private final Label piecesCollected;

    private final GnomeInfoWindow propertiesWindow;

    /**
     * Creates the whole SnarkGnome application.
     */
    private SnarkGnome ()
    {
        // Main window
        app = new App("snark", "Snark");
        app.addListener(this);

        // Status - what are we doing, how much have we done - thingy
        appbar = new AppBar(true, true, PreferencesType.USER);
        appbar.setStatusText("Snark ready...");
        app.setStatusBar(appbar);

        // Menus
        UIInfo quitMenuItem = UIInfo.quitItem(this);
        UIInfo propertiesMenuItem = UIInfo.propertiesItem(this);
        UIInfo fileMenu[] = { propertiesMenuItem, quitMenuItem, UIInfo.end() };

        UIInfo aboutMenuItem = UIInfo.aboutItem(this);
        UIInfo helpMenu[] = { aboutMenuItem, UIInfo.end() };

        UIInfo appMenus[] = { UIInfo.subtree("_File", fileMenu),
                UIInfo.subtree("_Help", helpMenu), UIInfo.end() };

        // Add them to the window and make them give status info.
        app.createMenus(appMenus);
        app.installMenuHints(appMenus);

        // Add some properties to the window
        // Torrent that we are sharing
        HBox torrentBox = new HBox(false, 6);
        Label torrent = new Label("Sharing:");
        torrent.setJustification(Justification.RIGHT);
        torrent.setAlignment(0d, 0.5d);
        torrentName = new Label("<unknown>");
        torrentName.setJustification(Justification.LEFT);
        torrentName.setAlignment(0d, 0.5d);
        torrentBox.packStart(torrent);
        torrentBox.packEnd(torrentName);

        // Download rate
        HBox downloadBox = new HBox(false, 6);
        Label download = new Label("Download:");
        download.setJustification(Justification.RIGHT);
        download.setAlignment(0d, 0.5d);
        downloadRate = new Label("0 KB/s");
        downloadRate.setJustification(Justification.LEFT);
        downloadRate.setAlignment(0d, 0.5d);
        downloadBox.packStart(download);
        downloadBox.packEnd(downloadRate);

        // Upload rate
        HBox uploadBox = new HBox(false, 6);
        Label upload = new Label("Upload:");
        upload.setJustification(Justification.RIGHT);
        upload.setAlignment(0d, 0.5d);
        uploadRate = new Label("0 KB/s");
        uploadRate.setJustification(Justification.LEFT);
        uploadRate.setAlignment(0d, 0.5d);
        uploadBox.packStart(upload);
        uploadBox.packEnd(uploadRate);

        // Pieces
        HBox piecesBox = new HBox(false, 6);
        Label pieces = new Label("Pieces:");
        pieces.setJustification(Justification.RIGHT);
        pieces.setAlignment(0d, 0.5d);
        piecesCollected = new Label("");
        piecesCollected.setJustification(Justification.LEFT);
        piecesCollected.setAlignment(0d, 0.5d);
        piecesBox.packStart(pieces);
        piecesBox.packEnd(piecesCollected);

        // Group labels to get the same sizes.
        SizeGroup labelGroup = new SizeGroup(SizeGroupMode.HORIZONTAL);
        labelGroup.addWidget(torrent);
        labelGroup.addWidget(download);
        labelGroup.addWidget(upload);
        labelGroup.addWidget(pieces);

        // Group values to get the same sizes.
        SizeGroup valueGroup = new SizeGroup(SizeGroupMode.HORIZONTAL);
        valueGroup.addWidget(torrentName);
        valueGroup.addWidget(downloadRate);
        valueGroup.addWidget(uploadRate);
        valueGroup.addWidget(piecesCollected);

        // Put it all together
        VBox infoBox = new VBox(true, 6);
        infoBox.setBorderWidth(12);
        infoBox.packStart(torrentBox);
        infoBox.packStart(downloadBox);
        infoBox.packStart(uploadBox);
        infoBox.packStart(piecesBox);
        app.setContent(infoBox);

        // HACK - Somehow the application window is to small.
        // Make sure it has room for at least the progress bar and status.
        // Sadly we cannot get the default size of the progress bar...
        // So we just guess some width. BAD!
        app.setDefaultSize(300, -1);

        // We are ready to show and tell!
        app.showAll();

        // Create, but don't show properties window.
        propertiesWindow = new GnomeInfoWindow(_snark);

        // HACK - Only after we show everything can we get the widgets.
        propertiesItem = propertiesMenuItem.getWidget();
        quitItem = quitMenuItem.getWidget();
        aboutItem = aboutMenuItem.getWidget();

        // Check for progress and update the progress bar every half second.
        Timer timer = new Timer(1000 / UPDATE_TIMER, this);
        timer.start();
    }

    // See lifeCycleEvent() and fire().
    private boolean shutdown_now = false;

    /**
     * Handles Menu events (quit, about, ...).
     */
    public void menuItemEvent (MenuItemEvent event)
    {
        Object source = event.getSource();
        if (source.equals(propertiesItem)) {
            propertiesWindow.show();
        } else if (source.equals(quitItem)) {
            quit();
        } else if (source.equals(aboutItem)) {
            about();
        } else {
            log.log(Level.WARNING, "Unknown event: " + event +
                " from source: " + source);
        }
    }

    /**
     * Called when the application should quit.
     */
    private void quit ()
    {
        activity = SHUTDOWN;
        if (snarkhook != null) {
            Runtime.getRuntime().removeShutdownHook(snarkhook);
            snarkhook.start();
        } else {
            shutdown();
        }
    }

    // Called by the shutdown hook
    public void shutdown ()
    {
        Gtk.mainQuit();
        System.exit(0);
    }

    /**
     * Called when we have to show info about the program.
     */
    private void about ()
    {
        String[] authors = { AUTHOR1, AUTHOR2 };
        AboutDialog about = new AboutDialog();
        about.setName("The Hunting of the Snark Project");
        about.setVersion(VERSION);
        about.setCopyright(COPYING);
        about.setComments(COMMENT);
        about.setAuthors(authors);
        about.show();
    }

    // Creates the actual Snark object. Runs in the background
    public void run ()
    {
        _snark = SnarkApplication.parseArguments(args, this, this);

        snarkhook = new SnarkShutdown(_snark.storage, _snark.coordinator,
            _snark.acceptor, _snark.trackerclient, this);
        Runtime.getRuntime().addShutdownHook(snarkhook);
    }

    /**
     * Starts snark with a Gnome UI.
     */
    public static void main (String[] args)
    {
        // Initialize Gnome libraries and handle common arguments.
        Program.initGnomeUI("Snark", VERSION, args);

        // Setup the main application window
        SnarkGnome snarkgnome = new SnarkGnome();
        snarkgnome.args = args;

        // Initialize the Snark in a separate thread
        Thread thread = new Thread(snarkgnome);
        thread.start();

        // Go! Handle events...
        Gtk.main();
    }

    // True when progress() is called, cleared when madeProgress() is called.
    // Change/Check this flag only while holding the lock on this.
    private boolean progress = false;

    private synchronized void progress ()
    {
        progress = true;
    }

    public void peerChange (PeerCoordinator coordinator, Peer peer)
    {
        activity = getActivity();
        progress();
    }

    public void storageCreateFile (Storage storage, String name, long length)
    {
        // We should display something about this...
        activity = ALLOCATING;
    }

    // How much storage space has been allocated
    private long allocated = 0;

    public void storageAllocated (Storage storage, long length)
    {
        activity = ALLOCATING;
        allocated += length;
        if (allocated == _snark.meta.getTotalLength()) {
            ; // We are done, but we done't care
        }
        progress();
    }

    // How many pieces have been checked.
    private int checked = 0;

    boolean prechecking = true;

    public void storageChecked (Storage storage, int num, boolean checked)
    {
        if (prechecking) {
            activity = CHECKING;
        } else {
            // Should we display something about BAD pieces?
            activity = getActivity();
        }
        this.checked++;
        progress();
    }

    public void storageAllChecked (Storage storage)
    {
        prechecking = false;
        activity = getActivity();
    }

    /**
     * Returns the current activity by checking the storage and peer
     * coordinator.
     */
    private String getActivity ()
    {
        String activity;

        // No turning back from a shutdown
        if (this.activity == SHUTDOWN) {
            return SHUTDOWN;
        }

        if (_snark.coordinator != null && _snark.coordinator.peers != null) {
            synchronized (_snark.coordinator.peers) {
                if (_snark.coordinator.peers.size() > 0
                    && (_snark.coordinator.getDownloaded() > 0 || _snark.coordinator.getUploaded() > 0)) {
                    if (_snark.storage != null && _snark.storage.complete()) {
                        activity = SHARING;
                    } else {
                        activity = COLLECTING;
                    }
                } else {
                    activity = CONNECTING;
                }
            }
        } else {
            activity = CONNECTING;
        }

        return activity;
    }

    /**
     * Called by fire() to check if there was any progress recently. Returns
     * true when progress() was called since the last call to madeProgress().
     * Synchronized to make sure we don't miss any progress events.
     */
    private synchronized boolean madeProgress ()
    {
        boolean result = progress;
        progress = false;
        return result;
    }

    // Used for keeping track of the up and download rate
    // Updated every second in fire().
    private long lastDownloaded = 0;

    private long downb = 0;

    private long lastUploaded = 0;

    private long upb = 0;

    private static final String STARTUP = "Starting up";

    private static final String ALLOCATING = "Creating files";

    private static final String CHECKING = "Checking files";

    private static final String CONNECTING = "Connecting to peers";

    private static final String COLLECTING = "Collecting pieces";

    private static final String SHARING = "Sharing pieces";

    private static final String SHUTDOWN = "Shutting down";

    private String activity = STARTUP;

    private String lastActivity = null;

    /**
     * Sets upload and download rates texts. Used for everything gtk+/gnome
     * since that seems the most thread save way.
     */
    public boolean fire ()
    {
        // XXX - Little bit of a hack, when the close box has been pressed
        // anything to do with the main window seems to crash...
        if (shutdown_now) {
            return false;
        }

        // What are we doing?
        if (activity != lastActivity) {
            // Update status text and reset progress bar
            appbar.setStatusDefault(activity);
            ProgressBar progressBar = appbar.getProgressBar();
            progressBar.setFraction(0);
            progressBar.setText("");
            lastActivity = activity;
            progress();
        }

        // Do we know the torrent name we are sharing now?
        if (_snark.meta != null && name == null) {
            name = _snark.meta.getName();
            torrentName.setText(name);
        }

        // Calculate and update download and upload speeds
        if (_snark.coordinator != null
            && (activity == COLLECTING || activity == SHARING)) {
            // Calculate and show download rate.
            long downloaded = _snark.coordinator.getDownloaded();
            long diff = downloaded - lastDownloaded;
            downb -= downb / 10;
            downb += diff / 10;
            long kb = downb / (1024 / UPDATE_TIMER);
            String totalDown;
            if (downloaded >= (10 * 1024 * 1024)) {
                totalDown = (downloaded / (1024 * 1024)) + " MB";
            } else {
                totalDown = (downloaded / 1024) + " KB";
            }

            downloadRate.setText(kb + " KB/s (" + totalDown + ")");
            lastDownloaded = downloaded;

            // Calculate and show upload rate.
            long uploaded = _snark.coordinator.getUploaded();
            diff = uploaded - lastUploaded;
            upb -= upb / 10;
            upb += diff / 10;
            kb = upb / (1024 / UPDATE_TIMER);
            String totalUp;
            if (uploaded >= (10 * 1024 * 1024)) {
                totalUp = (uploaded / (1024 * 1024)) + " MB";
            } else {
                totalUp = (uploaded / 1024) + " KB";
            }

            uploadRate.setText(kb + " KB/s (" + totalUp + ")");
            lastUploaded = uploaded;
        }

        // Did we make progress?
        boolean update = madeProgress();
        if (_snark.meta != null && _snark.storage != null && update) {
            // Pieces and percentagees
            int pieces = _snark.meta.getPieces();
            int needed = _snark.storage.needed();
            int got = pieces - needed;

            long percentage;
            if (activity == ALLOCATING) {
                percentage = (100 * allocated) / _snark.meta.getTotalLength();
            } else if (activity == CHECKING) {
                percentage = (100 * checked) / pieces;
            } else {
                percentage = (100 * got) / pieces;
            }

            // Update progress bar
            double progress = percentage / 100d;
            ProgressBar progressBar = appbar.getProgressBar();

            if (activity == ALLOCATING || activity == CHECKING
                || activity == COLLECTING) {
                progressBar.setFraction(progress);
                progressBar.setText(percentage + "%");
            } else {
                progressBar.pulse();
            }

            // Update collected pieces count
            piecesCollected.setText(got + " of " + pieces);

            if (_snark.coordinator != null) {
                propertiesWindow.update(_snark.coordinator.getPeers());
            }
        }

        // We want to run again and again and again...
        return true;
    }

    // documentation inherited from interface LifeCycleListener
    public void lifeCycleEvent (LifeCycleEvent event)
    {
    }

    // documentation inherited from interface LifeCycleListener
    public boolean lifeCycleQuery (LifeCycleEvent arg0)
    {
        shutdown_now = true;
        quit();
        return false;
    }

    protected Snark _snark;

    /** The Java logger used to process our log events. */
    protected static final Logger log = Logger.getLogger("org.klomp.snark.gtk");
}
