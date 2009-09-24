package org.wingx;

import java.awt.Color;
import org.wings.*;
import org.wings.style.CSSAttributeSet;
import org.wings.style.CSSProperty;
import org.wings.style.CSSStyle;
import org.wings.style.CSSStyleSheet;
import org.wings.style.Selector;
import org.wings.style.Style;

public class XDivision
    extends SContainer
    implements LowLevelEventListener
{
    String title;
    SIcon icon;
    
    /**
     * Is the XDivision shaded?
     */
    boolean shaded;
    
    /**
     * Is the title clickable? Default is false.
     */
    protected boolean isTitleClickable = false;

    public static final Selector SELECTOR_TITLE = new Selector("xdiv.title");
    
    /**
     * Creates a XDivision instance with the specified LayoutManager
     * @param l the LayoutManager
     */
    public XDivision(SLayoutManager l) {
        super(l);
    }

    /**
     * Creates a XDivision instance
     */
    public XDivision() {
    }

    /**
     * Returns the title of the XDivision.
     * @return String the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the XDivision.
     * @param title the title
     */
    public void setTitle(String title) {
        String oldVal = this.title;
        reloadIfChange(this.title, title);
        this.title = title;
        propertyChangeSupport.firePropertyChange("title", oldVal, this.title);
    }
    
    /**
     * Sets the title-font of the XDivision.
     * @param titleFont the font for the title
     */
    public void setTitleFont( org.wings.SFont titleFont) {
        SFont oldVal = this.getTitleFont();
        CSSAttributeSet attributes = CSSStyleSheet.getAttributes(titleFont);
        Style style = getDynamicStyle(SELECTOR_TITLE);
        if (style == null) {
            addDynamicStyle(new CSSStyle(SELECTOR_TITLE, attributes));
        }
        else {
            style.remove(CSSProperty.FONT);
            style.remove(CSSProperty.FONT_FAMILY);
            style.remove(CSSProperty.FONT_SIZE);
            style.remove(CSSProperty.FONT_STYLE);
            style.remove(CSSProperty.FONT_WEIGHT);
            style.putAll(attributes);
        }
        propertyChangeSupport.firePropertyChange("titleFont", oldVal, this.getTitleFont());
    }
    
    /**
     * Returns the title-font of the XDivision.
     * @return SFont the font for the title
     */
    public SFont getTitleFont() {
        return dynamicStyles == null || dynamicStyles.get(SELECTOR_TITLE) == null ? null : CSSStyleSheet.getFont((CSSAttributeSet) dynamicStyles.get(SELECTOR_TITLE));
    }    
    
    /**
     * Sets the title-color of the XDivision.
     * @param titleColor the color for the title
     */
    public void setTitleColor( Color titleColor ) {
        Color oldVal = this.getTitleColor();
        setAttribute( SELECTOR_TITLE, CSSProperty.COLOR, CSSStyleSheet.getAttribute( titleColor ) );
        propertyChangeSupport.firePropertyChange("titleColor", oldVal, this.getTitleColor());
    }
    
    /**
     * Returns the title-color of the XDivision.
     * @return titleColor the color for the title
     */
    public Color getTitleColor() {
        return dynamicStyles == null || dynamicStyles.get(SELECTOR_TITLE) == null ? null : CSSStyleSheet.getForeground((CSSAttributeSet) dynamicStyles.get(SELECTOR_TITLE));
    }
    
    /**
     * Determines whether or not the title is clickable.
     * @param clickable true if the title is clickable
     */
    public void setTitleClickable( boolean clickable ) {
        boolean oldVal = this.isTitleClickable;
        this.isTitleClickable = clickable;
        propertyChangeSupport.firePropertyChange("titleClickable", oldVal, this.isTitleClickable);
    }
    
    /**
     * Returns true if the title is clickable.
     * @return boolean true if the title is clickable
     */
    public boolean isTitleClickable() {
        return this.isTitleClickable;
    }

    public SIcon getIcon() {
        return icon;
    }

    public void setIcon(SIcon icon) {
        SIcon oldVal = this.icon;
        reloadIfChange(this.icon, icon);
        this.icon = icon;
        propertyChangeSupport.firePropertyChange("icon", oldVal, this.icon);
    }

    /**
     * Returns true if the XDivision is shaded.
     * @return boolean true if the XDivision is shaded
     */
    public boolean isShaded() {
        return shaded;
    }

    /** 
     * Determines whether or not the XDivision is shaded.
     * @param shaded true if the XDivision is shaded
     */
    public void setShaded(boolean shaded) {
        if (this.shaded != shaded) {
            reload();
            this.shaded = shaded;
            propertyChangeSupport.firePropertyChange("shaded", !this.shaded, this.shaded);
            setRecursivelyVisible(isRecursivelyVisible());
        }
    }

    public void processLowLevelEvent(String name, String[] values) {
        if (values.length == 1 && "t".equals(values[0]))
            setShaded(!isShaded());

        /*
        TODO: first focusable component
        if (!shaded && getComponentCount() > 0)
            getComponent(0).requestFocus();
        else
            requestFocus();
        */
    }

    public void fireIntermediateEvents() {
    }

    public boolean isEpochCheckEnabled() {
        return false;
    }

    @Override
    protected boolean isShowingChildren() {
        return !shaded;
    }
}
