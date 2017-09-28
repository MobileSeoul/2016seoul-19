package com.hour24.toysrental.service.login;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;

import com.hour24.toysrental.R;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

import org.json.JSONObject;


/**
 * Kakao Login
 */
public class NaverLogin extends AppCompatActivity {

    private Context context;
    private Dialog dialog;

    /**
     * client 정보를 넣어준다.
     */
    private String OAUTH_CLIENT_ID;
    private String OAUTH_CLIENT_SECRET;
    private String OAUTH_CLIENT_NAME;

    private OAuthLogin mOAuthLoginModule;
    private OAuthLoginButton mOAuthLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_naver);
        context = NaverLogin.this;

        // dialog
        dialog = new Dialog(context, R.style.DialogTransparent);
        dialog.addContentView(new ProgressBar(context),
                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                loginFail();
            }
        });

        OAUTH_CLIENT_ID = getString(R.string.naver_client_id);
        OAUTH_CLIENT_SECRET = getString(R.string.naver_client_secret);
        OAUTH_CLIENT_NAME = getString(R.string.login_naver);

        mOAuthLoginModule = OAuthLogin.getInstance();
        mOAuthLoginModule.init(
                context
                , OAUTH_CLIENT_ID
                , OAUTH_CLIENT_SECRET
                , OAUTH_CLIENT_NAME
                //,OAUTH_CALLBACK_INTENT
                // SDK 4.1.4 버전부터는 OAUTH_CALLBACK_INTENT변수를 사용하지 않습니다.
        );

        mOAuthLoginButton = (OAuthLoginButton) findViewById(R.id.buttonOAuthLoginImg);
        mOAuthLoginButton.setOAuthLoginHandler(mOAuthLoginHandler);

        mOAuthLoginModule.startOauthLoginActivity(this, mOAuthLoginHandler);

    }

    /**
     * OAuthLoginHandler를 startOAuthLoginActivity() 메서드 호출 시 파라미터로 전달하거나 OAuthLoginButton
     * 객체에 등록하면 인증이 종료되는 것을 확인할 수 있습니다.
     */
    private OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
        @Override
        public void run(boolean success) {
            if (success) {
                loginPass();
            } else {
                String errorCode = mOAuthLoginModule.getLastErrorCode(context).getCode();
                String errorDesc = mOAuthLoginModule.getLastErrorDesc(context);
                Log.e("sjjang", "errorCode : " + errorCode + " / " + "errorDesc : " + errorDesc);
                loginFail();
            }
        }
    };

    /**
     * 로그인 성공
     */
    private void loginPass() {
        try {
            JSONObject result = new JSONObject(new RequestApiTask().execute().get());
            if ("00".equals(result.getString("resultcode"))) {
                JSONObject profile = new JSONObject(result.getString("response"));
                String name = profile.getString("nickname");
                String email = profile.getString("email");

                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("memberName", name);
                intent.putExtra("memberId", email);
                setResult(RESULT_OK, intent);
                dialog.dismiss();

                mOAuthLoginModule.logout(context);

                finish();
            } else {
                loginFail();
            }

            mOAuthLoginModule.logout(context);
        } catch (Exception e) {
            e.printStackTrace();
            loginFail();
        }
    }

    /**
     * 로그인 실패
     */
    private void loginFail() {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        setResult(RESULT_CANCELED);
        dialog.dismiss();
        finish();
    }

    /**
     * Api 요청
     */
    private class RequestApiTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... params) {
            String url = "https://openapi.naver.com/v1/nid/me";
            String at = mOAuthLoginModule.getAccessToken(context);
            return mOAuthLoginModule.requestApi(context, at, url);
        }

        protected void onPostExecute(String content) {
        }
    }

    /**
     * Tocken 삭제
     */
    private class DeleteTokenTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            boolean isSuccessDeleteToken = mOAuthLoginModule.logoutAndDeleteToken(context);

            if (!isSuccessDeleteToken) {
                // 서버에서 token 삭제에 실패했어도 클라이언트에 있는 token 은 삭제되어 로그아웃된 상태이다
                // 실패했어도 클라이언트 상에 token 정보가 없기 때문에 추가적으로 해줄 수 있는 것은 없음
                Log.d("sjjang", "errorCode:" + mOAuthLoginModule.getLastErrorCode(context));
                Log.d("sjjang", "errorDesc:" + mOAuthLoginModule.getLastErrorDesc(context));
            }

            return null;
        }

        protected void onPostExecute(Void v) {
        }
    }

}
