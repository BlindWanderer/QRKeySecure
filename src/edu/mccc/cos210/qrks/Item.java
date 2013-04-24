package edu.mccc.cos210.qrks;

import javax.swing.*;
import java.awt.*;

public interface Item<T> {
	JPanel generateGUI();
	T save();
}