package fragment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yan.today.R;

import activity.ArticleActivity;
import activity.MainActivity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import entity.Data;
import tool.GetJson;
import tool.MyAdapter;
import tool.MySQLiteOpenHelper;

public class FragmentMain extends Fragment {

	MainActivity activity;
	AlphaAnimation anm;
	View view;
	boolean imgReadied;
	boolean TRUE = true;
	MyAdapter adapter;
	// Button post;
	ImageView thumbnail_pic_s;
	ListView listView;
	String jsonData, type;
	GetJson gj;
	Bitmap noPicture;
	Bitmap[] bmp;
	Data[] data;
	Date curDate;
	int num, i;
	SwipeRefreshLayout swipe;
	MySQLiteOpenHelper dbHelper;
	SQLiteDatabase db;
	Cursor cursor;
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				// 设置新闻标题列表
				for (i = 0; i < num; i++) {
					Date date = StringToDate(data[i].getDate());
					StringBuilder minutes = new StringBuilder();
					if (date.getMinutes() < 10)
						minutes.append("0");
					minutes.append(date.getMinutes());
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("title", data[i].getTitle());
					if(!String.valueOf(date.getDay()).equals(String.valueOf(curDate.getDay())))
						map.put("date","昨天 "+ date.getHours() + ":" + minutes);
					else
						map.put("date",date.getHours() + ":" + minutes);
					if (bmp[i] != null)
						map.put("img", bmp[i]);
					else
						map.put("img", noPicture);
					list.add(map);
				}
				adapter = new MyAdapter(getActivity(), R.layout.list_item, R.id.img, list, db, data);
				listView.setAdapter(adapter);

				listView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						try {
							db.execSQL("insert into userData (readedTitle) values(?)",
									new String[] { data[position].getTitle() });
						} catch (Exception e) {
						}
						adapter.setIsReaded(position);
						// adapter.notifyDataSetChanged();
						Intent intent = new Intent(getActivity(), ArticleActivity.class);
						intent.putExtra("url", data[position].getUrl());
						startActivity(intent);
					}
				});

				for (i = 0; i < num; i++) {
					saveBitmap(bmp[i], data[i].getTitle());
				}

				swipe.setRefreshing(false);
			}
		};
	};



	@Override
	public void onAttach(Activity activity) {
		this.activity = (MainActivity) activity;
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle=getArguments();
		type=bundle.getString("id");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_main, null);
		listView = (ListView) v.findViewById(R.id.listView);
		swipe = (SwipeRefreshLayout) v.findViewById(R.id.swipe);
		//swipe.setColorScheme(android.R.color.holo_green_dark, android.R.color.holo_green_light,
		//		android.R.color.holo_orange_light, android.R.color.holo_red_light);

		Drawable d = getResources().getDrawable(R.drawable.nopicture);
		BitmapDrawable bd = (BitmapDrawable) d;
		noPicture = bd.getBitmap();
		swipe.post(new Runnable() {

			@Override
			public void run() {
				swipe.setRefreshing(true);
			}
		});
		getNews();

		dbHelper = new MySQLiteOpenHelper(getActivity(), "today.db", null, 1);
		db = dbHelper.getWritableDatabase();

		// 下拉刷新
		swipe.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				getNews();
			}
		});

		return v;
	}

	public Date StringToDate(String dt) {
		DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date = new Date();
		try {
			// Log.d("StringToDate", dt);
			if(dt!=null)
				date = fmt.parse(dt);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	private void getImage(String PhotoUrl, int n) {
		try {
			Cursor cursor = db.rawQuery("select * from picture where title = ?", new String[] { data[n].getTitle() });
			if (cursor.moveToFirst()) {
				byte[] image = cursor.getBlob(cursor.getColumnIndex("img"));
				if (image != null) {
					ByteArrayInputStream bais = new ByteArrayInputStream(image);
					bmp[n] = BitmapFactory.decodeStream(bais);
				}
			} else {
				getNetImage(PhotoUrl, n);
			}
		} catch (Exception e) {
		}
	}

	private void getNetImage(String PhotoUrl, int n) {
		try {
			URL url = new URL(PhotoUrl);
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setRequestMethod("GET");
			bmp[n] = BitmapFactory.decodeStream(httpConn.getInputStream());
			httpConn.disconnect();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private void saveBitmap(Bitmap bitmap, String title) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.JPEG, 50, baos);
			db.execSQL("insert into picture (title,img) values (?,?)", new Object[] { title, baos.toByteArray() });
			baos.close();
		} catch (Exception e) {
		}
	}

	private void getNews() {
		curDate = new Date(System.currentTimeMillis());
		gj = new GetJson(type);
		new Thread() {
			public void run() {
				try {
					while (!gj.isFinish) {

					}
					data = gj.getData();
					num = data.length;
					bmp = new Bitmap[num];
					for (i = 0; i < num; i++)
						getImage(data[i].getThumbnail_pic_s(), i);
					Message msg = new Message();
					msg.what = 1;
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	@Override
	public void onDestroy() {
		db.close();
		super.onDestroy();
	}
}
