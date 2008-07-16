package wingset;

import org.wings.*;
import org.wings.sdnd.DefaultTransferable;
import org.wings.style.CSSProperty;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;


public class NewDragAndDropExample extends WingSetPane {
    private final static Log log = LogFactory.getLog(NewDragAndDropExample.class);

    private static final SURLIcon ICON_BEE1 = new SURLIcon("../icons/bee_1.jpg", 100, 100);
    private static final SURLIcon ICON_BEE2 = new SURLIcon("../icons/bee_2.jpg", 100, 100);
    private static final SURLIcon ICON_BEE3 = new SURLIcon("../icons/bee_3.jpg", 100, 100);
    private static final SURLIcon ICON_BEE4 = new SURLIcon("../icons/bee_4.jpg", 100, 100);
    private static final SURLIcon ICON_BEE5 = new SURLIcon("../icons/bee_5.jpg", 100, 100);
    private static final SURLIcon ICON_BEE6 = new SURLIcon("../icons/bee_6.jpg", 100, 100);
    private static final SURLIcon ICON_BEE7 = new SURLIcon("../icons/bee_7.jpg", 100, 100);
    private static final SURLIcon ICON_BEE8 = new SURLIcon("../icons/bee_8.jpg", 100, 100);
    private static final SURLIcon ICON_BEE9 = new SURLIcon("../icons/bee_9.jpg", 100, 100);
    /*
    private static final SURLIcon ICON_BEE1_SMALL = new SURLIcon("../icons/bee_1.jpg", 30, 30);
    private static final SURLIcon ICON_BEE2_SMALL = new SURLIcon("../icons/bee_2.jpg", 30, 30);
    private static final SURLIcon ICON_BEE3_SMALL = new SURLIcon("../icons/bee_3.jpg", 30, 30);
    private static final SURLIcon ICON_BEE4_SMALL = new SURLIcon("../icons/bee_4.jpg", 30, 30);
    private static final SURLIcon ICON_BEE5_SMALL = new SURLIcon("../icons/bee_5.jpg", 30, 30);
    private static final SURLIcon ICON_BEE6_SMALL = new SURLIcon("../icons/bee_6.jpg", 30, 30);
    private static final SURLIcon ICON_BEE7_SMALL = new SURLIcon("../icons/bee_7.jpg", 30, 30);
    private static final SURLIcon ICON_BEE8_SMALL = new SURLIcon("../icons/bee_8.jpg", 30, 30);
    private static final SURLIcon ICON_BEE9_SMALL = new SURLIcon("../icons/bee_9.jpg", 30, 30);
                                       */
    private final SLabel statusLabel = new SLabel();
    private ComponentControls controls;

    private SDragDropLabel[] puzzleIcons = new SDragDropLabel[] {
            new SDragDropLabel(ICON_BEE1),
            new SDragDropLabel(ICON_BEE2),
            new SDragDropLabel(ICON_BEE3),
            new SDragDropLabel(ICON_BEE4),
            new SDragDropLabel(ICON_BEE5),
            new SDragDropLabel(ICON_BEE6),
            new SDragDropLabel(ICON_BEE7),
            new SDragDropLabel(ICON_BEE8),
            new SDragDropLabel(ICON_BEE9)
    };

    private SIcon[] puzzleCorrectIcons = new SIcon[] {
            ICON_BEE1,
            ICON_BEE2,
            ICON_BEE3,
            ICON_BEE4,
            ICON_BEE5,
            ICON_BEE6,
            ICON_BEE7,
            ICON_BEE8,
            ICON_BEE9
    };

    protected SComponent createControls() {
        controls = new Controls();
        return controls;
    }

    protected SComponent createExample() {
        final SPanel container = new SPanel(new SBoxLayout(SBoxLayout.HORIZONTAL));
        final SPanel puzzleContainer = new SPanel(new SGridLayout(2, 1, 0, 20));

        resetPuzzle();

        // build the puzzle
        SGridLayout gridLayout = new SGridLayout(3,3);
        gridLayout.setBorder(1);
        final SPanel puzzle = new SPanel(gridLayout);
        for (int i = 0; i < puzzleIcons.length; i++) {
            puzzle.add(puzzleIcons[i]);
        }

        puzzleContainer.add(puzzle);

        statusLabel.setPreferredSize(new SDimension("400px", null));

        container.add(puzzleContainer);
        container.add(statusLabel);

        return container;
    }

    protected void resetPuzzle() {
        statusLabel.setText("Try to solve the puzzle by swapping the pieces via drag-and-drop");
        statusLabel.setAttribute(CSSProperty.FONT_WEIGHT, "normal");
        statusLabel.setAttribute(CSSProperty.COLOR, "black");


        for (int i = 0; i < puzzleIcons.length; i++) {
            puzzleIcons[i].setTransferHandler(new DragDropLabelTransferHandler());
            puzzleIcons[i].setDragEnabled(true);
            puzzleIcons[i].setDropMode(DropMode.USE_SELECTION);
        }

        for(int i=0; i<20; ++i) {
            int swap1 = (int)(Math.random()*puzzleIcons.length);
            int swap2 = (int)(Math.random()*puzzleIcons.length);
            SIcon temp = puzzleIcons[swap1].getIcon();
            puzzleIcons[swap1].setIcon(puzzleIcons[swap2].getIcon());
            puzzleIcons[swap2].setIcon(temp);
        }

    }

    protected void updateStatus() {
        int correctIcons = 0;
        for(int i=0; i<puzzleIcons.length; ++i) {
            if(puzzleIcons[i].getIcon().getURL().equals(puzzleCorrectIcons[i].getURL())) {
                correctIcons++;
            }
        }

        if(correctIcons < puzzleIcons.length) {
            statusLabel.setText("You got " + correctIcons + " pieces correct");
        } else {
            statusLabel.setText("Look, it's got wingS ;) !");
            statusLabel.setAttribute(CSSProperty.FONT_WEIGHT, "600");
            statusLabel.setAttribute(CSSProperty.COLOR, "red");

            for (int i = 0; i < puzzleIcons.length; i++) {
                puzzleIcons[i].setDragEnabled(false);
            }
        }
    }

    private class Controls extends ComponentControls {
        public Controls() {
            globalControls.setVisible(false);

            final SButton resetButton = new SButton("Reset");
            resetButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    resetPuzzle();
                }
            });
            resetButton.setHorizontalAlignment(SConstants.LEFT_ALIGN);

            addControl(resetButton);
        }
    }

    private static DataFlavor supportedDataFlavor;

    static {
        try {
            supportedDataFlavor = new DataFlavor("image/x-java-icon; class=org.wings.SIcon");
        } catch(ClassNotFoundException e){
            e.printStackTrace();
        }
    };
    
    protected class IconTransferable extends DefaultTransferable {
        private SIcon icon;
        private SIcon oldIcon;
        private SComponent source;

        public IconTransferable(SComponent source, SIcon icon) {
            super(new DataFlavor[] { supportedDataFlavor });
            this.icon = icon;
            this.source = source;
        }

        public SComponent getSource() {
            return this.source;
        }

        public void setOldIcon(SIcon icon) {
            this.oldIcon = icon;
        }

        public SIcon getOldIcon() {
            return this.oldIcon;
        }

        protected Object getDataForClass(DataFlavor df, Class<?> cls) {
            if(df.equals(supportedDataFlavor))
                return icon;
            return null;
        }
    }

    protected class DragDropLabelTransferHandler extends STransferHandler {
        public DragDropLabelTransferHandler() {
            super(null);
        }

        protected Transferable createTransferable(SComponent component) {
            return new IconTransferable(component, ((SDragDropLabel)component).getIcon());
        }

        public boolean canImport(SComponent component, DataFlavor[] transferFlavors) {
            for(DataFlavor flavor:transferFlavors) {
                if(flavor.equals(supportedDataFlavor))
                    return true;
            }

            return false;
        }

        public void exportDone(SComponent source, Transferable data, int action) {
            if(action == MOVE) {
                if(data instanceof IconTransferable) {
                    SIcon oldIcon = ((IconTransferable)data).getOldIcon();
                    Object dragSource = ((IconTransferable)data).getSource();
                    if(dragSource instanceof SDragDropLabel)
                        ((SDragDropLabel)dragSource).setIcon(oldIcon);

                    updateStatus();
                }

            }
        }

        public boolean importData(SComponent component, Transferable transferable) {
            try {
                SIcon icon = (SIcon)transferable.getTransferData(supportedDataFlavor);
                SDragDropLabel label = (SDragDropLabel)component;
                ((IconTransferable)transferable).setOldIcon(label.getIcon());
                label.setIcon(icon);
            } catch (UnsupportedFlavorException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            return false;
        }

        public SIcon getVisualRepresentation(Transferable transferable) {
            if(transferable instanceof IconTransferable)
                try {
                    return (SIcon)(((IconTransferable)transferable).getTransferData(supportedDataFlavor));
                } catch (UnsupportedFlavorException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            return null;
        }

        public SLabel getVisualRepresentationLabel(Transferable transferable) {
            SLabel label = super.getVisualRepresentationLabel(transferable);
            label.setAttribute("opacity", ".7");
            return label;
        }

        public int getSourceActions(SComponent component) {
            return MOVE;
        }
    }

    private class SDragDropLabel extends SLabel {
        private boolean dragEnabled;

        public SDragDropLabel(SURLIcon icon) {
            super();
            
            setIcon(icon);
        }

        public boolean getDragEnabled() {
            return this.dragEnabled;
        }
        
        public void setDragEnabled(boolean dragEnabled) {
            if(dragEnabled)
                getSession().getSDragAndDropManager().addDragSource(this);
            else
                getSession().getSDragAndDropManager().removeDragSource(this);

            this.dragEnabled = dragEnabled;
        }

        public void setDropMode(DropMode dropMode) {
            getSession().getSDragAndDropManager().addDropTarget(this);
        }

    }

}
