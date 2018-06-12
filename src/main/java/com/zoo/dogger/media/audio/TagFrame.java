package com.zoo.dogger.media.audio;

public class TagFrame {
	
	public TagFrame() {}
	
	public TagFrame(String id, String flag, int headSize, int size, String value, int start) {
		this.id = id;
		this.flag = flag;
		this.headSize = headSize;
		this.size = size;
		this.value = value;
		this.start = start;
	}


	/**
	 * 帧id
	 */
	private String id;
	
	/**
	 * 帧标识
	 */
	private String flag;
	
	/**
	 * 帧头长度
	 */
	private int headSize;
	/**
	 * 帧长度
	 */
	private int size;
	
	/**
	 * 帧内容
	 */
	private String value;
	
	
	/**
	 * 帧起始位置
	 */
	private int start=-1;
	
	
	/**
	 * 帧id
	 */
	public String getId() {
		return id;
	}
	
	
	public void setId(String id) {
		this.id = id;
	}
	
	
	/**
	 * 帧标识
	 */
	public String getFlag() {
		return flag;
	}
	
	
	public void setFlag(String flag) {
		this.flag = flag;
	}
	
	
	/**
	 * 帧长度
	 */
	public int getSize() {
		return size;
	}
	
	
	public void setSize(int size) {
		this.size = size;
	}
	
	
	/**
	 * 帧内容
	 */
	public String getValue() {
		return value;
	}
	
	
	public void setValue(String value) {
		this.value = value;
	}
	
	/**
	 * 帧头长度
	 */
	public int getHeadSize() {
		return headSize;
	}


	public void setHeadSize(int headSize) {
		this.headSize = headSize;
	}


	/**
	 * 帧起始位置
	 */
	public int getStart() {
		return start;
	}


	public void setStart(int start) {
		this.start = start;
	}


	@Override
	public String toString() {
		return "TagFrame [id=" + id + ", flag=" + flag + ", headSize=" + headSize + ", size=" + size + ", value="
				+ value + ", start=" + start + "]";
	}

}
