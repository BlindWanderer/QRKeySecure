package edu.mccc.cos210.qrks;
import javax.swing.*;
import java.awt.*;
/**
 * JBuilderPanel<T>: An abstract class that provides a Generator that can be executed on a worker thread.
 * * @param <T> The type used for <a href="Item.html">Item</a>&lt;T&gt;s
 */
public abstract class JBuilderPanel<T> extends JPanel {
	private final Builder<T> builder;
	public JBuilderPanel(Builder<T> builder) {
		this.builder = builder;
	}
	Builder<T> getBuilder() {
		return builder;
	}
	public abstract Generator<T> getGenerator();
}