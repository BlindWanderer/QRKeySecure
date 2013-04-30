package edu.mccc.cos210.qrks;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
/**
 * Viewer: A JFrame that handles the construction of entire program.
 */
public class Viewer extends JFrame {
	private static final long serialVersionUID = 1L;
	private Builder<BufferedImage> builder = new QRSecureBuilder();
	private Reader<BufferedImage, BufferedImage> [] readers = {new QRReader()};
	public Viewer() {
		super("QRKey");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel builderPanel = generateBuilderPanel();
		JPanel readerPanel = new QRReaderPanel(this, readers);
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
		final BuilderPanel<BufferedImage> builderGeneratedPanel = builder.generateGUI();
		JPanel fun = new JPanel(new GridLayout(0, 1));
		JPanel blah = new JPanel();
		final ImagePanel imageBox = new ImagePanel();
		final JButton generateImage = new JButton("Preview");
		generateImage.setMnemonic(KeyEvent.VK_P);
		generateImage.addActionListener(new ActionListener() {
			SwingWorker sw;
			public void actionPerformed(final ActionEvent e) {
				final Factory<Item<BufferedImage>> factory = builderGeneratedPanel.getFactory();
				if (factory != null) {
					if (sw != null && !sw.isDone() && !swp.isCancelled()) {
						sw.cancel(true);
					}
					sw = new SwingWorker() {
						Item<BufferedImage> item;
						BufferedImage image;
						public Object doInBackground() {
							item = factory.runFactory();
							return image = item.save();
						}
						public void done() {
							imageBox.setImage(image);
						}
					};
					sw.execute();
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
					try {
						ImageIO.write(imageBox.getImage(), "png", file);
					}
					catch (IOException ex) {	
					}
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
}
