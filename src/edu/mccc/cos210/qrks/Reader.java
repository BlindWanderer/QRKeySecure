package edu.mccc.cos210.qrks;
import java.util.*;
import javax.swing.*;

public interface Reader<T, S> {
	List<Item<S>> process(T input, SingWorkerProtected<T> progress);
	String getName();
	void reset();
}