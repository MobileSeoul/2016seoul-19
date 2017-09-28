package com.hour24.toysrental.common;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.hour24.toysrental.R;

/**
 * Created by 장세진 on 2016-08-16.
 */
public class BackPressClose {
    private long backKeyPressedTime = 0;
    private Toast toast;

    private Activity activity;

    public BackPressClose(Activity activity) {
        this.activity = activity;
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            activity.finish();
            toast.cancel();
        }
    }

    public void showGuide() {
        toast = Toast.makeText(activity,
                activity.getString(R.string.toast_back_close), Toast.LENGTH_SHORT);
        toast.show();
    }
}
