package com.damytech.patient;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.damytech.utils.GlobalData;
import com.damytech.utils.ResolutionSet;
import com.damytech.utils.SmartImageView.Global;

import java.io.*;

public class CompareActivity extends Activity {
    RelativeLayout mainLayout;
    boolean bInitialized = false;

    private ImageView imgPhoto = null;
    private Button btnOk = null;
    private Button btnCancel = null;

    int nPhotoNum = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);

        mainLayout = (RelativeLayout)findViewById(R.id.rlCompareBack);
        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        if (bInitialized == false)
                        {
                            Rect r = new Rect();
                            mainLayout.getLocalVisibleRect(r);
                            ResolutionSet._instance.setResolution(r.width(), r.height());
                            ResolutionSet._instance.iterateChild(findViewById(R.id.rlCompareBack));
                            bInitialized = true;
                        }
                    }
                }
        );

        nPhotoNum = getIntent().getIntExtra("PhotoNum", nPhotoNum);

        initComponent();
    }

    private  void initComponent()
    {
        imgPhoto = (ImageView) findViewById(R.id.imgCompare_Photo);
        imgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strPath = "";
                if (nPhotoNum == 1)
                    strPath = GlobalData.getPhotoPath1();
                else if (nPhotoNum == 2)
                    strPath = GlobalData.getPhotoPath2();
                else if (nPhotoNum == 0)
                    strPath = GlobalData.getPhotoPath0();
                else
                    return;

                Intent intent = new Intent(CompareActivity.this, DetailPhotoActivity.class);
                intent.putExtra("PATH", strPath);
                startActivity(intent);
            }
        });

        int nFileCount = GlobalData.getFileCount();
        if (nFileCount != 0)
        {
            File srcFile = new File(GlobalData.createFileName(nFileCount-1));
            File dstFile = null;
            if (srcFile.exists())
            {
                String strPath = "";
                if (nPhotoNum == 1)
                    strPath = GlobalData.getPhotoPath1();
                else if (nPhotoNum == 2)
                    strPath = GlobalData.getPhotoPath2();
                else
                    strPath = GlobalData.getPhotoPath0();

                FileInputStream inStream = null;
                FileOutputStream outStream = null;
                try
                {
                    inStream = new FileInputStream(srcFile);
                    dstFile = new File(strPath);
                    if (dstFile.exists())
                        dstFile.delete();
                    dstFile = new File(strPath);
                    outStream = new FileOutputStream( dstFile );

                    byte[] buf = new byte[1024];
                    int len = 0;
                    while ( (len = inStream.read(buf)) > 0 )
                    {
                        outStream.write(buf, 0, len);
                    }

                    inStream.close();
                    outStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                } finally {}
            }
        }

        /*
         */
        if (nPhotoNum == 1)
        {
            BitmapDrawable bmd = GlobalData.getClockWite90Image(GlobalData.getPhotoPath1());
            if (bmd != null)
            {
                int nWidth = bmd.getBitmap().getWidth();
                int nHeight = bmd.getBitmap().getHeight();
                Bitmap croppedBmp = Bitmap.createBitmap(bmd.getBitmap(), 0, nHeight * 375 / 1280, bmd.getBitmap().getWidth(), nHeight * 530 / 1280);

                FileOutputStream ostream = null;

                try {
                    File file = new File(GlobalData.getPhotoPath1());
                    file.deleteOnExit();

                    ostream = new FileOutputStream(file);
                    croppedBmp.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    if (ostream != null) {
                        try { ostream.close(); }
                        catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                imgPhoto.setImageURI(Uri.parse(GlobalData.getPhotoPath1()));
            }
            else
            {
                Bitmap bmp = BitmapFactory.decodeFile(GlobalData.getPhotoPath1());
                int nWidth = bmp.getWidth();
                int nHeight = bmp.getHeight();
                Bitmap croppedBmp = Bitmap.createBitmap(bmp, 0, nHeight * 375 / 1280, bmp.getWidth(), nHeight * 530 / 1280);

                FileOutputStream ostream = null;

                try {
                    File file = new File(GlobalData.getPhotoPath1());
                    file.deleteOnExit();

                    ostream = new FileOutputStream(file);
                    croppedBmp.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    if (ostream != null) {
                        try { ostream.close(); }
                        catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                imgPhoto.setImageURI(Uri.parse(GlobalData.getPhotoPath1()));
            }
        }
        else if (nPhotoNum == 2)
        {
            Bitmap bmp = BitmapFactory.decodeFile(GlobalData.getPhotoPath2());
            int nHeight = bmp.getHeight();
            int nWidth = bmp.getWidth();
            Bitmap croppedBmp = Bitmap.createBitmap(bmp, 0, nHeight * 150 / 720, bmp.getWidth(), nHeight * 420 / 720);

            FileOutputStream ostream = null;

            try {
                File file = new File(GlobalData.getPhotoPath2());
                file.deleteOnExit();

                ostream = new FileOutputStream(file);
                croppedBmp.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (ostream != null) {
                    try { ostream.close(); }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            imgPhoto.setImageURI(Uri.parse(GlobalData.getPhotoPath2()));
        }
        else
            imgPhoto.setImageURI(Uri.parse(GlobalData.getPhotoPath0()));

        btnCancel = (Button) findViewById(R.id.btnCompare_ReShut);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nPhotoNum == 1) {
                    Intent intent = new Intent(CompareActivity.this, VerPhotoActivity.class);
                    intent.putExtra("PhotoNum", nPhotoNum);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                else {
                    Intent intent = new Intent(CompareActivity.this, HorPhotoActivity.class);
                    intent.putExtra("PhotoNum", nPhotoNum);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });

        btnOk = (Button) findViewById(R.id.btnCompare_OK);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GlobalData.GetUID(CompareActivity.this) == 0)
                {
                    GlobalData.showToast(CompareActivity.this, getString(R.string.requirelogin));
                    Intent intent = new Intent(CompareActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    return;
                }

                Intent intent = new Intent(CompareActivity.this, UploadActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("PhotoNum", 1);
                startActivity(intent);
                finish();
            }
        });
    }
}
