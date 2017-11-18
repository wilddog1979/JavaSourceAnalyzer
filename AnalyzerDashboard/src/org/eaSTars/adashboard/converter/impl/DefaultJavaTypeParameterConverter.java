package org.eaSTars.adashboard.converter.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.eaSTars.adashboard.gui.MainFrameDelegate;
import org.eaSTars.adashboard.gui.dto.JavaTypeParameterView;
import org.eaSTars.adashboard.gui.impl.AssemblyNameLabel;
import org.eaSTars.sca.dao.TypeParameterEntry;
import org.eaSTars.sca.model.JavaAssemblyModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.converter.Converter;

public class DefaultJavaTypeParameterConverter implements Converter<TypeParameterEntry, JavaTypeParameterView> {

	@Autowired
	@Lazy
	private MainFrameDelegate delegate;
	
	@Override
	public JavaTypeParameterView convert(TypeParameterEntry source) {
		JavaTypeParameterView result = new JavaTypeParameterView(delegate);
		
		result.setAssemblyLink(source.getTypeParameter().getName(), null);
		result.addBounds(convertBounds(source.getBounds()));
		
		return result;
	}
	
	private List<AssemblyNameLabel> convertBounds(List<JavaAssemblyModel> bounds) {
		return bounds.stream().map(b -> {
			AssemblyNameLabel target = new AssemblyNameLabel(delegate);
			target.setAssemblyLink(b.getName(), b.getPK());
			return target;
		}).collect(Collectors.toList());
	}

	public MainFrameDelegate getDelegate() {
		return delegate;
	}

	public void setDelegate(MainFrameDelegate delegate) {
		this.delegate = delegate;
	}

}
