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
package org.wings;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wings.plaf.DialogCG;
import org.wings.plaf.FrameCG;

/**
 * Top-level window with a title and a border that is typically used to take
 * some form of input from the user. <p/> As opposed to Swing, wingS dialogs are
 * non modal. However, the dismission of the dialog is propagated by means of
 * ActionEvents. The action command of the event tells, what kind of user
 * activity caused the dialog to dismiss.
 * 
 * @author <a href="mailto:engels@mercatis.de">Holger Engels</a>
 * @author <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman R&auml;dle</a>
 */
public class SDialog extends SWindow {
	private final transient static Log log = LogFactory.getLog(SDialog.class);

	/**
	 * Action command if dialog window was closed
	 */
	public static final String CLOSE_ACTION = "CLOSE";

	/**
	 * Action command if user hit return
	 */
	public static final String DEFAULT_ACTION = "DEFAULT";

	/**
	 * The Title of the Dialog Frame
	 */
	protected String title;

	protected SIcon icon = null;

	protected boolean modal = false;

	protected boolean draggable = true;

	private boolean closable = true;
	private boolean closed = false;

	/**
	 * Creates a Dialog without parent <code>SFrame</code> or
	 * <code>SDialog</code> and without Title
	 */
	public SDialog() {
	}

	/**
	 * Creates a dialog with the specifed parent <code>SFrame</code> as its
	 * owner.
	 * 
	 * @param owner
	 *            the parent <code>SFrame</code>
	 */
	public SDialog(SFrame owner) {
		this(owner, null, false);
	}

	/**
	 * Creates a modal or non-modal dialog without a title and with the
	 * specified owner <code>Frame</code>. If <code>owner</code> is
	 * <code>null</code>, a shared, hidden frame will be set as the owner of
	 * the dialog.
	 * <p>
	 * This constructor sets the component's locale property to the value
	 * returned by <code>JComponent.getDefaultLocale</code>.
	 * 
	 * @param owner
	 *            the <code>Frame</code> from which the dialog is displayed
	 * @param modal
	 *            true for a modal dialog, false for one that allows others
	 *            windows to be active at the same time returns true.
	 */
	public SDialog(SFrame owner, boolean modal) {
		this(owner, null, modal);
	}

	/**
	 * Creates a dialog with the specified title and the specified owner frame.
	 * 
	 * @param owner
	 *            the parent <code>SFrame</code>
	 * @param title
	 *            the <code>String</code> to display as titke
	 */
	public SDialog(SFrame owner, String title) {
		this(owner, title, false);
	}

	/**
	 * Creates a modal or non-modal dialog with the specified title and the
	 * specified owner <code>Frame</code>. If <code>owner</code> is
	 * <code>null</code>, a shared, hidden frame will be set as the owner of
	 * this dialog. All constructors defer to this one.
	 * <p>
	 * NOTE: Any popup components (<code>JComboBox</code>,
	 * <code>JPopupMenu</code>, <code>JMenuBar</code>) created within a
	 * modal dialog will be forced to be lightweight.
	 * <p>
	 * This constructor sets the component's locale property to the value
	 * returned by <code>JComponent.getDefaultLocale</code>.
	 * 
	 * @param owner
	 *            the <code>Frame</code> from which the dialog is displayed
	 * @param title
	 *            the <code>String</code> to display in the dialog's title bar
	 * @param modal
	 *            true for a modal dialog, false for one that allows other
	 *            windows to be active at the same time returns true.
	 */
	public SDialog(SFrame owner, String title, boolean modal) {
		this.owner = owner;
		this.title = title;
		this.modal = modal;
	}

	/**
	 * Sets the title of the dialog.
	 * 
	 * @param t
	 *            the title displayed in the dialog's border; a null value
	 *            results in an empty title
	 */
	public void setTitle(String t) {
		String oldTitle = title;
		title = t;
		if ((title == null && oldTitle != null)
				|| (title != null && !title.equals(oldTitle)))
			reload();
        propertyChangeSupport.firePropertyChange("title", oldTitle, this.title);
    }

	/**
	 * Gets the title of the dialog. The title is displayed in the dialog's
	 * border.
	 * 
	 * @return the title of this dialog window. The title may be null.
	 */
	public String getTitle() {
		return title;
	}

	public void setIcon(SIcon i) {
		if (i != icon || i != null && !i.equals(icon)) {
            SIcon oldVal = this.icon;
            icon = i;
			reload();
            propertyChangeSupport.firePropertyChange("icon", oldVal, this.icon);
        }
	}

	public SIcon getIcon() {
		return icon;
	}

	public boolean isModal() {
		return modal;
	}

	public void setModal(boolean modal) {
        boolean oldVal = this.modal;
        this.modal = modal;
        propertyChangeSupport.firePropertyChange("modal", oldVal, this.modal);
    }

	public boolean isDraggable() {
		return draggable;
	}

	public void setDraggable(boolean draggable) {
        boolean oldVal = this.draggable;
        this.draggable = draggable;
        propertyChangeSupport.firePropertyChange("draggable", oldVal, this.draggable);
    }

	public void hide() {
		super.hide();
	}

	public void setClosable(boolean v) {
		boolean old = closable;
		closable = v;
		if (old != closable)
			reload();
	}

	public boolean isClosable() {
		return closable;
	}

	// public void setClosed(boolean v) {
	// v &= isClosable();
	// boolean old = closed;
	// closed = v;
	// if (old != closed)
	// reload();
	// }
	//
	// public boolean isClosed() {
	// return closed;
	// }

	public void setCG(DialogCG cg) {
		super.setCG(cg);
	}
}
