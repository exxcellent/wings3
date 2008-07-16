/*
 * $Id$
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
package org.wings;

import org.wings.event.SDocumentEvent;
import org.wings.event.SDocumentListener;
import org.wings.event.SMouseEvent;
import org.wings.plaf.TextAreaCG;
import org.wings.plaf.TextFieldCG;
import org.wings.text.DefaultDocument;
import org.wings.text.SDocument;
import org.wings.sdnd.TextAndHTMLTransferable;
import org.wings.sdnd.SDropMode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.text.BadLocationException;
import javax.swing.*;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * Abstract base class of input text components like {@link STextArea} and {@link STextField}.
 * Requires a surrounding {@link SForm} element!
 *
 * @author <a href="mailto:armin.haaf@mercatis.de">Armin Haaf</a>
 * @version $Revision$
 */
public abstract class STextComponent extends SComponent implements LowLevelEventListener, SDocumentListener {
    private static final Log LOG = LogFactory.getLog(STextComponent.class);

    private boolean editable = true;

    private SDocument document;

    /**
     * @see LowLevelEventListener#isEpochCheckEnabled()
     */
    private boolean epochCheckEnabled = true;

    public STextComponent() {
        this(new DefaultDocument(), true);
    }

    public STextComponent(String text) {
        this(new DefaultDocument(text), true);
    }

    public STextComponent(SDocument document) {
        this(document, true);
    }

    public STextComponent(SDocument document, boolean editable) {
        setDocument(document);
        setEditable(editable);
        installTransferHandler();
        if(!(this instanceof STextField)) {
            setDropMode(SDropMode.USE_SELECTION);
        }
    }

    public SDocument getDocument() {
        return document;
    }

    public void setDocument(SDocument document) {
        if (document == null)
            throw new IllegalArgumentException("null");

        SDocument oldDocument = this.document;
        this.document = document;
        if (oldDocument != null)
            oldDocument.removeDocumentListener(this);
        document.addDocumentListener(this);
        reloadIfChange(oldDocument, document);
        propertyChangeSupport.firePropertyChange("document", oldDocument, this.document);
    }

    /**
     * Defines if the textcomponent is editable or not.
     * @see #isEditable()
     * @param ed true if the text component is to be editable false if not.
     */
    public void setEditable(boolean ed) {
        boolean oldEditable = editable;
        editable = ed;
        if (editable != oldEditable)
            reload();
        propertyChangeSupport.firePropertyChange("editable", oldEditable, this.editable);
    }


    public boolean isEditable() {
        return editable;
    }

    /**
     * Sets the text of the component to the specified text.
     * @see #getText()
     * @param text the new text for the component.
     */
    public void setText(String text) {
        String oldVal = document.getText();
        document.setText(text);
        propertyChangeSupport.firePropertyChange("text", oldVal, document.getText());
    }


    public String getText() {
        return document.getText();
    }

    /**
     * Appends the given text to the end of the document. Does nothing
     * if the string is null or empty.
     *
     * @param text the text to append.
     */
    public void append(String text) {
        try {
            document.insert(document.getLength(), text);
        } catch (BadLocationException e) {
        }
    }

    public void processLowLevelEvent(String action, String[] values) {
        processKeyEvents(values);
        if (action.endsWith("_keystroke"))
            return;

        if (isEditable() && isEnabled()) {
            String newValue = values[0];
            if (newValue != null && newValue.length() == 0) {
                newValue = null;
            }
            String text = getText();
            if (text != null && text.length() == 0) {
                text = null;
            }

            if (isDifferent(newValue, text)) {
                getDocument().setDelayEvents(true);
                setText(newValue);
                getDocument().setDelayEvents(false);
                SForm.addArmedComponent(this);
            }
        }
    }

    public SDocumentListener[] getDocumentListeners() {
    	return getDocument().getDocumentListeners();
    }

    public void addDocumentListener(SDocumentListener listener) {
        getDocument().addDocumentListener(listener);
    }

    public void removeDocumentListener(SDocumentListener listener) {
        getDocument().removeDocumentListener(listener);
    }

    public void fireIntermediateEvents() {
    	getDocument().fireDelayedIntermediateEvents();
    }

    public void fireFinalEvents() {
        super.fireFinalEvents();
        getDocument().fireDelayedFinalEvents();
    }

    /**
     * @see LowLevelEventListener#isEpochCheckEnabled()
     */
    public boolean isEpochCheckEnabled() {
        return epochCheckEnabled;
    }

    /**
     * @see LowLevelEventListener#isEpochCheckEnabled()
     */
    public void setEpochCheckEnabled(boolean epochCheckEnabled) {
        boolean oldVal = this.epochCheckEnabled;
        this.epochCheckEnabled = epochCheckEnabled;
        propertyChangeSupport.firePropertyChange("epochCheckEnabled", oldVal, this.epochCheckEnabled);
    }

    public void insertUpdate(SDocumentEvent e) {
        //reload();
    }

    public void removeUpdate(SDocumentEvent e) {
        //reload();
    }

    public void changedUpdate(SDocumentEvent e) {
        if (isUpdatePossible()) {
            if (STextField.class.isAssignableFrom(getClass()) && ! SPasswordField.class.isAssignableFrom(getClass()))
                update(((TextFieldCG) getCG()).getTextUpdate((STextField) this, getText()));
            else if (STextArea.class.isAssignableFrom(getClass()))
                update(((TextAreaCG) getCG()).getTextUpdate((STextArea) this, getText()));
            else
                reload();
        } else {
            reload();
        }
    }

    /**
     * Drag and Drop stuff
     */
    private int selectionStart;
    private int selectionEnd;

    /**
     * Internal Selection Mechanism for Drag and Drop - may be made public if a real selection support is introduced
     * @return
     */
    protected int getSelectionStart() {
        return selectionStart;
    }

    /**
     * Internal Selection Mechanism for Drag and Drop - may be made public if a real selection support is introduced
     * @param selectionStart
     */
    protected void setSelectionStart(int selectionStart) {
        this.selectionStart = selectionStart;
    }

    /**
     * Internal Selection Mechanism for Drag and Drop - may be made public if a real selection support is introduced
     * @return
     */
    protected int getSelectionEnd() {
        return selectionEnd;
    }

    /**
     * Internal Selection Mechanism for Drag and Drop - may be made public if a real selection support is introduced
     * @param selectionEnd
     */
    protected void setSelectionEnd(int selectionEnd) {
        this.selectionEnd = selectionEnd;
    }

    /**
     * Internal Selection Mechanism for Drag and Drop - may be made public if a real selection support is introduced
     * @return
     */
    protected String getSelectedText() {
        String text = getText();
        return text.substring(getSelectionStart(), getSelectionEnd());
    }

    protected boolean dragEnabled = false;
    protected SDropMode dropMode = null;

    /**
     * Sets the DropMode for this component - supports USE_SELECTION and INSERT
     * @param dropMode
     */
    public void setDropMode(SDropMode dropMode) {
        if(dropMode == null || (dropMode != SDropMode.USE_SELECTION))
            throw new IllegalArgumentException(dropMode + " - unsupported DropMode");

        if(this instanceof STextField) {
            LOG.warn("setDropMode: STextField as DropTarget will show different drop-behaviour in IE than in FF");
        }
        this.dropMode = dropMode;
        getSession().getSDragAndDropManager().addDropTarget(this);
    }

    private void installTransferHandler() {
        if(getTransferHandler() == null) {
            setTransferHandler(new DefaultTransferHandler());
        }
    }

    public SDropMode getDropMode() {
        return this.dropMode;
    }

    public void setDragEnabled(boolean dragEnabled) {
        if(dragEnabled != this.dragEnabled) {
            if(dragEnabled)
                this.getSession().getSDragAndDropManager().addDragSource(this);
            else
                this.getSession().getSDragAndDropManager().removeDragSource(this);

            this.dragEnabled = dragEnabled;
        }
    }

    public boolean getDragEnabled() {
        return this.dragEnabled;
    }

    protected DropLocation dropLocationForPoint(SPoint point) {
        if(point.getCoordinates() == null)
            return null;
        if(point.getCoordinates().indexOf("-") != -1 && point.getCoordinates().indexOf("-1") != 0) // dragstart => dragenter event
            return null;
        return new DropLocation(point);
    }

    protected static final class DropLocation extends STransferHandler.DropLocation {
        private int index;

        public DropLocation(SPoint point) {
            super(point);

            try {
                this.index = Integer.parseInt(point.getCoordinates());
                if(this.index == -1)
                    this.index = 0;
            } catch(NumberFormatException e) {
                this.index = -1;
                e.printStackTrace();
            }
        }

        public int getIndex() {
            return this.index;
        }
    }

    private static class TextTransferable extends TextAndHTMLTransferable {
        private int startPos, endPos;
        private STextComponent component;
        private int insertIndex;

        protected TextTransferable(STextComponent component, int startPos, int endPos) {
            super(null, null);

            this.startPos = startPos;
            this.endPos = endPos;
            this.component = component;

            this.plainTextData = component.getSelectedText();
        }

        public void setInsertIndex(int insertIndex) {
            this.insertIndex = insertIndex;
        }

        protected void removeText() {
            String newtext = this.component.getText();
            try {
                component.getDocument().remove(this.startPos+((insertIndex<=startPos)?this.endPos - this.startPos:-(this.endPos - this.startPos)), this.endPos - this.startPos);
                component.reload();
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }

    protected static class DefaultTransferHandler extends STransferHandler {
        public DefaultTransferHandler() {
            super("text");
        }
        
        public void exportDone(SComponent source, Transferable transferable, int action) {
            if(action == MOVE) {
                if(transferable instanceof TextTransferable) {
                    ((TextTransferable)transferable).removeText();
                }
            }

            super.exportDone(source, transferable, action);
        }

        protected Transferable createTransferable(SComponent component) {
            if(component instanceof STextComponent) {
                STextComponent textComponent = (STextComponent)component;
                return new TextTransferable(textComponent, textComponent.getSelectionStart(), textComponent.getSelectionEnd());
            }
            
            return null;
        }

        protected Transferable createTransferable(SComponent component, SMouseEvent event) {
            if(component instanceof STextComponent) {
                STextComponent textComponent = (STextComponent)component;

                String[] stringArray = event.getPoint().getCoordinates().split("-");
            
                int startIndex = Integer.parseInt(stringArray[0]);
                int endIndex = Integer.parseInt(stringArray[1]);
                textComponent.setSelectionStart(startIndex);
                textComponent.setSelectionEnd(endIndex);
            }

            return super.createTransferable(component, event);
        }

        protected static DataFlavor getImportFlavor(STextComponent component, DataFlavor[] flavors) {
            for(DataFlavor flavor:flavors) {
                if(flavor.getMimeType().startsWith("text/plain")) {
                    return flavor;
                } else if(flavor.getMimeType().startsWith(DataFlavor.javaJVMLocalObjectMimeType) && flavor.getRepresentationClass() == String.class) {
                    return flavor;
                }
            }
            return null;
        }

        public boolean canImport(SComponent component, DataFlavor[] transferFlavors) {
            STextComponent textComponent = (STextComponent)component;
            if(!textComponent.isEditable() || !textComponent.isEnabled()) // if the component is not editable
                return false;                                               // or disabled, don't allow imports

            if(getImportFlavor(textComponent, transferFlavors) != null) {
                return true;
            }

            return false;
        }

        private int insertIndex = 0;

        public boolean importData(SComponent component, Transferable transferable) {
            try {
                STextComponent textComponent = (STextComponent)component;

                String data = (String)(transferable.getTransferData(getImportFlavor((STextComponent)component, transferable.getTransferDataFlavors())));
                String text = textComponent.getText();

                if(insertIndex == -1) { // in case we couldn't determine a drop position, append
                    textComponent.setText(text + data);
                    return true;
                }
                if(insertIndex > text.length())
                    return false;

                String firstPart = text.substring(0, insertIndex);
                String secondPart = text.substring(insertIndex); 

                if(transferable instanceof TextTransferable) {
                    ((TextTransferable)transferable).setInsertIndex(insertIndex);
                }
                textComponent.setText(firstPart + data + secondPart);
                return true;
            } catch (UnsupportedFlavorException e) {
            } catch (IOException e) {
            }
            return false;
        }

        public boolean importData(TransferSupport support) {
            insertIndex = ((STextComponent.DropLocation)support.getDropLocation()).getIndex();
            return super.importData(support);
        }

        public int getSourceActions(SComponent component) {
            if(component instanceof SPasswordField) // don't allow drag/drop from password fields
                return NONE;

            if(!((STextComponent)component).isEditable()) // don't allow MOVE when the component isn't editable
                return COPY;
            
            return COPY_OR_MOVE;
        }
    }
}
