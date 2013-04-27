package edu.mccc.cos210.qrks;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.filechooser.*;
import java.io.*;
import javax.imageio.*;
/**
 * Viewer: A JFrame that handles the construction of entire program.
 */
public class Viewer extends JFrame {
	private static final long serialVersionUID = 1L;
	private Builder<BufferedImage> builder = new QRSecureBuilder();
	public Viewer() {
		super("QRKey");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel builderPanel = generateBuilderPanel();
		JPanel readerPanel = generateReaderPanel();
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("READ", readerPanel);	//can add a custom icon later
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		tabbedPane.addTab("CREATE", builderPanel);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
		
		add(tabbedPane, BorderLayout.CENTER);
		pack();
		setVisible(true);
	}
	private JPanel generateBuilderPanel() {
		JPanel builderPanel = new JPanel(new BorderLayout());
		final JBuilderPanel<BufferedImage> builderGeneratedPanel = builder.generateGUI();
		JPanel fun = new JPanel(new GridLayout(0, 1));
		JPanel blah = new JPanel();
		final ImageJPanel imageBox = new ImageJPanel();
		final JButton generateImage = new JButton("Preview");
		generateImage.setMnemonic(KeyEvent.VK_P);
		generateImage.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				final Generator<BufferedImage> generator = builderGeneratedPanel.getGenerator();
				if (generator != null) {
					//TODO: Only a single worker thread should be used.
					Thread worker = new Thread() {
						@Override
						public void run() {
							Item<BufferedImage> item = generator.generate();
							if (item != null) {
								final BufferedImage image = item.save();
								EventQueue.invokeLater(
									new Runnable() {
										@Override
										public void run() {
											imageBox.setImage(image);
										}
									}
								);
							}
						}
					};
					worker.start();
				}
			}
		});

		final JButton saveImage = new JButton("Save Image");
		saveImage.setMnemonic(KeyEvent.VK_S);
		saveImage.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setAcceptAllFileFilterUsed(false);
				fc.addChoosableFileFilter(new ImageFileFilter());
				int returnVal = fc.showSaveDialog(Viewer.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
//						try {
//							BufferedImage myImage = ImageIO.read(file);
//							cl.show(stack, "4");
//							imageBox.setImage(myImage);
//						}
//						catch (IOException ex) {	
//						}
				} 
				//Reset the file chooser for the next time it's shown.
				fc.setSelectedFile(null);
			}
		});
		fun.add(builderGeneratedPanel); //, BorderLayout.CENTER);
		blah.add(generateImage); //, BorderLayout.SOUTH);
		blah.add(saveImage); //, BorderLayout.SOUTH);
		fun.add(blah); //, BorderLayout.SOUTH);
		
		builderPanel.add(imageBox, BorderLayout.CENTER);
		builderPanel.add(fun, BorderLayout.SOUTH);
		return builderPanel;
	}
	/**
	 *
	 */
	private JPanel generateReaderPanel() {
		JPanel readerPanel = new JPanel();
		readerPanel.setPreferredSize(new Dimension(600, 800));
		readerPanel.setLayout(new BorderLayout());
		
		final ImageJPanel imageBox = new ImageJPanel();
		readerPanel.add(imageBox, BorderLayout.CENTER);
		
		final CardHistoryLayout cl = new CardHistoryLayout(2);
		final JPanel stack = new JPanel(cl);
		
		readerPanel.add(stack, BorderLayout.SOUTH);
		
		ActionListener oal = new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					JFileChooser fc = new JFileChooser();
					ImagePreview ip = new ImagePreview(fc);
					fc.setAcceptAllFileFilterUsed(false);
					fc.addChoosableFileFilter(new ImageFileFilter());
					fc.setAccessory(ip);
					
					int returnVal = fc.showDialog(Viewer.this, "Open Image");
					
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						BufferedImage image = ip.getImage();
						if (image == null) { //don't reopen the file unless strictly necessary
							File file = fc.getSelectedFile();
							try {
								image = ImageIO.read(file);
							} catch (IOException ex) {
								image = null;
								//TODO: display error message about trouble reading file. Status bar or dialog box?
							}
						}
						if (image != null) {
							cl.show(stack, "4");
							imageBox.setImage(image);
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
			startVideo.addActionListener(showActionListener(cl, stack, "2"));
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
			takeSnapshot.addActionListener(showActionListener(cl, stack, "3"));
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
			retake.addActionListener(showActionListener(cl, stack, "2"));
			process.addActionListener(showActionListener(cl, stack, "5"));
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
			process.addActionListener(showActionListener(cl, stack, "5"));
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
		
		return readerPanel;
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
