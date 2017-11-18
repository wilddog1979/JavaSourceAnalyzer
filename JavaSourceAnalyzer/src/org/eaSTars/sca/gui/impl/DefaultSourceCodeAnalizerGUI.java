package org.eaSTars.sca.gui.impl;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import org.eaSTars.sca.gui.ProgressListener;
import org.eaSTars.sca.gui.SourceCodeAnalizerGUI;

public class DefaultSourceCodeAnalizerGUI extends JFrame implements SourceCodeAnalizerGUI, ProgressListener {

	private static final long serialVersionUID = -7200696599021244065L;

	private static final JLabel overallLabel = new JLabel("Overall");
	
	private static final JLabel subprocessLabel = new JLabel("Subprocess");
	
	private JProgressBar overallProgressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
	
	private JProgressBar subprocessProgressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
	
	private JLabel statusTextLabel = new JLabel("");
	
	public DefaultSourceCodeAnalizerGUI() {
		
	}
	
	public void buildGUI() {
		setUndecorated(true);
		
		Container cnt = getContentPane();
		
		JPanel mainpanel = new JPanel();
		mainpanel.setLayout(new BoxLayout(mainpanel, BoxLayout.Y_AXIS));
		mainpanel.setBorder(BorderFactory.createRaisedBevelBorder());
		
		mainpanel.add(buildProgressPane(overallLabel, overallProgressBar));
		JPanel subprocesspane = buildProgressPane(subprocessLabel, subprocessProgressBar); 
		mainpanel.add(subprocesspane);
		overallProgressBar.setStringPainted(true);
		subprocessProgressBar.setStringPainted(true);
		int labelsize = Math.max(overallLabel.getPreferredSize().width, subprocessLabel.getPreferredSize().width);
		Dimension dim = overallLabel.getPreferredSize();
		dim.width = labelsize;
		overallLabel.setSize(dim);
		overallLabel.setPreferredSize(dim);
		dim = subprocessLabel.getPreferredSize();
		dim.width = labelsize;
		subprocessLabel.setSize(dim);
		subprocessLabel.setPreferredSize(dim);
		dim = overallProgressBar.getPreferredSize();
		dim.width *= 3;
		overallProgressBar.setSize(dim);
		overallProgressBar.setPreferredSize(dim);
		dim = subprocessProgressBar.getPreferredSize();
		dim.width *= 3;
		subprocessProgressBar.setSize(dim);
		subprocessProgressBar.setPreferredSize(dim);
		
		JPanel statusbar = new JPanel();
		statusbar.setLayout(new BoxLayout(statusbar, BoxLayout.X_AXIS));
		statusbar.setBorder(BorderFactory.createLoweredBevelBorder());
		statusbar.add(statusTextLabel);
		statusbar.add(Box.createHorizontalGlue());
		statusbar.setSize(subprocesspane.getPreferredSize());
		statusbar.setPreferredSize(subprocesspane.getPreferredSize());
		mainpanel.add(statusbar);
		
		cnt.add(mainpanel, BorderLayout.CENTER);
		
		pack();
		
		Dimension windowsize = getSize();
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screensize.width - windowsize.width) / 2, (screensize.height - windowsize.height) / 2);
		
		setVisible(true);
	}
	
	@Override
	public void completed() {
		setVisible(false);
		dispose();
	}
	
	private JPanel buildProgressPane(JLabel text, JProgressBar bar) {
		JPanel progresspane = new JPanel();
		progresspane.add(text);
		progresspane.add(bar);
		return progresspane;
	}

	@Override
	public void setOverallCount(int value) {
		overallProgressBar.setValue(0);
		overallProgressBar.setMaximum(value);
	}
	
	@Override
	public void setOverallStep(int value) {
		overallProgressBar.setValue(value);
	}
	
	@Override
	public void setSubprogressCount(int value) {
		subprocessProgressBar.setValue(0);
		subprocessProgressBar.setMaximum(value);
	}
	
	@Override
	public void setSubprogressStep(int value) {
		subprocessProgressBar.setValue(value);
	}
	
	@Override
	public void setStatusText(String text) {
		statusTextLabel.setText(text);
	}
}
