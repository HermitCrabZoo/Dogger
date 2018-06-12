package com.zoo.dogger.media.audio;

import com.zoo.base.Arrs;

public class APEV2Info extends ExtensibleTagInfo {
	
	private String reserved;
	
	public APEV2Info() {}


	public APEV2Info(byte[] datas) {
		isValid=extract(datas);
	}
	
	
	private boolean extract(byte[] apev2){
		int total=Arrs.len(apev2);
		if (total<32) {// 数据长度是否合法,至少包含8字节的信息
			return false;
		}
		
		byte[] apetags="APETAGEX".getBytes();
		int endOffset=0;//标签在末尾的偏移值
		
		//判断是否有ID3V1标签,有则跨过TAG标签所在位置做识别
		int tagIndex=Arrs.lastIndex("TAG".getBytes(), apev2, 0,total-128);
		int mayApev2TagFooterIndex=Arrs.lastIndex(apetags, apev2, total-tagIndex-5,tagIndex-3);
		if (tagIndex!=-1  && mayApev2TagFooterIndex==-1) {
			endOffset=total-tagIndex+24;
		}else {
			endOffset=24;
		}
		
		//APEV2标签尾位置
		int rare=Arrs.lastIndex(apetags, apev2, endOffset,total-8192);
		//存在APEV2标签
		if (rare!=-1) {
			rare+=12;
			size=(apev2[rare++] & 0xFF) | (apev2[rare++] & 0xFF) << 8 | (apev2[rare++] & 0xFF) << 16 | (apev2[rare++] & 0xFF) << 24;
			rare-=16;
			//标签头开始的位置
			start=Math.min(Arrs.index(apetags, apev2, rare-size), rare);
			size+=32;
		}else {//不存在APEV2标签
			return false;
		}
		
		//整个APEV2标签头32字节长度
		headSize=32;
		version=new String(apev2, start+8, 4).trim();//版本
		flag=new String(apev2, start+20, 4).trim();//标志
		reserved=new String(apev2, start+24, 8).trim();//保留
		
		int s=start+32;
		int maxmum=Math.min(total, size+start-(start==rare?0:32));
		//获取每个帧的信息,并且每帧至少9字节长度
		while ( s+9 < maxmum ) {
			int startPos=s;
			int contentSize=(apev2[s++] & 0xFF) | (apev2[s++] & 0xFF) << 8 | (apev2[s++] & 0xFF) << 16 | (apev2[s++] & 0xFF) << 24;//该帧内容大小不包含帧头
			String flags=new String(apev2, s, 4).trim();//帧标志
			s+=4;
			int frameIdLen=frameIdLength(apev2, s, size-(s-start));//帧ID的长度
			String frameId=new String(apev2, s, frameIdLen).trim();//帧ID
			s+=frameIdLen;
			contentSize=Math.min(Math.max(contentSize,0),total-s);//帧内容长度
			String content=new String(apev2, s,contentSize).trim();//帧内容
			s+=contentSize;
			//构造标签帧对象
			TagFrame frame=new TagFrame(frameId, flags, 11, s-startPos, content, startPos);
			frames.put(frameId.toUpperCase(), frame);
		}
		
		this.title=getTagContent(APEV2FrameId.TITLE);//歌曲名
		this.artist=getTagContent(APEV2FrameId.ARTIST);//艺术家
		this.album=getTagContent(APEV2FrameId.ALBUM);//专辑
		this.year=getTagContent(APEV2FrameId.YEAR);//年份
		this.comment=getTagContent(APEV2FrameId.COMMENT);//备注
		this.track=getTagContent(APEV2FrameId.TRACK);//音轨
		this.genre=alineGenre(getTagContent(APEV2FrameId.GENRE));//风格
		
		return true;
	}
	
	
	/**
	 * 计算帧ID的长度
	 * @param apev2
	 * @param start
	 * @param maxLen
	 * @return
	 */
	private int frameIdLength(byte[] apev2,int start,int maxLen) {
		int len=0;
		for (int i = start,l=apev2.length; i < l && len<=maxLen; i++) {
			len++;
			if(apev2[i]==0) {
				break;
			}
		}
		return len;
	}


	public String getReserved() {
		return reserved;
	}


	public void setReserved(String reserved) {
		this.reserved = reserved;
	}
	
	
}
