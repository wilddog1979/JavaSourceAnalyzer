package org.eaSTars.javasourcer.controller.impl;

import java.util.Optional;

import org.eaSTars.javasourcer.controller.JavaSourcerDataInputDialog;
import org.eaSTars.javasourcer.dto.CreateProjectDTO;
import org.springframework.stereotype.Component;

@Component("createprojectdailogcontroller")
public class DefaultJavaSourceCreateProjectDialogController implements JavaSourcerDataInputDialog<CreateProjectDTO> {

	@Override
	public Optional<CreateProjectDTO> getInputData(java.awt.Component parent) {
		CreateProjectDTO dto = new CreateProjectDTO();
		dto.setName("Test project dto");
		return Optional.of(dto);
	}

}
