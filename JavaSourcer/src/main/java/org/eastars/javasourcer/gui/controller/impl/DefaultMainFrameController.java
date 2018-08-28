package org.eastars.javasourcer.gui.controller.impl;

import static org.eastars.javasourcer.gui.context.ApplicationResources.ResourceBundle.MAIN_MENU_ABOUT;
import static org.eastars.javasourcer.gui.context.ApplicationResources.ResourceBundle.MAIN_MENU_ADD;
import static org.eastars.javasourcer.gui.context.ApplicationResources.ResourceBundle.MAIN_MENU_HELP;
import static org.eastars.javasourcer.gui.context.ApplicationResources.ResourceBundle.MAIN_MENU_PREFERENCES;
import static org.eastars.javasourcer.gui.context.ApplicationResources.ResourceBundle.MAIN_MENU_PROJECT;
import static org.eastars.javasourcer.gui.context.ApplicationResources.ResourceBundle.MAIN_MENU_PROPERTIES;
import static org.eastars.javasourcer.gui.context.ApplicationResources.ResourceBundle.MAIN_MENU_SETTINGS;
import static org.eastars.javasourcer.gui.context.ApplicationResources.ResourceBundle.MAIN_MENU_SWITCH;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;

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

import org.eastars.javasourcer.gui.context.ApplicationResources;
import org.eastars.javasourcer.gui.controller.JavaSourcerDataInputDialog;
import org.eastars.javasourcer.gui.controller.JavaSourcerDialog;
import org.eastars.javasourcer.gui.controller.JavaSourcerMessageDialog;
import org.eastars.javasourcer.gui.controller.MainFrameController;
import org.eastars.javasourcer.gui.dto.ProjectDTO;
import org.eastars.javasourcer.gui.exception.JavaSourcerProjectAlreadyExistsException;
import org.eastars.javasourcer.gui.service.ApplicationGuiService;
import org.eastars.javasourcer.gui.service.ProjectService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.core.convert.ConversionFailedException;

public class DefaultMainFrameController extends AbstractInternationalizableController implements ControllerSupport, MainFrameController, InitializingBean {

	private ProjectService projectService;

	private JavaSourcerDialog aboutDialog;
	
	private JavaSourcerDialog preferencesDialog;

	private JavaSourcerDataInputDialog<ProjectDTO> projectDialog;

	private JavaSourcerMessageDialog messageDialog;

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
			ProjectService projectService,
			@Qualifier("aboutdailogcontroller") JavaSourcerDialog aboutDialog,
			@Qualifier("preferencesdailogcontroller") JavaSourcerDialog preferencesDialog,
			@Qualifier("projectdailogcontroller") JavaSourcerDataInputDialog<ProjectDTO> projectDialog,
			JavaSourcerMessageDialog messageDialog) {
		super(messageSource, applicationGuiService.getLocale());
		this.applicationGuiService = applicationGuiService;
		this.projectService = projectService;
		this.aboutDialog = aboutDialog;
		this.preferencesDialog = preferencesDialog;
		this.projectDialog = projectDialog;
		this.messageDialog = messageDialog;
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

		menuSwitch = createMenu(getResourceBundle(MAIN_MENU_SWITCH),
				new JMenuItemSeparator(),
				createMenuItem(getResourceBundle(MAIN_MENU_ADD), a -> 
				actionProjectDialog(Optional.empty(), c -> {
					JRadioButtonMenuItem menuitem = projectService.createProject(c);
					addProjectMenuEntry(menuitem);
					menuitem.setSelected(true);
				})));

		JMenu menuProject = createMenu(getResourceBundle(MAIN_MENU_PROJECT),
				menuSwitch,
				createMenuItem(getResourceBundle(MAIN_MENU_PROPERTIES), a -> {
					ButtonModel selection = projectGroup.getSelection();
					if (selection != null) {
						String name = selection.getActionCommand();
						actionProjectDialog(projectService.getProject(name), updateDTO -> {
							projectService.updateProject(name, updateDTO);
							Collections.list(projectGroup.getElements()).stream()
							.filter(b -> name.equals(b.getActionCommand()))
							.findFirst().ifPresent(b -> {
								b.setActionCommand(updateDTO.getName());
								b.setText(updateDTO.getName());
							});
						});
					}
				}));

		menubar.add(menuProject);

		if (isExtendedMenu()) {
			menubar.add(
					createMenu(getResourceBundle(MAIN_MENU_SETTINGS),
							createMenuItem(getResourceBundle(MAIN_MENU_PREFERENCES), a -> this.showPreferences())));

			menubar.add(
					createMenu(getResourceBundle(MAIN_MENU_HELP),
							createMenuItem(getResourceBundle(MAIN_MENU_ABOUT), a -> this.showAbout())));
		}

		frame.setJMenuBar(menubar);
	}

	private void actionProjectDialog(Optional<ProjectDTO> dto, Consumer<ProjectDTO> consumeNewDTO) {
		boolean error = false;
		while(true) {
			try {
				projectDialog.getInputData(frame, dto, error)
				.ifPresent(consumeNewDTO::accept);
				break;
			}catch (ConversionFailedException e) {
				messageDialog.showMessage(frame, e.getRootCause().getMessage());
				error = true;
			}catch (JavaSourcerProjectAlreadyExistsException e) {
				messageDialog.showMessage(frame, e.getMessage());
				error = true;
			}
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
		aboutDialog.showDialog(frame);
	}

	@Override
	public void showPreferences() {
		preferencesDialog.showDialog(frame);
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
