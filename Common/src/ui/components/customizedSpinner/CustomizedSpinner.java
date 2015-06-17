package ui.components.customizedSpinner;

import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JTextField;

import org.omg.CORBA.portable.ApplicationException;

import ui.icones.IconListener;
import ui.icones.Icone;

public class CustomizedSpinner extends JComponent implements IconListener {

	JTextField textField;
	Icone plusIc;
	Icone minusIc;
	
	private int minValue;
	private int maxValue;
	private int currentValue;
	
	ArrayList<CustomizedSpinnerListener> listeners;

	public CustomizedSpinner(int width, int height, int minValue, int maxValue,int initValue) {
		setLayout(null);
		
		listeners = new ArrayList<CustomizedSpinnerListener>();

		setSize(width, height);
		this.minValue = minValue;
		this.maxValue = maxValue;
		

		plusIc = new Icone("/Spinner/plus.png", 18, 18);
		minusIc = new Icone("/Spinner/minus.png", 18, 18);

		textField = new JTextField();
		textField.setSize(width - 40, height);
		textField.setLocation(20, 0);
		textField.setEditable(false);
		textField.setFocusable(false);
		textField.setHorizontalAlignment(JTextField.CENTER);
		

		minusIc.setLocation(0, (height - 18) / 2);
		plusIc.setLocation(width - 18, (height - 18) / 2);
		
		minusIc.addIconListener(this);
		plusIc.addIconListener(this);

		add(minusIc);
		add(textField);
		add(plusIc);
		
		setSpinnerValue(initValue);
	}
	
	public void addCustomizedSpinnerListener(CustomizedSpinnerListener listener){
		listeners.add(listener);
	}
	
	public void removeCustomizedSpinnerListener(CustomizedSpinnerListener listener){
		listeners.remove(listener);
	}
	
	@Override
	public void setToolTipText(String text) {
		super.setToolTipText(text);
		textField.setToolTipText(text);
		minusIc.setToolTipText(text);
		plusIc.setToolTipText(text);
	}
	
	public void setSpinnerValue(int value){
		
		if(value == currentValue){
			return;
		}
			
		
		if(value > maxValue){
			textField.setText(""+maxValue);
			return;
		}else if(value < minValue){
			textField.setText(""+minValue);
			return;
		}else{
			textField.setText(""+value);
		}
		
		currentValue = value;
		
		// Notify all listeners that the value has changed
		for (CustomizedSpinnerListener listener : listeners) {
			listener.valueChanged(value);
		}
		
	}

	@Override
	public void iconePressedPerformed(MouseEvent arg0) {
	}

	@Override
	public void iconeClickedPerformed(MouseEvent arg0) {
		if(arg0.getSource() == plusIc){
			setSpinnerValue(currentValue+1);
		}else if(arg0.getSource() == minusIc){
			setSpinnerValue(currentValue-1);
		}
	}

}
