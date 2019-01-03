package com.jvt.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.Player.Core.PlayerClient;
import com.Player.Source.TSearchDev;
import com.jvt.MyApplication;
import com.jvt.R;
import com.jvt.bean.SearchDeviceInfo;
import com.jvt.ui.adapter.SearchAdapter;
import com.jvt.ui.dialog.ShowProgress;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liaolei on 2018/12/28.
 */
public class SearchActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    public ShowProgress pd;
    MyApplication appMain;
    private ListView listView;
    private SearchAdapter sAdapter;
    public static ArrayList<SearchDeviceInfo> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        appMain = (MyApplication) this.getApplication();
        listView = (ListView) findViewById(R.id.lvLive);
        listView.setVisibility(View.INVISIBLE);
        sAdapter = new SearchAdapter(mContext);
        listView.setAdapter(sAdapter);
        listView.setOnItemClickListener(this);

        Button back = findViewById(R.id.back_btn);
        Button search = findViewById(R.id.menu_btn1);

        back.requestFocus();
        back.setNextFocusDownId(R.id.lvLive);
        back.setNextFocusRightId(R.id.menu_btn1);

        search.setNextFocusDownId(R.id.lvLive);
        search.setNextFocusLeftId(R.id.back_btn);

        listView.setNextFocusUpId(R.id.back_btn);


        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
        findViewById(R.id.menu_btn1).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new ThreadSearchDevice().execute();
            }
        });
        new ThreadSearchDevice().execute();

    }

    @Override
    protected void onResume() {
        if (sAdapter != null) {
            sAdapter.notifyDataSetChanged();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SearchDeviceInfo node = list.get(position);
        String name = node.getsDevName();
        if (TextUtils.isEmpty(name)) {
            //name=node.getDwVendorId()
            name = node.sDevModel;
        }
        Intent Intent = new Intent()
                .putExtra("deviceName", name)
                .putExtra("umid", node.getsDevId())
                .putExtra("channels", node.usChNum)
                .putExtra("node", node);

        setResult(Activity.RESULT_OK, Intent);
        finish();
    }

    public class ThreadSearchDevice extends
            AsyncTask<Void, Void, List<SearchDeviceInfo>> {

        @Override
        protected List<SearchDeviceInfo> doInBackground(Void... params) {
            // TODO Auto-generated method stub
            list = new ArrayList<SearchDeviceInfo>();
            PlayerClient playerclient = appMain.getPlayerclient();
            int searchRet = playerclient.StartSearchDev(10);// 5代表等待多少秒
            for (int i = 0; i < searchRet; i++) {
                TSearchDev tsearch = playerclient.SearchDevByIndex(i);
                Log.w("searchRet", "UMId :" + tsearch.sIpaddr_1 + " , "
                        + tsearch.sCloudServerAddr + " , " + tsearch.sDevName);
                SearchDeviceInfo searchInfo = new SearchDeviceInfo(
                        tsearch.dwVendorId, tsearch.sDevName, tsearch.sDevId,
                        tsearch.sDevUserName, tsearch.bIfSetPwd,
                        tsearch.bIfEnableDhcp, tsearch.sAdapterName_1,
                        tsearch.sAdapterMac_1, tsearch.sIpaddr_1,
                        tsearch.sNetmask_1, tsearch.sGateway_1,
                        tsearch.usChNum, tsearch.iDevPort, tsearch.sDevModel);
                list.add(searchInfo);

            }
            playerclient.StopSearchDev();
            return list;
        }

        @Override
        protected void onPostExecute(List<SearchDeviceInfo> flist) {
            // TODO Auto-generated method stub
            pd.dismiss();
            if (list.size() > 0) {
                listView.setVisibility(View.VISIBLE);
                sAdapter.setNodeList(flist);
                // listView.startLayoutAnimation();
            } else {
                listView.setVisibility(View.INVISIBLE);
                showToast(getResources().getString(R.string.nodataerro));
            }

            super.onPostExecute(list);
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            if (pd == null) {
                pd = new ShowProgress(mContext);
                pd.setMessage(mContext.getResources().getString(
                        R.string.searching_device));
                pd.setCanceledOnTouchOutside(false);
            }
            pd.show();
            super.onPreExecute();
        }
    }
}

