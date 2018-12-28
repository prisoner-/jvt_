package com.jvt.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.Player.Core.PlayerClient;
import com.Player.Source.TFileListNode;
import com.Player.web.response.ResponseCommon;
import com.Player.web.websocket.ClientCore;

public class AddThread

{
    public final static int OK = 0;
    public final static int ERROR = 1;
    public final static int update = 10;
    Handler handler;
    String deviceName;
    String Umid;
    int channels;
    String username;
    String pass;
    int stream;
    String address;
    int port;
    int vendor;
    TFileListNode parent;
    int mode = 0; // 0表示添加云，1表示添加直连
    boolean isCamera;
    ClientCore clientCore;
    Context context;

    public AddThread(Context context, boolean isCamera, TFileListNode parent,
                     Handler handler, String deviceName, String umid, int channels,
                     String username, String pass, int stream) {

        this.isCamera = isCamera;
        this.parent = parent;
        this.stream = stream;
        this.channels = channels;
        this.handler = handler;
        this.deviceName = deviceName;
        this.Umid = umid;
        this.pass = pass;
        this.username = username;
        mode = 0;
        this.context = context;
    }

    public AddThread(Context context, boolean isCamera, TFileListNode parent,
                     Handler handler, String deviceName, String address, String port,
                     int vendor, int channels, String username, String pass, int stream) {

        this.isCamera = isCamera;
        this.parent = parent;
        this.stream = stream;
        this.channels = channels;
        this.handler = handler;
        this.deviceName = deviceName;
        this.address = address;
        this.port = Integer.parseInt(port);
        this.vendor = vendor;
        this.pass = pass;
        this.username = username;
        mode = 1;
        this.context = context;
    }

    int vendorID(String umid) {
        if (umid.length() == 16) {
            return PlayerClient.NPC_D_MON_VENDOR_ID_HZXM;
        } else if (umid.substring(0, 2).contains("xm")
                || umid.substring(0, 2).contains("Xm")
                || umid.substring(0, 2).contains("xM")) {
            return PlayerClient.NPC_D_MON_VENDOR_ID_HZXM;
        } else
            return PlayerClient.NPC_D_MON_VENDOR_ID_UMSP;

    }

    public void start() {
        // TODO Auto-generated method stub

        int node_type = 1;
        if (isCamera) {
            node_type = 2;
        }
        int conn_mode = 0;
        int vendor_id = 1009;
        if (mode == 0) {
            conn_mode = 2;
            vendor_id = vendorID(Umid);
        } else
            vendor_id = vendor;
        String parentNodeId = "";
        if (parent != null) {
            parentNodeId = parent.dwNodeId;
        }
        ClientCore clientCore = ClientCore.getInstance();
        clientCore.addNodeInfo(deviceName, parentNodeId, node_type, conn_mode,
                vendor_id, Umid, address, port, username, pass, channels,
                channels, stream, 0,new Handler() {

                    @Override
                    public void handleMessage(Message msg) {
                        ResponseCommon responseCommon = (ResponseCommon) msg.obj;
                        if (responseCommon != null && responseCommon.h != null) {
                            if (responseCommon.h.e == 200) {
                                handler.sendEmptyMessage(OK);
                            } else {
                                Log.e("addNodeInfo", "添加设备失败!code=" + responseCommon.h.e);
                                handler.sendEmptyMessage(ERROR);
                            }
                        } else {
                            Log.e("addNodeInfo", "添加设备失败! error=" + msg.what);
                            handler.sendEmptyMessage(ERROR);
                        }
                        super.handleMessage(msg);
                    }

                });
    }
}
