package com.zoo.dogger.media.decoder;

import java.util.List;

import com.zoo.base.Arrs;
import com.zoo.base.Typer;

/**
 * 网易音乐缓存文件解码器
 * 
 * @author ZOO
 *
 */
public class NEDecoder implements Decoder {

	private static final String NAME="网易音乐缓存解码器";
	
	private List<String> suffixs=Arrs.of("uc");
	
	/**
	 * @param raws 原始加密字节
	 */
	@Override
	public byte[] decode(byte[] raws) {
		if (raws!=null) {
			int len=raws.length;
			byte[] results=new byte[len];
			for (int i = 0; i < len; i++) {
				results[i]=(byte) (raws[i] ^ 0xa3);
			}
			return results;
		}
		return Typer.bytes();
	}
	
	@Override
	public String name() {
		return NAME;
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
