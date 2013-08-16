/**
 * Copyright (C) 2008-2100, all rights reserved.
 * Company<br>		OPPO Mobile Comm Corp., Ltd. 
 * Author<br>		luoJ
 * Since<br>		2012-5-22
 */
package cn.yueying.tools.file;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

/**
 * @Description<br>
 * @Author<br>luoJ
 * @Since<br>2012-5-22
 */
public class InputReader {
	public static String read(HttpEntity httpEntity) throws IOException, HttpException {
		String rtnStr = EntityUtils.toString(httpEntity, HTTP.UTF_8);
		if (rtnStr == null) {
			throw new HttpException("Sever Response failed");
		}
		return rtnStr;
	}

	/**
	 * 
	 * @param uc
	 * @return String
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 * @throws HttpException
	 */
	public static String read(HttpURLConnection uc) throws UnsupportedEncodingException,
			IOException, HttpException {
		InputStream is = uc.getInputStream();
		if (is == null) {
			throw new HttpException("400");
		}
		try {
			return toString(is, HTTP.UTF_8);
		} finally {
			is.close();
		}
	}

	private static String toString(InputStream input, String charset) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		int size = 0;
		while ((size = input.read(data)) != -1) {
			output.write(data, 0, size);
		}
		return new String(output.toByteArray(), charset);
	}
}
