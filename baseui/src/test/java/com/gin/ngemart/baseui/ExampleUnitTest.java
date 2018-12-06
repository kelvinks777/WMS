package com.gin.ngemart.baseui;

import android.content.Context;
import android.content.Intent;
import org.junit.Test;
import org.mockito.Mockito;
import static org.junit.Assert.assertEquals;


/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void ShowNotification_isCorrect() {
        Context context = Mockito.mock(Context.class);
        Intent mockIntent = Mockito.mock(Intent.class);
        ParameterNotification param = new ParameterNotification();
        param.context = context;
        param.intent = mockIntent;
        param.notificationID = 100;
        param.title = "Go to Login";
        param.message = "Go to Login Activity";
    }

    @Test
    public void ShowNotification_isContextNull() {
        Intent mockIntent = Mockito.mock(Intent.class);
        ParameterNotification param =  new ParameterNotification();
        param.context = null;
        param.intent = mockIntent;
        param.notificationID = 100;
        param.title = "Go to Login";
        param.message = "Go to Login Activity";

        try {
            param.validate();
        }catch (Exception e)
        {
            assertEquals("contex_null", e.getMessage());

        }
    }

    @Test
    public void ShowNotification_isTitleNull() {
        Context context = Mockito.mock(Context.class);
        Intent mockIntent = Mockito.mock(Intent.class);

        ParameterNotification param = new ParameterNotification();
        param.context = context;
        param.intent = mockIntent;
        param.notificationID = 100;
        param.title = "";
        param.message = "Go to Login Activity";

        try {
            param.validate();
        }catch (Exception e)
        {
            assertEquals("title_null", e.getMessage());
        }



    }

    @Test
    public void ShowNotification_MessageNull() {
        Context context = Mockito.mock(Context.class);
        Intent mockIntent = Mockito.mock(Intent.class);

        ParameterNotification param = new ParameterNotification();
        param.context = context;
        param.intent = mockIntent;
        param.notificationID = 100;
        param.title = "Show Browser";
        param.message = "";

        try {
            param.validate();
        }catch (Exception e)
        {
            assertEquals("message_null", e.getMessage());
        }
    }

    @Test
    public void ShowNotification_IntentNull() {
        Context context = Mockito.mock(Context.class);

        ParameterNotification param =  new ParameterNotification();
        param.context = context;
        param.intent = null;
        param.notificationID = 100;
        param.title = "Show Browser";
        param.message = "Show google.com";

        try {
            param.validate();
        }catch (Exception e)
        {
            assertEquals("intent_null", e.getMessage());
        }
    }
}