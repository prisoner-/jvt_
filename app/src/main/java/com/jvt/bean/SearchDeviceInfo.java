package com.jvt.bean;

import java.io.Serializable;

public class SearchDeviceInfo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 8562951431755164029L;
    public int dwVendorId;
    public String sDevModel;
    public String sUMDevModel;
    public String sDevName;
    public String sDevId;
    public String sDevUserName;
    public int bIfSetPwd;
    public String sCloudServerAddr;
    public int usCloudServerPort;
    public int usChNum;
    public int iDevIdType;
    public int bIfAllowSetIpaddr;
    public int bIfEnableDhcp;
    public int iAdapterNum;
    public String sAdapterName_1;
    public String sAdapterMac_1;
    public String sIpaddr_1;
    public String sNetmask_1;
    public String sGateway_1;
    public String sAdapterName_2;
    public String sAdapterMac_2;
    public String sIpaddr_2;
    public String sNetmask_2;
    public String sGateway_2;
    public String sAdapterName_3;
    public String sAdapterMac_3;
    public String sIpaddr_3;
    public String sNetmask_3;
    public String sGateway_3;
    public int iDevPort; // 设备的端口号

    public SearchDeviceInfo(int dwVendorId, String sDevName, String sDevId,
                            String sDevUserName, int bIfSetPwd, int bIfEnableDhcp,
                            String sAdapterName_1, String sAdapterMac_1, String sIpaddr_1,
                            String sNetmask_1, String sGateway_1, int usChNum, int iDevPort, String sDevModel) {
        super();
        this.sDevModel=sDevModel;
        this.usChNum = usChNum;
        this.dwVendorId = dwVendorId;
        this.sDevName = sDevName;
        this.sDevId = sDevId;
        this.sDevUserName = sDevUserName;
        this.bIfSetPwd = bIfSetPwd;
        this.bIfEnableDhcp = bIfEnableDhcp;
        this.sAdapterName_1 = sAdapterName_1;
        this.sAdapterMac_1 = sAdapterMac_1;
        this.sIpaddr_1 = sIpaddr_1;
        this.sNetmask_1 = sNetmask_1;
        this.sGateway_1 = sGateway_1;
        this.iDevPort = iDevPort;
    }

    public int getDwVendorId() {
        return dwVendorId;
    }

    public void setDwVendorId(int dwVendorId) {
        this.dwVendorId = dwVendorId;
    }

    public String getsDevName() {
        return sDevName;
    }

    public void setsDevName(String sDevName) {
        this.sDevName = sDevName;
    }

    public String getsDevId() {
        return sDevId;
    }

    public void setsDevId(String sDevId) {
        this.sDevId = sDevId;
    }

    public String getsDevUserName() {
        return sDevUserName;
    }

    public void setsDevUserName(String sDevUserName) {
        this.sDevUserName = sDevUserName;
    }

    public int getbIfSetPwd() {
        return bIfSetPwd;
    }

    public void setbIfSetPwd(int bIfSetPwd) {
        this.bIfSetPwd = bIfSetPwd;
    }

    public int getbIfEnableDhcp() {
        return bIfEnableDhcp;
    }

    public void setbIfEnableDhcp(int bIfEnableDhcp) {
        this.bIfEnableDhcp = bIfEnableDhcp;
    }

    public String getsAdapterName_1() {
        return sAdapterName_1;
    }

    public void setsAdapterName_1(String sAdapterName_1) {
        this.sAdapterName_1 = sAdapterName_1;
    }

    public String getsAdapterMac_1() {
        return sAdapterMac_1;
    }

    public void setsAdapterMac_1(String sAdapterMac_1) {
        this.sAdapterMac_1 = sAdapterMac_1;
    }

    public String getsIpaddr_1() {
        return sIpaddr_1;
    }

    public void setsIpaddr_1(String sIpaddr_1) {
        this.sIpaddr_1 = sIpaddr_1;
    }

    public String getsNetmask_1() {
        return sNetmask_1;
    }

    public void setsNetmask_1(String sNetmask_1) {
        this.sNetmask_1 = sNetmask_1;
    }

    public String getsGateway_1() {
        return sGateway_1;
    }

    public void setsGateway_1(String sGateway_1) {
        this.sGateway_1 = sGateway_1;
    }

}
