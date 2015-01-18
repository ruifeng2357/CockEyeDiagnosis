package com.damytech.patient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.*;
import com.damytech.HttpConn.JsonHttpResponseHandler;
import com.damytech.STData.STDiagnDetail;
import com.damytech.commservice.CommMgr;
import com.damytech.utils.GlobalData;
import com.damytech.utils.ResolutionSet;
import com.damytech.utils.SmartImageView.SmartImageView;
import org.json.JSONObject;

public class FeedbackDetailActivity extends Activity {
    RelativeLayout mainLayout;
    boolean bInitialized = false;

    private ImageView imgBack;
    private SmartImageView imgPhoto1;
    private SmartImageView imgPhoto2;
    private TextView lblName;
    private TextView lblDate;
    private EditText txtFeeback;

    private long nID = 0;
    private JsonHttpResponseHandler handler = null;
    private ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedbackdetail);

        nID = getIntent().getLongExtra("UID", 0);

        mainLayout = (RelativeLayout)findViewById(R.id.rlFeedbackDetailBack);
        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        if (bInitialized == false)
                        {
                            Rect r = new Rect();
                            mainLayout.getLocalVisibleRect(r);
                            ResolutionSet._instance.setResolution(r.width(), r.height());
                            ResolutionSet._instance.iterateChild(findViewById(R.id.rlFeedbackDetailBack));
                            bInitialized = true;
                        }
                    }
                }
        );

        initComponent();
        initHandler();
        Runbackground();
    }

    private void Runbackground()
    {
        if (nID == 0)
            finish();

        dialog = ProgressDialog.show(
                FeedbackDetailActivity.this,
                "",
                getString(R.string.waiting),
                true,
                false,
                null);
        CommMgr.commService.GetDiagnDetail(handler, (int)GlobalData.GetUID(FeedbackDetailActivity.this), (int)nID);

        return;
    }

    private void initComponent ()
    {
        imgBack = (ImageView) findViewById(R.id.imgFeedbackDetail_Back);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imgPhoto1 = (SmartImageView) findViewById(R.id.imgFeedbackDeatil_Photo1);
        imgPhoto2 = (SmartImageView) findViewById(R.id.imgFeedbackDeatil_Photo2);
        lblName = (TextView) findViewById(R.id.lblFeedbackDetail_NameValue);
        lblDate = (TextView) findViewById(R.id.lblFeedbackDetail_DateValue);
        txtFeeback = (EditText) findViewById(R.id.txtFeedbackDetail_ResValue);

        return;
    }

    private void initHandler()
    {
        handler = new JsonHttpResponseHandler()
        {
            int result = 0;

            @Override
            public void onSuccess(JSONObject jsonData)
            {
                result = 1;
                dialog.dismiss();

                STDiagnDetail detailInfo = new STDiagnDetail();
                long nRet = CommMgr.commService.parseGetDiagnDetail(jsonData, detailInfo);
                if (nRet == 0)
                {
                    imgPhoto1.setImageUrl(detailInfo.imgpath1, R.drawable.defaultimgback);
                    imgPhoto2.setImageUrl(detailInfo.imgpath2, R.drawable.defaultimgback);
                    lblName.setText(detailInfo.name);
                    lblDate.setText(detailInfo.diagndate);
                    txtFeeback.setText(detailInfo.feedback);
                }
                else if (nRet == 110)
                    result = -1;
                else
                    result = 2;
            }

            @Override
            public void onFailure(Throwable ex, String exception)
            {
            }

            @Override
            public void onFinish()
            {
                dialog.dismiss();
                if (result == 2)
                    GlobalData.showToast(FeedbackDetailActivity.this, getString(R.string.service_error));
                else if (result == -1)
                    GlobalData.showToast(FeedbackDetailActivity.this, getString(R.string.noexistuser));
                else if (result == 0)
                    GlobalData.showToast(FeedbackDetailActivity.this, getString(R.string.network_error));

                if (result != 1)
                    finish();
            }
        };

        return;
    }
}
