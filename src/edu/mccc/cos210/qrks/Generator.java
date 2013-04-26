package edu.mccc.cos210.qrks;
/**
 * An interface for an intermediate object that is passed between threads.
 * @param <T> The type used for <a href="Item.html">Item</a>&lt;T&gt;s .
 */
public interface Generator<T> {
	Item<T> generate();
}

