package com.hour24.toysrental.common.view.picture;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hour24.toysrental.R;
import com.hour24.toysrental.common.Constants;
import com.hour24.toysrental.service.timeline.model.MPicture;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by 장세진 on 2016-08-13.
 */
public class PictureListView {

    public static Context context;
    public static RecyclerView recyclerView;
    public static ArrayList<MPicture> listPicture;

    public PictureListView(Context context, RecyclerView recyclerView, ArrayList<MPicture> listPicture) {
        this.context = context;
        this.listPicture = listPicture;
        this.recyclerView = recyclerView;
    }

    public void onProc() {
        if (listPicture.size() >= 1) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(new RecyclerAdapter(context, listPicture));
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.GONE);
        }
    }

    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

        Context context;
        ArrayList<MPicture> items;

        public RecyclerAdapter(Context context, ArrayList<MPicture> items) {
            this.context = context;
            this.items = items;
        }

        @Override
        public int getItemCount() {
            return items == null ? 0 : items.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.timeline_item_picture, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final MPicture item = items.get(position);

            String fileUrl = Constants.HOST_URL + item.getUrl();

            // 부모의 layout을 맞춘다
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.rlMain.getLayoutParams();
            layoutParams.setMargins(0, 0, 0, 0);
            holder.rlMain.setLayoutParams(layoutParams);

            holder.llMain.getLayoutParams().width = Constants.SCREEN_WIDTH;
            holder.llMain.getLayoutParams().height = Constants.SCREEN_WIDTH;

            int width = (Constants.SCREEN_WIDTH / 10) * 10;
            Picasso.with(context).load(fileUrl)
                    .resize(width, width)
                    .centerCrop()
                    .tag(context)
                    .into(holder.ivPicture);

            // 이미지 클릭 이벤트
            holder.ivPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, PictureView.class);
                    intent.putExtra("listPicture", items);
                    intent.putExtra("picturePosition", position);
                    context.startActivity(intent);
                }
            });

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            LinearLayout llMain;
            RelativeLayout rlMain;
            ImageView ivPicture;

            public ViewHolder(View itemView) {
                super(itemView);
                llMain = (LinearLayout) itemView.findViewById(R.id.llMain);
                rlMain = (RelativeLayout) itemView.findViewById(R.id.rlMain);
                ivPicture = (ImageView) itemView.findViewById(R.id.ivPicture);
            }
        }
    }
}
