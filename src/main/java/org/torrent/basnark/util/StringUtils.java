package org.torrent.basnark.util;

import java.util.Collection;
import java.util.Iterator;

/**
 * Date: 30.09.2009
 * Time: 18:53:09 (Moscow Standard Time)
 *
 * @author Vlad Vinichenko (akerigan@gmail.com)
 */
public class StringUtils {

    public static String toHexString(byte[] bytes) {
        if (bytes != null) {
            return toHexString(bytes, 0, bytes.length);
        } else {
            return null;
        }
    }

    public static String toHexString(byte[] bytes, int offset, int count) {
        if (bytes != null) {
            StringBuffer sb = new StringBuffer(count * 2);
            for (int i = offset; i < offset + count; ++i) {
                int c = bytes[i] & 0xFF;
                if (c < 16) {
                    sb.append('0');
                }
                sb.append(Integer.toHexString(c));
            }

            return sb.toString();
        } else {
            return null;
        }
    }

    public static <T> String join(Collection<T> objs, String delimiter) {
        StringBuilder builder = new StringBuilder();

        for (Iterator<T> it = objs.iterator(); it.hasNext();) {
            T obj = it.next();
            builder.append(obj);
            if (it.hasNext()) {
                builder.append(delimiter);
            }
        }

        return builder.toString();
    }

    
}
