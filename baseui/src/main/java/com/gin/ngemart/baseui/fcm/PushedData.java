package com.gin.ngemart.baseui.fcm;

import android.os.Bundle;

import com.bosnet.ngemart.libgen.GsonMapper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by manbaul on 9/23/2016.
 */

public class PushedData {
    public String pushType = "";
    public String referenceId = "";
    public String message = "";
    public String optData = "";
    public Date serverDate = new Date();

    public static PushedData getDataFromMap(Map<String, String> data) {
        PushedData pushedData = new PushedData();
        pushedData.pushType = data.get("subjectId");
        pushedData.referenceId = data.get("referenceId");
        pushedData.message = data.get("message");
        pushedData.optData = data.get("optData");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        try {
            pushedData.serverDate = simpleDateFormat.parse(data.get("serverDate"));
        } catch (Exception e)
        {
            pushedData.serverDate = new Date();
        }
        return pushedData;
    }

    public static PushedData GetPushedDataFromBundle(Bundle bundle) {
        GsonMapper gsonMapper = new GsonMapper();
        PushedData pushedData = gsonMapper.read(bundle.getString("pushedData"), PushedData.class);
        return pushedData;
    }

}
