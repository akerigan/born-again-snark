package org.torrent.basnark.storage.builder;

import org.torrent.basnark.bencode.BEntry;
import org.torrent.basnark.storage.domain.FileInfo;
import org.torrent.basnark.storage.domain.TorrentInfo;
import org.torrent.basnark.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Date: 03.10.2009
 * Time: 11:54:58 (Moscow Standard Time)
 *
 * @author Vlad Vinichenko (akerigan@gmail.com)
 */
public class TorrentInfoBuilder {

    public static TorrentInfo build(BEntry torrentEntries) throws IOException, NoSuchAlgorithmException {

        TorrentInfo torrentInfo = new TorrentInfo();

        if (torrentEntries != null && torrentEntries.getMap() != null) {
            BEntry bEntry1 = torrentEntries.getMap().get("encoding");
/*
            String encoding;
            if (bEntry1 != null && bEntry1.getByteArray() != null) {
                encoding = new String(bEntry1.getByteArray());
            } else {
                encoding = "UTF-8";
            }
*/
//            String encoding = "windows-1251";
            String encoding = "koi8-r";

            BEntry infoEntry = torrentEntries.getMap().get("info");
            if (infoEntry != null && infoEntry.getMap() != null) {
                MessageDigest md = MessageDigest.getInstance("SHA1");
                md.reset();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                infoEntry.serialize(baos);
                md.update(baos.toByteArray());

                torrentInfo.setInfoHash(StringUtils.toHexString(md.digest()));

                String name = null;
                bEntry1 = infoEntry.getMap().get("name.utf-8");
                if (bEntry1 != null && bEntry1.getByteArray() != null) {
                    name = new String(bEntry1.getByteArray(), "UTF-8");
                }
                if (name == null) {
                    bEntry1 = infoEntry.getMap().get("name");
                    if (bEntry1 != null && bEntry1.getByteArray() != null) {
                        name = new String(bEntry1.getByteArray(), encoding);
                    }
                }

                List<FileInfo> fileInfos = new ArrayList<FileInfo>();
                List<String> pathList = new ArrayList<String>();
                bEntry1 = infoEntry.getMap().get("files");
                if (bEntry1 != null && bEntry1.getList() != null) {
                    for (BEntry bEntry2 : bEntry1.getList()) {
                        if (bEntry2.getMap() != null) {
                            FileInfo fileInfo = new FileInfo();
                            fileInfos.add(fileInfo);
                            BEntry bEntry3 = bEntry2.getMap().get("length");
                            if (bEntry3 != null && bEntry3.getNumber() != null) {
                                fileInfo.setSize(bEntry3.getNumber().longValue());
                            }
                            bEntry3 = bEntry2.getMap().get("path");
                            if (bEntry3 != null && bEntry3.getList() != null) {
                                pathList.clear();
                                if (name != null) {
                                    pathList.add(name);
                                }
                                int partsCount = bEntry3.getList().size();
                                int partIndex = 1;
                                for (BEntry bEntry4 : bEntry3.getList()) {
                                    if (bEntry4.getByteArray() != null) {
                                        String value = new String(bEntry4.getByteArray(), encoding);
                                        if (partIndex < partsCount) {
                                            pathList.add(value);
                                        } else {
                                            fileInfo.setFileName(value);
                                        }
                                    }
                                    ++partIndex;
                                }
                                fileInfo.setDirectory(StringUtils.join(pathList, "/"));
                            }
                            bEntry3 = bEntry2.getMap().get("md5");
                            if (bEntry3 != null && bEntry3.getByteArray() != null) {
                                fileInfo.setMd5Hash(StringUtils.toHexString(bEntry3.getByteArray()));
                            }
                        }
                    }
                } else {
                    FileInfo fileInfo = new FileInfo();
                    fileInfos.add(fileInfo);

                    fileInfo.setFileName(name);
                    bEntry1 = infoEntry.getMap().get("length");
                    if (bEntry1 != null && bEntry1.getNumber() != null) {
                        fileInfo.setSize(bEntry1.getNumber().longValue());
                    }
                }
                torrentInfo.setFiles(fileInfos);

                bEntry1 = infoEntry.getMap().get("piece length");
                if (bEntry1 != null && bEntry1.getNumber() != null) {
                    torrentInfo.setPieceLength(bEntry1.getNumber().intValue());
                }

                bEntry1 = infoEntry.getMap().get("pieces");
                List<String> piecesHashes = new ArrayList<String>();
                if (bEntry1 != null && bEntry1.getByteArray() != null) {
                    byte[] allPiecesHashes = bEntry1.getByteArray();
                    int piecesCount = allPiecesHashes.length / 20;
                    for (int i = 0; i < piecesCount; ++i) {
                        piecesHashes.add(StringUtils.toHexString(allPiecesHashes, i * 20, 20));
                    }
                }
                torrentInfo.setPiecesHashes(piecesHashes);

                bEntry1 = infoEntry.getMap().get("private");
                if (bEntry1 != null && bEntry1.getNumber() != null) {
                    torrentInfo.setObeyPrivacy(bEntry1.getNumber().intValue() > 0);
                }

                bEntry1 = infoEntry.getMap().get("source");
                if (bEntry1 != null && bEntry1.getByteArray() != null) {
                    torrentInfo.setSource(new String(bEntry1.getByteArray(), encoding));
                }
            }

            Set<String> announceSet = new LinkedHashSet<String>();

            bEntry1 = torrentEntries.getMap().get("announce");
            if (bEntry1 != null && bEntry1.getByteArray() != null) {
                announceSet.add(new String(bEntry1.getByteArray(), encoding));
            }

            bEntry1 = torrentEntries.getMap().get("announce-list");
            if (bEntry1 != null && bEntry1.getList() != null) {
                for (BEntry bEntry2 : bEntry1.getList()) {
                    if (bEntry2.getList() != null) {
                        for (BEntry bEntry3 : bEntry2.getList()) {
                            if (bEntry3.getByteArray() != null) {
                                announceSet.add(new String(bEntry3.getByteArray(), encoding));
                            }
                        }
                    }
                }
            }
            torrentInfo.setAnnounces(announceSet);

            Map<String, String> properties = new HashMap<String, String>();
            bEntry1 = torrentEntries.getMap().get("azureus_properties");
            if (bEntry1 != null && bEntry1.getMap() != null) {
                for (String key : bEntry1.getMap().keySet()) {
                    BEntry bEntry2 = bEntry1.getMap().get(key);
                    if (bEntry2 != null && bEntry2.getByteArray() != null) {
                        properties.put(key, new String(bEntry2.getByteArray(), encoding));
                    }
                }
            }
            if (properties.size() > 0) {
                torrentInfo.setProperties(properties);
            }

            bEntry1 = torrentEntries.getMap().get("comment");
            if (bEntry1 != null && bEntry1.getByteArray() != null) {
                torrentInfo.setComment(new String(bEntry1.getByteArray(), encoding));
            }

            bEntry1 = torrentEntries.getMap().get("created by");
            if (bEntry1 != null && bEntry1.getByteArray() != null) {
                torrentInfo.setCreatedBy(new String(bEntry1.getByteArray(), encoding));
            }

            bEntry1 = torrentEntries.getMap().get("creation date");
            if (bEntry1 != null && bEntry1.getNumber() != null) {
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+00:00"));
                calendar.setTimeInMillis(bEntry1.getNumber().longValue() * 1000);
                torrentInfo.setCreationDate(calendar.getTime());
            }

            bEntry1 = torrentEntries.getMap().get("private");
            if (bEntry1 != null && bEntry1.getNumber() != null) {
                torrentInfo.setObeyPrivacy(bEntry1.getNumber().intValue() > 0);
            }

            bEntry1 = torrentEntries.getMap().get("publisher.utf-8");
            if (bEntry1 != null && bEntry1.getByteArray() != null) {
                torrentInfo.setPublisher(new String(bEntry1.getByteArray(), "UTF-8"));
            } else {
                bEntry1 = torrentEntries.getMap().get("publisher");
                if (bEntry1 != null && bEntry1.getByteArray() != null) {
                    torrentInfo.setPublisher(new String(bEntry1.getByteArray(), encoding));
                }
            }

            bEntry1 = torrentEntries.getMap().get("publisher-url.utf-8");
            if (bEntry1 != null && bEntry1.getByteArray() != null) {
                torrentInfo.setPublisherUrl(new String(bEntry1.getByteArray(), "UTF-8"));
            } else {
                bEntry1 = torrentEntries.getMap().get("publisher-url");
                if (bEntry1 != null && bEntry1.getByteArray() != null) {
                    torrentInfo.setPublisherUrl(new String(bEntry1.getByteArray(), encoding));
                }
            }

        }
        return torrentInfo;
    }

}
