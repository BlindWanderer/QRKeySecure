package edu.mccc.cos210.qrks;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
/**
 * QRKeySecure: Main entry point for the program. Continues execution via the EventQueue.
 */
public final class QRKeySecure {
	private QRKeySecure() {
	}
	public static void main(final String[] sa) {
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
