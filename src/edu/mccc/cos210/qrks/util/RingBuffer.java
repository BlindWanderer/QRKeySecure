package edu.mccc.cos210.qrks.util;
import java.util.*;

public class RingBuffer<T> {
//	private static final long serialVersionUID = 1L;
	private T [] values;
	private int position;
	public RingBuffer(Class<T> clazz, final int count) {
		values = Utilities.newGenericArray(clazz, count);
		clear();
	}
	public RingBuffer<T> add(final T s) {
		if (s != null) {
			position = (1 + position != values.length) ? 1 + position : 0;
			values[position] = s;
		}
		return this;
	}
	public T get(final int past) {
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
		T r;
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
	public boolean isFull() {
		return (values[values.length - 1] == null);
	}
	public void clear() {
		position = -1;
		Arrays.fill(values, null);
	}
	public T [] getValues(){
		if (!isFull() || (position == values.length - 1)) {
			return Arrays.copyOf(values, position + 1);
		}
		T [] t = Arrays.copyOf(values, values.length);
		int i = 0;
		for(int p = position + 1; p < values.length; p++, i++) {
			t[i] = values[p];
		}
		for(int p = 0; p <= position; p++, i++) {
			t[i] = values[p];
		}
		return t;
	}
}