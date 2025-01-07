package io.github.springstudent.dekstop.client.core;

import io.github.springstudent.dekstop.client.RemoteClient;
import io.github.springstudent.dekstop.client.capture.CaptureEngine;
import io.github.springstudent.dekstop.client.capture.RobotCaptureFactory;
import io.github.springstudent.dekstop.client.compress.CompressorEngine;
import io.github.springstudent.dekstop.client.compress.CompressorEngineListener;
import io.github.springstudent.dekstop.common.bean.CompressionMethod;
import io.github.springstudent.dekstop.common.bean.Constants;
import io.github.springstudent.dekstop.common.bean.MemByteBuffer;
import io.github.springstudent.dekstop.common.command.*;
import io.github.springstudent.dekstop.common.configuration.CaptureEngineConfiguration;
import io.github.springstudent.dekstop.common.configuration.CompressorEngineConfiguration;

import java.awt.*;
import java.awt.event.InputEvent;

/**
 * 被控制方
 *
 * @author ZhouNing
 * @date 2024/12/9 8:40
 **/
public class RemoteControlled extends RemoteControll implements CompressorEngineListener, RemoteScreenRobot {

    private CaptureEngine captureEngine;

    private CompressorEngine compressorEngine;

    private CaptureEngineConfiguration captureEngineConfiguration;

    private CompressorEngineConfiguration compressorEngineConfiguration;

    private Robot robot;

    public RemoteControlled() {
        captureEngineConfiguration = new CaptureEngineConfiguration();
        compressorEngineConfiguration = new CompressorEngineConfiguration();
        captureEngine = new CaptureEngine(new RobotCaptureFactory(true));
        captureEngine.configure(captureEngineConfiguration);
        compressorEngine = new CompressorEngine();
        compressorEngine.configure(compressorEngineConfiguration);
        captureEngine.addListener(compressorEngine);
        compressorEngine.addListener(this);
        try {
            robot = new Robot();
            robot.setAutoDelay(1);
        } catch (AWTException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public void stop() {
        captureEngine.stop();
        compressorEngine.stop();
        super.stop();
    }

    @Override
    public void start() {
        captureEngine.configure(new CaptureEngineConfiguration());
        compressorEngine.configure(new CompressorEngineConfiguration());
        captureEngine.start();
        compressorEngine.start(1);
        super.start();
    }

    public void closeSession(String deviceCode) {
        fireCmd(new CmdReqCapture(deviceCode, CmdReqCapture.STOP_CAPTURE_BY_CONTROLLED));
    }

    @Override
    public void handleCmd(Cmd cmd) {
        if (cmd.getType().equals(CmdType.ResCapture)) {
            CmdResCapture cmdResCapture = (CmdResCapture) cmd;
            if (cmdResCapture.getCode() == CmdResCapture.START_) {
                RemoteClient.getRemoteClient().setControlledAndCloseSessionLabelVisible(true);
                start();
            } else if (cmdResCapture.getCode() == CmdResCapture.STOP_) {
                RemoteClient.getRemoteClient().setControlledAndCloseSessionLabelVisible(false);
                stop();
            }
        } else if (cmd.getType().equals(CmdType.CaptureConfig)) {
            captureEngine.reconfigure(((CmdCaptureConf) cmd).getConfiguration());
        } else if (cmd.getType().equals(CmdType.CompressorConfig)) {
            compressorEngine.reconfigure(((CmdCompressorConf) cmd).getConfiguration());
        } else if (cmd.getType().equals(CmdType.KeyControl)) {
            this.handleMessage((CmdKeyControl) cmd);
        } else if (cmd.getType().equals(CmdType.MouseControl)) {
            this.handleMessage((CmdMouseControl) cmd);
        } else if (cmd.getType().equals(CmdType.ReqRemoteClipboard)) {
            super.sendClipboard().whenComplete((aByte, throwable) -> {
                if (throwable != null || aByte != CmdResRemoteClipboard.OK) {
                    fireCmd(new CmdResRemoteClipboard());
                }
            });
        } else if (((cmd.getType().equals(CmdType.ClipboardText) || cmd.getType().equals(CmdType.ClipboardTransfer))) && needSetClipboard(cmd)) {
            super.setClipboard(cmd).whenComplete((o, o2) -> {
                fireCmd(new CmdResRemoteClipboard());
            });
        }
    }

    @Override
    public String getType() {
        return Constants.CONTROLLED;
    }

    @Override
    public void onCompressed(int captureId, CompressionMethod compressionMethod, CompressorEngineConfiguration compressionConfiguration, MemByteBuffer compressed) {
        fireCmd(new CmdCapture(captureId, compressionMethod, compressionConfiguration, compressed));
    }

    @Override
    public void handleMessage(CmdMouseControl message) {
        if (message.isPressed()) {
            if (message.isButton1()) {
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            } else if (message.isButton2()) {
                robot.mousePress(InputEvent.BUTTON2_DOWN_MASK);
            } else if (message.isButton3()) {
                robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
            }
        } else if (message.isReleased()) {
            if (message.isButton1()) {
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            } else if (message.isButton2()) {
                robot.mouseRelease(InputEvent.BUTTON2_DOWN_MASK);
            } else if (message.isButton3()) {
                robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
            }
        } else if (message.isWheel()) {
            robot.mouseWheel(message.getRotations());
        }
        robot.mouseMove(message.getX(), message.getY());
    }

    @Override
    public void handleMessage(CmdKeyControl message) {
        if (message.isPressed()) {
            robot.keyPress(message.getKeyCode());
        } else if (message.isReleased()) {
            robot.keyRelease(message.getKeyCode());
        }
    }
}
