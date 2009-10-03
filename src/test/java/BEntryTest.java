import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.torrent.basnark.bencode.BEntry;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

/**
 * Date: 22.09.2009
 * Time: 22:17:48 (Moscow Standard Time)
 *
 * @author Vlad Vinichenko (akerigan@gmail.com)
 */
public class BEntryTest extends TestCase {

    Log log = LogFactory.getLog(getClass());

    public InputStream getInputStream(String value) {
        return new ByteArrayInputStream(value.getBytes());
    }

    public String serialize(BEntry bEntry) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bEntry.serialize(baos);
        return new String(baos.toByteArray());
    }

    public void testInt() throws IOException {
//        log.info("intTest");
        String encoded = "i875e";
        BEntry entry = BEntry.readEntry(getInputStream(encoded));
        log.info(entry);
        assertEquals(new BigDecimal(875), entry.getNumber());
        assertEquals(encoded, serialize(entry));
    }

    public void testString() throws IOException {
//        log.info("stringTest");
        String encoded = "8:encoding";
        BEntry entry = BEntry.readEntry(getInputStream(encoded));
        log.info(entry);
        assertEquals("encoding", new String(entry.getByteArray()));
        assertEquals(encoded, serialize(entry));
    }

    public void testList() throws IOException {
//        log.info("listTest");
        String encoded = "li54e4:testi55ee";
        BEntry entry = BEntry.readEntry(getInputStream(encoded));
        log.info(entry);
        assertEquals(new BigDecimal(54), entry.getList().get(0).getNumber());
        assertEquals("test", new String(entry.getList().get(1).getByteArray()));
        assertEquals(new BigDecimal(55), entry.getList().get(2).getNumber());
        assertEquals(encoded, serialize(entry));
    }

    public void testDictionary() throws IOException {
//        log.info("dictTest");
        String encoded = "d4:name3:bob3:agei34ee";
        BEntry entry = BEntry.readEntry(getInputStream(encoded));
        log.info(entry);
        assertEquals("bob", new String(entry.getMap().get("name").getByteArray()));
        assertEquals(new BigDecimal(34), entry.getMap().get("age").getNumber());
        assertEquals(encoded, serialize(entry));
    }

    public void testBiggerDictionary() throws IOException {
//        log.info("dictTest");
        String encoded = "d4:Name5:James3:Agei22e9:Relativesl3:Bob5:James5:Jennye7:Addressd6:Street5:Smith6:Suburb7:Preston8:Postcodei5425eee";
        BEntry entry = BEntry.readEntry(getInputStream(encoded));
        log.info(entry);

        assertEquals("James", new String(entry.getMap().get("Name").getByteArray()));
        assertEquals(new BigDecimal(22), entry.getMap().get("Age").getNumber());

        List<BEntry> relatives = entry.getMap().get("Relatives").getList();
        assertEquals("Bob", new String(relatives.get(0).getByteArray()));
        assertEquals("James", new String(relatives.get(1).getByteArray()));
        assertEquals("Jenny", new String(relatives.get(2).getByteArray()));

        BEntry address = entry.getMap().get("Address");
        assertEquals("Smith", new String(address.getMap().get("Street").getByteArray()));
        assertEquals("Preston", new String(address.getMap().get("Suburb").getByteArray()));
        assertEquals(new BigDecimal(5425), address.getMap().get("Postcode").getNumber());
        assertEquals(encoded, serialize(entry));
    }

}
