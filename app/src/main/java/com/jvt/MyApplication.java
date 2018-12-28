package com.jvt;

import android.app.Application;

import com.Player.Core.PlayerClient;
import com.Player.Core.UserImg.UserImgEntity.UserImgCompareInfo;
import com.jvt.bean.PlayNode;
import com.jvt.utils.CommonData;
import com.jvt.utils.LimitQueue;
import com.jvt.utils.WriteLogThread;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liaolei on 2018/12/18.
 */
public class MyApplication extends Application {

    private List<PlayNode> NodeList = new ArrayList<PlayNode>();

    public static String currentPlayDeviceId = "";

    WriteLogThread writeLogThread;

    PlayerClient playerClient;

    public LimitQueue<UserImgCompareInfo> compareInfoLimitQueue = new LimitQueue<>(10);

    @Override
    public void onCreate() {
        super.onCreate();

        playerClient = new PlayerClient();
    }

    public synchronized List<PlayNode> getNodeList() {
        return NodeList;
    }

    public synchronized List<PlayNode> getDvrAndCamera() {
        List<PlayNode> list = new ArrayList<PlayNode>();
        for (int i = 0; i < NodeList.size(); i++) {
            PlayNode dvrNode = NodeList.get(i);
            if (dvrNode.IsDvr()) {
                list.add(dvrNode);
                if (dvrNode.isExanble) {
                    for (int j = 0; j < NodeList.size(); j++) {
                        PlayNode cameraNode = NodeList.get(j);
                        if (cameraNode.getParentId().equals(dvrNode.getNode().dwNodeId)
                                && cameraNode.isCamera()) {
                            cameraNode.parentIsDvr = true;
                            list.add(cameraNode);
                        }
                    }
                }
            }
        }
        return list;
    }

    public PlayerClient getPlayerclient() {
        return playerClient;
    }

    public synchronized List<PlayNode> getAllChildNodeList() {
        return CommonData.GetAllChildren(NodeList);
    }

    public synchronized void setNodeList(List<PlayNode> nodeList) {
        List<PlayNode> list = new ArrayList<PlayNode>();
        for (int i = 0; i < nodeList.size(); i++) {
            if (nodeList.get(i).isCamera() || nodeList.get(i).IsDvr()) {
                list.add(nodeList.get(i));
            }
        }
        NodeList = list;
    }


    public WriteLogThread getWriteLogThread() {
        WriteLogThread.isRun = false;
        writeLogThread = new WriteLogThread(this);
        return writeLogThread;
    }
}
