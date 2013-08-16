package cn.yueying.tools;

public abstract class OnThreadOperator<Params, Result> {
	protected final OnThreadOperatorListener<Result> mListener;

	public OnThreadOperator(OnThreadOperatorListener<Result> mListener) {
		this.mListener = mListener;
	}

	public abstract Result doInbackgroud(final Params... params);

	public void startOperator(final Params... params) {
		new Thread() {

			@Override
			public void run() {
				operatorStart();
				Result result = null;
				try {
					result = doInbackgroud(params);
				} catch (Exception e) {
					operatorFailed(e);
				} finally {
					operatorSuccess(result);
					operatorFinished();
				}
			}

		}.start();

	}

	private void operatorStart() {
		if (mListener != null) {
			mListener.onOperatorStart();
		}
	}

	private void operatorSuccess(final Result rtnMsg) {
		if (mListener != null) {
			mListener.onOperatorSuccess(rtnMsg);
		}
	}

	private void operatorFailed(final Exception e) {
		if (mListener != null) {
			mListener.onOperatorFailed(e);
		}
	}

	private void operatorFinished() {
		if (mListener != null) {
			mListener.onOperatorFinish();
		}
	}

}
