package org.eaSTars.javasourcer.controller.impl;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.Locale;

import javax.annotation.Resource;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPopupMenu.Separator;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eaSTars.javasourcer.configuration.ApplicationResources;
import org.eaSTars.javasourcer.controller.JavaSourcerDataInputDialog;
import org.eaSTars.javasourcer.controller.JavaSourcerDialog;
import org.eaSTars.javasourcer.controller.JavaSourcerMessageDialog;
import org.eaSTars.javasourcer.controller.MainFrameController;
import org.eaSTars.javasourcer.dto.CreateProjectDTO;
import org.eaSTars.javasourcer.facade.ApplicationGuiFacade;
import org.eaSTars.javasourcer.facade.ProjectFacade;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.convert.ConversionFailedException;

import static org.eaSTars.javasourcer.configuration.ApplicationResources.ResourceBundle.*;

public class DefaultMainFrameController extends AbstractFrameController implements MainFrameController, InitializingBean {

	private static final long serialVersionUID = -8106581169394657574L;
	
	private static final Logger LOGGER = LogManager.getLogger(DefaultMainFrameController.class);
	
	private Locale locale;
	
	private MessageSource messageSource;
	
	private ApplicationGuiFacade applicationGuiFacade;
	
	@Autowired
	private ProjectFacade projectFacade;
	
	@Resource(name="aboutdailogcontroller")
	private JavaSourcerDialog aboutDialog;
	
	@Resource(name="createprojectdailogcontroller")
	private JavaSourcerDataInputDialog<CreateProjectDTO> createProjectDialog;
	
	@Resource(name="messagedialog")
	private JavaSourcerMessageDialog messageDialog;
	
	private boolean extendedMenu = false;
	
	private JScrollPane leftPanel = new JScrollPane();
	
	private JScrollPane rightPanel = new JScrollPane();
	
	private JSplitPane splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
	
	private ButtonGroup projectGroup = new ButtonGroup();
	private JMenu menuSwitch;
	
	public DefaultMainFrameController(MessageSource messageSource, ApplicationGuiFacade applicationGuiFacade) {
		this.messageSource = messageSource;
		this.applicationGuiFacade = applicationGuiFacade;
		
		locale = applicationGuiFacade.getLocale();
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
			setVisible(true);
		});
	}
	
	private void initGui() {
		setIconImage(ApplicationResources.APPICON16);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		buildGui();
		
		buildMenu();
		
		applicationGuiFacade.getWindowLocation().ifPresent(this::setLocation);
		
		setSize(applicationGuiFacade.getWindowSize().orElseGet(() -> {
			Dimension dimension = new Dimension(640, 480);
			Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
			setLocation((screensize.width - dimension.width) / 2, (screensize.height - dimension.height) / 2);
			return dimension;
		}));
		
		applicationGuiFacade.getDividerLocation().ifPresent(splitpane::setDividerLocation);
		
		projectFacade.getProjects().forEach(this::addProjectMenuEntry);
	}
	
	private void buildMenu() {
		JMenuBar menubar = new JMenuBar();
		
		menuSwitch = createMenu(getResourceBundle(MAIN_MENU_SWITCH),
				new JMenuItemSeparator(),
				createMenuItem(getResourceBundle(MAIN_MENU_ADD), a -> {
					boolean error = false;
					while(true) {
						try {
							createProjectDialog.getInputData(this, error).ifPresent(c -> {
								JRadioButtonMenuItem menuitem = projectFacade.createProject(c);
								addProjectMenuEntry(menuitem);
								menuitem.setSelected(true);
							});
							break;
						}catch (ConversionFailedException e) {
							messageDialog.showMessage(this, e.getRootCause().getMessage());
							error = true;
						}
					}
				}));
		
		JMenu menuProject = createMenu(getResourceBundle(MAIN_MENU_PROJECT),
				menuSwitch,
				createMenuItem(getResourceBundle(MAIN_MENU_PROPERTIES), a -> {
					ButtonModel selection = projectGroup.getSelection();
					if (selection != null) {
						LOGGER.debug(() -> String.format("Selection %s", selection.getActionCommand()));
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
		
		setJMenuBar(menubar);
	}

	private void buildGui() {
		JPanel cnt = (JPanel) getContentPane();

		InputMap inputmap = cnt.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionmap = cnt.getActionMap();

		String backspace = "backspace";
		inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), backspace);
		actionmap.put(backspace, new AbstractAction() {

			private static final long serialVersionUID = 2010837049172121748L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
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
		//tree.setCellRenderer(new ADashboardTreeCellRenderer());
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		ToolTipManager.sharedInstance().registerComponent(tree);
		
		return tree;
	}

	private String getResourceBundle(String key) {
		return messageSource.getMessage(key, null, locale);
	}
	
	@Override
	public void showAbout() {
		aboutDialog.showDialog(this);
	}

	@Override
	public void showPreferences() {
		LOGGER.debug("showPreferences");
	}

	@Override
	public void dispatchClosingEvent() {
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	public boolean isExtendedMenu() {
		return extendedMenu;
	}

	@Override
	public void setExtendedMenu(boolean extendedMenu) {
		this.extendedMenu = extendedMenu;
	}

}
