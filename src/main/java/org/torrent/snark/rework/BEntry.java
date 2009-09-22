package org.torrent.snark.rework;

import java.io.IOException;
import java.io.InputStream;
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
public class BEntry {

    private BigDecimal number;
    private byte[] byteArray;
    private List<BEntry> list;
    private Map<String, BEntry> map;

    public BEntry() {
    }

    public BEntry(BigDecimal number) {
        this.number = number;
    }

    public BEntry(byte[] byteArray) {
        this.byteArray = byteArray;
    }

    public BigDecimal getNumber() {
        return number;
    }

    public void setNumber(BigDecimal number) {
        this.number = number;
    }

    public byte[] getByteArray() {
        return byteArray;
    }

    public void setByteArray(byte[] byteArray) {
        this.byteArray = byteArray;
    }

    public List<BEntry> getList() {
        return list;
    }

    public void addEntry(BEntry entry) {
        if (list == null) {
            list = new ArrayList<BEntry>();
        }
        list.add(entry);
    }

    public Map<String, BEntry> getMap() {
        return map;
    }

    public void putEntry(String key, BEntry value) {
        if (map == null) {
            map = new LinkedHashMap<String, BEntry>();
        }
        map.put(key, value);
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

    @Override
    public String toString() {
        if (number != null) {
            return number.toString();
        } else if (byteArray != null) {
            return "\"" + new String(byteArray) + "\"";
        } else if (list != null) {
            return list.toString();
        } else if (map != null) {
            return map.toString();
        } else {
            return "";
        }
    }
}
