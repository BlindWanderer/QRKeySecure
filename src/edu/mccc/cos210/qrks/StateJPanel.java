package edu.mccc.cos210.qrks;

import javax.swing.*;
import java.awt.*;

class StateJPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JPanel state = null;
	private JPanel previous = null;
	public void changeState(JPanel newState, JPanel previousState) {
		if (state != null) {
			state.setVisible(false);
			this.remove(state);
		}
		previous = previousState;
		state = newState;
		state.setVisible(true);
		this.add(newState, BorderLayout.SOUTH);
		this.invalidate();
	}
	public void changeState(JPanel newState) {
		changeState(newState, state);
	}
	public JPanel getPrevious() {
		return previous;
	}
}
