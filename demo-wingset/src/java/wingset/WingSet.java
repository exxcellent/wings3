package wingset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wings.Resource;
import org.wings.SBorderLayout;
import org.wings.SCardLayout;
import org.wings.SComponent;
import org.wings.SConstants;
import org.wings.SDimension;
import org.wings.SFrame;
import org.wings.SPanel;
import org.wings.SScrollPane;
import org.wings.STemplateLayout;
import org.wings.STree;
import org.wings.border.SLineBorder;
import org.wings.header.StyleSheetHeader;
import org.wings.plaf.WingSetExample;
import org.wings.plaf.css.Utils;
import org.wings.resource.ReloadResource;
import org.wings.session.ResourceMapper;
import org.wings.session.SessionManager;
import org.wings.style.CSSProperty;
import org.wings.tree.SDefaultTreeCellRenderer;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.awt.Color;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * The root of the WingSet demo application.
 *
 * @author holger engels
 */
public class WingSet {
    /**
     * If true then use {@link wingset.StatisticsTimerTask} to log statistics on a regular basis to a logging file. (Typically a file named
     * wings-statisticsxxxlog placed in jakarta-tomcat/temp directory)
     */
    private static final boolean LOG_STATISTICS_TO_FILE = false;

    private final static Log log = LogFactory.getLog(WingSet.class);

    static {
        if (LOG_STATISTICS_TO_FILE) {
            StatisticsTimerTask.startStatisticsLogging(60);
        }
    }

    /**
     * The root frame of the WingSet application.
     */
    private final SFrame frame = new SFrame("WingSet Demo");

    private final SPanel panel;
    private final STree tree;
    private final SPanel header;
    private final SPanel content;
    private final WingsImage wingsImage;
    private final SCardLayout cards = new SCardLayout();
    private final Map<String, WingSetExample> exampleByName = new HashMap<String, WingSetExample>();

    /**
     * Constructor of the wingS application.
     * <p/>
     * <p>This class is referenced in the <code>web.xml</code> as root entry point for the wingS application. For every new client an new
     * {@link org.wings.session.Session} is created which constructs a new instance of this class.
     */
    public WingSet() {
        wingsImage = new WingsImage();

        WingSetTreeModel treeModel = new WingSetTreeModel();
        tree = new STree(treeModel);
        tree.setName("examples");
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (node == null) {
                    show(wingsImage);
                } else {
                    Object userObject = node.getUserObject();
                    if (userObject instanceof WingSetExample) {
                        WingSetExample wingSetPane = (WingSetExample) userObject;
                        wingSetPane.activateExample();
                        show(wingSetPane.getExample());
                    } else {
                        show(wingsImage);
                    }
                }
            }
        });
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setVerticalAlignment(SConstants.TOP_ALIGN);
        tree.setCellRenderer(new SDefaultTreeCellRenderer() {
            public SComponent getTreeCellRendererComponent(STree tree, Object value, boolean selected, boolean expanded,
                                                           boolean leaf, int row, boolean hasFocus) {
                if (value instanceof DefaultMutableTreeNode ) {
                    Object example = ((DefaultMutableTreeNode)value).getUserObject();
                    if (example instanceof WingSetExample)
                        value = ((WingSetExample)example).getExampleName();
                }
                return super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            }
        });
        expandAllTreeNodes(tree);

        SScrollPane scrollPane = new SScrollPane(tree);
        scrollPane.setMode(SScrollPane.MODE_COMPLETE);
        scrollPane.setPreferredSize(new SDimension("220px", "100%"));
        scrollPane.setVerticalAlignment(SConstants.TOP_ALIGN);
        SLineBorder border = new SLineBorder(new Color(190, 190, 190), 0);
        border.setThickness(1, SConstants.RIGHT);
        scrollPane.setBorder(border);

        header = createHeader();

        content = new SPanel(cards);
        content.setPreferredSize(SDimension.FULLAREA);
        content.add(wingsImage);

        panel = new SPanel(new SBorderLayout());
        panel.setPreferredSize(SDimension.FULLAREA);
        panel.add(header, SBorderLayout.NORTH);
        panel.add(scrollPane, SBorderLayout.WEST);
        panel.add(content, SBorderLayout.CENTER);

        frame.getContentPane().add(panel, SBorderLayout.CENTER);
        frame.getContentPane().setPreferredSize(SDimension.FULLAREA);
        if (!Utils.isMSIE(frame)) {
            frame.getContentPane().setAttribute(CSSProperty.POSITION, "absolute");
            frame.getContentPane().setAttribute(CSSProperty.HEIGHT, "100%");
            frame.getContentPane().setAttribute(CSSProperty.WIDTH, "100%");
        }
        frame.setAttribute(CSSProperty.POSITION, "absolute");
        frame.setAttribute(CSSProperty.HEIGHT, "100%");
        frame.setAttribute(CSSProperty.WIDTH, "100%");

        frame.addHeader(new StyleSheetHeader("../css/wingset2.css"));

        frame.show();

        Enumeration enumeration = ((DefaultMutableTreeNode) treeModel.getRoot()).breadthFirstEnumeration();
        while (enumeration.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumeration.nextElement();
            Object userObject = node.getUserObject();
            if (userObject instanceof WingSetExample) {
                WingSetExample wingSetPane = (WingSetExample) userObject;
                exampleByName.put(wingSetPane.getExampleName(), wingSetPane);
            }
        }

        SessionManager.getSession().setResourceMapper(new ResourceMapper() {
            public Resource mapResource(String url) {
                String name = url.substring(1);
                WingSetExample pane = exampleByName.get(name);
                if (pane != null) {
                    show(pane.getExample());
                    return frame.getDynamicResource(ReloadResource.class);
                }
                return null;
            }
        });
    }
    
    public final void expandAllTreeNodes(STree tree) {
        for (int i = 0; i < tree.getRowCount(); ++i) {
            tree.expandRow(i);
        }
    }

    private void show(SComponent component) {
        if (component.getParent() != content) {
            content.add(component);
        }
        cards.show(component);
    }

    private SPanel createHeader() {
        SPanel header = new SPanel();
        header.setPreferredSize(SDimension.FULLWIDTH);
        header.setStyle("wingset_header");

        try {
            header.setLayout(new STemplateLayout(SessionManager.getSession().
                    getServletContext().getRealPath("/templates/WingSetHeader.thtml")));
        } catch (java.io.IOException ex) {
            log.error("Could not find template file!", ex);
        }

        return header;
    }

}
