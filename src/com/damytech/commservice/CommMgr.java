package com.damytech.commservice;

import com.damytech.HttpConn.AsyncHttpResponseHandler;
import com.damytech.STData.STDiagnDetail;
import com.damytech.STData.STDiagnInfo;
import com.damytech.STData.STUserID;
import org.json.JSONObject;
import java.util.ArrayList;

public class CommMgr {
	
	public static CommMgr commService = new CommMgr();
    public UserSvcMgr userMgr = new UserSvcMgr();
    public DiagnSvcMgr diagnMgr = new DiagnSvcMgr();

	public CommMgr() {}

    public void RegisterPatient(AsyncHttpResponseHandler handler, String name, int gender, String birth, String address, String phonenum, String password, String reldisease)
    {
        userMgr.RegisterPatient(handler, name, gender, birth, address, phonenum, password, reldisease);
    }

    public long parseRegisterPatient(JSONObject jsonObject)
    {
        return userMgr.parseRegisterPatient(jsonObject);
    }

    public void LoginPatient(AsyncHttpResponseHandler handler, String phonenum, String password)
    {
        userMgr.LoginPatient(handler, phonenum, password);
    }

    public long parseLoginPatient(JSONObject jsonObject, STUserID userID)
    {
        return userMgr.parseLoginPatient(jsonObject, userID);
    }

    public void AddDiagnInfo(AsyncHttpResponseHandler handler, long pid, String name, String phone, String birth, int gender, String imgdata1, String imgdata2)
    {
        diagnMgr.AddDiagnInfo(handler, pid, name, phone, birth, gender, imgdata1, imgdata2);
    }

    public long parseAddDiagnInfo(JSONObject jsonObject)
    {
        return diagnMgr.parseAddDiagnInfo(jsonObject);
    }

    public void GetDiagnList(AsyncHttpResponseHandler handler, int uid, int pageno)
    {
        diagnMgr.GetDiagnList(handler, uid, pageno);
    }

    public long parseGetDiagnList(JSONObject jsonObject, ArrayList<STDiagnInfo> dataList)
    {
        return diagnMgr.parseGetDiagnList(jsonObject, dataList);
    }

    public void GetDiagnDetail(AsyncHttpResponseHandler handler, int uid, int rqid)
    {
        diagnMgr.GetDiagnDetail(handler, uid, rqid);
    }

    public long parseGetDiagnDetail(JSONObject jsonObject, STDiagnDetail dataInfo)
    {
        return diagnMgr.parseGetDiagnDetail(jsonObject, dataInfo);
    }
}
