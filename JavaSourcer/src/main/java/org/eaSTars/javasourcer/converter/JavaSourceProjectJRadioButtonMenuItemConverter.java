package org.eaSTars.javasourcer.converter;

import javax.swing.JRadioButtonMenuItem;

import org.eaSTars.javasourcer.model.JavaSourceProject;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class JavaSourceProjectJRadioButtonMenuItemConverter
		implements Converter<JavaSourceProject, JRadioButtonMenuItem> {

	@Override
	public JRadioButtonMenuItem convert(JavaSourceProject source) {
		JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(source.getName());
		menuItem.setActionCommand(source.getName());
		return menuItem;
	}

}
