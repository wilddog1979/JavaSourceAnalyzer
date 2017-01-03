package org.eaSTars.adashboard.gui.dto;

public class ViewHistoryEntry {

	private ViewType viewType;
	
	private Integer PK;

	public ViewType getViewType() {
		return viewType;
	}

	public void setViewType(ViewType viewType) {
		this.viewType = viewType;
	}

	public Integer getPK() {
		return PK;
	}

	public void setPK(Integer pK) {
		PK = pK;
	}
}
