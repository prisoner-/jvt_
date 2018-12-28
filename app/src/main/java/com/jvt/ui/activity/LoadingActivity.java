package com.jvt.ui.activity;

import android.os.Bundle;

import com.jvt.R;
import com.jvt.presenter.LoadingPresenter;

/**
 * Created by liaolei on 2018/12/18.
 */
public class LoadingActivity extends BaseActivity {

//    private ClientCore mClientCore;
//    private MyApplication application;

    LoadingPresenter presenter;

//    /**
//     * TODO 内部类会保持所在外部类的引用导致内存泄漏，当activity结束时，被延迟的message会继续存活主线程10分钟，导致activity无法被回收
//     * 应该将handler声明为static类，并对外使用弱引用
//     */
//    Handler mBestServerHandler = new Handler() {
//
//        @Override
//        public void handleMessage(Message msg) {
//            final ResponseServer responseServer = (ResponseServer) msg.obj;
//            if (responseServer != null && responseServer.h != null) {
//                if (responseServer.h.e == 200) {
////                    goToMain();
//                    getDevList();
//                } else {
//                    Log.e(WebSdkApi_Error, "获取服务器失败! code=" + responseServer.h.e);
//                    showToast(getString(R.string.connectserverfail));
//
//                }
//            } else {
//                Log.e(WebSdkApi_Error, "获取服务器失败! error=" + msg.what);
//            }
////            actionToLogin();
//            super.handleMessage(msg);
//        }
//    };
//
//    Handler mGetDevListHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            parseDevList(msg);
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
//        application = (MyApplication) getApplication();
        presenter=new LoadingPresenter(mContext);
//        startLogWrite();
//        initClientCore();
    }

//    private void startLogWrite() {
//        if (PermissionUtils.checkStoragePermission(this)) { //检查文件读写权限
//
//            LogOut.IniteWriteLog(this);
//            try {
//                application.getWriteLogThread().start();// 开启写入线程
//            } catch (Exception e) {
//                Log.e("LL Test write_log_error", e.toString());
//            }
//        }
//    }
//
//    /**
//     * 初始化启动参数
//     */
//    private void initClientCore() {
//        mClientCore = ClientCore.getInstance();
//        mClientCore.setLocalList(true);
//        getBestServer();
//    }
//
//    private void getDevList() {
//        mClientCore.getNodeList(true, "", 0, 0, mGetDevListHandler);
//    }
//
//    private void parseDevList(Message msg) {
//        ResponseDevList responseDevList = (ResponseDevList) msg.obj;
//        if (responseDevList != null && responseDevList.h != null) {
//            if (responseDevList.h.e == 200) {
//                List<DevItemInfo> items = responseDevList.b.nodes;
//                List<PlayNode> nodeList = new ArrayList<PlayNode>();
//                for (int i = 0; i < items.size(); i++) {
//                    DevItemInfo devItemInfo = items.get(i);
//                    if (devItemInfo != null) {
//                        PlayNode node = PlayNode.ChangeData(devItemInfo);
//                        nodeList.add(node);
//                    }
//                }
//                CommonData.GetChildrenRoute(nodeList);
//                application.setNodeList(nodeList);
//                ClientCore.getInstance().preConnectDev(items, 10);
//                Log.d("LL Test", "获取设备列表成功!size=" + nodeList.size());
//            } else {
//                Log.e("LL Test", "获取设备列表失败!code=" + responseDevList.h.e);
//            }
//        } else {
//            Log.e("LL Test", "获取设备列表失败!code=" + responseDevList.h.e);
//        }
//        goToMain();
////        Config.InitUserDir(StreamData.defaultServerAddress, LOCALUSER, "");
//
//    }
//
//    /**
//     * 获取最佳服务器
//     */
//    private void getBestServer() {
//        int language = Utility.isZh(this);
//        mClientCore.setupHost(mContext, Const.WebAddress, 0,
//                Utils.getImsi(this), language, Const.CustomName,
//                Utils.getVersionName(this), "", "");
//
//        mClientCore.getCurrentBestServer(this, mBestServerHandler);
//    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mClientCore = null;
    }
}
