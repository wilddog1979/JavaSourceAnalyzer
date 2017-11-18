package org.eaSTars.adashboard.gui.dto;

public class ADashboardObjectView {

	private Integer id;
	
	private String name;
	
	private String tooltip;
	
	private ADashboardObjectType type;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getTooltip() {
		return tooltip;
	}

	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	public ADashboardObjectType getType() {
		return type;
	}

	public void setType(ADashboardObjectType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return getName();
	}
}
