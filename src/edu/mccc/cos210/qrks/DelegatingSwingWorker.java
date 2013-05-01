package edu.mccc.cos210.qrks;
import javax.swing.SwingWorker;

public abstract class DelegatingSwingWorker<T,V> extends SwingWorker<T,V> {
	public class Protected implements SingWorkerProtected<V> {
		@SuppressWarnings({"unchecked"})
		public void publish(V ... chunks) {
			DelegatingSwingWorker.this.publish(chunks);
		}
		public void setProgress(int p){
			DelegatingSwingWorker.this.setProgress(p);
		}
	}
	protected SingWorkerProtected<V> getProtected() {
		return new Protected();
	}
}