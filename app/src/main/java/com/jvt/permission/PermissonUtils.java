package com.jvt.permission;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.jvt.R;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;


public class PermissonUtils {
    public static String TAG = "Permission";
    public static Dialog dialog;
    private static PermissonUtils permissonUtil;
    private PermissonUtils(){}

    public static PermissonUtils getInstance(){
        if(permissonUtil == null){
            permissonUtil = new PermissonUtils();
        }
        return permissonUtil;
    }
    public void requestPermissions(final Activity activity, final PermissionCallback callback, final String... permissions) {
        RxPermissions rxPermission = new RxPermissions(activity);
        rxPermission.request(permissions).subscribeOn(AndroidSchedulers.mainThread())//一次获取全部权限
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean flag) {
                if (flag) {
                    if(callback != null){
                        callback.PermissionResult(true);
                    }
                } else{
                    dialog =  createPromissionDialog(activity, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (v.getId() == R.id.permission_cancel_btn) {
                                dialog.dismiss();
                            } else {
                                dialog.dismiss();
                                goAppDetailSettingIntent(activity);
                            }
                        }
                    });
                    if(!dialog.isShowing()){
                        dialog.show();
                    }
                    if(callback != null){
                        callback.PermissionResult(false);
                    }
                }
            }
        });
    }
    private static Dialog createPromissionDialog(Context context, View.OnClickListener listener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.permision_dialog);
        View view = LayoutInflater.from(context).inflate(R.layout.permission_tip, null);
        builder.create();
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView content = (TextView) view.findViewById(R.id.content);
        content.setText(context.getString(R.string.permission_content,context.getString(R.string.app_name)));
        final TextView cancel = (TextView) view.findViewById(R.id.permission_cancel_btn);
        final TextView commit = (TextView) view.findViewById(R.id.commit_btn);
        cancel.setOnClickListener(listener);
        commit.setOnClickListener(listener);
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.setView(view,0,0,0,0);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }
    public static Dialog createDeleteDeviceDialog(Context context, View.OnClickListener listener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.permision_dialog);
        View view = LayoutInflater.from(context).inflate(R.layout.delete_device_tip, null);
        builder.create();
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView content = (TextView) view.findViewById(R.id.content);
        final TextView cancel = (TextView) view.findViewById(R.id.permission_cancel_btn);
        final TextView commit = (TextView) view.findViewById(R.id.commit_btn);
        cancel.setOnClickListener(listener);
        commit.setOnClickListener(listener);
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.setView(view,0,0,0,0);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    public static Dialog createSelectPageDialog(Context context, View.OnClickListener listener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.permision_dialog);
        View view = LayoutInflater.from(context).inflate(R.layout.select_page_tip, null);
        builder.create();;
        final TextView cancel = (TextView) view.findViewById(R.id.permission_cancel_btn);
        final TextView commit = (TextView) view.findViewById(R.id.commit_btn);
        cancel.setOnClickListener(listener);
        commit.setOnClickListener(listener);
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.setView(view,0,0,0,0);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }
    private static void goAppDetailSettingIntent(Context context){
        Intent localIntent=new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(Build.VERSION.SDK_INT>=9){
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package",context.getPackageName(),null));
        }else if(Build.VERSION.SDK_INT<=8){
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings","com.android.setting.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName",context.getPackageName());
        }
        context.startActivity(localIntent);
    }

}
