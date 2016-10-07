package view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;

public class MyWebView extends WebView {

	private Activity articleActivity;
	@SuppressLint("ClickableViewAccessibility")
	public MyWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		articleActivity=(Activity)context;
		//��������
		setOnTouchListener(new OnTouchListener() {
			private float startX, startY, endX, endY;
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()){
				case MotionEvent.ACTION_DOWN:
					startX=event.getX();
					startY=event.getY();
					break;
				case MotionEvent.ACTION_UP:
					endX=event.getX();
					endY=event.getY();
					//���һ���
					if (Math.abs(endY - startY)/Math.abs(endX - startX)<0.4) {
						//�һ�
						if((endX-startX)>60){
							articleActivity.onBackPressed();
						}
					}
					break;
				}
				return false;
			}
		});
	}

}
