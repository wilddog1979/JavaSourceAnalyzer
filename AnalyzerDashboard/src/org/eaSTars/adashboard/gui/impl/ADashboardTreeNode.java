package org.eaSTars.adashboard.gui.impl;

import javax.swing.tree.DefaultMutableTreeNode;

import org.eaSTars.adashboard.gui.dto.ADashboardObjectView;

public class ADashboardTreeNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = -3702757606915523687L;

	public ADashboardTreeNode(ADashboardObjectView javaAssemblyView) {
		super(javaAssemblyView);
	}
	
	@Override
	public boolean isLeaf() {
		return false;
	}
}
