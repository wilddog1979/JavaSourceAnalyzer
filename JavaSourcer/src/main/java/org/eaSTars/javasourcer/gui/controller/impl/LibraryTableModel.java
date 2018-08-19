package org.eaSTars.javasourcer.gui.controller.impl;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.eaSTars.javasourcer.gui.dto.LibraryDTO;

public class LibraryTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1905045874029087333L;

	private List<String> columnNames = new ArrayList<>();
	
	private List<LibraryDTO> libraries = new ArrayList<>();
	
	@Override
	public String getColumnName(int column) {
		return columnNames.get(column);
	}
	
	@Override
	public int getRowCount() {
		return libraries.size();
	}

	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		LibraryDTO library = libraries.get(rowIndex);
		return library != null ? library.getName() : null;
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		LibraryDTO library = libraries.get(rowIndex);
		if (library != null) {
			library.setName(aValue.toString());
		}
		fireTableRowsUpdated(rowIndex, rowIndex);
	}

	public List<LibraryDTO> getLibraries() {
		return libraries;
	}

	public void setLibraries(List<LibraryDTO> libraries) {
		this.libraries = libraries;
		fireTableDataChanged();
	}

	public void addNewLibrary(LibraryDTO dto) {
		int index = getRowCount();
		libraries.add(dto);
		fireTableRowsInserted(index, index);
	}

	public List<String> getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(List<String> columnNames) {
		this.columnNames = columnNames;
		fireTableDataChanged();
	}
	
}
