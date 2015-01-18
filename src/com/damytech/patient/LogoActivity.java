package com.damytech.patient;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import com.damytech.utils.GlobalData;
import com.damytech.utils.ResolutionSet;

public class LogoActivity extends Activity {
    RelativeLayout mainLayout;
    boolean bInitialized = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        mainLayout = (RelativeLayout)findViewById(R.id.rlLogoBack);
        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        if (bInitialized == false)
                        {
                            Rect r = new Rect();
                            mainLayout.getLocalVisibleRect(r);
                            ResolutionSet._instance.setResolution(r.width(), r.height());
                            ResolutionSet._instance.iterateChild(findViewById(R.id.rlLogoBack));
                            bInitialized = true;
                        }
                    }
                }
        );

        Handler handler= new Handler() {
            public void handleMessage(Message msg){
                SharedPreferences pref = getSharedPreferences(GlobalData.strPrefName, MODE_PRIVATE);
                boolean bRet = pref.getBoolean(GlobalData.strPref_Help, false);

                if (bRet == false)
                {
                    startActivity(new Intent(LogoActivity.this, CopywriteActivity.class));
                }
                else
                {
                    startActivity(new Intent(LogoActivity.this, LoginActivity.class));
                }
                LogoActivity.this.finish();
            }
        };

        handler.sendEmptyMessageDelayed(0, 2000);
    }
}
