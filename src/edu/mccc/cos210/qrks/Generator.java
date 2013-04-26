package edu.mccc.cos210.qrks;
/**
 * Generator: An interface for an intermediate object that is passed between threads.
 */
public interface Generator<T> {
	Item<T> generate();
}

