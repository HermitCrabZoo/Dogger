package com.zoo.dogger.media.audio;

/**
 * 音乐风格
 * @author ZOO
 *
 */
public enum Genre {
	
	
	BLUES ("00", "Blues", "布鲁斯"), 
	CLASSIC_ROCK ("01", "ClassicRock", "古典摇滚"), 
	COUNTRY ("02", "Country", "乡村"), 
	DANCE ("03", "Dance", "舞曲"), 
	DISCO ("04", "Disco", "迪斯科"), 
	FUNK ("05", "Funk", "伤感爵士"), 
	GRUNGE ("06", "Grunge", "垃圾摇滚"), 
	HIP_HOP ("07", "Hip-Hop", "饶舌"), 
	JAZZ ("08", "Jazz", "爵士"), 
	METAL ("09", "Metal", "金属"), 
	NEWAGE ("0A", "NewAge", "前卫"), 
	OLDIES ("0B", "Oldies", "怀旧"), 
	OTHER ("0C", "Other", "其他"), 
	POP ("0D", "Pop", "流行"), 
	R_B ("0E", "R&B", "摇滚布鲁斯"), 
	RAP ("0F", "Rap", "说唱"), 
	REGGAE ("10", "Reggae", "雷盖扭摆舞"), 
	ROCK ("11", "Rock", "摇滚"), 
	TECHNO ("12", "Techno", "电子流行乐"), 
	INDUSTRIAL ("13", "Industrial", "工业"), 
	ALTERNATIVE ("14", "Alternative", "多变"), 
	SKA ("15", "Ska", "斯卡"), 
	DEATHMETAL ("16", "DeathMetal", "重金属"), 
	PRANKS ("17", "Pranks", "恶作剧"), 
	SOUNDTRACK ("18", "Soundtrack", "电影配音"), 
	EURO_TECHNO ("19", "Euro-Techno", "神游舞曲"), 
	AMBIENT ("1A", "Ambient", "流行"), 
	TRIP_HOP ("1B", "Trip-Hop", "迷幻舞曲"), 
	VOCAL ("1C", "Vocal", "非纯音乐"), 
	JAZZ_FUNK ("1D", "Jazz+Funk", "爵士摇滚"), 
	FUSION ("1E", "Fusion", "合成音乐");
	
	
	
	private String code;
	private String ename;
	private String cname;
	
	private Genre(String code, String ename, String cname) {
		this.code = code;
		this.ename = ename;
		this.cname = cname;
	}
	
	public String getCode() {
		return code;
	}
	public String getEname() {
		return ename;
	}
	public String getCname() {
		return cname;
	}
	
	/**
	 * 通过关键字获取Genre实例,可以是code、cname、ename中的任意一种
	 * @param codeOrEnameOrCname
	 * @return
	 */
	public static Genre get(String codeOrEnameOrCname) {
		if (codeOrEnameOrCname!=null) {
			codeOrEnameOrCname=codeOrEnameOrCname.trim();
			for(Genre g:values()) {
				if(codeOrEnameOrCname.equalsIgnoreCase(g.code) || codeOrEnameOrCname.equalsIgnoreCase(g.ename) || codeOrEnameOrCname.equalsIgnoreCase(g.cname)) {
					return g;
				}
			}
		}
		return null;
	}
	
	
}
