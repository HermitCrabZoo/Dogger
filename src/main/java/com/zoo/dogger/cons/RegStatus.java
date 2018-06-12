package com.zoo.dogger.cons;

public enum RegStatus {
	ORIGIN("原始"),SAVED("已保存"),UNSAVE("未保存"),ILLEGAL("非法");
	
	private final String value;

	private RegStatus(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
	@Override
	public String toString() {
		return value;
	}
	
}
