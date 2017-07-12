package org.eaSTars.sca.dao.impl;

import java.util.Optional;

import org.eaSTars.dblayer.dao.FilterEntry;
import org.eaSTars.dblayer.dao.impl.DefaultAbstractDBLayerDAO;
import org.eaSTars.sca.dao.JavaModuleDAO;
import org.eaSTars.sca.model.JavaModuleModel;

public class DefaultJavaModuleDAO extends DefaultAbstractDBLayerDAO implements JavaModuleDAO {

	@Override
	public JavaModuleModel getModuleByName(String name) {
		return queryModel(JavaModuleModel.class, new FilterEntry("name", name));
	}
	
	@Override
	public JavaModuleModel createJavaModule(String name, boolean isProject, String path) {
		return Optional.ofNullable(queryModel(JavaModuleModel.class, new FilterEntry("name", name)))
		.orElseGet(() -> {
			JavaModuleModel result = new JavaModuleModel();
			result.setName(name);
			result.setIsProject(isProject);
			result.setPath(path);
			saveModel(result);
			return result;
		});
	}
}
