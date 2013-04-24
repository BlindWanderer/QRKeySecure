package edu.mccc.cos210.qrks;
import java.util.*;
import javax.swing.*;
import java.awt.*;

public interface Reader<T> {
	List<Item> process(T input)
	String getName();
	void reset();
}