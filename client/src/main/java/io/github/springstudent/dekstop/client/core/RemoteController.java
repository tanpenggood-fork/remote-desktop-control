package io.github.springstudent.dekstop.client.core;

import io.github.springstudent.dekstop.client.RemoteClient;
import io.github.springstudent.dekstop.client.bean.Capture;
import io.github.springstudent.dekstop.client.compress.DeCompressorEngine;
import io.github.springstudent.dekstop.client.compress.DeCompressorEngineListener;
import io.github.springstudent.dekstop.common.command.*;
import io.github.springstudent.dekstop.common.log.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ImagingOpException;
import java.util.AbstractMap;

import static java.awt.image.BufferedImage.TYPE_BYTE_GRAY;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB_PRE;
import static java.lang.Math.abs;
import static java.lang.String.format;

/**
 * 控制方
 *
 * @author ZhouNing
 * @date 2024/12/9 8:39
 **/
public class RemoteController extends RemoteControll implements DeCompressorEngineListener {

    private String deviceCode;

    private DeCompressorEngine deCompressorEngine;

    private final Object prevBufferLOCK = new Object();

    private byte[] prevBuffer = null;

    private int prevWidth = -1;

    private int prevHeight = -1;

    private RemoteScreen remoteScreen;

    public RemoteController(){
        this.remoteScreen = RemoteClient.getRemoteClient().getRemoteScreen();
        deCompressorEngine = new DeCompressorEngine(this);
        start();
    }
    @Override
    public void stop() {

    }

    @Override
    public void start() {
        deCompressorEngine.start(8);
    }

    public void openSession(String deviceCode) {
        this.deviceCode = deviceCode;
        fireCmd(new CmdReqCapture(deviceCode, CmdReqCapture.START_CAPTURE));
    }

    public void closeSession() {
        fireCmd(new CmdReqCapture(deviceCode, CmdReqCapture.STOP_CAPTURE));
    }


    @Override
    public void handleCmd(Cmd cmd) {
        if (cmd.getType().equals(CmdType.ResCapture)) {
            CmdResCapture cmdResCapture = (CmdResCapture) cmd;
            if (cmdResCapture.getCode() == CmdResCapture.START) {
                RemoteClient.getRemoteClient().getRemoteScreen().launch();
            } else if (cmdResCapture.getCode() == CmdResCapture.STOP) {
                RemoteClient.getRemoteClient().getRemoteScreen().close();
            } else if (cmdResCapture.getCode() == CmdResCapture.OFFLINE) {
                RemoteClient.getRemoteClient().showMessageDialog("被控制端不在线", JOptionPane.ERROR_MESSAGE);
            } else if (cmdResCapture.getCode() == CmdResCapture.CONTROL) {
                RemoteClient.getRemoteClient().showMessageDialog("请先断开其他远程控制中的连接", JOptionPane.ERROR_MESSAGE);
            } else if (cmdResCapture.getCode() == CmdResCapture.FAIL) {
                RemoteClient.getRemoteClient().showMessageDialog("远程控制失败", JOptionPane.ERROR_MESSAGE);
            }
        }else if(cmd.getType().equals(CmdType.Capture)){
            deCompressorEngine.handleCapture((CmdCapture) cmd);
        }
    }

    @Override
    public void onDeCompressed(Capture capture, int cacheHits, double compressionRatio) {
        final AbstractMap.SimpleEntry<BufferedImage, byte[]> image;
        synchronized (prevBufferLOCK) {
            image = capture.createBufferedImage(prevBuffer, prevWidth, prevHeight);
            prevBuffer = image.getValue();
            prevWidth = image.getKey().getWidth();
            prevHeight = image.getKey().getHeight();
        }
        if (remoteScreen.getFitToScreenActivated()) {
            if (remoteScreen.getScreenPannel() == null) {
                Log.debug(format("ComputeScaleFactors for w: %s h: %s", prevWidth, prevHeight));
                remoteScreen.computeScaleFactors(prevWidth, prevHeight, remoteScreen.getKeepAspectRatioActivated());
            }
            // required as the canvas might have been reset if keepAspectRatio caused a resizing of the window
            final Dimension canvasDimension = remoteScreen.getCanvas();
            if (canvasDimension != null) {
                remoteScreen.getScreenPannel().onCaptureUpdated(scaleImage(image.getKey(), canvasDimension.width, canvasDimension.height));
            }
        } else {
            remoteScreen.getScreenPannel().onCaptureUpdated(image.getKey());
        }

    }

    private BufferedImage scaleImage(BufferedImage image, int width, int height) {
        AffineTransform scaleTransform = AffineTransform.getScaleInstance(remoteScreen.getxFactor(), remoteScreen.getyFactor());
        try {
            AffineTransformOp bilinearScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);
            return bilinearScaleOp.filter(image, new BufferedImage(abs(width), abs(height), image.getType() == 0 ? TYPE_INT_ARGB_PRE : TYPE_BYTE_GRAY));
        } catch (ImagingOpException e) {
            Log.error(e.getMessage());
            return image;
        }
    }
}
