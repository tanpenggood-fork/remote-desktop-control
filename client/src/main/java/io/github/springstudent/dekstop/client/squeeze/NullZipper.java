package io.github.springstudent.dekstop.client.squeeze;


import io.github.springstudent.dekstop.common.bean.MemByteBuffer;

public class NullZipper implements Zipper {
	@Override
    public MemByteBuffer zip(MemByteBuffer unzipped) {
		final MemByteBuffer zipped = new MemByteBuffer();
		zipped.write(unzipped.getInternal(), 0, unzipped.size());
		return zipped;
	}

	@Override
    public MemByteBuffer unzip(MemByteBuffer zipped) {
		final MemByteBuffer unzipped = new MemByteBuffer();
		unzipped.write(zipped.getInternal(), 0, zipped.size());
		return unzipped;
	}
}
