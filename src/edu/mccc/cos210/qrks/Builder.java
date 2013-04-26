package edu.mccc.cos210.qrks;

import javax.swing.*;
import java.awt.*;
/**
 * Provides the framework for building an item via a GUI.
 * @param <T> The type used for <a href="Item.html">Item</a>&lt;T&gt;s this builder indirectly makes.
 */
public interface Builder<T> {
	/**
	 * Generates the GUI for the builder.
	 * @return returns a specialized JPanel.
	 */	
	JBuilderPanel<T> generateGUI();
	/**
	 * Resets the internal state of the builder.
	 */	
	void reset();
	/**
	 * Gets a user-friendly name for the class.
	 * @return returns the user-friendly name for the class.
	 */	
	String getName();
}