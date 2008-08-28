package org.wingx;

import org.wings.*;
import org.wingx.table.TruncatableModel;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.MessageFormat;
import java.util.*;


/**
 * XScrollPane
 *
 * @author jdenzel
 */
public class XScrollPane extends SScrollPane {

	private ExtentComboModel extentModel = new ExtentComboModel(
			new Vector<Integer>(Arrays.asList(8, 10, 12, 14, 16, 18, 20, 22,
					24, 26, 28, 30, 32)
            ));

    protected SComboBox extentCombo = new SComboBox(extentModel);
    protected final XPageScroller pageScroller = new XPageScroller();
    protected final SLabel extentComboLabel = new SLabel();
    protected final SLabel totalLabel = new SLabel();
    private STable tableComponent;
    private String visibleSectionLabel = "{0} .. {1} of {2}";

    public XScrollPane() {
        this(null);
    }

    public XScrollPane(STable tableComponent) {
        this(tableComponent, 10);
    }

    public XScrollPane(STable tableComponent, int verticalExtent) {
        setPreferredSize(SDimension.FULLAREA);
        setVerticalAlignment(SConstants.TOP_ALIGN);

        extentCombo.addActionListener(new ExtentComboActionListener());

        pageScroller.add(totalLabel);
        pageScroller.add(extentComboLabel);
        pageScroller.add(extentCombo);
        pageScroller.add(new SLabel(" "), 1d);

        pageScroller.addAdjustmentListener(new PageAdjustmentListener());
        pageScroller.setExtent(verticalExtent);
        pageScroller.setHorizontalAlignment(SConstants.LEFT_ALIGN);

        setHorizontalScrollBar(pageScroller);
        setHorizontalScrollBarPolicy(SScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        setVerticalScrollBar(null);
        setVerticalScrollBarPolicy(SScrollPane.VERTICAL_SCROLLBAR_NEVER);
        setMode(SScrollPane.MODE_PAGING);

        setHorizontalExtent(20);
        setVerticalExtent(verticalExtent);

        if (tableComponent != null)
            setViewportView(tableComponent);
    }

    /**
     * Sets the horizontal scrollbar.
     *
     * @param sb         the scrollbar that controls the viewport's horizontal view position
     * @param constraint the constraint for the {@link LayoutManager} of this {@link SContainer}.
     *                   The {@link LayoutManager} is per default {@link SScrollPaneLayout}.
     */
    public void setHorizontalScrollBar(Adjustable sb, String constraint) {
        Adjustable oldVal = this.horizontalScrollBar;
        if (horizontalScrollBar != null) {
            if (horizontalScrollBar instanceof SAbstractAdjustable)
                ((SAbstractAdjustable) horizontalScrollBar).setModel(new SDefaultBoundedRangeModel());
            else if (horizontalScrollBar instanceof XPageScroller)
                ((XPageScroller) horizontalScrollBar).setModel(new SDefaultBoundedRangeModel());

            if (horizontalScrollBar instanceof SComponent)
                remove((SComponent) horizontalScrollBar);
        }

        horizontalScrollBar = sb;

        if (horizontalScrollBar != null) {
            if (horizontalScrollBar instanceof SComponent)
                addComponent((SComponent) horizontalScrollBar, constraint, getComponentCount());

            if (horizontalScrollBar instanceof SAbstractAdjustable) {
                SAbstractAdjustable scrollbar = (SAbstractAdjustable) horizontalScrollBar;
                if (scrollbar.getOrientation() == SConstants.HORIZONTAL)
                    scrollbar.setModel(horizontalModel);
                else
                    scrollbar.setModel(verticalModel);
            }
            else if (horizontalScrollBar instanceof XPageScroller) {
                XPageScroller scrollbar = (XPageScroller) horizontalScrollBar;
                if (scrollbar.getOrientation() == SConstants.HORIZONTAL)
                    scrollbar.setModel(horizontalModel);
                else
                    scrollbar.setModel(verticalModel);
            }

            adoptScrollBarVisibility(horizontalScrollBar, horizontalScrollBarPolicy);
        }

        reload();
        propertyChangeSupport.firePropertyChange("horizontalScrollBar", oldVal, this.horizontalScrollBar);
    }

    /**
     * Sets the vertical scrollbar.
     *
     * @param sb         the scrollbar that controls the viewport's vertical view position
     * @param constraint the constraint for the {@link LayoutManager} of this {@link SContainer}.
     *                   The {@link LayoutManager} is per default {@link SScrollPaneLayout}.
     */
    public void setVerticalScrollBar(Adjustable sb, String constraint) {
        Adjustable oldVal = this.verticalScrollBar;
        if (verticalScrollBar != null) {
            if (verticalScrollBar instanceof SAbstractAdjustable)
                ((SAbstractAdjustable) verticalScrollBar).setModel(new SDefaultBoundedRangeModel());
            else if (verticalScrollBar instanceof XPageScroller)
                ((XPageScroller) verticalScrollBar).setModel(new SDefaultBoundedRangeModel());

            if (verticalScrollBar instanceof SComponent)
                remove((SComponent) verticalScrollBar);
        }

        verticalScrollBar = sb;

        if (verticalScrollBar != null) {
            if (verticalScrollBar instanceof SComponent)
                addComponent((SComponent) verticalScrollBar, constraint, getComponentCount());

            if (verticalScrollBar instanceof SAbstractAdjustable) {
                SAbstractAdjustable scrollbar = (SAbstractAdjustable) verticalScrollBar;
                if (scrollbar.getOrientation() == SConstants.HORIZONTAL)
                    scrollbar.setModel(horizontalModel);
                else
                    scrollbar.setModel(verticalModel);
            }
            else if (verticalScrollBar instanceof XPageScroller) {
                XPageScroller scrollbar = (XPageScroller) verticalScrollBar;
                if (scrollbar.getOrientation() == SConstants.HORIZONTAL)
                    scrollbar.setModel(horizontalModel);
                else
                    scrollbar.setModel(verticalModel);
            }

            adoptScrollBarVisibility(verticalScrollBar, verticalScrollBarPolicy);
        }

        reload();
        propertyChangeSupport.firePropertyChange("verticalScrollBar", oldVal, this.verticalScrollBar);
    }

    public void setExtentLabel(String label) {
        extentComboLabel.setText(label);
    }

    public String getVisibleSectionLabel() {
        return visibleSectionLabel;
    }

    public void setVisibleSectionLabel(String visibleSectionLabel) {
        String oldVal = this.visibleSectionLabel;
        this.visibleSectionLabel = visibleSectionLabel;
        propertyChangeSupport.firePropertyChange("visibleSectionLabel", oldVal, this.visibleSectionLabel);
    }

    /**
     * @inheritDoc
     */
    public void setViewportView(SComponent view) {
        if (view == null) {
            throw new NullPointerException();
        }
        if (getScrollable() != null) {
            throw new RuntimeException("error: this component is not reinitializable");
        }
        if (!(view instanceof XTable)) {
            throw new RuntimeException("the inner component must be of type XTable");
        }
        STable oldTableComponent = this.tableComponent;
        tableComponent = (XTable) view;
        tableComponent.setVerticalAlignment(SConstants.TOP_ALIGN);
        propertyChangeSupport.firePropertyChange("tableComponent", oldTableComponent, this.tableComponent);

        super.setViewportView(view);
        refresh();
    }

    public void setExtents(Integer[] extents) {
        extentModel.setExtents(extents);
        reload();
    }

    public void setVerticalExtent(int extent) {
        super.setVerticalExtent(extent);
        extentModel.addExtent(extent);
        extentCombo.setSelectedItem(extent);
    }

    public void addAdjustmentListener(AdjustmentListener al) {
        pageScroller.addAdjustmentListener(al);
    }

    public void refresh() {
        scrollable.getViewportSize().height = verticalExtent;
        refreshTotalLabel();
    }

    private void refreshTotalLabel() {
        if (tableComponent == null)
            return;

        Rectangle viewportSize = tableComponent.getViewportSize();
        if (viewportSize == null) {
            totalLabel.setText(null);
            return;
        }

        int startRow = viewportSize.y;
        int endRow = tableComponent.getRowCount();
        endRow = Math.min(startRow + viewportSize.height, endRow);

        String text;
        if (endRow > 0) {
            TableModel tableModel = tableComponent.getModel();
            int rowCount = tableModel.getRowCount();
            text = MessageFormat.format(visibleSectionLabel, startRow + 1, endRow, rowCount);

            if (tableModel instanceof TruncatableModel) {
                if (((TruncatableModel)tableModel).isTruncated())
                    text += "  (+)";
            }
        }
        else {
            text = null;
        }

        totalLabel.setText(text);
    }

    class PageAdjustmentListener implements AdjustmentListener {
        public void adjustmentValueChanged(AdjustmentEvent e) {
            refreshTotalLabel();
            //pageScroller.reload();
        }
    }

    class ExtentComboActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (tableComponent == null)
                return;
            Integer extent = (Integer) extentCombo.getSelectedItem();
            assert extent != null;
            setVerticalExtent(extent.intValue());
        }
    }

	class ExtentComboModel extends DefaultComboBoxModel
    {
		private Vector<Integer> extents;

		public ExtentComboModel(Vector<Integer> extents) {
			super(extents);
			this.extents = extents;
		}

		public void setExtents(Integer[] extents) {
			this.extents.removeAllElements();
			this.extents.addAll(Arrays.asList(extents));
			if(!addExtent((Integer) getSelectedItem())) {;
				fireContentsChanged(this, -1, -1);
			}
		}

		public boolean addExtent(int extent) {
			if (!extents.contains(extent)) {
				extents.add(extent);
				Collections.sort(extents);
				fireContentsChanged(this, -1, -1);
				return true;
			}
			return false;
		}
	}

    public XPageScroller getPageScroller() {
        return pageScroller;
    }

    public SComboBox getExtentCombo(){
        return extentCombo;
    }    
}
