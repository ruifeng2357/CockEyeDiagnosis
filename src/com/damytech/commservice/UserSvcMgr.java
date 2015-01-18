package com.damytech.commservice;

import com.damytech.HttpConn.AsyncHttpClient;
import com.damytech.HttpConn.AsyncHttpResponseHandler;
import com.damytech.HttpConn.RequestParams;
import com.damytech.STData.STServiceData;
import com.damytech.STData.STUserID;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

public class UserSvcMgr {

    public void RegisterPatient(AsyncHttpResponseHandler handler, String name, int gender, String birth, String address, String phonenum, String password, String reldisease)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            url = STServiceData.serviceAddr + STServiceData.cmdRegisterPatient;

            JSONObject jsonParams = new JSONObject();
            jsonParams.put("name", name);
            jsonParams.put("gender", gender);
            jsonParams.put("birth", birth);
            jsonParams.put("address", address);
            jsonParams.put("phonenum", phonenum);
            jsonParams.put("password", password);
            jsonParams.put("reldisease", reldisease);
            StringEntity entity = new StringEntity(jsonParams.toString(), "utf-8");

            client.setTimeout(STServiceData.connectTimeout);
            client.post(null, url, entity, "application/json", handler);
        }
		catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long parseRegisterPatient(JSONObject jsonObject)
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

    public void LoginPatient(AsyncHttpResponseHandler handler, String phonenum, String password)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();

        try {
            url = STServiceData.serviceAddr + STServiceData.cmdLoginPatient;

            JSONObject jsonParams = new JSONObject();
            jsonParams.put("phonenum", phonenum);
            jsonParams.put("password", password);
            StringEntity entity = new StringEntity(jsonParams.toString(), "utf-8");

            //client.setTimeout(STServiceData.connectTimeout);
            client.setTimeout(10000);
            client.post(null, url, entity, "application/json", handler);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long parseLoginPatient(JSONObject jsonObject, STUserID userID)
    {
        int retResult = STServiceData.ERR_SERVER_ERROR;

        try {
            retResult = jsonObject.getInt("SVCC_RETVAL");
            userID.uid = jsonObject.getString("SVCC_DATA");
            return retResult;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return retResult;
    }
}
