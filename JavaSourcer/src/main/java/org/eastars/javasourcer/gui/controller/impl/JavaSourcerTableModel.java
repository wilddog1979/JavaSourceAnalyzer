package org.eastars.javasourcer.gui.controller.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class JavaSourcerTableModel<T extends Serializable> extends AbstractTableModel {

	private static final long serialVersionUID = -4542278567019542376L;

	private boolean isEditable;
	
	private List<String> columnNames = new ArrayList<>();
	
	private List<T> entries = new ArrayList<>();
	
	private TableModelValueGetter<T> valueGetter;
	
	private TableModelValueSetter<T> valueSetter;
	
	public JavaSourcerTableModel(
			boolean isEditable,
			TableModelValueGetter<T> valueGetter,
			TableModelValueSetter<T> valueSetter) {
		this.isEditable = isEditable;
		this.valueGetter = valueGetter;
		this.valueSetter = valueSetter;
	}
	
	@Override
	public int getRowCount() {
		return entries.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.size();
	}

	@Override
	public String getColumnName(int column) {
		return columnNames.get(column);
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return valueGetter.getValue(entries.get(rowIndex), columnIndex);
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		entries.set(rowIndex, valueSetter.setValue(entries.get(rowIndex), columnIndex, aValue));
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return isEditable;
	}

	public List<String> getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(List<String> columnNames) {
		this.columnNames = columnNames;
		fireTableDataChanged();
	}

	public List<T> getEntries() {
		return entries;
	}

	public void setEntries(List<T> entries) {
		this.entries = entries;
		fireTableDataChanged();
	}
	
	public void addNewEntry(T entry) {
		int index = getRowCount();
		entries.add(entry);
		fireTableRowsInserted(index, index);
	}
	
	public void removeEntry(int index) {
		entries.remove(index);
		fireTableRowsDeleted(index, index);
	}

}
