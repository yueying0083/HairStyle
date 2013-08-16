package cn.yueying.hairstyle.view;

import java.util.ArrayList;
import java.util.List;

import cn.yueying.tools.LoggerFactory;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Scroller;

@SuppressWarnings("unchecked")
public class WaterFallScrollView<T extends ImageReadableObj> extends ScrollView {

	private static final LoggerFactory.Logger mLogger = LoggerFactory
			.getLogger(WaterFallScrollView.class);
	private static final int NUMBER_COLUMNS = 2; // 列数
	private static final int PARENT_LEFT_PADDING = 9;
	private static final int PARENT_RIGHT_PADDING = 9;
	private static final int PARENT_TOP_PADDINT = 2;
	private static final int ITEM_RIGHT_MARGINE = 2;
	private static final int ITEM_TOP_MARGINE = 2;

	private LinearLayout mScrollViewContainer; // 容器
	private List<LinearLayout> mScrollViewItemContainers;
	private List<Integer> mScrollViewItemViewCounts; // 每个Item中View的个数
	private List<Integer> mScrollViewItemHeights; // 每个Item的高度
	private List<Integer> mItemTopIndexs; // 每个Item中上端未被回收的Index，初始换值为0
	private List<Integer> mItemBottomIndex; // 每个Item中末端未被回收 index，初始化值为-1
	private int mAvailableWidth;
	private List<Integer>[] mScrollViewItemsBottonY; // 每个Item中每个子view的y坐标高度

	private OnScrollListener mOnScrollListener = null;
	private OnItemClickListener mOnItemClickListener = null;
	private ScrollHandler mHandler;
	private int mSDKVersion;

	/**
	 * @Description<br>滑动接口
	 * @Author<br>hufei
	 * @Since<br>2012-10-17
	 */
	public interface OnScrollListener {

		void onBottom();

		void onTop();

		void onScroll();
	}

	public static class SimpleOnScrollListener implements OnScrollListener {

		@Override
		public void onBottom() {
		}

		@Override
		public void onTop() {
		}

		@Override
		public void onScroll() {
		}

	}

	public WaterFallScrollView(Context context) {
		super(context);
		setupDatas();
		setupViews();
	}

	public WaterFallScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupDatas();
		setupViews();
	}

	public WaterFallScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setupDatas();
		setupViews();
	}

	public interface OnItemClickListener {

		void onItemClick(View view);
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		mOnItemClickListener = listener;
	}

	public void setOnScrollListener(OnScrollListener listener) {
		mOnScrollListener = listener;
	}

	private Scroller mScroller;

	@Override
	public void fling(int velocityY) {
		if (mSDKVersion >= 14) {
			super.fling(velocityY * 2 / 3);
		} else {
			int height = getHeight() - getPaddingBottom() - getPaddingTop();
			int bottom = getChildAt(0).getHeight();
			mScroller.fling(getScrollX(), getScrollY(), 0, velocityY * 2 / 3, 0, 0, 0,
					Math.max(0, bottom - height));
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void computeScroll() {
		if (mSDKVersion >= 14) {
			super.computeScroll();
		} else if (mScroller.computeScrollOffset()) {
			int oldX = getScrollX();
			int oldY = getScrollY();
			int x = mScroller.getCurrX();
			int y = mScroller.getCurrY();
			if (oldX != x || oldY != y) {
				overScrollBy(x - oldX, y - oldY, oldX, oldY, 0, getScrollRange(), 0, 0, false);
				onScrollChanged(x, y, oldX, oldY);
			}
			postInvalidate();
		}
	}

	private int getScrollRange() {
		int scrollRange = 0;
		if (getChildCount() > 0) {
			View child = getChildAt(0);
			scrollRange = Math.max(0, child.getHeight()
					- (getHeight() - getPaddingBottom() - getPaddingTop()));
		}
		return scrollRange;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			mScroller.abortAnimation();
		}
		return super.onInterceptTouchEvent(ev);
	}

	/**
	 * @Description<br>添加Item
	 * @param bitmap
	 * @param tag
	 */
	public void addItem(T t) {
		if (t == null) {
			return;
		}
		int showWidth = mAvailableWidth / NUMBER_COLUMNS;
		int showHeight = (int) (showWidth * ((float) t.getImageHeight() / (float) t.getImageWidth()));
		WaterFallScrollItem<T> item = new WaterFallScrollItem<T>(getContext());
		LayoutParams itemParams = new LayoutParams(showWidth, showHeight);
		itemParams.topMargin = ITEM_TOP_MARGINE;
		item.setLayoutParams(itemParams);
		item.setTag(t);
		item.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mOnItemClickListener != null) {
					mOnItemClickListener.onItemClick(v);
				}
			}
		});

		// 计算高度最小的列，然后把image添加进去
		int minHeightIndex = getMinHeightColumnsIndex();

		mScrollViewItemContainers.get(minHeightIndex).addView(item);
		invalidate();
		int itemViewCount = mScrollViewItemViewCounts.get(minHeightIndex) + 1;

		int waterFallItemHeight = showHeight + ITEM_TOP_MARGINE;
		int itemHeight = mScrollViewItemHeights.get(minHeightIndex);

		mScrollViewItemHeights.set(minHeightIndex, itemHeight + waterFallItemHeight); // 刷新该列的高度
		mItemBottomIndex.set(minHeightIndex, mItemBottomIndex.get(minHeightIndex) + 1); // 刷新该列最低端显示的view的编号

		mScrollViewItemViewCounts.set(minHeightIndex, itemViewCount); // 更新该列中Itemview的个数
		mScrollViewItemsBottonY[minHeightIndex].add(itemViewCount - 1, itemHeight);
		item.addItemContent(t);
		requestLayout();
		invalidate();
	}

	public void notifyDataSetChanged() {
		if (null != mScrollViewItemContainers) {
			for (int i = 0; i < NUMBER_COLUMNS; i++) {
				LinearLayout container = mScrollViewItemContainers.get(i);
				int count = container.getChildCount();
				for (int j = 0; j < count; j++) {
					WaterFallScrollItem<T> oppoImageView = (WaterFallScrollItem<T>) container
							.getChildAt(j);
					oppoImageView.recycleItemContent();
					oppoImageView = null;
				}
				container.removeAllViews();
			}
		}

		if (null != mScrollViewItemViewCounts) {
			mScrollViewItemViewCounts.clear();
		}

		if (null != mScrollViewItemHeights) {
			mScrollViewItemHeights.clear();
		}

		if (null != mItemTopIndexs) {
			mItemTopIndexs.clear();
		}

		if (null != mItemBottomIndex) {
			mItemBottomIndex.clear();
		}

		if (null != mScrollViewItemsBottonY) {
			int length = mScrollViewItemsBottonY.length;
			for (int i = 0; i < length; i++) {
				List<Integer> list = mScrollViewItemsBottonY[i];
				list.clear();
			}
		}

		for (int i = 0; i < NUMBER_COLUMNS; i++) {
			mScrollViewItemViewCounts.add(i, 0);
			mScrollViewItemHeights.add(i, 0);
			mItemTopIndexs.add(i, 0);
			mItemBottomIndex.add(i, -1);
			mScrollViewItemsBottonY[i] = new ArrayList<Integer>();
		}
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		mLogger.LogD("onScrollChanged t = {}, oldt = {}", t, oldt);
		Message msg = mHandler.obtainMessage();
		msg.what = MSG_SCROLL_CHANGED;
		mHandler.removeMessages(MSG_SCROLL_CHANGED);
		mHandler.sendMessageDelayed(msg, 200);
		recycleResourcesIfNeed(t, oldt);
	}

	public void recycleResources() {
		if (null != mScrollViewItemContainers) {
			for (int i = 0; i < NUMBER_COLUMNS; i++) {
				LinearLayout container = (LinearLayout) mScrollViewItemContainers.get(i);
				int count = container.getChildCount();
				for (int j = 0; j < count; j++) {
					WaterFallScrollItem<T> oppoImageView = (WaterFallScrollItem<T>) container
							.getChildAt(j);
					// recycleItemView(oppoImageView);
					oppoImageView.recycleItemContent();
					oppoImageView = null;
				}
				container.removeAllViews();
				container = null;
			}
			mScrollViewItemContainers.clear();
			mScrollViewItemContainers = null;
		}

		if (null != mScrollViewContainer) {
			mScrollViewContainer.removeAllViews();
			mScrollViewContainer = null;
		}
		if (null != mScrollViewItemViewCounts) {
			mScrollViewItemViewCounts.clear();
			mScrollViewItemViewCounts = null;
		}

		if (null != mScrollViewItemHeights) {
			mScrollViewItemHeights.clear();
			mScrollViewItemHeights = null;
		}

		if (null != mItemTopIndexs) {
			mItemTopIndexs.clear();
			mItemTopIndexs = null;
		}

		if (null != mItemBottomIndex) {
			mItemBottomIndex.clear();
			mItemBottomIndex = null;
		}

		if (null != mScrollViewItemsBottonY) {
			int length = mScrollViewItemsBottonY.length;
			for (int i = 0; i < length; i++) {
				List<Integer> list = mScrollViewItemsBottonY[i];
				list.clear();
				list = null;
			}
		}

		mOnScrollListener = null;
		mOnItemClickListener = null;

		this.removeAllViews();

	}

	private static final int MSG_SCROLL_CHANGED = 1;

	@SuppressLint("HandlerLeak")
	private class ScrollHandler extends Handler {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case MSG_SCROLL_CHANGED: // 完成滑动
					if (mOnScrollListener == null) {
						return;
					}
					int totalHeight = mScrollViewContainer.getMeasuredHeight();
					int scrollViewHeight = WaterFallScrollView.this.getHeight();
					int scrollY = WaterFallScrollView.this.getScrollY();
					if (scrollY <= 0) {
						mOnScrollListener.onTop();
					} else if ((scrollY + scrollViewHeight) >= totalHeight) {
						mOnScrollListener.onBottom();
					} else {
						mOnScrollListener.onScroll();
					}
					break;
				default:
					break;
			}

		};
	};

	/**
	 * @Description<br>获取Item中最小的列数
	 * @return
	 */
	private int getMinHeightColumnsIndex() {
		int index = -1;
		int minHeight = Integer.MAX_VALUE;
		for (int i = 0; i < NUMBER_COLUMNS; i++) {
			int itemHeight = mScrollViewItemHeights.get(i);
			if (itemHeight < minHeight) {
				minHeight = itemHeight;
				index = i;
			}
		}
		return index;
	}

	/**
	 * @Description<br>根据当前的滚动情况回收资源
	 * @param t
	 * @param oldT
	 */
	private void recycleResourcesIfNeed(int t, int oldT) {
		int scrollViewHeight = this.getMeasuredHeight();
		mLogger.LogD("scrollViewHeight = {}, t = {}, oldT = {}", scrollViewHeight, t, oldT);
		if (t > oldT)// 下滑，回收上面的图片资源，加载下面的图片资源
		{
			if (t <= 2 * scrollViewHeight) {
				return;
			}
			for (int i = 0; i < NUMBER_COLUMNS; i++) {
				int topIndex = mItemTopIndexs.get(i);
				int bottomIndex = mItemBottomIndex.get(i);
				int itemViewCount = mScrollViewItemViewCounts.get(i);
				List<Integer> itemViewsBittonY = mScrollViewItemsBottonY[i];
				// add to avoid itemViewsBittonY is null
				if (itemViewsBittonY == null || itemViewsBittonY.isEmpty()) {
					break;
				}
				LinearLayout container = mScrollViewItemContainers.get(i);
				int minCount = Math.min(topIndex, itemViewCount - 1);
				if (itemViewsBittonY.get(minCount) < t - 2 * scrollViewHeight) {
					WaterFallScrollItem<T> topImage = (WaterFallScrollItem<T>) container
							.getChildAt(minCount);
					mLogger.LogD("remove msg = {}", topImage.getId());
					topImage.recycleItemContent();
					topIndex = minCount;
					topIndex++;
					mItemTopIndexs.set(i, topIndex);
				}
				// :~
				int min = Math.min(bottomIndex, itemViewCount - 1);
				if (itemViewsBittonY.get(min) < t + 3 * scrollViewHeight) {
					WaterFallScrollItem<T> bottomImage = (WaterFallScrollItem<T>) container
							.getChildAt(min);
					bottomImage.addItemContent((T) bottomImage.getTag());
					bottomIndex = min;
					if (bottomIndex < itemViewCount - 1) {
						bottomIndex++;
					}
					mItemBottomIndex.set(i, bottomIndex);
				}
			}

			// 加载下面部分的图片资源
		} else {// 上滑，回收下面的资源，加载上面的控件
			// 上面加载
			for (int i = 0; i < NUMBER_COLUMNS; i++) {

				int topIndex = mItemTopIndexs.get(i);
				int bottomIndex = mItemBottomIndex.get(i);
				int itemViewCount = mScrollViewItemViewCounts.get(i);
				List<Integer> itemViewsBittonY = mScrollViewItemsBottonY[i];
				if (itemViewsBittonY == null || itemViewsBittonY.isEmpty()) {
					break;
				}
				// :~
				LinearLayout container = mScrollViewItemContainers.get(i);

				int max = Math.max(0, topIndex);
				if (itemViewsBittonY.get(max) >= t - 2 * scrollViewHeight) {
					WaterFallScrollItem<T> bottomImage = (WaterFallScrollItem<T>) container
							.getChildAt(topIndex);
					bottomImage.addItemContent((T) bottomImage.getTag());
					topIndex = Math.max(0, topIndex - 1);
					mItemTopIndexs.set(i, topIndex);
				}
				// :~

				// 下面回收
				int min = Math.min(bottomIndex, itemViewCount - 1);
				if (itemViewsBittonY.get(min) > t + 3 * scrollViewHeight) {
					WaterFallScrollItem<T> bottomImage = (WaterFallScrollItem<T>) container
							.getChildAt(min);
					mLogger.LogD("remove msg = {}", bottomImage.getId());
					bottomImage.recycleItemContent();
					bottomIndex = min;
					bottomIndex--;
					mItemBottomIndex.set(i, bottomIndex);
				}
			}
			// :~

		}
	}

	private void setupDatas() {
		mHandler = new ScrollHandler();
		mSDKVersion = getAndroidSDKVersion();
		mScroller = new Scroller(getContext());
		int screenWidth = this.getResources().getDisplayMetrics().widthPixels;
		mAvailableWidth = screenWidth - PARENT_LEFT_PADDING - PARENT_RIGHT_PADDING
				- ITEM_RIGHT_MARGINE * (NUMBER_COLUMNS - 1);
		mScrollViewItemViewCounts = new ArrayList<Integer>(); // 每个Item中View的个数
		mScrollViewItemHeights = new ArrayList<Integer>(); // 每个Item的高度
		mItemTopIndexs = new ArrayList<Integer>(); // 每个Item中上端未被回收的Index，初始换值为0
		mItemBottomIndex = new ArrayList<Integer>(); // 每个Item中末端未被回收
														// index，初始化值为-1

		mScrollViewItemsBottonY = new ArrayList[NUMBER_COLUMNS]; // 每列中每个Item的底部Y坐标
		for (int i = 0; i < NUMBER_COLUMNS; i++) {
			mScrollViewItemViewCounts.add(i, 0);
			mScrollViewItemHeights.add(i, 0);
			mItemTopIndexs.add(i, 0);
			mItemBottomIndex.add(i, -1);
			mScrollViewItemsBottonY[i] = new ArrayList<Integer>();
		}
	}

	private void setupViews() {
		this.setBackgroundColor(0xfffffcee);
		mScrollViewContainer = new LinearLayout(getContext());
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		mScrollViewContainer.setLayoutParams(params);
		mScrollViewContainer.setOrientation(LinearLayout.HORIZONTAL);
		addView(mScrollViewContainer);

		mScrollViewItemContainers = new ArrayList<LinearLayout>();
		for (int i = 0; i < NUMBER_COLUMNS; i++) {
			LinearLayout layout = createLinearLayout(i);
			mScrollViewItemContainers.add(i, layout);
			mScrollViewContainer.addView(layout);
		}
	}

	private LinearLayout createLinearLayout(int col) {
		LinearLayout layout = new LinearLayout(getContext());
		LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(mAvailableWidth
				/ NUMBER_COLUMNS, LinearLayout.LayoutParams.WRAP_CONTENT);
		itemParams.topMargin = PARENT_TOP_PADDINT;
		if (NUMBER_COLUMNS != col + 1) {
			itemParams.rightMargin = ITEM_RIGHT_MARGINE;
		}
		if (0 == col) {
			itemParams.leftMargin = PARENT_LEFT_PADDING;
		} else if (NUMBER_COLUMNS == col + 1) {
			itemParams.rightMargin = PARENT_RIGHT_PADDING;
		}
		layout.setLayoutParams(itemParams);
		layout.setOrientation(LinearLayout.VERTICAL);
		return layout;
	}

	@SuppressWarnings("deprecation")
	private int getAndroidSDKVersion() {
		int version = 0;
		try {
			version = Integer.valueOf(android.os.Build.VERSION.SDK);
		} catch (NumberFormatException e) {
		}
		return version;
	}
}