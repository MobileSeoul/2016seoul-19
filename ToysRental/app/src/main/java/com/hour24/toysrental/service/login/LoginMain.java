package com.hour24.toysrental.service.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hour24.toysrental.R;
import com.hour24.toysrental.common.Constants;

import java.security.MessageDigest;

/**
 * Created by 장세진 on 2016-08-09.
 */
public class LoginMain extends Activity implements View.OnClickListener {

    private Context context;

    private RelativeLayout rlGoogle;
    private RelativeLayout rlNaver;
    private RelativeLayout rlFacebook;
    private Button btClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_exit);
        setContentView(R.layout.login_main);
        context = LoginMain.this;

        rlGoogle = (RelativeLayout) findViewById(R.id.rlGoogle);
        rlNaver = (RelativeLayout) findViewById(R.id.rlNaver);
        rlFacebook = (RelativeLayout) findViewById(R.id.rlFacebook);
        btClose = (Button) findViewById(R.id.btClose);

        rlGoogle.setOnClickListener(this);
        rlNaver.setOnClickListener(this);
        rlFacebook.setOnClickListener(this);
        btClose.setOnClickListener(this);

        // getAppKeyHash();
    }

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.slide_exit, R.anim.slide_in_up);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            if (resultCode == RESULT_OK) {

                String memberId = data.getStringExtra("memberId");
                String memberName = data.getStringExtra("memberName");
//                Log.e("sjjang", "memberId : " + memberId);
//                Log.e("sjjang", "memberName : " + memberName);

                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("memberId", memberId);
                intent.putExtra("memberName", memberName);
                intent.putExtra("loginTypeCd", String.valueOf(requestCode));
                setResult(RESULT_OK, intent);
                finish();

            } else {
                Toast.makeText(context, getString(R.string.toast_login_fail), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, getString(R.string.toast_login_fail), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btClose:
                finish();
                break;

            case R.id.rlGoogle:
                intent = new Intent(context, GoogleLogin.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(intent, Constants.CODE.LOGIN_GOOGLE);
                break;

            case R.id.rlNaver:
                intent = new Intent(context, NaverLogin.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(intent, Constants.CODE.LOGIN_NAVER);
                break;

            case R.id.rlFacebook:
                intent = new Intent(context, FacebookLogin.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(intent, Constants.CODE.LOGIN_FACEBOOOK);
                break;
        }
    }

    /**
     * HashKey 생성
     */
    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.d("sjjang", something);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("sjjang", e.toString());
        }
    }
}
