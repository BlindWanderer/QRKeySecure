package edu.mccc.cos210.qrks;

import javax.swing.*;
import java.awt.*;
/**
 * Builder: Provides framework for building an item via a GUI.
 */
public interface Builder<T> {
	JBuilderPanel<T> generateGUI();
	void reset();
	String getName();
}