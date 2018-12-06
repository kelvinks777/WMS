package com.gin.wms.warehouse.component;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by manbaul on 3/13/2018.
 */

public class UomTextWatcher implements TextWatcher {
    private boolean isRunning = false;
    private boolean isDeleting = false;
    private final String mask;

    private static final String DEFAULT_MASK = "###/###/###";

    public UomTextWatcher(String separator) {
        this.mask = DEFAULT_MASK.replace("/",separator);
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        isDeleting = i1 > i2;
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (isRunning || isDeleting) {
            return;
        }
        isRunning = true;

        int editableLength = editable.length();
        if (editableLength < mask.length()) {
            if (mask.charAt(editableLength) != '#') {
                editable.append(mask.charAt(editableLength));
            } else if (mask.charAt(editableLength-1) != '#') {
                editable.insert(editableLength-1, mask, editableLength-1, editableLength);
            }
        }

        isRunning = false;
    }
}
