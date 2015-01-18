package com.damytech.patient;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import com.damytech.utils.ImgViewTouch;
import com.damytech.utils.ResolutionSet;

public class DetailPhotoActivity extends Activity {
    boolean bInitialized = false;
    RelativeLayout rlMainBack;

    ImgViewTouch imgData;
    String strPath = "";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailphoto);

        strPath = getIntent().getStringExtra("PATH");

        rlMainBack = (RelativeLayout)findViewById(R.id.rlDetailPhotoBack);
        rlMainBack.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        if (bInitialized == false) {
                            Rect r = new Rect();
                            rlMainBack.getLocalVisibleRect(r);
                            ResolutionSet._instance.setResolution(r.height(), r.width());
                            ResolutionSet._instance.iterateChild(findViewById(R.id.rlDetailPhotoBack));
                            bInitialized = true;
                        }
                    }
                }
        );

        imgData = (ImgViewTouch) findViewById(R.id.imgDetailPhoto_Data);
        try
        {
            Bitmap bmp = BitmapFactory.decodeFile(strPath);
            imgData.setImageBitmap(bmp);

        }
        catch (Exception ex)
        {
            imgData.setImageResource(R.drawable.defbackimage);
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }
}
