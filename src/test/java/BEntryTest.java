import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.torrent.snark.rework.BEntry;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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

    public void testInt() throws IOException {
//        log.info("intTest");
        BEntry entry = BEntry.readEntry(getInputStream("i875e"));
        log.info(entry);
        assertEquals(new BigDecimal(875), entry.getNumber());
    }

    public void testString() throws IOException {
//        log.info("stringTest");
        BEntry entry = BEntry.readEntry(getInputStream("8:encoding"));
        log.info(entry);
        assertEquals("encoding", new String(entry.getByteArray()));
    }

    public void testList() throws IOException {
//        log.info("listTest");
        BEntry entry = BEntry.readEntry(getInputStream("li54e4:testi55ee"));
        log.info(entry);
        assertEquals(new BigDecimal(54), entry.getList().get(0).getNumber());
        assertEquals("test", new String(entry.getList().get(1).getByteArray()));
        assertEquals(new BigDecimal(55), entry.getList().get(2).getNumber());
    }

    public void testDictionary() throws IOException {
//        log.info("dictTest");
        BEntry entry = BEntry.readEntry(getInputStream("d4:name3:bob3:agei34ee"));
        log.info(entry);
        assertEquals("bob", new String(entry.getMap().get("name").getByteArray()));
        assertEquals(new BigDecimal(34), entry.getMap().get("age").getNumber());
    }

    public void testBiggerDictionary() throws IOException {
//        log.info("dictTest");
        BEntry entry = BEntry.readEntry(getInputStream("d4:Name5:James3:Agei22e9:Relativesl3:Bob5:James5:Jennye7:Addressd6:Street5:Smith6:Suburb7:Preston8:Postcodei5425eee"));
        log.info(entry);

        assertEquals("James", new String(entry.getMap().get("Name").getByteArray()));
        assertEquals(new BigDecimal(22), entry.getMap().get("Age").getNumber());

        List<BEntry> relatives = entry.getMap().get("Relatives").getList();
        assertEquals("Bob", new String(relatives.get(0).getByteArray()));
        assertEquals("James", new String(relatives.get(1).getByteArray()));
        assertEquals("Jenny", new String(relatives.get(2).getByteArray()));

        Map<String,BEntry> address = entry.getMap().get("Address").getMap();
        assertEquals("Smith", new String(address.get("Street").getByteArray()));
        assertEquals("Preston", new String(address.get("Suburb").getByteArray()));
        assertEquals(new BigDecimal(5425), address.get("Postcode").getNumber());
    }

}
