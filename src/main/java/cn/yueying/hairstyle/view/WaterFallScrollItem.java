package cn.yueying.hairstyle.view;

import cn.yueying.hairstyle.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView.ScaleType;

public class WaterFallScrollItem<T extends ImageReadableObj> extends FrameLayout {

	private static final long LOAD_WAIT_TIME = 500L;
	private LayoutParams mLayoutParams;

	public WaterFallScrollItem(Context context) {
		super(context);
		initViews();
	}

	public WaterFallScrollItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		initViews();
	}

	private void initViews() {
		setBackgroundResource(R.drawable.bg_waterfall_item);
		int padding = getContext().getResources().getDimensionPixelSize(
				R.dimen.choice_item_padding_y);
		setPadding(padding, padding, padding, padding);
		mLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		resetImageSrc();
	}

	private void resetImageSrc() {
		if (getChildCount() > 0 && getChildAt(0) instanceof FixedImageView) {
			FixedImageView imageView = (FixedImageView) getChildAt(0);
			imageView.setLayoutParams(mLayoutParams);
			imageView.reload();
		}
	}

	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		super.onVisibilityChanged(changedView, visibility);
		if (visibility == VISIBLE) {
			resetImageSrc();
		}
	}

	public void addItemContent(T item) {
		if (getChildCount() > 0) {
			resetImageSrc();
		} else {
			FixedImageView imageView = new FixedImageView(getContext());
			imageView.setScaleType(ScaleType.FIT_XY);
			imageView.loadImage(item.getImageUrl(), LOAD_WAIT_TIME);
			addView(imageView, mLayoutParams);
		}
	}

	public void recycleItemContent() {
		if (getChildCount() > 0) {
			removeAllViews();
		}
	}
}
