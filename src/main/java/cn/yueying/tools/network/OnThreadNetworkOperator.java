package cn.yueying.tools.network;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import cn.yueying.hairstyle.HairstyleApplication;
import cn.yueying.tools.OnThreadOperatorListener;
import cn.yueying.tools.file.InputReader;
import android.content.Context;

public class OnThreadNetworkOperator {
	private final Context mContext;
	private final OnThreadOperatorListener<String> mListener;

	public OnThreadNetworkOperator(Context context,
			OnThreadOperatorListener<String> onThreadOperatorListener) {
		mContext = context;
		mListener = onThreadOperatorListener;
	}

	public String doBackgroud(String actionName, Map<String, Object> paramsMap) throws Exception {
		NetConnction mNetConnction = new NetConnction(HairstyleApplication.getApplication(mContext)
				.getDefaultHttpClient());
		List<NameValuePair> paramList = null;
		if (paramsMap != null) {
			paramList = new ArrayList<NameValuePair>();
			for (String key : paramsMap.keySet()) {
				paramList.add(new BasicNameValuePair(key, String.valueOf(paramsMap.get(key))));
			}
		}
		String rtnMsg = "";
		if (paramList == null) {
			rtnMsg = InputReader.read(mNetConnction.getHttpEntity(actionName));
		} else {
			rtnMsg = InputReader.read(mNetConnction
					.getHttpEntity(actionName, paramList, HTTP.UTF_8));
		}
		return rtnMsg;
	}

	public void startOperator(final String actionName, final Map<String, Object> paramsMap) {
		new Thread() {

			@Override
			public void run() {
				operatorStart();
				String result = null;
				try {
					result = doBackgroud(actionName, paramsMap);
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

	private void operatorSuccess(final String rtnMsg) {
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
