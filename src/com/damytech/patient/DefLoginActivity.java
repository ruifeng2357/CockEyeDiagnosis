package com.damytech.patient;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.*;
import com.damytech.utils.GlobalData;
import com.damytech.utils.ResolutionSet;

public class DefLoginActivity extends Activity {
    RelativeLayout mainLayout;
    boolean bInitialized = false;

    static long back_pressed = 0;

    private Button btnShutWithoutLogin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deflogin);

        mainLayout = (RelativeLayout)findViewById(R.id.rlDefLoginBack);
        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        if (bInitialized == false)
                        {
                            Rect r = new Rect();
                            mainLayout.getLocalVisibleRect(r);
                            ResolutionSet._instance.setResolution(r.width(), r.height());
                            ResolutionSet._instance.iterateChild(findViewById(R.id.rlDefLoginBack));
                            bInitialized = true;
                        }
                    }
                }
        );

        initComponent();
    }

    private void initComponent ()
    {
        btnShutWithoutLogin = (Button) findViewById(R.id.btnDefLogin);
        btnShutWithoutLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean bFlag = false;
                SharedPreferences prefOld = getSharedPreferences(GlobalData.strPrefName, MODE_PRIVATE);
                bFlag = prefOld.getBoolean(GlobalData.strPref_Help, false);
                if (bFlag == false)
                {
                    SharedPreferences pref = getSharedPreferences(GlobalData.strPrefName, MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean(GlobalData.strPref_Help, true);
                    editor.commit();

                    Intent intent = new Intent(DefLoginActivity.this, VerPhotoActivity.class);
                    startActivity(intent);
                    DefLoginActivity.this.finish();
                }
            }
        });

        return;
    }

    @Override
    public void onBackPressed(){
        if (back_pressed + 2000 > System.currentTimeMillis()){
            super.onBackPressed();
        }
        else{
            GlobalData.showToast(DefLoginActivity.this, getString(R.string.exitapp));
            back_pressed = System.currentTimeMillis();
        }
    }
}
