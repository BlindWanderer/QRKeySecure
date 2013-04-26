package edu.mccc.cos210.qrks;
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
	private static class Ring {
		private static final long serialVersionUID = 1L;
		private String [] values;
		private int position = -1;
		Ring(final int count) {
			values = new String[count];
			Arrays.fill(values, null);
		}
		void add(final String s) {
			if (s != null) {
				position = (1 + position != values.length) ? 1 + position : 0;
				values[position] = s;
			}
		}
		String get(final int past) {
			int max = values.length;
			int count;
			if (past < 0) {
				count = 0;
			} else {
				if (past >= max) {
					count = max - 1;
				} else {
					count = past;
				}
			}
			String r;
			if (past > position) {
				if (values[max - 1] == null) {
					r = values[0]; //return the oldest, may result in returning null.
				} else {
					r = values[max + position - past];
				}
			} else {
				r = values[position - past];
			}
			return r;
		}
	}
	private Map<Container, Ring> map = new ConcurrentHashMap<Container, Ring>();
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
		Ring ring = map.get(parent);
		if (ring == null) {
			ring = new Ring(max);
			map.put(parent, ring);
		}
		show(parent, name, ring);
	}
	private void show(final Container parent, final String name, final Ring ring) {
		ring.add(name);
		super.show(parent, name);
	}
	public void showPrevious(final Container parent) {
		Ring ring = map.get(parent);
		show(parent, ring.get(1), ring);
	}
	public void showPrevious(final Container parent, final int gone) {
		Ring ring = map.get(parent);
		show(parent, ring.get(gone), ring);
	}
}