package cn.yueying.tools;

public interface OnThreadOperatorListener<Result> {

	public void onOperatorStart();

	public void onOperatorSuccess(Result rtnMsg);

	public void onOperatorFailed(Exception e);

	public void onOperatorFinish();

	public static class SimpleOnThreadOperatorListener<Result> implements OnThreadOperatorListener<Result> {

		@Override
		public void onOperatorStart() {
		}

		@Override
		public void onOperatorSuccess(Result rtnMsg) {
		}

		@Override
		public void onOperatorFailed(Exception e) {
		}

		@Override
		public void onOperatorFinish() {
		}

	}

}
