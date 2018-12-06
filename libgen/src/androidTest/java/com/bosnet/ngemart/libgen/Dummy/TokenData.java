package com.bosnet.ngemart.libgen.Dummy;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.Data;

/**
 * Created by Jati on 8/1/2016.
 * Purpose :
 */
public class TokenData extends Data {
    public String token;

    @Override
    public Contract getContract() {
        return null;
    }

    @Override
    public String toString() {
        return "TokenData{" +
                "token='" + token + '\'' +
                '}';
    }
}
