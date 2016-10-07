package tool;

import java.util.List;
import java.util.Map;

import com.yan.today.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.preference.PreferenceManager.OnActivityDestroyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import entity.Data;

public class MyAdapter extends BaseAdapter  {

	private Context context;
	private int resource,imgId;
	private List<Map<String, Object>> list;
	private Map<String, Object> map;
	private Cursor cursor;
	private ViewHolder viewHolder;
	//private Data[] data;
	private boolean[] isReaded;
	
	public MyAdapter(Context c,int resourceId,int img,
			List<Map<String, Object>> l,SQLiteDatabase db,Data[] data){
		context=c;
		resource=resourceId;
		imgId=img;
		list=l;
		cursor=db.rawQuery("select * from userData", null);
		//this.isReaded=isReaded;
		//this.data=data;
			
		isReaded=new boolean[data.length];
		//this.isReaded[1]=true;
		//this.isReaded[3]=true;
		for(int i=0;i<data.length;i++){
			if(cursor.moveToFirst()){
				do{
					String title=cursor.getString(cursor.getColumnIndex("readedTitle"));
					//Log.e("key", key);
					if(title!=""&&title!=null&&title.equals(data[i].getTitle())){
						isReaded[i]=true;
						//Log.e("MyAdapter", ""+i);
						break;
					}
				}while(cursor.moveToNext());
			}
		}
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v;
		if(convertView==null){
			v=LayoutInflater.from(context).inflate(resource, null);
			viewHolder=new ViewHolder();
			viewHolder.img=(ImageView)v.findViewById(imgId);
			viewHolder.title=(TextView)v.findViewById(R.id.title);
			viewHolder.date=(TextView)v.findViewById(R.id.date);
			v.setTag(viewHolder);
		}
		else{
			v=convertView;
			viewHolder=(ViewHolder)v.getTag();
			//viewHolder.title.setVisibility(View.GONE);
			//resetViewHolder(viewHolder);
		}
		map=list.get(position);
		viewHolder.img.setImageBitmap((Bitmap) map.get("img"));
		if(isReaded[position]){
			viewHolder.title.setTextColor(Color.parseColor("#BFBFBF"));
			viewHolder.date.setTextColor(Color.parseColor("#BFBFBF"));
		}else{
			viewHolder.title.setTextColor(Color.parseColor("#000000"));
			viewHolder.date.setTextColor(Color.parseColor("#000000"));
		}
		viewHolder.title.setText(map.get("title").toString());
		viewHolder.date.setText(map.get("date").toString());
		return v;
	}
	
	public void setIsReaded(int position){
		isReaded[position]=true;
		this.notifyDataSetChanged(); 
	}
	
	class ViewHolder{
		ImageView img;
		TextView title;
		TextView date;
	}
	
}
