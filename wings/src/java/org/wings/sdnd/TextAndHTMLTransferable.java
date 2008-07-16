/*
 * Copyright 2000,2008 wingS development team.
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

package org.wings.sdnd;

import java.awt.datatransfer.DataFlavor;
import java.io.*;

/**
 * TextAndHTMLTransferable for Components that support HTML and Plaintext flavors
 * @author Florian Roks
 */
public class TextAndHTMLTransferable extends DefaultTransferable {
    protected String plainTextData;
    protected String htmlData;

    private static DataFlavor[] flavorList;
    static {
        try {
// from overwritten importData:
//    importData:javax.swing.TransferHandler$TransferSupport@12152e6 javax.swing.JTextArea[,0,-18,383x150,layout=javax.swing.plaf.basic.BasicTextUI$UpdateHandler,alignmentX=0.0,alignmentY=0.0,border=javax.swing.plaf.basic.BasicBorders$MarginBorder@c9ba38,flags=296,maximumSize=,minimumSize=,preferredSize=java.awt.Dimension[width=200,height=150],caretColor=javax.swing.plaf.ColorUIResource[r=0,g=0,b=0],disabledTextColor=javax.swing.plaf.ColorUIResource[r=128,g=128,b=128],editable=true,margin=javax.swing.plaf.InsetsUIResource[top=2,left=2,bottom=2,right=2],selectedTextColor=javax.swing.plaf.ColorUIResource[r=255,g=255,b=255],selectionColor=javax.swing.plaf.ColorUIResource[r=51,g=153,b=255],colums=0,columWidth=0,rows=0,rowHeight=18,word=false,wrap=false]
//    text/html; class=java.lang.String
//    text/html; class=java.io.Reader
//    text/html; class=java.io.InputStream; charset=unicode
//    text/plain; class=java.lang.String
//    text/plain; class=java.io.Reader
//    text/plain; class=java.io.InputStream; charset=unicode
//    application/x-java-jvm-local-objectref; class=java.lang.String
//    application/x-java-serialized-object; class=java.lang.String
            flavorList = new DataFlavor[] {
                    new DataFlavor("text/html; class=java.lang.String"),
                    new DataFlavor("text/html; class=java.io.Reader"),
                    new DataFlavor("text/html; class=java.io.InputStream; charset=unicode"),
                    new DataFlavor("text/plain; class=java.lang.String"),
                    new DataFlavor("text/plain; class=java.io.Reader"),
                    new DataFlavor("text/plain; class=java.io.InputStream; charset=unicode"),
                    new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + "; class=java.lang.String"),
                    DataFlavor.stringFlavor
                };
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructs a TextAndHTMLTransferable with plain text plainTextDAta and HTML-code htmlData
     * @param plainTextData
     * @param htmlData
     */
    public TextAndHTMLTransferable(String plainTextData, String htmlData) {
        super(TextAndHTMLTransferable.flavorList);

        this.plainTextData = plainTextData;
        this.htmlData = htmlData;
    }

    protected Object getDataForClass(DataFlavor df, Class<?> cls) {
        if(df.getPrimaryType().equals("text")) {
            // text/html
            if(df.getSubType().equals("html")) {
                if(cls.equals(String.class)) {
                    return this.htmlData;
                } else if(cls.equals(Reader.class)) {
                    return new StringReader(this.htmlData);
                } else if(cls.equals(InputStream.class)) {
                    return new ByteArrayInputStream(this.htmlData.getBytes());
                }
            }

            // text/plain
            if(df.getSubType().equals("plain")) {
                if(cls.equals(String.class)) {
                    return this.plainTextData;
                } else if(cls.equals(Reader.class)) {
                    return new StringReader(this.plainTextData);
                } else if(cls.equals(InputStream.class)) {
                    return new ByteArrayInputStream(this.plainTextData.getBytes());
                }
            }
        }

        if(df.getPrimaryType().equals("application")) {
            // application/x-java-jvm-local-objectref
            if(df.getMimeType().startsWith(DataFlavor.javaJVMLocalObjectMimeType)) {
                return (this.plainTextData != null) ? this.plainTextData : "";
            } else if(df.equals(DataFlavor.stringFlavor)) {
                return (this.plainTextData != null) ? this.plainTextData : "";
            }
        }
        
        return null;
    }
}
