package com.gin.wms.manager.config;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.bosnet.ngemart.libgen.RestConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by manbaul on 2/19/2018.
 */

public class FileConfigReader {
    public static void setHostApiFromFile(Context context) throws Exception {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            final String hostPath = Environment.getExternalStoragePublicDirectory("host.bos").getPath();
            File file = new File(hostPath);
            if (file.exists()) {
                String host;
                BufferedReader br = new BufferedReader(new FileReader(hostPath));
                host = br.readLine();
                RestConfig restConfig = new ManagerSetup().getRestConfig();
                restConfig.setApiHost(host);
                Toast.makeText(context, "Custom Host : " + new ManagerSetup().getRestConfig().GetApiHost(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}