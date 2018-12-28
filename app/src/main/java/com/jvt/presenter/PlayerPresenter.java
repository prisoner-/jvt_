package com.jvt.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.Player.Core.UserImg.UserImgEntity.UserImgCompareInfo;
import com.Player.Source.SDKError;
import com.jvt.R;
import com.jvt.bean.PlayNode;
import com.jvt.framwork.SnapCallBack;
import com.jvt.framwork.StateChangeListener;
import com.jvt.ui.activity.MainActivity;
import com.jvt.utils.ApplicationUtils;

/**
 * Created by liaolei on 2018/12/20.
 */
public class PlayerPresenter {

    public static final int STATE = 0;
    public static final byte READY = 0; // 准备就绪
    public static final byte CONNECTTING = 1;// 连接中
    public static final byte PLAYING = 2;// 播放中
    public static final byte STOP = 4;// 停止
    public static final byte CONNECTTING_FAIL = 3;// 连接失败
    public static final byte RECONNECT = 12;// 重新连接
    private static final int NPC_D_MPI_MON_ERROR_USERID_ERROR = -101; // 用户ID或用户名错误
    private static final int NPC_D_MPI_MON_ERROR_USERPWD_ERROR = -102; // 用户密码错误
    private static final int NPC_D_MPI_MON_ERROR_REJECT_ACCESS = -111; // 权限不够
    public static final int ADDTOPLAY_MULTI = 1;
    public static final int ADDTOPLAY_SINGLE = 2;

    boolean isRun = false;

    Context mContext;
    MainActivity activity;

    SDKError s;

    VideoCanvas canvas;
    int playState;
    long reconnectTime;
    PlayNode playNode;

//    PlayerCore player;//播放器
//    PlayerClient playerClient;//播放器相关处理

    StateChangeListener stateChangeListener;

    public PlayerPresenter(Context context) {
        this.mContext = context;
        activity = (MainActivity) mContext;

//        player = new PlayerCore(mContext);
//        playerClient = new PlayerClient();
    }

    SnapCallBack snapCallBack = new SnapCallBack() {
        @Override
        public void SnapCallBack(int pCamrea, UserImgCompareInfo userImgCompareInfo, String umid) {
            if (userImgCompareInfo != null &&canvas.isPlayed()) {//只有当前播放设备可显示回调。
                activity.application.compareInfoLimitQueue.offer(userImgCompareInfo, pCamrea);
                Message msg = new Message();
                msg.what = 0x11;
                msg.obj = userImgCompareInfo;
//                handler.sendMessage(msg);
                activity.updateUser(userImgCompareInfo);
            }
        }
    };

    /**
     * 初始化播放器
     *
     * @param node
     */
    public void initPlayer(PlayNode node) {
        playNode = node;
        canvas = new VideoCanvas(mContext);
        canvas.init(0, 0, true);

    }

    public void prepare(PlayNode node) {
        if (canvas == null) initPlayer(node);
        canvas.prepare(playNode.getName(), playNode.getDeviceId(), playNode.getRoute(), true, playNode, snapCallBack);
    }

    public void addCanvas() {
        activity.addCanvas();
    }

    public void reconnect() {
        canvas.ReconnectClear();
    }

    public View getCanvas() {
        return canvas.getView();
    }

    public void stop() {
        if (canvas != null && canvas.isPrepared()) {
            canvas.stop();
        }
    }

    public void release() {
        if (canvas != null && canvas.isPrepared()) {
            canvas.release();
        }
    }

    public void play() {
        canvas.SetbCleanLastView(false);
        playState = CONNECTTING;
        reconnectTime = 0;
        canvas.play();
        startGetState();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {

                case STATE:
                    if (msg.arg2 != CONNECTTING) {
                        canvas.setProgressVisible(false);
                        if (msg.arg2 == STOP || msg.arg2 == READY) {
                            canvas.setBtnAddVisible(true);
                        } else
                            canvas.setBtnAddVisible(false);
                    }
                    canvas.setState(showState(msg.arg2) + " " + (msg.arg2 == PLAYING ? canvas.getPlayFrameRate() : ""));
                    sendSateMessage(msg.arg2);
                    break;
                default:
                    break;
            }
        }
    };


    /**
     * play之后调用，开始监听状态
     */
    public void startGetState() {
        if (isRun) {
            return;
        } else {
            isRun = true;
        }
        new Thread() {

            @Override
            public void run() {
                try {
                    synchronized (canvas) {
                        while (isRun) {
                            if (ApplicationUtils.netOK) {
                                if (canvas == null) {
                                    continue;
                                }
                                Thread.sleep(500);
                                Log.d("play state:", canvas.getState() + "");
                                int state = canvas.getState();
                                reconnet(state);
                                Message message = new Message();
                                message.what = STATE;
                                message.arg2 = state;
                                handler.sendMessage(message);
                            }
                        }
                    }

                } catch (
                        InterruptedException e)

                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }.start();
    }

    private void reconnet(int state) {
        if (playState == STOP) {
            return;
        }
        if (state == NPC_D_MPI_MON_ERROR_USERID_ERROR

                || state == NPC_D_MPI_MON_ERROR_USERPWD_ERROR) { // 如果用户名密码错误，无权限，不需要重连
            playState = state;
            return;
        }
        switch (playState) {
            case READY:

                break;

            case RECONNECT:// 重新连接
            case CONNECTTING_FAIL:// 连接失败
            {
                Log.e("playState", "重连设备44444.");
                handler.post(new PlayRunnble());
                break;
            }
        }
        long current = System.currentTimeMillis();
        if ((state < 0 || state == 3) && reconnectTime == 0) {
            reconnectTime = current;
        }
        if (current - reconnectTime > 6000 && reconnectTime != 0) {
            reconnectTime = 0;
            handler.post(new PlayRunnble());
            Log.e("playState", "连接失败，重连设备1111111111.");
            playState = CONNECTTING;
        }

    }

    class PlayRunnble implements Runnable {

        public PlayRunnble() {
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            Reconnect();
        }

        public synchronized void Reconnect() {

            if (canvas.isPrepared()) {
                playState = CONNECTTING;
                reconnectTime = 0;
                canvas.Reconnect();
            }

        }
    }

    String showState(int state) {
        String ret = "";
        if (state == 1) {// 连接中
            ret = mContext.getString(R.string.connecting);
        } else if (state == 2) {// 播放中
            // ret = con.getString(R.string.playing);
        } else if (state == 3) {// 连接失败
            ret = mContext.getString(R.string.connect_fail);
        } else if (state == 4) {// 停止中
            ret = mContext.getString(R.string.stop);
        } else if (state == -102) {// 密码错误
            ret = mContext.getString(R.string.passworderro);
        } else if (state == -101) {// 用户ID或用户名错误
            ret = mContext.getString(R.string.usererro);
        } else if (state == -111) {//
            ret = mContext.getString(R.string.NPC_D_MPI_MON_ERROR_REJECT_ACCESS);
        } else if (state == 0) {// 准备就绪
            ret = mContext.getString(R.string.ready);
        } else if (state == 10) {
            ret = mContext.getString(R.string.buffering);
        } else if (state == 7) {//
            ret = mContext.getString(R.string.stop);
        } else {//
            ret = mContext.getString(R.string.connect_fail);
        }
        return ret;
    }

    public void setStateChangeListener(StateChangeListener stateChangeListener) {
        this.stateChangeListener = stateChangeListener;
    }

    public void sendSateMessage(int state) {
        Log.i("handleMessage", canvas.Name + "状态是：" + state);
        stateChangeListener.stateChange(state, state == PLAYING ? canvas.getPlayFrameRate() : "", canvas.Name);
        stateChangeListener.isTalk(canvas.IsPPt());
        stateChangeListener.isPlaying(state == PLAYING ? true : false);
        stateChangeListener.isRecord(canvas.getVideoRecordState());
        stateChangeListener.isAudio(canvas.IsAudio());
        stateChangeListener.isMainStream(canvas.stream);
    }
}
