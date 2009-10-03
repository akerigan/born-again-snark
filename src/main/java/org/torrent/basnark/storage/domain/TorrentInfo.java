package org.torrent.basnark.storage.domain;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Date: 26.09.2009
 * Time: 14:52:08 (Moscow Standard Time)
 *
 * @author Vlad Vinichenko (akerigan@gmail.com)
 */
public class TorrentInfo {

    private Collection<String> announces;
    private Map<String, String> properties;
    private String comment;
    private String createdBy;
    private Date creationDate;
    private boolean obeyPrivacy;
    private String publisher;
    private String publisherUrl;
    private String infoHash;
    private Collection<FileInfo> files;
    private int pieceLength;
    private Collection<String> piecesHashes;
    private String source;

    public Collection<String> getAnnounces() {
        return announces;
    }

    public void setAnnounces(Collection<String> announces) {
        this.announces = announces;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isObeyPrivacy() {
        return obeyPrivacy;
    }

    public void setObeyPrivacy(boolean obeyPrivacy) {
        this.obeyPrivacy = obeyPrivacy;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublisherUrl() {
        return publisherUrl;
    }

    public void setPublisherUrl(String publisherUrl) {
        this.publisherUrl = publisherUrl;
    }

    public String getInfoHash() {
        return infoHash;
    }

    public void setInfoHash(String infoHash) {
        this.infoHash = infoHash;
    }

    public int getPieceLength() {
        return pieceLength;
    }

    public void setPieceLength(int pieceLength) {
        this.pieceLength = pieceLength;
    }

    public Collection<FileInfo> getFiles() {
        return files;
    }

    public void setFiles(Collection<FileInfo> files) {
        this.files = files;
    }

    public Collection<String> getPiecesHashes() {
        return piecesHashes;
    }

    public void setPiecesHashes(List<String> piecesHashes) {
        this.piecesHashes = piecesHashes;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
