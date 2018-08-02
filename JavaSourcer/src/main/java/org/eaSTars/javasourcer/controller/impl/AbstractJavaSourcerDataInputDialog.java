package org.eaSTars.javasourcer.controller.impl;

import java.awt.Component;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Spring;
import javax.swing.SpringLayout;

import org.eaSTars.javasourcer.controller.JavaSourcerDataInputDialog;
import org.springframework.context.MessageSource;

public abstract class AbstractJavaSourcerDataInputDialog<T> extends AbstractInternationalizableController implements JavaSourcerDataInputDialog<T> {

	private JPanel settingspanel;
	
	protected abstract String getTitle();
	
	protected abstract JPanel buildPanel();
	
	protected abstract void cleanupPanel();
	
	protected abstract void initializePanel(T parameter);
	
	protected abstract T getInputData();
	
	public AbstractJavaSourcerDataInputDialog(MessageSource messageSource, Locale locale) {
		super(messageSource, locale);
	}
	
	private JPanel getDialogPanel(T parameter, boolean error) {
		if (settingspanel == null) {
			settingspanel = buildPanel();
		}
		if (!error) {
			if (parameter != null) {
				initializePanel(parameter);
			} else {
				cleanupPanel();
			}
		}
		return settingspanel;
	}
	
	@Override
	public Optional<T> getInputData(Component parent, T input, boolean error) {
		return getInputDataInternal(parent, getDialogPanel(input, error));
	}
	
	@Override
	public Optional<T> getInputData(Component parent, boolean error) {
		return getInputDataInternal(parent, getDialogPanel(null, error));
	}
	
	protected Optional<T> getInputDataInternal(Component parent, JPanel panel) {
		if (JOptionPane.showConfirmDialog(null, panel, getTitle(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
			return Optional.of(getInputData());
		} else {
			return Optional.empty();
		}
	}
	
    protected JPanel makeCompactGrid(
    		List<Component> components,
    		int rows, int cols,
    		int initialX, int initialY,
    		int xPad, int yPad) {
        SpringLayout layout = new SpringLayout();
    	JPanel panel = new JPanel(layout);
    	components.forEach(panel::add);
    	
    	//Align all cells in each column and make them the same width.
        Spring x = Spring.constant(initialX);
        for (int c = 0; c < cols; c++) {
            Spring width = Spring.constant(0);
            for (int r = 0; r < rows; r++) {
            	width = Spring.max(width, layout.getConstraints(components.get(r * cols + c)).getWidth());
            }
            for (int r = 0; r < rows; r++) {
                SpringLayout.Constraints constraints =
                		layout.getConstraints(components.get(r * cols + c));
                constraints.setX(x);
                constraints.setWidth(width);
            }
            x = Spring.sum(x, Spring.sum(width, Spring.constant(xPad)));
        }

        //Align all cells in each row and make them the same height.
        Spring y = Spring.constant(initialY);
        for (int r = 0; r < rows; r++) {
            Spring height = Spring.constant(0);
            for (int c = 0; c < cols; c++) {
                height = Spring.max(height, layout.getConstraints(components.get(r * cols + c)).getHeight());
            }
            for (int c = 0; c < cols; c++) {
                SpringLayout.Constraints constraints =
                		layout.getConstraints(components.get(r * cols + c));
                constraints.setY(y);
                constraints.setHeight(height);
            }
            y = Spring.sum(y, Spring.sum(height, Spring.constant(yPad)));
        }

        //Set the parent's size.
        SpringLayout.Constraints pCons = layout.getConstraints(panel);
        pCons.setConstraint(SpringLayout.SOUTH, y);
        pCons.setConstraint(SpringLayout.EAST, x);
        
        return panel;
    }
	
}
