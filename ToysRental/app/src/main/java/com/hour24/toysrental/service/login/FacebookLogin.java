package com.hour24.toysrental.service.login;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.hour24.toysrental.R;

import org.json.JSONObject;

import java.util.Arrays;


/**
 * Facebook Login
 */
public class FacebookLogin extends AppCompatActivity {

    private Context context;
    private Dialog dialog;

    private CallbackManager callbackManager;
    private FacebookCallback<LoginResult> facebookCallback;
    private LoginButton loginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.login_facebook);
        context = FacebookLogin.this;

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

        // 권한 획득
        LoginManager.getInstance().logInWithReadPermissions(this,
                Arrays.asList("email", "user_photos", "public_profile"));

        // LoginResult CallBack 클래스
        facebookCallback = new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request;
                request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {

                            @Override
                            public void onCompleted(JSONObject jsonObject,
                                                    GraphResponse response) {
                                loginPass(jsonObject);
                            }
                        });
                // 불러올 것들을 세팅한다.
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, email, gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.e("sjjang", "On cancel");
                loginFail();
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("sjjang", error.toString());
                loginFail();
            }
        };

        // facebook default button
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.registerCallback(callbackManager, facebookCallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 로그인 성공
     */
    private void loginPass(JSONObject jsonObject) {
        try {
            String email = jsonObject.getString("email");
            String name = jsonObject.getString("name");

            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.putExtra("memberName", name);
            intent.putExtra("memberId", email);
            setResult(RESULT_OK, intent);
            dialog.dismiss();

            LoginManager.getInstance().logOut();

            finish();
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

}
