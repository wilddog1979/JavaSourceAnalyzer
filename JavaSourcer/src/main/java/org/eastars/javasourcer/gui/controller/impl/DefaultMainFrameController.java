package org.eastars.javasourcer.gui.controller.impl;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPopupMenu.Separator;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.eastars.javasourcer.analyzis.facade.ProjectAnalysisFacade;
import org.eastars.javasourcer.gui.context.ApplicationResources;
import org.eastars.javasourcer.gui.context.ApplicationResources.ResourceBundle;
import org.eastars.javasourcer.gui.controller.ApplicationController;
import org.eastars.javasourcer.gui.controller.DialogControllers;
import org.eastars.javasourcer.gui.service.ApplicationGuiService;
import org.eastars.javasourcer.gui.service.ProjectMapperService;
import org.eastars.javasourcer.gui.service.ProjectService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;

public class DefaultMainFrameController extends AbstractMainFrameController implements ApplicationController, InitializingBean {

	private ProjectMapperService projectMappingService;
	
	private ProjectService projectService;
	
	private ProjectAnalysisFacade projectAnalysisFacade;

	private DialogControllers dialogControllers;
	
	private ApplicationGuiService applicationGuiService;

	private JFrame frame = new JFrame();

	private boolean extendedMenu = false;

	private JScrollPane leftPanel = new JScrollPane();

	private JScrollPane rightPanel = new JScrollPane();

	private JSplitPane splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);

	private ButtonGroup projectGroup = new ButtonGroup();
	private JMenu menuSwitch;

	public DefaultMainFrameController(
			MessageSource messageSource,
			ApplicationGuiService applicationGuiService,
			ProjectMapperService projectMappingService,	
			ProjectService projectService,
			ProjectAnalysisFacade projectAnalysisFacade,
			DialogControllers dialogControllers) {
		super(messageSource, applicationGuiService.getLocale());
		this.applicationGuiService = applicationGuiService;
		this.projectMappingService = projectMappingService;
		this.projectAnalysisFacade = projectAnalysisFacade;
		this.projectService = projectService;
		this.dialogControllers = dialogControllers;
	}

	private void addProjectMenuEntry(JRadioButtonMenuItem menuitem) {
		int index = 0;
		for (Component component : menuSwitch.getMenuComponents()) {
			if (component instanceof Separator) {
				break;
			}
			index++;
		}

		menuSwitch.add(menuitem, index);
		projectGroup.add(menuitem);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		EventQueue.invokeLater(() -> {
			initGui();
			frame.setVisible(true);
		});
	}

	private void initGui() {
		frame.setIconImage(ApplicationResources.APPICON16);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		buildGui();

		buildMenu();

		applicationGuiService.getWindowLocation().ifPresent(frame::setLocation);

		frame.setSize(applicationGuiService.getWindowSize().orElseGet(() -> {
			Dimension dimension = new Dimension(640, 480);
			Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
			frame.setLocation((screensize.width - dimension.width) / 2, (screensize.height - dimension.height) / 2);
			return dimension;
		}));

		applicationGuiService.getDividerLocation().ifPresent(splitpane::setDividerLocation);

		projectService.getProjects().forEach(this::addProjectMenuEntry);
	}
	
	private void buildMenu() {
		JMenuBar menubar = new JMenuBar();

		menuSwitch = createMenu(getResourceBundle(ResourceBundle.MAIN_MENU_SWITCH),
				new JMenuItemSeparator(),
				createMenuItem(getResourceBundle(ResourceBundle.MAIN_MENU_ADD),
						a -> this.actionMainMenuAdd()));

		JMenu menuProject = createMenu(getResourceBundle(ResourceBundle.MAIN_MENU_PROJECT),
				menuSwitch,
				new JMenuItemSeparator(),
				createMenuItem(getResourceBundle(ResourceBundle.MAIN_MENU_ANALYZE),
						a -> this.actionMainMenuAnalyze()),
				new JMenuItemSeparator(),
				createMenuItem(getResourceBundle(ResourceBundle.MAIN_MENU_PROPERTIES),
						a -> this.actionMainMenuProperties()));

		menubar.add(menuProject);

		if (isExtendedMenu()) {
			menubar.add(
					createMenu(getResourceBundle(ResourceBundle.MAIN_MENU_SETTINGS),
							createMenuItem(getResourceBundle(ResourceBundle.MAIN_MENU_PREFERENCES),
									a -> this.showPreferences())));

			menubar.add(
					createMenu(getResourceBundle(ResourceBundle.MAIN_MENU_HELP),
							createMenuItem(getResourceBundle(ResourceBundle.MAIN_MENU_ABOUT),
									a -> this.showAbout())));
		}

		frame.setJMenuBar(menubar);
	}
	
	private void actionMainMenuAdd() {
		dialogControllers.getProjectDialog().getInputData(frame, null, true).ifPresent(c -> {
			JRadioButtonMenuItem menuitem = projectMappingService.mapJRadioButtonMenuItem(c);
			addProjectMenuEntry(menuitem);
			menuitem.setSelected(true);
		});
	}
	
	private void actionMainMenuAnalyze() {
		ButtonModel selection = projectGroup.getSelection();
		if (selection != null) {
			String projectName = selection.getActionCommand();
			projectAnalysisFacade.analyzeProject(projectName);
		}
	}
	
	private void actionMainMenuProperties() {
		ButtonModel selection = projectGroup.getSelection();
		if (selection != null) {
			String projectName = selection.getActionCommand();
			dialogControllers.getProjectDialog().getInputData(frame, projectName, true)
			.ifPresent(updateDTO -> Collections.list(projectGroup.getElements()).stream()
			.filter(b -> projectName.equals(b.getActionCommand()))
			.findFirst().ifPresent(b -> {
				b.setActionCommand(updateDTO.getName());
				b.setText(updateDTO.getName());
			}));
		}
	}

	private void buildGui() {
		JPanel cnt = (JPanel) frame.getContentPane();

		leftPanel.setViewportView(buildTreeView());
		Dimension minsize = leftPanel.getMinimumSize();
		minsize.width = 150;
		leftPanel.setMinimumSize(minsize);

		rightPanel.setViewportView(new JPanel());

		cnt.add(splitpane, BorderLayout.CENTER);

		JPanel bottompane = new JPanel(new BorderLayout());
		bottompane.setBorder(BorderFactory.createLoweredBevelBorder());

		JPanel sequencesettings = new JPanel();

		// bottom panel components must be added here

		bottompane.add(sequencesettings, BorderLayout.EAST);
		cnt.add(bottompane, BorderLayout.SOUTH);
	}

	private Component buildTreeView() {
		DefaultMutableTreeNode defaultroot = new DefaultMutableTreeNode();

		JTree tree = new JTree(defaultroot);
		tree.setRootVisible(false);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		ToolTipManager.sharedInstance().registerComponent(tree);

		return tree;
	}

	@Override
	public void showAbout() {
		dialogControllers.getAboutDialog().showDialog(frame);
	}

	@Override
	public void showPreferences() {
		dialogControllers.getPreferencesDialog().showDialog(frame);
	}

	@Override
	public void dispatchClosingEvent() {
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	}

	public boolean isExtendedMenu() {
		return extendedMenu;
	}

	@Override
	public void setExtendedMenu(boolean extendedMenu) {
		this.extendedMenu = extendedMenu;
	}

}
