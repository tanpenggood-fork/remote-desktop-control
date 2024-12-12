package io.github.springstudent.dekstop.client.capture;

import io.github.springstudent.dekstop.client.utils.ScreenUtilities;
import io.github.springstudent.dekstop.common.bean.Gray8Bits;
import java.awt.*;

public class RobotCaptureFactory implements CaptureFactory {
	private final Dimension captureDimension;
	public RobotCaptureFactory(boolean allScreens) {
		ScreenUtilities.setShareAllScreens(allScreens);
		captureDimension = ScreenUtilities.getSharedScreenSize().getSize();
	}

	@Override
	public Dimension getDimension() {
		return new Dimension(captureDimension);
	}

	@Override
	public byte[] captureScreen(Gray8Bits quantization) {
		return quantization == null ? ScreenUtilities.captureColors() : ScreenUtilities.captureGray(quantization);
	}
}
