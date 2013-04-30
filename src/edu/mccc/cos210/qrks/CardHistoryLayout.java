package edu.mccc.cos210.qrks;
import edu.mccc.cos210.qrks.util.RingBuffer;
import java.util.*;
import java.util.concurrent.*;
import javax.swing.*;
import java.awt.*;
/**
 * CardHistoryLayout: An extension of CardLayout that provides a finite history for the show() method.
 */
public class CardHistoryLayout extends CardLayout {
	private static final int DEFAULT_SIZE = 4;
	private static final long serialVersionUID = 1L;
	private Map<Container, RingBuffer<String>> map = new ConcurrentHashMap<Container, RingBuffer<String>>();
	private final int max;
	public CardHistoryLayout() {
		super();
		max = DEFAULT_SIZE;
	}
	public CardHistoryLayout(final int max) {
		super();
		this.max = Math.max(2, max);
	}
	public CardHistoryLayout(final int hgap, final int vgap) {
		super(hgap, vgap);
		max = DEFAULT_SIZE;
	}
	public CardHistoryLayout(final int hgap, final int vgap, final int max) {
		super(hgap, vgap);
		this.max = Math.max(2, max);
	}
	@Override
	public void show(final Container parent, final String name) {
		RingBuffer<String> RingBuffer = map.get(parent);
		if (RingBuffer == null) {
			RingBuffer = new RingBuffer<String>(String.class, max);
			map.put(parent, RingBuffer);
		}
		show(parent, name, RingBuffer);
	}
	private void show(final Container parent, final String name, final RingBuffer<String> RingBuffer) {
		RingBuffer.add(name);
		super.show(parent, name);
	}
	public void showPrevious(final Container parent) {
		RingBuffer<String> RingBuffer = map.get(parent);
		show(parent, RingBuffer.get(1), RingBuffer);
	}
	public void showPrevious(final Container parent, final int gone) {
		RingBuffer<String> RingBuffer = map.get(parent);
		show(parent, RingBuffer.get(gone), RingBuffer);
	}
}