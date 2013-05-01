package edu.mccc.cos210.qrks;

public interface SingWorkerProtected<T> {
	@SuppressWarnings({"unchecked"})
	void publish(T ... chunks);
	void setProgress(int p);
}