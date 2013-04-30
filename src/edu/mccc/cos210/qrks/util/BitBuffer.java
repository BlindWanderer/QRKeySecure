package edu.mccc.cos210.qrks.util;
import java.util.*;
public class BitBuffer {
	/*/
	public static void main(String [] args) {
		BitBuffer b = new BitBuffer(36);
		byte [] blah = {(byte)0xF0,(byte)0x00,(byte)0x80,(byte)0x01};
		System.out.println(b);
		b.write(0xFFFFFFFEL, 1);
//		b.write(0xEFEFEFEF, 31);
		System.out.println(b);
		b.write(blah);
		System.out.println(b);
		System.out.println(Integer.toHexString(0xff & b.data[1]));
	}/**/
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
	public void write(byte x) {
		write(x, 8);
	}
	public void write(char x) {
		write(x,16);
	}
	public void write(int x) {
		write(x, 32);
	}
	public void write(long x) {
		write(x, 64);
	}
	public void write(byte ... x) {
		for (byte y : x) {
			if (pos >= size) {
				break;
			}
			write(y, 8);
		}
	}
	public void write(char ... x) {
		for (char y : x) {
			if (pos >= size) {
				break;
			}
			write(y, 16);
		}
	}
	public void write(int ... x) {
		for (int y : x) {
			if (pos >= size) {
				break;
			}
			write(y, 32);
		}
	}
	public void write(long ... x) {
		for (long y : x) {
			if (pos >= size) {
				break;
			}
			write(y, 64);
		}
	}
	public void write(byte x, int count) {
		count = Math.min(Math.max(count, 0), 8);
		int d = ((x << (8 - count)) & 0xFF) & (0xFF00 >> count);
		int used = pos & 7;
		int p = pos >> 3;
		data[p] = (byte)(data[p] | (d >> used));
		data[p+1] = (byte)(d << (8 - used));
		pos += count;
	}
	public void write(char x, int count) {
		count = Math.min(Math.max(count, 0), 16);
		int d = ((x << (16 - count)) & 0xFFFF) & (0xFFFF << (16 - count));
		int used = pos & 7;
		int p = pos >> 3;
		data[p] = (byte)(data[p] | (d >> (used + 8)));
		data[p+1] = (byte)(d >> used);
		data[p+2] = (byte)(d << (8 - used));
		pos += count;
	}
	public void write(int x, int count) {
		count = Math.min(Math.max(count, 0), 32);
		int mask = ~(-1 << count);
		int d = x << (32 - count);
		int used = pos & 7;
		int p = pos >> 3;
		data[p] = (byte)(data[p] | ((d >> (used + 24)) & (mask >> 24)));
		data[p+1] = (byte)((d >> (used + 16)) & (mask >> 16));
		data[p+2] = (byte)((d >> (used + 8)) & (mask >> 16));
		data[p+3] = (byte)((d >> used) & mask);
		data[p+4] = (byte)(d << (8 - used));
		pos += count;
	}
	public void write(long x, int count) {
		count = Math.min(Math.max(count, 0), 64);
		long mask = ~(-1L << count);
		long d = ((int)(x << (64 - count)));
		int used = pos & 7;
		int p = pos >> 3;
		data[p] = (byte)(data[p] | ((d >> (used + 56))) & (mask >> 56));
		data[p+1] = (byte)((d >> (used + 48)) & (mask >> 48));
		data[p+2] = (byte)((d >> (used + 40)) & (mask >> 40));
		data[p+3] = (byte)((d >> (used + 32)) & (mask >> 32));
		data[p+4] = (byte)((d >> (used + 24)) & (mask >> 24));
		data[p+5] = (byte)((d >> (used + 16)) & (mask >> 16));
		data[p+6] = (byte)((d >> (used + 8)) & (mask >> 8));
		data[p+7] = (byte)((d >> used) & mask);
		data[p+8] = (byte)(d << (8 - used));
		pos += count;
	}
}