package com.zoo.dogger.media.audio;

import java.util.HashMap;
import java.util.Map;

import com.zoo.base.Strs;

public class ExtensibleTagInfo extends TagInfo {
	
	protected String version;
	
	protected String flag;
	
	protected Map<String,TagFrame> frames=new HashMap<>();
	
	/**
	 * 主版本号
	 * @return
	 */
	public String getVersion() {
		return version;
	}


	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * 标志值
	 * @return
	 */
	public String getFlag() {
		return flag;
	}


	public void setFlag(String flag) {
		this.flag = flag;
	}

	
	/**
	 * 通过帧ID来获取对应帧的内容
	 * @param frameId
	 * @return
	 */
	public String getTagContent(FrameId frameId) {
		if (frameId!=null) {
			TagFrame frame=frames.get(frameId.name());
			return frame==null?Strs.empty():Strs.nullToEmpty(frame.getValue());
		}
		return Strs.empty();
	}
	
	
	/**
	 * 通过帧ID来获取对应帧对象
	 * @param frameId
	 * @return
	 */
	public TagFrame getTagFrame(FrameId frameId) {
		if (frameId!=null) {
			return frames.get(frameId.name());
		}
		return null;
	}
	
	/**
	 * 获取所有标签帧对象
	 * @return
	 */
	public Map<String,TagFrame> frames(){
		Map<String,TagFrame> t=new HashMap<>(frames);
		return t;
	}


	@Override
	public String toString() {
		return "ExtensibleTagInfo [isValid=" + isValid + ", headSize=" + headSize + ", size=" + size + ", start="
				+ start + ", title=" + title + ", artist=" + artist + ", album=" + album + ", year=" + year
				+ ", comment=" + comment + ", track=" + track + ", genre=" + genre + ", version=" + version + ", flag="
				+ flag + ", frames=" + frames + "]";
	}

	
}
