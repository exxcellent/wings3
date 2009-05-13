/*
 * Copyright 2000,2005 wingS development team.
 *
 * This file is part of wingS (http://wingsframework.org).
 *
 * wingS is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * Please see COPYING for the complete licence.
 */
package org.wings.io;

import javax.servlet.ServletOutputStream;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * A Device encapsulating a ServletOutputStream.
 *
 * @author <a href="mailto:H.Zeller@acm.org">Henner Zeller</a>
 */
public class ServletDevice implements Device {
    protected ServletOutputStream out;
    private Writer writer;
    private String encoding;

    public ServletDevice(ServletOutputStream out, String encoding) throws IOException {
        this.out = out;
        this.encoding = encoding;
        writer = new BufferedWriter(new OutputStreamWriter(out, encoding));
    }

    public ServletDevice(String encoding) {
        this.encoding = encoding;
    }

    public void setServletOutputStream(ServletOutputStream out) throws IOException {
        this.out = out;
        writer = new BufferedWriter(new OutputStreamWriter(out, encoding));
    }

    public boolean isSizePreserving() { return true; }
    
    /**
     * Flush this Stream.
     */
    public void flush() throws IOException {
        writer.flush();
    }

    public void close() throws IOException {
        writer.flush();
        out.close();
    }

    /**
     * Print a String.
     */
    public Device print(String s) throws IOException {
        if (s == null)
            writer.write("null");
        else
            writer.write(s);
        return this;
    }

    /**
     * Print an integer.
     */
    public Device print(int i) throws IOException {
        print(String.valueOf(i));
        return this;
    }

    /**
     * Print any Object
     */
    public Device print(Object o) throws IOException {
        if (o == null)
            print("null");
        else
            print(o.toString());
        return this;
    }

    /**
     * Print a character.
     */
    public Device print(char c) throws IOException {
        writer.write(c);
        return this;
    }

    /**
     * Print an array of chars.
     */
    public Device print(char[] c) throws IOException {
        writer.write(c);
        return this;
    }

    /**
     * Print a character array.
     */
    public Device print(char[] c, int start, int len) throws IOException {
        writer.write(c, start, len);
        return this;
    }

    /**
     * Writes the specified byte to this data output stream.
     */
    public Device write(int c) throws IOException {
        // This method is expensive.
        writer.flush();
        out.write(c);
        out.flush();
        return this;
    }

    /**
     * Writes b.length bytes from the specified byte array to this
     * output stream.
     */
    public Device write(byte b[]) throws IOException {
        // This method is expensive.
        writer.flush();
        out.write(b);
        out.flush();
        return this;
    }

    /**
     * Writes len bytes from the specified byte array starting at offset
     * off to this output stream.
     */
    public Device write(byte b[], int off, int len) throws IOException {
        // This method is expensive.
        writer.flush();
        out.write(b, off, len);
        out.flush();
        return this;
    }
}


