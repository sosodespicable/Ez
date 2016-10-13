package com.lxj.sample.letsplay.Activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.lxj.sample.letsplay.DatabaseHelper;
import com.lxj.sample.letsplay.NetWorkState;
import com.lxj.sample.letsplay.R;
import com.lxj.sample.letsplay.ZhiHu.ZhiHu_API;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/8/6 0006.
 */
public class ZhiHuReadActivity extends AppCompatActivity {

    private WebView webView;
    private FloatingActionButton fab;
    private ImageView head_img;
    private TextView head_title;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;

    private RequestQueue queue;

    private int likes = 0;
    private int comments = 0;

    private AlertDialog dialog;

    private String shareUrl = null;
    private String id;

    private SharedPreferences sp;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhihu_read);
        initViews();

        sp = getSharedPreferences("user_settings",MODE_PRIVATE);

        dialog = new AlertDialog.Builder(ZhiHuReadActivity.this).create();
        dialog.setView(getLayoutInflater().inflate(R.layout.loading_layout,null));
        dialog.show();

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        final String title = intent.getStringExtra("title");
        final String image = intent.getStringExtra("image");

        setCollapsingToolbarLayoutTitle(title);

        queue = Volley.newRequestQueue(getApplicationContext());

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAppCacheEnabled(false);

        //不需要调用第三方浏览器
        if (sp.getBoolean("in_app_brower",false)){
            webView.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    webView.loadUrl(url);
                    return true;
                }
            });
            //设置通过按下返回返回上一个页面
            webView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN){
                        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()){
                            webView.goBack();
                            return true;
                        }
                    }
                    return false;
                }
            });
        }

        //设置是否加载图片
        webView.getSettings().setBlockNetworkImage(sp.getBoolean("no_picture_mode",false));

        //如果当前没有网络，就加载缓存中的内容
        if (!NetWorkState.networkConnected(ZhiHuReadActivity.this)){
            head_img.setImageResource(R.drawable.no_img);
            head_img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            String parseByTheme = "<body>\n";

            if (loadFromDB(id) == null || loadFromDB(id).isEmpty()){
                Snackbar.make(fab,"发生了一些错误",Snackbar.LENGTH_SHORT).show();
            } else {
                String css = "<link rel=\"stylesheet\" href=\"file:///android_asset/zhihu_master.css\" type=\"text/css\">";
                String html = "<!DOCTYPE html>\n"
                        + "<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\">\n"
                        + "<head>\n"
                        + "\t<meta charset=\"utf-8\" />"
                        + css
                        + "\n</head>\n"
                        + parseByTheme
                        + loadFromDB(id).replace("<div class=\"img-place-holder\">", "")
                        + "</body></html>";
                webView.loadDataWithBaseURL("x-data://base",html,"text/html","utf-8",null);
            }
            if (dialog.isShowing()){
                dialog.dismiss();
            }
        } else{
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, ZhiHu_API.NEWS + id, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        // 需要注意的是这里有可能没有body。。。 好多坑。。。
                        // 如果没有body，则加载share_url中内容
                        if (jsonObject.isNull("body")) {
                            webView.loadUrl(jsonObject.getString("share_url"));
                            head_img.setImageResource(R.drawable.no_img);
                            head_img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            //body为null的情况下,share_url还是存在的
                            shareUrl = jsonObject.getString("share_url");
                        } else {
                            shareUrl = jsonObject.getString("share_url");

                            if (!jsonObject.isNull("image")) {
                                Glide.with(ZhiHuReadActivity.this)
                                        .load(jsonObject.getString("image"))
                                        .centerCrop()
                                        .into(head_img);
                                head_title.setText(jsonObject.getString("image_source"));
                            } else if (image == null) {
                                head_img.setImageResource(R.drawable.no_img);
                                head_img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            } else {
                                Glide.with(ZhiHuReadActivity.this)
                                        .load(image)
                                        .centerCrop()
                                        .into(head_img);
                            }

                            String css = "<link rel=\"stylesheet\" href=\"file:///android_asset/zhihu_master.css\" type=\"text/css\">";
                            String content = jsonObject.getString("body").replace("<div class=\"img-place-holder\">", "");
                            content = content.replace("<div class=\"headline\">", "");
                            String parseByTheme = "<body>\n";
                            String html = "<!DOCTYPE html>\n"
                                    + "<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\">\n"
                                    + "<head>\n"
                                    + "\t<meta charset=\"utf-8\" />"
                                    + css
                                    + "\n</head>\n"
                                    + parseByTheme
                                    + content
                                    + "</body></html>";
                            webView.loadDataWithBaseURL("x-data://base",html,"text/html","utf-8",null);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (dialog.isShowing()){
                        dialog.dismiss();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Snackbar.make(fab,"发生了一些错误",Snackbar.LENGTH_SHORT).show();
                    if (dialog.isShowing()){
                        dialog.dismiss();
                    }
                }
            });
            queue.add(request);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent shareIntent = new Intent().setAction(Intent.ACTION_SEND).setType("text/plain");
                    String shareText = title + " " + shareUrl + "分享自Letsplay";
                    shareIntent.putExtra(Intent.EXTRA_TEXT,shareText);
                    startActivity(Intent.createChooser(shareIntent,"分享至"));
                } catch (ActivityNotFoundException e){
                    Snackbar.make(fab,"发生了一些错误",Snackbar.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void initViews(){
        webView = (WebView) findViewById(R.id.zhihu_read_webview);
        webView.setScrollbarFadingEnabled(true);

        fab = (FloatingActionButton) findViewById(R.id.zhihu_read_fab);
        toolbar = (Toolbar) findViewById(R.id.zhihu_read_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        head_img = (ImageView) findViewById(R.id.zhihu_read_head_img);
        head_title = (TextView) findViewById(R.id.zhihu_read_head_title);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.zhihu_read_toolbar_layout);
    }

    private String loadFromDB(String id){
        String content = null;
        DatabaseHelper databaseHelper = new DatabaseHelper(ZhiHuReadActivity.this,"History.db",null,3);
        db = databaseHelper.getWritableDatabase();
        Cursor cursor = db.query("Contents",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                if (cursor.getString(cursor.getColumnIndex("id")).equals(id)){
                    content = cursor.getString(cursor.getColumnIndex("content"));
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        return content;
    }

    private void setCollapsingToolbarLayoutTitle(String title){
        collapsingToolbarLayout.setTitle(title);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBarPlus1);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBarPlus1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
