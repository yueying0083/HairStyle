package cn.yueying.hairstyle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.impl.client.DefaultHttpClient;
import cn.yueying.hairstyle.DaoMaster.OpenHelper;
import cn.yueying.waterfalllibrary.tool.ImageLoader;
import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class HairstyleApplication extends Application {

	public static final int DB_VERSION = DaoMaster.SCHEMA_VERSION;
	public static final boolean DEBUG = true;
	public static final boolean LOGCAT = false;
	public static final String CACHE_FILE_LOCATION = "hairstyle/bitmapCache";

	private DefaultHttpClient mDefaultHttpClient;
	private DaoMaster mDaoMaster;
	private ImageLoader mImageLoader;

	@Override
	public void onCreate() {
		super.onCreate();
		copyFileIfNotExsit();
		initDatabase();
		mImageLoader = ImageLoader.getInstance(this);
	}

	private void initDatabase() {
		OpenHelper helper = new DaoMaster.OpenHelper(this, getFilesDir().getPath()
				+ "/hairstyle_db", null) {

			@Override
			public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
				// we do not need to upgrade in version 1.
			}

			@Override
			public void onCreate(SQLiteDatabase db) {
				// we just copy file to create datafile.
			}
		};
		mDaoMaster = new DaoMaster(helper.getWritableDatabase());
	}

	private void copyFileIfNotExsit() {
		File file = new File(getFilesDir().getPath() + "/hairstyle_db");
		if (!file.exists()) {
			Log.e("copyFileIfNotExsit", "copyFileIfNotExsit");
			InputStream is = null;
			FileOutputStream os = null;
			try {
				is = getResources().openRawResource(R.raw.hairstyle_db);
				os = new FileOutputStream(file);
				byte[] buffer = new byte[1024];
				int count = 0;
				while ((count = is.read(buffer)) > 0) {
					os.write(buffer, 0, count);
				}
			} catch (IOException e) {
			} finally {
				try {
					if (is != null)
						is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					if (os != null)
						os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public synchronized DaoSession getDaoSession() {
		return mDaoMaster.newSession();
	}

	public synchronized DefaultHttpClient getDefaultHttpClient() {
		if (mDefaultHttpClient == null) {
			mDefaultHttpClient = new DefaultHttpClient();
		}
		return mDefaultHttpClient;
	}
	
	public synchronized ImageLoader getImageLoader(){
		return mImageLoader;
	}

	public static HairstyleApplication getApplication(Context context) {
		return (HairstyleApplication) context.getApplicationContext();
	}

}
