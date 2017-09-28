package com.hour24.toysrental.service.timeline.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hour24.toysrental.R;
import com.hour24.toysrental.common.Constants;
import com.hour24.toysrental.common.GetPicture;
import com.hour24.toysrental.common.Utils;
import com.hour24.toysrental.service.timeline.model.MPicture;
import com.hour24.toysrental.service.timeline.model.MPlaceInfo;
import com.hour24.toysrental.service.timeline.model.MTimeLine;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class WriteMain extends Activity implements View.OnClickListener {

    private Context context;

    private MTimeLine timeLine;
    private ArrayList<MPicture> listPicture;
    private ArrayList<MPlaceInfo> listPlaceInfo;
    private ArrayList<String> listDeleteSeq;

    private RecyclerView recyclerViewPic;
    private RelativeLayout rlGalley;
    private RelativeLayout rlWrite;
    private EditText etWrite;
    private ImageView ivGalley;
    private ImageView ivWrite;
    private ImageView ivModify;
    private AppCompatSpinner spPlaceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_main);
        context = WriteMain.this;

        Intent intent = getIntent();
        timeLine = (MTimeLine) intent.getSerializableExtra("boardInfo");
        listPicture = (ArrayList<MPicture>) intent.getSerializableExtra("listPicture");
        listDeleteSeq = new ArrayList<String>();

        if (listPicture == null) {
            // 이미지가 없을경우에 새로 객체 생성
            listPicture = new ArrayList<MPicture>();
        }

        initLayout();
        getPlaceInfo();
    }

    private void initLayout() {

        rlGalley = (RelativeLayout) findViewById(R.id.rlGalley);
        rlWrite = (RelativeLayout) findViewById(R.id.rlWrite);
        etWrite = (EditText) findViewById(R.id.etWrite);
        ivGalley = (ImageView) findViewById(R.id.ivGalley);
        ivWrite = (ImageView) findViewById(R.id.ivWrite);
        ivModify = (ImageView) findViewById(R.id.ivModify);
        spPlaceInfo = (AppCompatSpinner) findViewById(R.id.spPlaceInfo);

        rlGalley.setOnClickListener(this);
        rlWrite.setOnClickListener(this);

        ivGalley.setColorFilter(ContextCompat.getColor(context, R.color.color_e6d7d7));
        ivWrite.setColorFilter(ContextCompat.getColor(context, R.color.color_e6d7d7));
        ivModify.setColorFilter(ContextCompat.getColor(context, R.color.color_e6d7d7));

        // recyclerView
        recyclerViewPic = (RecyclerView) findViewById(R.id.recyclerViewPic);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewPic.setLayoutManager(layoutManager);
        recyclerViewPic.setItemAnimator(new DefaultItemAnimator());

        recyclerViewPic.setAdapter(new RecyclerAdapter(context, listPicture));

        if (timeLine == null) {
            // 등록
            ivWrite.setVisibility(View.VISIBLE);
            ivModify.setVisibility(View.GONE);
        } else {
            // 수정
            ivWrite.setVisibility(View.GONE);
            ivModify.setVisibility(View.VISIBLE);
            setLayout();
        }
    }

    // 위치 정보 데이터
    // 현재 시설 정보의 데이터를 가져와서 데이터를 삽입해야함
    private void getPlaceInfo() {

        RequestParams params = new RequestParams();

        String hostUrl = Constants.HOST_URL + Constants.HOST_DAO_TOY;
        String requestUrl = hostUrl + "/selectCenter.jsp";
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

                    listPlaceInfo = new ArrayList<MPlaceInfo>();

                    String[] code = {"0"};
                    String[] name = {"센터선택"};

                    MPlaceInfo placeInfo = new MPlaceInfo();
                    placeInfo.setPlaceCd(code[0]);
                    placeInfo.setPlaceName(name[0]);
                    listPlaceInfo.add(placeInfo);

                    // 관리자만 한국산업인력공단입력 가능
                    if (Constants.AUTH_CD != null && "20001".equals(Constants.AUTH_CD)) {
                        placeInfo = new MPlaceInfo();
                        placeInfo.setPlaceCd(code[1]);
                        placeInfo.setPlaceName(name[1]);
                        listPlaceInfo.add(placeInfo);
                    }

                    JSONObject json = new JSONObject(new String(response).trim());
                    boolean isSuccess = json.getBoolean("isSuccess");
                    if (isSuccess) {
                        JSONArray result = json.getJSONArray("RESULT");
                        for (int i = 0; i < result.length(); i++) {
                            JSONObject data = result.getJSONObject(i);
                            placeInfo = new MPlaceInfo();
                            placeInfo.setPlaceCd(data.getString("CENTER_SEQ"));
                            placeInfo.setPlaceName(data.getString("CENTER_NAME"));
                            listPlaceInfo.add(placeInfo);
                        }

                        // 커스텀 어뎁터 설정
                        PlaceSpinnerAdapter adapter = new PlaceSpinnerAdapter(context, R.layout.write_item_spinner, listPlaceInfo);
                        spPlaceInfo.setAdapter(adapter);

                        // 수정할 데이터 세팅
                        if (timeLine != null) {
                            String placeCd = timeLine.getPlaceCd();
                            for (int j = 0; j < listPlaceInfo.size(); j++) {
                                if (placeCd.equals(listPlaceInfo.get(j).getPlaceCd())) {
                                    spPlaceInfo.setSelection(j);
                                }
                            }
                        }

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
                Utils.progressDialogDismiss();
                Toast.makeText(context, getString(R.string.toast_data_get_fail), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.CODE.REQUEST_GALLERY) {
            if (resultCode == RESULT_OK) {
                File file = new GetPicture(context, data).onProc();
                if (file != null) {
                    setPictureFile(file);
                } else {
                    Toast.makeText(context, getString(R.string.toast_get_image_fail), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, getString(R.string.toast_get_image_fail), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // Log.e("sjjang", "requestCode : " + requestCode);

        for (String permission : permissions) {
            // Log.e("sjjang", "permission : " + permission);
        }

        for (int grantResult : grantResults) {
            // Log.e("sjjang", "grantResult : " + grantResult);
        }

        if (requestCode == Constants.CODE.REQUEST_PERMISSION_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(context, getString(R.string.toast_need_permission_file), Toast.LENGTH_SHORT).show();
            }

            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, getString(R.string.toast_need_permission_file), Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlWrite:
                // 글 등록
                String content = etWrite.getText().toString();
                // 경기장 Cd
                String placeCd = listPlaceInfo.get(spPlaceInfo.getSelectedItemPosition()).getPlaceCd();
                if (content.length() >= 1) {
                    if (!"0".equals(placeCd)) {
                        if (timeLine == null) {
                            writeContentProc();
                        } else {
                            modifyContentProc();
                        }

                        // 키보드 숨기기
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(etWrite.getWindowToken(), 0);
                    } else {
                        Toast.makeText(context, getString(R.string.write_select_place), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, getString(R.string.toast_text_empty), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.rlGalley:
                // 사진 불러오기
                if (listPicture.size() < 5) {
                    boolean flag = false;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (Utils.checkPermissionsStorage(WriteMain.this)) {
                            flag = true;
                        }
                    } else {
                        flag = true;
                    }

                    if (flag) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.putExtra("return-data", true);

                        startActivityForResult(intent, Constants.CODE.REQUEST_GALLERY);
                    }
                } else {
                    Toast.makeText(context, getString(R.string.write_max_file_cout), Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    /**
     * 수정할 데이터 세팅
     */
    private void setLayout() {
        etWrite.setText(timeLine.getContent());

//        // 새로운 파일을 넣기 위해 초기화
//        listPicture = new ArrayList<MPicture>();
    }

    /**
     * 글 등록
     */
    private void writeContentProc() {
        String memberSeq = Constants.MEMBER_SEQ;

        // 개행처리
        String content = etWrite.getText().toString();
        content = content.replace("\n", "#n");

        // 경기장 코드
        String placeCd = listPlaceInfo.get(spPlaceInfo.getSelectedItemPosition()).getPlaceCd();
        String boardCd = "60002";
        String source = "AOS";
        String serviceCd = Constants.CODE.SERVICE_CD;

        try {

            // text data
            JSONObject req = new JSONObject();
            req.put("memberSeq", memberSeq);
            req.put("content", content);
            req.put("boardCd", boardCd);
            req.put("placeCd", placeCd);
            req.put("source", source);
            req.put("serviceCd", serviceCd);
            Log.e("sjjang", req.toString());

            RequestParams params = new RequestParams();
            params.setForceMultipartEntityContentType(true); // 파일처리

            params.put("param", req.toString());
            for (int i = 0; i < listPicture.size(); i++) {
                params.put("file" + i, listPicture.get(i).getFile());
            }

            String hostUrl = Constants.HOST_URL + Constants.HOST_DAO;
            String requestUrl = hostUrl + "/insertTimeLine.jsp";
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
                    // Log.e("sjjang", "onSuccess : " + new String(response).trim());
                    Utils.progressDialogDismiss();

                    try {
                        JSONObject json = new JSONObject(new String(response).trim());
                        boolean isSuccess = json.getBoolean("isSuccess");
                        if (isSuccess) {
                            setResult(RESULT_OK);
                            finish();
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
     * 글 수정
     */
    private void modifyContentProc() {

        String memberSeq = Constants.MEMBER_SEQ;
        String placeCd = listPlaceInfo.get(spPlaceInfo.getSelectedItemPosition()).getPlaceCd();
        String boardSeq = timeLine.getBoardSeq();
        String content = etWrite.getText().toString();
        content = content.replace("\n", "#n");
        String boardCd = "60002";
        String source = "AOS";

        // 사진 deleteSeq를 ,로 묶어서 보낸다.
        String attachSeq = "";
        for (String seq : listDeleteSeq) {
            attachSeq += seq + ",";
        }

        try {
            JSONObject req = new JSONObject();
            req.put("memberSeq", memberSeq);
            req.put("content", content);
            req.put("boardSeq", boardSeq);
            req.put("boardCd", boardCd);
            req.put("placeCd", placeCd);
            req.put("source", source);
            req.put("deleteAttachSeq", attachSeq);
            // Log.e("sjjang", req.toString());

            RequestParams params = new RequestParams();
            params.setForceMultipartEntityContentType(true);
            params.put("param", req.toString());

            // 파일이 null이 아닐경우에만 집어 넣는다.
            for (int i = 0; i < listPicture.size(); i++) {
                MPicture picture = listPicture.get(i);
                if (picture.getFile() != null) {
                    params.put("file" + i, picture.getFile());
                }
            }

            String hostUrl = Constants.HOST_URL + Constants.HOST_DAO;
            String requestUrl = hostUrl + "/updateTimeLine.jsp";
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
                            setResult(RESULT_OK);
                            finish();
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
     * 이미지 리스트에 이미지 세팅
     */
    private void setPictureFile(File file) {
        MPicture attach = new MPicture();
        attach.setAttachSeq(null);
        attach.setFile(file);
        listPicture.add(attach);
    }

    /*
    * 이미지 리스트
    * */
    private class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
        Context context;
        List<MPicture> items;

        public RecyclerAdapter(Context context, ArrayList<MPicture> items) {
            this.context = context;
            this.items = items;
        }

        @Override
        public int getItemCount() {
            return items == null ? 0 : items.size();
        }

        public void removeItem(int position) {

            // 사진 리스트에서 사진 제거
            MPicture picture = items.get(position);
            if (picture.getAttachSeq() != null) {
                // deleteSeq 만 따로 담아서 한거번에 보낸다.
                listDeleteSeq.add(picture.getAttachSeq());
            }

            items.remove(position);

            // recyclerView 아이템 제거
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount() - position);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.write_item_picture, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final MPicture item = items.get(position);

            holder.ivRemove.setColorFilter(ContextCompat.getColor(context, R.color.color_e6d7d7));

            int width = (Constants.SCREEN_WIDTH / 10) * 3;

            String attachSeq = item.getAttachSeq();
            File file = item.getFile();
            if (attachSeq != null) {
                // 서버 이미지
                String fileUrl = Constants.HOST_URL + item.getUrl();
                Picasso.with(context).load(fileUrl).resize(width, width).centerCrop().into(holder.ivPicture);
            } else if (file != null) {
                // 로컬 이미지
                Picasso.with(context).load(file).resize(width, width).centerCrop().into(holder.ivPicture);
            }

            holder.ivRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, getString(R.string.write_delete_long_click), Toast.LENGTH_SHORT).show();
                }
            });

            holder.ivRemove.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    removeItem(position);
                    return false;
                }
            });

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            RelativeLayout rlMain;
            ImageView ivPicture;
            ImageView ivRemove;

            public ViewHolder(View itemView) {
                super(itemView);
                rlMain = (RelativeLayout) itemView.findViewById(R.id.rlMain);
                ivPicture = (ImageView) itemView.findViewById(R.id.ivPicture);
                ivRemove = (ImageView) itemView.findViewById(R.id.ivRemove);
            }
        }
    }

    /**
     * 경기장 선택 어뎁터
     */
    public class PlaceSpinnerAdapter extends ArrayAdapter<MPlaceInfo> {

        private Context context;
        private int resId;
        private ArrayList<MPlaceInfo> items;

        public PlaceSpinnerAdapter(Context context, int resId, ArrayList<MPlaceInfo> items) {
            super(context, resId, items);
            this.context = context;
            this.resId = resId;
            this.items = items;
        }

        @Override
        public int getCount() {
            return items == null ? 0 : items.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if (convertView == null) {
                final MPlaceInfo item = items.get(position);

                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(resId, parent, false);
                holder = new ViewHolder();
                holder.tvPlaceName = (TextView) convertView.findViewById(R.id.tvPlaceName);
                holder.tvPlaceName.setText(item.getPlaceName());

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if (convertView == null) {

                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(resId, parent, false);
                holder = new ViewHolder();

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final MPlaceInfo item = items.get(position);
            holder.tvPlaceName = (TextView) convertView.findViewById(R.id.tvPlaceName);
            holder.tvPlaceName.setText(item.getPlaceName());

            // 선택된 값은 색 처리
            String selected = listPlaceInfo.get(spPlaceInfo.getSelectedItemPosition()).getPlaceCd();
            if(selected.equals(item.getPlaceCd())) {
                holder.tvPlaceName.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                holder.tvPlaceName.setTypeface(Typeface.DEFAULT_BOLD);
            } else {
                holder.tvPlaceName.setTextColor(ContextCompat.getColor(context, R.color.color_000000));
                holder.tvPlaceName.setTypeface(Typeface.DEFAULT);
            }

            if ("0".equals(item.getPlaceCd())) {
                // hint (센터선택)
                holder.tvPlaceName.setTextColor(ContextCompat.getColor(context, R.color.color_e6d7d7));
            }

            return convertView;
        }

        public class ViewHolder {
            TextView tvPlaceName;
        }
    }
}
