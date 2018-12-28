package com.jvt.bean;

public class DeviceInfo {
	String name="";
	int id;
	String address="";
	int channels;
	String umid;



	public DeviceInfo(String name, int id, String address, int channels,
                      String umid) {
		super();
		this.name = name;
		this.id = id;
		this.address = address;
		this.channels = channels;
		this.umid = umid;
	}

	public DeviceInfo(String name, int id) {
		this.name = name;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getChannels() {
		return channels;
	}

	public void setChannels(int channels) {
		this.channels = channels;
	}

	public String getUmid() {
		return umid;
	}

	public void setUmid(String umid) {
		this.umid = umid;
	}

}
