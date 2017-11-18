package org.eaSTars.sca.service.impl;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eaSTars.dblayer.dao.DatabaseConnectionException;
import org.eaSTars.dblayer.model.GenericModel;
import org.eaSTars.dblayer.service.impl.DefaultDBLayerMaintenanceService;
import org.eaSTars.sca.model.JavaAssemblyModel;
import org.eaSTars.sca.model.JavaAssemblyTypeParameterModel;
import org.eaSTars.sca.model.JavaExtendsImplementsModel;
import org.eaSTars.sca.model.JavaFieldModel;
import org.eaSTars.sca.model.JavaMethodModel;
import org.eaSTars.sca.model.JavaMethodParameterModel;
import org.eaSTars.sca.model.JavaMethodTypeParameterModel;
import org.eaSTars.sca.model.JavaModuleModel;
import org.eaSTars.sca.model.JavaObjectTypeModel;
import org.eaSTars.sca.model.JavaThrowsModel;
import org.eaSTars.sca.model.JavaTypeArgumentModel;
import org.eaSTars.sca.model.JavaTypeBoundModel;
import org.eaSTars.sca.model.JavaTypeModel;
import org.eaSTars.sca.model.JavaTypeParameterModel;
import org.eaSTars.sca.service.SCADatabaseMaintenanceService;

public class DefaultSCADatabaseMaintenanceService extends DefaultDBLayerMaintenanceService implements SCADatabaseMaintenanceService {

	private static final Logger LOGGER = LogManager.getLogger(DefaultDBLayerMaintenanceService.class);
	
	private static final List<Class<? extends GenericModel>> MODELS = Arrays.asList(
			JavaModuleModel.class,
			JavaAssemblyModel.class, JavaObjectTypeModel.class, JavaAssemblyTypeParameterModel.class,
			JavaExtendsImplementsModel.class,
			JavaFieldModel.class,
			JavaMethodModel.class, JavaMethodParameterModel.class, JavaThrowsModel.class, JavaMethodTypeParameterModel.class,
			JavaTypeModel.class, JavaTypeArgumentModel.class, JavaTypeParameterModel.class, JavaTypeBoundModel.class
			);	

	@Override
	public void init() {
		MODELS.forEach(model -> {
			try {
				dropModel(model);
			} catch (DatabaseConnectionException e) {
				LOGGER.warn("Error while dropping table", e);
			}
		});
		
		MODELS.forEach(model -> {
			try {
				createModel(model);
			}catch (DatabaseConnectionException e) {
				LOGGER.warn("Error while creating table", e);
			}
		});
	}

}
