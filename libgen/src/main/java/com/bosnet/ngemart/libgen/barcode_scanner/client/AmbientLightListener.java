package com.bosnet.ngemart.libgen.barcode_scanner.client;

import android.content.Context;

/**
 * Created by Jati on 3/3/2017.
 */

public class AmbientLightListener {
    public interface IAmbientLightListener {
        void OnTorchChangeTurnOn(boolean isTorchOn);
    }

    public static class ChangeTorchListener {
        static IAmbientLightListener ambientLightListener;
        public void addChangeTorchListener(Context context) {
            ambientLightListener = (IAmbientLightListener) context;
        }

        public  void setChangeTorch(Boolean isTorchOn) {
            if(ambientLightListener != null) {
                ambientLightListener.OnTorchChangeTurnOn(isTorchOn);
            }
        }
    }


}
