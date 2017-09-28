package com.hour24.toysrental.service.timeline.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hour24.toysrental.R;
import com.hour24.toysrental.common.AES256Util;
import com.hour24.toysrental.common.BackPressClose;
import com.hour24.toysrental.common.Constants;
import com.hour24.toysrental.common.Utils;
import com.hour24.toysrental.common.view.picture.PictureView;
import com.hour24.toysrental.common.view.RecyclerViewPositionHelper;
import com.hour24.toysrental.common.view.floatingbutton.FloatingActionButton;
import com.hour24.toysrental.common.view.floatingbutton.FloatingActionsMenu;
import com.hour24.toysrental.service.center.model.MCenter;
import com.hour24.toysrental.service.center.view.CenterDetail;
import com.hour24.toysrental.service.center.view.CenterMain;
import com.hour24.toysrental.service.login.LoginMain;
import com.hour24.toysrental.service.login.LogoutMain;
import com.hour24.toysrental.service.reply.view.ReplyMain;
import com.hour24.toysrental.service.timeline.model.MPicture;
import com.hour24.toysrental.service.timeline.model.MTimeLine;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import cz.msebera.android.httpclient.Header;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TimeLineMain extends AppCompatActivity implements View.OnClickListener {

    private Context context;

    private RecyclerView recyclerView;
    private LinearLayout llEmptyData;
    private ImageView ivEmpty;

    private LinearLayoutManager layoutManager;

    // FAB
    private FloatingActionsMenu fabGroup;
    private FloatingActionButton fabWrite;
    private FloatingActionButton fabCenter;
    private FloatingActionButton fabMember;

    public static RecyclerAdapter recyclerAdapter;
    private ArrayList<MTimeLine> listTimeLine;

    // paging
    private boolean isSetAdapter = false;
    private int pagingCount = 20; // 가져올 아이템 갯수
    private boolean lastItemVisibleFlag = false;
    private RecyclerViewPositionHelper recyclerViewPositionHelper;
    private String moreYn = "";

    private BackPressClose backPressClose;

    // 기타 변수
    private boolean isScrolled = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_exit);
        setContentView(R.layout.timeline_main);
        backPressClose = new BackPressClose(this);
        getSupportActionBar().setTitle("타임라인");
        context = TimeLineMain.this;

        // list 초기화
        listTimeLine = new ArrayList<MTimeLine>();

        initLayout();
        getData();
        getMemberInfo();

        // 처음 안내문
        String isInfoNever = Utils.getPreferences(context, "isInfoNever");
        if (isInfoNever == null || "false".equals(isInfoNever)) {
            Intent intent = new Intent(context, Info.class);
            startActivity(intent);
        }

    }

    @Override
    public void onBackPressed() {
        // 두번누르면 종료
        backPressClose.onBackPressed();
    }


    @Override
    public void onClick(View v) {
        Intent intent = null;

        switch (v.getId()) {
            case R.id.fabCenter:
                intent = new Intent(context, CenterMain.class);
                startActivity(intent);
                break;

            case R.id.fabMember:
                String memberSeq = Utils.getPreferences(context, "memberSeq");
                // memberSeq가 null면 로그인 처리
                if (memberSeq == null) {
                    intent = new Intent(context, LoginMain.class);
                    startActivityForResult(intent, Constants.CODE.REQUEST_LOGIN);
                } else {
                    intent = new Intent(context, LogoutMain.class);
                    startActivityForResult(intent, Constants.CODE.REQUEST_LOGOUT);
                }
                break;

            case R.id.fabWrite:
                if (Constants.MEMBER_SEQ != null) {
                    intent = new Intent(context, WriteMain.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivityForResult(intent, Constants.CODE.REQUEST_WRITE);
                } else {
                    Toast.makeText(context, getString(R.string.toast_login_need), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Log.e("sjjang", "requestCode : " + requestCode + " / " + "resultCode : " + resultCode + " / " + "data : " + data);

        if (requestCode == Constants.CODE.REQUEST_LOGIN) {
            // 로그인처리
            if (data != null) {
                if (resultCode == RESULT_OK) {
                    String memberId = data.getStringExtra("memberId");
                    String memberName = data.getStringExtra("memberName");
                    String loginTypeCd = data.getStringExtra("loginTypeCd");
                    Log.e("sjjang", "memberId : " + memberId);
                    Log.e("sjjang", "memberName : " + memberName);
                    Log.e("sjjang", "loginTypeCd : " + loginTypeCd);

                    onLogin(memberId, memberName, loginTypeCd);
                    getData();
                }
            }
        } else if (requestCode == Constants.CODE.REQUEST_LOGOUT) {
            if (resultCode == RESULT_OK) {

                Constants.MEMBER_SEQ = null;
                Constants.AUTH_CD = null;
                Utils.setPreferences(context, "authCd", null);
                Utils.setPreferences(context, "memberSeq", null);
                Utils.setPreferences(context, "memberId", null);
                Utils.setPreferences(context, "memberName", null);
                Utils.setPreferences(context, "loginTypeCd", null);

                // 현재 보이는 화면 새로고침 필요
                getMemberInfo();
                getData();

                Toast.makeText(context, getString(R.string.toast_logout), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == Constants.CODE.REQUEST_WRITE) {
            // 타임라인 글 등록 후 화면 새로고침
            if (resultCode == RESULT_OK) {
                refreshData();
            }
        } else if (requestCode == Constants.CODE.REQUEST_MODIFY) {
            if (resultCode == RESULT_OK) {
                refreshData();
            }
        }
    }

    /**
     * 레이아웃 초기화
     */
    private void initLayout() {

        // SCREEN
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Constants.SCREEN_WIDTH = metrics.widthPixels;
        Constants.SCREEN_HEIGHT = metrics.heightPixels;

        llEmptyData = (LinearLayout) findViewById(R.id.llEmptyData);
        ivEmpty = (ImageView) findViewById(R.id.ivEmpty);

        fabGroup = (FloatingActionsMenu) findViewById(R.id.fabGroup);
        fabWrite = (FloatingActionButton) findViewById(R.id.fabWrite);
        fabCenter = (FloatingActionButton) findViewById(R.id.fabCenter);
        fabMember = (FloatingActionButton) findViewById(R.id.fabMember);

        fabWrite.setOnClickListener(this);
        fabCenter.setOnClickListener(this);
        fabMember.setOnClickListener(this);
        llEmptyData.setOnClickListener(this);


        ivEmpty.setColorFilter(ContextCompat.getColor(context, R.color.color_ffffff));

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        new RecyclerViewToolbarHideShow(toolbar, recyclerView).onProc();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastItemVisibleFlag) {
                    if ("Y".equals(moreYn)) {
                        getData();
                    }
                }

                // 스크롤을 올렸을때 fab 를 보이게 한다.
                if (!isScrolled) {
                    fabGroup.setVisibility(View.VISIBLE);
                } else {
                    fabGroup.setVisibility(View.GONE);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (dy > 0) {
                    isScrolled = true;
                    fabGroup.setVisibility(View.GONE);

                    recyclerViewPositionHelper = RecyclerViewPositionHelper.createHelper(recyclerView);
                    //현재 화면에 보이는 첫번째 리스트 아이템의 번호(firstVisibleItem) + 현재 화면에 보이는 리스트 아이템의 갯수(visibleItemCount)가 리스트 전체의 갯수(totalItemCount) -1 보다 크거나 같을때

                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = recyclerViewPositionHelper.getItemCount();
                    int firstVisibleItem = recyclerViewPositionHelper.findFirstVisibleItemPosition();

                    lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
                } else {
                    isScrolled = false;
                    fabGroup.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void refreshData() {
        this.finish();
        Intent intent = new Intent(context, TimeLineMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    /**
     * 내부 로그인 절차
     */
    private void onLogin(String memberId, String memberName, String loginTypeCd) {
        // 1. 서버에 id(양방향), name(양방향)을 보낸다.
        // 2. id 조회후 기존 회원 일경우 update
        //  신규 회원일 경우 id, tokent 등록
        // 3. prefrence에 사용자 정보 저장
        // 4. nav Header에 사용자 정보 세팅

        try {

            AES256Util aes256 = new AES256Util(AES256Util.key);
            String aesMemberId = aes256.aesEncode(memberId);
            String aesMemberName = aes256.aesEncode(memberName);
            String serviceCd = Constants.CODE.SERVICE_CD;

            JSONObject req = new JSONObject();
            req.put("memberId", aesMemberId);
            req.put("memberName", aesMemberName);
            req.put("loginTypeCd", loginTypeCd);
            req.put("serviceCd", serviceCd);
            Log.e("sjjang", req.toString());

            RequestParams params = new RequestParams();
            params.put("param", req.toString());

            String hostUrl = Constants.HOST_URL + Constants.HOST_DAO;
            String requestUrl = hostUrl + "/selectMemberLogin.jsp";
            // Log.e("sjjang", "requestUrl : " + requestUrl);
            AsyncHttpClient client = new AsyncHttpClient();
            client.post(context, requestUrl, params, new AsyncHttpResponseHandler() {
                @Override
                public void onStart() {
                    // Log.e("sjjang", "onStart");
                    //Utils.progressDialogShow(context);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    Log.e("sjjang", "onSuccess : " + new String(response).trim());
                    // Utils.progressDialogDismiss();

                    try {
                        JSONObject json = new JSONObject(new String(response).trim());
                        boolean isSuccess = json.getBoolean("isSuccess");

                        if (isSuccess) {
                            JSONObject jsonMember = json.getJSONObject("MEMBER");
                            String memberSeq = jsonMember.getString("MEMBER_SEQ");
                            String authCd = jsonMember.getString("AUTH_CD");
                            String memberId = jsonMember.getString("MEMBER_ID");
                            String memberName = jsonMember.getString("MEMBER_NAME");
                            String loginTypeCd = jsonMember.getString("LOGIN_TYPE_CD");

                            Constants.MEMBER_SEQ = memberSeq;
                            Constants.AUTH_CD = authCd;
                            Utils.setPreferences(context, "authCd", authCd);
                            Utils.setPreferences(context, "memberSeq", memberSeq);
                            Utils.setPreferences(context, "memberId", memberId);
                            Utils.setPreferences(context, "memberName", memberName);
                            Utils.setPreferences(context, "loginTypeCd", loginTypeCd);

                            // fab에 로그인을 회원정보로 세팅
                            getMemberInfo();

                        } else {
                            Toast.makeText(context, json.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(context, getString(R.string.toast_data_proc_fail), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    // Log.e("sjjang", "onFailure : " + e.toString());
                    // Utils.progressDialogDismiss();
                    Toast.makeText(context, getString(R.string.toast_data_get_fail), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, getString(R.string.toast_data_proc_fail), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 회원정보 세팅
     */
    private void getMemberInfo() {

        String memberSeq = Utils.getPreferences(context, "memberSeq");
        String authCd = Utils.getPreferences(context, "authCd");
        Constants.MEMBER_SEQ = memberSeq;
        Constants.AUTH_CD = authCd;

        if (memberSeq == null) {
            // 로그인 정보가 없을 경우
            fabMember.setTitle("로그인");
            fabMember.setIcon(R.drawable.ic_lock_black_36dp);
        } else {
            // 로그인 정보가 있을 경우
            fabMember.setTitle("회원정보");
            fabMember.setIcon(R.drawable.ic_account_circle_black_36dp);
        }
    }

    /*
   * 데이터 가져옴
   * */
    public void getData() {
        try {

            String boardCd = "60002";
            String placeCd = "30000";
            String serviceCd = Constants.CODE.SERVICE_CD;

            JSONObject req = new JSONObject();
            req.put("boardCd", boardCd);
            req.put("curRowCount", layoutManager.getItemCount()); // 현재 로우 카운트;
            req.put("pagingCount", pagingCount);
            req.put("placeCd", placeCd);
            req.put("serviceCd", serviceCd);

            RequestParams params = new RequestParams();
            params.put("param", req.toString());
            Log.e("sjjang", "params : " + params);

            String hostUrl = Constants.HOST_URL + Constants.HOST_DAO;
            String requestUrl = hostUrl + "/selectTimeLine.jsp";
            Log.e("sjjang", "requestUrl : " + requestUrl);
            AsyncHttpClient client = new AsyncHttpClient();
            client.post(context, requestUrl, params, new AsyncHttpResponseHandler() {
                @Override
                public void onStart() {
                    // Log.e("sjjang", "onStart");
                    Utils.progressDialogShow(context);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    Log.e("sjjang", "onSuccess : " + new String(response).trim());
                    Utils.progressDialogDismiss();
                    dataPasre(new String(response).trim());

                    if (!isSetAdapter) {
                        recyclerAdapter = new RecyclerAdapter(context, listTimeLine);
                        recyclerView.setAdapter(recyclerAdapter);
                        isSetAdapter = true;
                    } else {
                        recyclerAdapter.notifyDataSetChanged();
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
    * 데이터 파싱
    * */
    private void dataPasre(String res) {
        try {

            JSONObject json = new JSONObject(res);

            boolean isSuccess = json.getBoolean("isSuccess");
            if (isSuccess) {

                moreYn = json.getString("moreYn");

                AES256Util aes256 = new AES256Util(AES256Util.key);

                JSONArray result = json.getJSONArray("RESULT");
                if (result.length() > 0) {
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject data = result.getJSONObject(i);

                        MTimeLine timeLine = new MTimeLine();
                        timeLine.setBoardSeq(data.getString("BOARD_SEQ"));
                        timeLine.setBoardCd(data.getString("BOARD_CD"));
                        timeLine.setMemberSeq(data.getString("MEMBER_SEQ"));
                        timeLine.setSource(data.getString("SOURCE"));
                        timeLine.setDeleteYn(data.getString("DELETE_YN"));
                        timeLine.setPlaceCd(data.getString("PLACE_CD"));
                        timeLine.setPlaceName(data.getString("PLACE_NAME"));
                        timeLine.setIntDt(data.getString("INT_DT"));
                        timeLine.setUdtDt(data.getString("UDT_DT"));
                        timeLine.setUdtDt(data.getString("AUTH_CD"));
                        timeLine.setLikeCt(data.getString("LIKE_CT"));

                        // 컨텐츠 개행 처리
                        String content = data.getString("CONTENT");
                        content = content.replace("#n", "\n");
                        timeLine.setContent(content);

                        String memberName = aes256.aesDecode(data.getString("MEMBER_NAME"));
                        timeLine.setMemberName(memberName);

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
                        timeLine.setMemberId(memberId);

                        // 이미지
                        ArrayList<MPicture> listPicture = new ArrayList<MPicture>();
                        JSONObject jsonFile = data.getJSONObject("fileList");
                        JSONArray arrayFile = jsonFile.getJSONArray("RESULT");
                        for (int j = 0; j < arrayFile.length(); j++) {
                            JSONObject file = arrayFile.getJSONObject(j);
                            MPicture picture = new MPicture();
                            picture.setUrl(file.getString("FILE_URL"));
                            picture.setAttachSeq(file.getString("ATTACH_SEQ"));
                            listPicture.add(picture);
                        }

                        timeLine.setListPictures(listPicture);

                        listTimeLine.add(timeLine);
                    }
                    isEmptyData(false);
                } else {
                    // 기존에 데이터가 없는지 확인
                    if (listTimeLine.size() < 1) {
                        isEmptyData(true);
                    }
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
        List<MTimeLine> items;

        public RecyclerAdapter(Context context, ArrayList<MTimeLine> items) {
            this.context = context;
            this.items = items;
        }

        @Override
        public int getItemCount() {
            return items == null ? 0 : items.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.timeline_item, parent, false);
            return new ViewHolder(v);
        }

        public void removeItem(int position) {
            this.items.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount() - position);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final MTimeLine item = items.get(position);

            holder.tvName.setText(item.getMemberName());
            holder.tvId.setText("(" + item.getMemberId() + ")");
            holder.tvContent.setText(item.getContent());
            holder.tvIntDt.setText(item.getIntDt());
            holder.tvPlaceName.setText(item.getPlaceName());
            holder.tvLikeCt.setText(item.getLikeCt());

            holder.ivCalendar.setColorFilter(ContextCompat.getColor(context, R.color.color_c5aeae));
            holder.ivPlace.setColorFilter(ContextCompat.getColor(context, R.color.color_c5aeae));
            holder.ivLike.setColorFilter(ContextCompat.getColor(context, R.color.color_e6d7d7));
            holder.ivReply.setColorFilter(ContextCompat.getColor(context, R.color.color_e6d7d7));
            holder.ivModify.setColorFilter(ContextCompat.getColor(context, R.color.color_e6d7d7));
            holder.ivRemove.setColorFilter(ContextCompat.getColor(context, R.color.color_e6d7d7));

            // 이미지 영역
            final ArrayList<MPicture> listPicture = item.getListPictures();
            if (listPicture.size() != 0) {
                holder.rlPictureMain.setVisibility(View.VISIBLE);

                // 부모의 layout을 맞춘다
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.rlPictureMain.getLayoutParams();
                layoutParams.setMargins(0, 0, 0, 0);
                holder.rlPictureMain.setLayoutParams(layoutParams);
                holder.rlPictureMain.getLayoutParams().width = Constants.SCREEN_WIDTH;
                holder.rlPictureMain.getLayoutParams().height = Constants.SCREEN_WIDTH;

                int width = Constants.SCREEN_WIDTH;
                String fileUrl = Constants.HOST_URL + listPicture.get(0).getUrl();
                Picasso.with(context).load(fileUrl)
                        .resize(width, width)
                        .centerCrop()
                        .tag(context)
                        .into(holder.ivPicture);

                // 이미지가 2개 이상일 경우 +1 해준다
                if (listPicture.size() >= 2) {
                    holder.llCountMain.setVisibility(View.VISIBLE);
                    holder.tvPictureCount.setText("+" + (listPicture.size() - 1));
                } else {
                    holder.llCountMain.setVisibility(View.GONE);
                    holder.tvPictureCount.setText("");
                }
            } else {
                holder.rlPictureMain.setVisibility(View.GONE);
            }

            // 이미지 클릭 이벤트
            holder.ivPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, PictureView.class);
                    intent.putExtra("listPicture", listPicture);
                    intent.putExtra("picturePosition", 0);
                    context.startActivity(intent);
                }
            });

            // 공감
            int imageRes = 0;
            int colorRes = 0;
            int likeCt = Integer.parseInt(item.getLikeCt());
            holder.tvLikeCt.setText(String.valueOf(likeCt));
            if (likeCt >= 1) {
                // 공감
                imageRes = R.drawable.ic_favorite_black_24dp;
                colorRes = R.color.color_f5b2c9;
            } else {
                // 비공감
                imageRes = R.drawable.ic_favorite_border_black_24dp;
                colorRes = R.color.color_e6d7d7;
            }
            holder.ivLike.setImageDrawable(ContextCompat.getDrawable(context, imageRes));
            holder.ivLike.setColorFilter(ContextCompat.getColor(context, colorRes));

            // 공감 클릭 이벤트
            holder.rlLike.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (Constants.MEMBER_SEQ != null) {
                        try {
                            JSONObject req = new JSONObject();
                            req.put("memberSeq", Constants.MEMBER_SEQ);
                            req.put("contentSeq", item.getBoardSeq());
                            req.put("contentCd", item.getBoardCd());

                            RequestParams params = new RequestParams();
                            params.put("param", req.toString());
                            // Log.e("sjjang", "params : " + params);

                            String hostUrl = Constants.HOST_URL + Constants.HOST_DAO;
                            String requestUrl = hostUrl + "/insertLike.jsp";
                            // Log.e("sjjang", "requestUrl : " + requestUrl);
                            AsyncHttpClient client = new AsyncHttpClient();
                            client.post(context, requestUrl, params, new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                                    // Log.e("sjjang", "onSuccess : " + new String(response).trim());
                                    // 현재 공감 갯수
                                    int curCt = Integer.parseInt(holder.tvLikeCt.getText().toString());
                                    int imageRes = 0;
                                    int colorRes = 0;

                                    try {
                                        JSONObject json = new JSONObject(new String(response).trim());
                                        boolean isSuccess = json.getBoolean("isSuccess");
                                        if (isSuccess) {
                                            String procType = json.getString("PROC_TYPE");

                                            // 공감 추가일 경우 +1
                                            if ("insert".equals(procType)) {
                                                curCt += 1;
                                                imageRes = R.drawable.ic_favorite_black_24dp;
                                                colorRes = R.color.color_f5b2c9;
                                            } else {
                                                // delete 일경우 -1
                                                curCt -= 1;
                                                if (curCt < 1) {
                                                    imageRes = R.drawable.ic_favorite_border_black_24dp;
                                                    colorRes = R.color.color_e6d7d7;
                                                } else {
                                                    imageRes = R.drawable.ic_favorite_black_24dp;
                                                    colorRes = R.color.color_f5b2c9;
                                                }
                                            }
                                            holder.tvLikeCt.setText(String.valueOf(curCt));
                                            holder.ivLike.setImageDrawable(ContextCompat.getDrawable(context, imageRes));
                                            holder.ivLike.setColorFilter(ContextCompat.getColor(context, colorRes));

                                            // 현재 list의 공감 데이터 수정
                                            item.setLikeCt(String.valueOf(curCt));
                                            listTimeLine.set(position, item);
                                            recyclerAdapter.notifyDataSetChanged();

                                        } else {
                                            Toast.makeText(context, json.getString("message"), Toast.LENGTH_SHORT).show();
                                        }
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
                    } else {
                        Toast.makeText(context, getString(R.string.toast_login_need), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // 댓글
            holder.rlReply.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ReplyMain.class);
                    intent.putExtra("contentSeq", item.getBoardSeq());
                    intent.putExtra("contentCd", item.getBoardCd());
                    startActivity(intent);
                }
            });

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

            // 수정
            holder.rlModify.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, WriteMain.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.putExtra("boardInfo", item);
                    intent.putExtra("listPicture", items.get(position).getListPictures());
                    startActivityForResult(intent, Constants.CODE.REQUEST_MODIFY);
                }
            });

            // 삭제
            holder.rlRemove.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Toast.makeText(context, getString(R.string.timeline_delete_long_click), Toast.LENGTH_SHORT).show();
                }
            });

            // 삭제 처리
            holder.rlRemove.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    try {
                        JSONObject req = new JSONObject();
                        req.put("boardSeq", item.getBoardSeq());

                        RequestParams params = new RequestParams();
                        params.put("param", req.toString());
                        // Log.e("sjjang", "params : " + params);

                        String hostUrl = Constants.HOST_URL + Constants.HOST_DAO;
                        String requestUrl = hostUrl + "/deleteTimeLine.jsp";
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

            holder.tvPlaceName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CenterDetail.class);
                    MCenter center = new MCenter();
                    center.setCenterSeq(item.getPlaceCd());
                    center.setCenterName(item.getPlaceName());
                    intent.putExtra("center", center);
                    startActivity(intent);
                }
            });
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            LinearLayout llMain;
            LinearLayout llCountMain;
            RelativeLayout rlLike;
            RelativeLayout rlReply;
            RelativeLayout rlModify;
            RelativeLayout rlRemove;
            RelativeLayout rlPictureMain;

            TextView tvName;
            TextView tvId;
            TextView tvContent;
            TextView tvIntDt;
            TextView tvPlaceName;
            TextView tvLikeCt;
            TextView tvPictureCount;

            ImageView ivCalendar;
            ImageView ivPlace;
            ImageView ivLike;
            ImageView ivReply;
            ImageView ivModify;
            ImageView ivRemove;
            ImageView ivPicture;

            public ViewHolder(View itemView) {
                super(itemView);

                llMain = (LinearLayout) itemView.findViewById(R.id.llMain);
                llCountMain = (LinearLayout) itemView.findViewById(R.id.llCountMain);
                rlLike = (RelativeLayout) itemView.findViewById(R.id.rlLike);
                rlReply = (RelativeLayout) itemView.findViewById(R.id.rlReply);
                rlModify = (RelativeLayout) itemView.findViewById(R.id.rlModify);
                rlRemove = (RelativeLayout) itemView.findViewById(R.id.rlRemove);
                rlPictureMain = (RelativeLayout) itemView.findViewById(R.id.rlPictureMain);

                tvName = (TextView) itemView.findViewById(R.id.tvName);
                tvId = (TextView) itemView.findViewById(R.id.tvId);
                tvContent = (TextView) itemView.findViewById(R.id.tvContent);
                tvIntDt = (TextView) itemView.findViewById(R.id.tvIntDt);
                tvPlaceName = (TextView) itemView.findViewById(R.id.tvPlaceName);
                tvLikeCt = (TextView) itemView.findViewById(R.id.tvLikeCt);
                tvPictureCount = (TextView) itemView.findViewById(R.id.tvPictureCount);

                ivCalendar = (ImageView) itemView.findViewById(R.id.ivCalendar);
                ivPlace = (ImageView) itemView.findViewById(R.id.ivPlace);
                ivLike = (ImageView) itemView.findViewById(R.id.ivLike);
                ivReply = (ImageView) itemView.findViewById(R.id.ivReply);
                ivModify = (ImageView) itemView.findViewById(R.id.ivModify);
                ivRemove = (ImageView) itemView.findViewById(R.id.ivRemove);
                ivPicture = (ImageView) itemView.findViewById(R.id.ivPicture);
            }
        }
    }
}
