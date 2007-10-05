package logconfig;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.wings.SAnchor;
import org.wings.SBorderLayout;
import org.wings.SBoxLayout;
import org.wings.SButton;
import org.wings.SButtonGroup;
import org.wings.SCheckBox;
import org.wings.SComboBox;
import org.wings.SComponent;
import org.wings.SConstants;
import org.wings.SContainer;
import org.wings.SDimension;
import org.wings.SForm;
import org.wings.SFrame;
import org.wings.SGridBagLayout;
import org.wings.SGridLayout;
import org.wings.SLabel;
import org.wings.SList;
import org.wings.SPanel;
import org.wings.SRadioButton;
import org.wings.SScrollPane;
import org.wings.SSpacer;
import org.wings.STemplateLayout;
import org.wings.STextField;
import org.wings.STree;
import org.wings.SURLIcon;
import org.wings.event.SDocumentEvent;
import org.wings.event.SDocumentListener;
import org.wings.header.StyleSheetHeader;
import org.wings.plaf.css.script.OnPageRenderedScript;
import org.wings.script.JavaScriptEvent;
import org.wings.script.JavaScriptListener;
import org.wings.session.ScriptManager;
import org.wings.session.Session;
import org.wings.session.SessionManager;
import org.wings.style.CSSProperty;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

public class LogConfig {
    private static final Log log = LogFactory.getLog(LogConfig.class);
    
    private static final boolean SHOW_DEBUGGING_PANEL = true;
    
    private static final SURLIcon INSERT_IMG = new SURLIcon("../images/insert.gif");
    private static final SURLIcon UPDATE_IMG = new SURLIcon("../images/update.gif");
    private static final SDimension BU_DIM = new SDimension(100, SDimension.AUTO_INT);
    private static final SDimension IN_DIM = SDimension.FULLWIDTH;

    private SFrame fr_frame;

    private String logConfigDir;
    private String log4jXmlPath;
    private String log4jDtdPath;

    private Document document;
    private DomModel treeModel;
    private List<String> listModel;
    private Element rootNode;
    private Node selectedNode;

    private SForm fo_form;
    private STree tr_domTree;
    private SScrollPane sp_tree;
    private SButtonGroup bg_insertOrUpdate;
    private SRadioButton rb_insertNode;
    private SRadioButton rb_updateNode;
    private STextField tf_editCategoryName;
    private STextField tf_editPriorityValue;
    private SComboBox cb_editAdditivityFlag;
    private SList li_editAppenderRef;
    private SLabel la_status;
    private SButton bu_saveNode;
    private SButton bu_deleteNode;
    private SButton bu_commitChanges;

    public LogConfig() {
        final Session session = SessionManager.getSession();
        logConfigDir = session.getServletContext().getRealPath("/");
        log4jXmlPath = logConfigDir + session.getProperty("log4j.xml.path").toString();
        log4jDtdPath = logConfigDir + session.getProperty("log4j.dtd.path").toString();
        
        DOMConfigurator.configureAndWatch(log4jXmlPath, 3000);

        try {
            SAXReader reader = new SAXReader(false);
            reader.setEntityResolver(new EntityResolver() {
                public InputSource resolveEntity(String publicId, String systemId) {
                    if (systemId != null && systemId.endsWith("log4j.dtd")) {
                        try {
                            return new InputSource(new FileInputStream(log4jDtdPath));
                        } catch (FileNotFoundException ex) {
                            log.error("Could not load 'log4j.dtd'.", ex);
                        }
                    }
                    return null;
                }
            });
            reader.setIgnoreComments(true);
            document = reader.read(new FileInputStream(log4jXmlPath));
            rootNode = document.getRootElement();
            treeModel = new DomModel(rootNode);
        } catch (Exception ex) {
            log.error("Could not load 'log4j.xml'.", ex);
        }

        SLabel la_header = new SLabel(new SURLIcon("../images/header.jpg"));
        la_header.setPreferredSize(SDimension.FULLWIDTH);
        la_header.setStyle(la_header.getStyle() + " la_header");

        la_status = new SLabel();
        la_status.setStyle(la_status.getStyle() + " la_status");
        la_status.setPreferredSize(SDimension.FULLWIDTH);
        resetStatusLabel();

        SPanel pa_application = new SPanel(new SBorderLayout());
        pa_application.setStyle(pa_application.getStyle() + " pa_application");
        pa_application.setPreferredSize(new SDimension(730, 540));
        pa_application.add(la_header, SBorderLayout.NORTH);
        pa_application.add(document == null ? createErrorPanel() : createMainPanel(), SBorderLayout.CENTER);
        pa_application.add(la_status, SBorderLayout.SOUTH);

        fr_frame = new SFrame("Log4J - Configurator");
        fo_form = (SForm) fr_frame.getContentPane();
        
        SContainer pa_content = fr_frame.getContentPane();
        pa_content.setStyle(pa_content.getStyle() + " pa_content");
        try {
            java.net.URL templateURL = SessionManager.getSession()
                .getServletContext().getResource("/templates/main.thtml");
            if (templateURL == null) {
                pa_content.add(new SLabel("Could not find template file!"));
                return;
            }
            pa_content.setLayout(new STemplateLayout(templateURL));
        } catch (java.io.IOException ex) {
            log.error("Could not find template file!", ex);
        }
        pa_content.add(pa_application, "application");
        if (document != null && SHOW_DEBUGGING_PANEL) {
            SPanel pa_debugging = createDebuggingPanel();
            pa_debugging.setPreferredSize(new SDimension(-1, 540));
            pa_content.add(pa_debugging, "debugging");
        }
        
        InputMap inputMap = new InputMap();
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F12, KeyEvent.SHIFT_DOWN_MASK, false), "Shift F12");
        ActionMap actionMap = new ActionMap();
        actionMap.put("Shift F12", new AbstractAction() {
            private static final long serialVersionUID = 573765095316826768L;

            public void actionPerformed(ActionEvent e) {
                ScriptManager.getInstance().addScriptListener(
                        new OnPageRenderedScript("wingS.ajax.toggleDebugView()"));
            }
        });
        pa_content.setInputMap(SComponent.WHEN_IN_FOCUSED_FRAME, inputMap);
        pa_content.setActionMap(actionMap);

        fr_frame.addHeader(new StyleSheetHeader("../css/custom.css"));
        fr_frame.setVisible(true);

        testSomething(false);
    }

    private SPanel createErrorPanel() {
        String msg = "<html><b>ERROR >>> Unable to load log4j.xml!</b><br />" +
            "Please check the paths in your web.xml.";
        SLabel la_error = new SLabel(msg);
        SPanel pa_error = new SPanel();
        pa_error.setStyle(pa_error.getStyle() + " pa_error");
        pa_error.setPreferredSize(SDimension.FULLAREA);
        pa_error.setHorizontalAlignment(SConstants.CENTER_ALIGN);
        pa_error.add(la_error);
        return pa_error;
    }

    private SPanel createMainPanel() {
        tr_domTree = new STree(treeModel);
        tr_domTree.setCellRenderer(new DomRenderer());
        tr_domTree.setPreferredSize(SDimension.FULLWIDTH);
        tr_domTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tr_domTree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent tse) {
                selectedNode = (Node) tse.getPath().getLastPathComponent();
                Node categoryNode = selectedNode.selectSingleNode("ancestor-or-self::category");

                if (tr_domTree.getSelectionCount() > 0 && categoryNode != null) {
                    fillEditFields(categoryNode);
                    rb_updateNode.setSelected(true);
                    rb_updateNode.setEnabled(true);
                    bu_deleteNode.setEnabled(true);
                } else {
                    clearEditFields();
                    rb_insertNode.setSelected(true);
                    rb_updateNode.setEnabled(false);
                    bu_deleteNode.setEnabled(false);
                }
            }
        });

        sp_tree = new SScrollPane(tr_domTree);
        sp_tree.setStyle(sp_tree.getStyle() + " sp_tree");
        sp_tree.setPreferredSize(SDimension.FULLAREA);
        sp_tree.setMode(SScrollPane.MODE_COMPLETE);

        bg_insertOrUpdate = new SButtonGroup();
        rb_insertNode = new SRadioButton("Insert node");
        rb_insertNode.setSelected(true);
        rb_insertNode.setShowAsFormComponent(false);
        rb_insertNode.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (rb_insertNode.isSelected()) {
                  clearEditFields();
              }
            }
        });
        rb_updateNode = new SRadioButton("Update node");
        rb_updateNode.setEnabled(false);
        rb_updateNode.setShowAsFormComponent(false);
        rb_updateNode.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (rb_updateNode.isSelected()) {
                    fillEditFields(selectedNode.selectSingleNode("ancestor-or-self::category"));
                }
            }
        });
        bg_insertOrUpdate.add(rb_insertNode);
        bg_insertOrUpdate.add(rb_updateNode);

        tf_editCategoryName = new STextField();
        tf_editCategoryName.setPreferredSize(IN_DIM);
        tf_editPriorityValue = new STextField();
        tf_editPriorityValue.setPreferredSize(IN_DIM);

        String[] additivityModel = {"true", "false"};
        cb_editAdditivityFlag = new SComboBox(additivityModel);
        cb_editAdditivityFlag.setPreferredSize(IN_DIM);

        listModel = new ArrayList<String>();
        List appenderNames = rootNode.selectNodes("./appender/@name");
        for (Iterator i = appenderNames.iterator(); i.hasNext();) {
            listModel.add(((Node) i.next()).getText());
        }
        li_editAppenderRef = new SList(listModel);
        li_editAppenderRef.setVisibleRowCount(2);
        li_editAppenderRef.setPreferredSize(IN_DIM);

        bu_saveNode = new SButton("Insert", new SURLIcon("../images/insert.gif"));
        bu_saveNode.setPreferredSize(BU_DIM);
        bu_saveNode.setHorizontalAlignment(SConstants.CENTER_ALIGN);
        bu_saveNode.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveEditFields();
            }
        });

        bu_deleteNode = new SButton("Delete", new SURLIcon("../images/delete.gif"));
        bu_deleteNode.setDisabledIcon(new SURLIcon("../images/delete_disabled.gif"));
        bu_deleteNode.setPreferredSize(BU_DIM);
        bu_deleteNode.setHorizontalAlignment(SConstants.CENTER_ALIGN);
        bu_deleteNode.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Element categoryNode = getSelectedCategory();
                if (categoryNode == null) {
                    throw new IllegalStateException("a category must be selected for deletion");
                }

                Node[] path = { rootNode };
                int[] childIndices = { treeModel.getIndexOfChild(rootNode, categoryNode) };
                Node[] children = { categoryNode };
                categoryNode.detach();
                treeModel.fireTreeNodesRemoved(
                        new TreeModelEvent(LogConfig.this, path, childIndices, children));

                clearEditFields();
                rb_insertNode.setSelected(true);
                rb_updateNode.setEnabled(false);
                bu_deleteNode.setEnabled(false);
            }
        });
        bu_deleteNode.setEnabled(false);

        bu_commitChanges = new SButton("Commit", new SURLIcon("../images/commit.gif"));
        bu_commitChanges.setPreferredSize(BU_DIM);
        bu_commitChanges.setHorizontalAlignment(SConstants.CENTER_ALIGN);
        bu_commitChanges.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    XMLWriter writer = new XMLWriter(new FileWriter(log4jXmlPath), OutputFormat.createPrettyPrint());
                    writer.write(document);
                    writer.close();
                    la_status.setText("  Your changes have been successfully written to 'log4j.xml'!");
                } catch (IOException ex) {
                    log.error("Could not write file!", ex);
                    la_status.setText("  Couldn't write changes to 'log4j.xml'! See log for details.");
                }
            }
        });

        SPanel pa_edit = createControlPanel("Insert / update category nodes:");
        SPanel pa_mode = new SPanel(new SGridLayout(2));
        pa_mode.setStyle(pa_mode.getStyle() + " pa_mode");
        pa_mode.setPreferredSize(SDimension.FULLWIDTH);
        rb_insertNode.setHorizontalAlignment(SConstants.LEFT_ALIGN);
        rb_updateNode.setHorizontalAlignment(SConstants.RIGHT_ALIGN);
        pa_mode.add(rb_insertNode);
        pa_mode.add(rb_updateNode);
        pa_edit.add(pa_mode);
        pa_edit.add(new SLabel("Category name:"));
        pa_edit.add(tf_editCategoryName);
        pa_edit.add(new SLabel("Priority value:"));
        pa_edit.add(tf_editPriorityValue);
        pa_edit.add(new SLabel("Additivity flag:"));
        pa_edit.add(cb_editAdditivityFlag);
        pa_edit.add(new SLabel("Appender reference:"));
        pa_edit.add(li_editAppenderRef);
        pa_edit.add(verticalSpace(0));
        pa_edit.add(bu_saveNode);

        SPanel pa_delete = createControlPanel("Delete selected category node:");
        pa_delete.add(bu_deleteNode);

        SPanel pa_commit = createControlPanel("Commit changes to 'log4j.xml':");
        pa_commit.add(bu_commitChanges);

        SLabel la_activityIndicator = new SLabel(" Loading data, wait!", new SURLIcon("../images/progress.gif"));
        la_activityIndicator.setName("ajaxActivityIndicator");

        SPanel pa_controls = new SPanel(new SBoxLayout(SBoxLayout.VERTICAL));
        pa_controls.setVerticalAlignment(SConstants.TOP_ALIGN);
        pa_controls.add(pa_edit);
        pa_controls.add(verticalSpace(10));
        pa_controls.add(pa_delete);
        pa_controls.add(verticalSpace(10));
        pa_controls.add(pa_commit);
        pa_controls.add(verticalSpace(10));
        pa_controls.add(la_activityIndicator);

        SPanel pa_main = new SPanel(new SGridBagLayout());
        pa_main.setPreferredSize(SDimension.FULLAREA);
        pa_main.setStyle(pa_main.getStyle() + " pa_main");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        pa_main.add(sp_tree, gbc);
        gbc.insets = new Insets(10, 5, 10, 10);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.weighty = 1;
        pa_main.add(pa_controls, gbc);
        return pa_main;
    }

    private void testSomething(boolean enabled) {
        if (!enabled) return;
        
        // TESTING BACK BUTTON & HISTORY
        fr_frame.setNoCaching(false);
        tr_domTree.setEpochCheckEnabled(false);
        fo_form.setPostMethod(false);

        // TESTING ON-CHANGE-SUBMIT-LISTENERS
        bg_insertOrUpdate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                log.info("ActionListener of ButtonGroup!!!");
            }
        });
        cb_editAdditivityFlag.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                log.info("ItemListener of ComboBox!!!");
            }
        });
        li_editAppenderRef.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                log.info("ListSelectionListener of List!!!");
            }
        });
        tf_editCategoryName.addDocumentListener(new SDocumentListener() {
            public void changedUpdate(SDocumentEvent e) {
                log.info("DocumentListener of TextField - changed!!!");
            }
            public void insertUpdate(SDocumentEvent e) {
                log.info("DocumentListener of TextField - insert!!!");
            }
            public void removeUpdate(SDocumentEvent e) {
                log.info("DocumentListener of TextField - remove!!!");
            }
        });
    }

//    private Vector getExpandedPaths() {
//        Vector expandedPaths = new Vector();
//        for (int i = 0; i < tr_domTree.getRowCount(); ++i) {
//            TreePath path = tr_domTree.getPathForRow(i);
//            if (tr_domTree.isExpanded(path)) {
//                expandedPaths.add(path);
//            }
//        }
//        return expandedPaths;
//    }
//
//    private void setExpandedPaths(Vector expandedPaths) {
//        for (int i = 0; i < expandedPaths.size(); ++i) {
//            tr_domTree.expandRow((TreePath) expandedPaths.get(i));
//        }
//    }

    private void saveEditFields() {
        String catName = tf_editCategoryName.getText();
        String priValue = tf_editPriorityValue.getText();
        String addFlag = cb_editAdditivityFlag.getSelectedItem().toString();
        Object[] appRefNames = li_editAppenderRef.getSelectedValues();

        if (catName.equals("") || priValue.equals("")) {
            la_status.setText("  You have to provide at least a category name and a priority value!");
            return;
        }

        if (rb_insertNode.isSelected()) {
            Element categoryNode = DocumentHelper.createElement("category").addAttribute("name", catName);
            categoryNode.addElement("priority").addAttribute("value", priValue);
            categoryNode.addAttribute("additivity", addFlag);
            for (int i = 0; i < appRefNames.length; ++i) {
                categoryNode.addElement("appender-ref").addAttribute("ref", appRefNames[i].toString());
            }

            Node firstCategory = rootNode.selectSingleNode("./category[1]");
            if (firstCategory == null) {
                rootNode.add(categoryNode);
            } else {
                rootNode.content().add(rootNode.indexOf(firstCategory), categoryNode);
            }
            int index = rootNode.selectNodes("./* | ./@*").indexOf(categoryNode);

            Node[] path = { rootNode };
            int[] childIndices = { index };
            Node[] children = { categoryNode };
            treeModel.fireTreeNodesInserted(new TreeModelEvent(LogConfig.this, path, childIndices, children));

            selectedNode = categoryNode;
            Node[] selectedPath = { rootNode, categoryNode};
            tr_domTree.setSelectionPath(new TreePath(selectedPath));

            fillEditFields(categoryNode);
        } else if (rb_updateNode.isSelected()) {
            Element categoryNode = getSelectedCategory();
            if (categoryNode == null) {
                throw new IllegalStateException("a category must be selected for update");
            }
            categoryNode.selectSingleNode("./@name").setText(catName);
            categoryNode.selectSingleNode("./priority/@value").setText(priValue);
            categoryNode.selectSingleNode("./@additivity").setText(addFlag);

            Node[] path = { rootNode, categoryNode };

            List oldAppRefNodes = categoryNode.selectNodes("./appender-ref");
            int[] oldChildIndices = new int[oldAppRefNodes.size()];
            for (int i = 0; i < oldAppRefNodes.size(); ++i) {
                Node oldAppenderNode = (Node) oldAppRefNodes.get(i);
                oldChildIndices[i] = treeModel.getIndexOfChild(categoryNode, oldAppenderNode);
                oldAppenderNode.detach();
            }
            treeModel.fireTreeNodesRemoved(
                    new TreeModelEvent(LogConfig.this, path, oldChildIndices, oldAppRefNodes.toArray()));

            List<Node> newAppRefNodes = new ArrayList<Node>();
            int[] newChildIndices = new int[appRefNames.length];
            for (int i = 0; i < appRefNames.length; ++i) {
                Node newAppenderNode = (Node) categoryNode.addElement("appender-ref").addAttribute("ref",
                        appRefNames[i].toString());
                newAppRefNodes.add(newAppenderNode);
                newChildIndices[i] = treeModel.getIndexOfChild(categoryNode, newAppenderNode);
            }
            treeModel.fireTreeNodesInserted(
                    new TreeModelEvent(LogConfig.this, path, newChildIndices, newAppRefNodes.toArray()));

            selectedNode = categoryNode;
            tr_domTree.setSelectionPath(new TreePath(path));
        }
    }

    private void clearEditFields() {
        tf_editCategoryName.setText("");
        tf_editPriorityValue.setText("");
        cb_editAdditivityFlag.setSelectedItem("true");
        li_editAppenderRef.clearSelection();
        bu_saveNode.setText("Insert");
        bu_saveNode.setIcon(INSERT_IMG);
        resetStatusLabel();
    }

    private void fillEditFields(Node categoryNode) {
        tf_editCategoryName.setText(categoryNode.valueOf("./@name"));
        tf_editPriorityValue.setText(categoryNode.valueOf("./priority/@value"));
        cb_editAdditivityFlag.setSelectedItem(categoryNode.valueOf("./@additivity"));
        List appenderRefs = categoryNode.selectNodes("./appender-ref/@ref");
        int[] selectedIndices = new int[appenderRefs.size()];
        for (int i = 0; i < appenderRefs.size(); ++i) {
            selectedIndices[i] = listModel.indexOf(((Node) appenderRefs.get(i)).getText());
        }
        li_editAppenderRef.setSelectedIndices(selectedIndices);
        bu_saveNode.setText("Update");
        bu_saveNode.setIcon(UPDATE_IMG);
        resetStatusLabel();
    }

    private void resetStatusLabel() {
        la_status.setText("  Usage: Insert, update or delete category nodes by means of the" +
                " according widgets - then commit all changes to 'log4j.xml'.");
    }

    private Element getSelectedCategory() {
        if (selectedNode == null) return null;
        Node selectedCategoryNode = selectedNode.selectSingleNode("ancestor-or-self::category");
        if (selectedCategoryNode == null) return null;

        List categoryElements = rootNode.elements("category");
        for (Iterator i = categoryElements.iterator(); i.hasNext();) {
            Element currentElement = (Element) i.next();
            if (currentElement == selectedCategoryNode) {
                return currentElement;
            }
        }
        return null;
    }

    private SComponent verticalSpace(int height) {
        SLabel label = new SLabel();
        label.setAttribute(CSSProperty.HEIGHT, height + "px");
        return label;
    }

    private SPanel createControlPanel(String title) {
        SBoxLayout boxLayout = new SBoxLayout(SBoxLayout.VERTICAL);
        boxLayout.setHgap(20);
        boxLayout.setVgap(5);

        SPanel panel = new SPanel();
        panel.setLayout(boxLayout);
        panel.setStyle(panel.getStyle() + " pa_control");

        SLabel la_title = new SLabel(title);
        la_title.setStyle(la_title.getStyle() + " la_title");

        panel.add(la_title);
        panel.add(verticalSpace(0));
        return panel;
    }

    private SPanel createDebuggingPanel() {
        SBoxLayout boxLayout = new SBoxLayout(SBoxLayout.VERTICAL);
        boxLayout.setHgap(20);
        boxLayout.setVgap(5);

        SPanel pa_debug = new SPanel(boxLayout);
        pa_debug.setStyle(pa_debug.getStyle() + " pa_debug");
        pa_debug.setVerticalAlignment(SConstants.TOP_ALIGN);

        SLabel la_title = new SLabel("Playground for debugging Ajax:");
        la_title.setStyle(la_title.getStyle() + " la_title");
        pa_debug.add(la_title);

        boolean selected;
        final String[] cb_texts = {
                "Frame: updates enabled --> ",
                "Frame: cursor enabled --> ",
                "Form: reload forced --> ",
                "Tree: reload forced --> "
        };

        selected = fr_frame.isUpdateEnabled();
        final SCheckBox cb_toggleFrameUpdateEnabled =
            new SCheckBox(cb_texts[0] + selected, selected);
        cb_toggleFrameUpdateEnabled.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean state = fr_frame.isUpdateEnabled();
                fr_frame.setUpdateEnabled(!state);
                cb_toggleFrameUpdateEnabled.setText(cb_texts[0] + !state);
            }
        });

        Map<String, Object> updateCursor = fr_frame.getUpdateCursor();
        selected = ((Boolean) updateCursor.get("enabled")).booleanValue();
        final SCheckBox cb_toggleFrameUpdateCursor =
            new SCheckBox(cb_texts[1] + selected, selected);
        cb_toggleFrameUpdateCursor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Map<String, Object> updateCursor = new HashMap<String, Object>(fr_frame.getUpdateCursor());
                boolean state = ((Boolean) updateCursor.get("enabled")).booleanValue();
                updateCursor.put("enabled", !state);
                fr_frame.setUpdateCursor(updateCursor);
                cb_toggleFrameUpdateCursor.setText(cb_texts[1] + !state);
            }
        });

        selected = fo_form.isReloadForced();
        final SCheckBox cb_toggleFormReloadForced =
            new SCheckBox(cb_texts[2] + selected, selected);
        cb_toggleFormReloadForced.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean state = fo_form.isReloadForced();
                fo_form.setReloadForced(!state);
                cb_toggleFormReloadForced.setText(cb_texts[2]  + !state);
            }
        });

        selected = tr_domTree.isReloadForced();
        final SCheckBox cb_toggleTreeReloadForced =
            new SCheckBox(cb_texts[3] + selected, selected);
        cb_toggleTreeReloadForced.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean state = tr_domTree.isReloadForced();
                tr_domTree.setReloadForced(!state);
                cb_toggleTreeReloadForced.setText(cb_texts[3]  + !state);
            }
        });

        final SButton bu_markFrameDirty = new SButton("Reload the entire frame (mark it dirty)");
        bu_markFrameDirty.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fr_frame.reload();
            }
        });

        final SButton bu_doSomethingSpecial = new SButton("Do something special for 10 seconds");
        bu_doSomethingSpecial.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e1) {}
            }
        });

        final SButton bu_forceServerError = new SButton("Force a stupid NullPointerException");
        bu_forceServerError.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String error = null;
                error.toString();
            }
        });

        final SAnchor an_abortCurrentAjaxRequest = new SAnchor();
        an_abortCurrentAjaxRequest.setLayout(new SBorderLayout());
        an_abortCurrentAjaxRequest.addScriptListener(new JavaScriptListener(
                JavaScriptEvent.ON_CLICK, "wingS.ajax.abortRequest(); return false;"
        ));
        an_abortCurrentAjaxRequest.add(new SLabel("Try to abort the current Ajax request"));
        
        final SButton bu_logDebug = new SButton("Log message with level: DEBUG");
        bu_logDebug.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                log.debug("I'm a log message with level: DEBUG");
            }
        });
        
        final SButton bu_logInfo = new SButton("Log message with level: INFO");
        bu_logInfo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                log.info("I'm a log message with level: INFO");
            }
        });
        
        final SButton bu_logWarn = new SButton("Log message with level: WARN");
        bu_logWarn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (log.isWarnEnabled()) log.warn("I'm a log message with level: WARN");
            }
        });
        
        final SButton bu_logError = new SButton("Log message with level: ERROR");
        bu_logError.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                log.error("I'm a log message with level: ERROR");
            }
        });
        
        final SButton bu_logFatal = new SButton("Log message with level: FATAL");
        bu_logFatal.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                log.fatal("I'm a log message with level: FATAL");
            }
        });

        final SButton bu_toggleAjaxDebugView = new SButton("Toggle Ajax debug view --- (Shift F12)");
        bu_toggleAjaxDebugView.addScriptListener(new JavaScriptListener(
                JavaScriptEvent.ON_CLICK, "wingS.ajax.toggleDebugView(); return false;"));

        addToDebuggingPanel(pa_debug, verticalSpace(0));
        addToDebuggingPanel(pa_debug, cb_toggleFrameUpdateEnabled);
        addToDebuggingPanel(pa_debug, cb_toggleFrameUpdateCursor);
        addToDebuggingPanel(pa_debug, cb_toggleFormReloadForced);
        addToDebuggingPanel(pa_debug, cb_toggleTreeReloadForced);

        addToDebuggingPanel(pa_debug, verticalSpace(5));
        addToDebuggingPanel(pa_debug, bu_markFrameDirty);
        addToDebuggingPanel(pa_debug, bu_doSomethingSpecial);
        addToDebuggingPanel(pa_debug, bu_forceServerError);
        addToDebuggingPanel(pa_debug, an_abortCurrentAjaxRequest);

        addToDebuggingPanel(pa_debug, verticalSpace(5));
        addToDebuggingPanel(pa_debug, createRandomResultPanel());
        
        addToDebuggingPanel(pa_debug, verticalSpace(5));
        addToDebuggingPanel(pa_debug, bu_logDebug);
        addToDebuggingPanel(pa_debug, bu_logInfo);
        addToDebuggingPanel(pa_debug, bu_logWarn);
        addToDebuggingPanel(pa_debug, bu_logError);
        addToDebuggingPanel(pa_debug, bu_logFatal);

        addToDebuggingPanel(pa_debug, verticalSpace(5));
        addToDebuggingPanel(pa_debug, bu_toggleAjaxDebugView);

        return pa_debug;
    }

    private void addToDebuggingPanel(SPanel debug, SComponent component) {
        component.setHorizontalAlignment(SConstants.LEFT_ALIGN);
        component.setShowAsFormComponent(false);
        debug.add(component);
    }

    private SPanel createRandomResultPanel() {
        int links = 6;

        class RandomResultGenerator implements ActionListener {
            private SLabel label;
            private boolean sleep;

            public RandomResultGenerator(SLabel label, boolean sleep) {
                this.label = label;
                this.sleep = sleep;
            }

            public void actionPerformed(ActionEvent e) {
                if (sleep) {
                    int ms = new Random().nextInt(3001);
                    try {
                        Thread.sleep(ms);
                    } catch (InterruptedException e1) {}
                    label.setText(ms + " ms");
                } else {
                    int nr = new Random().nextInt(9000) + 1000;
                    label.setText(nr + "");
                }
            }
        }

        SPanel panel = new SPanel(new SGridLayout(links, 3));
        for (int i = 0; i < links; ++i) {
            SLabel label = new SLabel();
            SButton button = new SButton();
            button.setShowAsFormComponent(false);
            button.setHorizontalAlignment(SConstants.LEFT_ALIGN);
            if (i < links - 1) {
                button.setText("Sleep for a while!  >>");
                button.addActionListener(new RandomResultGenerator(label, true));
            } else {
                button.setText("Say any number!  >>");
                button.addActionListener(new RandomResultGenerator(label, false));
                panel.add(new SSpacer(0, 10));
                panel.add(new SSpacer(0, 5));
                panel.add(new SSpacer(0, 10));
            }
            panel.add(button);
            panel.add(new SSpacer(0, 5));
            panel.add(label);
        }
        return panel;
    }

}