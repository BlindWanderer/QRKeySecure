package edu.mccc.cos210.qrks;

import javax.swing.*;
import java.awt.*;
/**
 * Provides the framework for building an item via a GUI.
 * @param <T> The type used for <a href="Item.html">Item</a>&lt;T&gt;s this builder indirectly makes.
 */
public interface Builder<T> {
	JBuilderPanel<T> generateGUI();
	void reset();
	String getName();
}