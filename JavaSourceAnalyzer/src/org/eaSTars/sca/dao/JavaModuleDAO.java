package org.eaSTars.sca.dao;

import org.eaSTars.dblayer.dao.AbstractDBLayerDAO;
import org.eaSTars.sca.model.JavaModuleModel;

public interface JavaModuleDAO extends AbstractDBLayerDAO{

	public JavaModuleModel createJavaModule(String name, String path);
}
