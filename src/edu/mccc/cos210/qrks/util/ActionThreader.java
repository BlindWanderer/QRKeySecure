//package edu.mccc.cos210.qrks.util;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import javax.imageio.*;

public abstract class ActionThreader implements ActionListener{
	protected abstract boolean warmup(ActionEvent e);
	protected abstract boolean run();
	protected abstract void cooloff();
	public final void actionPerformed(final ActionEvent e) {
		if (ActionThreader.this.warmup(e)) {
			Thread worker = new Thread() {
				@Override
				public void run() {
					if (ActionThreader.this.run()) {
						EventQueue.invokeLater(new Runnable(){
							public void run(){
								ActionThreader.this.cooloff();
							}
						});
					}
				}
			};
			worker.start();
		}
	}
}