package edu.mccc.cos210.qrks;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Observable;
import java.util.Observer;
import javax.swing.*;

public class QRKeySecure {
	public static void main(String[] sa) {
		EventQueue.invokeLater(
			new Runnable() {
				@Override
				public void run() {
					new Viewer();
				}
			}
		);
	}
}
