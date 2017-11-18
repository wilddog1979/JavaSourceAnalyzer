package org.eaSTars.dblayer.dao;

import org.eaSTars.dblayer.model.GenericModel;

public interface AbstractDBLayerDAO {
	
	public <T extends GenericModel> T getModelByPK(Class<T> modelclass, Integer id);
	
	public <T extends GenericModel> void saveModel(T model);
}
