package edu.mccc.cos210.qrks;

import javax.swing.*;
import java.awt.*;

public interface Builder {
	JPanel generateGUI();
	void reset();
	String getName();
}