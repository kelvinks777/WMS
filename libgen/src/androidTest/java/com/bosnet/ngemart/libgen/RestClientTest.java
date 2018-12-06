package com.bosnet.ngemart.libgen;


import com.bosnet.ngemart.libgen.Dummy.DeviceId;
import com.bosnet.ngemart.libgen.Dummy.TokenData;
import com.bosnet.ngemart.libgen.Dummy.UserData;

import junit.framework.TestCase;

import java.util.Date;
import java.util.List;

/**
 * Created by luis on 2/26/2016.
 * Purpose : Testing RestClient, testing basic function Restful feature
 */
public class RestClientTest extends TestCase {

    //email : luisginan@gmail.com , password : 123456 , use Postman to generate authorization code
    private final String authorization = "bHVpc2dpbmFuQGdtYWlsLmNvbToxMjM0NTY=";
    public void testGet(){
        try{
            String passValue = "http://homecamportal.cloudapp.net/Api/UserData/Signin?email=luisginan@gmail.com&password=123456";
            RestClient IRestClient = new RestClient();
            UserData userData = IRestClient.get(UserData.class, passValue);
            assertEquals("Luis Ginanjar", userData.fullName);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void testGetList() {
        try {
            String passValue = "http://homecamportal.cloudapp.net/Api/Camera/GetCameras";
            RestClient restClient = new RestClient();
            restClient.setAuthorization(authorization, "shop", "");
            restClient.getList(UserData[].class, passValue);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void testPostWithReturnValue() throws Exception {
        String url = "http://ngemart-webapi.azurewebsites.net/api/values/datafrompost/";
        TokenData tokenData = new TokenData();
        tokenData.updated = new Date();
        tokenData.token = "";

        RestClient IRestClient = new RestClient();
        TokenData tokenResult =  IRestClient.post(TokenData.class, url, tokenData);
        assertEquals(tokenResult.token,"123456");
    }
    public void testPostWithReturnListValue() throws Exception {
        String url = "http://ngemart-webapi.azurewebsites.net/api/values/listfrompost/";
        TokenData tokenData = new TokenData();
        tokenData.updated = new Date();
        tokenData.token = "";

        RestClient IRestClient = new RestClient();
        List<TokenData> tokenResultList =  IRestClient.postl(TokenData[].class, url, tokenData);
        assertEquals(tokenResultList.get(tokenResultList.size()-1).token,"123456");
    }

    public void testPost() throws Exception {
        String url = "http://homecamportal.cloudapp.net/api/Camera/InsertDeviceID";
        DeviceId deviceId = new DeviceId();
        deviceId.UID = String.valueOf(Math.random());
        deviceId.RowKey = "1234567890199";

        RestClient IRestClient = new RestClient();
        IRestClient.setAuthorization(authorization, "shop", "");
        IRestClient.post(url, deviceId);
    }

    public void testPut() throws Exception {
        String url = "http://homecamportal.cloudapp.net/api/UserData/UpdateUniqueAppCode?code=123456";
        RestClient IRestClient = new RestClient();
        IRestClient.setAuthorization(authorization, "shop", "");
        IRestClient.put(url);
    }

    public void testPut_WithBody() throws Exception {
        String url = "http://homecamportal.cloudapp.net/api/UserData/UpdateProfile";
        RestClient IRestClient = new RestClient();
        IRestClient.setAuthorization(authorization, "shop", "");
        UserData userData = new UserData();
        userData.fullName = "Luis Ginanjar";
        IRestClient.put(url, userData);
    }

    public void testDelete_ErrorSystem() {
        try {
            String url = "http://homecamportal.cloudapp.net/api/Camera/DeleteCamera?cameraRK=123456";
            RestClient IRestClient = new RestClient();
            IRestClient.setAuthorization(authorization, "shop", "");
            IRestClient.delete(url);
            assertFalse(true);
        }
        catch (Exception e)
        {
            assertEquals("java.lang.Exception: Failed : HTTP error code : 404-Camera not found",e.toString());
        }

    }

}