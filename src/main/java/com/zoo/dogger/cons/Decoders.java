package com.zoo.dogger.cons;

import com.zoo.dogger.media.decoder.Decoder;
import com.zoo.dogger.media.decoder.KGDecoder;
import com.zoo.dogger.media.decoder.NEDecoder;;

public enum Decoders {
	
	
	NE("网易",new NEDecoder()),KG("酷狗",new KGDecoder());
	
	private String name;
	
	private Decoder decoder;

	private Decoders(String name,Decoder decoder) {
		this.name=name;
		this.decoder = decoder;
	}

	public Decoder getDecoder() {
		return decoder;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
}
