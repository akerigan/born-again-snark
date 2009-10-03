package org.torrent.basnark.storage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.torrent.basnark.storage.domain.TorrentInfo;
import org.torrent.basnark.storage.domain.FileInfo;

import java.util.List;
import java.io.Serializable;

/**
 * Date: 26.09.2009
 * Time: 13:44:52 (Moscow Standard Time)
 *
 * @author Vlad Vinichenko (akerigan@gmail.com)
 */
public class StorageDao {

    private SessionFactory sessionFactory;
    private Log log = LogFactory.getLog(getClass());

    public StorageDao() {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            log.error("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public void createTorrent(TorrentInfo torrentInfo) {
        Session session = sessionFactory.openSession();
        session.getTransaction().begin();
        session.save(torrentInfo);
        session.getTransaction().commit();
    }

    public List findAllTorrents() {
        Session session = sessionFactory.openSession();
        List<TorrentInfo> list = session.createQuery("From Torrent").list();
        return list;
    }

    public void createFile(FileInfo file) {
        Session session = sessionFactory.openSession();
        session.getTransaction().begin();
        session.save(file);
        session.getTransaction().commit();
    }

    public List<FileInfo> findAllFiles() {
        Session session = sessionFactory.openSession();
        List<FileInfo> list = session.createQuery("From FileInfo").list();
        return list;
    }

    public void deleteFile(FileInfo fileInfo) {
        Session session = sessionFactory.openSession();
        session.getTransaction().begin();
        session.delete(fileInfo);
        session.getTransaction().commit();
    }

    public void updateFile(FileInfo fileInfo) {
        Session session = sessionFactory.openSession();
        session.getTransaction().begin();
        session.update(fileInfo);
        session.getTransaction().commit();
    }
}
