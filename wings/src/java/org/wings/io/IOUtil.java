package org.wings.io;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.wings.session.SessionManager;

public class IOUtil {

	public static final String DEFAULT_ENCODING = "UTF-8";

	/**
	 * copies a Reader (input) to a Writer (output). copies max. length bytes.
	 * 
	 * @param in
	 *            The source reader
	 * @param out
	 *            The destination writer
	 * @param length
	 *            number of bytes to copy; -1 for unlimited
	 * @param buf
	 *            Buffer used as temporary space to copy block-wise.
	 */
	public static void copy(Reader in, Writer out, long length, char[] buf)
			throws IOException {
		int len;
		boolean limited = (length >= 0);
		int rest = limited ? (int) length : buf.length;
		while (rest > 0
				&& (len = in.read(buf, 0, (rest > buf.length) ? buf.length
						: rest)) > 0) {
			out.write(buf, 0, len);
			if (limited)
				rest -= len;
		}
		out.flush();
	}

	/**
	 * Returns the I/O encoding used to read from or write to streams. The {
	 * {@link #DEFAULT_ENCODING} will be returned if encoding parameter has not
	 * been set in web.xml.
	 * 
	 * @return The I/O encoding used to read from or write to streams.
	 */
	public static String getIOEncoding() {
		return getIOEncoding(DEFAULT_ENCODING);
	}

	/**
	 * Returns the I/O encoding used to read from or write to streams.
	 * 
	 * @param defaultEncoding
	 *            The default encoding will be returned if encoding parameter
	 *            has not been set in web.xml.
	 * @return The I/O encoding used to read from or write to streams.
	 */
	public static String getIOEncoding(String defaultEncoding) {
		String encoding = (String) SessionManager.getSession().getProperty(
				"wings.io.encoding");
		return encoding != null && !"".equals(encoding) ? encoding
				: defaultEncoding;
	}
}
