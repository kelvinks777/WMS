package com.gin.ngemart.baseui.component;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.ParseException;
import java.util.Timer;
import java.util.TimerTask;

import static com.bosnet.ngemart.libgen.Common.GetNumberFractionFormat;
import static com.bosnet.ngemart.libgen.Common.GetNumberWithoutFractionFormat;

/**
 * Created by Jati on 3/10/2017.
 */

public class NumberTextWatcherWithDelayTime implements TextWatcher {
    private boolean hasFractionalPart;
    private EditText editText;
    private int trailingZeroCount = 0;
    private INumberHandler numberHandler = null;

    private Timer timer = new Timer();
    private long delayTime = 1000;
    private Activity activity;

    public NumberTextWatcherWithDelayTime(Activity activity, EditText editText, long delayTime, INumberHandler numberHandler) {
        this.editText = editText;
        this.activity = activity;
        if (delayTime > 0)
            this.delayTime = delayTime;
        hasFractionalPart = false;
    }

    public NumberTextWatcherWithDelayTime(Activity activity, EditText editText, long delayTime) {
        this.editText = editText;
        this.activity = activity;
        if (delayTime > 0)
            this.delayTime = delayTime;
        hasFractionalPart = false;
    }

    public NumberTextWatcherWithDelayTime(Activity activity, EditText editText) {
        this.editText = editText;
        this.activity = activity;
        hasFractionalPart = false;

    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        int index = charSequence
                .toString()
                .indexOf(String.valueOf(
                        GetNumberFractionFormat()
                                .getDecimalFormatSymbols()
                                .getDecimalSeparator()));
        trailingZeroCount = 0;
        if (index > -1) {
            for (index++; index < charSequence.length(); index++) {
                if (charSequence.charAt(index) == '0')
                    trailingZeroCount++;
                else {
                    trailingZeroCount = 0;
                }
            }
            hasFractionalPart = true;
        } else {
            hasFractionalPart = false;
        }

    }

    @Override
    public void afterTextChanged(final Editable editable) {
        final TextWatcher textWatcher = this;

        timer.cancel();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        editText.removeTextChangedListener(textWatcher);
                        Number valueNumber = 0;
                        String valueString = "";
                        try {
                            int inilen, endlen;
                            inilen = editText.getText().length();

                            String v = editable.toString()
                                    .replace(String.valueOf(
                                            GetNumberFractionFormat()
                                                    .getDecimalFormatSymbols()
                                                    .getGroupingSeparator()), "");
                            valueNumber = GetNumberFractionFormat().parse(v);
                            int cp = editText.getSelectionStart();
                            if (hasFractionalPart) {
                                StringBuilder trailingZeros = new StringBuilder();
                                while (trailingZeroCount-- > 0)
                                    trailingZeros.append('0');
                                valueString = GetNumberFractionFormat().format(valueNumber) + trailingZeros.toString();
                            } else
                                valueString = GetNumberWithoutFractionFormat().format(valueNumber);

                            editText.setText(valueString);
                            endlen = editText.getText().length();
                            int sel = (cp + (endlen - inilen));
                            if (sel > 0 && sel <= editText.getText().length()) {
                                editText.setSelection(sel);
                            } else {
                                // place cursor at the end?
                                editText.setSelection(editText.getText().length() - 1);
                            }
                        } catch (NumberFormatException nfe) {
                            // do nothing?
                        } catch (ParseException e) {
                            // do nothing?
                        }

                        editText.addTextChangedListener(textWatcher);
                        if (numberHandler != null) {
                            numberHandler.AfterTextChanged(editText, valueNumber, valueString);
                        }
                    }
                });
            }
        }, delayTime);

    }

    public interface INumberHandler {
        void AfterTextChanged(EditText editText, Number valueNumber, String valueString);
    }
}
