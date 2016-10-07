package tool;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import com.google.gson.Gson;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.util.Log;
import entity.Data;
import entity.JsonLable;
import entity.Result;

public class GetJson {
	String jsonData;
	Data[] data;
	String t;
	public boolean isFinish;
	
	@SuppressLint("HandlerLeak")
	Handler handler=new Handler(){
		@SuppressWarnings("unchecked")
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			data =parseJSONWithGSON(jsonData);
			//Arrays.sort(data,new MyComparator());
			isFinish=true;
		};
	};
	
	public GetJson(String type){
		//Log.e("GetJson", type);
		t=type;
		data=new Data[50];
		new Thread() {
			public void run() {
				try {
					String uri="http://v.juhe.cn/toutiao/index?type="+t+"&key=e46f9dc7aaf63e52991b8f0c9b3b789a";
					jsonData = readParse(uri);
					//Log.e("GetJson", uri);
					handler.sendEmptyMessage(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	public static String readParse(String urlPath) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		int len = 0;
		URL url = new URL(urlPath);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		InputStream inStream = conn.getInputStream();
		while ((len = inStream.read(data)) != -1) {
			outStream.write(data, 0, len);
		}
		inStream.close();
		return new String(outStream.toByteArray());// 通过out.Stream.toByteArray获取到写的数据
	}

	private Data[] parseJSONWithGSON(String jsonData) {
		Gson gson = new Gson();
		JsonLable lableList = gson.fromJson(jsonData, JsonLable.class);
		Result result = lableList.getResult();
		return result.getData();
	}
	
	@SuppressLint("SimpleDateFormat")
	public Date StringToDate(String dt){
		DateFormat fmt =new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date = null;
		try {
			date = fmt.parse(dt);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	@SuppressWarnings("rawtypes")
	class MyComparator implements Comparator{

		@Override
		public int compare(Object data1, Object data2) {
			if(StringToDate(((Data)data1).getDate()).after(StringToDate(((Data)data2).getDate())))
				return -1;
			else if(StringToDate(((Data)data1).getDate()).before(StringToDate(((Data)data2).getDate())))
				return 1;
			else
				return 0;
		}
		
	}
	
	public String getJsonData() {
		return jsonData;
	}

	public Data[] getData() {
		return data;
	}
}
