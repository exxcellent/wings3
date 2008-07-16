package org.wings.session;

import org.wings.*;
import org.wings.plaf.CursorCG;

import javax.swing.*;
import java.util.*;
import java.util.List;
import java.awt.*;

/**
 * SCursor is a class that let's you display additional informations at the cursor position.
 * It consists of 0-n SIcons, which are displayed at the top of the SCursor area and of a SLabel displayed below the
 * SIcons. It offers several methods to add/remove SIcons and to set the label.
 */
public class SCursor extends SComponent {
// TODO: remove Scomponent 
    public static final int HIGHEST_PRIORITY = 0;
    public static final int HIGH_PRIORITY = 50;
    public static final int NORMAL_PRIORITY = 100;
    public static final int LOW_PRIORITY = 200;
    public static final int LOWEST_PRIORITY = 255;
    
    private SLabel label;
    private Map<Integer, List<CategorizedSIcon>> icons;
    private boolean visibility;

    private final static class CategorizedSIcon {
        private String category;
        private SIcon icon;

        public CategorizedSIcon(String category, SIcon icon) {
            this.category = category;
            this.icon = icon;
        }

        public String getCategory() {
            return category;
        }

        public SIcon getIcon() {
            return icon;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CategorizedSIcon that = (CategorizedSIcon) o;

            if (category != null ? !category.equals(that.category) : that.category != null) return false;
            if (icon != null ? !icon.equals(that.icon) : that.icon != null) return false;

            return true;
        }

        public int hashCode() {
            int result;
            result = (category != null ? category.hashCode() : 0);
            result = 31 * result + (icon != null ? icon.hashCode() : 0);
            return result;
        }
    }

    public SCursor() {
        icons = new HashMap<Integer, List<CategorizedSIcon>>();
        
        this.setParentFrame(getSession().getRootFrame());

        this.setAttribute("z-index", "9999");
        this.setAttribute("position", "absolute");
        this.setAttribute("display", "none");       // only the default attributes, they'll be changed without setAttribute
        this.setAttribute("visibility", "hidden");
        this.visibility = false;
        
        //this.setBorder(SBorderFactory.createSLineBorder(Color.BLACK, 3));

        this.setShowCursor(false);
    }

    /**
     * Adds a Icon in category with priority
     * @param category Category of the item
     * @param icon The Icon to add
     * @param priority Priority of the Icon - SCursor.HIGHEST_PRIORITY means show first, LOWEST_PRIORITY means show last.
     */
    public void addIcon(String category, SIcon icon, int priority) {
        List<CategorizedSIcon> list = icons.get(priority);
        if(list == null) {
            list = new LinkedList<CategorizedSIcon>();
        }
        list.add(new CategorizedSIcon(category, icon));

        icons.put(priority, list);

        update(((CursorCG)getCG()).getContentUpdate(this));
    }

    /**
     * Adds a Icon in the 'default' category with the given priority
     * @param icon
     * @param priority
     */
    public void addIcon(SIcon icon, int priority) {
        this.addIcon("default", icon, priority);
    }

    /**
     * Sets the visibility of the cursor
     * @param visible
     */
    public void setShowCursor(boolean visible) {
        update(((CursorCG)getCG()).getVisibilityUpdate(this, visible));

        this.visibility = visible;
    }

    private boolean isEmpty() {
        return getIconsByPriority().size() == 0 && label == null;
    }

    /**
     * Hides the cursor, if there are neither icons nor a label to display.
     */
    public void hideCursorIfPossible() {
        if(isEmpty()) {
            setShowCursor(false);
        }
    }

    public boolean isShown() {
        return this.visibility;
    }

    /**
     * Removes a Icon from a category - if icon is null, all icons from that category are removed
     * @param category
     * @param icon
     */
    public void removeIcon(String category, SIcon icon) {
        for(int i=0; i<=255; ++i) {
            List<CategorizedSIcon> list = icons.get(i);
            if(list == null)
                continue;

            list.remove(new CategorizedSIcon(category, icon));
        }

        update(((CursorCG)getCG()).getContentUpdate(this));
    }

    /**
     * Removes all icons from the given category
     * @param category
     */
    public void removeIcons(String category) {
        for(int i=0; i<=255; ++i) {
            List<CategorizedSIcon> list = icons.get(i);
            if(list == null)
                continue;

            List<SIcon> tempList = new LinkedList<SIcon>();
            for(CategorizedSIcon icon : list) {
                if(icon.getCategory().equals(category)) {
                    tempList.add(icon.getIcon());
                }
            }

            for(SIcon icon : tempList) {
                removeIcon(category, icon);
            }
        }

        update(((CursorCG)getCG()).getContentUpdate(this));
    }

    /**
     * Sets a label to be displayed
     * @param label
     */
    public void setLabel(SLabel label) {
        this.label = label;

        update(((CursorCG)getCG()).getContentUpdate(this));
    }

    /**
     * Returns the currently set label
     * @return
     */
    public SLabel getLabel() {
        return label;
    }

    /**
     * Returns the icons sorted by priority (highest priority first)
     * @return
     */
    public List<SIcon> getIconsByPriority() {
        List<SIcon> retList = new LinkedList<SIcon>();
        for(int i=0; i<=255; ++i) {
            List<CategorizedSIcon> list = icons.get(i);
            if(list == null)
                continue;

            for(CategorizedSIcon icon: list) {
                retList.add(icon.getIcon());
            }
        }

        return retList;
    }
}
