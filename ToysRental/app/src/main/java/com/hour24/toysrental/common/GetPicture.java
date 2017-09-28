package com.hour24.toysrental.common;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 장세진 on 2016-08-12.
 */
public class GetPicture {

    private static final String DIRECTORY_NAME = "ToysRental";
    private static final int IMAGE_MAX_WIDTH = 1028;
    private static final int IMAGE_MAX_HEIGHT = 720;

    private Context context;
    private Intent data;

    private Bitmap bitmap;

    public GetPicture(Context context, Intent data) {
        this.context = context;
        this.data = data;
    }

    public File onProc() {

        final String[] filePathColumn = {MediaStore.MediaColumns.DATA};
        Uri fileUri = data.getData();
//        Log.e("sjjang", "data : " + data);
//        Log.e("sjjang", "data.getData() : " + data.getData());

        Cursor cursor = context.getContentResolver().query(fileUri, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
//        Log.e("sjjang", "filePath : " + filePath);

        // 다른 앱을 통한 이미지가 http통신을 통해 다운로드 받는 것이 있다.
        // filePath를 통해 실제 파일이 있는지 확인을 한다.
        File tempFile = new File(filePath);
        if (tempFile.isFile()) {
            // 이미지 사이즈 처리
            bitmap = bitmapReSize(filePath);
        } else {
            bitmap = bitmapURLdownload(filePath);
        }
        cursor.close();

        if (bitmap == null) {
            return null;
        }

        bitmap = bitmapRotate(bitmap, tempFile);

        return bitmapToFile(bitmap);
    }

    /**
     * 파일 사이즈 변환
     *
     * @param bitmap
     * @return
     */
    private Bitmap bitmapReSize(String filePath) {
        // TODO Auto-generated method stub

        Bitmap bitmap = null;

        // bitmap을 불러올때 파일이 큰 경우 Out of Memory가 나올 수 있다.
        // 불러오기 전에 미리 사이즈를 측정해서 사이즈 변환을 한다.
        BitmapFactory.Options options1 = new BitmapFactory.Options();
        options1.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options1);

        int bitmapWidth = options1.outWidth;
        int btimapHeight = options1.outHeight;
        int inSampleSize = 1;

        // 최대 사이즈가 넘을 경우 아래와 같이 사이즈 처리를 실시 한다.
        if (bitmapWidth > IMAGE_MAX_WIDTH || btimapHeight > IMAGE_MAX_HEIGHT) {

            int widthRatio = Math.round((float) bitmapWidth / (float) IMAGE_MAX_WIDTH);
            int heightRatio = Math.round((float) btimapHeight / (float) IMAGE_MAX_HEIGHT);

            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        // 사이즈 처리하여 bitmap 반환
        BitmapFactory.Options options2 = new BitmapFactory.Options();
        options2.inSampleSize = inSampleSize;
        bitmap = BitmapFactory.decodeFile(filePath, options2);

        return bitmap;
    }

    /**
     * url file download
     *
     * @param filePath
     * @return
     */
    private Bitmap bitmapURLdownload(String filePath) {
        // TODO Auto-generated method stub
        // url로 된 이미지를 다운로드하여 return한다.

        Bitmap bitmap = null;
        BufferedInputStream bis = null;

        try {
            URL url = new URL(filePath);
            URLConnection conn = url.openConnection();
            conn.connect();

            int nSize = conn.getContentLength();
            bis = new BufferedInputStream(conn.getInputStream(), nSize);
            bitmap = BitmapFactory.decodeStream(bis);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                bis.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return bitmap;
    }

    /**
     * bitmap을 file로 저장
     *
     * @param bitmap
     */
    public File bitmapToFile(Bitmap bitmap) {

        File file = null;
        FileOutputStream out = null;

        try {
            // 파일 path설정
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .getAbsolutePath() + File.separator + DIRECTORY_NAME;
//            Log.e("sjjang", "path : " + path);

            // 폴더 만들기
            File fileDir = new File(path);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }

            // 파일 이름 설정
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "SkillGame_" + timeStamp + ".jpg";
            String fullPath = fileDir.getPath() + File.separator + fileName;

            // bitmap을 file로 저장
            out = new FileOutputStream(fullPath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

            file = new File(fullPath);
//            Log.e("sjjang", "file : " + file);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } finally {
            try {
                out.flush();
                out.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * bitmap을 회전
     *
     * @param bitmap
     */
    public Bitmap bitmapRotate(Bitmap bitmap, File file) {
        try {

            ExifInterface exif = new ExifInterface(file.getPath());
            int exifOrientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            int exifDegree = exifOrientationToDegrees(exifOrientation);
            bitmap = rotate(bitmap, exifDegree);

            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    /*
    * EXIF정보를 회전각도로 변환하는 메서드
     * @param exifOrientation EXIF 회전각
    * @return 실제 각도
    */
    public int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    /**
     * 이미지를 회전시킵니다.
     *
     * @param bitmap  비트맵 이미지
     * @param degrees 회전 각도
     * @return 회전된 이미지
     */
    // 최종적으로 이미지를 리턴함
    public Bitmap rotate(Bitmap bitmap, int degrees) {
        if (degrees != 0 && bitmap != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2,
                    (float) bitmap.getHeight() / 2);

            try {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                if (bitmap != converted) {
                    bitmap.recycle();
                    bitmap = converted;
                }
            } catch (OutOfMemoryError ex) {
                // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
            }
        }
        return bitmap;
    }
}
