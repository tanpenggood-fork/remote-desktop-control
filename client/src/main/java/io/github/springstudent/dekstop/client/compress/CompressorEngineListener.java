package io.github.springstudent.dekstop.client.compress;


import io.github.springstudent.dekstop.client.bean.Listener;
import io.github.springstudent.dekstop.common.bean.CompressionMethod;
import io.github.springstudent.dekstop.common.bean.MemByteBuffer;
import io.github.springstudent.dekstop.common.configuration.CompressorEngineConfiguration;

public interface CompressorEngineListener extends Listener {
	/**
	 * May block (!)
	 */
	void onCompressed(int captureId, CompressionMethod compressionMethod, CompressorEngineConfiguration compressionConfiguration,
					  MemByteBuffer compressed);

}