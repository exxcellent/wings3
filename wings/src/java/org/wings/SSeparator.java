package org.wings;

/**
 * 
 */
public class SSeparator extends SComponent {

    private static final long serialVersionUID = -6257881596567546337L;
    
    private static final SDimension initialDimensionH = new SDimension("100%", "1px");
    private static final SDimension initialDimensionV = new SDimension("1px", "17px");
    
    private int orientation = -1;

    public SSeparator() {
        this(SConstants.HORIZONTAL);
    }

    public SSeparator(final int orientation) {
        setOrientation(orientation);
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(final int orientation) {
        if (isDifferent(this.orientation, orientation)) {
            switch (orientation) {
            case SConstants.HORIZONTAL:
                if (getPreferredSize() == null) {
                    setPreferredSize(initialDimensionH);
                }
                addStyle("horizontal");
                break;
            case SConstants.VERTICAL:
                if (getPreferredSize() == null) {
                    setPreferredSize(initialDimensionV);
                }
                addStyle("vertical");
                break;
            default:
                throw new IllegalArgumentException("Orientation must be one of: HORIZONTAL, VERTICAL");
            }
            this.orientation = orientation;
            reload();
        }
        
    }

}
