package org.wingx.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;
import org.wings.SComponent;

/**
 * Helper class to manage component styles (CSS class names).
 * @author leon
 */
public class StyleNameBuilder implements Iterable<String> {

    private LinkedList<String> styles = new LinkedList<String>();

    public StyleNameBuilder() {
    }

    /**
     * Creates a new instance.
     * @param style a CSS conforming class name (different classes are separated by whitespace).
     */
    public StyleNameBuilder(String style) {
        if (style == null || style.trim().length() == 0) {
            return;
        }
        StringTokenizer tok = new StringTokenizer(style, " ");
        while (tok.hasMoreTokens()) {
            styles.add(tok.nextToken());
        }
    }

    /**
     * Creates a new instance based on the style the component given component currently has.
     * @param component the component whose style will be taken as a base for the current style builder.
     */
    public StyleNameBuilder(SComponent component) {
        this(component.getStyle());
    }

    /**
     * Adds a new style  (CSS class name) to the list of styles.
     * @param styleName the style (CSS class name) to be added.
     * @return this instance.
     */
    public StyleNameBuilder addStyle(String styleName) {
        if (styleName == null || styleName.trim().length() == 0) {
            return this;
        }
        styleName = styleName.trim();
        if (styles.contains(styleName)) {
            return this;
        }
        styles.add(styleName);
        return this;
    }

    /**
     * Removes a style (CSS class name) from the list of styles.
     * @param styleName the style (CSS class name) to be removed.
     * @return true if the style name was successfully removed.
     */
    public boolean removeStyle(String styleName) {
        if (styleName == null || styleName.trim().length() == 0) {
            return false;
        }
        styleName = styleName.trim();
        return styles.remove(styleName);
    }

    /**
     * Returns a CSS conforming string containing all styles currently held by
     * this style builder.
     * @return a CSS conforming string containing all styles currently held by
     */
    public String toStyleName() {
        StringBuilder sb = new StringBuilder();
        for (String style : styles) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(style);
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return toStyleName();
    }

    /**
     * Returns an iterator iterating over all style names. It's purpose is to support
     * the enhanced foreach loop from Java 5.
     * @return an iterator iterating over all style names.
     */
    public Iterator<String> iterator() {
        return styles.iterator();
    }

    /**
     * Applies the style of this style builder to the given component.
     * @param component the component which will get the given style.
     */
    public void applyTo(SComponent component) {
        component.setStyle(toStyleName());
    }

}
