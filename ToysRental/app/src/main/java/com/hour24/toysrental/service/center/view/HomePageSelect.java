package com.hour24.toysrental.service.center.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.hour24.toysrental.R;
import com.hour24.toysrental.service.center.model.MCenter;

/**
 * Created by 장세진 on 2016-08-09.
 */
public class HomePageSelect extends Activity implements View.OnClickListener {

    private Context context;

    private LinearLayout llHomePage;
    private LinearLayout llToySearch;
    private Button btClose;

    private MCenter center;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_exit);
        setContentView(R.layout.center_hompage);
        context = HomePageSelect.this;

        Intent intent = getIntent();
        center = (MCenter) intent.getSerializableExtra("center");

        llHomePage = (LinearLayout) findViewById(R.id.llHomePage);
        llToySearch = (LinearLayout) findViewById(R.id.llToySearch);
        btClose = (Button) findViewById(R.id.btClose);

        llHomePage.setOnClickListener(this);
        llToySearch.setOnClickListener(this);
        btClose.setOnClickListener(this);

    }

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.slide_exit, R.anim.slide_in_up);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;

        switch (v.getId()) {
            case R.id.btClose:
                finish();
                break;

            case R.id.llHomePage:
                String url = center.getUrl();
                // 일반 홈페이지 바로 연결
                Uri uriHomePage = Uri.parse(url);
                intent = new Intent(Intent.ACTION_VIEW, uriHomePage);
                startActivity(intent);
                break;

            case R.id.llToySearch:
                String toySearchUrl = center.getUrlToySearch();
                // 일반 홈페이지 바로 연결
                Uri uriToySearch = Uri.parse(toySearchUrl);
                intent = new Intent(Intent.ACTION_VIEW, uriToySearch);
                startActivity(intent);
                break;
        }
    }
}
