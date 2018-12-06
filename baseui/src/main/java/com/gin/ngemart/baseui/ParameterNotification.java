package com.gin.ngemart.baseui;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Jati on 18/04/2016.
 * Purpose : this class using to Parameter for function NgemartCommon.ShowNotification
 */
public  class ParameterNotification
{
    public Context context;
    public int notificationID;
    public String title;
    public String message;
    public Intent intent;

    public ParameterNotification()
    {
    }

    public void validate() {
        if (this.context == null)
        {
            throw new NullPointerException("contex_null");
        }
        else if (this.title.equals(""))
        {
            throw new NullPointerException("title_null");
        }
        else if (this.message.equals(""))
        {
            throw new NullPointerException("message_null");
        }
        else if (this.intent == null)
        {
            throw new NullPointerException("intent_null");
        }
    }
}
