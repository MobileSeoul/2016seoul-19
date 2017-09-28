package com.hour24.toysrental.service.timeline.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.hour24.toysrental.R;
import com.hour24.toysrental.common.Utils;


public class Info extends Activity implements View.OnClickListener {

    private Context context;

    private Button btClose;
    private Button btNever;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_exit);
        setContentView(R.layout.timeline_info);
        context = Info.this;

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
        btNever = (Button) findViewById(R.id.btNever);

        btClose.setOnClickListener(this);
        btNever.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        Intent intent = null;

        switch (v.getId()) {
            case R.id.btClose:
                finish();
                break;

            case R.id.btNever:
                Utils.setPreferences(context, "isInfoNever", "true");
                finish();
                break;
        }
    }
}