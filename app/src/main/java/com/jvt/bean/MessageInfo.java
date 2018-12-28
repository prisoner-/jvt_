package com.jvt.bean;

import android.content.Context;

import com.Player.Core.PlayerClient;
import com.Player.Source.TAlarmPushInfor;
import com.Player.Source.TSystemMsg;
import com.Player.web.response.AlarmInfo;
import com.jvt.R;

import java.io.Serializable;

public class MessageInfo implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -4826959073341616431L;
    String alarmId;
    String name;
    String deviceId;
    String message;
    int type;
    String time;// 0为设备报警，1为系统消息
    int state;
    String title;
    boolean isNew = true;
    public String AlarmSmallImg;
    public String bigImg;
    public static final int BLACK_LIST_ALARM = 22;
    public static final int WHITE_LIST_ALARM = 23;
    public static final int NO_WHITE_BLACK_LIST_ALARM = 24;
    public static final int VIP_LIST_ALARM = 28;
    public static final int STRANGER_LIST_ALARM = 29;

    public MessageInfo() {

    }

    public MessageInfo(Context con, TAlarmPushInfor tpi) {
        this.type = tpi.iAlarmType;
        this.state = tpi.iAlarmState;
        this.name = tpi.sDevName;
        this.deviceId = tpi.sDevId;
        this.message = toMessage(con, type);
        this.type = tpi.iAlarmType;
        this.time = tpi.tAlarmTime;
        this.alarmId = tpi.sAlarmRecordId;

    }

    public MessageInfo(Context con, AlarmInfo alarmInfo) {
        this.type = alarmInfo.alarm_event;
        this.state = alarmInfo.alarm_state;
        this.name = alarmInfo.dev_name;
        this.deviceId = alarmInfo.dev_id;
        this.message = toMessage(con, type);
        this.time = alarmInfo.alarm_time;
        this.alarmId = alarmInfo.alarm_id;
        this.AlarmSmallImg = alarmInfo.alarm_small_img;
        this.bigImg = alarmInfo.alarm_big_img;
    }

    public String toMessage(Context con, int type) {
        if (type == PlayerClient.NPC_D_MON_ALARM_TYPE_VIDEO_BLIND) {
            return con.getString(R.string.alarminfo_video_cover);
        } else if (type == PlayerClient.NPC_D_MON_ALARM_TYPE_DEV_FAULT) {
            return con.getString(R.string.alarminfo_equipment);
        } else if (type == PlayerClient.NPC_D_MON_ALARM_TYPE_VIDEO_LOSS) {
            return con.getString(R.string.alarminfo_video_lose);
        } else if (type == PlayerClient.NPC_D_MON_ALARM_TYPE_PROBE) {
            return con.getString(R.string.alarminfo_probe);
        } else if (type == BLACK_LIST_ALARM) {
            return con.getString(R.string.black_list_alarm);
        } else if (type == WHITE_LIST_ALARM) {
            return con.getString(R.string.white_list_alarm);
        } else if (type == NO_WHITE_BLACK_LIST_ALARM) {
            return con.getString(R.string.no_white_black_list_alarm);
        } else if (type == VIP_LIST_ALARM) {
            return con.getString(R.string.vip_list_alarm);
        } else if (type == STRANGER_LIST_ALARM) {
            return con.getString(R.string.stranger_list_alarm);
        } else {
            return con.getString(R.string.alarminfo_move);
        }
    }

    public MessageInfo(TSystemMsg tpi) {
        this.type = 1;
        this.message = tpi.sMsgContent;
        this.alarmId = tpi.sMsgId;
        this.time = tpi.sMsgTime;
        this.title = tpi.sMsgTitle;

    }

    public static MessageInfo createMessageInfo(String message) {
        MessageInfo info = new MessageInfo();
        info.state = 1;
        info.name = "device1";
        info.deviceId = "1023";
        info.message = message;
        info.type = 2;
        info.time = "2014-04-05 12:30";
        info.alarmId = "123";
        return info;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(String alarmId) {
        this.alarmId = alarmId;
    }

    public String getId() {
        return deviceId;
    }

    public void setId(String id) {
        this.deviceId = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
