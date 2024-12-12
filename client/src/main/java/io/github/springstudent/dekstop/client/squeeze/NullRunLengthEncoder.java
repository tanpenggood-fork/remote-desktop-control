package io.github.springstudent.dekstop.client.squeeze;


import io.github.springstudent.dekstop.common.bean.MemByteBuffer;

public class NullRunLengthEncoder implements RunLengthEncoder {
	@Override
    public void runLengthEncode(MemByteBuffer out, MemByteBuffer capture) {
		out.write(capture.getInternal(), 0, capture.size());
	}

	@Override
    public void runLengthDecode(MemByteBuffer out, MemByteBuffer encoded) {
		out.write(encoded.getInternal(), 0, encoded.size());
	}
}
