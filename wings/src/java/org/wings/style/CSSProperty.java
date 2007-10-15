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

package org.wings.style;

import java.io.Serializable;
import java.util.*;

/**
 * A CSS attribute is a property on any abritriary HTML element which can be set via CSS.
 *
 * You use CSS selectors ({@link Selector}) to define <b>which</b> elements you want to modify and define
 * with CSS properties {@link CSSProperty} <b>what</b> visual property you want to modify.
 *
 * <p>Please refer to <a href="http://www.w3.org/TR/REC-CSS2/selector.html">http://www.w3.org/TR/REC-CSS2/selector.html</a> 
 * for details
 *
 * @author bschmid
 */
public class CSSProperty implements Serializable {

    // intern() Pool for CSS Properties if resolved via valueOf() method
    private final static Hashtable<String, CSSProperty> INSTANCE_POOL = new Hashtable<String, CSSProperty>(100);

    /* These CSSProperty Constants are the complete CSS2 standard for visual
     * media as in the CSS2 spec at http://www.w3.org/TR/REC-CSS2/propidx.html
     */

    public final static CSSProperty BACKGROUND = new CSSProperty("background").intern();

    public final static CSSProperty BACKGROUND_ATTACHMENT = new CSSProperty("background-attachment").intern();

    public final static CSSProperty BACKGROUND_COLOR = new CSSProperty("background-color").intern();

    public final static CSSProperty BACKGROUND_IMAGE = new CSSProperty("background-image").intern();

    public final static CSSProperty BACKGROUND_POSITION = new CSSProperty("background-position").intern();

    public final static CSSProperty BACKGROUND_REPEAT = new CSSProperty("background-repeat").intern();

    public final static CSSProperty BORDER = new CSSProperty("border").intern();

    public final static CSSProperty BORDER_BOTTOM = new CSSProperty("border-bottom").intern();

    public final static CSSProperty BORDER_BOTTOM_COLOR = new CSSProperty("border-bottom-color").intern();

    public final static CSSProperty BORDER_BOTTOM_STYLE = new CSSProperty("border-bottom-style").intern();

    public final static CSSProperty BORDER_BOTTOM_WIDTH = new CSSProperty("border-bottom-width").intern();

    public final static CSSProperty BORDER_COLLAPSE = new CSSProperty("border-collapse").intern();

    public final static CSSProperty BORDER_COLOR = new CSSProperty("border-color").intern();

    public final static CSSProperty BORDER_LEFT = new CSSProperty("border-left").intern();

    public final static CSSProperty BORDER_LEFT_COLOR = new CSSProperty("border-left-color").intern();

    public final static CSSProperty BORDER_LEFT_STYLE = new CSSProperty("border-left-style").intern();

    public final static CSSProperty BORDER_LEFT_WIDTH = new CSSProperty("border-left-width").intern();

    public final static CSSProperty BORDER_RIGHT = new CSSProperty("border-right").intern();

    public final static CSSProperty BORDER_RIGHT_COLOR = new CSSProperty("border-right-color").intern();

    public final static CSSProperty BORDER_RIGHT_STYLE = new CSSProperty("border-right-style").intern();

    public final static CSSProperty BORDER_RIGHT_WIDTH = new CSSProperty("border-right-width").intern();

    public final static CSSProperty BORDER_SPACING = new CSSProperty("border-spacing").intern();

    public final static CSSProperty BORDER_STYLE = new CSSProperty("border-style").intern();

    public final static CSSProperty BORDER_TOP = new CSSProperty("border-top").intern();

    public final static CSSProperty BORDER_TOP_COLOR = new CSSProperty("border-top-color").intern();

    public final static CSSProperty BORDER_TOP_STYLE = new CSSProperty("border-top-style").intern();

    public final static CSSProperty BORDER_TOP_WIDTH = new CSSProperty("border-top-width").intern();

    public final static CSSProperty BORDER_WIDTH = new CSSProperty("border-width").intern();

    public final static CSSProperty BOTTOM = new CSSProperty("bottom").intern();

    public final static CSSProperty CAPTION_SIDE = new CSSProperty("caption-side").intern();

    public final static CSSProperty CLEAR = new CSSProperty("clear").intern();

    public final static CSSProperty COLOR = new CSSProperty("color").intern();

    /* This is just used for :before and :after pseudo elements. We don't have those
     * public final static CSSProperty CONTENT = new CSSProperty("content");
     */

    public final static CSSProperty COUNTER_INCREMENT = new CSSProperty("counter-increment").intern();

    public final static CSSProperty COUNTER_RESET = new CSSProperty("counter-reset").intern();

    public final static CSSProperty CURSOR = new CSSProperty("cursor").intern();

    public final static CSSProperty DIRECTION = new CSSProperty("direction").intern();

    public final static CSSProperty DISPLAY = new CSSProperty("display").intern();

    public final static CSSProperty EMPTY_CELLS = new CSSProperty("empty-cells").intern();

    public final static CSSProperty FLOAT = new CSSProperty("float").intern();

    public final static CSSProperty FONT = new CSSProperty("font").intern();

    public final static CSSProperty FONT_FAMILY = new CSSProperty("font-family").intern();

    public final static CSSProperty FONT_SIZE = new CSSProperty("font-size").intern();

    public final static CSSProperty FONT_SIZE_ADJUST = new CSSProperty("font-size-adjust").intern();

    public final static CSSProperty FONT_STRETCH = new CSSProperty("font-stretch").intern();

    public final static CSSProperty FONT_STYLE = new CSSProperty("font-style").intern();

    public final static CSSProperty FONT_VARIANT = new CSSProperty("font-variant").intern();

    public final static CSSProperty FONT_WEIGHT = new CSSProperty("font-weight").intern();

    public final static CSSProperty HEIGHT = new CSSProperty("height").intern();

    public final static CSSProperty LEFT = new CSSProperty("left").intern();

    public final static CSSProperty LETTER_SPACING = new CSSProperty("letter-spacing").intern();

    public final static CSSProperty LINE_HEIGHT = new CSSProperty("line-height").intern();

    public final static CSSProperty LIST_STYLE = new CSSProperty("list-style").intern();

    public final static CSSProperty LIST_STYLE_IMAGE = new CSSProperty("list-style-image").intern();

    public final static CSSProperty LIST_STYLE_POSITION = new CSSProperty("list-style-position").intern();

    public final static CSSProperty LIST_STYLE_TYPE = new CSSProperty("list-style-type").intern();

    public final static CSSProperty MARGIN = new CSSProperty("margin").intern();

    public final static CSSProperty MARGIN_TOP = new CSSProperty("margin-top").intern();

    public final static CSSProperty MARGIN_BOTTOM = new CSSProperty("margin-bottom").intern();

    public final static CSSProperty MARGIN_LEFT = new CSSProperty("margin-left").intern();

    public final static CSSProperty MARGIN_RIGHT = new CSSProperty("margin-right").intern();

    // only for paged output mediums, we don't need it at the moment
    //public final static CSSProperty MARKS = new CSSProperty("marks");

    public final static CSSProperty MAX_HEIGHT = new CSSProperty("max-height").intern();

    public final static CSSProperty MAX_WIDTH = new CSSProperty("max-width").intern();

    public final static CSSProperty MIN_HEIGHT = new CSSProperty("min-height").intern();

    public final static CSSProperty MIN_WIDTH = new CSSProperty("min-width").intern();

    // only for paged output mediums, we don't need it at the moment
    //public final static CSSProperty ORPHANS = new CSSProperty("orphans");

    public final static CSSProperty OUTLINE = new CSSProperty("outline").intern();

    public final static CSSProperty OUTLINE_COLOR = new CSSProperty("outline-color").intern();

    public final static CSSProperty OUTLINE_STYLE = new CSSProperty("outline-style").intern();

    public final static CSSProperty OUTLINE_WIDTH = new CSSProperty("outline-width").intern();

    public final static CSSProperty OVERFLOW = new CSSProperty("overflow").intern();

    public final static CSSProperty PADDING = new CSSProperty("padding").intern();

    public final static CSSProperty PADDING_BOTTOM = new CSSProperty("padding-bottom").intern();

    public final static CSSProperty PADDING_LEFT = new CSSProperty("padding-left").intern();

    public final static CSSProperty PADDING_RIGHT = new CSSProperty("padding-right").intern();

    public final static CSSProperty PADDING_TOP = new CSSProperty("padding-top").intern();

    // only for paged output mediums, we don't need it at the moment
    /*
     public final static CSSProperty PAGE = new CSSProperty("page");

     public final static CSSProperty PAGE_BREAK_AFTER = new CSSProperty("page-break-after");

     public final static CSSProperty PAGE_BREAK_BEFORE = new CSSProperty("page-break-before");

     public final static CSSProperty PAGE_BREAK_INSIDE = new CSSProperty("page-break-inside");
     */

    public final static CSSProperty POSITION = new CSSProperty("position").intern();

    public final static CSSProperty QUOTES = new CSSProperty("quotes").intern();

    public final static CSSProperty RIGHT = new CSSProperty("right").intern();

    // only for paged output mediums, we don't need it at the moment
    //public final static CSSProperty SIZE = new CSSProperty("size");

    public final static CSSProperty TABLE_LAYOUT = new CSSProperty("table-layout").intern();

    public final static CSSProperty TEXT_ALIGN = new CSSProperty("text-align").intern();

    public final static CSSProperty TEXT_DECORATION = new CSSProperty("text-decoration").intern();

    public final static CSSProperty TEXT_INDENT = new CSSProperty("text-indent").intern();

    public final static CSSProperty TEXT_SHADOW = new CSSProperty("text-shadow").intern();

    public final static CSSProperty TEXT_TRANSFORM = new CSSProperty("text-transform").intern();

    public final static CSSProperty TOP = new CSSProperty("top").intern();

    public final static CSSProperty UNICODE_BIDI = new CSSProperty("unicode-bidi").intern();

    public final static CSSProperty VERTICAL_ALIGN = new CSSProperty("vertical-align").intern();

    public final static CSSProperty VISIBILITY = new CSSProperty("visibility").intern();

    public final static CSSProperty WHITE_SPACE = new CSSProperty("white-space").intern();

    // only for paged output mediums, we don't need it at the moment
    //public final static CSSProperty WIDOWS = new CSSProperty("widows");

    public final static CSSProperty WIDTH = new CSSProperty("width").intern();

    public final static CSSProperty WORD_SPACING = new CSSProperty("word-spacing").intern();

    public final static CSSProperty Z_INDEX = new CSSProperty("z-index").intern();

    /**
     * List of CSS properties which may not be applied to SComponents but their borders.
     */
    public static final Set<CSSProperty> BORDER_PROPERTIES = Collections.unmodifiableSet(new HashSet<CSSProperty>(Arrays.asList(
            CSSProperty.BORDER,
            //CSSProperty.BORDER_COLLAPSE,
            CSSProperty.BORDER_COLOR,
            //CSSProperty.BORDER_SPACING,
            CSSProperty.BORDER_STYLE, CSSProperty.BORDER_WIDTH, CSSProperty.BORDER_BOTTOM,
            CSSProperty.BORDER_BOTTOM_COLOR, CSSProperty.BORDER_BOTTOM_STYLE, CSSProperty.BORDER_BOTTOM_WIDTH,
            CSSProperty.BORDER_LEFT, CSSProperty.BORDER_LEFT_COLOR, CSSProperty.BORDER_LEFT_STYLE,
            CSSProperty.BORDER_LEFT_WIDTH, CSSProperty.BORDER_RIGHT, CSSProperty.BORDER_RIGHT_COLOR,
            CSSProperty.BORDER_RIGHT_STYLE, CSSProperty.BORDER_RIGHT_WIDTH, CSSProperty.BORDER_TOP,
            CSSProperty.BORDER_TOP_COLOR, CSSProperty.BORDER_TOP_STYLE, CSSProperty.BORDER_TOP_WIDTH,
            CSSProperty.PADDING, CSSProperty.PADDING_TOP, CSSProperty.PADDING_RIGHT,
            CSSProperty.PADDING_BOTTOM, CSSProperty.PADDING_LEFT)));

    private final String name;

    public CSSProperty(String cssAttributeName) {
        // CSS properties are CASE INSENSITIVE!
        this.name = cssAttributeName.toLowerCase();
    }

    /**
     * Retrieve the CSS property with the given attribute name.
     * @param cssPropertyName The css attribute name.
     * @return A pooled CSS instance if this is a known css property or new instance.
     */
    public static CSSProperty valueOf(String cssPropertyName) {
        CSSProperty instance = INSTANCE_POOL.get(cssPropertyName);
        if (instance != null) {
            return instance;
        } else {
            return new CSSProperty(cssPropertyName);
        }
    }

    /**
     * @return The CSS attribute name.
     */
    public String getName() {
        return name;
    }

    /**
     * When the intern method is invoked, if the pool already contains a
     * property equal to this <code>CSSProperty</code> object as determined by
     * the {@link #equals(Object)} method, then the object from the pool is
     * returned. Otherwise, this <code>CSSProperty</code> object is added to the
     * pool and a reference to this <code>CSSProperty</code> object is returned.

     * @param property The CSS Property too pool.
     * @return A pooled object instance.
     */
    public CSSProperty intern() {
        if (INSTANCE_POOL.containsKey(name)) {
            return INSTANCE_POOL.get(name);
        } else {
            INSTANCE_POOL.put(name, this);
            return this;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof CSSProperty))
            return false;
        final CSSProperty cssProperty = (CSSProperty) o;

        // CSS properties are CASE INSENSITIVE! Enfored in constructor
        // return (name.equalsIgnoreCase(cssProperty.name));
        return (name.equals(cssProperty.name));
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return getName();
    }

}
