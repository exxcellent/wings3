package hugetestportlet;

import hugetestportlet.data.NavigationTreeModel;
import hugetestportlet.data.PersonBean;
import hugetestportlet.data.PersonTableModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.wings.SButton;
import org.wings.SButtonGroup;

import org.wings.SAnchor;
import org.wings.SBorderLayout;
import org.wings.SCheckBox;
import org.wings.SFileIcon;
import org.wings.SForm;
import org.wings.SFrame;
import org.wings.SGridLayout;
import org.wings.SLabel;
import org.wings.SPanel;
import org.wings.SPortletAnchor;
import org.wings.SRadioButton;
import org.wings.SResourceIcon;
import org.wings.SScrollPane;
import org.wings.STabbedPane;
import org.wings.STable;
import org.wings.STextField;
import org.wings.STree;
import org.wings.SURLIcon;
import org.wings.event.SRenderEvent;
import org.wings.event.SRenderListener;
import org.wings.portlet.PortletParameterEvent;
import org.wings.portlet.PortletParameterListener;
import org.wings.portlet.PortletParameterProvider;
import org.wings.portlet.WindowStateEvent;
import org.wings.portlet.WindowStateListener;
import org.wings.portlet.WindowStateProvider;

public class HugeTestPortletWingSEdit {

	// private List<PersonBean> personList = new LinkedList<PersonBean>();

	public HugeTestPortletWingSEdit() {

		// TabbedPane for table and table input
		final STabbedPane tabbedPane = getTableTabbedPane();

		// Image panel
		final SPanel imagePanel = getImagePanel();

		// Links panel
		final SPanel linksPanel = getLinksPanel();

		// Links with parameters panel
		final SPanel linksWithParametersPanel = getLinksWithParametersPanel();

		// Links with mode and param
		final SPanel linksWithParametersAndModesPanel = getLinksWithParametersAndModesPanel();

		// Links with window states
		final SPanel linksWithWindowState = getLinksWithWindowStates();

		// Navigation Tree panel
		STree tree = new STree(new DefaultTreeModel(
				NavigationTreeModel.ROOT_NODE));
		tree.getSelectionModel().setSelectionMode(STree.SINGLE_TREE_SELECTION);

		SGridLayout treePanelLayout = new SGridLayout(1);
		treePanelLayout.setVgap(2);
		treePanelLayout.setHgap(2);
		SPanel treePanel = new SPanel(treePanelLayout);
		treePanel.setVerticalAlignment(SGridLayout.TOP);

		treePanel.add(tree);

		// Root Form
		SBorderLayout rootFormLayout = new SBorderLayout();
		rootFormLayout.setVgap(2);
		rootFormLayout.setHgap(30);
		final SForm rootForm = new SForm(rootFormLayout);

		rootForm.add(treePanel, SBorderLayout.WEST);

		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
				TreePath path = treeSelectionEvent.getNewLeadSelectionPath();
				if (path != null) {
					String choose = path.getLastPathComponent().toString();
					if (choose.equals("Table")) {
						rootForm.remove(imagePanel);
						rootForm.remove(linksPanel);
						rootForm.remove(linksWithParametersPanel);
						rootForm.remove(linksWithParametersAndModesPanel);
						rootForm.remove(linksWithWindowState);
						rootForm.add(tabbedPane, SBorderLayout.CENTER);
					} else if (choose.equals("Pictures")) {
						rootForm.remove(tabbedPane);
						rootForm.remove(linksPanel);
						rootForm.remove(linksWithParametersPanel);
						rootForm.remove(linksWithParametersAndModesPanel);
						rootForm.remove(linksWithWindowState);
						rootForm.add(imagePanel, SBorderLayout.CENTER);
					} else if (choose.equals("Links")) {
						rootForm.remove(tabbedPane);
						rootForm.remove(imagePanel);
						rootForm.remove(linksWithParametersAndModesPanel);
						rootForm.remove(linksWithParametersPanel);
						rootForm.remove(linksWithWindowState);
						rootForm.add(linksPanel, SBorderLayout.CENTER);
					} else if (choose.equals("Params")) {
						rootForm.remove(tabbedPane);
						rootForm.remove(imagePanel);
						rootForm.remove(linksPanel);
						rootForm.remove(linksWithParametersAndModesPanel);
						rootForm.remove(linksWithWindowState);
						rootForm.add(linksWithParametersPanel,
								SBorderLayout.CENTER);
					} else if (choose.equals("ParamsMode")) {
						rootForm.remove(tabbedPane);
						rootForm.remove(imagePanel);
						rootForm.remove(linksPanel);
						rootForm.remove(linksWithParametersPanel);
						rootForm.remove(linksWithWindowState);
						rootForm.add(linksWithParametersAndModesPanel,
								SBorderLayout.CENTER);
					} else if (choose.equals("ParamsWS")) {
						rootForm.remove(tabbedPane);
						rootForm.remove(imagePanel);
						rootForm.remove(linksPanel);
						rootForm.remove(linksWithParametersPanel);
						rootForm.remove(linksWithParametersAndModesPanel);
						rootForm
								.add(linksWithWindowState, SBorderLayout.CENTER);
					} else {
						rootForm.remove(imagePanel);
						rootForm.remove(tabbedPane);
						rootForm.remove(linksPanel);
						rootForm.remove(linksWithParametersPanel);
						rootForm.remove(linksWithParametersAndModesPanel);
						rootForm.remove(linksWithWindowState);
					}
				} else {
					rootForm.remove(imagePanel);
					rootForm.remove(tabbedPane);
					rootForm.remove(linksPanel);
					rootForm.remove(linksWithParametersPanel);
					rootForm.remove(linksWithParametersAndModesPanel);
					rootForm.remove(linksWithWindowState);
				}

			}
		});

		// test window state
		final SLabel max = new SLabel();

		WindowStateProvider pwsp = WindowStateProvider
				.getInstance();
		pwsp.addWindowStateChangeListener(new WindowStateListener() {

			public void windowStateChanged(WindowStateEvent e) {
				if (e.getWindowState().equals(
						WindowStateProvider.MAXIMIZED_WS)) {

					String maxString = "Currently in Maximized_WS, Currently "
							+ "in Maximized_WS, ";
					for (int i = 0; i < 40; i++) {
						maxString += "Currently in Maximized_WS, ";
						if (i % 4 == 0)
							maxString += "\n";
					}
					maxString += "Currently in Maximized_WS, Currently in Maximized_WS.";
					max.setText(maxString);
				}
				else {
					max.setText("");
				}

			}

		});

		rootForm.add(max, SBorderLayout.NORTH);

		// Frame
		SFrame rootFrame = new SFrame();
		rootFrame.getContentPane().add(rootForm);
		rootFrame.setVisible(true);

	}

	private STabbedPane getTableTabbedPane() {

		// DATA PANEL

		// male/female Radio Buttons in Group
		final SButtonGroup genderGroup = new SButtonGroup();
		final SRadioButton male = new SRadioButton("Mr", true);
		final SRadioButton female = new SRadioButton("Mrs");
		genderGroup.add(male);
		genderGroup.add(female);

		// prof and dr check Boxes
		final SCheckBox prof = new SCheckBox("Prof.");
		final SCheckBox dr = new SCheckBox("Dr.");

		// name and givenName fields
		final SLabel nameLabel = new SLabel("Name: ");
		final STextField name = new STextField();
		final SLabel givenNameLabel = new SLabel("given name: ");
		final STextField givenName = new STextField();

		SGridLayout dataPanelLayout = new SGridLayout(2);
		dataPanelLayout.setVgap(10);
		dataPanelLayout.setHgap(5);
		SPanel dataPanel = new SPanel(dataPanelLayout);

		dataPanel.add(male);
		dataPanel.add(female);
		dataPanel.add(prof);
		dataPanel.add(dr);
		dataPanel.add(nameLabel);
		dataPanel.add(name);
		dataPanel.add(givenNameLabel);
		dataPanel.add(givenName);

		// SUBMIT PANEL

		SButton submitButton = new SButton("Submit");

		SGridLayout submitPanelLayout = new SGridLayout(1);
		submitPanelLayout.setVgap(2);
		submitPanelLayout.setHgap(2);
		SPanel submitPanel = new SPanel(submitPanelLayout);

		submitPanel.add(submitButton);

		// INPUT PANEL

		SGridLayout inputLayout = new SGridLayout(1);
		inputLayout.setVgap(15);
		inputLayout.setBorder(1);
		SPanel inputPanel = new SPanel(inputLayout);

		inputPanel.add(dataPanel);
		inputPanel.add(submitPanel);

		// ---------------------------------------------

		// TABLE PANEL

		final SLabel tableStatusLabel = new SLabel();

		final PersonTableModel ptm = new PersonTableModel();
		ptm.addPerson(new PersonBean("Mr", true, true, "wurst", "hans1"));
		ptm.addPerson(new PersonBean("Mr", true, true, "wurst", "hans2"));
		ptm.addPerson(new PersonBean("Mr", true, true, "wurst", "hans3"));
		ptm.addPerson(new PersonBean("Mr", true, true, "wurst", "hans4"));
		ptm.addPerson(new PersonBean("Mr", true, true, "wurst", "hans5"));
		ptm.addPerson(new PersonBean("Mr", true, true, "wurst", "hans6"));
		ptm.addPerson(new PersonBean("Mr", true, true, "wurst", "hans7"));
		ptm.addPerson(new PersonBean("Mr", true, true, "wurst", "hans8"));
		ptm.addPerson(new PersonBean("Mr", true, true, "wurst", "hans9"));
		ptm.addPerson(new PersonBean("Mr", true, true, "wurst", "hans10"));
		ptm.addPerson(new PersonBean("Mr", true, true, "wurst", "hans11"));
		ptm.addPerson(new PersonBean("Mr", true, true, "wurst", "hans12"));
		ptm.addPerson(new PersonBean("Mr", true, true, "wurst", "hans13"));
		ptm.addPerson(new PersonBean("Mr", true, true, "wurst", "hans14"));
		final STable table = new STable(ptm);
		table.setSelectionMode(STable.SINGLE_SELECTION);

		final SButton deleteButton = new SButton("Delete selected row");

		SGridLayout tablePanelLayout = new SGridLayout(1);
		tablePanelLayout.setVgap(5);
		tablePanelLayout.setHgap(5);
		SPanel tablePanel = new SPanel(tablePanelLayout);

		SScrollPane scrollPane = new SScrollPane(table);
		tablePanel.add(scrollPane);
		tablePanel.add(tableStatusLabel);
		tablePanel.add(deleteButton);

		// ---------------------------------------------

		// TABBEDPANEL

		STabbedPane tappedPane = new STabbedPane();
		tappedPane.add("NewEntry", inputPanel);
		tappedPane.add("Table", tablePanel);

		// ---------------------------------------------

		// LISTENER

		// listner for submit button
		submitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PersonBean person = new PersonBean();
				if (genderGroup.getSelection().equals(male)) {
					person.setGender("Mr");
				} else {
					person.setGender("Mrs");
				}
				person.setDr(dr.isSelected());
				person.setProf(prof.isSelected());
				person.setName(name.getText());
				person.setGivenName(givenName.getText());

				ptm.addPerson(person);

			}
		});

		// listner for table selection
		table.addSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent listSelectionEvent) {
				tableStatusLabel.setText("Selection changed to: "
						+ table.getSelectedRow());
			}
		});

		// listner for delete button
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				if (table.getSelectedRow() != -1) {
					ptm.removePerson(table.getSelectedRow());
				}
			}
		});

		return tappedPane;

	}

	private SPanel getImagePanel() {

		// image contained in the class path
		SResourceIcon classPathImg = new SResourceIcon(
				"org/wings/icons/JavaCup.gif");
		SLabel classPathImgL = new SLabel(classPathImg);

		// image deployed in a icons directory of you application
		SURLIcon directoryImg = new SURLIcon("/testportlet/img/tux.gif");
		SLabel directoryImgL = new SLabel(directoryImg);


		SGridLayout imagesPanelLayout = new SGridLayout(1);
		imagesPanelLayout.setBorder(1);
		imagesPanelLayout.setVgap(2);
		imagesPanelLayout.setHgap(2);
		final SPanel imagePanel = new SPanel(imagesPanelLayout);

		imagePanel.add(new SLabel("Class Path Image:"));
		imagePanel.add(classPathImgL);
		imagePanel.add(new SLabel("Directory Path Image:"));
		imagePanel.add(directoryImgL);

		return imagePanel;

	}

	private SPanel getLinksPanel() {

		SLabel googleLink = new SLabel("Google");
		SAnchor googleAnchor = new SAnchor("http://www.google.de", "_blank");
		googleAnchor.add(googleLink);

		SLabel actionLink = new SLabel("Action");
		SPortletAnchor actionAnchor = new SPortletAnchor(
				SPortletAnchor.ACTION_URL);
		actionAnchor.add(actionLink);

		SLabel renderLink = new SLabel("Render");
		SPortletAnchor renderAnchor = new SPortletAnchor(
				SPortletAnchor.RENDER_URL);
		renderAnchor.add(renderLink);

		SLabel viewMode = new SLabel("go to view mode with action url");
		SPortletAnchor viewModeActionAnchor = new SPortletAnchor(
				SPortletAnchor.ACTION_URL, SPortletAnchor.VIEW_MODE);
		viewModeActionAnchor.add(viewMode);

		SLabel viewRenderMode = new SLabel("go to view mode with render url");
		SPortletAnchor viewRenderModeActionAnchor = new SPortletAnchor(
				SPortletAnchor.RENDER_URL, SPortletAnchor.VIEW_MODE);
		viewRenderModeActionAnchor.add(viewRenderMode);

		SLabel actionLink2 = new SLabel("Action2");
		SPortletAnchor actionAnchor2 = new SPortletAnchor(
				SPortletAnchor.ACTION_URL);
		actionAnchor2.add(actionLink2);

		SGridLayout linksPanelLayout = new SGridLayout(1);
		linksPanelLayout.setBorder(1);
		linksPanelLayout.setVgap(2);
		linksPanelLayout.setHgap(2);
		final SPanel linksPanel = new SPanel(linksPanelLayout);

		linksPanel.add(googleAnchor);
		linksPanel.add(actionAnchor);
		linksPanel.add(renderAnchor);
		linksPanel.add(viewModeActionAnchor);
		linksPanel.add(viewRenderModeActionAnchor);
		linksPanel.add(actionAnchor2);

		return linksPanel;

	}

	private SPanel getLinksWithParametersPanel() {

		Map<String, String[]> actionParams = new HashMap<String, String[]>();
		actionParams.put("testprop", new String[] { "action", "array" });

		Map<String, String[]> renderParams = new HashMap<String, String[]>();
		renderParams.put("testprop", new String[] { "render" });

		Map<String, String[]> actionParams2 = new HashMap<String, String[]>();
		actionParams2.put("testprop", new String[] { "action2" });

		SLabel actionLink = new SLabel("Action");
		SPortletAnchor actionAnchor = new SPortletAnchor(
				SPortletAnchor.ACTION_URL, null, null, actionParams);
		actionAnchor.add(actionLink);

		SLabel actionExtraParamLink = new SLabel("Action extra Param");
		SPortletAnchor actionExtraParamAnchor = new SPortletAnchor(
				SPortletAnchor.ACTION_URL, null, null, actionParams);
		actionExtraParamAnchor.add(actionExtraParamLink);
		actionExtraParamAnchor.setParameter("extraParam", "actionExtraParam");
		actionExtraParamAnchor.setParameters("extraParamArray", new String[] {
				"epa1", "epa2" });

		SLabel actionLink2 = new SLabel("Action2");
		SPortletAnchor actionAnchor2 = new SPortletAnchor(
				SPortletAnchor.ACTION_URL, null, null, actionParams2);
		actionAnchor2.add(actionLink2);

		SLabel actionLinkO = new SLabel("Action without param");
		SPortletAnchor actionAnchorO = new SPortletAnchor(
				SPortletAnchor.ACTION_URL, null);
		actionAnchorO.add(actionLinkO);

		SLabel renderLink = new SLabel("Render");
		SPortletAnchor renderAnchor = new SPortletAnchor(
				SPortletAnchor.RENDER_URL, null, null, renderParams);
		renderAnchor.add(renderLink);

		SLabel viewMode = new SLabel("view mode action url without param ");
		SPortletAnchor viewModeActionAnchor = new SPortletAnchor(
				SPortletAnchor.ACTION_URL, SPortletAnchor.VIEW_MODE);
		viewModeActionAnchor.add(viewMode);

		SLabel viewRenderMode = new SLabel("view mode render url without param");
		SPortletAnchor viewRenderModeActionAnchor = new SPortletAnchor(
				SPortletAnchor.RENDER_URL, SPortletAnchor.VIEW_MODE);
		viewRenderModeActionAnchor.add(viewRenderMode);

		SLabel actionLinkO2 = new SLabel("Action without param 2");
		SPortletAnchor actionAnchorO2 = new SPortletAnchor(
				SPortletAnchor.ACTION_URL, null, null);
		actionAnchorO2.add(actionLinkO2);

		SGridLayout linksPanelLayout = new SGridLayout(1);
		linksPanelLayout.setBorder(1);
		linksPanelLayout.setVgap(2);
		linksPanelLayout.setHgap(2);
		final SPanel linksPanel = new SPanel(linksPanelLayout);

		linksPanel.add(actionAnchor);
		linksPanel.add(actionExtraParamAnchor);
		linksPanel.add(actionAnchor2);
		linksPanel.add(actionAnchorO);
		linksPanel.add(renderAnchor);
		linksPanel.add(viewModeActionAnchor);
		linksPanel.add(viewRenderModeActionAnchor);
		linksPanel.add(actionAnchorO2);

		final SLabel paramLabel = new SLabel();
	
		PortletParameterProvider ppp = PortletParameterProvider.getInstance();
		
		ppp.addPortletParameterListener(new PortletParameterListener(){

			public void newPortletParameters(PortletParameterEvent e) {
				Map<String, String[]> params = e.getParameterMap();
				String paramList = "Parameter: \n";
				if (params != null) {
					Set<String> paramSet = params.keySet();
					Iterator<String> iter = paramSet.iterator();
					while (iter.hasNext()) {
						String paramName = iter.next();
						String[] value = (String[]) params.get(paramName);
						paramList += paramName + ": " + Arrays.asList(value)
								+ "\n";
					}
				}
				paramLabel.setText(paramList);				
			}
			
		});

		linksPanel.add(paramLabel);

		return linksPanel;

	}

	private SPanel getLinksWithParametersAndModesPanel() {

		Map<String, String[]> actionParams = new HashMap<String, String[]>();
		actionParams.put("testprop", new String[] { "action" });

		Map<String, String[]> renderParams = new HashMap<String, String[]>();
		renderParams.put("testprop", new String[] { "render" });

		SLabel viewMode = new SLabel("view mode action url");
		SPortletAnchor viewModeActionAnchor = new SPortletAnchor(
				SPortletAnchor.ACTION_URL, SPortletAnchor.VIEW_MODE, null,
				actionParams);
		viewModeActionAnchor.add(viewMode);

		SLabel viewRenderMode = new SLabel("view mode render url");
		SPortletAnchor viewRenderModeActionAnchor = new SPortletAnchor(
				SPortletAnchor.RENDER_URL, SPortletAnchor.VIEW_MODE, null,
				renderParams);
		viewRenderModeActionAnchor.add(viewRenderMode);

		SGridLayout linksPanelLayout = new SGridLayout(1);
		linksPanelLayout.setBorder(1);
		linksPanelLayout.setVgap(2);
		linksPanelLayout.setHgap(2);
		final SPanel linksPanel = new SPanel(linksPanelLayout);

		linksPanel.add(viewModeActionAnchor);
		linksPanel.add(viewRenderModeActionAnchor);

		return linksPanel;
	}

	private SPanel getLinksWithWindowStates() {

		SLabel normalWS = new SLabel("normalWS");
		SPortletAnchor normalWSAnchor = new SPortletAnchor(
				SPortletAnchor.RENDER_URL, null, SPortletAnchor.NORMAL_WS);
		normalWSAnchor.add(normalWS);

		SLabel maximizedWS = new SLabel("maximizedWS");
		SPortletAnchor maximizedWSAnchor = new SPortletAnchor(
				SPortletAnchor.RENDER_URL, null, SPortletAnchor.MAXIMIZED_WS);
		maximizedWSAnchor.add(maximizedWS);

		SLabel miminizedWS = new SLabel("minimizedWS");
		SPortletAnchor miminizedWSAnchor = new SPortletAnchor(
				SPortletAnchor.RENDER_URL, null, SPortletAnchor.MINIMIZED_WS);
		miminizedWSAnchor.add(miminizedWS);

		SGridLayout linksPanelLayout = new SGridLayout(1);
		linksPanelLayout.setBorder(1);
		linksPanelLayout.setVgap(2);
		linksPanelLayout.setHgap(2);
		final SPanel linksPanel = new SPanel(linksPanelLayout);

		linksPanel.add(normalWSAnchor);
		linksPanel.add(maximizedWSAnchor);
		linksPanel.add(miminizedWSAnchor);

		return linksPanel;
	}

}
