package io.github.springstudent.dekstop.client.squeeze;


import io.github.springstudent.dekstop.common.bean.MemByteBuffer;

import java.io.IOException;

interface Zipper {
	MemByteBuffer zip(MemByteBuffer unzipped) throws IOException;

	MemByteBuffer unzip(MemByteBuffer zipped) throws IOException;

}
