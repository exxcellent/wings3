package hugetestportlet.data;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

public class NavigationTreeModel {

	public static final TreeNode ROOT_NODE = generateTree();

	static TreeNode generateTree() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Navigation");

		root.add(new DefaultMutableTreeNode("Table"));
		root.add(new DefaultMutableTreeNode("Pictures"));
		root.add(new DefaultMutableTreeNode("Links"));
		root.add(new DefaultMutableTreeNode("Params"));
		root.add(new DefaultMutableTreeNode("ParamsMode"));
		root.add(new DefaultMutableTreeNode("ParamsWS"));

		return root;
	}

}
