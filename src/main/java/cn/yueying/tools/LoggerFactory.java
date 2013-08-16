package cn.yueying.tools;

import java.text.MessageFormat;

import cn.yueying.hairstyle.HairstyleApplication;

public class LoggerFactory {

	public static Logger getLogger(Class<?> clz) {
		return new Logger(clz.getSimpleName());
	}

	private LoggerFactory() {

	}

	public static final class Logger {
		private final boolean DEBUG = HairstyleApplication.DEBUG;
		private final String tag;

		public Logger(String tag) {
			this.tag = tag;
		}

		public void LogI(String msg, Object... objs) {
			if (DEBUG) {
				msg = MessageFormat.format(msg, objs);
				android.util.Log.i(tag, msg);
			}
		}

		public void LogD(String msg, Object... objs) {
			if (DEBUG) {
				msg = MessageFormat.format(msg, objs);
				android.util.Log.d(tag, msg);
			}
		}

		public void LogW(String msg, Object... objs) {
			if (DEBUG) {
				msg = MessageFormat.format(msg, objs);
				android.util.Log.w(tag, msg);
			}
		}

		public void LogE(String msg, Object... objs) {
			if (DEBUG) {
				msg = MessageFormat.format(msg, objs);
				android.util.Log.e(tag, msg);
			}
		}

		public void LogI(String msg) {
			if (DEBUG) {
				android.util.Log.i(tag, msg);
			}
		}

		public void LogD(String msg) {
			if (DEBUG) {
				android.util.Log.d(tag, msg);
			}
		}

		public void LogW(String msg) {
			if (DEBUG) {
				android.util.Log.w(tag, msg);
			}
		}

		public void LogE(String msg) {
			if (DEBUG) {
				android.util.Log.e(tag, msg);
			}
		}
	}
}
