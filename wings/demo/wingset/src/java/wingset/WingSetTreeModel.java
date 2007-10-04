// (c) copyright 2006 by eXXcellent solutions, Ulm. Author: bschmid

package wingset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wings.plaf.WingSetExample;
import org.wings.session.Session;
import org.wings.session.SessionManager;
import org.wings.util.PropertyDiscovery;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Benjamin Schmid <B.Schmid@exxcellent.de>
 */
class WingSetTreeModel extends DefaultTreeModel {
    private static final Log LOG = LogFactory.getLog(WingSetTreeModel.class);

    public WingSetTreeModel() {
        super(buildNodes());
    }

    private static TreeNode buildNodes() {
        final DefaultMutableTreeNode root = new DefaultMutableTreeNode("Demo");

        // Collect all classes which are in question of being a wingset example
        final List<String> wingsClassNames = new ArrayList<String>();
        wingsClassNames.addAll(collectExampleViaWebappDir());
        wingsClassNames.addAll(collectExampleViaDefaultProperties());
        Collections.sort(wingsClassNames);

        final Map<String, Set<WingSetExample>> exampleRegistry = new HashMap<String, Set<WingSetExample>>();
        // Test them for being an instancable wingsetexample
        for (String wingsClassFileName : wingsClassNames) {
            try {
                Class clazz = Thread.currentThread().getContextClassLoader().loadClass(wingsClassFileName);
                if (WingSetExample.class.isAssignableFrom(clazz)) {
                    final WingSetExample exampleInstance = (WingSetExample) clazz.newInstance();
                    final String sectionName = exampleInstance.getExampleGroup();
                    Set<WingSetExample> sectionMap = exampleRegistry.get(sectionName);
                    if (sectionMap == null) {
                        sectionMap = new LinkedHashSet<WingSetExample>();
                        exampleRegistry.put(sectionName, sectionMap);
                    }
                    sectionMap.add(exampleInstance);
                }
            } catch (Exception e) {
                LOG.debug("Skipping class " + wingsClassFileName + " due to " + e);
            }
        }

        // build the nodes
        buildNodes(root, exampleRegistry);

        return root;
    }

    private static List<String> collectExampleViaWebappDir() {
        final String dirName = SessionManager.getSession().getServletContext().getRealPath("/WEB-INF/classes/wingset");
        final File dir = new File(dirName);
        final String[] dirContent = dir.list();
        for (int i = 0; i < dirContent.length; i++) {
            String fileName = dirContent[i];
            if (fileName.endsWith(".class")) {
                dirContent[i] = "wingset." + fileName.substring(0, fileName.length() - 6);
            }
        }
        return Arrays.asList(dirContent);
    }

    private static List<String> collectExampleViaDefaultProperties() {
        final ArrayList<String> exampleClassNames = new ArrayList<String>();
        Map properties = PropertyDiscovery.loadOptionalProperties("org/wings/plaf/css/default.properties");
        for (Object o : properties.keySet()) {
            if (o instanceof String && ((String) o).endsWith(".wingset-example")) {
                exampleClassNames.add((String) properties.get(o));
            }
        }
        return exampleClassNames;
    }

    private static void buildNodes(DefaultMutableTreeNode rootNode,  Map<String, Set<WingSetExample>> registry) {
        final Session session = SessionManager.getSession();
        final boolean includeTests = "TRUE".equalsIgnoreCase((String) session.getProperty("wingset.include.tests"));
        final boolean includeExperiments = "TRUE".equalsIgnoreCase((String) session.getProperty("wingset.include.experiments"));

        Map<String, DefaultMutableTreeNode> sectionNodeMap = new HashMap<String,DefaultMutableTreeNode>();

        for (String sectionName : registry.keySet()) {
            if (!includeTests && "Test".equalsIgnoreCase(sectionName))
                continue;
            if (!includeExperiments && "Experimental".equalsIgnoreCase(sectionName))
                continue;

            DefaultMutableTreeNode sectionNode = sectionNodeMap.get(sectionName);
            if (sectionNode == null) {
                sectionNode = new DefaultMutableTreeNode(sectionName);
                sectionNodeMap.put(sectionName, sectionNode);
                rootNode.add(sectionNode);
            }
            Set<WingSetExample> examples = registry.get(sectionName);
            for (WingSetExample example : examples) {
                sectionNode.add(new DefaultMutableTreeNode(example));
            }
        }
    }
}
