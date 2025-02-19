package io.github.springstudent.dekstop.client.squeeze;

import com.github.luben.zstd.ZstdInputStream;
import com.github.luben.zstd.ZstdOutputStream;
import io.github.springstudent.dekstop.common.bean.MemByteBuffer;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author ZhouNing
 * @date 2025/2/19 10:02
 **/
public class ZstdZipper implements Zipper {
    @Override
    public MemByteBuffer zip(MemByteBuffer unzipped) throws IOException {
        try (MemByteBuffer zipped = new MemByteBuffer()) {
            try (ZstdOutputStream zstdOutputStream = new ZstdOutputStream(zipped, 12)) {
                zstdOutputStream.write(unzipped.getInternal(), 0, unzipped.size());
                zstdOutputStream.flush();
                return zipped;
            }
        }
    }

    @Override
    public MemByteBuffer unzip(MemByteBuffer zipped) throws IOException {
        try (final MemByteBuffer unzipped = new MemByteBuffer()) {
            try (ZstdInputStream zstdInputStream = new ZstdInputStream(new ByteArrayInputStream(zipped.getInternal(), 0, zipped.size()))) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = zstdInputStream.read(buffer)) > 0) {
                    unzipped.write(buffer, 0, bytesRead);
                }
            }
            return unzipped;
        }
    }
}
