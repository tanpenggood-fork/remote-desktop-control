package io.github.springstudent.dekstop.client.capture;

import io.github.springstudent.dekstop.common.bean.Gray8Bits;

import java.awt.*;
/**
 * @author ZhouNing
 * @date 2024/12/12 15:34
 **/
public interface CaptureFactory {

	Dimension getDimension();

	byte[] captureScreen(Gray8Bits quantization);

}
