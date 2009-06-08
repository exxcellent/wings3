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

package org.wings.plaf.css;

import org.wings.*;
import org.wings.script.ScriptListener;
import org.wings.session.SessionManager;
import org.wings.session.ScriptManager;
import org.wings.sdnd.SDragAndDropManager;
import org.wings.sdnd.CustomDropStayHandler;
import org.wings.header.Header;
import org.wings.header.SessionHeaders;
import org.wings.plaf.Update;
import org.wings.io.Device;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * SDragAndDropManager Default CG
 *
 * @author Florian Roks
 */
public class SDragAndDropManagerCG extends AbstractComponentCG implements org.wings.plaf.SDragAndDropManagerCG {
    protected final static List<Header> headers = new ArrayList<Header>();
    protected final static List<SResourceIcon> icons = new ArrayList<SResourceIcon>();

    public SDragAndDropManagerCG() {
    }

    @Override
    public void installCG(SComponent c) {
    }

    @Override
    public void uninstallCG(SComponent c) {
    }

    protected String getAddDropTargetString(SDragAndDropManager manager, SComponent dropTarget) {
        String dropCode = manager.getCustomDropCode(dropTarget);

        if (dropCode == null) {
            if (dropTarget instanceof STextComponent)
                dropCode = "text";
            else if (dropTarget instanceof SList)
                dropCode = "list";
            else if (dropTarget instanceof STree)
                dropCode = "tree";
            else if (dropTarget instanceof STable)
                dropCode = "table";
            else
                dropCode = "default";
        }

        String configuration = null;
        STransferHandler handler = dropTarget.getTransferHandler();
        if (handler instanceof CustomDropStayHandler) {
            configuration = "{"; /* { blah = 123, blub = 321 } */
            Map<String, Object> map = ((CustomDropStayHandler) handler).getDropStayConfiguration();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                configuration += entry.getKey() + ":";
                Object value = entry.getValue();
                if (value instanceof Integer)
                    configuration += value + ", ";
                else if (value instanceof String)
                    configuration += "'" + value + "', ";
            }
            configuration = configuration.substring(0, configuration.length() - 2); // removes the last ", "
            configuration += "}";
        }

        return "wingS.global.onHeadersLoaded(function() { wingS.sdnd.addDropTarget('" + dropTarget.getName() + "', '" + dropCode + "', " + configuration + "); });";
    }

    protected String getAddDragSourceString(SDragAndDropManager manager, SComponent dragSource) {
        String dragCode = manager.getCustomDragCode(dragSource);

        if (dragCode == null) {
            if (dragSource instanceof STextComponent)
                dragCode = "text";
            else if (dragSource instanceof SList)
                dragCode = "list";
            else if (dragSource instanceof STree)
                dragCode = "tree";
            else if (dragSource instanceof STable)
                dragCode = "table";
            else
                dragCode = "default";
        }

        return "wingS.global.onHeadersLoaded(function() { wingS.sdnd.addDragSource('" + dragSource.getName() + "', " + dragSource.getTransferHandler().getSourceActions(dragSource) + ", '" + dragCode + "'); });";
    }

    protected String getRemoveDragSourceString(SComponent dragSource) {
        return "wingS.sdnd.removeDragSource('" + dragSource.getName() + "');";
    }

    protected String getRemoveDropTargetString(SComponent dropTarget) {
        return "wingS.sdnd.removeDropTarget('" + dropTarget.getName() + "');";
    }

    public Update getComponentUpdate(SComponent component) {   // no component updates possible nor allowed
        return null;
    }

    @Override
    public void writeInternal(Device device, SComponent component) throws IOException {
        // it is guaranteed to be called at the end of the page by SFrame/CmsFrameCG
        final SDragAndDropManager manager = (SDragAndDropManager) component;
        ScriptManager.getInstance().addScriptListener(new ScriptListener() {
            public String getScript() {
                StringBuilder builder = AbstractComponentCG.STRING_BUILDER.get();
                builder.setLength(0);

                builder.append("wingS.sdnd.setManager('" + manager.getName() + "');\n");

                Collection<SComponent> dragSources = manager.getDragSources();
                if (dragSources != null) {
                    for (SComponent dragSource : dragSources.toArray(new SComponent[0])) {
                        if (dragSource == null || dragSource.getTransferHandler() == null)
                            continue;

                        builder.append(getAddDragSourceString(manager, dragSource));
                    }
                }

                Collection<SComponent> dropTargets = manager.getDropTargets();
                if (dragSources != null && dropTargets != null) {
                    for (SComponent dropTarget : dropTargets.toArray(new SComponent[0])) {
                        if (dropTarget == null || dropTarget.getTransferHandler() == null)
                            continue;

                        builder.append(getAddDropTargetString(manager, dropTarget));
                    }
                }

                return builder.toString();
            }


            public String getEvent() {
                return null;
            }

            public String getCode() {
                return null;
            }

            public int getPriority() {
                return LOW_PRIORITY;
            }
        });
    }

    public Update getRegistrationUpdate(SComponent component, SDragAndDropManager.DragAndDropRegistrationEvent event) {
        return new RegistrationUpdate(component, event);
    }

    private class RegistrationUpdate extends AbstractUpdate {
        private SDragAndDropManager.DragAndDropRegistrationEvent event;
        private SDragAndDropManager manager;

        public RegistrationUpdate(SComponent component, SDragAndDropManager.DragAndDropRegistrationEvent event) {
            super(component); // act as if this update was from the affected component

            this.manager = (SDragAndDropManager) component;
            this.event = event;
        }

        public Handler getHandler() {
            UpdateHandler handler = new UpdateHandler("runScript");
            String runScriptParam = "";
            switch (event.getEventType()) {
                case ADD_DRAGSOURCE:
                    runScriptParam = getAddDragSourceString(manager, event.getComponent());
                    break;
                case REMOVE_DRAGSOURCE:
                    runScriptParam = getRemoveDragSourceString(event.getComponent());
                    break;
                case ADD_DROPTARGET:
                    runScriptParam = getAddDropTargetString(manager, event.getComponent());
                    break;
                case REMOVE_DROPTARGET:
                    runScriptParam = getRemoveDropTargetString(event.getComponent());
                    break;
            }
            handler.addParameter(runScriptParam);
            return handler;
        }

        public SDragAndDropManager.DragAndDropRegistrationEvent getEvent() {
            return event;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            RegistrationUpdate that = (RegistrationUpdate) o;

            if (event != null ? !event.equals(that.event) : that.event != null) return false;

            return true;
        }

        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (event != null ? event.hashCode() : 0);
            return result;
        }
    }
}
