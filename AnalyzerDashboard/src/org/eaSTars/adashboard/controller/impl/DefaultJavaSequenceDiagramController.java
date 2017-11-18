package org.eaSTars.adashboard.controller.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;

import javax.swing.JOptionPane;

import org.eaSTars.adashboard.controller.AdashboardDelegate;
import org.eaSTars.adashboard.controller.JavaSequenceDiagramController;
import org.eaSTars.adashboard.controller.JavaSequenceDiagramDelegate;
import org.eaSTars.adashboard.gui.FileDialog;
import org.eaSTars.adashboard.gui.PNGFileFilter;
import org.eaSTars.adashboard.gui.PlantUMLFileFilter;
import org.eaSTars.adashboard.gui.dto.JavaSequenceDiagramView;
import org.eaSTars.adashboard.service.JavaSequenceService;
import org.eaSTars.adashboard.service.dto.JavaSequenceScript;
import org.springframework.core.convert.ConversionService;

public class DefaultJavaSequenceDiagramController implements JavaSequenceDiagramController, JavaSequenceDiagramDelegate {

	private FileDialog fileDialog;
	
	private AdashboardDelegate adashboardDelegate;
	
	private JavaSequenceService javaSequenceService;
	
	private ConversionService conversionService;
	
	private JavaSequenceScript sequencescript;
	
	@Override
	public JavaSequenceDiagramView getSequenceView(Integer methodid, boolean ordered) {
		sequencescript = javaSequenceService.generateMethodSequence(methodid);
		sequencescript.setSorted(ordered);
		
		return createSequenceDiagramView();
	}

	@Override
	public JavaSequenceDiagramView updateSequenceView(boolean ordered) {
		sequencescript.setSorted(ordered);
		
		return createSequenceDiagramView();
	}
	
	private JavaSequenceDiagramView createSequenceDiagramView() {
		JavaSequenceDiagramView jsdw = conversionService.convert(sequencescript, JavaSequenceDiagramView.class);
		jsdw.setDelegate(this);
		return jsdw;
	}
	
	@Override
	public void saveScript() {
		Optional.ofNullable(fileDialog.showSaveDialog(null, new PlantUMLFileFilter()))
		.ifPresent(file -> {
			if (!file.getName().toLowerCase().endsWith(PlantUMLFileFilter.EXTENSION)) {
				file = new File(file.getAbsolutePath()+PlantUMLFileFilter.EXTENSION);
			}
			try (FileOutputStream fout = new FileOutputStream(file)) {
				fout.write(sequencescript.buildString(adashboardDelegate.getOrderSequence()).getBytes());
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Error while saving PlantUML file\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
	}

	@Override
	public void saveImage() {
		Optional.ofNullable(fileDialog.showSaveDialog(null, new PNGFileFilter()))
		.ifPresent(file -> {
			if (!file.getName().toLowerCase().endsWith(PNGFileFilter.EXTENSION)) {
				file = new File(file.getAbsolutePath()+PNGFileFilter.EXTENSION);
			}
			try (FileOutputStream fout = new FileOutputStream(file)) {
				ByteArrayOutputStream bo = conversionService.convert(sequencescript, ByteArrayOutputStream.class);
				fout.write(bo.toByteArray());
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Error while saving PNG file\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
	}
	
	public FileDialog getFileDialog() {
		return fileDialog;
	}

	public void setFileDialog(FileDialog fileDialog) {
		this.fileDialog = fileDialog;
	}

	public AdashboardDelegate getAdashboardDelegate() {
		return adashboardDelegate;
	}

	public void setAdashboardDelegate(AdashboardDelegate adashboardDelegate) {
		this.adashboardDelegate = adashboardDelegate;
	}

	public JavaSequenceService getJavaSequenceService() {
		return javaSequenceService;
	}

	public void setJavaSequenceService(JavaSequenceService javaSequenceService) {
		this.javaSequenceService = javaSequenceService;
	}

	public ConversionService getConversionService() {
		return conversionService;
	}

	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}
}
