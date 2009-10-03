package org.torrent.basnark.storage.domain;

/**
 * Date: 26.09.2009
 * Time: 20:36:35 (Moscow Standard Time)
 *
 * @author Vlad Vinichenko (akerigan@gmail.com)
 */
public class FileInfo {

    private int id;

    private String baseDirectory;
    private String directory;
    private String fileName;

    private long size;
    private String md5Hash;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBaseDirectory() {
        return baseDirectory;
    }

    public void setBaseDirectory(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getMd5Hash() {
        return md5Hash;
    }

    public void setMd5Hash(String md5Hash) {
        this.md5Hash = md5Hash;
    }

    public String getPath() {
        StringBuilder sb = new StringBuilder();
        sb.append(baseDirectory);
        if (directory != null) {
            sb.append("/").append(directory);
        }
        sb.append("/").append(fileName);
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileInfo fileInfo = (FileInfo) o;

        if (size != fileInfo.size) return false;
        if (!baseDirectory.equals(fileInfo.baseDirectory)) return false;
        if (directory != null ? !directory.equals(fileInfo.directory) : fileInfo.directory != null) return false;
        if (!fileName.equals(fileInfo.fileName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = baseDirectory.hashCode();
        result = 31 * result + (directory != null ? directory.hashCode() : 0);
        result = 31 * result + fileName.hashCode();
        result = 31 * result + (int) (size ^ (size >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "id=" + id +
                ", baseDirectory='" + baseDirectory + '\'' +
                ", directory='" + directory + '\'' +
                ", fileName='" + fileName + '\'' +
                ", size=" + size +
                ", md5Hash='" + md5Hash + '\'' +
                '}';
    }
}
