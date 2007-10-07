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
package org.wings.plaf.css;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wings.*;
import org.wings.border.SDefaultBorder;
import org.wings.border.SEmptyBorder;
import org.wings.dnd.DragSource;
import org.wings.dnd.DropTarget;
import org.wings.io.Device;
import org.wings.io.StringBuilderDevice;
import org.wings.plaf.*;
import org.wings.plaf.css.dwr.CallableManager;
import org.wings.plaf.css.script.OnPageRenderedScript;
import org.wings.script.ScriptListener;
import org.wings.session.BrowserType;
import org.wings.session.ScriptManager;
import org.wings.util.SStringBuilder;
import org.wings.util.SessionLocal;

import javax.swing.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Partial CG implementation that is common to all ComponentCGs.
 *
 * @author <a href="mailto:engels@mercatis.de">Holger Engels</a>
 */
public abstract class AbstractComponentCG<COMPONENT_TYPE
    extends SComponent>
    implements ComponentCG<COMPONENT_TYPE>, SConstants, Serializable
{
    private static final Log log = LogFactory.getLog(AbstractComponentCG.class);

    /**
     * An invisible icon / graphic (spacer graphic)
     */
    private static final SIcon BLIND_ICON = new SResourceIcon("org/wings/icons/blind.gif");

    /**
     * Be careful with this shared StringBuilder. Don't use it in situations where unknown code is called, that might
     * use the StringBuilder, too.
     */
    public static final SessionLocal<StringBuilder> STRING_BUILDER = new SessionLocal<StringBuilder>() {
        protected StringBuilder initialValue() {
            return new StringBuilder();
        }
    };

    protected AbstractComponentCG() {
    }

    /**
     * Renders the default HTML TABLE prefix code for a component. This prefix will handle
     * <ul>
     * <li>All CSS Attrbiutes declared on the SComponent</li>
     * <li>Components CSS class</li>
     * <li>Components id</li>
     * <li>Borders and insets (only if TABLE is used)</li>
     * <li>Components ToolTip hooks</li>
     * <li>Components Popup menu hooks</li>
     * <li>components event id <code>eid</code> </li>
     * </ul>
     *
     * @param device The output device to use
     * @param component The component to render
     * @throws IOException on error on the output device
     */
    protected final void writeTablePrefix(Device device, COMPONENT_TYPE component) throws IOException {
        writePrefix(device, component, true, null);
    }


    /**
     * Renders the default HTML TABLE prefix code for a component. This prefix will handle
     * <ul>
     * <li>All CSS Attrbiutes declared on the SComponent</li>
     * <li>Components CSS class</li>
     * <li>Components id</li>
     * <li>Borders and insets (only if TABLE is used)</li>
     * <li>Components ToolTip hooks</li>
     * <li>Components Popup menu hooks</li>
     * <li>components event id <code>eid</code> </li>
     * </ul>
     *
     * @param device The output device to use
     * @param component The component to render
     * @throws IOException on error on the output device
     */
    protected final void writeTablePrefix(Device device, COMPONENT_TYPE component, Map optionalAttributes) throws IOException {
        writePrefix(device, component, true, optionalAttributes);
    }


    /**
     * Renders the closing suffix for a TABLE based component prefix.
     * @param device The output device to use
     * @param component The component to render
     * @throws IOException on error on the output device
     */
    protected final void writeTableSuffix(Device device, COMPONENT_TYPE component) throws IOException {
        writeSuffix(device, component, true);
    }

    /**
     * Renders a DIV prefix code for a component. <b>Discouraged</b>
     * This prefix will handle
     * <ul>
     * <li>All CSS Attrbiutes declared on the SComponent</li>
     * <li>Components CSS class</li>
     * <li>Components id</li>
     * <li>Borders and insets (only if TABLE is used)</li>
     * <li>Components ToolTip hooks</li>
     * <li>Components Popup menu hooks</li>
     * <li>components event id <code>eid</code> </li>
     * </ul>
     *
     * @param device The output device to use
     * @param component The component to render
     * @param optionalAttributes A map of additional CSS attributes
     * @throws IOException on error on the output device
     */
    protected final void writeDivPrefix(Device device, COMPONENT_TYPE component, Map optionalAttributes) throws IOException {
        writePrefix(device, component, false, optionalAttributes);
    }


    /**
     * Renders the closing suffix for a DIV prefix.
     * @param device The output device to use
     * @param component The component to render
     * @throws IOException on error on the output device
     */
    protected final void writeDivSuffix(Device device, COMPONENT_TYPE component) throws IOException {
        writeSuffix(device, component, false);
    }


    /**
     * Renders the HTML prefix code for a component. This prefix will handle
     * <ul>
     * <li>All CSS Attrbiutes declared on the SComponent</li>
     * <li>Components CSS class</li>
     * <li>Components id</li>
     * <li>Borders and insets (only if TABLE is used)</li>
     * <li>Components ToolTip hooks</li>
     * <li>Components Popup menu hooks</li>
     * <li>components event id <code>eid</code> </li>
     * </ul>
     *
     * @param device The output device to use
     * @param component The component to render
     * @param useTable <code>true</code> if it should be wrapped into a <code>TABLE</code> element <b>(recommended!)</b> or a <code>DIV</code>
     * @param optionalAttributes A map of additional CSS attributes
     * @throws IOException on error on the output device
     */
    protected final void writePrefix(final Device device, final COMPONENT_TYPE component,
                               final boolean useTable, Map optionalAttributes)
            throws IOException {
        // This is the containing element of a component
        // it is responsible for styles, sizing...
        if (useTable) {
            device.print("<table"); // table
        }
        else {
            device.print("<div"); // div
        }

        // We cant render this here.
        // Utils.writeEvents(device, component, null);

        Utils.writeAllAttributes(device, component);

        // Render the optional attributes.
        Utils.optAttributes(device, optionalAttributes);

        if (useTable) {
            device.print("><tr><td"); // table
            final SStringBuilder styleString = new SStringBuilder();
            if (component.getBorder() != null) {
                Utils.createInlineStylesForInsets(styleString, component.getBorder().getInsets());
            }
            Utils.optAttribute(device, "style", styleString);

            device.print(">"); // table
        }
        else {
            device.print(">"); // div
        }
    }

    protected final void writeSuffix(Device device, COMPONENT_TYPE component, boolean useTable) throws IOException {
        if (useTable) {
            device.print("</td></tr></table>");
        }
        else {
            device.print("</div>");
        }
    }

    /**
     * Write JS code for context menus. Common implementaton for MSIE and gecko.
     */
    protected static void writeContextMenu(Device device, SComponent component) throws IOException {
        final SPopupMenu menu = component.getComponentPopupMenu();
        if (menu != null && menu.isEnabled()) {
            final String componentId = menu.getName();
            final String popupId = componentId + "_pop";
            device.print(" onContextMenu=\"return wpm_menuPopup(event, '");
            device.print(popupId);
            device.print("');\" onMouseDown=\"return wpm_menuPopup(event, '");
            device.print(popupId);
            device.print("');\"");
        }
    }

    /**
     * Write Tooltip code.
     */
    protected static void writeTooltipMouseOver(Device device, SComponent component) throws IOException {
        final String toolTipText = component != null ? component.getToolTipText() : null;
        if (toolTipText != null && toolTipText.length() > 0) {
            device.print(" onmouseover=\"Tip('");
            Utils.quote(device, toolTipText, true, false, true);
            device.print("')\"");
        }
    }

    /**
     * Install the appropriate CG for <code>component</code>.
     *
     * @param component the component
     */
    public void installCG(COMPONENT_TYPE component) {
        Class clazz = component.getClass();
        while (clazz.getPackage() == null || !("org.wings".equals(clazz.getPackage().getName()) || "org.wingx".equals(clazz.getPackage().getName())))
            clazz = clazz.getSuperclass();
        String style = clazz.getName();
        style = style.substring(style.lastIndexOf('.') + 1);
        component.setStyle(style); // set default style name to component class (ie. SLabel).
        component.setBorder(SDefaultBorder.INSTANCE); // the default style writes _no_ attributes, thus the stylesheet is in effect

        if (Utils.isMSIE(component)) {
            final CGManager manager = component.getSession().getCGManager();
            Object value;
            int verticalOversize = 0;
            value = manager.getObject(style + ".verticalOversize", Integer.class);
            if (value != null)
                verticalOversize = ((Integer) value).intValue();
            int horizontalOversize = 0;
            value = manager.getObject(style + ".horizontalOversize", Integer.class);
            if (value != null)
                horizontalOversize = ((Integer) value).intValue();

            if (verticalOversize != 0 || horizontalOversize != 0)
                component.putClientProperty("oversize", new SEmptyBorder(verticalOversize, horizontalOversize, verticalOversize, horizontalOversize));
        }
    }

    /**
     * Uninstall the CG from <code>component</code>.
     *
     * @param component the component
     */
    public void uninstallCG(COMPONENT_TYPE component) {
    }

    /**
     * Retrieve a empty blind icon.
     * @return A empty blind icon.
     */
    protected final SIcon getBlindIcon() {
        return BLIND_ICON;
    }

    public void componentChanged(COMPONENT_TYPE component) {
        /*
        InputMap inputMap = component.getInputMap();
        if (inputMap != null && inputMap.size() > 0) {
            if (!(inputMap instanceof VersionedInputMap)) {
                inputMap = new VersionedInputMap(inputMap);
                component.setInputMap(inputMap);
            }

            final VersionedInputMap versionedInputMap = (VersionedInputMap) inputMap;
            final Integer inputMapVersion = (Integer) component.getClientProperty("inputMapVersion");
            if (inputMapVersion == null || versionedInputMap.getVersion() != inputMapVersion.intValue()) {
                //InputMapScriptListener.install(component);
                component.putClientProperty("inputMapVersion", new Integer(versionedInputMap.getVersion()));
            }
        }
        */

        // Add script listener support.
        List scriptListenerList = component.getScriptListenerList();
        if (scriptListenerList != null && !scriptListenerList.isEmpty()) {
            // BSC START ----------------: Bad code : behaviour injection !!!
            /*

            HINT: Just try a
               - oldList = xx;
               - if (!currerntList.equals(oldList) !!!!!!!!!

            if (!(scriptListenerList instanceof VersionedList)) {
                scriptListenerList = new VersionedList(scriptListenerList);
                component.setScriptListenerList(scriptListenerList);
            }


            final VersionedList versionedList = (VersionedList) scriptListenerList;
            final Integer scriptListenerListVersion = (Integer) component.getClientProperty("scriptListenerListVersion");
            if (scriptListenerListVersion == null || versionedList.getVersion() != scriptListenerListVersion.intValue())
            */if (true) // BSC ------------ END
        {
            /* TODO: this code destroys the dwr functionality
            List removeCallables = new ArrayList();
            // Remove all existing - and maybe unusable - DWR script listeners.
            for (Iterator iter = CallableManager.getInstance().callableNames().iterator(); iter.hasNext();) {
                Object o = iter.next();
                if (o instanceof String) {
                    removeCallables.add(o);
                }
            }

            for (Iterator iter = removeCallables.iterator(); iter.hasNext(); ) {
                Object o = iter.next();
                if (o instanceof String) {
                    CallableManager.getInstance().unregisterCallable((String) o);
                }
            }
            */

            // Add DWR script listener support.
            ScriptListener[] scriptListeners = component.getScriptListeners();
            for (ScriptListener scriptListener1 : scriptListeners) {
                if (scriptListener1 instanceof DWRScriptListener) {
                    DWRScriptListener scriptListener = (DWRScriptListener) scriptListener1;
                    CallableManager.getInstance().registerCallable(scriptListener.getCallableName(), scriptListener.getCallable());
                }
            }

            //component.putClientProperty("scriptListenerListVersion", new Integer(versionedList.getVersion()));
        }
        }
    }

    /**
     * This method renders the component (and all of its subcomponents) to the given device.
     */
	public final void write(final Device device, final COMPONENT_TYPE component) throws IOException {
        Utils.printDebug(device, "<!-- ").print(component.getName()).print(" -->");
        component.fireRenderEvent(SComponent.START_RENDERING);

        try {
            BorderCG.writeComponentBorderPrefix(device, component);
            writeInternal(device, component);
            ScriptManager.getInstance().addScriptListeners(component.getScriptListeners());
            BorderCG.writeComponentBorderSufix(device, component);
        } catch (RuntimeException e) {
            log.fatal("Runtime exception during rendering of " + component.getName(), e);
            device.print("<blink>" + e.getClass().getName() + " during code generation of "
                    + component.getName() + "(" + component.getClass().getName() + ")</blink>\n");
        }

        component.fireRenderEvent(SComponent.DONE_RENDERING);
        Utils.printDebug(device, "<!-- /").print(component.getName()).print(" -->");

        updateDragAndDrop(component);
    }

    protected void updateDragAndDrop(final SComponent component) {
        if (component instanceof DragSource && ((DragSource)component).isDragEnabled()) {
            StringBuilder builder = STRING_BUILDER.get();
            builder.setLength(0);

            String name = component.getName();
            builder.append("var ds_");
            builder.append(name);
            builder.append(" = new wingS.dnd.DD(\"");
            builder.append(name);
            builder.append("\");");
            writeRegisterDragHandle(builder, component);
            builder.append("\n");

            ScriptManager.getInstance().addScriptListener(new OnPageRenderedScript(builder.toString()));
        }
        if (component instanceof DropTarget) {
            StringBuilder builder = STRING_BUILDER.get();
            builder.setLength(0);

            String name = component.getName();
            builder.append("var dt_");
            builder.append(name);
            builder.append(" = new YAHOO.util.DDTarget(\"");
            builder.append(name);
            builder.append("\");\n");
            ScriptManager.getInstance().addScriptListener(new OnPageRenderedScript(builder.toString()));
        }
    }

    protected void writeRegisterDragHandle(StringBuilder builder, SComponent component) {
        String dragHandle = getDragHandle(component);
        if (dragHandle != null) {
            String name = component.getName();
            builder.append("ds_");
            builder.append(name);
            builder.append(".setHandleElId(\"");
            builder.append(dragHandle);
            builder.append("\");");
        }
    }

    protected String getDragHandle(SComponent component) {
        return null;
    }

    public abstract void writeInternal(Device device, COMPONENT_TYPE component) throws IOException;

    /**
     * @return true if current browser is Microsoft Internet Explorer
     */
    protected final boolean isMSIE(final SComponent component) {
        return component.getSession().getUserAgent().getBrowserType() == BrowserType.IE;
    }

	public Update getComponentUpdate(COMPONENT_TYPE component) {
        updateDragAndDrop(component);
        return new ComponentUpdate(component);
	}

	protected class ComponentUpdate extends AbstractUpdate<COMPONENT_TYPE> {

		public ComponentUpdate(COMPONENT_TYPE component) {
			super(component);
		}

        @Override
        public int getProperty() {
            return FULL_REPLACE_UPDATE;
        }

        @Override
        public int getPriority() {
            return 4;
        }

		@Override
        public Handler getHandler() {
            String htmlCode = "";
            String exception = null;

            try {
                StringBuilderDevice htmlDevice = new StringBuilderDevice(1024);
                write(htmlDevice, component);
                htmlCode = htmlDevice.toString();
            } catch (Throwable t) {
                log.fatal("An error occured during rendering", t);
                exception = t.getClass().getName();
            }

            UpdateHandler handler = new UpdateHandler("component");
            handler.addParameter(component.getName());
            handler.addParameter(htmlCode);
            if (exception != null) {
                handler.addParameter(exception);
            }
			return handler;
		}
	}
}
