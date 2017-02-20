package com.tchip.autoprovider;

public class AutoState {

	String name; // 字段
	String value; // 内容

	public AutoState(String name, String content) {
		this.name = name;
		this.value = content;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
