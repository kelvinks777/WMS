package com.gin.ngemart.baseui;

/**
 * Created by luis on 9/13/2016.
 * Purpose :
 */
public class AsyncTaskResult<T> {
    public String id;
    public boolean isError = false;
    public T data;
    public Exception exception = null;
}
