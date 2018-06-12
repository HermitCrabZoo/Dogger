package com.zoo.dogger.media.audio;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.zoo.base.Arrs;

public class ID3V2Info extends ExtensibleTagInfo {
	
	
	private String revision;
	
	
	public ID3V2Info() {}


	public ID3V2Info(byte[] datas) {
		isValid=extract(datas);
	}
	
	
	private boolean extract(byte[] id3v2){
		int total=Arrs.len(id3v2);
		if (total<21) {//数据是否包含标签头的10个字节,和至少一个最短11个字节的标签帧
			return false;
		}
		//代表ID3V2标签头是否存在
		if (!"ID3".equalsIgnoreCase(new String(id3v2, 0, 3))) {
			return false;
		}
		
		//整个ID3V2标签头10字节长度
		headSize=10;
		start=0;//起始位置
		version=new String(id3v2, 3, 1).trim();//主版本
		revision=new String(id3v2, 4, 1).trim();//副版本
		flag=new String(id3v2, 5, 1).trim();//标志
		size=id3v2[6] << 21 | id3v2[7] << 14 | id3v2[8] << 7 | id3v2[9];//后面所有标签帧的总大小
		size+=10;//加上头部长度
		
		int s=10;//帧起始位置
		int maxmum=Math.min(total, size);
		//获取每个帧的信息,并且每帧至少11字节长度
		while ( s+12 <= maxmum ) {
			//ID3V2中每个帧=帧头(10字节)+大于1字节的帧内容组成
			int startPos=s;
			String frameId=new String(id3v2, s, 4).trim();//帧ID,ascii码
			s+=4;
			int frameSize=id3v2[s++] << 24 | id3v2[s++] << 16 | id3v2[s++] << 8 | id3v2[s++];//该帧内容大小不包含帧头
			String flags=new String(id3v2, s, 2).trim();//帧标志
			s+=2;
			Charset charset=id3v2Charset(id3v2[s]);
			s++;
			frameSize--;//frameSize--是过滤掉内容里第一个字节,该字节代表内容编码方式
			frameSize=Math.min(Math.max(frameSize,0),total-s);
			String content=new String(id3v2, s,frameSize,charset).trim();//帧内容
			s+=frameSize;
			//构造标签帧对象
			TagFrame frame=new TagFrame(frameId, flags, 11, s-startPos, content, startPos);
			frames.put(frameId.toUpperCase(), frame);
		}
		
		this.title=getTagContent(ID3V2FrameId.TIT2);//歌曲名
		this.artist=getTagContent(ID3V2FrameId.TPE1);//艺术家
		this.album=getTagContent(ID3V2FrameId.TALB);//专辑
		this.year=getTagContent(ID3V2FrameId.TYER);//年份
		this.comment=getTagContent(ID3V2FrameId.COMM);//备注
		this.track=getTagContent(ID3V2FrameId.TRCK);//音轨
		this.genre=alineGenre(getTagContent(ID3V2FrameId.TCON));//风格
		
		return true;
	}
	
	
	/**
	 * 获取id3v2标签帧内容的字符集
	 * @param flag
	 * @return
	 */
	private Charset id3v2Charset(int flag) {
		switch (flag) {
		case 0:
			return GBK;
		case 1:
			return UNICODE;
		case 3:
			return StandardCharsets.UTF_8;
		default:
			return Charset.defaultCharset();
		}
	}
	

	/**
	 * 副版本号
	 * @return
	 */
	public String getRevision() {
		return revision;
	}


	public void setRevision(String revision) {
		this.revision = revision;
	}

}
