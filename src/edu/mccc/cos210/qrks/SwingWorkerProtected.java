package edu.mccc.cos210.qrks;
import javax.swing.*;

public abstract class SwingWorkerProtected<T,V> {
	public SwingWorkerProtected(SwingWorker<T,V> worker) {
		if (worker == null) {
			throw new NullPointerException("worker");
		}
		this.worker = worker;
	}
	public final SwingWorker<T, V> worker;
	@SuppressWarnings({"unchecked"})
	public abstract void publish(V ... chunks);
	public abstract void setProgress(int p);
	public boolean isCancelled(){
		return worker.isCancelled();
	}
	public boolean isDone(){
		return worker.isDone();
	}
}