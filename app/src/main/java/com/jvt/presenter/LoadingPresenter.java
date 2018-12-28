package com.jvt.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.Player.Core.PlayerClient;
import com.Player.Source.LogOut;
import com.Player.Source.TSearchDev;
import com.Player.web.response.DevItemInfo;
import com.Player.web.response.ResponseDevList;
import com.Player.web.response.ResponseServer;
import com.Player.web.websocket.ClientCore;
import com.Player.web.websocket.PermissionUtils;
import com.jvt.MyApplication;
import com.jvt.R;
import com.jvt.bean.PlayNode;
import com.jvt.bean.SearchDeviceInfo;
import com.jvt.ui.activity.LoadingActivity;
import com.jvt.ui.activity.MainActivity;
import com.jvt.ui.dialog.ShowProgress;
import com.jvt.utils.CommonData;
import com.jvt.utils.Const;
import com.jvt.utils.Utility;
import com.jvt.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.Player.web.websocket.ChainClient.WebSdkApi_Error;


/**
 * Created by liaolei on 2018/12/25.
 */
public class LoadingPresenter {

    private ClientCore mClientCore;
    private MyApplication application;
    List<SearchDeviceInfo> list;

    Context mContext;
    LoadingActivity activity;

    ShowProgress pd;

    public LoadingPresenter(Context ctx) {
        mContext = ctx;
        activity = (LoadingActivity) ctx;
        application = (MyApplication) activity.getApplication();
        startLogWrite();
        initClientCore();
    }

    /**
     * TODO 内部类会保持所在外部类的引用导致内存泄漏，当activity结束时，被延迟的message会继续存活主线程10分钟，导致activity无法被回收
     * 应该将handler声明为static类，并对外使用弱引用
     */
    Handler mBestServerHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            final ResponseServer responseServer = (ResponseServer) msg.obj;
            if (responseServer != null && responseServer.h != null) {
                if (responseServer.h.e == 200) {
//                    goToMain();
                    getDevList();
                } else {
                    Log.e(WebSdkApi_Error, "获取服务器失败! code=" + responseServer.h.e);
                    activity.showToast(mContext.getString(R.string.connectserverfail));

                }
            } else {
                Log.e(WebSdkApi_Error, "获取服务器失败! error=" + msg.what);
            }
//            actionToLogin();
            super.handleMessage(msg);
        }
    };

    Handler mGetDevListHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            parseDevList(msg);
        }
    };

    private void startLogWrite() {
        if (PermissionUtils.checkStoragePermission(activity)) { //检查文件读写权限

            LogOut.IniteWriteLog(mContext);
            try {
                application.getWriteLogThread().start();// 开启写入线程
            } catch (Exception e) {
                Log.e("LL Test write_log_error", e.toString());
            }
        }
    }

    /**
     * 初始化启动参数
     */
    private void initClientCore() {
        mClientCore = ClientCore.getInstance();
        mClientCore.setLocalList(true);
        getBestServer();
    }

    private void getDevList() {
        mClientCore.getNodeList(true, "", 0, 0, mGetDevListHandler);
    }

    private void parseDevList(Message msg) {
        ResponseDevList responseDevList = (ResponseDevList) msg.obj;
        if (responseDevList != null && responseDevList.h != null) {
            if (responseDevList.h.e == 200) {
                List<DevItemInfo> items = responseDevList.b.nodes;
                List<PlayNode> nodeList = new ArrayList<PlayNode>();
                for (int i = 0; i < items.size(); i++) {
                    DevItemInfo devItemInfo = items.get(i);
                    if (devItemInfo != null) {
                        PlayNode node = PlayNode.ChangeData(devItemInfo);
                        nodeList.add(node);
                    }
                }
                CommonData.GetChildrenRoute(nodeList);
                application.setNodeList(nodeList);
                ClientCore.getInstance().preConnectDev(items, 10);
                Log.d("LL Test", "获取设备列表成功!size=" + nodeList.size());
            } else {
                Log.e("LL Test", "获取设备列表失败!code=" + responseDevList.h.e);
            }
        } else {
            Log.e("LL Test", "获取设备列表失败!code=" + responseDevList.h.e);
        }
        goToMain();
//        Config.InitUserDir(StreamData.defaultServerAddress, LOCALUSER, "");

    }

    private void goToMain() {
        mClientCore.setLocalList(true);
        mContext.startActivity(new Intent(mContext, MainActivity.class));
        activity.finish();
    }

    /**
     * 获取最佳服务器
     */
    private void getBestServer() {
        int language = Utility.isZh(mContext);
        mClientCore.setupHost(mContext, Const.WebAddress, 0,
                Utils.getImsi(mContext), language, Const.CustomName,
                Utils.getVersionName(mContext), "", "");

        mClientCore.getCurrentBestServer(mContext, mBestServerHandler);
    }

    public class ThreadSearchDevice extends
            AsyncTask<Void, Void, List<SearchDeviceInfo>> {

        @Override
        protected List<SearchDeviceInfo> doInBackground(Void... params) {
            // TODO Auto-generated method stub
            list = new ArrayList<SearchDeviceInfo>();
            PlayerClient playerclient = application.getPlayerclient();
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
//            pd.dismiss();
            activity.showToast(mContext.getResources().getString(R.string.nodataerro));
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
