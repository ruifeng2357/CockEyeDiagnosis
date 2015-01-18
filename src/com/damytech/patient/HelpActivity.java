package com.damytech.patient;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.*;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.RelativeLayout;
import com.damytech.utils.GlobalData;
import com.damytech.utils.NoZoomControllWebView;
import com.damytech.utils.ResolutionSet;

public class HelpActivity extends Activity {
    RelativeLayout mainLayout;
    boolean bInitialized = false;

    private Button btnOk = null;
    private NoZoomControllWebView webData = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        mainLayout = (RelativeLayout)findViewById(R.id.rlHelpBack);
        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        if (bInitialized == false)
                        {
                            Rect r = new Rect();
                            mainLayout.getLocalVisibleRect(r);
                            ResolutionSet._instance.setResolution(r.width(), r.height());
                            ResolutionSet._instance.iterateChild(findViewById(R.id.rlHelpBack));
                            bInitialized = true;
                        }
                    }
                }
        );

        initComponent();
    }

    private  void initComponent()
    {
        webData = (NoZoomControllWebView) findViewById(R.id.viewHelp_Data);
        webData.getSettings().setJavaScriptEnabled(true);
        webData.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webData.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webData.setBackgroundColor(0);
        webData.loadUrl("file:///android_asset/Help.html");

        btnOk = (Button) findViewById(R.id.btnHelp_OK);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    finish();
            }
        });
    }
}
