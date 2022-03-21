package com.yubin.simpleapp.net;

import android.content.Context;

public class LoadDialog {
    private static MyLoadDialog dialog;

    public static MyLoadDialog getDialog() {
        return dialog;
    }

    private static void initDialog(Context context) {
        if (context == null) {
            return;
        }
        dialog = new MyLoadDialog(context);

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
    }

    public static void show(Context context) {
        if (dialog == null) {
            initDialog(context);
            if (dialog != null && !dialog.isShowing()) {
                dialog.show();
            }
        }

    }

    public static void dismiss() {

        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }
}
