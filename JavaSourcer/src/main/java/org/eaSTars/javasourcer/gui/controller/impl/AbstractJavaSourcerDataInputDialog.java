package org.eaSTars.javasourcer.gui.controller.impl;

import java.awt.Color;
import java.awt.Component;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Spring;
import javax.swing.SpringLayout;

import org.eaSTars.javasourcer.gui.context.ApplicationResources.ResourceBundle;
import org.eaSTars.javasourcer.gui.controller.JavaSourcerDataInputDialog;
import org.springframework.context.MessageSource;

public abstract class AbstractJavaSourcerDataInputDialog<T> extends AbstractInternationalizableController implements JavaSourcerDataInputDialog<T> {

	private JPanel settingspanel;
	
	protected abstract JPanel buildPanel();
	
	protected abstract void cleanupPanel();
	
	protected abstract void initializePanel(T parameter);
	
	protected abstract T getInputData();
	
	protected abstract boolean validateInputData();
	
	private boolean newobject = false;
	
	protected String getTitle() {
		return getResourceBundle(newobject ? ResourceBundle.TITLE_NEW : ResourceBundle.TITLE_PROPERTIES);
	}
	
	public AbstractJavaSourcerDataInputDialog(MessageSource messageSource, Locale locale) {
		super(messageSource, locale);
	}
	
	private JPanel getDialogPanel(T parameter, boolean error) {
		if (settingspanel == null) {
			settingspanel = buildPanel();
		}
		if (!error) {
			newobject = parameter == null;
			if (newobject) {
				cleanupPanel();
			} else {
				initializePanel(parameter);
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
	
	protected boolean indicateError(JComponent component, boolean errorcondition) {
		if (errorcondition) {
			component.setBorder(BorderFactory.createLineBorder(Color.RED));
		} else {
			component.setBorder(null);
		}
		return errorcondition;
	}
	
	protected Optional<T> getInputDataInternal(Component parent, JPanel panel) {
		//resetValidation();
		
		while (JOptionPane.showConfirmDialog(null, panel, getTitle(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
			if (validateInputData()) {
				return Optional.of(getInputData());
			}
		}
		return Optional.empty();
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
