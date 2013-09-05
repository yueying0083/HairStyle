package cn.yueying.hairstyle;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import cn.yueying.tools.LoggerFactory;
import cn.yueying.waterfalllibrary.view.FixedImageView;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HairStyleDetailActivity extends BaseActivity {
	public static final LoggerFactory.Logger mLogger = LoggerFactory
			.getLogger(HairStyleDetailActivity.class);
	public static final String INTENT_HAIR_STYLE_ID = "INTENT_HAIR_STYLE_ID";
	private ViewHolder mViewHolder = new ViewHolder();
	private int mScreenWidth;
	private Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initViews();
		initData();
	}

	private void initViews() {
		mScreenWidth = getResources().getDisplayMetrics().widthPixels;
		setContentView(R.layout.hair_style_detail);
		mViewHolder.mScrollView = findViewById(R.id.sv_hairstyle_detail_container);
		mViewHolder.mScrollView.setDrawingCacheEnabled(true);
		mViewHolder.mLabelView = (TextView) findViewById(R.id.tv_hairstyle_detail_keyword);
		mViewHolder.mTitleView = (TextView) findViewById(R.id.tv_hairstyle_detail_title);
		mViewHolder.mSourceView = (TextView) findViewById(R.id.tv_hairstyle_detail_source);
		mViewHolder.mContentLayout = (LinearLayout) findViewById(R.id.ll_hiar_style_detail);
		mViewHolder.mBtn_01 = (Button) findViewById(R.id.btn_hairstyle_detail_prev);
		mViewHolder.mBtn_01.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Bitmap bm = mViewHolder.mScrollView.getDrawingCache();
				if (bm == null) {
					Toast.makeText(HairStyleDetailActivity.this, "null", Toast.LENGTH_SHORT).show();
					return;
				}
				String fileName = getFilesDir().getPath() + System.currentTimeMillis() + ".jpg";
				File myCaptureFile = new File(fileName);
				BufferedOutputStream bos;
				try {
					bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
					bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
					bos.flush();
					bos.close();
					Toast.makeText(HairStyleDetailActivity.this, "save to " + fileName,
							Toast.LENGTH_SHORT).show();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void initData() {
		Object obj = getIntent().getSerializableExtra(INTENT_HAIR_STYLE_ID);
		if (obj != null && obj instanceof HairStyle) {
			loadDataFromDB((HairStyle) obj);
		}
	}

	private void loadDataFromDB(final HairStyle hs) {
		mViewHolder.mTitleView.setText(hs.getTitle());
		mViewHolder.mSourceView.setText(hs.getSource());
		mViewHolder.mLabelView.setText(hs.getLabel());
		new Thread() {
			@Override
			public void run() {
				HairStyle hss = HairstyleApplication.getApplication(HairStyleDetailActivity.this)
						.getDaoSession().getHairStyleDao().load(hs.getId());
				loadDataToView(hss.getHairStyleContentList());
			}

		}.start();

	}

	private void loadDataToView(final List<HairStyleContent> cList) {
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				mLogger.LogE("cList.size = {}", cList.size());
				for (HairStyleContent hsc : cList) {
					String content = hsc.getPicUrl();
					boolean isText = content == null || "".equals(content)
							|| "null".equals(content);
					View v;
					if (isText) {
						String desc = hsc.getContentDesc();
						if (desc == null || desc.startsWith(getString(R.string.filter_string_1))) {
							continue;
						} else if (desc.startsWith(getString(R.string.filter_string_2))
								|| desc.startsWith(getString(R.string.filter_string_3))) {
							continue;
						}
						v = buildTextView(desc);
						mViewHolder.mContentLayout.addView(v);
					} else {
						v = buildImageView(hsc);
						mViewHolder.mContentLayout.addView(v);
					}
				}
				mViewHolder.mContentLayout.requestLayout();
			}
		};
		if (Looper.getMainLooper() == Looper.myLooper()) {
			runnable.run();
		} else {
			mHandler.post(runnable);
		}
	}

	private View buildImageView(HairStyleContent hsc) {
		View v = LayoutInflater.from(this).inflate(R.layout.hair_style_detail_img, null);
		FixedImageView iv = (FixedImageView) v.findViewById(R.id.fiv_image_content);
		int imageWidth = (int) (mScreenWidth - 2 * getResources().getDimension(R.dimen.margin) - 2 * getResources()
				.getDimension(R.dimen.padding));
		iv.setFitWidth(true, imageWidth, imageWidth);
		iv.loadImage(hsc.getImageUrl(), 0);
		return v;
	}

	private TextView buildTextView(String desc) {
		TextView tv = new TextView(this);
		tv.setTextColor(getResources().getColor(R.color.black));
		tv.setText("\t\t" + desc);
		return tv;
	}

	static class ViewHolder {
		TextView mTitleView;
		TextView mSourceView;
		TextView mLabelView;
		LinearLayout mContentLayout;
		Button mBtn_01;
		View mScrollView;
	}

}
