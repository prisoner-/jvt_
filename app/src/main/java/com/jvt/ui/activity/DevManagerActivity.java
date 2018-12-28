package com.jvt.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.Player.web.response.DevItemInfo;
import com.Player.web.response.ResponseCommon;
import com.Player.web.response.ResponseDevList;
import com.Player.web.websocket.ClientCore;
import com.jvt.MyApplication;
import com.jvt.R;
import com.jvt.bean.PlayNode;
import com.jvt.ui.adapter.DevListAdapter;
import com.jvt.utils.CommonData;

import java.util.ArrayList;
import java.util.List;

public class DevManagerActivity extends BaseActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, SwipeRefreshLayout.OnRefreshListener {
    public static int requestCode = 10;
    public final int OK = 0;
    public final int ERROR = 1;
    public static final int CONNECT_DEVICE_SUCCESS = 0x99;
    public static final int CONNECT_DEVICE_FAIL = 0x98;

    private ListView listView;

    public DevListAdapter devListAdapter;

    public List<PlayNode> nodeList = new ArrayList<PlayNode>();

    public MyApplication application;

    private Button btnMenu;

    private Button btnAdd;

//    private boolean deviceOnline = false;


    private List<PlayNode> addToPlayNodeList = new ArrayList<PlayNode>();

//    OnClickItemToAddListener onClickItemToAddListener;

    private SwipeRefreshLayout swipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dev_manager);

        application = (MyApplication) getApplication();
        addToPlayNodeList.clear();
        initeView();

//        RefreshListView();
        requestNodeList();
//        ViewGroup parent = (ViewGroup) getParent();
//        if (parent != null) {
//            parent.removeView(view);
//        }
    }

    /**
     * 下载设备列表
     */
    @SuppressLint("HandlerLeak")
    private Handler GetDevListhandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            getDevList(msg);
        }
    };

    /**
     * 删除设备
     */
    public Handler hander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case OK:
                    showToast(getString(R.string.delete_success));
                    setResult(RESULT_OK);
                    finish();
                    break;
                case ERROR:
                    showToast(getString(R.string.delete_failed));
                    break;
                case CONNECT_DEVICE_SUCCESS:
//                    deviceOnline = true;
                    break;
                case CONNECT_DEVICE_FAIL:
//                    deviceOnline = false;
                    showToast(getString(R.string.device_off_line));
                    break;
                default:
                    break;
            }
            progressDialog.dismiss();
        }
    };


    @Override
    public void onResume() {
        Log.w("LL Test onResume", "onResume--------->FgListManagerNew");

        super.onResume();
    }

//    public void setOnClickItemToAddListener(
//            OnClickItemToAddListener onClickItemToAddListener) {
//        this.onClickItemToAddListener = onClickItemToAddListener;
//}

    public void initeView() {
        btnMenu = (Button) findViewById(R.id.menu_btn);
        btnAdd = (Button) findViewById(R.id.btn_add);
        btnMenu.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        listView = (ListView) findViewById(R.id.lvLive);
        devListAdapter = new DevListAdapter(mContext);
//        devListAdapter.setOnClickItemToAddListener(onClickItemToAddListener);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        listView.setAdapter(devListAdapter);
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }

    public void RefreshListView() {
        Log.d("LL Test RefreshListView", "RefreshListView---->");
//        if (application != null) {
        new GetList1().execute();
//        }
    }

    class GetList1 extends AsyncTask<Void, Void, Void> {

        public GetList1() {
            super();
            // TODO Auto-generated constructor stub
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            nodeList = application.getAllChildNodeList();
            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            if (swipeLayout.isRefreshing()) {
                swipeLayout.setRefreshing(false);
            }
            devListAdapter.setNodeList(nodeList);
            super.onPostExecute(result);
        }

    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        Log.i("onPause", "onPause---->FgListManagerNew");
    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        addToPlayNodeList.clear();
        super.onStop();
        Log.i("slide", "liveFragment--onStop");
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.menu_btn:
//                back();

                finish();
                break;
            case R.id.btn_add:
                Intent it1 = new Intent(mContext, AddDevActivity.class);
                startActivityForResult(it1, requestCode);
                break;
        }
    }

//    public void back() {
//        AcMain acMain = (AcMain) con;
//        if (AcMain.isChooseToPlay) {
//            acMain.fragmentTransaction(0);
//        } else {
//            if (sm != null) {
//                sm.showMenu();
//            }
//        }
//    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub
        PlayNode node = nodeList.get(arg2);
        if (node.IsDvr()) {
            node.isExanble = !node.isExanble;
            for (int i = 0; i < application.getNodeList().size(); i++) {
                if (node.getNode().dwNodeId.equals(application.getNodeList().get(i).getNode().dwNodeId)) {
                    application.getNodeList().get(i).isExanble = node.isExanble;
                    break;
                }
            }
            RefreshListView();
        } else if (node.isCamera()) {
            //回到播放页面播放识别
            Intent intent = new Intent();
            intent.putExtra("node", node);
            setResult(0, intent);
            finish();
        }
    }

    /**
     * @author Administrator
     */

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        ShowDeleteDialog(nodeList.get(position));
        return true;
    }

    /**
     * 删除摄像机
     */
    private void ShowDeleteDialog(PlayNode node) {
        AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setTitle(R.string.delete)
                .setMessage(R.string.delete_tips)
                .setPositiveButton(R.string.positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteDevice(node);
                    }
                }).setNegativeButton(R.string.negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }


    void deleteDevice(PlayNode node) {
        progressDialog.show();
        ClientCore clientCore = ClientCore.getInstance();
        clientCore.deleteNodeInfo(node.node.dwNodeId,
                node.node.iNodeType, node.node.id_type, new Handler() {

                    @Override
                    public void handleMessage(Message msg) {
                        ResponseCommon responseCommon = (ResponseCommon) msg.obj;
                        if (responseCommon != null && responseCommon.h != null) {
                            if (responseCommon.h.e == 200) {
                                hander.sendEmptyMessage(OK);
                            } else {
                                Log.e("LL Test  deleteNodeInfo", " 删除设备设备失败!code="
                                        + responseCommon.h.e);
                                hander.sendEmptyMessage(ERROR);
                            }
                        } else {
                            Log.e("LL Test deleteNodeInfo", " 删除设备设备失败! error="
                                    + msg.what);
                            hander.sendEmptyMessage(ERROR);
                        }
                        super.handleMessage(msg);
                    }

                });
    }

    @Override
    public void onRefresh() {
        // TODO Auto-generated method stub
        requestNodeList();
    }

    private void requestNodeList() {
        ClientCore clientCore = ClientCore.getInstance();
        clientCore.getNodeList(true,"", 0, 0, GetDevListhandler);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        Log.d("LL Test ", "fgListonActivityResult: requestCode=" + requestCode + ",resultCode=" + resultCode);
        if (requestCode == requestCode && resultCode == Activity.RESULT_OK) {
            requestNodeList();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 处理设备列表
     *
     * @param msg
     */
    void getDevList(Message msg) {
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
                Log.e("LL Test getNodeList", "获取设备列表成功!size=" + nodeList.size());
                RefreshListView();
            } else {
                Log.e("LL Test getNodeList", "获取设备列表失败!code=" + responseDevList.h.e);
            }
        } else {
            Log.e("LL Test getNodeList", "获取设备列表失败! error=" + msg.what);
        }
    }
}
