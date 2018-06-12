package com.zoo.dogger.media.audio;

import com.zoo.base.Strs;

public interface FrameId {

	default String getRemark() {
		return Strs.empty();
	}
	
	default String name() {
		return Strs.empty();
	}
}
