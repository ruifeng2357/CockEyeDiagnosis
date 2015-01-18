package com.damytech.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;
import com.damytech.patient.R;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.regex.Pattern;

public class GlobalData
{
    public static final int PHONENUM_LENGTH = 11;

    public static String strPrefName = "YanBing_Patient";
    public static String strPref_Help = "HELP";
    public static String strPref_UID = "UID";
    public static String strPref_PHONE = "PHONE";
    public static String strPref_PWD = "PWD";
    public static String strPref_PWDFLAG = "PWDFLAG";

    private static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
	          "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
	          "\\@" +
	          "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
	          "(" +
	          "\\." +
	          "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
	          ")+"
	      );

    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("^[+]?[0-9]{10,13}$");
	
	private static Toast g_Toast = null;
	public static void showToast(Context context, String toastStr)
	{
		if ((g_Toast == null) || (g_Toast.getView().getWindowVisibility() != View.VISIBLE))
		{
			g_Toast = Toast.makeText(context, toastStr, Toast.LENGTH_SHORT);
			g_Toast.show();
		}

		return;
	}

	public static boolean isValidEmail(String strEmail)
	{
		return EMAIL_ADDRESS_PATTERN.matcher(strEmail).matches();
	}

    public static boolean isValidPhone(String strPhone)
    {
        return PHONE_NUMBER_PATTERN.matcher(strPhone).matches();
    }

    public static boolean isOnline(Context ctx)
    {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public static String createFileName(int nFileNo)
    {
        String strFileName = "";
        NumberFormat fileCountFormatter = new DecimalFormat("0000");

        strFileName = Environment.getExternalStorageDirectory().getPath() + "/YanBingPhoto/frame_" + fileCountFormatter.format(nFileNo) + ".jpg";

        return strFileName;
    }

    public static int getFileCount()
    {
        int nCount = 0;

        try {
            File file = new File(Environment.getExternalStorageDirectory(), "/YanBingPhoto/");
            if (file.exists()) {
                if (file.isDirectory())
                    nCount = file.listFiles().length;
            }
        } catch (Exception ex) {}

        return nCount;
    }

    public static String getPhotoPath0()
    {
        String strPath;

        File tempDir= Environment.getExternalStorageDirectory();
        tempDir=new File(tempDir.getAbsolutePath()+"/Download/");
        strPath = tempDir + "/Photo0.jpg";

        return strPath;
    }

    public static String getPhotoPath1()
    {
        String strPath;

        File tempDir= Environment.getExternalStorageDirectory();
        tempDir=new File(tempDir.getAbsolutePath()+"/Download/");
        if (tempDir.exists() == false)
            tempDir.mkdir();
        strPath = tempDir + "/Photo1.jpg";

        return strPath;
    }

    public static String getPhotoPath2()
    {
        String strPath;

        File tempDir= Environment.getExternalStorageDirectory();
        tempDir=new File(tempDir.getAbsolutePath()+"/Download/");
        if (tempDir.exists() == false)
            tempDir.mkdir();
        strPath = tempDir + "/Photo2.jpg";

        return strPath;
    }

    public static boolean existPhoto1(Context context)
    {
        try
        {
            File file = new File(getPhotoPath1());

            return file.exists();
        } catch (Exception ex) {}

        return false;
    }

    public static boolean existPhoto2(Context context)
    {
        try
        {
            File file = new File(getPhotoPath2());

            return file.exists();
        } catch (Exception ex) {}

        return false;
    }

    public static String toDoubleNum(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    public static String getDateFormat(Context context, int nYear, int nMonth, int nDay)
    {
        return (nYear + context.getString(R.string.nian) + toDoubleNum(nMonth+1) + " " + context.getString(R.string.yue) + toDoubleNum(nDay) + " " + context.getString(R.string.ri));
    }

    public static boolean DeletePhotos(Context context)
    {
        try
        {
            if (existPhoto1(context) == true)
            {
                File file = new File(getPhotoPath1());
                file.delete();
            }
            if (existPhoto2(context) == true)
            {
                File file = new File(getPhotoPath2());
                file.delete();
            }
        }
        catch (Exception ex)
        {
            return false;
        }

        return true;
    }

    public static long GetUID(Context context)
    {
        long nID = 0;

        SharedPreferences pref = context.getSharedPreferences(strPrefName, Context.MODE_PRIVATE);
        nID = pref.getLong(strPref_UID, 0);

        return nID;
    }

    public static void SetUID(Context context, long nID)
    {
        SharedPreferences pref = context.getSharedPreferences(strPrefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(strPref_UID, nID);
        editor.commit();

        return;
    }

    public static boolean GetPWDFlag(Context context)
    {
        boolean bRet = true;

        SharedPreferences pref = context.getSharedPreferences(strPrefName, Context.MODE_PRIVATE);
        bRet = pref.getBoolean(strPref_PWDFLAG, false);

        return bRet;
    }

    public static void SetPWDFlag(Context context, boolean bFlag)
    {
        SharedPreferences pref = context.getSharedPreferences(strPrefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(strPref_PWDFLAG, bFlag);
        editor.commit();

        return;
    }

    public static String GetPWD(Context context)
    {
        String strRet;

        SharedPreferences pref = context.getSharedPreferences(strPrefName, Context.MODE_PRIVATE);
        strRet = pref.getString(strPref_PWD, "");

        return strRet;
    }

    public static void SetPWD(Context context, String strPassword)
    {
        SharedPreferences pref = context.getSharedPreferences(strPrefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(strPref_PWD, strPassword);
        editor.commit();

        return;
    }

    public static String GetPhoneNum(Context context)
    {
        String strRet;

        SharedPreferences pref = context.getSharedPreferences(strPrefName, Context.MODE_PRIVATE);
        strRet = pref.getString(strPref_PHONE, "");

        return strRet;
    }

    public static void SetPhoneNum(Context context, String strPhoneNum)
    {
        SharedPreferences pref = context.getSharedPreferences(strPrefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(strPref_PHONE, strPhoneNum);
        editor.commit();

        return;
    }

    public static Bitmap rotateImage(String pathToImage, int nAngle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(nAngle);

        Bitmap sourceBitmap = BitmapFactory.decodeFile(pathToImage);
        return Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight(), matrix, true);
    }

    public static BitmapDrawable getClockWite90Image(String path)
    {
        BitmapDrawable bmd = null;
        try {
            Bitmap bmp = BitmapFactory.decodeFile(path);
            int w = bmp.getWidth();
            int h = bmp.getHeight();

            Matrix mtx = new Matrix();
            mtx.postRotate(90);

            Bitmap rotatedBMP = Bitmap.createBitmap(bmp, 0, 0, w, h, mtx, true);
            bmd = new BitmapDrawable(rotatedBMP);
        } catch (Exception ex) {
            bmd = null;
        }

        return bmd;
    }
}
