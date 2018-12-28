//package com.jvt.presenter;
//
//import android.content.Context;
//import android.os.Message;
//import android.util.Log;
//import android.widget.ImageView;
//
//import com.Player.Core.PlayerCore;
//import com.Player.Source.SDKError;
//import com.Player.Source.StopRecodeVideoListener;
//import com.Player.web.response.DevItemInfo;
//import com.HEyes.constants.Common;
//import com.HEyes.model.net.NodeType;
//import com.HEyes.mvp.contract.ModuleContract;
//import com.HEyes.thread.PlayerThread;
//import com.HEyes.util.FileUtil;
//import com.HEyes.util.LogUtil;
//
//import java.io.File;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
///**
// * Created by Ouyb on 2018/8/22.
// */
//
//
////playerCore.ALBUM_PATH/VIDEO_PATH
//public class VideoPlayModule {
//
//    private PlayerCore playerCore;
//    //    private boolean isRunning = false;
////    private boolean isPlaying;
//    private PlayerThread stateThread;
//
//    private static final String TAG = VideoPlayModule.class.getSimpleName();
//
//    private ModuleContract.StateChangeStateChangeCallBack listener;
//
//    private DevItemInfo info;
//
//    public class CMD {
//        public static final int ERR_PLAYING_PPT = 0;
//        public static final int ERR_PLAYING_RECORD = 1;
//        public static final int ERR_PLAYING_CAMERA = 2;
//        public static final int ERR_PLAYING_UNLOCK = 3;
//        public static final int ERR_PLAYING_ALARM = 4;
//        public static final int IDLE = 5;
//        public static final int ERR_SET_ALARM = 6;
//        public static final int ERR_NO_SDCARD = 7;
//        public static final int STATE_SNAP_VIDEO = 8;
//        public static final int RESULT_SNAP_VIDEO = 9;
//        public static final int RESULT_SNAP_PHOTO = 10;
//        public static final int STATE_ALARM = 11;
//        public static final int STATE_SPEAKER = 12;
//    }
//
//
//    private boolean shouldStart;
//
//    private boolean hasSnapPicture;
//    private boolean hasSnapVideo;
//
//    private ExecutorService service = Executors.newSingleThreadExecutor();
//
//    private String videoPath, imagePath;
//
//    private int isAlarmState = 2;
//
//
//    public VideoPlayModule(Context context, DevItemInfo info, ModuleContract.StateChangeStateChangeCallBack listener) {
//        this.info = info;
//        this.listener = listener;
//        this.playerCore = new PlayerCore(context);
//        ImageView iv = listener.getVideoView();
//        playerCore.InitParam(info.conn_params, 0, iv);
//        // 关闭播放日志输出
//        playerCore.SetOpenLog(false);
//        playerCore.setStopRecodeVideoListener(new StopRecodeVideoListener() {
//
//            @Override
//            public void finish(boolean isSuccess, String path) {
//                // TODO Auto-generated method stub
//                Log.d(TAG, "isSuccess:" + isSuccess
//                        + ",path=" + path);
//            }
//        });
////        AlarmUtils.getDeviceAlarm(this, info.dev_id, AlarmUtils.GETUI_CID, listener);
//    }
//
//
//    public int convertAlarmState() {
//        return Math.abs(3 - this.isAlarmState);
//    }
//
//
//    public void setAlarmState(boolean isAlarm) {
//        this.isAlarmState = ((this.isAlarmState == 1) != isAlarm ? convertAlarmState() : this.isAlarmState);
//    }
//
//
//    //停止播放
//    public void stopPlay() {
//        if (stateThread != null) {
//            stateThread.interrupt();
//            stateThread = null;
//        }
//        setPPTAudio(false);
//        service.execute(new Runnable() {
//            @Override
//            public void run() {
//                playerCore.Stop();
//            }
//        });
////        shouldStart = true;
//    }
//
//
//    //播放
//    public void startPlay() {
//        if (stateThread == null || !stateThread.isAlive()) {
//            stateThread = new PlayerThread(1000, true, new PlayerThread.onResultListener() {
//
//                @Override
//                public void onResult(Message msg) {
//                    listener.onStateChange(playerCore.GetPlayFrameRate(), playerCore.PlayCoreGetCameraPlayerState());
//                    listener.showRec(playerCore.GetIsSnapVideo());
//                }
//            });
//            stateThread.start();
//        }
//        if (shouldStart) {
//            return;
//        }
//        playerCore.Play();
//    }
//
//
//    //用户点击播放或暂停（根据状态）
//    public void userClickPlay() {
//        if (playerCore.GetPlayerState() == SDKError.Statue_PLAYING) {
//            setPPTAudio(false);
//            shouldStart = true;
//            service.execute(new Runnable() {
//                @Override
//                public void run() {
//                    playerCore.Stop();
//                }
//            });
//        } else {
//            playerCore.Play();
//            shouldStart = false;
//        }
//        listener.onPlayStatus(!shouldStart);
//    }
//
//
//    private String getFilterPath(String arg0, String arg1, String arg2) {
//        return arg0.concat(arg1 + "_" + arg2 + File.separator);
//    }
//
//
//    private boolean hasSDCard() {
//        if (!FileUtil.hasSDCard()) {
//            listener.onOptResult(CMD.ERR_NO_SDCARD);
//            return false;
//        }
//        return true;
//    }
//
//
//    //拍照
//    public void camera() {
//        if (!isVideoPlay(CMD.ERR_PLAYING_CAMERA)) {
//            return;
//        }
//        setSnapPicture(true);
//    }
//
//
//    //录像
//    public void record() {
//        if (playerCore.GetIsSnapVideo()) {
//            setSnapVideo(false);
//            if (playerCore.isWriteMp4Data) {
//                playerCore.isWriteMp4Data = false;
//                listener.onOptResult(CMD.RESULT_SNAP_VIDEO, true, videoPath);
//                hasSnapVideo = true;
//            } else {
//                listener.onOptResult(CMD.RESULT_SNAP_VIDEO, false);
//            }
//        } else {
//            if (!isVideoPlay(CMD.ERR_PLAYING_RECORD)) {
//                return;
//            }
//            setSnapVideo(true);
//        }
//    }
//
//
//    //录像
//    private void setSnapVideo(boolean snap) {
//        if(snap) {
//            if (!hasSDCard()) {
//                return;
//            }
//            this.videoPath = getFilterPath(Common.getUserVideoDir(), info.node_name, info.umid);
//            playerCore.SetVideoPath(videoPath);
//            playerCore.SetSnapVideo(true);
//            listener.onOptResult(CMD.STATE_SNAP_VIDEO, 1);
//        }else {
//            playerCore.SetSnapVideo(false);
//            listener.onOptResult(CMD.STATE_SNAP_VIDEO, 0);
//        }
//    }
//
//
//    //拍照
//    private boolean setSnapPicture(boolean snap) {
//        if(snap) {
//            if (!hasSDCard()) {
//                return false;
//            }
//            this.imagePath = getFilterPath(Common.getUserImageDir(), info.node_name, info.umid);
//            LogUtil.e("imagePath: "+imagePath);
//            playerCore.SetAlbumPath(imagePath);
//            playerCore.SetSnapPicture(true);
//            listener.onOptResult(CMD.RESULT_SNAP_PHOTO, imagePath);
//            hasSnapPicture = true;
//        }else {
//            playerCore.SetSnapPicture(false);
//        }
//        return true;
//    }
//
//
//
//
//    //报警
//    public void alarm() {
//        if (!isVideoPlay(CMD.ERR_PLAYING_ALARM)) {
//            return;
//        }
//
//        if (info.node_type == NodeType.DVR || info.node_type == NodeType.DIRECTORY || info.conn_mode != 2) {
//            //不是摄像机
//            listener.onOptResult(CMD.ERR_SET_ALARM);
//            return;
//        }
//
////        AlarmUtils.setDeviceAlarm(this, info, AlarmUtils.GETUI_CID, new int[]{1, 2, 3, 4, 5},listener);
////        AlarmUtils.setDeviceAlarm(this, info, AlarmUtils.GETUI_CID, new int[]{7},listener);
//
//        listener.onOptResult(CMD.STATE_ALARM);
//    }
//
//
//
//
//    //退出activity调用
//    public void idle() {
//        setSnapPicture(false);
//        setSnapVideo(false);
//        setPPTAudio(false);
//        if (stateThread != null) {
//            stateThread.interrupt();
//            stateThread = null;
//        }
//        listener.onOptResult(CMD.IDLE, hasSnapPicture, hasSnapVideo);
//        if(service != null) {
//            service.execute(new Runnable() {
//                @Override
//                public void run() {
//                    playerCore.Stop();//放在ui会同步锁阻塞
//                }
//            });
//            service.shutdown();
//            service = null;
//        }
//    }
//
//
//    private boolean isVideoPlay(int cmd) {
//        boolean isPlaying = playerCore.GetPlayerState() == SDKError.Statue_PLAYING;
//        if (!isPlaying) {
//            listener.onOptResult(cmd);
//        }
//        return isPlaying;
//    }
//
//
//    //对讲
//    public void speaker() {
//        if (!isVideoPlay(CMD.ERR_PLAYING_PPT)) {
//            return;
//        }
//        if (playerCore.GetIsPPT()) {
//            setPPTAudio(false);
//        } else {
//            setPPTAudio(true);
//        }
//    }
//
//
//
//    private void setPPTAudio(boolean ppt) {
//        if(ppt) {
//            playerCore.SetPPtMode(true);//开启双向语音
//            playerCore.StartPPTAudio();
//            listener.onOptResult(CMD.STATE_SPEAKER, 1);
//        }else {
//            playerCore.StopPPTAudio();
//            listener.onOptResult(CMD.STATE_SPEAKER, 0);
//        }
//    }
//
//
//
//
//
//
//
//    public void unLock() {
//        if (!isVideoPlay(CMD.ERR_PLAYING_UNLOCK)) {
//            return;
//        }
//        playerCore.SendOpenLockCmd("");//开锁密码,8位
////        if (!playerCore.GetIsVoicePause()) {
////            playerCore.CloseAudio();
////        } else {
////            playerCore.OpenAudio();
////        }
//    }
//
//}
