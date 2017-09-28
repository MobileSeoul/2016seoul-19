/**
 * (주)오픈잇 | http://www.openit.co.kr
 * Copyright (c)2006-2013, openit Inc.
 * All rights reserved.
 */
package com.hour24.toysrental.common.view.picture;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.hour24.toysrental.R;
import com.hour24.toysrental.common.Constants;
import com.hour24.toysrental.service.timeline.model.MPicture;
import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoView;

/**
 * 첨부 이미지 상세보기
 */
public class PictureView extends Activity {

    private Context context;

    // 이미지 리스트
    private ArrayList<MPicture> listPicture;

    // 클릭한 postion
    private int picturePosition;

    private ViewPager viewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_view);
        context = PictureView.this;

        // 화면 구성
        initLayout();

    }

    private void initLayout() {

        Intent intent = getIntent();
        listPicture = (ArrayList<MPicture>) intent.getSerializableExtra("listPicture");
        // 처음 화면에 보여질 이미지
        picturePosition = intent.getIntExtra("picturePosition", 0);

        viewPager = (ViewPager) findViewById(R.id.viewPager);

        ImagePagerAdapter imagePagerAdapter = new ImagePagerAdapter(context);
        viewPager.setAdapter(imagePagerAdapter);
        viewPager.setCurrentItem(picturePosition);
    }

    private class ImagePagerAdapter extends PagerAdapter {

        private final Context context;

        public ImagePagerAdapter(Context context) {
            super();
            this.context = context;
        }

        @Override
        public int getCount() {
            return listPicture.size();
        }

        @Override
        public Object instantiateItem(View pager, final int position) {

            final MPicture item = listPicture.get(position);

            PhotoView photoView = new PhotoView(context);
            String fileUrl = Constants.HOST_URL + item.getUrl();
            Picasso.with(context).load(fileUrl).into(photoView);
            ((ViewPager) pager).addView(photoView, 0);

            return photoView;
        }

        @Override
        public void destroyItem(View pager, int position, Object view) {
            ((ViewPager) pager).removeView((View) view);
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public void finishUpdate(View arg0) {
            // nothing
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
            // nothing
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
            // nothing
        }
    }
}
