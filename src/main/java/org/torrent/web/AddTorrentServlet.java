package org.torrent.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.torrent.snark.MetaInfo;
import org.torrent.snark.rework.BEntry;
import org.torrent.snark.bencode.BDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
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

    public AddTorrentServlet(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
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
                MetaInfo metaInfo = new MetaInfo(new BDecoder(new FileInputStream(torrentFile)));
                context.put("metaInfo", metaInfo);
                context.put("entry", BEntry.readEntry(new FileInputStream(torrentFile)));                
            }
            Template template = velocityEngine.getTemplate("addtorrent.vtl");
            template.merge(context, response.getWriter());
        } catch (Exception e) {
            log.error("", e);
            throw new ServletException(e);
        }
    }
}
