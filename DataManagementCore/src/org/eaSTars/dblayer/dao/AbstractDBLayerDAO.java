package org.eaSTars.dblayer.dao;

import org.eaSTars.dblayer.model.GenericModel;

public interface AbstractDBLayerDAO {
	
	public <T extends GenericModel> void saveModel(T model);
}
