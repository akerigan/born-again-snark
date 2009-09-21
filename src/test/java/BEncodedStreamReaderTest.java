import org.junit.Test;
import org.torrent.snark.rework.BEncodedStreamReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import junit.framework.TestCase;

/**
 * Date: 21.09.2009
 * Time: 23:16:06 (Moscow Standard Time)
 *
 * @author Vlad Vinichenko (akerigan@gmail.com)
 */
public class BEncodedStreamReaderTest extends TestCase {

    Log log = LogFactory.getLog(getClass());

    public BEncodedStreamReader getReader(String value) {
        return new BEncodedStreamReader(new ByteArrayInputStream(value.getBytes()));
    }

    public void testInt() throws IOException {
        log.info("intTest");
        BEncodedStreamReader reader = getReader("i875e");
        assertTrue(reader.hasNext());
        assertEquals(BEncodedStreamReader.INTEGER, reader.next());
        assertEquals(new Integer(875), reader.getInteger());
        assertFalse(reader.hasNext());
    }

    public void testString() throws IOException {
        log.info("stringTest");
        BEncodedStreamReader reader = getReader("8:encoding");
        assertTrue(reader.hasNext());
        assertEquals(BEncodedStreamReader.BYTE_ARRAY, reader.next());
        assertEquals("encoding", new String(reader.getByteArray()));
        assertFalse(reader.hasNext());
    }

    public void testList() throws IOException {
        log.info("listTest");
        BEncodedStreamReader reader = getReader("li54e4:testi55ee");
        assertTrue(reader.hasNext());
        assertEquals(BEncodedStreamReader.LIST_START, reader.next());
        assertEquals(BEncodedStreamReader.INTEGER, reader.next());
        assertEquals(new Integer(54), reader.getInteger());
        assertEquals(BEncodedStreamReader.BYTE_ARRAY, reader.next());
        assertEquals(new String(reader.getByteArray()), "test");
        assertEquals(BEncodedStreamReader.INTEGER, reader.next());
        assertEquals(new Integer(55), reader.getInteger());
        assertEquals(BEncodedStreamReader.LIST_END, reader.next());
        assertFalse(reader.hasNext());
    }

    public void testDictionary() throws IOException {
        log.info("dictTest");
        BEncodedStreamReader reader = getReader("d4:name3:bob3:agei34ee");
        assertTrue(reader.hasNext());
        assertEquals(BEncodedStreamReader.DICTIONARY_START, reader.next());
        assertEquals(BEncodedStreamReader.BYTE_ARRAY, reader.next());
        assertEquals(new String(reader.getByteArray()), "name");
        assertEquals(BEncodedStreamReader.BYTE_ARRAY, reader.next());
        assertEquals(new String(reader.getByteArray()), "bob");
        assertEquals(BEncodedStreamReader.BYTE_ARRAY, reader.next());
        assertEquals(new String(reader.getByteArray()), "age");
        assertEquals(BEncodedStreamReader.INTEGER, reader.next());
        assertEquals(new Integer(34), reader.getInteger());
        assertEquals(BEncodedStreamReader.DICTIONARY_END, reader.next());
        assertFalse(reader.hasNext());
    }

    public void testBiggerDictionary() throws IOException {
        log.info("dictTest");
        BEncodedStreamReader reader = getReader("d4:Name5:James3:Agei22e9:Relativesl3:Bob5:James5:Jennye7:Addressd6:Street5:Smith6:Suburb7:Preston8:Postcodei5425eee");
        assertTrue(reader.hasNext());
        assertEquals(BEncodedStreamReader.DICTIONARY_START, reader.next());

        assertEquals(BEncodedStreamReader.BYTE_ARRAY, reader.next());
        assertEquals(new String(reader.getByteArray()), "Name");

        assertEquals(BEncodedStreamReader.BYTE_ARRAY, reader.next());
        assertEquals(new String(reader.getByteArray()), "James");

        assertEquals(BEncodedStreamReader.BYTE_ARRAY, reader.next());
        assertEquals(new String(reader.getByteArray()), "Age");

        assertEquals(BEncodedStreamReader.INTEGER, reader.next());
        assertEquals(new Integer(22), reader.getInteger());

        assertEquals(BEncodedStreamReader.BYTE_ARRAY, reader.next());
        assertEquals(new String(reader.getByteArray()), "Relatives");

        assertEquals(BEncodedStreamReader.LIST_START, reader.next());

        assertEquals(BEncodedStreamReader.BYTE_ARRAY, reader.next());
        assertEquals(new String(reader.getByteArray()), "Bob");

        assertEquals(BEncodedStreamReader.BYTE_ARRAY, reader.next());
        assertEquals(new String(reader.getByteArray()), "James");

        assertEquals(BEncodedStreamReader.BYTE_ARRAY, reader.next());
        assertEquals(new String(reader.getByteArray()), "Jenny");        

        assertEquals(BEncodedStreamReader.LIST_END, reader.next());

        assertEquals(BEncodedStreamReader.BYTE_ARRAY, reader.next());
        assertEquals(new String(reader.getByteArray()), "Address");

        assertEquals(BEncodedStreamReader.DICTIONARY_START, reader.next());

        assertEquals(BEncodedStreamReader.BYTE_ARRAY, reader.next());
        assertEquals(new String(reader.getByteArray()), "Street");

        assertEquals(BEncodedStreamReader.BYTE_ARRAY, reader.next());
        assertEquals(new String(reader.getByteArray()), "Smith");

        assertEquals(BEncodedStreamReader.BYTE_ARRAY, reader.next());
        assertEquals(new String(reader.getByteArray()), "Suburb");

        assertEquals(BEncodedStreamReader.BYTE_ARRAY, reader.next());
        assertEquals(new String(reader.getByteArray()), "Preston");

        assertEquals(BEncodedStreamReader.BYTE_ARRAY, reader.next());
        assertEquals(new String(reader.getByteArray()), "Postcode");

        assertEquals(BEncodedStreamReader.INTEGER, reader.next());
        assertEquals(new Integer(5425), reader.getInteger());

        assertEquals(BEncodedStreamReader.DICTIONARY_END, reader.next());
        assertEquals(BEncodedStreamReader.DICTIONARY_END, reader.next());
        assertFalse(reader.hasNext());
    }

}
