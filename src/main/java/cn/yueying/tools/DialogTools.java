package cn.yueying.tools;

import cn.yueying.hairstyle.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DialogTools {

	/**
	 * 显示提醒对话框
	 * 
	 * @param context
	 * @param warn
	 *            提示的警告信息字符串
	 * @param onClickListener
	 *            为ID分别为BUTTON_ID_OK和BUTTON_ID_CANCEL的按钮提供点击响应事件
	 * @return Dialog
	 */
	public static Dialog showWarningDialog(Context context, String warn,
			final OnClickListener onClickListener) {
		final AlertDialog dialog = new AlertDialog.Builder(context).create();
		dialog.show();
		Window window = dialog.getWindow();
		window.setContentView(R.layout.confirm_dialog);
		TextView message = (TextView) window.findViewById(R.id.tv_confirm_dialog_msg);
		message.setText(warn);
		Button ok = (Button) window.findViewById(R.id.btn_confirm_dialog_confirm);
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onClickListener.onClick(v);
				dialog.dismiss();
			}
		});
		Button cancel = (Button) window.findViewById(R.id.btn_confirm_dialog_cancel);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onClickListener.onClick(v);
				dialog.dismiss();
			}
		});
		return dialog;
	}

}
