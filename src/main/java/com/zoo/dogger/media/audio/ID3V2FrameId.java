package com.zoo.dogger.media.audio;

/**
 * ID3V2.3帧类型
 * @author ZOO
 *
 */
public enum ID3V2FrameId implements FrameId{
	
	AENC("音频加密技术"),
	APIC("附加描述"),
	COMM("备注"),
	COMR("广告"),
	ENCR("加密方法注册"),
	ETC0("事件时间编码"),
	GEOB("常规压缩对象"),
	GRID("组识别注册"),
	IPLS("复杂类别列表"),
	MCDI("音乐CD标识符"),
	MLLT("MPEG位置查找表格"),
	OWNE("所有权"),
	PRIV("私有"),
	PCNT("播放计数"),
	POPM("普通仪表"),
	POSS("位置同步"),
	RBUF("推荐缓冲区大小"),
	RVAD("音量调节器"),
	RVRB("混响"),
	SYLT("同步歌词或文本"),
	SYTC("同步节拍编码"),
	TALB("专辑"),
	TBPM("每分钟节拍数"),
	TCOM("作曲家"),
	TCON("流派（风格），见表2"),
	TCOP("版权"),
	TDAT("日期"),
	TDLY("播放列表返录"),
	TENC("编码 "),
	TEXT("歌词作者"),
	TFLT("文件类型"),
	TIME("时间"),
	TIT1("内容组描述"),
	TIT2("标题"),
	TIT3("副标题"),
	TKEY("最初关键字"),
	TLAN("语言"),
	TLEN("长度"),
	TMED("媒体类型"),
	TOAL("原唱片集"),
	TOFN("原文件名"),
	TOLY("原歌词作者"),
	TOPE("原艺术家"),
	TORY("最初发行年份"),
	TOWM("文件所有者（许可证者）"),
	TPE1("艺术家"),
	TPE2("乐队"),
	TPE3("指挥者"),
	TPE4("翻译（记录员、修改员）"),
	TPOS("作品集部分"),
	TPUB("发行人"),
	TRCK("音轨（曲号）"),
	TRDA("录制日期"),
	TRSN("Intenet电台名称"),
	TRSO("Intenet电台所有者"),
	TSIZ("大小"),
	TSRC("ISRC（国际的标准记录代码）"),
	TSSE("编码使用的软件（硬件设置）"),
	TYER("年代"),
	TXXX("年度"),
	UFID("唯一的文件标识符"),
	USER("使用条款"),
	USLT("歌词"),
	WCOM("广告信息"),
	WCOP("版权信息"),
	WOAF("官方音频文件网页"),
	WOAR("官方艺术家网页"),
	WOAS("官方音频原始资料网页"),
	WORS("官方互联网无线配置首页"),
	WPAY("付款"),
	WPUB("出版商官方网页"),
	WXXX("用户定义的URL链接");
	
	
	private String remark;
	
	private ID3V2FrameId(String remark){
		this.remark=remark;
	}

	public String getRemark() {
		return remark;
	}

}
