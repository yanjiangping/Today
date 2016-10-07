package activity;

import java.util.ArrayList;

import com.yan.today.R;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import entity.Lable;
import fragment.FragmentMain;
import android.widget.FrameLayout.LayoutParams;
import android.widget.HorizontalScrollView;
import tool.BaseTools;
import tool.NewsFragmentPagerAdapter;
import view.ColumnHorizontalScrollView;

public class MainActivity extends FragmentActivity{

	private LinearLayout lableGroup;
	private TextView lable;
	private int lableCount;
	private ArrayList<Lable> list;
	private int screenWitch,lableWitch;
	private int lableSelect;
	private ViewPager mViewPager;
	private ColumnHorizontalScrollView horizontalScrollView;
	private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
	/** ????CODE */
	public final static int CHANNELREQUEST = 1;
	/** ?????????RESULTCODE */
	public final static int CHANNELRESULT = 10;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		horizontalScrollView=(ColumnHorizontalScrollView)findViewById(R.id.mColumnHorizontalScrollView);
		lableGroup=(LinearLayout)findViewById(R.id.lable_group);
		mViewPager=(ViewPager)findViewById(R.id.mViewPager);
		list=new ArrayList<Lable>();
		screenWitch=BaseTools.getWindowsWidth(this);
		lableWitch=screenWitch/7;
		lableSelect=0;
		initView();
	}
	
	/**
	 * ????????????????
	 */
	private void getLableData(){
		list.add(new Lable("top","头条"));
		list.add(new Lable("keji","科技"));
		list.add(new Lable("shehui","社会"));
		list.add(new Lable("guonei","国内"));
		list.add(new Lable("guoji","国际"));
		list.add(new Lable("yule","娱乐"));
		list.add(new Lable("tiyu","体育"));
		list.add(new Lable("junshi","军事"));
	}
	
	/**
	 * ???????????,????fragment
	 */
	private void initView(){
		getLableData();
		lableCount=list.size();
		horizontalScrollView.setParam(this, screenWitch, lableGroup);
		lableGroup.removeAllViews();
		for(int i=0;i<lableCount;i++){
			LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(lableWitch, LayoutParams.WRAP_CONTENT);
			params.leftMargin=5;
			lable=new TextView(this);
			lable.setBackgroundResource(R.drawable.radio_buttong_bg);
			lable.setGravity(Gravity.CENTER);
			lable.setPadding(5, 5, 5, 5);
			lable.setId(i);
			lable.setText(list.get(i).getLableName());
			if(lableSelect == i){
				lable.setSelected(true);
			}
			lable.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//  Log.e("aa", lable.getText().toString());
					 // type=lable.getText().toString();
		        	 // replaceFragment();
			          for(int i = 0;i < lableGroup.getChildCount();i++){
				          View localView = lableGroup.getChildAt(i);
				          if (localView != v){
				        	  localView.setSelected(false);
				          }
				          else{
				        	  localView.setSelected(true);
				        	  mViewPager.setCurrentItem(i);
				          }
			          }
				}
			});
			lableGroup.addView(lable, i ,params);
		}
		initFragment();
	}
	
	/**
	 * ?��?fragment
	 */
	/*private void replaceFragment(){
		FragmentMain frm=new FragmentMain();
		FragmentTransaction transaction=getFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, frm);
		transaction.commit();
	}*/
	/** 
	 *  ViewPager?��?????????
	 * */
	public OnPageChangeListener pageListener= new OnPageChangeListener(){

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int position) {
			mViewPager.setCurrentItem(position);
			selectTab(position);
		}
	};
	
	/** 
	 *  ????Column?????Tab
	 * */
	private void selectTab(int tab_postion) {
		lableSelect = tab_postion;
		for (int i = 0; i < lableGroup.getChildCount(); i++) {
			View checkView = lableGroup.getChildAt(tab_postion);
			int k = checkView.getMeasuredWidth();
			int l = checkView.getLeft();
			int i2 = l + k / 2 - screenWitch / 2;
			// rg_nav_content.getParent()).smoothScrollTo(i2, 0);
			horizontalScrollView.smoothScrollTo(i2, 0);
			// mColumnHorizontalScrollView.smoothScrollTo((position - 2) *
			// mItemWidth , 0);
		}
		//?��???????
		for (int j = 0; j <  lableGroup.getChildCount(); j++) {
			View checkView = lableGroup.getChildAt(j);
			boolean ischeck;
			if (j == tab_postion) {
				ischeck = true;
			} else {
				ischeck = false;
			}
			checkView.setSelected(ischeck);
		}
	}
	
	/** 
	 *  ?????Fragment
	 * */
	private void initFragment() {
		fragments.clear();//???
		int count =  list.size();
		for(int i = 0; i< count;i++){
    		FragmentMain newfragment = new FragmentMain();
			Bundle bundle=new Bundle();
			bundle.putString("id",list.get(i).getLableId());
			newfragment.setArguments(bundle);
			fragments.add(newfragment);
		}
		NewsFragmentPagerAdapter mAdapetr = new NewsFragmentPagerAdapter(getSupportFragmentManager(), fragments);
//		mViewPager.setOffscreenPageLimit(0);
		mViewPager.setAdapter(mAdapetr);
		mViewPager.setOnPageChangeListener(pageListener);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case CHANNELREQUEST:
			if(resultCode == CHANNELRESULT){
				initView();
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
}

