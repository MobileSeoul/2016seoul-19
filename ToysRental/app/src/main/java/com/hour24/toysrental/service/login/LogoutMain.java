package com.hour24.toysrental.service.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hour24.toysrental.R;
import com.hour24.toysrental.common.AES256Util;
import com.hour24.toysrental.common.Utils;

/**
 * Created by 장세진 on 2016-08-09.
 */
public class LogoutMain extends Activity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_exit);
        setContentView(R.layout.logout_main);
        context = LogoutMain.this;

        findViewById(R.id.btClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        findViewById(R.id.btLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });

        try {
            // 회원정보 세팅
            String memberName = Utils.getPreferences(context, "memberName");
            String memberId = Utils.getPreferences(context, "memberId");

            TextView tvMemberName = (TextView) findViewById(R.id.tvMemberName);
            TextView tvMemberId = (TextView) findViewById(R.id.tvMemberId);

            AES256Util aes256 = new AES256Util(AES256Util.key);

            tvMemberName.setText(aes256.aesDecode(memberName));
            tvMemberId.setText(aes256.aesDecode(memberId));
        } catch (Exception e) {
            e.toString();
        }
    }

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.slide_exit, R.anim.slide_in_up);
    }

}
