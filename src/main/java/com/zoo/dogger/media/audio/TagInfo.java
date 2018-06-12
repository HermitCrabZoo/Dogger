package com.zoo.dogger.media.audio;

import java.nio.charset.Charset;

import com.zoo.base.Strs;

public class TagInfo{
	
	protected static final Charset GBK=Charset.forName("GBK");
	protected static final Charset UNICODE=Charset.forName("unicode");
	
	
	/**
	 * 是否存在有效的ID3V1标签信息
	 */
	protected boolean isValid=false;
	
	/**
	 * 标签头长度
	 */
	protected int headSize;
	
	/**
	 * 标签总长度(包含头部和标签帧)
	 */
	protected int size;
	
	/**
	 * 标签开始的索引
	 */
	protected int start=-1;
	
	
	/**
	 * 歌曲名
	 */
	protected String title;
	
	/**
	 * 歌手名
	 */
	protected String artist;
	
	/**
	 * 专辑名
	 */
	protected String album;
	
	/**
	 * 年份
	 */
	protected String year;
	
	/**
	 * 注释
	 */
	protected String comment;
	
	/**
	 * 音轨
	 */
	protected String track;
	
	/**
	 * 歌曲风格
	 */
	protected String genre;

	/**
	 * 获取歌曲名
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * 获取歌手名
	 * @return
	 */
	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}
	
	/**
	 * 获取专辑名
	 * @return
	 */
	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	/**
	 * 获取年份
	 * @return
	 */
	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	/**
	 * 获取注释
	 * @return
	 */
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * 获取音轨
	 * @return
	 */
	public String getTrack() {
		return track;
	}

	public void setTrack(String track) {
		this.track = track;
	}

	/**
	 * 获取歌曲风格编号
	 * @return
	 */
	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}


	/**
	 * 是否存在有效的ID3V1标签信息
	 */
	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}
	
	
	/**
	 * 标签头的长度
	 * @return
	 */
	public int getHeadSize() {
		return headSize;
	}

	public void setHeadSize(int headSize) {
		this.headSize = headSize;
	}
	

	/**
	 * ID3V2标签信息总长度(包括标签头和标签帧)
	 * @return
	 */
	public int getSize() {
		return size;
	}


	public void setSize(int size) {
		this.size = size;
	}


	/**
	 * 该标签在数据中开始的索引值
	 * @return
	 */
	public int getStart() {
		return start;
	}


	public void setStart(int start) {
		this.start = start;
	}

	/**
	 * 风格ID左对齐补零
	 * @param genreId
	 * @return
	 */
	public String alineGenre(String genreId) {
		return (Strs.notEmpty(genreId)?Strs.rjust(genreId, 2, '0'):Strs.nullToEmpty(genreId)).toUpperCase();
	}

	@Override
	public String toString() {
		return "TagInfo [isValid=" + isValid + ", headSize=" + headSize + ", size=" + size + ", start=" + start
				+ ", title=" + title + ", artist=" + artist + ", album=" + album + ", year=" + year + ", comment="
				+ comment + ", track=" + track + ", genre=" + genre + "]";
	}

	
}
