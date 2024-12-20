package io.github.springstudent.dekstop.common.command;

import io.github.springstudent.dekstop.common.bean.TransferableImage;
import io.netty.buffer.ByteBuf;

import java.awt.datatransfer.DataFlavor;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author ZhouNing
 * @date 2024/12/19 19:49
 **/
public class CmdClipboardImg extends Cmd {

    private final TransferableImage payload;

    private String controllType;

    private int size;

    public CmdClipboardImg(TransferableImage payload, String controllType) {
        this.payload = new TransferableImage(payload.getTransferData(DataFlavor.imageFlavor));
        this.controllType = controllType;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(payload);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        byte[] serializedData = byteArrayOutputStream.toByteArray();
        this.size = serializedData.length;
    }


    public TransferableImage getGraphic() {
        return new TransferableImage(payload.getTransferData(DataFlavor.imageFlavor));
    }

    @Override
    public CmdType getType() {
        return CmdType.ClipboardImg;
    }

    @Override
    public int getWireSize() {
        return 8 + controllType.length() + size;
    }

    @Override
    public void encode(ByteBuf out) throws IOException {
        out.writeInt(controllType.length());
        out.writeCharSequence(controllType, StandardCharsets.UTF_8);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(payload);
        }
        byte[] serializedData = byteArrayOutputStream.toByteArray();
        out.writeInt(serializedData.length);
        out.writeBytes(serializedData);
    }


    public static CmdClipboardImg decode(ByteBuf in) throws IOException, ClassNotFoundException {
        int controllTypeLength = in.readInt();
        String controllType = in.readCharSequence(controllTypeLength, StandardCharsets.UTF_8).toString();
        int serializedDataLength = in.readInt();
        byte[] serializedData = new byte[serializedDataLength];
        in.readBytes(serializedData);
        TransferableImage payload;
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(serializedData);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            payload = (TransferableImage) objectInputStream.readObject();
        }
        return new CmdClipboardImg(payload, controllType);
    }

    @Override
    public String toString() {
        return CmdClipboardImg.class.getSimpleName();
    }

}
