package edu.mccc.cos210.qrks.util;
import java.util.*;
public class BitBuffer {
	/*/
	public static void main(String [] args) {
		BitBuffer b = new BitBuffer(36);
		byte [] blah = {(byte)0xF0,(byte)0x00,(byte)0x80,(byte)0x01};
		System.out.println(b);
		b.write(0b10, 1);
//		b.write(0xEFEFEFEF, 31);
		System.out.println(b);
		b.write(blah);
		System.out.println(b);
		System.out.println(Integer.toHexString(0xff & b.data[1]));
	}/**/
	private int size;
	private int pos = 0;
	private byte data[];
	private static int EXTRA = 8;
	public BitBuffer(int initialSize){
		size = initialSize;
		data = new byte[(size + 7 + (EXTRA * 8)) / 8];
		Arrays.fill(data, (byte)0);
	}
	public String toString() {
		return Arrays.toString(getData()) + " @ " + pos + " of " + size;
	}
	public boolean getBitAndIncrementPosition(){
		boolean v = (data[pos >> 3] & (0x80 >> (pos & 7))) != 0;;
		pos++;
		return v; 
	}
	public int getIntAndIncrementPosition(int bitCount) {
		int myInt = 0;
		int min = Math.max(bitCount - getSize(), 0);
		int max = Math.min(bitCount, getSize()) - 1;
		for (int i = max; i >= min; i--) {
			if (getBitAndIncrementPosition()) {
				myInt |= 1 << i ;
			}
		}
		return myInt;
	}
	public int advance(int count) {
		return pos += count;
	}
	public void seek(int pos) {
		this.pos = pos;
	}
	public byte [] getInternalArray() {
		return data;
	}
	public byte[] getData() {
		return Arrays.copyOf(data, data.length - EXTRA);
	}
	public int getSize() {
		return size;
	}
	public void write(boolean x) {
		write(x ? 1 : 0, (byte)1);
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
		final int d = (x << (8 - count)) & 0xFF;
		final int used = pos & 7;
		final int p = pos >> 3;
		final int mask = (0xFF & (0xFF00 >> count));
		data[p] = (byte)((data[p] & ~(mask >> used)) | ((d >>> used)));
		data[p+1] = (byte)((data[p+1] & ~(mask >> (8 - used))) | (d << (8 - used)));
		pos += count;
	}
	public void write(char x, int count) {
		if(count > 8) {
			write((byte)(x >> 8), count - 8);
		}
		write((byte)x, count);
	}
	public void write(int x, int count) {
		if(count > 16) {
			write((char)(x >> 16), count - 16);
		}
		write((char)x, count);
	}
	public void write(long x, int count) {
		if(count > 32) {
			write((int)(x >> 32), count - 32);
		}
		write((int)x, count);
	}
}