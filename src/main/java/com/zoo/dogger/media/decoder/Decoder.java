package com.zoo.dogger.media.decoder;

public interface Decoder {
	
	
	/**
	 * 传入编码的字节数组,返回解密后的字节数组
	 * @param raws
	 * @return
	 */
	byte[] decode(byte[] raws);
	
	/**
	 * 解码器名称
	 * @return
	 */
	default String name() {
		return null;
	}
	
	/**
	 * 版本号信息
	 * @return
	 */
	default String version() {
		return "1.0";
	}
	
	/**
	 * 跳过的字节数
	 * @return
	 */
	default long skip() {
		return 0;
	}
	
	/**
	 * 默认的可解码的文件名后缀
	 * @return
	 */
	default String[] suffixs() {
		return new String[]{};
	}
	
	/**
	 * 添加该解码器可解码的文件类型后缀
	 * @param suffix
	 * @return
	 */
	default Decoder putSuffix(String...suffix) {
		return this;
	}
	
	
}
