package com.damytech.patient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.*;
import android.widget.*;
import com.damytech.HttpConn.JsonHttpResponseHandler;
import com.damytech.STData.STDiagnInfo;
import com.damytech.commservice.CommMgr;
import com.damytech.utils.GlobalData;
import com.damytech.utils.PullRefreshListView.PullToRefreshBase;
import com.damytech.utils.PullRefreshListView.PullToRefreshListView;
import com.damytech.utils.ResolutionSet;
import org.json.JSONObject;

import java.util.ArrayList;

public class FeedbackActivity extends Activity {
    RelativeLayout mainLayout;
    boolean bInitialized = false;

    private Button btnUpload = null;

    private final static int ITEMCOUNT_PER_PAGE = 10;
    boolean bExistNext = true;
    private int nRequestPageNo = 0;
    private PullToRefreshListView mListData;
    private ListView mRealListView;
    private FeedbakDataAdapter mFeedbackDataAdapter;

    private ArrayList<STDiagnInfo> m_arrDiagnItems = new ArrayList<STDiagnInfo>();
    private JsonHttpResponseHandler handlerDiagnList = null;
    private ProgressDialog progDialog = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        mainLayout = (RelativeLayout)findViewById(R.id.rlFeedbackBack);
        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        if (bInitialized == false)
                        {
                            Rect r = new Rect();
                            mainLayout.getLocalVisibleRect(r);
                            ResolutionSet._instance.setResolution(r.width(), r.height());
                            ResolutionSet._instance.iterateChild(findViewById(R.id.rlFeedbackBack));
                            bInitialized = true;
                        }
                    }
                }
        );

        initComponent();
        initHandler();

        RunBackgroundHandler();
    }

    private void RunBackgroundHandler()
    {
        try {
            mRealListView.setAdapter(null);
        } catch(Exception ex) {}

        nRequestPageNo = 0;
        progDialog = ProgressDialog.show(
                FeedbackActivity.this,
                "",
                getString(R.string.waiting),
                true,
                false,
                null);
        CommMgr.commService.GetDiagnList(handlerDiagnList, (int)GlobalData.GetUID(FeedbackActivity.this), nRequestPageNo);

        return;
    }

    private void initComponent()
    {
        btnUpload = (Button) findViewById(R.id.btnFeedback_Upload);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedbackActivity.this, UploadActivity.class);
                startActivity(intent);
                FeedbackActivity.this.finish();
            }
        });

        mListData = (PullToRefreshListView) findViewById(R.id.listFeedback_Datas);
        mListData.setMode(PullToRefreshBase.Mode.PULL_FROM_END);

        m_arrDiagnItems.clear();
        mListData.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                nRequestPageNo = nRequestPageNo + 1;

                CommMgr.commService.GetDiagnList(handlerDiagnList, (int)GlobalData.GetUID(FeedbackActivity.this), nRequestPageNo);
            }
        });

        mListData.setOnLastItemVisibleListener( new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {}
        });

        mRealListView = mListData.getRefreshableView();
    }

    public void initHandler()
    {
        handlerDiagnList = new JsonHttpResponseHandler()
        {
            ArrayList<STDiagnInfo>  diagnInfos = new ArrayList<STDiagnInfo>();
            int result = 0;

            @Override
            public void onSuccess(JSONObject jsonData)
            {
                progDialog.dismiss();
                mListData.onRefreshComplete();
                result = 1;
                CommMgr.commService.parseGetDiagnList(jsonData, diagnInfos);

                for ( int i = 0; i < diagnInfos.size(); i++ )
                {
                    m_arrDiagnItems.add(diagnInfos.get(i));
                }

                initContents();
            }

            @Override
            public void onFailure(Throwable ex, String exception)
            {
                progDialog.dismiss();
            }

            @Override
            public void onFinish()
            {
                result = 0;

            }
        };
    }

    private void initContents()
    {
        if (mRealListView != null) {
            getShowListFromData();
        }
    }

    private void getShowListFromData()
    {
        if (m_arrDiagnItems == null)
            return;

        if (m_arrDiagnItems.size() % ITEMCOUNT_PER_PAGE == 0)
        {
            bExistNext = true;
            mListData.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        }
        else
        {
            bExistNext = false;
            mListData.setMode(PullToRefreshBase.Mode.DISABLED);
        }

        if (mRealListView != null)
        {
            mRealListView.setCacheColorHint(Color.TRANSPARENT);
            mRealListView.setDivider(new ColorDrawable(Color.TRANSPARENT));
            mRealListView.setDividerHeight(2);

            mFeedbackDataAdapter = new FeedbakDataAdapter(FeedbackActivity.this, 0, m_arrDiagnItems);
            mRealListView.setAdapter(mFeedbackDataAdapter);
        }
    }

    class FeedbakDataAdapter extends ArrayAdapter<STDiagnInfo>
    {
        ArrayList<STDiagnInfo> list;
        Context ctx;

        public FeedbakDataAdapter(Context ctx, int resourceId, ArrayList<STDiagnInfo> list) {
            super(ctx, resourceId, list);
            this.ctx = ctx;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null)
            {
                LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.view_feedbackitem, null);
                ResolutionSet._instance.iterateChild(v.findViewById(R.id.rlFeedbackItemBack));
            }

            TextView lblDate = (TextView) v.findViewById(R.id.lblFeedBack_Date);
            lblDate.setText(GetShowDate(list.get(position).regtime));
            TextView lblName = (TextView) v.findViewById(R.id.lblFeedBack_NameValue);
            lblName.setText(list.get(position).name);
            TextView lblFeedback = (TextView) v.findViewById(R.id.lblFeedBack_FeedBackValue);
            if (list.get(position).isdiagn == 0)
                lblFeedback.setText(ctx.getString(R.string.zanwu));
            else
                lblFeedback.setText(list.get(position).feedback);
            RelativeLayout rlDetail = (RelativeLayout) v.findViewById(R.id.rlFeedback_Detail);
            if (list.get(position).isdiagn == 1)
            {
                rlDetail.setTag(list.get(position).id);
                rlDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Long nID = (Long) v.getTag();
                        if (nID.longValue() > 0)
                        {
                            Intent intent = new Intent(FeedbackActivity.this, FeedbackDetailActivity.class);
                            intent.putExtra("UID", nID.longValue());
                            startActivity(intent);
                        }

                    }
                });
            }

            return v;
        }
    }

    private String GetShowDate(String strDate)
    {
        String strRet = "";

        int nPos = strDate.indexOf("-");
        if (nPos > 0)
        {
            strRet = strDate.substring(0, nPos) + "\n" + strDate.substring(nPos+1, strDate.length());
        }

        return strRet;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(FeedbackActivity.this, UploadActivity.class);
            startActivity(intent);
            FeedbackActivity.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
