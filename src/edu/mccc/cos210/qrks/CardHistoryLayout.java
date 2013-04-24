package edu.mccc.cos210.qrks;
import java.util.*;
import java.util.concurrent.*;
import javax.swing.*;
import java.awt.*;

public class CardHistoryLayout extends CardLayout {
	private static final long serialVersionUID = 1L;
	private static class Ring {
		private static final long serialVersionUID = 1L;
		private String [] values;
		private int position = -1;
		Ring(int count) {
			values = new String[count];
			Arrays.fill(values, null);
		}
		void add(String s) {
			if (s != null) {
				values[position=(1+position!=values.length)?1+position:0] = s;
			}
		}
		String get(int past) {
			int max = values.length;
			if (past < 0) {
				past = 0;
			} else {
				if (past >= max) {
					past = max - 1;
				}
			}
			String r;
			if (past > position) {
				if(values[max - 1] == null) {
					r = values[0];//return the oldest
				} else {
					r = values[max + position - past];
				}
			} else {
				r = values[position - past];
			}
			System.out.println(past + " ~ " + Arrays.toString(values) + " ~ " + r);
			return r;
		}
		int getCount(){
			int max = values.length;
			if (values[max - 1] == null) {
				return position + 1;
			} else {
				return max;
			}
		}
	}
	private Map<Container, Ring> map = new ConcurrentHashMap<Container, Ring>();
	private int max = 4;
	public CardHistoryLayout() {
		super();
	}
	public CardHistoryLayout(int max) {
		super();
		this.max = Math.max(2, max);
	}
	public CardHistoryLayout(int hgap, int vgap) {
		super(hgap, vgap);
	}
	public CardHistoryLayout(int hgap, int vgap, int max) {
		super(hgap, vgap);
		this.max = Math.max(2, max);
	}
	@Override
	public void show(Container parent, String name) {
		Ring ring = map.get(parent);
		if (ring == null) {
			map.put(parent, ring = new Ring(max));
		}
		show(parent, name, ring);
	}
	private void show(Container parent, String name, Ring ring) {
		ring.add(name);
		super.show(parent, name);
	}
	public void showPrevious(Container parent) {
		showPrevious(parent, 1);
	}
	public void showPrevious(Container parent, int gone) {
		Ring ring = map.get(parent);
		show(parent, ring.get(gone), ring);
	}
}