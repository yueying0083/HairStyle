/**
 * Copyright (C) 2008-2100, all rights reserved.
 * Company<br>		OPPO Mobile Comm Corp., Ltd. 
 * Author<br>		luoJ
 * Since<br>		2012-5-18
 */
package cn.yueying.tools.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import cn.yueying.tools.LoggerFactory;

/**
 * @Description<br>
 * @Author<br>luoJ
 * @Since<br>2012-5-18
 */
public class NetConnction {
	private final String BOUNDARY = "---------------------------7da2137580612";
	private DefaultHttpClient mDefaultHttpClient;
	private final LoggerFactory.Logger mLogger = LoggerFactory.getLogger(getClass());
	private final String serverAdd = "http://m.ulikemm.nearme.com.cn/ulikemm/user/";

	public NetConnction(DefaultHttpClient httpclient) {
		mDefaultHttpClient = httpclient;
	}

	public HttpEntity getHttpEntity(String urlStr) throws HttpException, ClientProtocolException,
			IOException {
		urlStr = serverAdd + urlStr;
		mLogger.LogD("try to get " + urlStr);
		HttpGet req = new HttpGet(urlStr);
		HttpResponse rsp;
		rsp = mDefaultHttpClient.execute(req);
		int statusCode = rsp.getStatusLine().getStatusCode();
		mLogger.LogD("statusCode got " + statusCode);
		if (statusCode != 200) {
			throw new HttpException("statusCode = " + statusCode);
		}
		return rsp.getEntity();
	}

	public HttpEntity getHttpEntity(String urlStr, List<NameValuePair> params, String encoding)
			throws HttpException, ClientProtocolException, IOException {

		urlStr = serverAdd + urlStr;
		mLogger.LogD("try to get " + urlStr + " param[");
		if (params != null) {
			for (NameValuePair nvp : params) {
				if (nvp != null)
					mLogger.LogD(nvp.getName() + " : " + nvp.getValue());
			}
		}
		mLogger.LogD("]");
		return getHttpEntity(urlStr, new UrlEncodedFormEntity(params, encoding));
	}

	private HttpEntity getHttpEntity(String urlStr, HttpEntity entity) throws IOException,
			HttpException {
		HttpPost req = new HttpPost(urlStr);
		req.setEntity(entity);
		HttpResponse rsp;
		rsp = mDefaultHttpClient.execute(req);
		int statusCode = rsp.getStatusLine().getStatusCode();
		if (statusCode != 200) {
			throw new HttpException("statusCode = " + statusCode);
		}
		return rsp.getEntity();
	}

	public HttpURLConnection uploadFile(String urlPath, Map<String, String> map, File file)
			throws IOException, HttpException {
		urlPath = serverAdd + urlPath;
		mLogger.LogD("try to get " + urlPath + " param[");
		if (map != null) {
			Iterator<String> it = map.keySet().iterator();
			while (it.hasNext()) {
				String name = it.next();
				mLogger.LogD(name + " : " + map.get(name));
			}
		}
		mLogger.LogD("]");

		URL url = new URL(urlPath);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
		StringBuilder sb = new StringBuilder();

		for (Map.Entry<String, String> entry : map.entrySet()) {
			sb.append("--");
			sb.append(BOUNDARY);
			sb.append("\r\n");
			sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"\r\n\r\n");
			sb.append(entry.getValue());
			sb.append("\r\n");
		}
		imageContentToUpload(conn.getOutputStream(), file, sb.toString().getBytes());
		int statusCode = conn.getResponseCode();
		if (statusCode != 200) {
			throw new HttpException("statusCode = " + statusCode);
		}
		return conn;
	}

	private void imageContentToUpload(OutputStream output, File file, byte[] bs) throws IOException {
		OutputStream out = new DataOutputStream(output);
		out.write(bs);
		byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();// 定义最后数据分隔线
		StringBuilder sb = new StringBuilder();
		sb.append("--");
		sb.append(BOUNDARY);
		sb.append("\r\n");
		sb.append("Content-Disposition: form-data;name=\"picFile\";filename=\"" + file.getName()
				+ "\"\r\n");
		sb.append("Content-Type:application/octet-stream\r\n\r\n");
		byte[] data = sb.toString().getBytes();
		out.write(data);
		DataInputStream in = new DataInputStream(new FileInputStream(file));
		int bytes = 0;
		byte[] bufferOut = new byte[1024];
		while ((bytes = in.read(bufferOut)) != -1) {
			out.write(bufferOut, 0, bytes);
		}
		out.write("\r\n".getBytes()); // 多个文件时，二个文件之间加入这个
		in.close();
		out.write(end_data);
		out.flush();
		out.close();
	}
}
