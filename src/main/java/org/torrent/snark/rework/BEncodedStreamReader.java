package org.torrent.snark.rework;

import java.io.*;
import java.math.BigDecimal;
import java.util.Stack;

/**
 * Date: 21.09.2009
 * Time: 21:08:46 (Moscow Standard Time)
 *
 * @author Vlad Vinichenko (akerigan@gmail.com)
 */
public class BEncodedStreamReader {

    public static final int DICTIONARY_START = 0;
    public static final int DICTIONARY_END = 1;

    public static final int LIST_START = 10;
    public static final int LIST_END = 11;

    public static final int INTEGER = 20;
    public static final int BYTE_ARRAY = 30;

    private InputStream in;
    private int current = -1;

    private Stack<Integer> events = new Stack<Integer>();
    private ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

    private BigDecimal parsedNumber;
    private byte[] parsedByteArray;

    public BEncodedStreamReader(InputStream in) {
        if (in == null) {
            throw new IllegalStateException("Input stream is null");
        }
        this.in = in;
    }

    public BigDecimal getNumber() {
        return parsedNumber;
    }

    public byte[] getByteArray() {
        return parsedByteArray;
    }

    public boolean hasNext() throws IOException {
        return current != -1 || (current = in.read()) != -1;
    }

    public int next() throws IOException {
        if (current == -1) {
            current = in.read();
        }
        if (current == -1) {
            if (events.size() > 0) {
                throw new EOFException("Unexpected end of stream");
            }
        }
        if (current == 'd') {
            current = -1;
            events.push(DICTIONARY_START);
            return DICTIONARY_START;
        } else if (current == 'l') {
            current = -1;
            events.push(LIST_START);
            return LIST_START;
        } else if (current == 'e') {
            current = -1;
            if (events.size() == 0) {
                throw new EOFException("Unexpected end of data");
            }
            int event = events.pop();
            switch (event) {
                case DICTIONARY_START:
                    return DICTIONARY_END;
                case LIST_START:
                    return LIST_END;
                default:
                    throw new EOFException("Unexpected end of data");
            }
        } else if (current == 'i') {
            current = -1;
            parsedNumber = readNumber('e');
            return INTEGER;
        } else if (current >= '0' && current <= '9') {
            parsedByteArray = readBytesArray();
            return BYTE_ARRAY;
        } else {
            throw new InvalidObjectException("Unparsable token: " + current);
        }
    }

    private byte[] readBytesArray() throws IOException {
        BigDecimal count = readNumber(':');
        return readCount(count.intValue());
    }

    private BigDecimal readNumber(char end) throws IOException {
        BigDecimal result = new BigDecimal(new String(readUntil(end)));
        current = in.read();
        return result;
    }

    private byte[] readUntil(char end) throws IOException {
        byteBuffer.reset();
        if (current == -1) {
            current = in.read();
        }
        while (current != -1 && current != end) {
            byteBuffer.write(current);
            current = in.read();
        }
        if (current != end) {
            throw new EOFException("Unexpected end of stream");
        }
        return byteBuffer.toByteArray();
    }

    private byte[] readCount(int count) throws IOException {
        byteBuffer.reset();
        if (current == -1) {
            current = in.read();
        }
        int i;
        for (i = 0; current != -1 && i != count; ++i) {
            byteBuffer.write(current);
            current = in.read();
        }
        if (i != count) {
            throw new EOFException("Unexpected end of stream");
        }
        return byteBuffer.toByteArray();
    }

}
