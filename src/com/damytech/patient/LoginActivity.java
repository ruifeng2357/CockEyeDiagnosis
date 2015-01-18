package com.damytech.patient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.*;
import com.damytech.HttpConn.JsonHttpResponseHandler;
import com.damytech.STData.STUserID;
import com.damytech.commservice.CommMgr;
import com.damytech.utils.GlobalData;
import com.damytech.utils.ResolutionSet;
import com.damytech.utils.SmartImageView.Global;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

public class LoginActivity extends Activity {
    RelativeLayout mainLayout;
    boolean bInitialized = false;

    private static long back_pressed;

    private TextView lblRememberPassword = null;
    private ImageView imgRememberPassword = null;
    private EditText txtPhoneNum;
    private EditText txtPassword;
    private Button btnRegister;
    private Button btnLogin;
    private Button btnShutWithoutLogin;

    private boolean bRememberPassword = false;

    private JsonHttpResponseHandler handlerLogin;
    private ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mainLayout = (RelativeLayout)findViewById(R.id.rlLoginBack);
        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        if (bInitialized == false)
                        {
                            Rect r = new Rect();
                            mainLayout.getLocalVisibleRect(r);
                            ResolutionSet._instance.setResolution(r.width(), r.height());
                            ResolutionSet._instance.iterateChild(findViewById(R.id.rlLoginBack));
                            bInitialized = true;
                        }
                    }
                }
        );

        GlobalData.SetUID(LoginActivity.this, 0);

        initComponent();
        initHandler();
    }

    private void initComponent ()
    {
        bRememberPassword = GlobalData.GetPWDFlag(LoginActivity.this);

        imgRememberPassword = (ImageView) findViewById(R.id.imgLogin_RememberPassword);
        if (bRememberPassword)
            imgRememberPassword.setImageResource(R.drawable.checked);
        else
            imgRememberPassword.setImageResource(R.drawable.unchecked);
        imgRememberPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bRememberPassword == true)
                {
                    imgRememberPassword.setImageResource(R.drawable.unchecked);
                    bRememberPassword = false;
                }
                else
                {
                    imgRememberPassword.setImageResource(R.drawable.checked);
                    bRememberPassword = true;
                }
            }
        });

        lblRememberPassword = (TextView) findViewById(R.id.lblLogin_RememberPassword);
        lblRememberPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bRememberPassword == true)
                {
                    imgRememberPassword.setImageResource(R.drawable.unchecked);
                    bRememberPassword = false;
                }
                else
                {
                    imgRememberPassword.setImageResource(R.drawable.checked);
                    bRememberPassword = true;
                }
            }
        });

        txtPhoneNum = (EditText) findViewById(R.id.txtLogin_UserName);
        txtPassword = (EditText) findViewById(R.id.txtLogin_Password);
        if (GlobalData.GetPWDFlag(LoginActivity.this))
        {
            txtPhoneNum.setText(GlobalData.GetPhoneNum(LoginActivity.this));
            txtPassword.setText(GlobalData.GetPWD(LoginActivity.this));
        }

        btnRegister = (Button) findViewById(R.id.btnLogin_Register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btnLogin = (Button) findViewById(R.id.btnLogin_Login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strPhoneNum, strPassword;

                strPhoneNum = txtPhoneNum.getText().toString();
                if (strPhoneNum == null || strPhoneNum.length() == 0)
                {
                    GlobalData.showToast(LoginActivity.this, getString(R.string.insertphonenum));
                    return;
                }

                strPassword = txtPassword.getText().toString();
                if (strPassword == null || strPassword.length() == 0)
                {
                    GlobalData.showToast(LoginActivity.this, getString(R.string.insertpassword));
                    return;
                }

//                Intent intent = new Intent(LoginActivity.this, UploadActivity.class);
//                startActivity(intent);
//                LoginActivity.this.finish();
//                return;

                dialog = ProgressDialog.show(
                        LoginActivity.this,
                        "",
                        getString(R.string.waiting),
                        true,
                        false,
                        null);
                CommMgr.commService.LoginPatient(handlerLogin, strPhoneNum, strPassword);
            }
        });

        btnShutWithoutLogin = (Button) findViewById(R.id.btnLogin_UnLogin);
        btnShutWithoutLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Calendar thatDay = Calendar.getInstance();
//                thatDay.set(Calendar.DAY_OF_MONTH, 1);
//                thatDay.set(Calendar.MONTH, 11); // 0-11 so 1 less
//                thatDay.set(Calendar.YEAR, 2014);
//
//                Calendar today = Calendar.getInstance();
//
//                long diff = today.getTimeInMillis() - thatDay.getTimeInMillis();
//                if (diff >= 0)
//                {
//                    GlobalData.showToast(LoginActivity.this, getString(R.string.error_trialversion));
//                    return;
//                }

                Intent intent = new Intent(LoginActivity.this, HorPhotoActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            }
        });

        return;
    }

    private void initHandler()
    {
        handlerLogin = new JsonHttpResponseHandler()
        {
            int result = 0;

            @Override
            public void onSuccess(JSONObject jsonData)
            {
                result = 1;

                dialog.dismiss();
                STUserID userID = new STUserID();
                long nRet = CommMgr.commService.parseLoginPatient(jsonData, userID);
                if (nRet == 0)
                {
                    GlobalData.SetUID(LoginActivity.this, Long.parseLong(userID.uid));

                    if (bRememberPassword == true)
                    {
                        GlobalData.SetPWDFlag(LoginActivity.this, bRememberPassword);
                        GlobalData.SetPhoneNum(LoginActivity.this, txtPhoneNum.getText().toString());
                        GlobalData.SetPWD(LoginActivity.this, txtPassword.getText().toString());
                    }
                    else
                        GlobalData.SetPWDFlag(LoginActivity.this, false);

                    Intent intent = new Intent(LoginActivity.this, UploadActivity.class);
                    startActivity(intent);
                    LoginActivity.this.finish();
                }
                else if (nRet == 110)
                    result = -1;
                else if (nRet == -1)
                    result = -2;
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
                    GlobalData.showToast(LoginActivity.this, getString(R.string.service_error));
                else if (result == -1)
                    GlobalData.showToast(LoginActivity.this, getString(R.string.noexistuser));
                else if (result == -2)
                    GlobalData.showToast(LoginActivity.this, getString(R.string.error_trialversion));
                else if (result == 0)
                    GlobalData.showToast(LoginActivity.this, getString(R.string.network_error));
            }
        };

        return;
    }

    @Override
    public void onBackPressed(){
        if (back_pressed + 2000 > System.currentTimeMillis()){
            super.onBackPressed();
        }
        else{
            GlobalData.showToast(LoginActivity.this, getString(R.string.exitapp));
            back_pressed = System.currentTimeMillis();
        }
    }
}
