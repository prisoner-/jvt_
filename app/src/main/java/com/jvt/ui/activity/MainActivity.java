package com.jvt.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.Player.Core.UserImg.UserImgEntity.UserImgCompareInfo;
import com.Player.Core.Utils.CommenUtil;
import com.jvt.MyApplication;
import com.jvt.R;
import com.jvt.bean.PlayNode;
import com.jvt.framwork.StateChangeListener;
import com.jvt.presenter.PlayerPresenter;
import com.jvt.ui.adapter.RecyclerUsersAdapter;
import com.jvt.ui.widget.RoundImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.jvt.ui.adapter.RecyclerUsersAdapter.TYPE_LIST;

/**
 * Created by liaolei on 2018/12/18.
 */
public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity LL Test";

    public static final int REQUEST_CODE = 1011;

    public static final int STATE = 0;
    public static final byte READY = 0; // 准备就绪
    public static final byte CONNECTTING = 1;// 连接中
    public static final byte PLAYING = 2;// 播放中
    public static final byte STOP = 4;// 停止
    public static final byte CONNECTTING_FAIL = 3;// 连接失败
    public static final byte RECONNECT = 12;// 重新连接

    public static final int NPC_D_UMSP_CUSTOM_FUNCID_CMP_DATA = 65541;//对比数据
    public static final int NPC_D_UMSP_CUSTOM_FUNCID_CAP_JPG = 65542;//上传实时抓拍图片

    public static final int USERS_SIZE = 60;//比对列表长度

    @BindView(R.id.button)
    Button button;
    @BindView(R.id.frameLayout)
    RelativeLayout preview;
    /**
     * 列表展示，一屏三个
     */
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    /**
     * 单个展示，一屏一个
     */
//    @BindView(R.id.img_face)
//    ImageView face;
    @BindView(R.id.img_lib_face)
    RoundImageView libFace;
    @BindView(R.id.tv_name)
    TextView name;
    //    @BindView(R.id.tv_similarity)
//    TextView similarity;
    @BindView(R.id.tv_type)
    TextView type;
    //    @BindView(R.id.tv_sex)
//    TextView sex;
    @BindView(R.id.tv_count)
    TextView count;
    @BindView(R.id.tv_time)
    TextView time;
    @BindView(R.id.tv_remarks)
    TextView remarks;

    /**
     * 0为单个展示   1为列表展示   默认为单个展示
     */
    @BindView(R.id.btn_style)
    Button btnStyle;
    int layoutStyle;
    @BindView(R.id.layout_single)
    LinearLayout layout_single;
    String[] styles = new String[]{"single", "list"};

    RecyclerUsersAdapter adapter;
    LinearLayoutManager manager;

    PlayNode playNode;
    boolean isLand = false;

    PlayerPresenter presenter;
    public MyApplication application;

    List<UserImgCompareInfo> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        presenter = new PlayerPresenter(mContext);
        application = (MyApplication) getApplication();

        configration(getResources().getConfiguration());

//        initRecyclerView();
        initPlayer();

    }

    private void switchStyle() {
        layoutStyle = layoutStyle == 0 ? 1 : 0;
        btnStyle.setText(styles[layoutStyle]);
        recyclerView.setVisibility(layoutStyle == 1 ? View.VISIBLE : View.GONE);
        layout_single.setVisibility(layoutStyle == 0 ? View.VISIBLE : View.GONE);
    }

    private long lastClickTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {

            case KeyEvent.KEYCODE_ENTER:     //确定键enter
            case KeyEvent.KEYCODE_DPAD_CENTER:
                Log.d(TAG, "enter--->");

                break;

            case KeyEvent.KEYCODE_BACK:    //返回键
                Log.d(TAG, "back--->");
                long l = System.currentTimeMillis();
                if (l - lastClickTime < 1300) {
                    finish();
                } else {
                    showToast("再点击一次退出");
                }
                finish();
                break;
//                return true;   //这里由于break会退出，所以我们自己要处理掉 不返回上一层

            case KeyEvent.KEYCODE_SETTINGS: //设置键
                Log.d(TAG, "setting--->");

                break;

            case KeyEvent.KEYCODE_DPAD_DOWN:   //向下键

                /*    实际开发中有时候会触发两次，所以要判断一下按下时触发 ，松开按键时不触发
                 *    exp:KeyEvent.ACTION_UP
                 */
                if (event.getAction() == KeyEvent.ACTION_DOWN) {

                    Log.d(TAG, "down--->");
                }

                break;

            case KeyEvent.KEYCODE_DPAD_UP:   //向上键
                Log.d(TAG, "up--->");

                break;

            case KeyEvent.KEYCODE_DPAD_LEFT: //向左键
                Log.d(TAG, "left--->");
                //隐藏按钮
                btnAnimation(false);
                button.setVisibility(View.GONE);
                btnStyle.setVisibility(View.GONE);

                break;

            case KeyEvent.KEYCODE_DPAD_RIGHT:  //向右键
                Log.d(TAG, "right--->");
                //显示按钮
                btnAnimation(true);
                button.setVisibility(View.VISIBLE);
                btnStyle.setVisibility(View.VISIBLE);

                break;
            case KeyEvent.KEYCODE_VOLUME_UP:   //调大声音键
                Log.d(TAG, "voice up--->");

                break;

            case KeyEvent.KEYCODE_VOLUME_DOWN: //降低声音键
                Log.d(TAG, "voice down--->");

                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 设备管理和展示样式按钮动画
     *
     * @param isShow 是否显示
     */
    private void btnAnimation(boolean isShow) {
        Animation animation = AnimationUtils.loadAnimation(mContext, isShow ? R.anim.left_in : R.anim.left_out);
        button.setAnimation(animation);
        btnStyle.setAnimation(animation);
        animation.start();
    }


    private void initRecyclerView() {
        manager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(manager);
        manager.setOrientation(OrientationHelper.HORIZONTAL);

        adapter = new RecyclerUsersAdapter(mContext, users);
        recyclerView.setAdapter(adapter);

        //添加item分割线
//        RecyclerItemDecoration decoration = new RecyclerItemDecoration();
//        recyclerView.addItemDecoration(decoration);

        //设置新增或删除条目动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }


    /**
     * 更新比对列表，当列表长度超出时，只留最后5条，前面的全部清除，继续添加更新
     *
     * @param user
     */
    public void updateUser(UserImgCompareInfo user) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recyclerView.setVisibility(layoutStyle == 1 ? View.VISIBLE : View.GONE);
                layout_single.setVisibility(layoutStyle == 0 ? View.VISIBLE : View.GONE);
                showToast("比对成功 ：" + user.i_sLibName);
                if (layoutStyle == 0) {
                    libFace.setImageBitmap(CommenUtil.getBitmapFromByte(user.i_libImg));//注册图
                    type.setText(mContext.getResources().getText(TYPE_LIST[user.i_iBWMode - 1]));
                    name.setText(user.i_sLibName);
                    time.setText(user.i_sCapTime);
                    count.setText(mContext.getResources().getString(R.string.snap_count) + user.i_iCount + "");
                    remarks.setText("进垃圾堆了关键是浪费了的几十个了减肥是个垃圾房贷款了房价高了设计费路公交上了的方式");
                } else {
                    if (manager == null || adapter == null) initRecyclerView();
                    if (users.size() >= USERS_SIZE) {
                        users.clear();
                    }
                    users.add(user);
                    adapter.notifyItemInserted(users.size() - 1);
                    manager.scrollToPosition(users.size() - 1);
                }
            }
        });

    }

    public void addCanvas() {
        preview.removeAllViews();
//        setLand();
//        preview.addView(presenter.getCanvas(), playWidth, playHeight);

        WindowManager wm = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);

        int screenwidth = wm.getDefaultDisplay().getWidth() - 5;
        int height = wm.getDefaultDisplay().getHeight();
        preview.addView(presenter.getCanvas(), screenwidth, height);

    }

    int playHeight, playWidth;

    public void setLand() {
//        this.isLand = isLand1;
        playHeight = 0;
        playWidth = 0;
        ViewTreeObserver vto = preview.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {

//                int hight = preview.getMeasuredHeight();
//                int width = preview.getMeasuredWidth();

//                if (width != playWidth && playHight != hight) {

                WindowManager wm = (WindowManager) mContext
                        .getSystemService(Context.WINDOW_SERVICE);

                int screenwidth = wm.getDefaultDisplay().getWidth() - 5;
                int height = wm.getDefaultDisplay().getHeight();
                playWidth = screenwidth;
                Log.w("LL Test setLand", "isLand 播放:" + isLand
//                            + ",getMeasuredHeight=" + hight
//                            + ",getMeasuredWidth=" + width + ",screenwidth="
                        + screenwidth + ",height=" + height);
                if (isLand) {
                    playHeight = height;
                } else {
                    playHeight = (int) (playWidth * 9 / 10);
                }
                Log.d("LL Test", " playH：" + playHeight + " playW：" + playWidth);
//                }
                return true;
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        configration(newConfig);
//        addCanvas();
        fullScreenChange(isLand);
    }

    private void configration(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //横屏
            isLand = true;
        } else {
            //竖屏
            isLand = false;
        }
    }

    @OnClick({R.id.button, R.id.btn_style})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                Intent intent = new Intent(mContext, DevManagerActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.btn_style:
                switchStyle();
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data == null) return;

        if (requestCode == REQUEST_CODE) {
            playNode = (PlayNode) data.getSerializableExtra("node");
            presenter.prepare(playNode);
            presenter.reconnect();
        }
    }

    int playState;
    boolean isPlaying;

    StateChangeListener stateChangeListener = new StateChangeListener() {
        @Override
        public void stateChange(int state, String framRate, String devName) {
            if (playState != state) {
                showToast("play state " + state);
                playState = state;
            }
        }

        @Override
        public void isPlaying(boolean isplaying) {
            if (isPlaying != isplaying) {
                showToast("isPlaying " + isPlaying);
                isPlaying = isplaying;
            }
        }

        @Override
        public void isRecord(boolean isRecord) {

        }

        @Override
        public void isAudio(boolean isAudio) {

        }

        @Override
        public void isTalk(boolean isTalk) {

        }

        @Override
        public void showControlBtn(boolean isShow) {

        }

        @Override
        public void isMainStream(int stream) {

        }
    };

    private void initPlayer() {

        if (application.getAllChildNodeList() != null && application.getAllChildNodeList().size() != 0) {
            playNode = application.getAllChildNodeList().get(0);
            presenter.initPlayer(playNode);
            presenter.setStateChangeListener(stateChangeListener);
            presenter.prepare(playNode);
            presenter.addCanvas();
            play();
        }
    }

    private void play() {
        presenter.play();
    }

    /**
     * 全屏隐藏通知栏
     *
     * @param fullScreen
     */
    public void fullScreenChange(boolean fullScreen) {
        Window window = getWindow();
        WindowManager.LayoutParams attrs = window.getAttributes();

        if (!fullScreen) {
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.setAttributes(attrs);
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        } else {
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            window.setAttributes(attrs);
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }
}
