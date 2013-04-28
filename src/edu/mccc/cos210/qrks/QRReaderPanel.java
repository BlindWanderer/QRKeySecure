package edu.mccc.cos210.qrks;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.filechooser.*;
import java.io.*;
import javax.imageio.*;

public class QRReaderPanel extends JPanel {
    private static final long serialVersionUID = 1L;
	private Image image;
	public QRReaderPanel(final Viewer viewer){
		this.setPreferredSize(new Dimension(600, 800));
		this.setLayout(new BorderLayout());
		
		final Camera camera = new Camera();
		this.add(camera, BorderLayout.CENTER);
		
		final CardHistoryLayout cl = new CardHistoryLayout(2);
		final JPanel stack = new JPanel(cl);
		
		this.add(stack, BorderLayout.SOUTH);
		

		ActionListener pal = new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					
					cl.show(stack, "5");
				}
			};
		ActionListener tpal = new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					image = camera.takePicture();
					cl.show(stack, "3");
				}
			};
		ActionListener scal = new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					camera.showCamera();
					cl.show(stack, "2");
				}
			};
		ActionListener oal = new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					JFileChooser fc = new JFileChooser();
					ImagePreview ip = new ImagePreview(fc);
					fc.setAcceptAllFileFilterUsed(false);
					fc.addChoosableFileFilter(new ImageFileFilter());
					fc.setAccessory(ip);
					
					int returnVal = fc.showDialog(viewer, "Open Image");
					
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						Image fi = ip.getImage();
						if (image == null) { //don't reopen the file unless strictly necessary
							File file = fc.getSelectedFile();
							try {
								fi = ImageIO.read(file);
							} catch (IOException ex) {
								fi = null;
								//TODO: display error message about trouble reading file. Status bar or dialog box?
							}
						}
						if (fi != null) {
							cl.show(stack, "4");
							image = camera.setImage(fi);
						}
					}
					//Reset the file chooser for the next time it's shown.
					fc.setSelectedFile(null);
				}
			};
		{ //STATE 1
			JPanel state1 = new JPanel(new BorderLayout());
			stack.add(state1, "1");
			final JButton openImage = new JButton("Open Image");
			openImage.setMnemonic(KeyEvent.VK_O);
			openImage.addActionListener(oal);
			state1.add(openImage, BorderLayout.LINE_START);
			final JButton startVideo = new JButton("Start Video");
			startVideo.setMnemonic(KeyEvent.VK_S);
			state1.add(startVideo, BorderLayout.LINE_END);
			startVideo.addActionListener(scal);
		}
		{ //STATE 2
			JPanel state2 = new JPanel(new BorderLayout());
			stack.add(state2, "2");
			final JButton openImage = new JButton("Open Image");
			openImage.setMnemonic(KeyEvent.VK_O);
			openImage.addActionListener(oal);
			state2.add(openImage, BorderLayout.LINE_START);
			final JButton takeSnapshot = new JButton("Take Snapshot");
			takeSnapshot.setMnemonic(KeyEvent.VK_T);
			state2.add(takeSnapshot, BorderLayout.LINE_END);
			takeSnapshot.addActionListener(tpal);
		}
		{ //STATE 3
			JPanel state3 = new JPanel(new BorderLayout());
			stack.add(state3, "3");
			final JButton retake = new JButton("Retake");
			retake.setMnemonic(KeyEvent.VK_R);
			state3.add(retake, BorderLayout.LINE_START);
			final JButton process = new JButton("Process");
			process.setMnemonic(KeyEvent.VK_P);
			state3.add(process, BorderLayout.LINE_END);
			retake.addActionListener(scal);
			process.addActionListener(pal);
		}
		{ //STATE 4
			JPanel state4 = new JPanel(new BorderLayout());
			stack.add(state4, "4");
			final JButton goBack = new JButton("Go Back");
			goBack.setMnemonic(KeyEvent.VK_G);
			state4.add(goBack, BorderLayout.LINE_START);
			final JButton process = new JButton("Process");
			process.setMnemonic(KeyEvent.VK_P);
			state4.add(process, BorderLayout.LINE_END);
			goBack.addActionListener(showActionListener(cl, stack, "1"));
			process.addActionListener(pal);
		}
		{ //STATE 5
			JPanel state5 = new JPanel(new BorderLayout());
			stack.add(state5, "5");
			final JButton stop = new JButton("Stop");
			stop.setMnemonic(KeyEvent.VK_T);
			state5.add(stop, BorderLayout.CENTER);
			stop.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					cl.showPrevious(stack);
				}
			});
		}
		{ //STATE 6
			JPanel state6 = new JPanel(new BorderLayout());
			stack.add(state6, "6");
			final JButton startOver = new JButton("Start Over");
			startOver.setMnemonic(KeyEvent.VK_O);
			state6.add(startOver, BorderLayout.CENTER);
			startOver.addActionListener(showActionListener(cl, stack, "1"));
		}
		
		cl.show(stack, "1");
	}
	/**
	 * Quick and dirty closure to unify similar code.
	 */
	private ActionListener showActionListener(final CardLayout cl, final JPanel stack, final String target) {
		return new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				cl.show(stack, target);
			}
		};
	}
}