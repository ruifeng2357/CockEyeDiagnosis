package com.damytech.patient;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.graphics.*;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.os.*;
import android.view.*;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.damytech.utils.GlobalData;
import com.damytech.utils.ResolutionSet;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import java.io.*;
import java.util.List;

public class VerPhotoActivity extends Activity implements SurfaceHolder.Callback, Camera.PreviewCallback, Camera.PictureCallback {
    RelativeLayout mainLayout;
    boolean bInitialized = false;

    private Button btnVideo;
    private ImageView imgShutter;
    private SurfaceView videoData;
    private SurfaceHolder mHolder;
    private CamcorderProfile camcorderProfile;
    private Camera mCamera = null;
    private Camera.Parameters param;

    private int nFileCount = 0;
    private int nSelPhotoNo = 0;

    private boolean bRecording = false;
    private boolean bPreRunning = false;

    MediaPlayer mediaPlayer;

    File jpgPhotoFile;
    FileOutputStream fos;
    BufferedOutputStream bos;

    @Override
    public void onPreviewFrame(byte[] data, Camera camera)
    {
        if (bRecording)
        {
            param = camera.getParameters();
            if (param.getPreviewFormat() == ImageFormat.NV21)
            {
                try {
                    jpgPhotoFile = new File(GlobalData.createFileName(nFileCount));
                    nFileCount++;

                    fos = new FileOutputStream(jpgPhotoFile);
                    bos = new BufferedOutputStream(fos);

                    YuvImage im = new YuvImage(data, ImageFormat.NV21, param.getPreviewSize().width, param.getPreviewSize().height, null);
                    Rect r = new Rect(0, 0, param.getPreviewSize().width, param.getPreviewSize().height);
                    im.compressToJpeg(r, 100, bos);

                    bos.flush();
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mediaPlayer = new MediaPlayer();

        DeleteAllPhotoData();
        File savePath = new File(Environment.getExternalStorageDirectory().getPath() + "/YanBingPhoto/");
        savePath.mkdirs();

        setContentView(R.layout.activity_verphoto);

        mainLayout = (RelativeLayout)findViewById(R.id.rlPhotoBack);
        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        if (bInitialized == false) {
                            Rect r = new Rect();
                            mainLayout.getLocalVisibleRect(r);
                            ResolutionSet._instance.setResolution(r.width(), r.height());
                            ResolutionSet._instance.iterateChild(findViewById(R.id.rlPhotoBack));
                            bInitialized = true;
                        }
                    }
                }
        );

        initControl();

        Thread.UncaughtExceptionHandler mUEHandler = new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                e.printStackTrace();
            }
        };
        Thread.setDefaultUncaughtExceptionHandler(mUEHandler);
    }

    private void initControl()
    {
        btnVideo = (Button) findViewById(R.id.btnPhoto_Video);
        btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bRecording = true;
            }
        });

        imgShutter = (ImageView) findViewById(R.id.imgPhto_Shutter);
        imgShutter.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCamera != null)
                {
                    try {
                        play();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    param = mCamera.getParameters();
                    param.setFlashMode(Parameters.FLASH_MODE_TORCH);
                    mCamera.setParameters(param);

                    nSelPhotoNo = nFileCount;

                    getJpgPhoto(nSelPhotoNo-1);
                }

                return;
            }
        });

        camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        videoData = (SurfaceView) findViewById(R.id.surfacePhoto_Video);
        mHolder = videoData.getHolder();
        mHolder.addCallback(VerPhotoActivity.this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        return;
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if (mediaPlayer != null)
            mediaPlayer.release();
    }

    public void getJpgPhoto ( int nFileNo ) {
        File srcFile = new File(GlobalData.createFileName(nFileNo));
        File dstFile = null;
        if (srcFile.exists())
        {
            String strPath = "";
            strPath = GlobalData.getPhotoPath1();

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

                correctBitmap(strPath);

                Intent intent = new Intent(VerPhotoActivity.this, CompareActivity.class);
                intent.putExtra("PhotoNum", 1);
                startActivity(intent);
                finish();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
        }
        else
            return;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            return false;
//        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mCamera = Camera.open();
        if (mCamera == null)
        {
            GlobalData.showToast(VerPhotoActivity.this, getString(R.string.cameranotavailable));
            finish();
        }

        Parameters param = mCamera.getParameters();
        List<Size> sizes = param.getSupportedPreviewSizes();

        //Size mPreviewSize = selectBestMatchSize( sizes, mScreenSize );
        Size mPreviewSize = selectLargestSize( sizes );
        param.setPreviewSize(mPreviewSize.width, mPreviewSize.height);

        int nZoom = param.getMaxZoom();
        if (nZoom < 10)
            param.setZoom(nZoom);
        else
            param.setZoom(10);

        sizes = param.getSupportedPictureSizes();
        Size picSize = selectLargestSize(sizes);
        if (picSize != null)
        {
            picSize.height = (int)(picSize.width * (mPreviewSize.height * 1.0f / mPreviewSize.width));
            param.setPictureSize(picSize.width, picSize.height);
        }
//        Size mPictureSize = selectBestMatchSize(sizes, mScreenSize);
//        param.setPictureSize(mPictureSize.width, mPictureSize.height);

        mCamera.setDisplayOrientation(90);

        try {
            mCamera.setParameters(param);
        } catch (Exception ex){
        }
    }

    private Size selectLargestSize ( List<Size> sizes ) {
        Size  largestSize = null;
        int availmem = getAvailableMemory();

        for (Size size : sizes) {
            int bmpsize = size.width*size.height*4/1024;
            if ( availmem*2/3 < bmpsize ) {
                continue;
            }

            if ( largestSize == null || largestSize.width<size.width ) {
                largestSize = size;
            }
        }

        return largestSize;
    }

    private int getAvailableMemory () {
        Debug.MemoryInfo memoryInfo = new Debug.MemoryInfo();
        Debug.getMemoryInfo(memoryInfo);

        int totalmem_kb = (int) (Runtime.getRuntime().maxMemory() / 1024.0);

        return ( totalmem_kb - memoryInfo.getTotalPss() );
    }

    private Size selectBestMatchSize (List<Size> sizes, Size keySize) {
        final double ASPECT_TOLERANCE = 0.05;
        double targetRatio = (double) keySize.width/keySize.height;
        int availmem = getAvailableMemory();

        if (sizes==null) return null;

        Size optimalSize = null;

        double minDiff = Double.MAX_VALUE;

        int targetHeight = keySize.height;

        // Find size
        for (Size size : sizes) {
            int bmpsize = size.width*size.height*4/1024;
            if ( availmem/2 < bmpsize ) {
                continue;
            }

            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                int bmpsize = size.width*size.height*4/1024;
                if ( availmem/2 < bmpsize ) {
                    continue;
                }

                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        if (!bRecording)
        {
            if (bPreRunning)
                mCamera.stopPreview();

            try
            {
                param = mCamera.getParameters();

                //param.setPreviewSize(camcorderProfile.videoFrameWidth, camcorderProfile.videoFrameHeight);
                param.setPreviewFrameRate(camcorderProfile.videoFrameRate);
                mCamera.setParameters(param);

                mCamera.setPreviewDisplay(holder);
                mCamera.setPreviewCallback(this);

                mCamera.startPreview();
                bRecording = true;
                bPreRunning = true;
            }
            catch (IOException e) {}
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
        mCamera.release();

        if (bRecording)
        {
            bRecording = false;

            try
            {
                if (bos != null)
                {
                    bos.flush();
                    bos.close();
                }
            }
            catch (IOException e) {}
        }

        bPreRunning = false;
        mCamera.release();
    }

    @Override
    public void onConfigurationChanged(Configuration conf)
    {
        super.onConfigurationChanged(conf);
    }

    public void DeleteAllPhotoData()
    {
        try {
            File file = new File(Environment.getExternalStorageDirectory(), "/YanBingPhoto/");
            if (file.exists()) {
                DeleteRecursive(file);
            }
        } catch (Exception ex) {}
    }

    public void DeleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                DeleteRecursive(child);

        fileOrDirectory.delete();
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        if (bRecording)
        {
            param = camera.getParameters();
            if (param.getPreviewFormat() == ImageFormat.NV21)
            {
                try {
                    jpgPhotoFile = new File(GlobalData.createFileName(nFileCount));
                    nFileCount++;

                    fos = new FileOutputStream(jpgPhotoFile);
                    bos = new BufferedOutputStream(fos);

                    YuvImage im = new YuvImage(data, ImageFormat.NV21, param.getPictureSize().width, param.getPictureSize().height, null);
                    Rect r = new Rect(0, 0, param.getPreviewSize().width, param.getPreviewSize().height);
                    im.compressToJpeg(r, 100, bos);

                    bos.flush();
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void correctBitmap(String szPath)
    {
        //Bitmap bmpRot = GlobalData.rotateImage(szPath, 0);
        Bitmap bmpRot = BitmapFactory.decodeFile(szPath);
        FileOutputStream ostream = null;

        try {
            File file = new File(szPath);
            file.deleteOnExit();

            ostream = new FileOutputStream(file);
            bmpRot.compress(Bitmap.CompressFormat.JPEG, 50, ostream);
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
    }

    private void play() throws IOException {
        AssetFileDescriptor descriptor = getAssets().openFd("click.mp3");
        mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
        descriptor.close();

        mediaPlayer.prepare();
        mediaPlayer.setVolume(1f, 1f);
        mediaPlayer.setLooping(false);
        mediaPlayer.start();
    }
}
