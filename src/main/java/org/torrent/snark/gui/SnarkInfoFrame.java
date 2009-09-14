/*
 * SnarkInfoFrame - Show properties of the file being shared. Copyright (C)
 * 2003 Mark J. Wielaard
 * 
 * This file is part of snark.
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

package org.torrent.snark.gui;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.torrent.snark.Snark;

import javax.swing.*;

/**
 * Show a Gnome window with the properties of the file being shared.
 *
 * @author Mark Wielaard (mark@klomp.org)
 * @author Vlad Vinichenko (akerigan@gmail.com)
 */
public class SnarkInfoFrame extends EmptyFrame {

    private JLabel fileNameLabel;
    private JLabel torrentNameLabel;
    private JLabel trackerNameLabel;
    private JLabel piecesTotalLabel;
    private JLabel pieceSizeLabel;
    private JLabel totalSizeLabel;
    private JLabel peersTotalLabel;
    private JButton showPeersButton;

    private SnarkPeerListFrame peersWindow;

    private int peers;

    protected Snark snark;

    /**
     * The Java logger used to process our log events.
     */
    protected static final Log log = LogFactory.getLog(SnarkInfoFrame.class);

    public SnarkInfoFrame(String title, int width, int height, Snark snark) {
        super(title, width, height);
        this.snark = snark;
//        peersWindow = new SnarkPeerListFrame(snark);
    }

    @Override
    protected void initComponents() {
        fileNameLabel = new JLabel(snark.meta.getName());
        torrentNameLabel = new JLabel(snark.torrent);
        trackerNameLabel = new JLabel(snark.meta.getAnnounce());
        piecesTotalLabel = new JLabel(String.valueOf(snark.meta.getPieces()));
        pieceSizeLabel = new JLabel(snark.meta.getPieceLength(0) / 1024 + " KB");
        totalSizeLabel = new JLabel(snark.meta.getTotalLength() / (1024 * 1024) + " MB");
        peersTotalLabel = new JLabel();
        showPeersButton = new JButton("Peers...");
    }

    @Override
    protected JPanel getMainPanel() {
        FormLayout formLayout = new FormLayout("right:pref, 4dlu, 100dlu, 4dlu, pref", "");

        DefaultFormBuilder builder = new DefaultFormBuilder(formLayout);
        builder.setDefaultDialogBorder();

        builder.appendSeparator("Snark Properties");

        builder.append("Name:", fileNameLabel);
        builder.nextLine();

        builder.append("Torrent:", torrentNameLabel);
        builder.nextLine();

        builder.append("Tracker:", trackerNameLabel);
        builder.nextLine();

        builder.appendSeparator("Пароль");

        builder.append("Pieces:", piecesTotalLabel);
        builder.nextLine();

        builder.append("Piece size:", pieceSizeLabel);
        builder.nextLine();

        builder.append("Total size:", totalSizeLabel);
        builder.nextLine();

        builder.append("Peers:", peersTotalLabel, showPeersButton);

        return builder.getPanel();
    }


/*
    public void buttonEvent(ButtonEvent event) {
        if (event.isOfType(ButtonEvent.Type.CLICK)) {
            Object source = event.getSource();
            if (source.equals(peersButton)) {
                peersWindow.show();
            } else if (source.equals(closeButton)) {
                window.destroy();
                window = null;
            } else {
                log.log(Level.WARNING, "Unknown event: " + event +
                        " from source: " + source);
            }
        }
    }

    void show() {
        if (window != null) {
            window.present();
            return;
        }

        window = new Window(WindowType.TOPLEVEL);
        window.setTitle("Snark - Properties");
        window.addListener(this);

        Widget infoBox = create();

        window.add(infoBox);
        infoBox.showAll();
        window.showAll();
    }

    // Update the number of peers and the peers window.
    void update(int peers) {
        if (window != null && this.peers != peers) {
            this.peers = peers;
            peersLabel.setText(String.valueOf(peers));
        }
        peersWindow.update();
    }
*/

}
