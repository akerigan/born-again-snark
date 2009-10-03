package org.torrent.basnark.storage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;
import org.torrent.basnark.storage.domain.FileInfo;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

/**
 * Date: 26.09.2009
 * Time: 21:01:48 (Moscow Standard Time)
 *
 * @author Vlad Vinichenko (akerigan@gmail.com)
 */
public class FileReader {

    private static Log log = LogFactory.getLog(FileReader.class);

    public static void main(String[] args) {

        PropertyConfigurator.configure("./app/conf/log4j.properties");

        try {

            Properties properties = new Properties();
            properties.load(new FileInputStream("app/conf/born-again-snark.properties"));

            String contentDirs = properties.getProperty("storage.content.dirs");
            if (contentDirs == null) {
                throw new IllegalStateException("'storage.content.dirs' not set");
            }

            StorageDao storageDao = new StorageDao();

            Map<String, FileInfo> storedFiles = new HashMap<String, FileInfo>();
            for (FileInfo fileInfo : storageDao.findAllFiles()) {
                storedFiles.put(fileInfo.getPath(), fileInfo);
            }

            Map<String, FileInfo> scannedFiles = new HashMap<String, FileInfo>();
            for (String baseDirectory : contentDirs.trim().split("\\s*:\\s*")) {
                List<File> files = new ArrayList<File>();
                walkDir(new File(baseDirectory), files);
                for (File file : files) {
                    FileInfo fileInfo = new FileInfo();
                    fileInfo.setBaseDirectory(baseDirectory);
                    String directory = file.getParentFile().getCanonicalPath();
                    if (!baseDirectory.equals(directory)) {
                        fileInfo.setDirectory(directory.substring(baseDirectory.length() + 1, directory.length()));
                    }
                    fileInfo.setFileName(file.getName());
                    fileInfo.setSize(file.length());
//                    fileInfo.setMd5Hash(hexencode(DigestUtils.md5(new FileInputStream(file))));

                    scannedFiles.put(fileInfo.getPath(), fileInfo);
                }
            }

            Set<String> keys = new HashSet<String>(storedFiles.keySet());
            keys.removeAll(scannedFiles.keySet());
            for (String key : keys) {
                FileInfo fileInfo = storedFiles.get(key);
                log.info("remove entry for: " + key);
                storageDao.deleteFile(fileInfo);
            }

            keys.clear();
            keys.addAll(scannedFiles.keySet());
            keys.removeAll(storedFiles.keySet());
            for (String key : keys) {
                FileInfo fileInfo = scannedFiles.get(key);
                log.info("create entry for: " + fileInfo);
                storageDao.createFile(fileInfo);
            }

            keys.clear();
            keys.addAll(scannedFiles.keySet());
            keys.retainAll(storedFiles.keySet());
            for (String key : keys) {
                FileInfo fileInfoOld = storedFiles.get(key);
                FileInfo fileInfoNew = scannedFiles.get(key);
                if (!fileInfoNew.equals(fileInfoOld)) {
                    log.info("update entry for: " + key);
                    fileInfoNew.setId(fileInfoOld.getId());
                    storageDao.updateFile(fileInfoNew);
                }
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }

    private static void walkDir(File dir, List<File> result) {
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    walkDir(file, result);
                } else {
                    result.add(file);
                }
            }
        }
    }

}
