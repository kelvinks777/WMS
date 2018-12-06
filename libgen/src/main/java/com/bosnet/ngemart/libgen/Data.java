package com.bosnet.ngemart.libgen;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by luis on 2/12/2016.
 * Purpose :
 */
public abstract class Data implements Serializable{

    public Date updated = new Date();

    public abstract Contract getContract() throws Exception;

}
