package edu.mccc.cos210.qrks;

import javax.swing.*;
import java.awt.*;

public interface Builder<T> {
	JBuilderPanel<T> generateGUI();
	void reset();
	String getName();
}