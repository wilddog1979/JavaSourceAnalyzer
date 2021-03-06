package org.eaSTars.adashboard.gui.impl;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.eaSTars.adashboard.gui.dto.ADashboardObjectType;
import org.eaSTars.adashboard.gui.dto.ADashboardObjectView;
import org.eaSTars.adashboard.gui.resources.Resources;

public class ADashboardTreeCellRenderer extends DefaultTreeCellRenderer {
	
	private static final long serialVersionUID = -4611277782352746408L;

	private ADashboardObjectType type;
	
	@Override
	public Icon getLeafIcon() {
		Icon result = null;
		if (type != null) {
			switch(type) {
			case PACKAGE:
				result = Resources.PACKAGEICON;
				break;
			case CLASS:
			case INTERFACE:
			case ENUM:
			case ANNOTATION:
				result = Resources.CLASSICON;
				break;
			default:
				result = super.getLeafIcon();
				break;
			}
		}
		
		return result;
	}
	
	@Override
	public Icon getOpenIcon() {
		return getLeafIcon();
	}
	
	@Override
	public Icon getClosedIcon() {
		return getLeafIcon();
	}
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
			int row, boolean hasFocus) {
		String tooltiptext = null;
		if (value instanceof DefaultMutableTreeNode && ((DefaultMutableTreeNode)value).getUserObject() instanceof ADashboardObjectView) {
			ADashboardObjectView jav = (ADashboardObjectView) ((DefaultMutableTreeNode)value).getUserObject();
			type = jav.getType();
			tooltiptext = jav.getTooltip();
		}
		JLabel result = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		if (tooltiptext != null) {
			result.setToolTipText(tooltiptext);
		}
		return result;
	}
}
