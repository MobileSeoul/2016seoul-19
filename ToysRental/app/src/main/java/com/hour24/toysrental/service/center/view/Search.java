package com.hour24.toysrental.service.center.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.hour24.toysrental.R;
import com.hour24.toysrental.common.Constants;
import com.hour24.toysrental.common.Utils;
import com.hour24.toysrental.common.view.floatingbutton.FloatingActionButton;
import com.hour24.toysrental.common.view.floatingbutton.FloatingActionsMenu;
import com.hour24.toysrental.service.center.model.MCenter;


public class Search extends Activity implements View.OnClickListener {

    private Context context;

    private Button btClose;
    private Button btSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_exit);
        setContentView(R.layout.center_search);
        context = Search.this;

        initLayout();

    }


    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.slide_exit, R.anim.slide_in_up);
    }

    /**
     * 레이아웃 초기화
     */
    private void initLayout() {

        btClose = (Button) findViewById(R.id.btClose);
        btSearch = (Button) findViewById(R.id.btSearch);

        btClose.setOnClickListener(this);
        btSearch.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        Intent intent = null;

        switch (v.getId()) {
            case R.id.btClose:
                setResult(RESULT_CANCELED);
                finish();
                break;

            case R.id.btSearch:
                setResult(RESULT_OK);
                finish();
                break;

            case R.id.fabSearch:
                setResult(RESULT_OK);
                finish();
                break;
        }
    }
}