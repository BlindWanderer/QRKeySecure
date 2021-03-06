package edu.mccc.cos210.qrks;
import java.util.*;
import javax.swing.*;

public interface Reader<T, S> {
	List<Item<S>> process(T input, SwingWorkerProtected<?,T> swp);
	String getName();
	void reset();
}