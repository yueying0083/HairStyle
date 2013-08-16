package cn.yueying.hairstyle;

import java.util.List;
import cn.yueying.hairstyle.view.WaterFallScrollView;
import cn.yueying.hairstyle.view.WaterFallScrollView.OnItemClickListener;
import cn.yueying.hairstyle.view.WaterFallScrollView.OnScrollListener;
import cn.yueying.hairstyle.view.WaterFallScrollView.SimpleOnScrollListener;
import cn.yueying.tools.DialogTools;
import cn.yueying.tools.LoggerFactory;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends BaseActivity {
	public static final LoggerFactory.Logger mLogger = LoggerFactory.getLogger(MainActivity.class);
	public static final String LAST_READ_HAIR_STYLE_ID = "LAST_READ_HAIR_STYLE_ID";
	private HairStyleDao mHairStyleDao;
	private WaterFallScrollView<HairStyle> mWaterFallScrollView;
	private boolean mIsQuerying = false;
	private long mCurrentQueryId = 0;
	private SharedPreferences mSharedPreferences;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mWaterFallScrollView = (WaterFallScrollView<HairStyle>) this
				.findViewById(R.id.staggeredGridView1);
		mWaterFallScrollView.setOnItemClickListener(mOnItemClickListener);
		initData();
	}

	public void initData() {
		//loadSharedPreference();
		mHairStyleDao = HairstyleApplication.getApplication(this).getDaoSession().getHairStyleDao();
		for (HairStyle hs : queryByDate(mCurrentQueryId)) {
			mWaterFallScrollView.addItem(hs);
			mCurrentQueryId = hs.getId();
		}
	}

	@SuppressWarnings("unused")
	private void loadSharedPreference() {
		mSharedPreferences = getSharedPreferences("SP", MODE_PRIVATE);
		mCurrentQueryId = mSharedPreferences.getLong(LAST_READ_HAIR_STYLE_ID, 0L);
		if (mCurrentQueryId != 0) {
			DialogTools.showWarningDialog(this, getString(R.string.confirm_dialog_content),
					mOnClickListener);
		}
	}

	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.btn_confirm_dialog_cancel:
					mCurrentQueryId = 0L;
					break;
			}
		}
	};

	public List<HairStyle> queryByDate(long currentQueryId) {
		mIsQuerying = true;
		mWaterFallScrollView.setOnScrollListener(null);
		List<HairStyle> hsList = mHairStyleDao.queryBuilder()
				.where(HairStyleDao.Properties.Id.gt(currentQueryId))
				.orderAsc(HairStyleDao.Properties.Id).limit(10).list();
		try {
			return hsList;
		} finally {
			mIsQuerying = false;
			mWaterFallScrollView.setOnScrollListener(mOnScrollListener);
		}
	}

	private OnScrollListener mOnScrollListener = new SimpleOnScrollListener() {

		@Override
		public void onBottom() {
			if (mIsQuerying)
				return;
			List<HairStyle> list = queryByDate(mCurrentQueryId);
			if (list != null && !list.isEmpty()) {
//				saveLastReadId(list.get(0).getId());
				for (HairStyle hs : list) {
					mLogger.LogE("onBottom add hs.id = {}", hs.getId());
					mWaterFallScrollView.addItem(hs);
					mCurrentQueryId = hs.getId();
				}
			}
		}

	};

	@SuppressWarnings("unused")
	private void saveLastReadId(long id) {
		Editor editor = mSharedPreferences.edit();
		editor.putLong(LAST_READ_HAIR_STYLE_ID, id);
		editor.commit();
	}

	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(View view) {
			Object obj = view.getTag();
			if (obj != null && obj instanceof HairStyle) {
				HairStyle hs = (HairStyle) obj;
				mLogger.LogE("item click on hairStyle.id = {}", hs.getId());
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, HairStyleDetailActivity.class);
				intent.putExtra(HairStyleDetailActivity.INTENT_HAIR_STYLE_ID, hs);
				startActivity(intent);
			}
		}

	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
