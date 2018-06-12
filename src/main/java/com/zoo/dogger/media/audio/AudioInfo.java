package com.zoo.dogger.media.audio;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

import com.zoo.base.Arrs;

public class AudioInfo {
	
	
	protected ID3V2Info id3v2Info=new ID3V2Info();
	protected ID3V1Info id3v1Info=new ID3V1Info();
	protected APEV2Info apev2Info=new APEV2Info();
	
	
	/**
	 * 使用音频文件名构造实例
	 * @param filename
	 */
	public AudioInfo(String filename) {
		this(Objects.isNull(filename)?null:new File(filename));
	}
	
	
	
	/**
	 * 通过Path构造实例
	 * @param file
	 */
	public AudioInfo(Path file) {
		this(Objects.isNull(file)?null:file.toFile());
	}

	
	
	/**
	 * 使用File构造实例
	 * @param file
	 */
	public AudioInfo(File file) {
		// 随机读写方式打开MP3文件
		try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
			long len=raf.length();
			
			//未获取到ID3V2信息,再尝试获取ID3V1
			byte[] id3v2=new byte[(int) Math.min(len, 512)];
			raf.read(id3v2);
			id3v2Info=new ID3V2Info(id3v2);
			
			//文件后128个字节的是ID3V1标签帧
			byte[] id3v1 = new byte[(int) Math.min(len,128)];
			raf.seek(len - id3v1.length);//移动到MP3文件末尾
			raf.read(id3v1);
			id3v1Info=new ID3V1Info(id3v1);
			
			//APEV2信息
			byte[] apev2 = new byte[(int) Math.min(len,1024)];
			raf.seek(len-apev2.length);
			raf.read(apev2);
			apev2Info=new APEV2Info(apev2);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 使用音频文件的数据构造实例
	 * @param filename
	 */
	public AudioInfo(byte[] datas) {
		//ID3V2信息
		id3v2Info=new ID3V2Info(datas);
		
		//ID3V1信息
		int len=Arrs.len(datas);
		id3v1Info=new ID3V1Info(len<=128?datas:Arrays.copyOfRange(datas, len-128, len));
		
		//APEV2信息
		apev2Info=new APEV2Info(datas);
	}
	
	
	public ID3V1Info getId3v1Info() {
		return id3v1Info;
	}

	public void setId3v1Info(ID3V1Info id3v1Info) {
		this.id3v1Info = id3v1Info;
	}

	public ID3V2Info getId3v2Info() {
		return id3v2Info;
	}

	public void setId3v2Info(ID3V2Info id3v2Info) {
		this.id3v2Info = id3v2Info;
	}

	public APEV2Info getApev2Info() {
		return apev2Info;
	}

	public void setApev2Info(APEV2Info apev2Info) {
		this.apev2Info = apev2Info;
	}

	@Override
	public String toString() {
		return "AudioInfo [id3v2Info=" + id3v2Info + ", id3v1Info=" + id3v1Info + ", apev2Info=" + apev2Info + "]";
	}

}
