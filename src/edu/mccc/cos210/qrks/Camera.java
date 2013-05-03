package edu.mccc.cos210.qrks;
import edu.mccc.cos210.qrks.util.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.util.*;
import javax.media.*;
import javax.media.control.*;
import javax.media.format.*;
import javax.media.util.*;
/**
 * ImageJPanel: A JPanel that can conditionally have an image drawn into it.
 */
public class Camera extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final int BORDER = 8;
	private Image image = null;
	private VideoFormat videoFormat = null;
	private CaptureDeviceInfo cdi;
	private Player player;
	private FrameGrabbingControl frameGrabber;
	private java.util.List<VideoFormat> videoFormats;
	private BufferToImage bti = null;
	private CardLayout cardLayout;
	private ImagePanel imagePanel;
	private boolean camera = false;
	private final boolean cameraAvailable;
	
	public boolean isCameraAvailable() {
		return cameraAvailable;
	}
	public Camera() {
		this(0);
	}
	public Camera(int device) {
		this(intToCaptureDeviceInfo(device));
	}
	public Camera(CaptureDeviceInfo cdi) {
		this.cdi = cdi;
		this.cardLayout = new CardLayout();
		setLayout(cardLayout);
		setBorder(BorderFactory.createLineBorder(Color.BLACK, BORDER));
		
		imagePanel = new ImagePanel();
		add(imagePanel, "image");
		boolean success = false;
		if (cdi != null) {
			java.util.List<VideoFormat> videoFormats = null;
			int area = -1;
			for (Format f : cdi.getFormats()) {
				if (f instanceof VideoFormat) {
					VideoFormat vf = (VideoFormat)f;
					Dimension d = vf.getSize();
					int a = d.height * d.width;
					if (a > area) {
						videoFormats = new LinkedList<VideoFormat>();
						videoFormats.add(vf);
						area = a;
					} else if (a == area ) {
						videoFormats.add(vf);
					}
				}
			}
			if (videoFormats != null) {
				try {
					videoFormat = videoFormats.get(0);
					player = Manager.createRealizedPlayer(cdi.getLocator());
					frameGrabber = (FrameGrabbingControl)player.getControl("javax.media.control.FrameGrabbingControl");
					FormatControl formatControl = (FormatControl)player.getControl("javax.media.control.FormatControl");
					formatControl.setFormat(videoFormat);
					success = true;
				} catch (Exception e) {
					//System.out.println(cdi);
					//System.out.println(videoFormats);
					//e.printStackTrace();
				}
			}
		}
		if (cameraAvailable = success) {
			Dimension size = videoFormat.getSize();
			Utilities.cloberSizes(this, Utilities.addDimensions(BORDER + BORDER, BORDER + BORDER, size));
			Utilities.cloberSizes(imagePanel, size);
			add(player.getVisualComponent(), "camera");
		} else {
			videoFormat = null;
			frameGrabber = null;
			Utilities.cloberSizes(imagePanel, new Dimension(640, 480));
		}
	}
	private static CaptureDeviceInfo intToCaptureDeviceInfo(final int device) {
		@SuppressWarnings("unchecked")
		Vector<CaptureDeviceInfo> devices = CaptureDeviceManager.getDeviceList(new VideoFormat(null));
		if (devices == null || devices.size() < 1) {
			System.out.println("No capture devices available!");
			return null;
		} else if (devices.size() < device) {
			throw new IndexOutOfBoundsException("'device' ("+device+") is not in range [0,"+devices.size()+")  ");
		}
		return devices.elementAt(device);
	}
	public void showImage() {
		cardLayout.show(this, "image");
		player.stop();
		camera = false;
	}
	public void showCamera() {
		player.start();
		cardLayout.show(this, "camera");
		camera = true;
	}
	public Image toggleCamera() {
		if (camera) {
			return takePicture();
		}
		showCamera();
		return null;
	}
	public Image takePicture() {
		if (frameGrabber == null) {
			System.out.println("FrameGrabber is not initialized!");
			return null;
		}
		if (!camera) {
			showCamera();
		}
		Buffer buf = frameGrabber.grabFrame();
		if (buf != null) {
			VideoFormat vf = (VideoFormat)buf.getFormat();
			if (bti == null || !vf.equals(videoFormat)) {
				videoFormat = vf;
				bti = new BufferToImage(vf);
			}
			Image i = bti.createImage(buf);
			System.out.println(i);
			return setImage(i);
		}
		return null;
	}
	/**
	 * Sets the image inside this panel.
	 */
	public Image setImage(final Image image) {
		this.image = image;
		if (imagePanel.isVisible()) {
			imagePanel.repaint();
		} else {
			showImage();
		}
		return image;
	}
	/**
	 * Gets the image inside this panel. You probably want to call takePicture.
	 * @return Image currently displayed in the panel
	 */
	public Image getImage() {
		return image;
	}
	private class ImagePanel extends JPanel {
		private static final long serialVersionUID = 1L;
		@Override
		public void paintComponent(final Graphics g) {
			super.paintComponent(g);
			if (image != null) {
				int ix = image.getWidth(null);
				int iy = image.getHeight(null);
				if ((ix > 0) && (iy > 0)) { //There is something to display.
					int cx = getWidth();
					int cy = getHeight();
					//Figure out how much we have to shrink it to get it to fit.
					double sm = Math.min(cx / (double)ix, cy / (double)iy);
					if (sm > 0) { //has to be space to display it in. Zero or less indicates there is no space to display it in.
						Image t = image;
						/*if (sm < 1.0)*/ { //it's too big
							ix = (int)Math.round(ix * sm);
							iy = (int)Math.round(iy * sm);
							t = image.getScaledInstance(ix, iy, Image.SCALE_DEFAULT);
						}
						g.drawImage(t, (cx - ix)/ 2, (cy - iy) / 2, null);
					}
				}
			}
		}
	}
}