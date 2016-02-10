package com.gmo.ui.icones;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;

import com.gmo.ui.icones.StateIcone.STATE;

public class StateIconGroup implements StateIconListener{

	private ArrayList<StateIcone> listIcones;

	public StateIconGroup() {
		listIcones = new ArrayList<StateIcone>();
	}

	public void addIconGroup(StateIcone stateIc) {
		stateIc.addStateIconListener(this);
		listIcones.add(stateIc);
	}

	@Override
	public void iconeClickedPerformed(MouseEvent arg0) {
		for (Iterator<StateIcone> iterator = listIcones.iterator(); iterator.hasNext();) {
			StateIcone icon = (StateIcone) iterator.next();
			if((StateIcone)arg0.getSource() == icon){
				icon.setState(STATE.ON);
			}else{
				icon.setState(STATE.OFF);
			}
		}
	}
}
