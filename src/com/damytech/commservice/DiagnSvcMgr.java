package com.damytech.commservice;

import com.damytech.HttpConn.AsyncHttpClient;
import com.damytech.HttpConn.AsyncHttpResponseHandler;
import com.damytech.HttpConn.RequestParams;
import com.damytech.STData.STDiagnDetail;
import com.damytech.STData.STDiagnInfo;
import com.damytech.STData.STServiceData;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class DiagnSvcMgr {

    public void AddDiagnInfo(AsyncHttpResponseHandler handler, long pid, String name, String phone, String birth, int gender, String imgdata1, String imgdata2)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            url = STServiceData.serviceAddr + STServiceData.cmdAddDiagnInfo;

            JSONObject jsonParams = new JSONObject();
            jsonParams.put("pid", pid);
            jsonParams.put("name", name);
            jsonParams.put("phone", phone);
            jsonParams.put("birth", birth);
            jsonParams.put("gender", gender);
            jsonParams.put("imgdata1", imgdata1);
            jsonParams.put("imgdata2", imgdata2);
            StringEntity entity = new StringEntity(jsonParams.toString(), "utf-8");

            client.setTimeout(STServiceData.connectImageTimeout);
            client.post(null, url, entity, "application/json", handler);
        }
		catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long parseAddDiagnInfo(JSONObject jsonObject)
    {
        int retResult = STServiceData.ERR_SERVER_ERROR;

        try {
            retResult = jsonObject.getInt("SVCC_RETVAL");
            return retResult;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return retResult;
    }

    public void GetDiagnList(AsyncHttpResponseHandler handler, int uid, int pageno)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            url = STServiceData.serviceAddr + STServiceData.cmdGetDiagnList;
            param.put("uid", Integer.toString(uid));
            param.put("pageno", Integer.toString(pageno));
            client.setTimeout(STServiceData.connectTimeout);
            client.get(url, param, handler);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long parseGetDiagnList(JSONObject jsonObject, ArrayList<STDiagnInfo> dataList)
    {
        long retVal = 0;
        String basePath = "";
        int retResult = STServiceData.ERR_SERVER_ERROR;

        dataList.clear();
        try {
            retResult = jsonObject.getInt("SVCC_RETVAL");
            basePath = jsonObject.getString("SVCC_BASEURL");
            if (STServiceData.ERR_SUCCESS != retResult)
            {
                retVal = retResult;
            }
            else
            {
                JSONArray jsonArray = jsonObject.getJSONArray("SVCC_DATA");
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    JSONObject item = jsonArray.getJSONObject(i);
                    STDiagnInfo stInfo = new STDiagnInfo();
                    stInfo.id= item.getLong("Id");
                    stInfo.name = item.getString("Name");
                    stInfo.birth = item.getString("Birth");
                    stInfo.gender = item.getInt("Gender");
                    stInfo.imgpath1 = basePath + item.getString("Imgpath1");
                    stInfo.imgpath2 = basePath + item.getString("Imgpath2");
                    stInfo.regtime = item.getString("Regtime");
                    stInfo.isdiagn = item.getInt("Isdiagn");
                    stInfo.feedback = item.getString("Feedback");

                    dataList.add(stInfo);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            retVal = STServiceData.ERR_SERVER_ERROR;
        }

        return retVal;
    }

    public void GetDiagnDetail(AsyncHttpResponseHandler handler, int uid, int rqid)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            url = STServiceData.serviceAddr + STServiceData.cmdGetDiagnDetail;
            param.put("uid", Integer.toString(uid));
            param.put("rqid", Integer.toString(rqid));
            client.setTimeout(STServiceData.connectTimeout);
            client.get(url, param, handler);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long parseGetDiagnDetail(JSONObject jsonObject, STDiagnDetail dataInfo)
    {
        long retVal = 0;
        String basePath = "";
        int retResult = STServiceData.ERR_SERVER_ERROR;

        try {
            retResult = jsonObject.getInt("SVCC_RETVAL");
            basePath = jsonObject.getString("SVCC_BASEURL");
            if (STServiceData.ERR_SUCCESS != retResult)
            {
                retVal = retResult;
            }
            else
            {
                JSONObject obj = jsonObject.getJSONObject("SVCC_DATA");
                dataInfo.name = obj.getString("Name");
                dataInfo.diagndate = obj.getString("DiagnDate");
                dataInfo.feedback = obj.getString("Feedback");
                dataInfo.imgpath1 = basePath + obj.getString("Imgpath1");
                dataInfo.imgpath2 = basePath + obj.getString("Imgpath2");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            retVal = STServiceData.ERR_SERVER_ERROR;
        }

        return retVal;
    }
}
