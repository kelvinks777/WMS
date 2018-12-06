package com.gin.ngemart.baseui.component;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.util.Base64;

import com.gin.ngemart.baseui.AsyncTaskResult;

import java.io.ByteArrayOutputStream;
import java.net.URL;

/**
 * Created by manbaul on 10/21/2016.
 */

public class ImageToStringDownloader extends AsyncTask<String, String, AsyncTaskResult<String>>
{
    public interface IDownloaderHandler
    {
        void PostExecuteDownloader(String image);
    }

    IDownloaderHandler downloaderHandler;

    public ImageToStringDownloader(IDownloaderHandler downloaderHandler)
    {
        this.downloaderHandler = downloaderHandler;
    }

    @Override
    protected void onPostExecute(AsyncTaskResult<String> stringAsyncTaskResult) {
        super.onPostExecute(stringAsyncTaskResult);
        downloaderHandler.PostExecuteDownloader(stringAsyncTaskResult.data);
    }

    @Override
    protected AsyncTaskResult<String> doInBackground(String... strings) {
        AsyncTaskResult<String> result = new AsyncTaskResult<>();
        try {
            String imageUrl = strings[0];
            result.data = getProfilePicture(imageUrl);
        } catch (Exception e) {
            result.isError = true;
            result.exception = e;
        }
        return result;
    }

    private String getProfilePicture(String imageUrl) throws Exception {
        URL imageURL = new URL(imageUrl);
        Bitmap bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
        String result = "";
        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            byte[] byteArray;
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byteArray = stream.toByteArray();
            result = Base64.encodeToString(byteArray,Base64.DEFAULT);
        }

        return result;
    }
}

