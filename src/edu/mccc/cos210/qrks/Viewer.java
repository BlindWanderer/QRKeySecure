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
		final ImagePanel imageBox = new ImagePanel();
		final JButton generateImage = new JButton("Preview");
		generateImage.setMnemonic(KeyEvent.VK_P);
		generateImage.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				final Generator<Item<BufferedImage>> generator = builderGeneratedPanel.getGenerator();
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
		return new QRReaderPanel();
	}
}
