package org.eastars.javasourcer.gui.controller.impl;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class PackageTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 710734734326814038L;

	private List<String> columnNames = new ArrayList<>();
	
	private List<String> packages = new ArrayList<>();
	
	@Override
	public String getColumnName(int column) {
		return columnNames.get(column);
	}
	
	@Override
	public int getRowCount() {
		return packages.size();
	}

	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return packages.get(rowIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		packages.set(rowIndex, aValue.toString());
		fireTableRowsUpdated(rowIndex, rowIndex);
	}

	public List<String> getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(List<String> columnNames) {
		this.columnNames = columnNames;
		fireTableDataChanged();
	}

	public List<String> getPackages() {
		return packages;
	}

	public void setPackages(List<String> packages) {
		this.packages = packages;
		fireTableDataChanged();
	}
	
	public void addNewPackage(String packagename) {
		int index = getRowCount();
		packages.add(packagename);
		fireTableRowsInserted(index, index);
	}
	
	public void removePackage(int index) {
		packages.remove(index);
		fireTableRowsDeleted(index, index);
	}

}
