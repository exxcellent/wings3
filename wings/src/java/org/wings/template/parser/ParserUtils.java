package org.wings.template.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.wings.session.SessionManager;

public class ParserUtils {

	/**
     * copies an Reader (input) to an Writer (output). copies max. length
     * bytes.
     *
     * @param in     The source reader
     * @param out    The destination writer
     * @param length number of bytes to copy; -1 for unlimited
     * @param buf    Buffer used as temporary space to copy
     *               block-wise.
     */
    public static void copy(Reader in, Writer out, long length, char[] buf) throws IOException {
        int len;
        boolean limited = (length >= 0);
        int rest = limited ? (int) length : buf.length;
        while (rest > 0 && (len = in.read(buf, 0, (rest > buf.length) ? buf.length : rest)) > 0) {
            out.write(buf, 0, len);
            if (limited) rest -= len;
        }
        out.flush();
    }
    
    /**
     * Returns the encoding of the streams.
     * 
     * @return The encoding of the streams.
     */
    public static String getStreamEncoding() {
    	String encoding = (String) SessionManager.getSession().getProperty("wings.template.layout.encoding");
    	if (encoding == null || "".equals(encoding)) {
    		encoding = "UTF-8";
    	}
    	return encoding;
    }
}
