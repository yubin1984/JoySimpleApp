package com.yubin.simpleapp.net;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;

import com.yubin.simpleapp.R;


public class MyLoadDialog extends Dialog {

    public MyLoadDialog(@NonNull Context context) {
        super(context, R.style.MyLoadDialog);
        setContentView(R.layout.dialog_load_dialog);
        setCancelable(false);
    }

}
