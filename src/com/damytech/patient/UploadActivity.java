package com.damytech.patient;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.*;
import android.widget.*;
import com.damytech.HttpConn.JsonHttpResponseHandler;
import com.damytech.commservice.CommMgr;
import com.damytech.utils.GlobalData;
import com.damytech.utils.ResolutionSet;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Calendar;

public class UploadActivity extends Activity {
    RelativeLayout mainLayout;
    boolean bInitialized = false;

    private Button btnUpload = null;
    private Button btnFeedback = null;
    private Button btnHelp = null;

    private EditText txtUploadDate = null;
    private EditText txtName;
    private EditText txtPhone;
    private ImageView imgMale;
    private TextView lblMale;
    private ImageView imgFemale;
    private TextView lblFemale;
    private ImageView imgPhoto1 = null;
    private ImageView imgPhoto2 = null;
    private TextView lblBirth = null;
    private TextView lblPhoto1 = null;
    private TextView lblPhoto2 = null;

    private int nPhotoNum = 0;

    int nGender = 0;
    int nYear = 0, nMonth = 0, nDay = 0;
    Calendar curdate = Calendar.getInstance();

    private JsonHttpResponseHandler handlerUpload;
    private ProgressDialog dialog;

    DatePickerDialog.OnDateSetListener dateListener =
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                    TextView lblText = (TextView) findViewById(R.id.lblUpload_Birthday);
                    nYear = year;
                    nMonth = month;
                    nDay = dayOfMonth;
                    lblText.setText(GlobalData.getDateFormat(UploadActivity.this, nYear, nMonth, nDay));
                }
            };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        mainLayout = (RelativeLayout)findViewById(R.id.rlUploadBack);
        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        if (bInitialized == false)
                        {
                            Rect r = new Rect();
                            mainLayout.getLocalVisibleRect(r);
                            ResolutionSet._instance.setResolution(r.width(), r.height());
                            ResolutionSet._instance.iterateChild(findViewById(R.id.rlUploadBack));
                            bInitialized = true;
                        }
                    }
                }
        );

        initComponent();
        initHandler();
    }

    private  void initComponent()
    {
        nYear = curdate.get(Calendar.YEAR);
        nMonth = curdate.get(Calendar.MONTH);
        nDay = curdate.get(Calendar.DAY_OF_MONTH);

        txtUploadDate = (EditText) findViewById(R.id.txtUpload_Date);
        txtUploadDate.setEnabled(false);
        txtUploadDate.setText(nYear + getString(R.string.nian) + GlobalData.toDoubleNum((nMonth+1)) + getString(R.string.yue) + GlobalData.toDoubleNum(nDay) + getString(R.string.ri));
        txtName = (EditText) findViewById(R.id.txtUpload_Name);
        txtPhone = (EditText) findViewById(R.id.txtUpload_Phone);

        imgMale = (ImageView) findViewById(R.id.imgUpload_Male);
        imgMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgMale.setImageResource(R.drawable.checked);
                imgFemale.setImageResource(R.drawable.unchecked);
                nGender = 0;
            }
        });

        lblMale = (TextView) findViewById(R.id.lblUpload_Male);
        lblMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgMale.setImageResource(R.drawable.checked);
                imgFemale.setImageResource(R.drawable.unchecked);
                nGender = 0;
            }
        });

        imgFemale = (ImageView) findViewById(R.id.imgUpload_Female);
        imgFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgMale.setImageResource(R.drawable.unchecked);
                imgFemale.setImageResource(R.drawable.checked);
                nGender = 1;
            }
        });

        lblFemale = (TextView) findViewById(R.id.lblUpload_Female);
        lblFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgMale.setImageResource(R.drawable.unchecked);
                imgFemale.setImageResource(R.drawable.checked);
                nGender = 1;
            }
        });

        lblPhoto1 = (TextView) findViewById(R.id.lblPhotoHint1);
        lblPhoto1.setVisibility(View.VISIBLE);
        lblPhoto2 = (TextView) findViewById(R.id.lblPhotoHint2);
        lblPhoto2.setVisibility(View.VISIBLE);

        btnUpload = (Button) findViewById(R.id.btnUpload_OK);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strName;
                strName = txtName.getText().toString();
                if (strName == null || strName.length() == 0)
                {
                    GlobalData.showToast(UploadActivity.this, getString(R.string.insertname));
                    return;
                }

                String strPhone;
                strPhone = txtPhone.getText().toString();
                if (strPhone.length() != GlobalData.PHONENUM_LENGTH)
                {
                    GlobalData.showToast(UploadActivity.this, getString(R.string.qingshurushoujihaoma));
                    return;
                }

                if (GlobalData.existPhoto1(UploadActivity.this) == false || GlobalData.existPhoto2(UploadActivity.this) == false)
                {
                    GlobalData.showToast(UploadActivity.this, getString(R.string.selectphoto));
                    return;
                }

                String strPhoto1 = "", strPhoto2 = "";
                try
                {
                    File file = new File(GlobalData.getPhotoPath1());
                    byte[] arrResult = new byte[(int)file.length()];
                    FileInputStream fis = new FileInputStream(file);
                    fis.read(arrResult);
                    fis.close();
                    strPhoto1 = Base64.encodeToString(arrResult, Base64.NO_WRAP);
                }
                catch (Exception ex)
                {
                    strPhoto1 = "";
                }
                try
                {
                    File file = new File(GlobalData.getPhotoPath2());
                    byte[] arrResult = new byte[(int)file.length()];
                    FileInputStream fis = new FileInputStream(file);
                    fis.read(arrResult);
                    fis.close();
                    strPhoto2 = Base64.encodeToString(arrResult, Base64.NO_WRAP);
                }
                catch (Exception ex)
                {
                    strPhoto2 = "";
                }

                if (strPhoto1 == null || strPhoto1.length() == 0)
                {
                    GlobalData.showToast(UploadActivity.this, getString(R.string.photoerror));
                    return;
                }
                if (strPhoto2 == null || strPhoto2.length() == 0)
                {
                    GlobalData.showToast(UploadActivity.this, getString(R.string.photoerror));
                    return;
                }

                dialog = ProgressDialog.show(
                        UploadActivity.this,
                        "",
                        getString(R.string.waiting),
                        true,
                        false,
                        null);
                String strBirth = "";
                strBirth = nYear + "-" + GlobalData.toDoubleNum((nMonth+1)) + "-" + GlobalData.toDoubleNum(nDay) + " 00:00:00";
                CommMgr.commService.AddDiagnInfo(handlerUpload, GlobalData.GetUID(UploadActivity.this), strName, strPhone, strBirth, nGender, strPhoto1, strPhoto2);
            }
        });

        btnFeedback = (Button) findViewById(R.id.btnUpload_Feedback);
        btnFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UploadActivity.this, FeedbackActivity.class);
                startActivity(intent);
                UploadActivity.this.finish();
            }
        });

        btnHelp = (Button) findViewById(R.id.btnUpload_Help);
        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UploadActivity.this, HelpActivity.class);
                startActivity(intent);
            }
        });

        imgPhoto1 = (ImageView) findViewById(R.id.imgUpload_Photo1);
        imgPhoto1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UploadActivity.this, VerPhotoActivity.class);
                startActivity(intent);
            }
        });

        imgPhoto2 = (ImageView) findViewById(R.id.imgUpload_Photo2);
        imgPhoto2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UploadActivity.this, HorPhotoActivity.class);
                intent.putExtra("PhotoNum", 2);
                startActivity(intent);
            }
        });

        lblBirth = (TextView) findViewById(R.id.lblUpload_Birthday);
        lblBirth.setText(GlobalData.getDateFormat(UploadActivity.this, nYear, nMonth, nDay));
        lblBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(UploadActivity.this, dateListener, nYear, nMonth, nDay).show();
            }
        });

        nPhotoNum = getIntent().getIntExtra("PhotoNum", 0);
        if (nPhotoNum == 0)
        {
            GlobalData.DeletePhotos(UploadActivity.this);
        }

        if (nPhotoNum > 0)
        {
            if (GlobalData.existPhoto1(UploadActivity.this))
            {
                String strPath = GlobalData.getPhotoPath1();
                if (strPath != null && strPath.length() > 0)
                {
//                    BitmapDrawable bmd = GlobalData.getClockWite90Image(strPath);
//                    if (bmd != null)
//                        imgPhoto1.setImageDrawable(bmd);
//                    else
                        imgPhoto1.setImageURI(Uri.parse(strPath));

                    lblPhoto1.setVisibility(View.INVISIBLE);
                }
            }

            if (GlobalData.existPhoto2(UploadActivity.this))
            {
                String strPath = GlobalData.getPhotoPath2();
                if (strPath != null && strPath.length() > 0)
                {
                    imgPhoto2.setImageURI(Uri.parse(strPath));
                    lblPhoto2.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    private void initHandler()
    {
        handlerUpload = new JsonHttpResponseHandler()
        {
            int result = 0;

            @Override
            public void onSuccess(JSONObject jsonData)
            {
                result = 1;

                dialog.dismiss();
                long nRet = CommMgr.commService.parseAddDiagnInfo(jsonData);
                if (nRet == 0)
                {
                    Intent intent = new Intent(UploadActivity.this, FeedbackActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                    result = 2;
            }

            @Override
            public void onFailure(Throwable ex, String exception)
            {
                dialog.dismiss();
            }

            @Override
            public void onFinish()
            {
                if (result == 2)
                    GlobalData.showToast(UploadActivity.this, getString(R.string.service_error));
                else if (result == 0)
                    GlobalData.showToast(UploadActivity.this, getString(R.string.network_error));
            }
        };

        return;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(UploadActivity.this, LoginActivity.class);
            startActivity(intent);
            UploadActivity.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
