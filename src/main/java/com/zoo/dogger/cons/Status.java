package com.zoo.dogger.cons;

public enum Status {
	WAIT("待解码"),ING("转码中"),DID("已完成"),UNDO("不解码"),FAIL("失败");
	
	private final String value;

	private Status(String value) {
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
