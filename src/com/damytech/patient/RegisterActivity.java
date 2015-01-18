package com.damytech.patient;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.*;
import com.damytech.HttpConn.JsonHttpResponseHandler;
import com.damytech.commservice.CommMgr;
import com.damytech.utils.GlobalData;
import com.damytech.utils.ResolutionSet;
import org.json.JSONObject;

import java.util.Calendar;

public class RegisterActivity extends Activity {
    RelativeLayout mainLayout;
    boolean bInitialized = false;
    final int PASS_LENGTH = 6;

    int nGender = 0;
    int nYear = 0, nMonth = 0, nDay = 0;
    Calendar curdate = Calendar.getInstance();

    private EditText txtName = null;
    private ImageView imgMale = null;
    private ImageView imgFemale = null;
    private TextView lblMale = null;
    private TextView lblFemale = null;
    private EditText txtBirth = null;
    private EditText txtAddress = null;
    private EditText txtPhoneNum = null;
    private EditText txtPassword = null;
    private EditText txtRePassword = null;
    private EditText txtRelDisease = null;
    private Button btnOK = null;

    private JsonHttpResponseHandler handlerRegister = null;
    private ProgressDialog dialog;

    DatePickerDialog.OnDateSetListener dateListener =
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                    EditText editText = (EditText) findViewById(R.id.txtRegister_Birth);
                    nYear = year;
                    nMonth = month;
                    nDay = dayOfMonth;
                    editText.setText(GlobalData.getDateFormat(RegisterActivity.this, nYear, nMonth, nDay));
                }
            };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mainLayout = (RelativeLayout)findViewById(R.id.rlRegisterBack);
        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        if (bInitialized == false)
                        {
                            Rect r = new Rect();
                            mainLayout.getLocalVisibleRect(r);
                            ResolutionSet._instance.setResolution(r.width(), r.height());
                            ResolutionSet._instance.iterateChild(findViewById(R.id.rlRegisterBack));
                            bInitialized = true;
                        }
                    }
                }
        );

        initComponent();
        initHandler();}

    private void initComponent()
    {
        nYear = curdate.get(Calendar.YEAR);
        nMonth = curdate.get(Calendar.MONTH);
        nDay = curdate.get(Calendar.DAY_OF_MONTH);

        txtName = (EditText) findViewById(R.id.txtRegister_Name);
        imgMale = (ImageView) findViewById(R.id.imgRegister_Male);
        imgMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgMale.setImageResource(R.drawable.checked);
                imgFemale.setImageResource(R.drawable.unchecked);
                nGender = 0;
            }
        });
        imgFemale = (ImageView) findViewById(R.id.imgRegister_Female);
        imgFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgMale.setImageResource(R.drawable.unchecked);
                imgFemale.setImageResource(R.drawable.checked);
                nGender = 1;
            }
        });
        lblMale = (TextView) findViewById(R.id.lblRegister_Male);
        lblMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgMale.setImageResource(R.drawable.checked);
                imgFemale.setImageResource(R.drawable.unchecked);
                nGender = 0;
            }
        });
        lblFemale = (TextView) findViewById(R.id.lblRegister_Female);
        lblFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgMale.setImageResource(R.drawable.unchecked);
                imgFemale.setImageResource(R.drawable.checked);
                nGender = 1;
            }
        });
        txtBirth = (EditText) findViewById(R.id.txtRegister_Birth);
        txtBirth.setText(GlobalData.getDateFormat(RegisterActivity.this, nYear, nMonth, nDay));
        txtBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(RegisterActivity.this, dateListener, nYear, nMonth, nDay).show();
            }
        });
        txtAddress = (EditText) findViewById(R.id.txtRegister_Addr);
        txtPhoneNum = (EditText) findViewById(R.id.txtRegister_Phone);
        txtPassword = (EditText) findViewById(R.id.txtRegister_Password);
        txtRePassword = (EditText) findViewById(R.id.txtRegister_RePassword);
        txtRelDisease = (EditText) findViewById(R.id.txtRegister_Ill);
        btnOK = (Button) findViewById(R.id.btnRegister_OK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strName, strAddr, strPhoneNum, strPassword, strRePassword, strIll;

                strName = txtName.getText().toString();
                if (strName == null || strName.length() == 0)
                {
                    GlobalData.showToast(RegisterActivity.this, getString(R.string.insertname));
                    return;
                }

                strAddr = txtAddress.getText().toString();
                strPhoneNum = txtPhoneNum.getText().toString();
                if (strPhoneNum == null || strPhoneNum.length() == 0)
                {
                    GlobalData.showToast(RegisterActivity.this, getString(R.string.insertphonenum));
                    return;
                }

                strPassword = txtPassword.getText().toString();
                if (strPassword == null || strPassword.length() == 0)
                {
                    GlobalData.showToast(RegisterActivity.this, getString(R.string.insertpassword));
                    return;
                }

                if (strPassword.length() < PASS_LENGTH)
                {
                    GlobalData.showToast(RegisterActivity.this, getString(R.string.passlength_error));
                    return;
                }

                strRePassword = txtRePassword.getText().toString();
                if (strRePassword == null || strRePassword.length() == 0)
                {
                    GlobalData.showToast(RegisterActivity.this, getString(R.string.insertrepassword));
                    return;
                }

                if (strPassword.equals(strRePassword) == false)
                {
                    GlobalData.showToast(RegisterActivity.this, getString(R.string.notmatchpassword));
                    return;
                }

                strIll = txtRelDisease.getText().toString();

                dialog = ProgressDialog.show(
                        RegisterActivity.this,
                        "",
                        getString(R.string.waiting),
                        true,
                        false,
                        null);

                String strBirth = "";
                strBirth = nYear + "-" + GlobalData.toDoubleNum((nMonth+1)) + "-" + GlobalData.toDoubleNum(nDay) + " 00:00:00";
                CommMgr.commService.RegisterPatient(handlerRegister, strName, nGender, strBirth, strAddr, strPhoneNum, strPassword, strIll);
            }
        });
    }

    private void initHandler()
    {
        handlerRegister = new JsonHttpResponseHandler()
        {
            int result = 0;

            @Override
            public void onSuccess(JSONObject jsonData)
            {
                result = 1;

                dialog.dismiss();
                long nRet = CommMgr.commService.parseRegisterPatient(jsonData);
                if (nRet == 0)
                {
                    GlobalData.showToast(RegisterActivity.this, getString(R.string.registersucess));
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    RegisterActivity.this.finish();
                }
                else if (nRet == 100)
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
                    GlobalData.showToast(RegisterActivity.this, getString(R.string.service_error));
                else if (result == -1)
                    GlobalData.showToast(RegisterActivity.this, getString(R.string.existphonenum));
                else if (result == 0)
                    GlobalData.showToast(RegisterActivity.this, getString(R.string.network_error));
            }
        };
    }
}
