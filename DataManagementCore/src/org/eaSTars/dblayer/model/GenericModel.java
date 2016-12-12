package org.eaSTars.dblayer.model;

public abstract class GenericModel {

	@Attribute(column="PK", primarykey=true)
	private Integer PK;

	public Integer getPK() {
		return PK;
	}

	public void setPK(Integer PK) {
		this.PK = PK;
	}
}
