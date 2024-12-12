package io.github.springstudent.dekstop.client.compress;


import io.github.springstudent.dekstop.client.bean.Capture;
import io.github.springstudent.dekstop.client.bean.Listener;

public interface DeCompressorEngineListener extends Listener {
	/**
	 * Called from within a de-compressor engine thread (!)
	 */
	void onDeCompressed(Capture capture, int cacheHits, double compressionRatio);
}
