package org.wingx;

import org.wings.*;
import org.wings.event.SViewportChangeListener;

import java.awt.*;

public class XScrollablePanel
    extends SPanel
    implements Scrollable
{
    private SScrollPane scrollPane;

    public XScrollablePanel() {
        super.setLayout(new SBorderLayout());
    }

    public XScrollablePanel(SComponent comp) {
        this();
        add(comp, SBorderLayout.CENTER);
    }

    public XScrollablePanel(SComponent comp, SDimension preferredSize) {
        this();
        setPreferredSize(preferredSize);
        add(comp, SBorderLayout.CENTER);
    }

    public Rectangle getScrollableViewportSize() {
        return null;
    }

    public void setViewportSize(Rectangle d) {
    }

    public void addViewportChangeListener(SViewportChangeListener l) {
    }

    public void removeViewportChangeListener(SViewportChangeListener l) {
    }

    public Rectangle getViewportSize() {
        return null;
    }

    public Dimension getPreferredExtent() {
        return null;
    }

    public SScrollPane asScrollPane() {
        if (scrollPane == null) {
            scrollPane = new SScrollPane(this);
            scrollPane.setPreferredSize(SDimension.FULLAREA);
            scrollPane.setMode(SScrollPane.MODE_COMPLETE);
        }
        return scrollPane;
    }
}
