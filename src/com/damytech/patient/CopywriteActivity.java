package com.damytech.patient;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.*;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.damytech.utils.GlobalData;
import com.damytech.utils.NoZoomControllWebView;
import com.damytech.utils.ResolutionSet;

public class CopywriteActivity extends Activity {
    RelativeLayout mainLayout;
    boolean bInitialized = false;

    private Button btnOk = null;
    private NoZoomControllWebView webData = null;
    ImageView imgTongyi = null, imgButongyi = null;
    TextView lblTongyi = null, lblButongyi = null;

    boolean bAgree = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copyright);

        mainLayout = (RelativeLayout)findViewById(R.id.rlCopywriteBack);
        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        if (bInitialized == false)
                        {
                            Rect r = new Rect();
                            mainLayout.getLocalVisibleRect(r);
                            ResolutionSet._instance.setResolution(r.width(), r.height());
                            ResolutionSet._instance.iterateChild(findViewById(R.id.rlCopywriteBack));
                            bInitialized = true;
                        }
                    }
                }
        );

        initComponent();
    }

    private  void initComponent()
    {
        imgTongyi = (ImageView) findViewById(R.id.imgTongyi);
        imgTongyi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bAgree = true;
                imgTongyi.setImageResource(R.drawable.checked);
                imgButongyi.setImageResource(R.drawable.unchecked);
                btnOk.setVisibility(View.VISIBLE);
            }
        });
        lblTongyi = (TextView) findViewById(R.id.lblTongyi);
        lblTongyi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bAgree = true;
                imgTongyi.setImageResource(R.drawable.checked);
                imgButongyi.setImageResource(R.drawable.unchecked);
                btnOk.setVisibility(View.VISIBLE);
            }
        });

        imgButongyi = (ImageView) findViewById(R.id.imgButongyi);
        imgButongyi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bAgree = false;
                imgButongyi.setImageResource(R.drawable.checked);
                imgTongyi.setImageResource(R.drawable.unchecked);
                btnOk.setVisibility(View.INVISIBLE);
            }
        });
        lblButongyi = (TextView) findViewById(R.id.lblButongyi);
        lblButongyi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bAgree = false;
                imgButongyi.setImageResource(R.drawable.checked);
                imgTongyi.setImageResource(R.drawable.unchecked);
                btnOk.setVisibility(View.INVISIBLE);
            }
        });

        webData = (NoZoomControllWebView) findViewById(R.id.viewCopywrite_Data);
        webData.getSettings().setJavaScriptEnabled(true);
        webData.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webData.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webData.setBackgroundColor(0);
        webData.loadUrl("file:///android_asset/copywrite.html");

        btnOk = (Button) findViewById(R.id.btnCopywrite_OK);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(CopywriteActivity.this, DefLoginActivity.class);
                    startActivity(intent);
                    finish();
            }
        });
    }
}
