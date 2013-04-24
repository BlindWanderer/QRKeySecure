package edu.mccc.cos210.qrks;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.filechooser.*;
import java.io.*;
import java.awt.image.*;
import javax.imageio.*;

class Viewer extends JFrame{
	private static final long serialVersionUID = 1L;
	private StateJPanel readerPanel = new StateJPanel();
	private Builder builder = new QRSecureBuilder();
	public Viewer() {
        super("QRKey");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel builderPanel = builder.generateGUI();
        
        readerPanel.setPreferredSize(new Dimension(600, 800));
        readerPanel.setLayout(new BorderLayout());
		
        final ImageJPanel imageBox = new ImageJPanel();
        imageBox.setPreferredSize(new Dimension(400, 400));
        imageBox.setBorder(BorderFactory.createLineBorder(Color.BLACK, 8));
        readerPanel.add(imageBox, BorderLayout.CENTER);
        
		//should use a secondary JPanel with a CardLayout to house the states instead of rolling our own.
		
        final JPanel state1 = new JPanel();
        final JPanel state2 = new JPanel();
        final JPanel state3 = new JPanel();
        final JPanel state4 = new JPanel();
        final JPanel state5 = new JPanel();
        final JPanel state6 = new JPanel();
        //STATE 1
        {
	        state1.setLayout(new BorderLayout());
	        final JButton openImage = new JButton("Open Image");
	        openImage.setMnemonic(KeyEvent.VK_O);
	        state1.add(openImage, BorderLayout.LINE_START);
	        final JButton startVideo = new JButton("Start Video");
	        startVideo.setMnemonic(KeyEvent.VK_S);
	        state1.add(startVideo, BorderLayout.LINE_END);
	        openImage.addActionListener(new ActionListener() {
	        	public void actionPerformed(ActionEvent e) {
	        		JFileChooser fc = new JFileChooser();
					//fc.setAcceptAllFileFilterUsed(false);
					//fc.addChoosableFileFilter(new ImageFilter());
	                
	                //Add the preview pane.
	                //fc.setAccessory(new ImagePreview(fc));
					
	                //Show it.
	                int returnVal = fc.showDialog(Viewer.this, "Open");
	         
	                //Process the results.
	                if (returnVal == JFileChooser.APPROVE_OPTION) {
	                    File file = fc.getSelectedFile();
	                    try {
	                    	BufferedImage myImage = ImageIO.read(file);
		                    readerPanel.changeState(state4);
		                    imageBox.setImage(myImage);
	                    }
	                    catch (IOException ex) {	
	                    }
	                } 
	         
	                //Reset the file chooser for the next time it's shown.
	                fc.setSelectedFile(null);
	        		
	        		//don't forget to paste this code in the other open image actionListener
	        	}
	        });
	        
	        startVideo.addActionListener(new ActionListener() {
	        	public void actionPerformed(ActionEvent e) {
	        		readerPanel.changeState(state2);
	        	}
	        });
        }
        //STATE 2
        {
	        state2.setLayout(new BorderLayout());
	        final JButton openImage = new JButton("Open Image");
	        openImage.setMnemonic(KeyEvent.VK_O);
	        state2.add(openImage, BorderLayout.LINE_START);
	        final JButton takeSnapshot = new JButton("Take Snapshot");
	        takeSnapshot.setMnemonic(KeyEvent.VK_T);
	        state2.add(takeSnapshot, BorderLayout.LINE_END);
	        openImage.addActionListener(new ActionListener() {
	        	public void actionPerformed(ActionEvent e) {
	        		readerPanel.changeState(state4);
	        	}
	        });
	        takeSnapshot.addActionListener(new ActionListener() {
	        	public void actionPerformed(ActionEvent e) {
	        		readerPanel.changeState(state3);
	        	}
	        });
        }
        
        //STATE 3
        {
	        state3.setLayout(new BorderLayout());
	        final JButton retake = new JButton("Retake");
	        retake.setMnemonic(KeyEvent.VK_R);
	        state3.add(retake, BorderLayout.LINE_START);
	        final JButton process = new JButton("Process");
	        process.setMnemonic(KeyEvent.VK_P);
	        state3.add(process, BorderLayout.LINE_END);
	        retake.addActionListener(new ActionListener() {
	        	public void actionPerformed(ActionEvent e) {
	        		readerPanel.changeState(state2);
	        	}
	        });
	        
	        process.addActionListener(new ActionListener() {
	        	public void actionPerformed(ActionEvent e) {
	        		readerPanel.changeState(state5);
	        	}
	        });
        }
        //STATE 4
        {
	        state4.setLayout(new BorderLayout());
	        final JButton goBack = new JButton("Go Back");
	        goBack.setMnemonic(KeyEvent.VK_G);
	        state4.add(goBack, BorderLayout.LINE_START);
	        final JButton process = new JButton("Process");
	        process.setMnemonic(KeyEvent.VK_P);
	        state4.add(process, BorderLayout.LINE_END);
	        goBack.addActionListener(new ActionListener() {
	        	public void actionPerformed(ActionEvent e) {
	        		readerPanel.changeState(state1);
	        	}
	        });
	        process.addActionListener(new ActionListener() {
	        	public void actionPerformed(ActionEvent e) {
	        		readerPanel.changeState(state5);
	        	}
	        });
        }
        //STATE 5
        {
	        state5.setLayout(new BorderLayout());
	        final JButton stop = new JButton("Stop");
	        stop.setMnemonic(KeyEvent.VK_T);
	        state5.add(stop, BorderLayout.CENTER);
	        stop.addActionListener(new ActionListener() {
	        	public void actionPerformed(ActionEvent e) {
	        		if (readerPanel.getPrevious() == state3) {
	        			readerPanel.changeState(state3);
	        		}	
	        		if (readerPanel.getPrevious() == state4) {
	        			readerPanel.changeState(state4);
	        		}
	        	}
	        });
        }
        //STATE 6
        {
	        state6.setLayout(new BorderLayout());
	        final JButton startOver = new JButton("Start Over");
	        startOver.setMnemonic(KeyEvent.VK_O);
	        state6.add(startOver, BorderLayout.CENTER);
	        startOver.addActionListener(new ActionListener() {
	        	public void actionPerformed(ActionEvent e) {
	        		readerPanel.changeState(state1);
	        	}
	        });
        }
        
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("READ", readerPanel);	//can add a custom icon later
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		tabbedPane.addTab("CREATE", builderPanel);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
		
		readerPanel.changeState(state1);
		add(tabbedPane, BorderLayout.CENTER);
		pack();
        setVisible(true);
	}
}
