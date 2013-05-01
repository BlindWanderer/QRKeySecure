package edu.mccc.cos210.qrks;
import javax.swing.*;

public interface SwingWorkerProtected<T,V> {
	@SuppressWarnings({"unchecked"})
	public void publish(V ... chunks);
	public void setProgress(int p);
	public SwingWorker<T, V> getSwingWorker();
}