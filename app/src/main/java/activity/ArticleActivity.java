package activity;

import com.yan.today.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebViewClient;
import view.MyWebView;

public class ArticleActivity extends Activity {

	MyWebView article;

	@SuppressLint("SetJavaScriptEnabled")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.article);
		article = (MyWebView) findViewById(R.id.article);

		Intent intent = getIntent();
		String url = intent.getStringExtra("url");

		article.getSettings().setJavaScriptEnabled(true);
		article.setWebViewClient(new WebViewClient());
		article.loadUrl(url);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && article.canGoBack()) {
			article.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onStop() {
		article.getSettings().setJavaScriptEnabled(false);//????????????js??????
		super.onStop();
	}
	
	
	@Override
	protected void onDestroy() {
		article.clearCache(true);   
		article.clearHistory();  
		System.exit(0);//
		super.onDestroy();
	}

}
