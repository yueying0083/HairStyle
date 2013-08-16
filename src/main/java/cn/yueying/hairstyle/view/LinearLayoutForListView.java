package cn.yueying.hairstyle.view;

import java.util.ArrayList;
import java.util.List;
import cn.yueying.hairstyle.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

public class LinearLayoutForListView<T> extends LinearLayout {

	public static final int TAG_COMMENT_VIEWHOLDER_ID = R.id.llfl_viewholder_tag;
	public static final int TAG_COMMENT_OBJ_ID = R.id.llfl_obj_tag;
	public static final int TAG_COMMENT_CLICK_ZONE_ID = R.id.comment_viewholder_clickzone_tag;

	private LinearLayoutListAdapter<T> adapter;
	private OnClickListener onClickListener = null;
	private List<DataSetObserver> mDataSetObservers;

	/**
	 * 绑定布局
	 */
	public void bindLinearLayout(int start) {
		int count = adapter.getCount();
		for (int i = start; i < count; i++) {
			View v = adapter.getView(i, null, null);
			Object o = v.getTag(TAG_COMMENT_CLICK_ZONE_ID);
			if (o != null && o instanceof View) {
				((View) o).setOnClickListener(this.onClickListener);
			}
			addView(v, i);
		}
	}

	public LinearLayoutForListView(Context context) {
		super(context, null);
	}

	public LinearLayoutForListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(LinearLayout.VERTICAL);
	}

	/**
	 * 获取Adapter
	 * 
	 * @return adapter
	 */
	public Adapter getAdapter() {
		return adapter;
	}

	/**
	 * 设置数据
	 * 
	 * @param adpater
	 */
	public void setAdapter(final LinearLayoutListAdapter<T> adapter) {
		removeAllViews();
		this.adapter = adapter;
		bindLinearLayout(0);
	}

	/**
	 * 添加数据入口
	 * 
	 * @param data
	 *            void
	 */
	public void addData(List<T> data) {
		int pos = this.adapter.getCount();
		this.adapter.addData(data);
		bindLinearLayout(pos);
		dataSetChange();
	}

	/**
	 * 添加数据入口
	 * 
	 * @param data
	 *            void
	 */
	public void addData(T data) {
		int pos = this.adapter.getCount();
		this.adapter.addData(data);
		bindLinearLayout(pos);
		dataSetChange();
	}

	public void changeData(List<T> list) {
		removeAllViews();
		this.adapter.changeData(list);
		bindLinearLayout(0);
		dataSetChange();
	}

	/**
	 * 获取点击事件
	 * 
	 * @return
	 */
	public OnClickListener getOnclickListner() {
		return onClickListener;
	}

	/**
	 * 设置点击事件
	 * 
	 * @param onClickListener
	 */
	public void setOnclickLinstener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

	private void dataSetChange() {
		if (mDataSetObservers != null) {
			for (DataSetObserver dso : mDataSetObservers) {
				dso.onDataSetChange();
			}
		}
	}

	/**
	 * 注册数据更改观察者
	 * 
	 * @param dso
	 *            void
	 */
	public void regDataSetObserver(DataSetObserver dso) {
		mDataSetObservers = mDataSetObservers == null ? new ArrayList<DataSetObserver>()
				: mDataSetObservers;
		mDataSetObservers.add(dso);
	}

	/**
	 * 取消注册数据更改观察者
	 * 
	 * @param dso
	 *            void
	 */
	public void unRegDataSetObserver(DataSetObserver dso) {
		if (mDataSetObservers != null) {
			mDataSetObservers.remove(dso);
		}
	}

	public static abstract class LinearLayoutListAdapter<T> extends BaseAdapter {

		protected abstract void addData(List<T> list);

		protected abstract void addData(T t);

		protected abstract void changeData(List<T> list);

	}

	public static interface DataSetObserver {
		public void onDataSetChange();
	}

}
