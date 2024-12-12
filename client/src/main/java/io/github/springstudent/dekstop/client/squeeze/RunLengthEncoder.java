package io.github.springstudent.dekstop.client.squeeze;

import io.github.springstudent.dekstop.common.bean.MemByteBuffer;

interface RunLengthEncoder {
	void runLengthEncode(MemByteBuffer out, MemByteBuffer capture);

	void runLengthDecode(MemByteBuffer out, MemByteBuffer encoded);

}
