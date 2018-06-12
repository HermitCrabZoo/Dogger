package com.zoo.dogger.media.audio;

/**
 * ID3V2.3帧类型
 * @author ZOO
 *
 */
public enum APEV2FrameId implements FrameId{
	
	ALBUM("专辑名"),
	ARTIST("歌手名"),
	COMMENT("备注"),
	COMPOSER("作曲家"),
	COPYRIGHT("版权"),
	ENCODER("编码类型"),
	GENRE("风格"),
	LYRIC("歌词"),
	ORCHESTRA("乐队"),
	TITLE("歌曲名"),
	TRACK("音轨号"),
	WM_AUTHORWEBPAGA("作者网页"),
	WM_BPM(""),
	WM_CODEDBY("编码依据"),
	WM_ENCODEDBY("英文编码依据"),
	WM_FILEWEBPAGE("文件网页"),
	WM_GENREID("风格编号"),
	WM_ORIGARTIST("原创艺术家"),
	WM_PROMOTIONURL("推销URL"),
	WM_URL("URL"),
	WM_WRITER("作者"),
	WMFSDKVERSION("发行版本"),
	WMFSDKNEEDED("发行必需"),
	YEAR("发行日期");
	
	
	private String remark;
	
	private APEV2FrameId(String remark){
		this.remark=remark;
	}

	public String getRemark() {
		return remark;
	}

}
