package org.eaSTars.adashboard.gui.impl;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Optional;
import java.util.Stack;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeSelectionModel;

import org.eaSTars.adashboard.controller.ADashboardController;
import org.eaSTars.adashboard.controller.JavaAssemblyController;
import org.eaSTars.adashboard.controller.JavaSequenceDiagramController;
import org.eaSTars.adashboard.gui.MainFrame;
import org.eaSTars.adashboard.gui.MainFrameAdapter;
import org.eaSTars.adashboard.gui.MainFrameDelegate;
import org.eaSTars.adashboard.gui.dto.ADashboardObjectType;
import org.eaSTars.adashboard.gui.dto.ADashboardObjectView;
import org.eaSTars.adashboard.gui.dto.JavaSequenceDiagramView;
import org.eaSTars.adashboard.gui.dto.ViewHistoryEntry;
import org.eaSTars.adashboard.gui.dto.ViewType;

public class DefaultMainFrame extends JFrame implements MainFrame, MainFrameDelegate, TreeWillExpandListener {

	private static final long serialVersionUID = -8684346050770527232L;

	private MainFrameAdapter mainFrameListener;

	private JavaAssemblyController javaAssemblyController;

	private JavaSequenceDiagramController javaSequenceDiagramController;

	private ADashboardController adashboardController;
	
	private Stack<ViewHistoryEntry> assemblyHistory = new Stack<ViewHistoryEntry>();

	private JScrollPane leftPanel = new JScrollPane();
	
	private JScrollPane rightPanel = new JScrollPane();
	
	private JCheckBox orderchecker = new JCheckBox("Ordered sequence");
	
	private int sliderpreviousvalue = 100;
	
	private JSlider imageScalingSlider = new JSlider(JSlider.HORIZONTAL, 10, 200, sliderpreviousvalue);
	
	private JPanel currentview;
	
	private JSplitPane splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
	
	public void init() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addWindowListener(mainFrameListener);

		buildGUI();

		Optional.ofNullable(adashboardController.getWindowLocation()).ifPresent(l -> setLocation(l));
		
		Dimension dimension = adashboardController.getWindowSize();
		if (dimension != null) {
			setSize(dimension);
		} else {
			dimension = new Dimension(640, 480);
			setSize(dimension);
			Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
			setLocation((screensize.width - dimension.width) / 2, (screensize.height - dimension.height) / 2);
		}
		
		Optional.ofNullable(adashboardController.getDividerLocation()).ifPresent(d -> splitpane.setDividerLocation(d));
		
		mainFrameListener.addFrameClosingListener(w -> {
			adashboardController.setWindowLocation(w.getLocation());
			adashboardController.setWindowSize(w.getSize());
			adashboardController.setDividerLocation(splitpane.getDividerLocation());
			adashboardController.saveSettings();
		});
	}

	@Override
	public void dispatchClosingEvent() {
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}
	
	@Override
	public void buildMenu(boolean extended, ADashboardController controller) {
		JMenuBar menubar = new JMenuBar();
		
		if (extended) {
			JMenu menuSettings = new JMenu("Settings");
			JMenuItem menuitemPreferences = new JMenuItem("Preferences...");
			menuitemPreferences.addActionListener(l -> controller.showPreferences());
			menuSettings.add(menuitemPreferences);
			menubar.add(menuSettings);
			
			JMenu menuHelp = new JMenu("Help");
			JMenuItem menuitemAbout = new JMenuItem("About...");
			menuitemAbout.addActionListener(l -> controller.showAbout());
			menuHelp.add(menuitemAbout);
			menubar.add(menuHelp);
		}
		
		setJMenuBar(menubar);
	}
	
	private void buildGUI() {
		JPanel cnt = (JPanel) getContentPane();

		InputMap inputmap = cnt.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionmap = cnt.getActionMap();

		String backspace = "backspace";
		inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), backspace);
		actionmap.put(backspace, new AbstractAction() {

			private static final long serialVersionUID = 6767535390505400110L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (assemblyHistory.size() > 1) {
					assemblyHistory.pop();
					ViewHistoryEntry historyentry = assemblyHistory.pop();
					switch(historyentry.getViewType()) {
					case Assembly:
						openAssembly(historyentry.getPK());
						break;
					case Method:
						openMethod(historyentry.getPK());
						break;
					default:
						break;
					}
				}
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
		setEnabledSequenceControllers(false);
		
		sequencesettings.add(orderchecker);
		orderchecker.addActionListener(a -> {
			JavaSequenceDiagramView panel = javaSequenceDiagramController.updateSequenceView(orderchecker.isSelected());
			currentview = panel;
			rightPanel.setViewportView(panel);
			
			int slidervalue = imageScalingSlider.getValue();
			if (slidervalue != 100) {
				panel.scaleImage(slidervalue);
			}
		});
		
		imageScalingSlider.setMinorTickSpacing(10);
		imageScalingSlider.setPaintTicks(true);
		imageScalingSlider.setSnapToTicks(true);
		imageScalingSlider.addChangeListener(e -> {
			int slidervalue = imageScalingSlider.getValue();
			if (currentview != null && currentview instanceof JavaSequenceDiagramView && !imageScalingSlider.getValueIsAdjusting() && slidervalue % 10 == 0 && sliderpreviousvalue != slidervalue) {
				((JavaSequenceDiagramView)currentview).scaleImage(slidervalue);
				sliderpreviousvalue = slidervalue;
			}
		});
		sequencesettings.add(imageScalingSlider);
		
		bottompane.add(sequencesettings, BorderLayout.EAST);
		cnt.add(bottompane, BorderLayout.SOUTH);
	}

	private Component buildTreeView() {
		DefaultMutableTreeNode defaultroot = new DefaultMutableTreeNode();

		javaAssemblyController.getChildAssemblies(null)
		.forEach(a -> defaultroot.add(new ADashboardTreeNode(a)));

		JTree tree = new JTree(defaultroot);
		tree.setRootVisible(false);
		tree.setCellRenderer(new ADashboardTreeCellRenderer());
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		ToolTipManager.sharedInstance().registerComponent(tree);

		tree.addTreeWillExpandListener(this);
		tree.addTreeSelectionListener(event -> {
			Object obj = event.getPath().getLastPathComponent();
			if (obj instanceof ADashboardTreeNode && ((ADashboardTreeNode)obj).getUserObject() instanceof ADashboardObjectView) {
				ADashboardTreeNode node = (ADashboardTreeNode) obj;
				ADashboardObjectView jav = (ADashboardObjectView) node.getUserObject();

				if (jav.getType() == ADashboardObjectType.METHOD) {
					openMethod(jav.getId());
				} else if (jav.getType() != ADashboardObjectType.PACKAGE) {
					openAssembly(jav.getId());
				}
			}
		});

		return tree;
	}

	@Override
	public void openAssembly(Integer id) {
		setEnabledSequenceControllers(false);
		openDetailView(id, ViewType.Assembly, javaAssemblyController.getAssemblyFullView(id));
	}

	@Override
	public void openMethod(Integer id) {
		imageScalingSlider.setValue(100);
		setEnabledSequenceControllers(true);
		openDetailView(id, ViewType.Method, javaSequenceDiagramController.getSequenceView(id, orderchecker.isSelected()));
	}
	
	private void setEnabledSequenceControllers(boolean value) {
		orderchecker.setEnabled(value);
		imageScalingSlider.setEnabled(value);
	}

	private void openDetailView(Integer id, ViewType viewtype, JPanel panel) {
		if (panel != null) {
			currentview = panel;
			ViewHistoryEntry historyentry = new ViewHistoryEntry();
			historyentry.setViewType(viewtype);
			historyentry.setPK(id);
			assemblyHistory.push(historyentry);
			rightPanel.setViewportView(panel);
		} else {
			rightPanel.setViewportView(new JPanel());
		}
	}
	
	@Override
	public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
		Object obj = event.getPath().getLastPathComponent();
		ADashboardTreeNode node;
		if (obj instanceof ADashboardTreeNode &&
				(node = (ADashboardTreeNode)obj).getUserObject() instanceof ADashboardObjectView) {
			ADashboardObjectView objectview =  (ADashboardObjectView) node.getUserObject();
			if (objectview.getType() != ADashboardObjectType.METHOD) {
				ADashboardObjectView jav = (ADashboardObjectView) node.getUserObject();
				javaAssemblyController.getChildAssemblies(jav.getId())
				.forEach(a -> node.add(new ADashboardTreeNode(a)));
				javaAssemblyController.getAssemblyMethods(jav.getId())
				.forEach(m -> node.add(new ADashboardTreeNode(m)));
			}

		}
	}

	@Override
	public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {

	}

	public WindowListener getMainFrameListener() {
		return mainFrameListener;
	}

	public void setMainFrameListener(MainFrameAdapter mainFrameListener) {
		this.mainFrameListener = mainFrameListener;
	}

	public JavaAssemblyController getJavaAssemblyController() {
		return javaAssemblyController;
	}

	public void setJavaAssemblyController(JavaAssemblyController javaAssemblyController) {
		this.javaAssemblyController = javaAssemblyController;
	}

	public JavaSequenceDiagramController getJavaSequenceDiagramController() {
		return javaSequenceDiagramController;
	}

	public void setJavaSequenceDiagramController(JavaSequenceDiagramController javaSequenceDiagramController) {
		this.javaSequenceDiagramController = javaSequenceDiagramController;
	}

	public ADashboardController getAdashboardController() {
		return adashboardController;
	}

	public void setAdashboardController(ADashboardController adashboardController) {
		this.adashboardController = adashboardController;
	}
}
