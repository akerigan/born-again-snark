package org.torrent.basnark.bencode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Date: 22.09.2009
 * Time: 21:23:05 (Moscow Standard Time)
 *
 * @author Vlad Vinichenko (akerigan@gmail.com)
 */
@SuppressWarnings({"unchecked"})
public class BEntry {

    private Object content;

    public BEntry() {
    }

    public BEntry(BigDecimal number) {
        this.content = number;
    }

    public BEntry(byte[] byteArray) {
        this.content = byteArray;
    }

    public BigDecimal getNumber() {
        if (content instanceof BigDecimal) {
            return (BigDecimal) content;
        } else {
            return null;
        }
    }

    public void setNumber(BigDecimal number) {
        this.content = number;
    }

    public byte[] getByteArray() {
        if (content instanceof byte[]) {
            return (byte[]) content;
        } else {
            return null;
        }
    }

    public void setByteArray(byte[] byteArray) {
        this.content = byteArray;
    }

    public List<BEntry> getList() {
        if (content instanceof List) {
            return (List<BEntry>) content;
        } else {
            return null;
        }
    }

    public void addEntry(BEntry entry) {
        if (content == null) {
            content = new ArrayList<BEntry>();
        }
        ((List<BEntry>) content).add(entry);
    }

    public Map<String, BEntry> getMap() {
        if (content instanceof Map) {
            return (Map<String, BEntry>) content;
        } else {
            return null;
        }
    }

    public void putEntry(String key, BEntry value) {
        if (content == null) {
            content = new LinkedHashMap<String, BEntry>();
        }
        ((Map<String, BEntry>) content).put(key, value);
    }

    public static BEntry readEntry(InputStream in) throws IOException {
        return readEntry(new BEncodedStreamReader(in));
    }

    public static BEntry readEntry(BEncodedStreamReader reader) throws IOException {
        BEntry entry = new BEntry();
        if (reader.hasNext()) {
            switch (reader.next()) {
                case BEncodedStreamReader.DICTIONARY_START:
                    BEntry keyEntry;
                    do {
                        keyEntry = readEntry(reader);
                        if (keyEntry != null) {
                            entry.putEntry(new String(keyEntry.getByteArray()), readEntry(reader));
                        }
                    } while (keyEntry != null);
                    return entry;
                case BEncodedStreamReader.DICTIONARY_END:
                    return null;
                case BEncodedStreamReader.LIST_START:
                    BEntry newEntry;
                    do {
                        newEntry = readEntry(reader);
                        if (newEntry != null) {
                            entry.addEntry(newEntry);
                        }
                    } while (newEntry != null);
                    return entry;
                case BEncodedStreamReader.LIST_END:
                    return null;
                case BEncodedStreamReader.INTEGER:
                    entry.setNumber(reader.getNumber());
                    return entry;
                case BEncodedStreamReader.BYTE_ARRAY:
                    entry.setByteArray(reader.getByteArray());
                    return entry;
            }
        }
        return entry;
    }

    public void serialize(OutputStream outputStream) throws IOException {
        if (content != null) {
            if (content instanceof BigDecimal) {
                outputStream.write('i');
                serializeNumber((BigDecimal) content, outputStream);
                outputStream.write('e');
            } else if (content instanceof byte[]) {
                serializeByteArray((byte[]) content, outputStream);
            } else if (content instanceof List) {
                outputStream.write('l');
                for (BEntry bEntry : (List<BEntry>) content) {
                    bEntry.serialize(outputStream);
                }
                outputStream.write('e');
            } else if (content instanceof Map) {
                outputStream.write('d');
                for (Map.Entry<String, BEntry> entry : ((Map<String, BEntry>) content).entrySet()) {
                    serializeByteArray(entry.getKey().getBytes(), outputStream);
                    entry.getValue().serialize(outputStream);
                }
                outputStream.write('e');
            }
        }
    }

    private void serializeByteArray(byte[] bytes, OutputStream outputStream) throws IOException {
        if (bytes != null) {
            serializeNumber(new BigDecimal(bytes.length), outputStream);
            outputStream.write(':');
            outputStream.write(bytes);
        }
    }

    private void serializeNumber(BigDecimal number, OutputStream outputStream) throws IOException {
        if (number != null) {
            String value = number.toString();
            for (int i = 0; i < value.length(); ++i) {
                outputStream.write(value.charAt(i));
            }
        }
    }

    @Override
    public String toString() {
        if (content != null) {
            if (content instanceof BigDecimal
                    || content instanceof List
                    || content instanceof Map) {
                return content.toString();
            } else if (content instanceof byte[]) {
                return new String((byte[]) content);
            }
        }
        return "";
    }
}
