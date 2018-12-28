package com.jvt.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.Player.Core.PlayerClient;
import com.Player.Source.Date_Time;
import com.Player.Source.TAlarmSetInfor;
import com.Player.Source.TFileListNode;
import com.Player.Source.TVideoFile;
import com.jvt.MyApplication;
import com.jvt.R;
import com.jvt.bean.DeviceInfo;
import com.jvt.bean.MessageInfo;
import com.jvt.bean.PlayNode;
import com.jvt.bean.SearchDeviceInfo;

/**
 * 存储一些共同使用的数据
 *
 * @author Simula
 *
 */
public class CommonData {

    /**
     * 成员变量路径route使用的分割符
     */
    public static final String ROUTE_SEPERATOR = "###";
    public static final String FAVORITE_SEPERATOR = "@";

    // public static PlayerClient playerclient;
    /**
     * 存储播放目录与播放点,整个应用在服务上获取的数据都存储于此
     */
    // public static List<PlayNode> NodeList = new ArrayList<PlayNode>();
    /**
     * 当前播放列表，播放页面将利用这个数据列表来进行播放
     */
    public static List<PlayNode> PlayList = new ArrayList<PlayNode>();

    /**
     * 播放远程录像时的文件
     */
    public static TVideoFile VideoFile;
    /**
     * 播放远程录像时的设备ID
     */
    public static String VideoDeviceId;

    public static final int SERVICE_ALARM = 9;
    public static final int ALARMLIST_UPDATA_NODE = 8;
    public static final int DEVLIST_DEL_NODE = 7;

    public static synchronized boolean GetDataFromServer(
            PlayerClient playerclient, MyApplication appMain) {
        boolean HaveData = false;
        if (playerclient != null) {
            if (appMain.getNodeList() != null) {
                List<PlayNode> tempNodeList = new ArrayList<PlayNode>();
                HaveData = GetDevList(playerclient, 0, tempNodeList); // 获取设备列表
                GetChildCountAndLanLon(tempNodeList);
                // CommonData.InitLonLat(NodeList);
                GetChildrenRoute(tempNodeList);
                GetLevel(tempNodeList);
                appMain.setNodeList(tempNodeList);
                // if (tempNodeList.size() == 0) {
                // if (appMain.getNodeList().size() == 1) {
                //
                // appMain.setNodeList(tempNodeList);
                // }
                // } else {
                // appMain.setNodeList(tempNodeList);
                // }

            } else {
                // playerclient.LoginEx();

            }
        }

        return HaveData;
    }

    public static void GetFavorite(Context con, List<PlayNode> nodeList) {
        SharedPreferences pref = con.getSharedPreferences("favorite",
                Context.MODE_PRIVATE);
        Map<String, ?> map = pref.getAll();
        Object[] keySet = map.keySet().toArray();
        for (int i = 0; i < keySet.length; i++) {
            for (int j = 0; j < nodeList.size(); j++) {
                String id = nodeList.get(j).getDeviceId();
                if (keySet[i].equals(id)) {
                    nodeList.get(j).isFavorite = true;
                    Log.i("nodeNamelist", "NodeList.get(j):	"
                            + nodeList.get(j).getName());
                    break;
                }
            }

        }
    }

    public static boolean HasAllPower(PlayerClient playerclient) {
        return false;

    }

    /**
     * 获取设备列表 获取成功返回true,否则返回false
     *
     * @param
     * @return
     */
    public static boolean GetDevList(PlayerClient playerclient,
                                     int dwQueryNodeId, List<PlayNode> NodeList) {
        return false;

    }

    // /**
    // * 将一些找不到父节点的点的parentId设置为0
    // */
    // public static void RepairRoot()
    // {
    // for(int i=0;i<NodeList.size();i++)
    // {
    // PlayNode node=GetParent(NodeList.get(i).node.dwNodeId);
    // if(node==null)
    // {
    // NodeList.get(i).node.dwParentNodeId=0;
    // }
    // }
    // }
    /**
     * 得到level层相应的节点
     *
     * @param level
     * @return
     */
    public static List<PlayNode> GetList(int level, String parentId,
                                         List<PlayNode> NodeList) {
        List<PlayNode> list = new ArrayList<PlayNode>();
        Log.e("size", "" + NodeList.size());
        for (int i = 0; i < NodeList.size(); i++) {
            if (NodeList.get(i).getLevel() == level
                    && NodeList.get(i).node.dwParentNodeId.equals(parentId))
                list.add(NodeList.get(i));
        }
        return list;
    }

    /**
     * 得到nodeId层相应的节点
     *
//     * @param level
     * @return
     */
    public static List<PlayNode> GetList(String nodeId, List<PlayNode> NodeList) {
        List<PlayNode> list = new ArrayList<PlayNode>();
        Log.e("size", "" + NodeList.size());
        for (int i = 0; i < NodeList.size(); i++) {
            if (NodeList.get(i).node.dwParentNodeId.equals(nodeId))
                list.add(NodeList.get(i));
            Log.i("nodeName", "NodeList.get(i):" + NodeList.get(i).getName());
        }
        return list;
    }


    /**
     * 获取设备组下的子节点
     *
     * @param playNode
     *
     */
    public static List<PlayNode> GetDevChildrenList(PlayNode playNode,
                                                    List<PlayNode> NodeList) {
        List<PlayNode> list = new ArrayList<PlayNode>();
        if (playNode.IsDirectory() || playNode.IsDvr())// 找到的目录
        {
            for (int i = 0; i < NodeList.size(); i++)// 查找node下的直接子节点
            {
                PlayNode temp = NodeList.get(i);
                if (temp.node.dwParentNodeId.equals(playNode.node.dwNodeId)) {
                    list.add(temp);
                }
            }
        }
        return list;
    }

    /**
     * 找到节点ID的父节点
     *
     * @param id
     *            节点ID
     * @return 如果找不到，则返回null,否则返回相应父节点
     */
    public static PlayNode GetParent(String id, List<PlayNode> NodeList) {
        String parent = "-100";
        int i;
        for (i = 0; i < NodeList.size(); i++) {
            if (NodeList.get(i).node.dwNodeId .equals(id)) {
                parent = NodeList.get(i).node.dwParentNodeId;
                break;
            }
        }
        if (i == NodeList.size()) {
            return null;
        }
        for (int k = 0; k < NodeList.size(); k++) {
            if (NodeList.get(k).node.dwNodeId.equals(parent)) {
                return NodeList.get(k);
            }
        }
        return null;
    }

    /**
     * 获取当前目录节点
     *
     * @param parentid
     * @return
     */
    public static PlayNode GetCurrentParent(String parentid, List<PlayNode> NodeList) {
        int i;
        for (i = 0; i < NodeList.size(); i++) {
            if (NodeList.get(i).node.dwNodeId.equals(parentid)) {
                return NodeList.get(i);
            }
        }
        return null;
    }

    /**
     * 得到父节点下面直属子节点的数目及是否包含有经纬度信息
     */
    public static void GetChildCountAndLanLon(List<PlayNode> NodeList) {
        // boolean hasLanLon;
        for (int i = 0; i < NodeList.size(); i++) {
            // hasLanLon = false;// 默认没有经纬度
            if (NodeList.get(i).getName().contains("组织"))
                System.out.println("找到");
            if (NodeList.get(i).IsDirectory()) {
                int total = 0;
                for (int k = 0; k < NodeList.size(); k++) {
                    if (NodeList.get(k).node.dwParentNodeId .equals(NodeList.get(i).node.dwNodeId))// 表示是其子节点
                    {
                        total++;
                    }
                }
                NodeList.get(i).setChildrenCount(total);
            }
        }

    }

    /**
     * 得到所有摄像头点(子节点)
     *
     * @return 所有摄像头点(子节点)
     */
    public static List<PlayNode> GetAllChildren(List<PlayNode> NodeList) {
        List<PlayNode> list = new ArrayList<PlayNode>();
        for (int i = 0; i < NodeList.size(); i++) {
            if (!NodeList.get(i).IsDirectory() && !NodeList.get(i).IsDvr()) {
                list.add(NodeList.get(i));
                // Log.i("nodeName", "NodeList.get(i):"
                // + NodeList.get(i).getName());
            }
        }
        // AllChildrenList = list;
        return list;
    }

    /**
     * 获取每一个节点的路径route,例如"测试-风采楼"
     */
    public static void GetChildrenRoute(List<PlayNode> NodeList) {
        for (int k = 0; k < NodeList.size(); k++) {
            PlayNode node = NodeList.get(k);
            PlayNode parent = GetParent(node.node.dwNodeId, NodeList);
            if (parent == null) {
                node.setRoute(node.node.sNodeName);
            } else {
                node.setRoute(parent.getRoute() + ROUTE_SEPERATOR
                        + node.node.sNodeName);
            }
        }
    }

    /**
     * 得到每一个节点的层数
     */
    public static void GetLevel(List<PlayNode> NodeList) {
        for (int k = 0; k < NodeList.size(); k++) {
            PlayNode node = NodeList.get(k);
            String[] temp = node.getRoute().split(ROUTE_SEPERATOR);
            node.setLevel(temp.length - 1);
        }
        // System.out.println("OK");
    }

    /**
     * 得到这个列表中所有可播放的节点（子节点)
     *
     * @param nodeList
     *            目标列表
     * @return 可播放的节点(子节点)
     */
    public static List<PlayNode> getPlayList(List<PlayNode> nodeList) {
        List<PlayNode> playList = new ArrayList<PlayNode>();
        for (int i = 0; i < nodeList.size(); i++) {
            PlayNode item = nodeList.get(i);
            if (!item.IsDirectory()) {
                playList.add(item);
            }
        }
        return playList;
    }

    /**
     * 无权限提示
     *
     * @param con
     * @param message
     */
    public static void showRightDialog(Context con, int message) {
        new AlertDialog.Builder(con).setMessage(con.getString(message))
                .setPositiveButton(R.string.positive, null).show();
    }

    /**
     * 查找deviceId节点在playList中的位置
     *
     * @param playList
     *            列表
     * @param deviceId
     *            被查找节点的ID
     * @return 查找到则返回节点位置，没有查找到返回-1
     */
    public static int getIndex(List<PlayNode> playList, String deviceId) {
        for (int i = 0; i < playList.size(); i++) {
            if (playList.get(i).node.sDevId.equals(deviceId))
                return i;
        }
        return -1;
    }

    /**
     * 查找deviceId节点在playList中的位置
     *
     * @param playList
     *            列表
     * @param deviceId
     *            被查找节点的ID
     * @return 查找到则返回节点位置，没有查找到返回-1
     */
    public static PlayNode getPlayNode(List<PlayNode> playList, String deviceId) {
        for (int i = 0; i < playList.size(); i++) {
            if (playList.get(i).node.dwNodeId.equals(deviceId))
                return playList.get(i);
        }
        return null;
    }

    /**
     * 修改列表名字
     *
     * @param playList
     * @param deviceId
     * @param newName
     * @return
     */
    public static boolean modifiNodeName(List<PlayNode> playList,
                                         String deviceId, String newName) {
        for (int i = 0; i < playList.size(); i++) {
            if (playList.get(i).node.sDevId.equals(deviceId)) {
                playList.get(i).node.sNodeName = newName;
                return true;
            }

        }
        return false;
    }

    public static PlayNode getNodeByID(List<PlayNode> playList, String deviceId) {
        for (int i = 0; i < playList.size(); i++) {
            if (playList.get(i).getDeviceId().equals(deviceId)) {

                return playList.get(i);
            }

        }
        return null;

    }
    /**
     * 获取警报参数
     *
     * @param deviceId
     * @return
     */
    public static TAlarmSetInfor GetAlarm(PlayerClient playerclient,
                                          String deviceId) {
        return null;

    }

    /**
     * 布防，设置报警参数
     *
     * @param playerclient
     * @param deviceId
     *            设备Id
     * @param alarm
     *            报警参数 1为设备故障，2为移动报警，3为视频遮挡，4为视屏丢失，5为探头报警，例如"1,2,3,4,5"为开启所有报警
     *            ,"1,3,5"为开启设备故障，视频遮挡，探头报警
     * @return
     */
    public static boolean SetAlarmEx(PlayerClient playerclient,
                                     String deviceId, String alarm) {
        return false;

    }

    /**
     * 布防
     *
//     * @param type必须大于0
     * @param deviceId
     * @return
     */
    public static boolean SetAlarmParameters(PlayerClient playerclient,
                                             String deviceId, List<Integer> type) {
        return false;

    }

    /**
     * 布防添加手机号码
     *
//     * @param type必须大于0
     * @param deviceId
     * @return
     */
    public static boolean SetAlarmNote(PlayerClient playerclient,
                                       String deviceId, String number) {
        return false;

    }

    /**
     * 布防添加消息推送添加手机序列号
     *
     * @param playerclient
     * @param deviceId
     * @param number
     *            手机序列号
     * @return
     */
    public static boolean SetAlarmPush(PlayerClient playerclient,
                                       String deviceId, String number) {
        return false;

    }

    /**
     * 布防停止消息推送手机序列号
     *
     * @param deviceId
     *            设备ID
     * @return 大于0 返回成功 其他失败
     */
    public static boolean CancelAlarmPush(PlayerClient playerclient,
                                          String deviceId, String number) {
        return false;

    }

    /**
     * 布防删除手机号码
     *
//     * @param type必须大于0
     * @param deviceId
     * @return
     */
    public static boolean CancelAlarmNote(PlayerClient playerclient,
                                          String deviceId, String number) {
        return false;

    }

    /**
     * 设置警报
     *
//     * @param type必须大于0
     * @param deviceId
     * @return
     */
    public static boolean SetAlarm(PlayerClient playerclient, String deviceId,
                                   List<Integer> type) {
        return false;

    }

    /**
     * 获取可支持的设备列表
     */
    public static List<DeviceInfo> GetDeviceList(PlayerClient playerclient) {
        return null;

    }

    public static List<DeviceInfo> getVendor(PlayerClient playerclient) {
        return null;

    }

    // new
    public static MessageInfo getAlarmMsg(Context con, PlayerClient playerclient) {
        return null;

    }

    /**
     * 获取推送报警消息
     *
     * @return
     */
    public static MessageInfo getAlarmMessage(Context con,
                                              PlayerClient playerclient) {
        return null;

    }

    /***
     * 获取所有的报警信息
     *
     * @param deviceId
     * @return
     */
    public static List<MessageInfo> getAllAlarmList(Context con,
                                                    PlayerClient playerclient, String deviceId) {
        return null;

    }

    /***
     * 获取所有的系统信息
     *
//     * @param deviceId
     * @return
     */
    public static List<MessageInfo> getSystemMsgList(PlayerClient playerclient,
                                                     String customID) {
        return null;

    }

    /**
     * 查询某时间段，某类型 报警记录
     *
     * @param id
     * @param type
     * @param start
     * @param end
     * @return
     */
    public static List<MessageInfo> getAlarmList(Context con,
                                                 PlayerClient playerclient, String id, int type, Date_Time start,
                                                 Date_Time end) {
        return null;

    }

    /**
     * 删除报警记录
     *
     * @param alarmId
     * @return
     */
    public static boolean deleteAlarm(PlayerClient playerclient, String alarmId) {
        return false;

    }

    public static boolean CancelAlarm(PlayerClient playerclient, String deviceId) {
        return false;

    }



    /**
     * 修改服务器设备列表的名字
     *
     * @param node
     * @return
     */
    public static boolean ModifyDeviceName(PlayerClient playerclient,
                                           TFileListNode node, String newName) {
        return false;

    }

    /**
     * 修改搜索设备的名字
     *
     * @param node
     * @return
     */
    public static boolean ModifySearchDeviceName(PlayerClient playerclient,
                                                 SearchDeviceInfo node, String newName) {
        return false;

    }

    /**
     * 修改搜索设备的ip地址
     *
     * @param node
     * @return
     */
    public static boolean ModifySearchDeviceIP(PlayerClient playerclient,
                                               SearchDeviceInfo node, String newIP) {
        return false;

    }

    public static boolean ModifySearchDeviceIP(PlayerClient playerclient,
                                               SearchDeviceInfo node, String newIP, String newMask,
                                               String newGetWay, boolean isDhcp) {
        return isDhcp;

    }

    /**
     * 使用DHCP自动获取
     */
    public static boolean EnableDHCP(PlayerClient playerclient,
                                     SearchDeviceInfo node, boolean isEnable) {
        return isEnable;

    }

    /**
     * 修改搜索设备的密码
     */
    public static boolean ModifySearchDevicePass(PlayerClient playerclient,
                                                 SearchDeviceInfo node, String oldPass, String newPass) {
        return false;

    }

    /**
     * 设置警报
     *
     * @param deviceId
     * @return
     */
    public static boolean SetAlarm(PlayerClient playerclient, String deviceId,
                                   String number) {
        return false;

    }

    /**
     * 添加直连
     *
     * @param isDricte
     * @param addNodeParent
     * @param nodeName
     * @param devId
     * @param channels
     * @param username
     * @param password
     * @param stream
     * @return
     */

    public static int AddUmeyeDirct(PlayerClient playerclient,
                                    boolean isCamera, TFileListNode addNodeParent, String nodeName,
                                    String address, int port, int vendor, int channels,
                                    String username, String password, int stream) {
        return stream;

    }

    /**
     * 添加云
     *
     * @param addNodeParent
     * @param nodeName
     * @param devId
     * @param channels
     * @param username
     * @param password
     * @param stream
     * @return
     */
    public static int AddUmeyeCloud(PlayerClient playerclient,
                                    boolean isCamera, TFileListNode addNodeParent, String nodeName,
                                    String devId, int channels, String username, String password,
                                    int stream) {
        return stream;

    }

    /**
     * 修改密码
     *
     * @param UserID
     *            用户名字
     * @param OldPassword
     *            旧密码
     * @param NewPassword
     *            新密码
     * @return 请求成功返回true,否则返回 false
     */
    public static boolean ModifyUserPwd(PlayerClient playerclient,
                                        String UserID, String OldPassword, String NewPassword) {
        return false;

    }

    /**
     * 添加目录
     *
     * @param addDirParent
     *            添加目录的父节点名字
     * @param addDir
     *            要目录的节点名字
     * @return 请求成功返回true,否则返回 false
     */
    public static boolean AddDirectory(PlayerClient playerclient,
                                       TFileListNode addDirParent, String addDir) {
        return false;

    }

}
