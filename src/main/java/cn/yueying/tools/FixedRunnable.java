package cn.yueying.tools;

public abstract class FixedRunnable<T> implements Runnable {

	private T t;

	public FixedRunnable(T t) {
		this.t = t;
	}
	
	public abstract void run(T t);

	@Override
	public void run() {
		run(t);
	}

}
