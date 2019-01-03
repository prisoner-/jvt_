package com.jvt.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.Player.Core.PlayerClient;
import com.Player.Source.TDevNodeInfor;
import com.Player.Source.TFileListNode;
import com.Player.web.response.ResponseCommon;
import com.Player.web.websocket.ClientCore;
import com.jvt.MyApplication;
import com.jvt.R;
import com.jvt.bean.PlayNode;
import com.jvt.bean.SearchDeviceInfo;
import com.jvt.ui.dialog.ShowProgress;
import com.jvt.utils.AddThread;
import com.jvt.utils.CommonData;
import com.jvt.wifi.WiFiAdmin;
import com.macrovideo.smartlink.SmarkLinkTool;

import java.util.List;

public class AddDevActivity extends BaseActivity implements android.widget.RadioGroup.OnCheckedChangeListener, OnClickListener, OnItemSelectedListener {
    private final int EDIT_FAIL = 3;
    private final int EDIT_OK = 2;
    private int MADE_ID = 1009;
    private EditText etalias, etumid, etuser, etpassword, etport, etaddress;

    private TextView tvstream, tvVendor;
    private ShowProgress progressDialog;
    MyApplication application;
    String currentParent = "";
    int streamType = 0;////默认添加主码流
    View lay_p2p, lay_direct;
    RadioGroup rgAdd;
    int deviceType = 0;
    public TDevNodeInfor info;
    int addchannels = 0;
    private ShowProgress pdLoading;
    private CheckBox CkdvrORnvr;
    private RelativeLayout dev_channel;
    private LinearLayout lin_dvrORnvr;
    private LinearLayout rg_channel;
    private TextView ck_oneC, ck_fourC, ck_eightC, ck_sixteenC, ck_24, ck_32, ck_64, ck_128;
    private TextView singlech, multich;
    int devchannels = 1;
    public Handler hander = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AddThread.OK:
                    showToast(getString(R.string.add_success));
                    setResult(RESULT_OK);
                    finish();
                    break;
                case AddThread.ERROR:
                    showToast(getString(R.string.add_failed));
                    break;
                case EDIT_OK:
                    pdLoading.dismiss();
                    showToast(getString(R.string.modify_success));
                    setResult(RESULT_OK);
                    finish();
                    break;
                case EDIT_FAIL:
                    pdLoading.dismiss();
                    showToast(getString(R.string.modify_connect_fail));
                    break;
                default:
                    break;
            }
            progressDialog.dismiss();
        }
    };
    private TextView titleName;
    private boolean isModify;
    private PlayNode modifyNode;
    private Dialog asyncDialog;
    private Spinner etSsid;
    private EditText etWifiPass;
    private CheckBox ckShowPass;
    private Button btnAsyncSure;
    private Button btnAsyncCancel;
    private ShowProgress pd;
    private ArrayAdapter<String> adapter;
    private String devSSid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_dev);
        Intent intent = getIntent();
        currentParent = intent.getStringExtra("currentParent");
        application = (MyApplication) getApplication();
        etalias = (EditText) findViewById(R.id.et_user_alias);
        etalias.setText("Device");
        etumid = (EditText) findViewById(R.id.et_umid);
        etuser = (EditText) findViewById(R.id.et_user_name);
        etpassword = (EditText) findViewById(R.id.et_user_password);

        etport = (EditText) findViewById(R.id.et_port);
        etaddress = (EditText) findViewById(R.id.et_ip_address);

        tvstream = (TextView) findViewById(R.id.et_stream);
        tvVendor = (TextView) findViewById(R.id.et_vendor);
        rgAdd = (RadioGroup) findViewById(R.id.rg_add);
        lay_p2p = findViewById(R.id.l_add_p2P);
        lay_direct = findViewById(R.id.l_add_direct);

        rgAdd.setOnCheckedChangeListener(this);
        isModify = getIntent().getBooleanExtra("isModify", false);
        titleName = (TextView) findViewById(R.id.title_name);
        View erweima = findViewById(R.id.erweima);
        View localSearch = findViewById(R.id.btn_add_device_search);
        Button back=findViewById(R.id.back_btn);
        RadioButton p2p=findViewById(R.id.rb_p2p);
        RadioButton direct=findViewById(R.id.rb_direct);

        // d通道数
        singlech = (TextView) findViewById(R.id.et6);
        multich = (TextView) findViewById(R.id.tv6);
        CkdvrORnvr = (CheckBox) findViewById(R.id.ck_dvrORnvr);
        dev_channel = (RelativeLayout) findViewById(R.id.dev_channel);// 通道数选择面板
        lin_dvrORnvr = (LinearLayout) findViewById(R.id.lin_dvrORnvr);
        rg_channel = (LinearLayout) findViewById(R.id.rg_channel);
        ck_oneC = (TextView) findViewById(R.id.ck_oneC);
        ck_fourC = (TextView) findViewById(R.id.ck_fourC);
        ck_eightC = (TextView) findViewById(R.id.ck_eightC);
        ck_sixteenC = (TextView) findViewById(R.id.ck_sixteenC);
        ck_24 = (TextView) findViewById(R.id.ck_24);
        ck_32 = (TextView) findViewById(R.id.ck_32);
        ck_64 = (TextView) findViewById(R.id.ck_64);
        ck_128 = (TextView) findViewById(R.id.ck_128);

        /**
         * 测试用
         */
//        etumid.setText("umkse5bbg3q9");
//        etpassword.setText("admin.123");
//        streamType = 1;//子码流
//        tvstream.setText(getString(R.string.subtype));

        //返回键获取焦点，并设置下一个焦点
        back.requestFocus();
        back.setNextFocusRightId(R.id.btn_add_device_search);
        back.setNextFocusDownId(R.id.et_user_alias);

        localSearch.setNextFocusDownId(R.id.et_user_alias);
        localSearch.setNextFocusLeftId(R.id.back_btn);

        etalias.setNextFocusDownId(R.id.rb_p2p);
        etalias.setNextFocusUpId(R.id.back_btn);

        p2p.setNextFocusRightId(R.id.rb_direct);
        p2p.setNextFocusDownId(R.id.et_umid);
        p2p.setNextFocusUpId(R.id.et_user_alias);

        direct.setNextFocusLeftId(R.id.rb_p2p);
        direct.setNextFocusDownId(R.id.et_umid);
        direct.setNextFocusUpId(R.id.et_user_alias);


        ck_oneC.setOnClickListener(this);
        ck_fourC.setOnClickListener(this);
        ck_eightC.setOnClickListener(this);
        ck_sixteenC.setOnClickListener(this);
        ck_24.setOnClickListener(this);
        ck_32.setOnClickListener(this);
        ck_64.setOnClickListener(this);
        ck_128.setOnClickListener(this);
        if (!CkdvrORnvr.isChecked()) {
            dev_channel.setVisibility(View.GONE);
            singlech.setTextColor(Color.parseColor("#333333"));
            multich.setTextColor(Color.parseColor("#acaaaa"));
        }
        select(4);
        CkdvrORnvr.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundbutton, boolean flag) {
                // TODO Auto-generated method stub
                if (flag) {
                    dev_channel.setVisibility(View.VISIBLE);
                    singlech.setTextColor(Color.parseColor("#acaaaa"));
                    multich.setTextColor(Color.parseColor("#333333"));
                } else {
                    dev_channel.setVisibility(View.GONE);
                    singlech.setTextColor(Color.parseColor("#333333"));
                    multich.setTextColor(Color.parseColor("#acaaaa"));
                }
            }
        });
        if (isModify) {
            String deviceId = getIntent().getStringExtra("currentId");
            modifyNode = CommonData.getPlayNode(application.getNodeList(), deviceId);
            titleName.setText(getString(R.string.modify_device_paramete));
            info = TDevNodeInfor.changeToTDevNodeInfor(
                    modifyNode.getDeviceId(), modifyNode.node.iConnMode);
            etalias.setText(modifyNode.getName() + "");
            if (modifyNode.IsDvr()) {
                CkdvrORnvr.setEnabled(false);
                rg_channel.setEnabled(false);
                CkdvrORnvr.setChecked(true);
                ck_fourC.setEnabled(false);
                ck_eightC.setEnabled(false);
                ck_24.setEnabled(false);
                ck_32.setEnabled(false);
                ck_sixteenC.setEnabled(false);
                ck_64.setEnabled(false);
                ck_128.setEnabled(false);
                singlech.setTextColor(Color.parseColor("#acaaaa"));
                multich.setTextColor(Color.parseColor("#333333"));
                dev_channel.setVisibility(View.VISIBLE);
                select(info.iChNo);

            } else if (modifyNode.isCamera()) {
                devchannels = 1;
                CkdvrORnvr.setEnabled(false);
                CkdvrORnvr.setChecked(false);
                singlech.setTextColor(Color.parseColor("#333333"));
                multich.setTextColor(Color.parseColor("#acaaaa"));
                dev_channel.setVisibility(View.GONE);
            }
            MADE_ID = info.dwVendorId;
            streamType = info.streamtype;
            if (streamType == 1) {
                tvstream.setText(getString(R.string.subtype));
            } else {
                tvstream.setText(getString(R.string.maintype));
            }
            if (info.iConnMode == PlayerClient.NPC_D_MPI_MON_DEV_NODE_CONNECT_DER) {
                etaddress.setText(info.pAddress + "");
                etport.setText(info.devport + "");
                rgAdd.check(R.id.rb_direct);
            } else if (info.iConnMode == PlayerClient.NPC_D_MPI_MON_DEV_NODE_CONNECT_CLOUD) {
                etumid.setText(info.pDevId + "");
                etumid.setEnabled(false);
                rgAdd.check(R.id.rb_p2p);
            }
            findViewById(R.id.rb_p2p).setClickable(false);
            findViewById(R.id.rb_direct).setClickable(false);
            erweima.setVisibility(View.GONE);
            localSearch.setVisibility(View.VISIBLE);
            etuser.setText(info.pDevUser + "");
            etpassword.setText(info.pDevPwd + "");
        } else {
            Button button = (Button) findViewById(R.id.btn_add_device);
            button.setText(getString(R.string.add_device));
        }
        String deviceName = intent.getStringExtra("resultCodeDeviceName");
        String umid = intent.getStringExtra("resultCodeServer");
        addchannels = intent.getIntExtra("channels", 0);
        if (!TextUtils.isEmpty(umid)) {
            etumid.setText(umid);
            etalias.setText(deviceName);
        }
        progressDialog = new ShowProgress(this);
        findViewById(R.id.btn_add_wifi).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        showDialog();
                    }
                });
        tvstream.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(AddDevActivity.this, StreamSelectActivity.class);
                intent.putExtra("stream", streamType);
                startActivityForResult(intent, 2);
            }
        });
        findViewById(R.id.btn_add_device).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        if (isModify) {
                            if (checkValue()) {
                                pdLoading = new ShowProgress(AddDevActivity.this);
                                pdLoading.show();
                                Edit();
                            }
                        } else if (deviceType == 0) {
                            addP2P();
                        } else {
                            addAddress();
                        }

                    }
                });

        /**
         * 点击进入二维码扫描获取序列号
         */
//        erweima.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                PermissionCallback permissionCallback = new PermissionCallback() {
//                    @Override
//                    public void PermissionResult(boolean success) {
//                        if (success) {
//                            startActivityForResult(new Intent(AddDevActivity.this, CameraTestActivity.class).putExtra("isFromAdd", true), 0);
//                        } else {
//                            showToast(getString(R.string.permission_camrea));
//                        }
//                    }
//                };
//                PermissonUtils.getInstance().requestPermissions(AddDevActivity.this, permissionCallback, Manifest.permission.CAMERA);
//            }
//        });
		localSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivityForResult(new Intent(mContext, SearchActivity.class), 3);

			}
		});

        back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK) {
            streamType = data.getIntExtra("stream", 1);
            if (streamType == 1) {
                tvstream.setText(getString(R.string.subtype));
            } else {
                tvstream.setText(getString(R.string.maintype));
            }
        } else if (requestCode == 0 && resultCode == RESULT_OK) {
            String qrCode = data.getStringExtra("serianumber").replace(" ", "");
            etumid.setText("" + qrCode);
            if (!isModify) {
                etalias.setText(qrCode);
            }
        } else if (requestCode == 3 && resultCode == RESULT_OK) {
            String deviceName = data.getStringExtra("deviceName");
            String umid = data.getStringExtra("umid");
            SearchDeviceInfo searchInfo = (SearchDeviceInfo) data
                    .getSerializableExtra("node");
            etaddress.setText(searchInfo.sIpaddr_1 + "");
            etport.setText(searchInfo.iDevPort + "");
            etumid.setText(umid);
            etalias.setText(deviceName);
            etuser.setText(searchInfo.sDevUserName);
            select(searchInfo.usChNum);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 添加P2P
     */
    void addP2P() {
        List<PlayNode> nodeList = application.getAllChildNodeList();
        String deviceName = etalias.getText().toString();
        String Umid = etumid.getText().toString();
        String username = etuser.getText().toString();
        String pass = etpassword.getText().toString();
        // String channels = channelInput.getText().toString();

        int channels = 0;
        for (int i = 0; i < nodeList.size(); i++) {
            PlayNode node = nodeList.get(i);
            if (deviceName.equals(node.getName()) || Umid.equals(node.umid)) {
                showToast(getString(R.string.dev_existent));
                return;
            }
        }
        if (CkdvrORnvr.isChecked()) {
            if (devchannels == 0) {
                showToast(getString(R.string.over_maxchannel));
                return;
            }
            if (addchannels != 0) {
                if (devchannels > addchannels) {
                    showToast(getString(R.string.over_maxchannel));
                    return;
                }
            }
            channels = devchannels;
        } else
            channels = 0;

        if (TextUtils.isEmpty(deviceName)) {
            showToast(getString(R.string.input_not_empty));
        } else {
            if (TextUtils.isEmpty(Umid)) {
                showToast(getString(R.string.input_not_empty));
            } else {
                if (TextUtils.isEmpty(username)) {
                    showToast(getString(R.string.enter_user_name));
                } else {
                    progressDialog.show();
                    PlayNode temp = CommonData.GetCurrentParent(currentParent,
                            application.getNodeList());
                    TFileListNode node = null;
                    if (temp == null) {
                        node = null;
                    } else {
                        node = temp.node;
                    }
                    new AddThread(this, true, node, hander,
                            deviceName, Umid, channels, username, pass,
                            streamType).start();
                }
            }
        }
    }

    void addAddress() {
        List<PlayNode> nodeList = application.getAllChildNodeList();
        String deviceName = etalias.getText().toString();
        String address = etaddress.getText().toString();
        String port = etport.getText().toString();
        String username = etuser.getText().toString();
        String pass = etpassword.getText().toString();

        int channels = 0;
        for (int i = 0; i < nodeList.size(); i++) {
            PlayNode node = nodeList.get(i);
            if (deviceName.equals(node.getName()) || address.equals(node.ip)) {
                showToast(getString(R.string.dev_existent));
                return;
            }
        }
        if (CkdvrORnvr.isChecked()) {

            if (devchannels == 0) {
                showToast(getString(R.string.over_maxchannel));
                return;
            }
            if (addchannels != 0) {
                if (devchannels > addchannels) {
                    showToast(getString(R.string.over_maxchannel));
                    return;
                }
            }

            channels = devchannels;
        } else
            channels = 0;

        if (TextUtils.isEmpty(deviceName)) {
            showToast(getString(R.string.input_not_empty));
        } else {
            if (TextUtils.isEmpty(address) || TextUtils.isEmpty(port)) {
                showToast(getString(R.string.input_not_empty));
            } else {
                if (TextUtils.isEmpty(username)) {
                    showToast(getString(R.string.enter_user_name));
                } else {
                    progressDialog.show();
                    PlayNode temp = CommonData.GetCurrentParent(currentParent,
                            application.getNodeList());
                    TFileListNode node = null;
                    if (temp == null) {
                        node = null;

                    } else {
                        node = temp.node;

                    }

                    new AddThread(this, true, node,
                            hander, deviceName, address, port,
                            port.equals("34567") ? 2060 : 1009, channels,
                            username, pass, streamType).start();
                }
            }
        }
    }

    public boolean checkValue() {
        List<PlayNode> nodeList = application.getNodeList();
        String name = etalias.getText().toString().trim();
        for (int i = 0; i < nodeList.size(); i++) {
            PlayNode node = nodeList.get(i);
            if (name.equals(node.getName())
                    && !node.getDeviceId().equals(modifyNode.getDeviceId())) {
                showToast(getString(R.string.dev_existent));
                return false;
            }
        }
        if (deviceType == 0) {

            String user = etuser.getText().toString().trim();
            String pass = etpassword.getText().toString().trim();
            String id = etumid.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                etalias.setError(getString(R.string.input_not_empty));
                etalias.requestFocus();
                return false;
            }
            if (TextUtils.isEmpty(user)) {
                etuser.setError(getString(R.string.input_not_empty));
                etuser.requestFocus();
                return false;
            }
            if (TextUtils.isEmpty(id)) {
                etumid.setError(getString(R.string.input_not_empty));
                etumid.requestFocus();
                return false;
            }
            info.pDevUser = user;
            info.sNodeName = name;
            info.pDevPwd = pass;
            info.pDevId = id;
        } else {
            String user = etuser.getText().toString().trim();
            String pass = etpassword.getText().toString().trim();
            String id = etaddress.getText().toString().trim();
            String port = etport.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                etalias.setError(getString(R.string.input_not_empty));
                etalias.requestFocus();
                return false;
            }
            if (TextUtils.isEmpty(user)) {
                etuser.setError(getString(R.string.input_not_empty));
                etuser.requestFocus();
                return false;
            }
            if (TextUtils.isEmpty(port)) {
                etport.setError(getString(R.string.input_not_empty));
                etport.requestFocus();
                return false;
            }
            if (TextUtils.isEmpty(id)) {
                etaddress.setError(getString(R.string.input_not_empty));
                etaddress.requestFocus();
                return false;
            }
            info.pDevUser = user;
            info.sNodeName = name;
            info.pDevPwd = pass;
            info.pAddress = id;
            info.devport = Integer.parseInt(port);
        }

        return true;
    }

    public boolean Edit() {
        info.streamtype = streamType;
        ClientCore clientCore = ClientCore.getInstance();
        TFileListNode tFileListNode = modifyNode.node;
        int dev_ch_no = 0;
        if (modifyNode.isCamera()) {
            dev_ch_no = info.iChNo;
        }
        clientCore.modifyNodeInfo(tFileListNode.dwNodeId,
                info.sNodeName, tFileListNode.iNodeType, tFileListNode.id_type,
                tFileListNode.usVendorId, info.pDevId, info.pAddress,
                info.devport, info.pDevUser, info.pDevPwd, dev_ch_no,
                info.streamtype, new Handler() {

                    @Override
                    public void handleMessage(Message msg) {
                        ResponseCommon responseCommon = (ResponseCommon) msg.obj;
                        if (responseCommon != null && responseCommon.h != null) {
                            if (responseCommon.h.e == 200) {
                                hander.sendEmptyMessage(EDIT_OK);
                            } else {
                                Log.e("modifyNodeInfo", " 修改设备设备失败!code="
                                        + responseCommon.h.e);
                                hander.sendEmptyMessage(EDIT_FAIL);
                            }
                        } else {
                            Log.e("modifyNodeInfo", " 修改设备设备失败!code="
                                    + msg.what);
                            hander.sendEmptyMessage(EDIT_FAIL);
                        }
                        super.handleMessage(msg);
                    }

                });

        return isModify;

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        // TODO Auto-generated method stub
        if (checkedId == R.id.rb_direct) {
            deviceType = 1;
            lay_p2p.setVisibility(View.GONE);
            lay_direct.setVisibility(View.VISIBLE);
        } else {
            deviceType = 0;
            lay_p2p.setVisibility(View.VISIBLE);
            lay_direct.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        // showMadeID();

        switch (v.getId()) {
            case R.id.ck_oneC:
                devchannels = 1;
                break;
            case R.id.ck_fourC:
                devchannels = 4;
                break;
            case R.id.ck_eightC:
                devchannels = 8;
                break;
            case R.id.ck_sixteenC:
                devchannels = 16;
                break;
            case R.id.ck_24:
                devchannels = 24;
                break;
            case R.id.ck_32:
                devchannels = 32;

                break;
            case R.id.ck_64:
                devchannels = 64;
                break;
            case R.id.ck_128:
                devchannels = 128;
                break;
            case R.id.btn_async_cancel:
                asyncDialog.dismiss();
                break;
            case R.id.btn_async_sure:
                String ssid = devSSid;
                String pass = etWifiPass.getText().toString();
                if (TextUtils.isEmpty(ssid)) {
                    showToast(getString(R.string.wifi_enter_ssid));
                    return;
                }
                new SetWifi(this, ssid, pass).execute();
                break;
            default:
                break;
        }
        select(devchannels);
    }

    void showDialog() {
        if (asyncDialog == null) {
            asyncDialog = new Dialog(this, R.style.MMTheme_DataSheet);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_dialog_wifi_device, null);
            etSsid = (Spinner) view.findViewById(R.id.wifi_enter_ssid);
            etWifiPass = (EditText) view.findViewById(R.id.wifi_enter_pass);
            ckShowPass = (CheckBox) view.findViewById(R.id.ck_show_pass);
            btnAsyncSure = (Button) view.findViewById(R.id.btn_async_sure);
            btnAsyncSure.setOnClickListener(this);
            btnAsyncCancel = (Button) view.findViewById(R.id.btn_async_cancel);
            btnAsyncCancel.setOnClickListener(this);
            // 密码可见监听
            ckShowPass
                    .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                                     boolean isChecked) {
                            // TODO Auto-generated method stub
                            if (isChecked) {
                                etWifiPass
                                        .setTransformationMethod(HideReturnsTransformationMethod
                                                .getInstance());
                            } else {
                                etWifiPass
                                        .setTransformationMethod(PasswordTransformationMethod
                                                .getInstance());
                            }

                        }
                    });
            adapter = new ArrayAdapter<String>(AddDevActivity.this,
                    android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            etSsid.setAdapter(adapter);
            etSsid.setOnItemSelectedListener(AddDevActivity.this);
            etSsid.setSelection(0);
            new ThreadSearchAPDevice().execute();
            asyncDialog.setContentView(view);
            asyncDialog.setCanceledOnTouchOutside(true);
        }
        asyncDialog.show();
    }

    public void showProgressDialog(String message) {
        if (pd == null) {
            pd = new ShowProgress(this);
            pd.setCanceledOnTouchOutside(false);
        }
        pd.setMessage(message);
        if (!pd.isShowing()) {
            pd.show();
        }

    }

    public class ThreadSearchAPDevice extends
            AsyncTask<Void, Void, List<ScanResult>> {
        private WiFiAdmin wifiAdmin;
        private List<ScanResult> list;
        private String tempSSID;

        @Override
        protected List<ScanResult> doInBackground(Void... params) {
            list = wifiAdmin.getScanResultList();
            tempSSID = wifiAdmin.getSSID();
            return list;

        }

        @Override
        protected void onPostExecute(List<ScanResult> flist) {
            // TODO Auto-generated method stub

            pd.dismiss();
            if (flist == null) {
                showToast(getString(R.string.wifi_get_failre));
            } else {
                if (flist.size() == 0) {
                    showToast(getString(R.string.wifi_get_failre));
                } else {
                    for (int i = 0; i < flist.size(); i++) {
                        String SSID = flist.get(i).SSID;
                        adapter.add(SSID);
                    }

                    adapter.notifyDataSetChanged();
                    etSsid.setSelection(0);
                }
            }
            super.onPostExecute(list);
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub

            showProgressDialog(AddDevActivity.this.getResources().getString(
                    R.string.wifi_searching));
            if (list != null) {
                list.clear();
            }
            if (wifiAdmin == null) {
                wifiAdmin = new WiFiAdmin(AddDevActivity.this);
            }

            super.onPreExecute();
        }
    }

    class SetWifi extends AsyncTask<Void, Integer, Void> {
        Context context;
        ShowProgress showProgress;
        String ssid;
        String password;

        public SetWifi(Context context, String ssid, String password) {
            super();
            this.context = context;
            this.ssid = ssid;
            this.password = password;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            showProgress = new ShowProgress(context);
            showProgress.setMessage(getString(R.string.wifi_settings));
            showProgress.setCancelable(true);
            showProgress.setOnCancelListener(new OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    // TODO Auto-generated method stub
                    SmarkLinkTool.StopSmartConnection();
                }
            });
            showProgress.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            showProgress.dismiss();
            asyncDialog.dismiss();
            super.onPostExecute(result);
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            int time = 30;
            SmarkLinkTool.StartSmartConnection(ssid, password);
            while (time > 0) {
                time--;
                Log.d("StartSmartConnection", "StartSmartConnection" + time);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                publishProgress(time);
            }
            SmarkLinkTool.StopSmartConnection();
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // TODO Auto-generated method stub
            showProgress.setMessage(values[0] + "s");

            super.onProgressUpdate(values);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
        // TODO Auto-generated method stub
        devSSid = (String) etSsid.getItemAtPosition(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub

    }

    void select(int channel) {
        devchannels = channel;
        switch (channel) {
            case 1:
                ck_oneC.setBackgroundResource(R.drawable.ck_sixteenc_h);
                ck_fourC.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_eightC.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_24.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_32.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_sixteenC.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_64.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_128.setBackgroundResource(R.drawable.ck_sixteenc_l);
                break;
            case 4:
                ck_oneC.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_fourC.setBackgroundResource(R.drawable.ck_sixteenc_h);
                ck_eightC.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_24.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_32.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_sixteenC.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_64.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_128.setBackgroundResource(R.drawable.ck_sixteenc_l);
                break;
            case 8:
                ck_oneC.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_fourC.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_eightC.setBackgroundResource(R.drawable.ck_sixteenc_h);
                ck_24.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_32.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_sixteenC.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_64.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_128.setBackgroundResource(R.drawable.ck_sixteenc_l);
                break;
            case 16:
                ck_oneC.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_fourC.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_eightC.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_24.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_32.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_sixteenC.setBackgroundResource(R.drawable.ck_sixteenc_h);
                ck_64.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_128.setBackgroundResource(R.drawable.ck_sixteenc_l);
                break;
            case 24:
                ck_oneC.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_fourC.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_eightC.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_24.setBackgroundResource(R.drawable.ck_sixteenc_h);
                ck_32.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_sixteenC.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_64.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_128.setBackgroundResource(R.drawable.ck_sixteenc_l);
                break;
            case 32:
                ck_oneC.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_fourC.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_eightC.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_24.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_32.setBackgroundResource(R.drawable.ck_sixteenc_h);
                ck_sixteenC.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_64.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_128.setBackgroundResource(R.drawable.ck_sixteenc_l);
                break;
            case 128:
                ck_oneC.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_fourC.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_eightC.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_24.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_32.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_sixteenC.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_64.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_128.setBackgroundResource(R.drawable.ck_sixteenc_h);
                break;
            case 64:
                ck_oneC.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_fourC.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_eightC.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_24.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_32.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_sixteenC.setBackgroundResource(R.drawable.ck_sixteenc_l);
                ck_64.setBackgroundResource(R.drawable.ck_sixteenc_h);
                ck_128.setBackgroundResource(R.drawable.ck_sixteenc_l);
                break;
            default:
                break;
        }
    }
}
