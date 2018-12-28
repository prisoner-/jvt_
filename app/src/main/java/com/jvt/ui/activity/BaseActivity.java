package com.jvt.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jvt.ui.dialog.ShowProgress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by liaolei on 2018/12/18.
 */
public class BaseActivity extends AppCompatActivity {
    Context mContext;
    Map<String, Activity> mActivityMap = new HashMap<>();
    ShowProgress progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        progressDialog = new ShowProgress(mContext);
    }

    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addActivity(Activity activity) {
        if (mActivityMap != null) {
            mActivityMap.put(activity.getClass().getName(), activity);
        }
    }

    public void removeActivity(Activity activity) {
        if (mActivityMap != null) {
            if (!activity.isFinishing()) {
                activity.finish();
                mActivityMap.remove(activity);
            }
        }
    }

    public void removeAllActivity() {
        if (mActivityMap != null) {
            for (Activity activity : mActivityMap.values()) {
                removeActivity(activity);
            }
        }
    }


    @Override
    public void finish() {
        super.finish();
        removeActivity(this);
    }
}
