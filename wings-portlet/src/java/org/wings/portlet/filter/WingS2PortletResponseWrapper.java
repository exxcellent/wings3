package org.wings.portlet.filter;

import java.io.CharArrayWriter;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * @author <a href="mailto:marc.musch@mercatis.com">Marc Musch</a>
 */
public class WingS2PortletResponseWrapper 
	extends HttpServletResponseWrapper {
	
	@Override
	public ServletOutputStream getOutputStream() throws IOException {	
		return new ServletOutputStream() {

			public void write(int b) throws IOException {
				writer.write(b);					
			}
			
		};
	}

	/**
     * char array writer
     */
    final private CharArrayWriter writer = new CharArrayWriter();

    /**
     * constructor
     * @param response
     */
    public WingS2PortletResponseWrapper(final HttpServletResponse response) {
       super(response);
    }
//
//    /**
//     * get writer
//     */
//    public final PrintWriter getWriter() throws java.io.IOException {
//       return new PrintWriter(writer);
//    }
//
    /**
     * to string
     */
    public final String toString() {
      return writer.toString();
    }
}
