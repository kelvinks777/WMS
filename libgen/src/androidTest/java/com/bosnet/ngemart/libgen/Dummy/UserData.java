package com.bosnet.ngemart.libgen.Dummy;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.Data;

import java.io.Serializable;

/**
 * Created by luis on 2/12/2016.
 * Purpose :
 */
public class UserData extends Data implements Serializable {
    public String id;
    public String fullName;
    public String email;
    public String phoneNumber;
    public String profileImage;

    @Override
    public String toString() {
        return "UserData{" +
                "id='" + id + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", profileImage='" + profileImage + '\'' +
                '}';
    }

    @Override
    public Contract getContract() throws Exception {
        return new UserContract();
    }


}
