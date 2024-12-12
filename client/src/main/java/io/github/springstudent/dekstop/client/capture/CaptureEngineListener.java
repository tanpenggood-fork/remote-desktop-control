package io.github.springstudent.dekstop.client.capture;


import io.github.springstudent.dekstop.client.bean.Capture;
import io.github.springstudent.dekstop.client.bean.Listener;

public interface CaptureEngineListener extends Listener {
	/**
	 * Must not block.
	 */
	void onCaptured(Capture capture);

	/**
	 * Must not block: debugging purpose.
	 */
	void onRawCaptured(int id, byte[] grays);
}
