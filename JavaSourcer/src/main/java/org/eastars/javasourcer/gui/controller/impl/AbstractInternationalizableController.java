package org.eastars.javasourcer.gui.controller.impl;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.Spring;
import javax.swing.SpringLayout;

import org.springframework.context.MessageSource;

public class AbstractInternationalizableController {

	private Locale locale;
	
	private MessageSource messageSource;
	
	public AbstractInternationalizableController(MessageSource messageSource, Locale locale) {
		this.messageSource = messageSource;
		this.locale = locale;
	}
	
	protected String getResourceBundle(String key) {
		return messageSource.getMessage(String.format("%s.%s", this.getClass().getSimpleName().toLowerCase(), key), null, locale);
	}

	protected JPanel makeCompactGrid(List<Component> components, int rows, int cols, int initialX, int initialY, int xPad,
			int yPad) {
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

	protected void registerHotKeyAction(JRootPane rootpane, KeyStroke keystore, String actionkey, Consumer<ActionEvent> action) {
		InputMap inputMap = rootpane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionMap = rootpane.getActionMap();
		
		inputMap.put(keystore, actionkey);
		actionMap.put(actionkey, new CallbackAction(action));
	}
}
