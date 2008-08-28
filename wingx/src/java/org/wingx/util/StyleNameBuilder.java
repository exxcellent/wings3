package org.wingx.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;

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
     * Adds a new style  (CSS class name) to the list of styles.
     * @param styleName the style (CSS class name) to be added.
     */
    public void addStyle(String styleName) {
        if (styleName == null || styleName.trim().length() == 0) {
            return;
        }
        styleName = styleName.trim();
        if (styles.contains(styleName)) {
            return;
        }
        styles.add(styleName);
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

}
