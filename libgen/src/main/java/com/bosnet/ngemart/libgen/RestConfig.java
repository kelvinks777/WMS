package com.bosnet.ngemart.libgen;

/**
 * Created by luisginan on 7/4/17.
 * Purpose :
 */

public interface RestConfig {

    String GetApiHost() throws Exception;
    String GetAppId()throws Exception;
    String GetPrincipalId() throws Exception;
    void setApiHost(String host) throws Exception;
}
