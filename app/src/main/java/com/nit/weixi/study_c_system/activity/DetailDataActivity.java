package com.nit.weixi.study_c_system.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.text.TextUtilsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nit.weixi.study_c_system.R;
import com.nit.weixi.study_c_system.tools.DownUtils;
import com.nit.weixi.study_c_system.tools.Tool;

import java.io.File;
import java.io.IOException;

import cz.msebera.android.httpclient.Header;


/**
 * 查看NewsFeeds or 文章详情
 *
 * @author jayin
 */
@SuppressLint("SetJavaScriptEnabled")
public class DetailDataActivity extends MyBackActivity
        implements SwipeRefreshLayout.OnRefreshListener {
    public static final String EXTRA_LINK = "link";
    public static final String EXTRA_TITLE = "title";

    Toolbar mToolbar;

    SwipeRefreshLayout swipeRefreshLayout;
    WebView webview;
    String link;// 接受2种URL,一种是url,另一种文件路径path
    String title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acty_detail);
        swipeRefreshLayout= (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        webview= (WebView) findViewById(R.id.webview);
        initData();
        initLayout();
    }

    protected void initData() {

        link = getIntent().getStringExtra(EXTRA_LINK);
        title = getIntent().getStringExtra(EXTRA_TITLE);

        if (link == null || title == null ) {
            finish();
        }
    }

    protected void initLayout() {
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_blue_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_blue_light);

        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setAppCacheEnabled(true);
        webview.getSettings().setDatabaseEnabled(true);
        webview.getSettings().setGeolocationEnabled(true);
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        webview.setWebChromeClient(new MyWebChromeClient());
        webview.setWebViewClient(new MyWebViewClient());

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setBackActionBar(title,mToolbar); // 设置返回ActionBar相关
        onRefresh();

    }

    /**
     * 刷新前
     */
    private void onPreRefresh() {
        Tool.setRefreshing(swipeRefreshLayout, true);
    }

    // 如果是网络链接就去加载刷新
    @Override public void onRefresh() {
        if (DownUtils.isURL(link)) {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(link, new AsyncHttpResponseHandler() {
                @Override public void onStart() {
                    onPreRefresh();
                }

                @Override public void onSuccess(int statusCode,
                                                Header[] headers, byte[] data) {
                    load(new String(data));
                }

                @Override public void onFailure(int statusCode,
                                                Header[] headers, byte[] data, Throwable arg3) {
                    try {
                        load(DownUtils.readFile(getAssets().open("404.md")));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override public void onFinish() {
                    onPostRefresh();
                }

            });
        } else {
            // read file
            new ReadFileTask(link).execute();
        }

    }

    /**
     * 刷新完毕
     */
    private void onPostRefresh() {
        Tool.setRefreshing(swipeRefreshLayout, false);
//        displayMenu(mMenu);
    }

    private void load(String content) {
        try {
            String tpl = DownUtils.readFile(getAssets().open("preview.html"));
            webview.loadDataWithBaseURL("about:blank",
                    tpl.replace("{markdown}", TextUtilsCompat.htmlEncode(content)), "text/html", "UTF-8",
                    null);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    class MyWebChromeClient extends WebChromeClient {
        @Override public void onProgressChanged(WebView view, int newProgress) {
        }

        @Override public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            return true;
        }
    }

    class MyWebViewClient extends WebViewClient {

        @Override public void onLoadResource(WebView view, String url) {
        }

        @Override public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override public void onPageStarted(WebView view, String url,
                                            Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override public boolean shouldOverrideKeyEvent(WebView view,
                                                        KeyEvent event) {
            return super.shouldOverrideKeyEvent(view, event);
        }

        @Override public boolean shouldOverrideUrlLoading(WebView view,
                                                          String url) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            return true;
        }

        @Override public void onReceivedError(WebView view, int errorCode,
                                              String description, String failingUrl) {
            // toast("Error Code--->"+errorCode+"   failingUrl--> "+failingUrl);
            // view.loadUrl("file:///android_asset/404.html");
        }
    }

    class ReadFileTask extends AsyncTask<Void, Void, String> {
        private String path;

        public ReadFileTask(String path) {
            this.path = path;
        }

        @Override protected void onPreExecute() {
            onPreRefresh();
        }

        @Override protected String doInBackground(Void... params) {
            if(new File(this.path).exists()){
                return DownUtils.readFile(this.path);
            }else{
                try {
                    return DownUtils.readFile(getAssets().open("404.md"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override protected void onPostExecute(String result) {
            load(result);
            onPostRefresh();
        }

    }

}
