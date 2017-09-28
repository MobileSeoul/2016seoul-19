package com.hour24.toysrental.service.center.view;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hour24.toysrental.R;
import com.hour24.toysrental.common.CharSearchUtil;
import com.hour24.toysrental.common.Constants;
import com.hour24.toysrental.common.Utils;
import com.hour24.toysrental.common.view.floatingbutton.FloatingActionButton;
import com.hour24.toysrental.service.center.model.MCenter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.util.CharsetUtils;


public class CenterMain extends AppCompatActivity implements View.OnClickListener {

    private Context context;

    private LinearLayout llSearchMain;
    private EditText etSearch;

    private FloatingActionButton fabSearch;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private RecyclerAdapter recyclerAdapter;

    private ArrayList<MCenter> listCenter;
    private ArrayList<MCenter> listCenterSearch;

    private boolean isScrolled = false;
    private boolean isSearchOpen = false;

    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_exit);
        setContentView(R.layout.center);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("장난감 대여");
        context = CenterMain.this;

        // list 초기화
        listCenter = new ArrayList<MCenter>();
        listCenterSearch = new ArrayList<MCenter>();
        imm = (InputMethodManager) getSystemService(context.INPUT_METHOD_SERVICE);

        initLayout();
        getDataCenter();

    }

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.slide_exit, R.anim.slide_in_left);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isSearchOpen) {
            etSearch.requestFocus();
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.fabSearch:
                if (isSearchOpen) {
                    // 검색창 닫기
                    llSearchMain.setVisibility(View.GONE);
                    fabSearch.setIcon(R.drawable.ic_search_black_24dp);

                    // 키보드 내리기
                    imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);

                    // 원래 항목 세팅
                    recyclerAdapter = new RecyclerAdapter(context, listCenter);
                    recyclerView.setAdapter(recyclerAdapter);

                    // 검색항목 초기화
                    etSearch.setText("");
                } else {
                    // 검색창 열기
                    llSearchMain.setVisibility(View.VISIBLE);
                    fabSearch.setIcon(R.drawable.ic_clear_black_24dp);

                    // edittext 포커스, 키보드 올리기
                    etSearch.requestFocus();
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                }
                isSearchOpen = !isSearchOpen;
                break;

        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.CODE.REQUEST_SEARCH) {
            if (data != null) {

            }
        }
    }

    /**
     * 레이아웃 초기화
     */
    private void initLayout() {

        llSearchMain = (LinearLayout) findViewById(R.id.llSearchMain);
        etSearch = (EditText) findViewById(R.id.etSearch);
        fabSearch = (FloatingActionButton) findViewById(R.id.fabSearch);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        fabSearch.setOnClickListener(this);

        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                // 스크롤을 올렸을때 fab 를 보이게 한다.
                if (!isScrolled) {
                    fabSearch.setVisibility(View.VISIBLE);
                } else {
                    fabSearch.setVisibility(View.GONE);

                    // 키보드 내리기
                    imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    isScrolled = true;
                    fabSearch.setVisibility(View.GONE);

                    // 키보드 내리기
                    imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
                } else {
                    isScrolled = false;
                    fabSearch.setVisibility(View.VISIBLE);
                }
            }
        });

        // 실시간 검색
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable edit) {
                // 계속 초기화를 시켜줘야 한다.
                listCenterSearch = new ArrayList<MCenter>();

                String search = edit.toString();
                for (int i = 0; i < listCenter.size(); i++) {
                    String centerName = listCenter.get(i).getCenterName(); // 센터이름
                    String placeName = listCenter.get(i).getPlaceName(); // 지역구
                    if (CharSearchUtil.matchString(centerName, search)) {
                        // 센터이름 검색
                        listCenterSearch.add((listCenter.get(i)));
                    } else if (CharSearchUtil.matchString(placeName, search)) {
                        // 지역구 검색
                        listCenterSearch.add((listCenter.get(i)));
                    }

                    recyclerAdapter = new RecyclerAdapter(context, listCenterSearch);
                    recyclerView.setAdapter(recyclerAdapter);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        };
        etSearch.addTextChangedListener(textWatcher);
    }

    /*
   * 데이터 가져옴
   * */
    public void getDataCenter() {
        try {

            JSONObject req = new JSONObject();

            RequestParams params = new RequestParams();
            params.put("param", req.toString());
            Log.e("sjjang", "params : " + params);

            String hostUrl = Constants.HOST_URL + Constants.HOST_DAO_TOY;
            String requestUrl = hostUrl + "/selectCenter.jsp";
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

                    recyclerAdapter = new RecyclerAdapter(context, listCenter);
                    recyclerView.setAdapter(recyclerAdapter);
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

                JSONArray result = json.getJSONArray("RESULT");

                if (result.length() > 0) {
                    for (int i = 0; i < result.length(); i++) {

                        JSONObject data = result.getJSONObject(i);

                        MCenter center = new MCenter();
                        center.setCenterSeq(data.getString("CENTER_SEQ"));
                        center.setCenterName(data.getString("CENTER_NAME"));
                        center.setPlaceName(data.getString("PLACE_NAME"));
                        center.setAddrStreet(data.getString("ADDR_STREET"));
                        center.setAddrLocal(data.getString("ADDR_LOCAL"));
                        center.setAddrEtc(data.getString("ADDR_ETC"));
                        center.setTel(data.getString("TEL"));
                        center.setUrl(data.getString("URL"));
                        center.setUrlToySearch(data.getString("URL_TOY_SEARCH"));
                        center.setHoliDay(data.getString("HOLIDAY"));
                        center.setAbleDay(data.getString("ABLEDAY"));
                        center.setLatitude(data.getString("LATITUDE"));
                        center.setLongitude(data.getString("LONGITUDE"));

                        listCenter.add(center);
                    }
                }
            } else {
                Toast.makeText(context, json.getString("message"), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, getString(R.string.toast_data_proc_fail), Toast.LENGTH_SHORT).show();
        }
    }

    /*
    * 타임라인 어뎁터
    * */
    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

        Context context;
        List<MCenter> items;

        public RecyclerAdapter(Context context, ArrayList<MCenter> items) {
            this.context = context;
            this.items = items;
        }

        @Override
        public int getItemCount() {
            return items == null ? 0 : items.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.center_item, parent, false);
            return new ViewHolder(v);
        }

        public void removeItem(int position) {
            this.items.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount() - position);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final MCenter item = items.get(position);

            holder.tvCenterName.setText(item.getCenterName());
            holder.tvAddrStreet.setText(item.getAddrStreet());
            holder.tvAbleDay.setText("이용일 : " + item.getAbleDay());
            holder.tvHoliDay.setText("휴관일 : " + item.getHoliDay());

            holder.llMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);

                    Intent intent = new Intent(context, CenterDetail.class);
                    intent.putExtra("center", item);
                    startActivity(intent);
                }
            });

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            LinearLayout llMain;

            TextView tvCenterName;
            TextView tvAddrStreet;
            TextView tvAbleDay;
            TextView tvHoliDay;

            public ViewHolder(View itemView) {
                super(itemView);

                llMain = (LinearLayout) itemView.findViewById(R.id.llMain);

                tvCenterName = (TextView) itemView.findViewById(R.id.tvCenterName);
                tvAddrStreet = (TextView) itemView.findViewById(R.id.tvAddrStreet);
                tvAbleDay = (TextView) itemView.findViewById(R.id.tvAbleDay);
                tvHoliDay = (TextView) itemView.findViewById(R.id.tvHoliDay);
            }
        }
    }
}