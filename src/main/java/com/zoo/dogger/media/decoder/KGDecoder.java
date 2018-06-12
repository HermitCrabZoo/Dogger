package com.zoo.dogger.media.decoder;

import java.util.List;

import com.zoo.base.Arrs;

/**
 * 酷狗音乐缓存文件解码器
 * 
 * @author ZOO
 *
 */
public class KGDecoder implements Decoder {

	private static final int[] KEYS = new int[] { 0xAC, 0xEC, 0xDF, 0x57 };
	private static final String NAME="酷狗音乐缓存解码器";
	
	private List<String> suffixs=Arrs.of("kgtemp");
	
	/**
	 * @param raws 长度必须是4的倍数
	 */
	@Override
	public byte[] decode(byte[] raws) {
		//每份按keys的长度分
		byte[][] avgs = Arrs.splitEverys(raws, KEYS.length);
		byte[][] results=new byte[avgs.length][];
		
		for (int j=0;j<avgs.length;j++) {
			
			int len = avgs[j].length;
			byte[] result=new byte[len];
			
			for (int i = 0; i < len; i++) {
				int h = avgs[j][i] >> 4;
				int l = avgs[j][i] & 0xf;
				int kh = KEYS[i] >> 4;
				int kl = KEYS[i] & 0xf;
				int y = l ^ kl;
				y = (h ^ kh ^ y) << 4 | y;
				result[i]=(byte) y;
			}
			results[j]=result;
		}
		//合成一个数组
		return Arrs.concat(results);
	}
	
	@Override
	public String name() {
		return NAME;
	}
	
	@Override
	public long skip() {
		return 1024;
	}
	
	@Override
	public String[] suffixs() {
		return suffixs.toArray(new String[suffixs.size()]);
	}
	
	@Override
	public Decoder putSuffix(String... suffix) {
		if (suffix!=null) {
			for (String s : suffix) {
				if (s!=null && !suffixs.contains(s)) {
					suffixs.add(s);
				}
			}
		}
		return this;
	}
	
}
