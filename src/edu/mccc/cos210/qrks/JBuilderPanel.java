package edu.mccc.cos210.qrks;
import javax.swing.*;
import java.awt.*;
/**
 * JBuilderPanel<T>: An abstract class that provides a Generator that can be executed on a worker thread.
 */
public abstract class JBuilderPanel<T> extends JPanel {
	public abstract Generator<T> getGenerator();
}