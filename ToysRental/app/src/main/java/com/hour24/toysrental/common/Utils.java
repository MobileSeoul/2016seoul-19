package com.hour24.toysrental.common;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.widget.ProgressBar;
import android.view.ViewGroup.LayoutParams;

import com.hour24.toysrental.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by 장세진 on 2016-08-03.
 */
public class Utils {

    public static Dialog progressDialog = null;

    /**
     * AES 암호화 키
     */
    private static final String ENCRYPTION_SECRET_KEY = "0001234567!!!!!!";

    /**
     * 암호화 알고리즘
     */
    private static final String ENCRYPTION_ALGORITHM = "AES";

    /*
    * Progress Dialog Show
    * */
    public static void progressDialogShow(Context context) {
        progressDialog = new Dialog(context, R.style.DialogTransparent);
        progressDialog.addContentView(new ProgressBar(context), new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
//        progressDialog.setCancelable(cancelAble);
//        progressDialog.setCanceledOnTouchOutside(cancelOut);
        progressDialog.show();
    }

    /*
    * Progress Dialog Dismiss
    * */
    public static void progressDialogDismiss() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


    /**
     * now 형식 date
     */
    public static String NowtoString(String format) {
        try {

            SimpleDateFormat simpleFormat = new SimpleDateFormat(format);
            return simpleFormat.format(Calendar.getInstance().getTime());

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    /**
     * String 를 Date로 변환
     */
    public static String StringToDate(String textDate, String format) {
        try {

            SimpleDateFormat simpleFormat = new SimpleDateFormat(format);
            return simpleFormat.parse(textDate).toString();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }


    /**
     * String oldFomat에서 newFormat으로 변경
     */
    public static String StringNewDateFormat(String textDate, String oldFormat, String newFormat) {
        try {

            SimpleDateFormat oldDateFormat = new SimpleDateFormat(oldFormat);
            Date oldDate = oldDateFormat.parse(textDate);

            SimpleDateFormat newDateFormat = new SimpleDateFormat(newFormat);
            return newDateFormat.format(oldDate);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Preference String
     */
    public static void setPreferences(Context context, String key, String value) {
        SharedPreferences prefs = context.getSharedPreferences("SharedPreferences", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * Preference get
     */
    public static String getPreferences(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences("SharedPreferences", context.MODE_PRIVATE);
        return prefs.getString(key, null);
    }

    /**
     * Preference get
     */
    public static void removePreferences(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences("SharedPreferences", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
    }

    /**
     * get Mime=type
     */
    public static String getMimeType(String fileName) {
        String fileExtend = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
        fileExtend = fileExtend.toUpperCase();

        String mimeType = "";
        // 파일 확장자 별로 mime type 지정
        if (fileExtend.equals("MP3")) {
            mimeType = "audio/*";
        } else if (fileExtend.equals("MP4")) {
            mimeType = "vidio/*";
        } else if (fileExtend.equals("JPG") || fileExtend.equals("JPEG")
                || fileExtend.equals("GIF")
                || fileExtend.equals("PNG") || fileExtend.equals("BMP")) {
            mimeType = "image/*";
        } else if (fileExtend.equals("TXT")) {
            mimeType = "text/*";
        } else if (fileExtend.equals("DOC") || fileExtend.equals("DOCX")) {
            mimeType = "application/msword";
        } else if (fileExtend.equals("XLS") || fileExtend.equals("XLSX")) {
            mimeType = "application/vnd.ms-excel";
        } else if (fileExtend.equals("PPT") || fileExtend.equals("PPTX")) {
            mimeType = "application/vnd.ms-powerpoint";
        } else if (fileExtend.equals("PDF")) {
            mimeType = "application/pdf";
        } else if (fileExtend.equals("HWP")) {
            mimeType = "application/x-hwp";
        } else {
            mimeType = "";
        }
        return mimeType;
    }

    /**
     * Check permission
     */
    public static boolean checkPermissionsStorage(Activity activity) {

        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    Constants.CODE.REQUEST_PERMISSION_STORAGE
            );

            return false;
        } else {
            return true;
        }
    }
}
