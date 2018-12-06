package com.gin.wms.manager.db.data;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.Data;
import com.gin.wms.manager.db.contract.UserContract;

import java.io.Serializable;

/**
 * Created by manbaul on 2/19/2018.
 */

public class UserData extends Data implements Serializable {
    public String id;
    public String fullName;
    public String email;
    public String phoneNumber;
    public String profileImage;
    public String publicKey;

    @Override
    public String toString() {
        return "UserData{" +
                "id='" + id + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", profileImage='" + profileImage + '\'' +
                ", publicKey='" + publicKey + '\'' +
                '}';
    }

    @Override
    public Contract getContract() throws Exception {
        return new UserContract();
    }


}