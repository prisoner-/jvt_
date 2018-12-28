package com.jvt.presenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Player.Core.PlayerClient;
import com.Player.Core.PlayerCore;
import com.Player.Core.UserImg.UserImgEntity.UserImgCompareInfo;
import com.Player.Core.UserImg.UserImgEntity.UserImgSnapImgInfo;
import com.Player.Source.OnP2PDataListener;
import com.Player.Source.SDKError;
import com.Player.Source.TDateTime;
import com.Player.web.websocket.MD5Util;
import com.jvt.MyApplication;
import com.jvt.R;
import com.jvt.bean.CanvasInfo;
import com.jvt.bean.PlayNode;
import com.jvt.bean.VideoListResult;
import com.jvt.framwork.SnapCallBack;
import com.jvt.framwork.StopLisenter;
import com.jvt.ui.activity.MainActivity;
import com.jvt.utils.CommonData;
import com.jvt.utils.Config;
import com.jvt.utils.Imagedeal;
import com.jvt.utils.LocalFile;
import com.jvt.utils.Utility;

import java.io.File;
import java.util.Date;

/**
 * Created by liaolei on 2018/12/20.
 * 初始化，控制播放器，播放视频以及处理比对返回结果
 */
public class VideoCanvas {
    public static final int MAX_RECONNECT_COUNT = 10;// 最多连接次数
    protected static final int STATE = 0;
    protected static final int RECONENT = 1;
    protected static final int PlayBack_RECONENT = 3;
    protected static final int RESET = 4;
    protected static final int SNAP = 2;
    private static final String SEPERATER = "-";
    public boolean isShow = false;
    private RelativeLayout rlBackground;// 背景
    public ImageView imgVideo;// 视频显示
    public Button btnAdd;// 添加按钮显示

    public PlayerCore player;// 播放器

    private Context context;
    public String Name;// 设备名
    public String DevId;// 设备umID
    public int MediaStreamType;// 播放的媒体类型
    public int StreamParserType;// 流解析类型
    public boolean hasPrepare = false;// 是否初始化

    private int ReconnectCount = 0;
    public int tag;
    private boolean isFullScreen = false;// 是否全屏
    private boolean isAutoPlay = false;// 是否自动播放
    public boolean IsOpenSound = false;
    public TextView tvisRecordingStatus;
    boolean isStoping = false;// 正在停止中
    boolean isScale = false;
    long datacount = 0;

    public ImageButton[] imgBtn = new ImageButton[8];
    private String imageDir, videoDir;
    private boolean IsAudio;
    CanvasInfo ci = new CanvasInfo();
    ProgressBar progressBar;
    private TextView tvState;
    public int stream = 1;
    public Imagedeal deal;
    boolean isShot = false;
    Handler refreshListhandler;// 刷新list缩略图
    public TDateTime startTime;
    public TDateTime endTime;
    private boolean isPlayBack;
    public VideoListResult vediodData;
    public PlayNode playNode;
    public PlayerClient pc;
    public static final int NPC_D_UMSP_CUSTOM_FUNCID_CMP_DATA = 65541;//对比数据
    public static final int NPC_D_UMSP_CUSTOM_FUNCID_CAP_JPG = 65542;//上传实时抓拍图片

    public Handler getRefreshListhandler() {
        return refreshListhandler;
    }

    public static int screenScale = 0; // 0: 为充满整屏 1: 为按宽高比例
    public static int fluent = 0; // 0 实时，1为流畅

    public void setRefreshListhandler(Handler refreshListhandler) {
        this.refreshListhandler = refreshListhandler;
    }

    public VideoCanvas(Context context){
        this.context=context;
    };

    public void init(int w, int h,boolean isScreenScale) {
        this.isScale = isScreenScale;
        rlBackground = new RelativeLayout(context);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(w, h);

        rlBackground.setLayoutParams(lp);
        // rlBackground.setBackgroundResource(R.drawable.video_gray_bg);
        rlBackground.setPadding(2, 2, 2, 2);
        // 状态显示
        tvState = new TextView(context);
        tvState.setId(R.id.camrea_img);
        tvState.setBackgroundResource(R.color.front_state_n);
        tvState.setTextSize(10);
        tvState.setText(context.getResources().getString(R.string.ready));
        tvState.setSingleLine();
        tvState.setGravity(Gravity.CENTER);
        RelativeLayout.LayoutParams tvStatepa = new RelativeLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        tvStatepa.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rlBackground.addView(tvState, tvStatepa);
        // 画面显示
        RelativeLayout parentLayout = new RelativeLayout(context);
        imgVideo = new ImageView(context);
        if (getSDKVersionIsTabalets()) {
            imgVideo.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        imgVideo.setScaleType(isScreenScale ? ImageView.ScaleType.FIT_CENTER : ImageView.ScaleType.FIT_XY);
        RelativeLayout.LayoutParams imgVideolp = new RelativeLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT);
        imgVideolp.addRule(RelativeLayout.ABOVE, R.id.camrea_img);
        imgVideo.setLayoutParams(new RelativeLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.FILL_PARENT));
        btnAdd = new Button(context);
        btnAdd.setBackgroundResource(R.drawable.view_add_btn_dis);
        RelativeLayout.LayoutParams btnAddLp = new RelativeLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        btnAddLp.addRule(RelativeLayout.CENTER_IN_PARENT);

        tvisRecordingStatus = new TextView(context);
        tvisRecordingStatus.setTextColor(ColorStateList.valueOf(0xffff0000));
        RelativeLayout.LayoutParams pRecording = new RelativeLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        pRecording.addRule(RelativeLayout.ALIGN_TOP);
        pRecording.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        tvisRecordingStatus.setPadding(0, 2, 7, 0);
        tvisRecordingStatus.setText("●REC");
        tvisRecordingStatus.setTextSize(15);
        tvisRecordingStatus.setLayoutParams(pRecording);
        setRecordingState(false);

        progressBar = new ProgressBar(context);
        progressBar.setIndeterminateDrawable(context.getResources().getDrawable(
                R.drawable.progress_large));
        RelativeLayout.LayoutParams pgBar = new RelativeLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        pgBar.addRule(RelativeLayout.CENTER_IN_PARENT);

        parentLayout.addView(imgVideo);
        parentLayout.addView(tvisRecordingStatus);
        parentLayout.addView(progressBar, pgBar);
        parentLayout.addView(btnAdd, btnAddLp);
        rlBackground.addView(parentLayout, imgVideolp);
        setProgressVisible(false);
        player = new PlayerCore(context);
        pc = new PlayerClient();
        player.isQueryDevInfo = true;
        player.SetOpenLog(true);
    }

    public void setScreenScale(boolean isScreenScale) {

        imgVideo.setScaleType(isScreenScale ? ImageView.ScaleType.FIT_CENTER
                : ImageView.ScaleType.FIT_XY);
        Log.i("isScreenScale", "isScreenScale : " + isScreenScale);
    }

    public void setScaleType(ImageView.ScaleType scaleType) {
        imgVideo.setScaleType(scaleType);
        Log.i("isScreenScale", "isScreenScale : " + scaleType);
    }

    /**
     * 使用播放前的初始化
     */
    public void prepare(String name, String DevId, String route, boolean isRealTime, PlayNode playNode) {
        Log.d("playcoreinit", "playcoreinit"); // 运行
        this.Name = name;
        player.SetPPtMode(true);
        player.SetOpenLog(true);
        player.CloseAudio();
        IsAudio = false;

        imageDir = getCurrentImageDir(route);
        videoDir = getCurrentVideoDir(route);
        player.SetAlbumPath(imageDir);
        player.SetVideoPath(videoDir);
        this.DevId = DevId;
        // this.Channel = channel;
        player.InitParam(DevId, -1, imgVideo);
        SetbCleanLastView(false);
        player.setPreSnap(Config.ThumbDir
                + MD5Util.stringToMD5(player.DeviceNo) + ".jpg");
        hasPrepare = true;
        ReconnectCount = 0;
        this.playNode = playNode;
        // isIp = false;
    }

    public SnapCallBack snapCallBack = null;

    public void prepare(String name, String DevId, String route, boolean isRealTime, final PlayNode playNode, final SnapCallBack snapCallBack) {
        this.Name = name;
        this.snapCallBack = snapCallBack;
        player.SetPPtMode(true);
        player.SetOpenLog(true);
        player.CloseAudio();
        player.setP2pPortDataCallback(new OnP2PDataListener() {//设置回调数据
            @Override
            public void callBackData(int camrea, int function, int pData, int size) {
                Log.i("img_function", "camrea->" + camrea + " function->" + function + " size is->" + size + "state->" + player.getCamreaHandler());
                switch (function) {
                    case NPC_D_UMSP_CUSTOM_FUNCID_CMP_DATA://转对比数据结构体，图片对比
                        UserImgCompareInfo userImgCompareInfo = pc.JLImageCompareInfo(pData, size);
                        if (userImgCompareInfo != null && snapCallBack != null) {
                            snapCallBack.SnapCallBack(camrea, userImgCompareInfo, playNode.umid);
                        }
                        break;
                    case NPC_D_UMSP_CUSTOM_FUNCID_CAP_JPG://转对比图片结构体，图片抓拍
                        UserImgSnapImgInfo userImgSnapImgInfo = pc.JLImageSnapInfo(pData, size);
                        if (userImgSnapImgInfo != null && snapCallBack != null) {
                        }
                        break;
                }
            }
        });
        IsAudio = false;
        imageDir = getCurrentImageDir(route);
        videoDir = getCurrentVideoDir(route);
        player.SetAlbumPath(imageDir);
        player.SetVideoPath(videoDir);
        this.DevId = DevId;
        player.InitParam(DevId, -1, new ImageView(context));
//        player.InitParam(DevId, -1, imgVideo);
        SetbCleanLastView(false);
        player.setPreSnap(Config.ThumbDir + MD5Util.stringToMD5(player.DeviceNo) + ".jpg");
        hasPrepare = true;
        ReconnectCount = 0;
        this.playNode = playNode;
    }

    int setStream() {

        if (stream == 1) {
            stream = 0;
        } else
            stream = 1;
        player.InitParam(DevId, stream, imgVideo);
        SetbCleanLastView(false);
        play();
        return stream;
    }

    public void setProgressVisible(boolean visible) {
        if (visible) {
            if (progressBar.getVisibility() != View.VISIBLE) {
                progressBar.setVisibility(View.VISIBLE);
            }
        } else {
            if (progressBar.getVisibility() != View.GONE) {
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    public void setBtnAddVisible(boolean visible) {
        if (visible) {
            btnAdd.setVisibility(View.VISIBLE);
        } else {
            if (btnAdd.getVisibility() != View.GONE) {
                btnAdd.setVisibility(View.GONE);
            }
        }
    }

    public void setState(String state) {
        tvState.setText(state);
    }

    public String getCurrentImageDir(String sub) {
        String s = Config.UserImageDir + sub.replace(CommonData.ROUTE_SEPERATOR, "/") + "/";
        System.out.println("图片地址：" + s);
        return s;
    }

    public String getCurrentVideoDir(String sub) {
        String s = Config.UserVideoDir + sub.replace(CommonData.ROUTE_SEPERATOR, "/") + "/";
        System.out.println("录像地址：" + s);
        return s;
    }

    public boolean setIsAudio(boolean open) {
        IsAudio = open;
        if (isPrepared()) {
            if (open) {
                player.OpenAudio();
            } else {
                player.CloseAudio();
            }
            return player.GetIsVoicePause();
        }
        return true;
    }

    public boolean IsAudio() {
        if (isPrepared()) {
            return !player.GetIsVoicePause();
        }
        return false;
    }

    public boolean IsPPt() {
        if (isPrepared()) {
            return player.GetIsPPT();
        }
        return false;
    }

    public void setPPT(boolean isOpen) {
        if (isPrepared()) {
            if (isOpen) {
                player.StartPPTAudio();
            } else {

                player.StopPPTAudio();
            }
        }
    }

    public boolean isPlayed() {
        return isPrepared() ? (player.PlayCoreGetCameraPlayerState() == MainActivity.PLAYING) : false;
    }

    public boolean isBuffing() {
        return isPrepared() ? (player.PlayCoreGetCameraPlayerState() == 10) : false;
    }

    public String getPlayFrameRate() {
        if (isPrepared() && isPlayed()) {
            return player.GetPlayFrameRate() + "fps" + " "
                    + player.GetFrameBitRate() + "kbps" + " "
                    + player.GetConnectedType();
        } else {
            return "";
        }
    }

    public int[] getPlayRate() {
        int[] rate = new int[2];
        if (isPrepared() && isPlayed()) {
            rate[0] = player.GetPlayFrameRate();
            rate[1] = player.GetFrameBitRate();
            return rate;
        } else {
            return null;
        }
    }

    public boolean getVideoRecordState() {
        if (isPrepared()) {
            boolean isRecord = player.GetIsSnapVideo();
            setRecordingState(isRecord);
            return isRecord;
        } else {
            return false;
        }
    }

    public int getState() {
        //setStartTime();
        if (isPrepared()) {
            int state = player.PlayCoreGetCameraPlayerState();
            if (state == MainActivity.STOP) {
                if (player.GetIsSnapVideo()) {//停止播放的时候，停止录像
//                    FgLocalFile.isLoadData = true;
                    player.SetSnapVideo(false);
                    setRecordingState(false);
                }
            }
            if (!isShot) {
                if (state == MainActivity.PLAYING) {
                    isShot = true;
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {

                            // TODO Auto-generated method stub
                            int rate[] = getPlayRate();
                            if (rate == null) {
                                isShot = false;
                                return;
                            }
                            if (rate[0] != 0 || rate[1] != 0) {
                                try {
                                    setSnap(Config.ThumbDir);
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            boolean issnaping = GetIsSnapPicture();
                                            Log.e("snapingThumb",
                                                    "This issnaping thumb is "
                                                            + issnaping);
                                            if (issnaping == true)// 还在截图就不刷新
                                            // 等下次再截图
                                            {
                                                isShot = false;
                                            }
                                            // else
                                            // new GetThumb().execute();
                                        }
                                    }, 100);

                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            } else
                                isShot = false;
                        }
                    }, 500);
                }
            }
            return state;
        } else {
            return SDKError.Statue_Ready;
        }

    }

    /**
     * 是否初始化播放地址
     */
    public boolean isPrepared() {
        return player != null && !TextUtils.isEmpty(player.DeviceNo);

    }

    public void release() {
        player.DeviceNo = null;

    }

    /**
     * 播放视频
     * 通道号
     */
    public void play() {
        if (isPrepared()) {
            setProgressVisible(true);
            btnAdd.setVisibility(View.GONE);
            player.SetPlayModel(fluent);
            player.Play();
            MyApplication.currentPlayDeviceId = playNode.umid;
            setScaleType(screenScale == 0 ? ImageView.ScaleType.FIT_XY : ImageView.ScaleType.FIT_CENTER);
            isShot = false;

        }
    }

    public void initePlayBackTime(TDateTime startDateTime,
                                  TDateTime endTDateTime) {
        this.isPlayBack = true;
        TDateTime startTmp = new TDateTime();
        startTmp.iDay = startDateTime.iDay;
        startTmp.iYear = startDateTime.iYear;
        startTmp.iHour = startDateTime.iHour;
        startTmp.iMonth = startDateTime.iMonth;
        startTmp.iMinute = startDateTime.iMinute;
        startTmp.iSecond = startDateTime.iSecond;

        TDateTime endTmp = new TDateTime();
        endTmp.iDay = endTDateTime.iDay;
        endTmp.iYear = endTDateTime.iYear;
        endTmp.iHour = endTDateTime.iHour;
        endTmp.iMonth = endTDateTime.iMonth;
        endTmp.iMinute = endTDateTime.iMinute;
        endTmp.iSecond = endTDateTime.iSecond;
        this.startTime = startTmp;
        this.endTime = endTmp;

    }

    /**
     * 远程回放
     */
    public void PlayBack() {
        if (isPrepared()) {
            if (isStoping) {
                return;
            }
            setProgressVisible(true);
            btnAdd.setVisibility(View.GONE);
            Log.d("PlayTimeFile", "PlayBack 不带参数---------->" + startTime.toString() + "\n" + endTime.toString());
            player.PlayTimeFile(startTime, endTime, 0);
            isShot = false;
        }
    }

    /**
     * 远程回放
     */
//    public void Resume() {
//        if (isPrepared() && getState() == MainActivity.PLAYING) {
//            player.Resume();
//        }
//    }
    public void Pause() {
        if (isPrepared() && isPlayed()) {
            player.Pause();
        }
    }

    /**
     * 预览重新连接
     */

    public void Reconnect() {
        new Thread() {
            @Override
            public void run() {
                if (isStoping) {
                    return;
                }
                isStoping = true;
                VideoCanvas.this.stop();
                isStoping = false;
                handler.sendEmptyMessage(RECONENT);
            }
        }.start();
    }

    public void ReconnectClear() {
        new Thread() {
            @Override
            public void run() {
                if (isStoping) {
                    return;
                }
                isStoping = true;
                SetbCleanLastView(true);
                VideoCanvas.this.stop();
                isStoping = false;
                handler.sendEmptyMessage(RECONENT);
            }
        }.start();
    }

    /**
     * 回放重新连接
     */

    public void PlaybackReconnect() {
        new Thread() {
            @Override
            public void run() {
                if (isStoping) {
                    return;
                }
                isStoping = true;
                VideoCanvas.this.stop();
                isStoping = false;
                handler.sendEmptyMessage(PlayBack_RECONENT);

            }
        }.start();
    }

    void setStartTime() {
        if (isPlayBack) {
            if (isPrepared() && startTime != null) {
                long s = player.GetCurrentTime_Int();
                if (s != 0) {
                    Date date = new Date(s * 1000);
                    Log.d("GetCurrentTime", "GetCurrentTime_Int" + s);
                    startTime.iHour = date.getHours();
                    startTime.iMinute = date.getMinutes();
                    startTime.iSecond = date.getSeconds();
                    Log.d("GetCurrentTime", "startTime.iHour :" + startTime.iHour);
                }
            }
        }
    }

    public synchronized void stop() {
        setStartTime();
        MyApplication.currentPlayDeviceId = "";
        datacount = player.getDataCount() + datacount; // getDataCount
        player.Stop();
    }

    /**
     * 停止播放
     */
    public void Stop(final StopLisenter handler) {
        setRecordingState(false);
        new Thread() {

            @Override
            public void run() {
                if (isStoping) {

                    return;
                }
                isStoping = true;
                if (player.GetIsSnapVideo()) {
//                    FgLocalFile.isLoadData = true;
                    player.SetSnapVideo(false);

                }
                if (player.GetIsPPT()) {
                    player.StopPPTAudio();
                }
                if (!player.GetIsVoicePause()) {
                    player.CloseAudio();
                }
                VideoCanvas.this.stop();
                isStoping = false;
                handler.complete(VideoCanvas.this);

            }
        }.start();

    }

    /**
     * 停止播放
     */
    public void Stop() throws Exception {
        setRecordingState(false);

        // imgVideo.setScaleType(ScaleType.FIT_CENTER);
        new Thread() {

            @Override
            public void run() {
                if (isStoping) {
                    return;
                }
                isStoping = true;
                if (player.GetIsSnapVideo()) {
//                    FgLocalFile.isLoadData = true;
                    player.SetSnapVideo(false);

                }
                if (player.GetIsPPT()) {
                    player.StopPPTAudio();
                }
                if (!player.GetIsVoicePause()) {
                    player.CloseAudio();
                }

                VideoCanvas.this.stop();
                isStoping = false;

                handler.sendEmptyMessage(STATE);

            }
        }.start();

    }

    /**
     *
     */
    public void stopAndClearData() {
        Name = "";
        if (vediodData != null) {
            vediodData.multiData = null;
        }

        if (player.GetIsSnapVideo()) {
//            FgLocalFile.isLoadData = true;
            player.SetSnapVideo(false);
            setRecordingState(false);
        }
        if (player.GetIsPPT()) {
            player.StopPPTAudio();
        }
        if (!player.GetIsVoicePause()) {
            player.CloseAudio();
        }
        // imgVideo.setScaleType(ScaleType.FIT_CENTER);
        new Thread() {

            @Override
            public void run() {
                isStoping = true;
                VideoCanvas.this.stop();
                isStoping = false;
                handler.sendEmptyMessage(RESET);

            }
        }.start();

    }

    public RelativeLayout getView() {
        return rlBackground;
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            if (msg.what == STATE) {

                if (tempbCleanLastView) {
                    imgVideo.setImageBitmap(null);
                }
            } else if (msg.what == RECONENT) {
                play();
            } else if (msg.what == RESET) {
                imgVideo.setImageBitmap(null);
                SetbCleanLastView(tempbCleanLastView);
                player.DeviceNo = null;

            } else if (msg.what == PlayBack_RECONENT) {

                SetbCleanLastView(tempbCleanLastView);
                PlayBack();
            }

        }
    };

    /**
     * 设置位置
     *
     * @param left
     * @param top
     */
    public void setPosition(int left, int top) {
        RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) rlBackground
                .getLayoutParams();
        p.leftMargin = left;
        p.topMargin = top;
        p.setMargins(left, top, -2 * p.width, -2 * p.height);
        // System.out.println("改变画面位置--->("+left+","+top+","+p.rightMargin+","+p.bottomMargin+")");
        rlBackground.setLayoutParams(p);
    }

    public int getLeft() {
        RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) rlBackground
                .getLayoutParams();
        return p.leftMargin;
    }

    public int getTop() {
        RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) rlBackground
                .getLayoutParams();
        return p.topMargin;
    }

    public int getWidth() {
        RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) rlBackground
                .getLayoutParams();
        return p.width;
    }

    public int getHeight() {
        RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) rlBackground
                .getLayoutParams();
        return p.height;
    }

    /**
     * 重新设置尺寸
     *
     * @param w
     * @param h
     */
    public void setSize(int w, int h) {
        deal = Imagedeal.getdeal(imgVideo);
        RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) rlBackground
                .getLayoutParams();
        p.width = w;
        p.height = h;
        p.rightMargin = -2 * w;
        p.bottomMargin = -2 * h;
        rlBackground.setLayoutParams(p);
    }

    /**
     * 设置可视性
     *
     * @param visible
     */
    public void setVisibility(int visible) {
        rlBackground.setVisibility(visible);
        imgVideo.setVisibility(visible);
    }

    /**
     * 设置高亮
     *
     * @param isHightLight
     */
    public void setHightLight(boolean isHightLight) {
        if (isHightLight) {
            // rlBackground.setBackgroundResource(R.drawable.color_white);
            rlBackground.setBackgroundResource(R.drawable.video_blue_bg);
            tvState.setBackgroundResource(R.color.front_state_h);
        } else {
            rlBackground.setBackgroundResource(R.drawable.video_gray_bg);
            tvState.setBackgroundResource(R.color.front_state_n);

        }
        // if (isIsOpenSound()) {
        // setIsAudio(isHightLight);
        // }

    }

    // public void setText(String text, String devName) {
    // tvState.setText(text);
    // tvDevName.setText(devName);
    // }
    //
    // public void setText(int text, int devName) {
    // tvState.setText(text);
    // tvDevName.setText(devName);
    // }
    //
    // public void setTextVisible(int visible) {
    // tvState.setVisibility(visible);
    // tvDevName.setVisibility(visible);
    // }

    public void setImgBtnVisible(int visible) {
        for (int i = 0; i < 8; i++) {
            imgBtn[i].setVisibility(visible);
        }
    }

    /**
     * 查看视频是否可用（是否被隐藏)
     *
     * @return 可用返回True，否则为false
     */
    public boolean isEnable() {
        return rlBackground.getVisibility() == View.VISIBLE;
    }

    public void setFullScreen(boolean isFullScreen) {
        this.isFullScreen = isFullScreen;
        if (isFullScreen)
            rlBackground.setPadding(0, 0, 0, 0);
        else
            rlBackground.setPadding(1, 1, 1, 1);
    }

    /**
     * 返回是否全屏
     *
     * @return
     */
    public boolean IsFullScreen() {
        return isFullScreen;
    }

    /**
     * 设置是否自动播放
     *
     * @param auto
     */
    public void setAutoPlay(boolean auto) {
        isAutoPlay = auto;
    }

    /**
     * 是否自动播放
     *
     * @return
     */
    public boolean IsAutoPlay() {
        return isAutoPlay;
    }

    public void setRecordingState(boolean isShow) {
        if (isShow) {
            tvisRecordingStatus.setVisibility(View.VISIBLE);
        } else {
            tvisRecordingStatus.setVisibility(View.GONE);
        }
    }

    /**
     * 流量数据统计
     *
     * @return
     */
    public long getDatacount() {
        if (datacount == 0) {
            return player.getDataCount();
        }
        return datacount;
    }

    public void setDatacount(long datacount) {
        this.datacount = datacount;
    }

    public int firstLogin() {
        try {
            // player.Login();
            int i = 0;
            while (true) {

                Thread.sleep(1000);

                i++;
                // int state = player.GetPlayerState();
                // int state1 = player.GetLoginState();
                // Log.i("state", "state:" + state + "state1:" + state1);
                // if (state == SDKError.NET_ConnectingSucess) {
                // break;
                // } else if (state == SDKError.Statue_ConnectFail) {
                // break;
                // } else if (state == SDKError.Statue_PLAYING
                // || state1 == SDKError.Statue_LOGINSUCESS) {
                // break;
                // } else if (state == SDKError.NET_LOGIN_ERROR_USER) {
                // break;
                // } else if (state == SDKError.NET_Protocal_ERROR) {
                // break;
                // } else if (state == SDKError.NET_LOGIN_ERROR_PASSWORD) {
                // break;
                // } else if (i >= 40) {
                // // player.StopandRelease();
                // return -1;
                // }
            }
            // if (player.GetPlayerState() == SDKError.NET_ConnectingSucess
            // || player.GetPlayerState() == SDKError.Statue_PLAYING
            // || player.GetLoginState() == SDKError.Statue_LOGINSUCESS) {
            // int channel = player.GetMaxChannel();
            // // player.StopandRelease();
            // return channel;
            // }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            // player.StopandRelease();
            e.printStackTrace();
        }
        // player.StopandRelease();
        return -1;

    }

    /**
     * 全部停止
     *
     */

    /**
     * 截图
     */
    public void setSnap() throws Exception {
        if (!Utility.isSDCardAvaible()) {
            Toast.makeText(context, R.string.sdcard_unavaible,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (!LocalFile.CreateDirectory(imageDir)) {
            Toast.makeText(context, R.string.snap_fail, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
//        FgLocalFile.isLoadData = true;
        player.SetAlbumPath(imageDir);
        player.SetSnapPicture(true);
        Toast.makeText(context, getString(R.string.image_save_in) + imageDir,
                Toast.LENGTH_SHORT).show();
        // FgLocalPhoto.isSnap = true;
    }

    public boolean GetIsSnapPicture() {
        return player.GetIsSnapPicture();
    }

    /**
     * 截图
     */
    public void setSnap(String path) throws Exception {
        if (!Utility.isSDCardAvaible()) {
            return;
        }
        if (!LocalFile.CreateDirectory(path)) {
            return;
        }
//        FgLocalFile.isLoadData = true;
        player.SetAlbumPath(path, MD5Util.stringToMD5(player.DeviceNo) + ".jpg");
        player.SetSnapPicture(true);

        // deleteSameThumb(path, player.DeviceNo);
        // player.SetSnapPicture(true, player.DeviceNo + SEPERATER);

    }

    /**
     * 删除已经存在的缩略图
     *
     * @param path
     * @param deviceId
     */
    void deleteSameThumb(String path, String deviceId) {
        File dir = new File(path);
        File[] listFile = dir.listFiles();
        for (int i = 0; i < listFile.length; i++) {
            File file = listFile[i];
            Log.w("listFile", "listFile:" + file.getName());
            if (file.getName().contains(SEPERATER)) {
                if (deviceId.equals(file.getName().split(SEPERATER)[0])) {
                    file.delete();
                    return;
                }
            }
        }

    }

    /**
     * 录像
     */
    public boolean setVideo() throws Exception {

        if (!Utility.isSDCardAvaible()) {
            Toast.makeText(context, R.string.sdcard_unavaible, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!LocalFile.CreateDirectory(videoDir)) {
            Toast.makeText(context, R.string.snap_fail, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (player.GetIsSnapVideo()) {
//            FgLocalFile.isLoadData = true;
            player.SetSnapVideo(false);
            setRecordingState(false);
            Toast.makeText(context,
                    getString(R.string.video_save_in) + videoDir,
                    Toast.LENGTH_SHORT).show();
        } else {
            player.SetSnapVideo(true);
            setRecordingState(true);
        }
        // FgLocalVideo.isRecord = true;
        return true;
    }

    /**
     * 获取字符串
     *
     * @param id
     * @return
     */
    String getString(int id) {

        return context.getString(id);

    }

    public void setClickListenser(View.OnClickListener l) {
        rlBackground.setOnClickListener(l);
    }

    public void setOnTouchListener(View.OnTouchListener l) {
        rlBackground.setOnTouchListener(l);
    }

    boolean isSetStream = false;

    public int CameraSwitchChannel(int tstream) {
        // TODO Auto-generated method stub
        int ret = -1;
        if (isPlayed()) {
            stream = getStreamType();

            if (stream == tstream) {
                Log.w("CameraSwitchChannel", "重复调用----");
                return 0;
            } else
                stream = tstream;
            Log.d("CameraSwitchChannel", "CameraSwitchChannel:" + stream);
            ret = player.CameraSwitchChannel(stream);
        }
        return ret;
    }

    public void setMediaStreamType(final int stream) {
        if (isPlayed()) {
            if (!isSetStream) {
                new Thread() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        isSetStream = true;
                        CameraSwitchChannel(stream);
                        isSetStream = false;
                        super.run();
                    }
                }.start();
            }

        } else {

            player.setMediaStreamType(stream);
            play();
        }

    }

    public int getStreamType() {
        int ret = stream;
        if (player != null) {
            if (player.tDevNodeInfor != null) {
                return player.tDevNodeInfor.streamtype;
            }

        }
        return ret;
    }

    public boolean tempbCleanLastView = true;

    public void SetbCleanLastView(boolean tempbCleanLastView) {
        this.tempbCleanLastView = tempbCleanLastView;
        player.SetbCleanLastView(tempbCleanLastView);

        // deleteSameThumb(path, player.DeviceNo);
        // player.SetSnapPicture(true, player.DeviceNo + SEPERATER);

    }

    public boolean getSDKVersionIsTabalets() {
        int sdkVersion;
        try {
            sdkVersion = android.os.Build.VERSION.SDK_INT;
            if (11 <= sdkVersion && sdkVersion <= 13) {
                return true;
            }
        } catch (NumberFormatException e) {
            sdkVersion = 0;
            return false;
        }
        return false;
    }
}
