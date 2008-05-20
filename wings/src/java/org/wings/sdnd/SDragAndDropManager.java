package org.wings.sdnd;

import org.wings.SComponent;
import org.wings.LowLevelEventListener;

import java.util.Collection;
import java.util.ArrayList;


public class SDragAndDropManager extends SComponent implements LowLevelEventListener {
    private Collection<SComponent> dragSources;
    private Collection<SComponent> dropTargets;


    public SDragAndDropManager() {
        this.dragSources = new ArrayList<SComponent>();
        this.dropTargets = new ArrayList<SComponent>();

        this.setParentFrame(getSession().getRootFrame());
        getSession().getDispatcher().register(this);
    }

    public void addDragSource(SComponent component) {
        this.dragSources.add(component);
        reload();
    }

    public void addDropTarget(SComponent component) {
        this.dropTargets.add(component);
        reload();
    }

    public void removeDragSource(SComponent component) {
        this.dragSources.remove(component);
        reload();
    }

    public void removeDropTarget(SComponent component) {
        this.dropTargets.remove(component);
        reload();
    }

    public Collection<SComponent> getDragSources() {
        return this.dragSources;
    }

    public Collection<SComponent> getDropTargets() {
        return this.dropTargets;
    }

    public void processLowLevelEvent(String name, String[] values) {
        System.out.println("name: " + name + " values[0]: " + values[0]);
    }

    public void fireIntermediateEvents() {

    }

    public boolean isEpochCheckEnabled() {
        return false;
    }

    public enum DnDPhase {
        PREPARE_DRAG,
        DRAG_START,
        WHILE_DRAGGING,
        DROP
    }
}
