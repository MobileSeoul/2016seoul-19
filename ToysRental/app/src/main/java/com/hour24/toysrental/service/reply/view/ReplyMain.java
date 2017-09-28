package com.hour24.toysrental.service.reply.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hour24.toysrental.R;
import com.hour24.toysrental.common.AES256Util;
import com.hour24.toysrental.common.Constants;
import com.hour24.toysrental.common.Utils;
import com.hour24.toysrental.service.reply.model.MReply;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ReplyMain extends Activity implements View.OnClickListener {

    private Context context;

    private ArrayList<MReply> listReply;

    private LinearLayout llEmptyData;
    private LinearLayout llModifyControl;
    private RelativeLayout rlCancel;
    private RelativeLayout rlModify;
    private RecyclerView recyclerView;
    private RelativeLayout rlWrite;
    private EditText etContent;
    private ImageView ivWrite;
    private ImageView ivEmpty;
    private ImageView ivCancel;
    private ImageView ivModify;

    private String contentSeq = "";
    private String contentCd = "";
    private String replySeq = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_exit);
        setTheme(R.style.ReplyMainTheme);
        setContentView(R.layout.reply_main);
        context = ReplyMain.this;

        // 통합 댓글 seq, cd
        Intent intent = getIntent();
        contentSeq = intent.getStringExtra("contentSeq");
        contentCd = intent.getStringExtra("contentCd");
        // Log.e("sjjang", "contentSeq : " + contentSeq);
        // Log.e("sjjang", "contentCd : " + contentCd);

        listReply = new ArrayList<MReply>();

        initLayout();
        getData();

    }

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.slide_exit, R.anim.slide_in_up);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlWrite:
                // 로그인이 되어있을 경우 등록 절차
                if (Constants.MEMBER_SEQ != null) {
                    String reply = etContent.getText().toString();
                    if (reply.length() >= 1) {
                        setData();
                    } else {
                        Toast.makeText(context, getString(R.string.toast_text_empty), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, getString(R.string.toast_login_need), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.llEmptyData:
                refreshData();
                break;

            case R.id.rlCancel:
                etContent.setText("");
                llModifyControl.setVisibility(View.GONE);
                rlWrite.setVisibility(View.VISIBLE);
                break;
            case R.id.rlModify:
                // 로그인이 되어있을 경우 수정 절차
                if (Constants.MEMBER_SEQ != null) {
                    String reply = etContent.getText().toString();
                    if (reply.length() >= 1) {
                        modifyData();
                    } else {
                        Toast.makeText(context, getString(R.string.toast_text_empty), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, getString(R.string.toast_login_need), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * 데이터 새로고침
     */
    private void refreshData() {
        listReply = new ArrayList<MReply>();
        getData();
    }


    /**
     * 레이아웃 초기화
     */
    private void initLayout() {

        llEmptyData = (LinearLayout) findViewById(R.id.llEmptyData);
        llModifyControl = (LinearLayout) findViewById(R.id.llModifyControl);
        rlWrite = (RelativeLayout) findViewById(R.id.rlWrite);
        rlModify = (RelativeLayout) findViewById(R.id.rlModify);
        rlCancel = (RelativeLayout) findViewById(R.id.rlCancel);
        etContent = (EditText) findViewById(R.id.etContent);
        ivWrite = (ImageView) findViewById(R.id.ivWrite);
        ivEmpty = (ImageView) findViewById(R.id.ivEmpty);
        ivModify = (ImageView) findViewById(R.id.ivModify);
        ivCancel = (ImageView) findViewById(R.id.ivCancel);

        rlWrite.setOnClickListener(this);
        rlCancel.setOnClickListener(this);
        rlModify.setOnClickListener(this);
        llEmptyData.setOnClickListener(this);

        ivWrite.setColorFilter(ContextCompat.getColor(context, R.color.color_e6d7d7));
        ivCancel.setColorFilter(ContextCompat.getColor(context, R.color.color_e6d7d7));
        ivModify.setColorFilter(ContextCompat.getColor(context, R.color.color_e6d7d7));
        ivEmpty.setColorFilter(ContextCompat.getColor(context, R.color.color_ffffff));

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(new RecyclerAdapter(context, listReply));

    }

    private void setData() {

        try {

            String reply = etContent.getText().toString();
            reply = reply.replace("\n", "#n"); // 개행처리

            JSONObject req = new JSONObject();
            req.put("memberSeq", Constants.MEMBER_SEQ);
            req.put("contentSeq", contentSeq);
            req.put("contentCd", contentCd);
            req.put("content", reply);
            req.put("source", "AOS");

            RequestParams params = new RequestParams();
            params.put("param", req.toString());
            // Log.e("sjjang", "params : " + params);

            String hostUrl = Constants.HOST_URL + Constants.HOST_DAO;
            String requestUrl = hostUrl + "/insertReply.jsp";
            // Log.e("sjjang", "requestUrl : " + requestUrl);
            AsyncHttpClient client = new AsyncHttpClient();
            client.post(context, requestUrl, params, new AsyncHttpResponseHandler() {
                @Override
                public void onStart() {
                    // Log.e("sjjang", "onStart");
                    Utils.progressDialogShow(context);
                }


                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    // Log.e("sjjang", "onSuccess : " + new String(response).trim());
                    Utils.progressDialogDismiss();
                    try {
                        JSONObject json = new JSONObject(new String(response).trim());
                        boolean isSuccess = json.getBoolean("isSuccess");
                        if (isSuccess) {
                            // 데이터 새로고침
                            etContent.setText("");
                            listReply = new ArrayList<MReply>();
                            getData();
                        }
                        Toast.makeText(context, json.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(context, getString(R.string.toast_data_proc_fail), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    // Log.e("sjjang", "onFailure : " + e.toString());
                    Utils.progressDialogDismiss();
                    Toast.makeText(context, getString(R.string.toast_data_get_fail), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, getString(R.string.toast_data_proc_fail), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 댓글 수정
     */
    private void modifyData() {
        try {

            String reply = etContent.getText().toString();
            reply = reply.replace("\n", "#n"); // 개행처리

            JSONObject req = new JSONObject();
            req.put("replySeq", replySeq);
            req.put("content", reply);
            req.put("source", "AOS");

            RequestParams params = new RequestParams();
            params.put("param", req.toString());
            // Log.e("sjjang", "params : " + params);

            String hostUrl = Constants.HOST_URL + Constants.HOST_DAO;
            String requestUrl = hostUrl + "/updateReply.jsp";
            // Log.e("sjjang", "requestUrl : " + requestUrl);
            AsyncHttpClient client = new AsyncHttpClient();
            client.post(context, requestUrl, params, new AsyncHttpResponseHandler() {
                @Override
                public void onStart() {
                    // Log.e("sjjang", "onStart");
                    Utils.progressDialogShow(context);
                }


                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    // Log.e("sjjang", "onSuccess : " + new String(response).trim());
                    Utils.progressDialogDismiss();
                    try {
                        JSONObject json = new JSONObject(new String(response).trim());
                        boolean isSuccess = json.getBoolean("isSuccess");
                        if (isSuccess) {
                            // 데이터 새로고침
                            etContent.setText("");
                            listReply = new ArrayList<MReply>();
                            llModifyControl.setVisibility(View.GONE);
                            rlWrite.setVisibility(View.VISIBLE);
                            getData();
                        }
                        Toast.makeText(context, json.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(context, getString(R.string.toast_data_proc_fail), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    // Log.e("sjjang", "onFailure : " + e.toString());
                    Utils.progressDialogDismiss();
                    Toast.makeText(context, getString(R.string.toast_data_get_fail), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, getString(R.string.toast_data_proc_fail), Toast.LENGTH_SHORT).show();
        }
    }

    /*
    * 데이터 가져옴
    * */
    private void getData() {
        try {
            JSONObject req = new JSONObject();
            req.put("contentSeq", contentSeq);
            req.put("contentCd", contentCd);

            RequestParams params = new RequestParams();
            params.put("param", req.toString());
            // Log.e("sjjang", "params : " + params);

            String hostUrl = Constants.HOST_URL + Constants.HOST_DAO;
            String requestUrl = hostUrl + "/selectReply.jsp";
            // Log.e("sjjang", "requestUrl : " + requestUrl);
            AsyncHttpClient client = new AsyncHttpClient();
            client.post(context, requestUrl, params, new AsyncHttpResponseHandler() {
                @Override
                public void onStart() {
                    // Log.e("sjjang", "onStart");
                    Utils.progressDialogShow(context);
                }


                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    // Log.e("sjjang", "onSuccess : " + new String(response).trim());
                    Utils.progressDialogDismiss();
                    dataPasre(new String(response).trim());
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    // Log.e("sjjang", "onFailure : " + e.toString());
                    Utils.progressDialogDismiss();
                    Toast.makeText(context, getString(R.string.toast_data_get_fail), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, getString(R.string.toast_data_proc_fail), Toast.LENGTH_SHORT).show();
        }
    }

    /*
    * 데이터 파싱
    * */
    private void dataPasre(String res) {
        try {

            JSONObject json = new JSONObject(res);

            boolean isSuccess = json.getBoolean("isSuccess");
            if (isSuccess) {

                // 복호화
                AES256Util aes256 = new AES256Util(AES256Util.key);

                JSONArray result = json.getJSONArray("RESULT");
                if (result.length() > 0) {
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject data = result.getJSONObject(i);

                        MReply reply = new MReply();
                        reply.setReplySeq(data.getString("REPLY_SEQ"));
                        reply.setContentSeq(data.getString("CONTENT_SEQ"));
                        reply.setContentCd(data.getString("CONTENT_CD"));
                        reply.setSource(data.getString("SOURCE"));
                        reply.setIntDt(data.getString("INT_DT"));
                        reply.setMemberSeq(data.getString("MEMBER_SEQ"));

                        // 컨텐츠 개행 처리
                        String content = data.getString("CONTENT");
                        content = content.replace("#n", "\n");
                        reply.setContent(content);

                        // 복호화
                        String memberName = aes256.aesDecode(data.getString("MEMBER_NAME"));
                        reply.setMemberName(memberName);

                        // 이메일 ***로 표시
                        String memberId = aes256.aesDecode(data.getString("MEMBER_ID"));
                        String memberIdFront = memberId.split("@")[0];
                        String memberIdBack = memberId.split("@")[1];
                        String memberIdFrontSub1 = memberIdFront.substring(0, 3);
                        String memberIdFrontSub2 = "";
                        for (int j = 3; j < memberIdFront.length(); j++) {
                            memberIdFrontSub2 += "*";
                        }
                        memberId = memberIdFrontSub1 + memberIdFrontSub2 + "@" + memberIdBack;
                        reply.setMemberId(memberId);

                        listReply.add(reply);
                    }

                    recyclerView.setAdapter(new RecyclerAdapter(context, listReply));

                    isEmptyData(false);
                } else {
                    isEmptyData(true);
                }
            } else {
                Toast.makeText(context, json.getString("message"), Toast.LENGTH_SHORT).show();
                isEmptyData(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, getString(R.string.toast_data_proc_fail), Toast.LENGTH_SHORT).show();
            isEmptyData(true);
        }
    }

    /**
     * 데이터 없다는 안내문 표시
     */
    private void isEmptyData(boolean flag) {
        if (flag) {
            recyclerView.setVisibility(View.GONE);
            llEmptyData.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            llEmptyData.setVisibility(View.GONE);
        }
    }

    /*
     * 타임라인 어뎁터
     * */
    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

        Context context;
        List<MReply> items;

        public RecyclerAdapter(Context context, ArrayList<MReply> items) {
            this.context = context;
            this.items = items;
        }

        @Override
        public int getItemCount() {
            return items == null ? 0 : items.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.reply_item, parent, false);
            return new ViewHolder(v);
        }

        public void removeItem(int position) {
            this.items.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount() - position);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final MReply item = items.get(position);

            holder.tvName.setText(item.getMemberName());
            holder.tvId.setText("(" + item.getMemberId() + ")");
            holder.tvContent.setText(item.getContent());
            holder.tvIntDt.setText(item.getIntDt());

            holder.ivCalendar.setColorFilter(ContextCompat.getColor(context, R.color.color_c5aeae));
            holder.ivModify.setColorFilter(ContextCompat.getColor(context, R.color.color_e6d7d7));
            holder.ivRemove.setColorFilter(ContextCompat.getColor(context, R.color.color_e6d7d7));

            if (Constants.MEMBER_SEQ == null) {
                holder.rlModify.setVisibility(View.GONE);
                holder.rlRemove.setVisibility(View.GONE);
            } else {
                if (Constants.MEMBER_SEQ.equals(item.getMemberSeq())) {
                    // 자기 글에만 수정, 삭제 보이게 함
                    holder.rlModify.setVisibility(View.VISIBLE);
                    holder.rlRemove.setVisibility(View.VISIBLE);
                } else if (Constants.AUTH_CD != null && Constants.AUTH_CD.equals("20001")) {
                    // 관리자 권한일 경우 수정, 삭제 가능
                    holder.rlModify.setVisibility(View.VISIBLE);
                    holder.rlRemove.setVisibility(View.VISIBLE);
                } else {
                    holder.rlModify.setVisibility(View.GONE);
                    holder.rlRemove.setVisibility(View.GONE);
                }
            }

            // 댓글 수정 클릭
            holder.rlModify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etContent.setText(item.getContent());
                    rlWrite.setVisibility(View.GONE);
                    llModifyControl.setVisibility(View.VISIBLE);

                    replySeq = item.getReplySeq();
                }
            });

            // 댓글 삭제
            holder.rlRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, getString(R.string.reply_delete_long_click), Toast.LENGTH_SHORT).show();
                }
            });

            // 댓글 삭제
            holder.rlRemove.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    try {
                        JSONObject req = new JSONObject();
                        req.put("replySeq", item.getReplySeq());

                        RequestParams params = new RequestParams();
                        params.put("param", req.toString());
                        // Log.e("sjjang", "params : " + params);

                        String hostUrl = Constants.HOST_URL + Constants.HOST_DAO;
                        String requestUrl = hostUrl + "/deleteReply.jsp";
                        // Log.e("sjjang", "requestUrl : " + requestUrl);
                        AsyncHttpClient client = new AsyncHttpClient();
                        client.post(context, requestUrl, params, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                                // Log.e("sjjang", "onSuccess : " + new String(response).trim());

                                try {
                                    JSONObject json = new JSONObject(new String(response).trim());
                                    boolean isSuccess = json.getBoolean("isSuccess");
                                    if (isSuccess) {
                                        removeItem(position);
                                    }
                                    Toast.makeText(context, json.getString("message"), Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(context, getString(R.string.toast_data_proc_fail), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                                // Log.e("sjjang", "onFailure : " + e.toString());
                                Toast.makeText(context, getString(R.string.toast_data_get_fail), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, getString(R.string.toast_data_proc_fail), Toast.LENGTH_SHORT).show();
                    }
                    return false;
                }
            });
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            LinearLayout llMain;
            RelativeLayout rlModify;
            RelativeLayout rlRemove;

            TextView tvName;
            TextView tvId;
            TextView tvContent;
            TextView tvIntDt;

            ImageView ivCalendar;
            ImageView ivModify;
            ImageView ivRemove;

            public ViewHolder(View itemView) {
                super(itemView);

                llMain = (LinearLayout) itemView.findViewById(R.id.llMain);
                rlModify = (RelativeLayout) itemView.findViewById(R.id.rlModify);
                rlRemove = (RelativeLayout) itemView.findViewById(R.id.rlRemove);

                tvName = (TextView) itemView.findViewById(R.id.tvName);
                tvId = (TextView) itemView.findViewById(R.id.tvId);
                tvContent = (TextView) itemView.findViewById(R.id.tvContent);
                tvIntDt = (TextView) itemView.findViewById(R.id.tvIntDt);

                ivCalendar = (ImageView) itemView.findViewById(R.id.ivCalendar);
                ivModify = (ImageView) itemView.findViewById(R.id.ivModify);
                ivRemove = (ImageView) itemView.findViewById(R.id.ivRemove);

            }
        }
    }
}
