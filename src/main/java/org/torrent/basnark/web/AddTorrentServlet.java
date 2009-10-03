package org.torrent.basnark.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.DateTool;
import org.torrent.basnark.bencode.BEntry;
import org.torrent.basnark.storage.builder.TorrentInfoBuilder;
import org.torrent.basnark.storage.domain.TorrentInfo;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Date: 16.09.2009
 * Time: 21:44:10 (Moscow Standard Time)
 *
 * @author Vlad Vinichenko (akerigan@gmail.com)
 */
public class AddTorrentServlet extends HttpServlet {

    private VelocityEngine velocityEngine;
    private Log log = LogFactory.getLog(getClass());
    private File torrentsDirFile;

    public AddTorrentServlet(VelocityEngine velocityEngine, File torrentsDirFile) {
        this.velocityEngine = velocityEngine;
        this.torrentsDirFile = torrentsDirFile;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            VelocityContext context = new VelocityContext();

            File torrentFile = (File) request.getAttribute("torrent");
            if (torrentFile != null) {
                BEntry bEntry = BEntry.readEntry(new FileInputStream(torrentFile));
                TorrentInfo torrentInfo = TorrentInfoBuilder.build(bEntry);

                bEntry.serialize(new FileOutputStream(new File(torrentsDirFile, torrentInfo.getInfoHash() + ".torrent")));

                if (bEntry.getMap() != null) {
                    BEntry bEntry1 = bEntry.getMap().get("info").getMap().get("pieces");
                    if (bEntry1 != null && bEntry1.getByteArray() != null) {
                        bEntry1.setByteArray("...".getBytes());
                    }
                }

                context.put("torrentInfo", torrentInfo);
                context.put("entry", bEntry);
                context.put("dateTool", new DateTool());
            }
            Template template = velocityEngine.getTemplate("addtorrent.vtl");
            template.merge(context, response.getWriter());
        } catch (Exception e) {
            log.error("", e);
            throw new ServletException(e);
        }
    }
}
