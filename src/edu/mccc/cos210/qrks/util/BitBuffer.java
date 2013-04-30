package edu.mccc.cos210.qrks.util;
import java.util.*;
public class BitBuffer {
	/*public static void main(String [] args) {
		BitBuffer b = new BitBuffer(36);
		byte [] blah = {(byte)0xF0,(byte)0x00,(byte)0x80,(byte)0x01};
		System.out.println(b);
		b.add(0xF,4);
		System.out.println(b);
		b.add(blah);
		System.out.println(b);
		System.out.println(Integer.toHexString(b.data[1]));
	}*/
	private int size;
	private int pos = 0;
	private byte data[];
	public BitBuffer(int initialSize){
		size = initialSize;
		data = new byte[(size + 71) / 8];
		Arrays.fill(data, (byte)0);
	}
	public String toString() {
		return Arrays.toString(getData()) + " @ " + pos + " of " + size;
	}
	public byte[] getData() {
		return Arrays.copyOf(data, data.length - 8);
	}
	public int getSize() {
		return size;
	}
	public void add(byte x) {
		add(x, 8);
	}
	public void add(char x) {
		add(x,16);
	}
	public void add(int x) {
		add(x, 32);
	}
	public void add(long x) {
		add(x, 64);
	}
	public void add(byte[] x) {
		for (byte b : x)
			add(b, 8);
	}
	public void add(byte x, int count) {
		int d = ((x << (8 - count)) & 0xFF) & (0xFF00 >> count);
		int used = pos & 7;
		int p = pos >> 3;
		data[p] = (byte)(data[p] | (d >> used));
		data[p+1] = (byte)(d << (8 - used));
		pos += count;
	}
	//TODO: This is terrible but it's too much work to get them right (I tried several times) Yeah lack of sleep!
	public void add(char x, int count) {
		if (count > 8) {
			add((byte)(x >> 8), count - 8);
			add((byte)x, 8);
		} else {
			add((byte)x, count);
		}
	}
	public void add(int x, int count) {
		if (count > 24) {
			add((byte)(x >> 24), count - 24);
			add((byte)(x >> 16), 8);
			add((byte)(x >> 8), 8);
			add((byte)x, 8);
		} else if (count > 16) {
			add((byte)(x >> 16), count - 24);
			add((byte)(x >> 8), 8);
			add((byte)x, 8);
		} else if (count > 8) {
			add((byte)(x >> 8), count - 8);
			add((byte)x, 8);
		} else {
			add((byte)x, count);
		}
	}
	public void add(long x, int count) {
		if (count > 56) {
			add((byte)(x >> 56), count - 56);
			add((byte)(x >> 48), 8);
			add((byte)(x >> 40), 8);
			add((byte)(x >> 32), 8);
			add((byte)(x >> 24), 8);
			add((byte)(x >> 16), 8);
			add((byte)(x >> 8), 8);
			add((byte)x, 8);
		} else if (count > 48) {
			add((byte)(x >> 48), count - 48);
			add((byte)(x >> 40), 8);
			add((byte)(x >> 32), 8);
			add((byte)(x >> 24), 8);
			add((byte)(x >> 16), 8);
			add((byte)(x >> 8), 8);
			add((byte)x, 8);
		} else if (count > 40) {
			add((byte)(x >> 40), count - 40);
			add((byte)(x >> 32), 8);
			add((byte)(x >> 24), 8);
			add((byte)(x >> 16), 8);
			add((byte)(x >> 8), 8);
			add((byte)x, 8);
		} else if (count > 32) {
			add((byte)(x >> 32), count - 32);
			add((byte)(x >> 24), 8);
			add((byte)(x >> 16), 8);
			add((byte)(x >> 8), 8);
			add((byte)x, 8);
		} else if (count > 24) {
			add((byte)(x >> 24), count - 24);
			add((byte)(x >> 16), 8);
			add((byte)(x >> 8), 8);
			add((byte)x, 8);
		} else if (count > 16) {
			add((byte)(x >> 16), count - 24);
			add((byte)(x >> 8), 8);
			add((byte)x, 8);
		} else if (count > 8) {
			add((byte)(x >> 8), count - 8);
			add((byte)x, 8);
		} else {
			add((byte)x, count);
		}
	}
}