package com.hour24.toysrental.service.center.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hour24.toysrental.R;
import com.hour24.toysrental.common.Constants;
import com.hour24.toysrental.common.Utils;
import com.hour24.toysrental.common.view.floatingbutton.FloatingActionButton;
import com.hour24.toysrental.common.view.floatingbutton.FloatingActionsMenu;
import com.hour24.toysrental.service.center.model.MCenter;


public class CenterDetail extends AppCompatActivity implements View.OnClickListener {

    private Context context;

    private WebView webView;
    private FloatingActionsMenu fabGroup;
    private FloatingActionButton fabCall;
    private FloatingActionButton fabNavi;
    private FloatingActionButton fabHomePage;

    private MCenter center;

    private String centerSeq;
    private String pageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_exit);
        setContentView(R.layout.center_detail);
        context = CenterDetail.this;

        Intent intent = getIntent();
        center = (MCenter) intent.getSerializableExtra("center");
        centerSeq = center.getCenterSeq();

        String centerName = center.getCenterName();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(centerName);

        initLayout();

    }

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.slide_exit, R.anim.slide_in_left);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 레이아웃 초기화
     */
    private void initLayout() {

        fabGroup = (FloatingActionsMenu) findViewById(R.id.fabGroup);
        fabCall = (FloatingActionButton) findViewById(R.id.fabCall);
        fabNavi = (FloatingActionButton) findViewById(R.id.fabNavi);
        fabHomePage = (FloatingActionButton) findViewById(R.id.fabHomePage);

        fabCall.setOnClickListener(this);
        fabNavi.setOnClickListener(this);
        fabHomePage.setOnClickListener(this);

        webView = (WebView) findViewById(R.id.webView);
        webView.setVerticalScrollBarEnabled(true);
        webView.setHorizontalScrollBarEnabled(false);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);

        pageUrl = Constants.HOST_URL + "/toy/views/jsp/" + centerSeq + ".jsp";
        webView.loadUrl(pageUrl);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String loadUrl) {
                view.loadUrl(loadUrl);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String loadUrl, Bitmap favicon) {
                Utils.progressDialogShow(context);
            }

            @Override
            public void onPageFinished(WebView view, final String loadUrl) {
                Utils.progressDialogDismiss();
            }
        });

        // 장난감 검색 url이 있을 경우 "홈페이지/장난감검색"
        String url = center.getUrl();
        String toySearchUrl = center.getUrlToySearch();

        if ("null".equals(toySearchUrl)) {
            // 일반 홈페이지 바로 연결
            fabHomePage.setTitle("홈페이지");
        } else {
            fabHomePage.setTitle("홈페이지 / 장난감검색");
        }
    }

    @Override
    public void onClick(View v) {

        Intent intent = null;

        switch (v.getId()) {
            case R.id.fabCall:
                // 전화하기
                String number = center.getTel();
                intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
                startActivity(intent);
                // webView.loadUrl(pageUrl);
                break;

            case R.id.fabNavi:
                // 길찾기
                String latitude = center.getLatitude();
                String longitude = center.getLongitude();
                Log.e("sjjang", "latitude : " + latitude);
                Log.e("sjjang", "longitude : " + longitude);

                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + ", " + longitude);
                intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
                break;

            case R.id.fabHomePage:
                // 홈페이지
                // 장난감 검색이 있을 경우 다이어로그를 하나 더 띄어서 선택하게 한다.
                String url = center.getUrl();
                String toySearchUrl = center.getUrlToySearch();

                if ("null".equals(toySearchUrl)) {
                    // 일반 홈페이지 바로 연결
                    Uri uri = Uri.parse(url);
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                } else {
                    intent = new Intent(context, HomePageSelect.class);
                    intent.putExtra("center", center);
                }
                startActivity(intent);
                break;
        }
    }
}