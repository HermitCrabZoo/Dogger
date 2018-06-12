package com.zoo.dogger.media.audio;

import com.zoo.base.Arrs;
import com.zoo.base.Strs;

public class ID3V1Info extends TagInfo{
	
	
	private static final int[] seeks = {3,33,63,90,97,125,126,127};
	private static final int[] lens = {30,30,30,4,28,1,1,1};
	private static final int len=seeks.length;
	
	
	public ID3V1Info() {}

	public ID3V1Info(byte[] datas) {
		isValid=extract(datas);
	}
	
	private boolean extract(byte[] id3v1){
		int total=Arrs.len(id3v1);
		if (total<3) {// 数据长度是否合法,至少包含3字节的信息
			return false;
		}
		start=Arrs.lastIndex("TAG".getBytes(), id3v1, 0,total-128);
		int mayApev2TagFooterIndex=Arrs.lastIndex("APETAGEX".getBytes(), id3v1, total-start-5,start-3);
		//没有TAG标签则返回false
		if (start==-1 || mayApev2TagFooterIndex!=-1) {
			return false;
		}
		headSize=3;//标签头部分长度
		size=Math.min(128, total-start);//标签总长度
		
		//尽量读取
		String[] infos=new String[len];
		for (int i = 0; i < len; i++) {
			int s=seeks[i]+start,l=lens[i];
			int c=total-s;
			if (c<=0) {
				break;
			}
			infos[i]=new String(id3v1, s, Math.min(l, c), GBK).trim();
		}
		
		boolean isThirty=!"0".equals(infos[5]);
		// 歌曲名称
		this.title = infos[0];
		// 歌手名字
		this.artist = infos[1];
		// 专辑名称
		this.album = infos[2];
		// 出品年份
		this.year = infos[3];
		// 备注信息
		this.comment = isThirty?(infos[4]+infos[5]+infos[6]):infos[4];
		// 音轨信息
		this.track = isThirty?Strs.empty():infos[6];
		// 歌曲信息
		this.genre = alineGenre(infos[7]);
		
		return true;
	}

	@Override
	public String toString() {
		return "ID3V1Info [isValid=" + isValid + ", headSize=" + headSize + ", size=" + size + ", start=" + start
				+ ", title=" + title + ", artist=" + artist + ", album=" + album + ", year=" + year + ", comment="
				+ comment + ", track=" + track + ", genre=" + genre + "]";
	}
	
}
