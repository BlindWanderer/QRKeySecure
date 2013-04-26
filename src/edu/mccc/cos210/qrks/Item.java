package edu.mccc.cos210.qrks;
import javax.swing.*;
import java.awt.*;
/**
 * Item<T>: An interface that provides GUI generation and exporting abilities.
 */
public interface Item<T> {
	JPanel generateGUI();
	T save();
}