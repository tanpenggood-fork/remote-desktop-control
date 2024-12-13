package io.github.springstudent.dekstop.client.core;

import io.github.springstudent.dekstop.client.RemoteClient;
import io.github.springstudent.dekstop.client.bean.Capture;
import io.github.springstudent.dekstop.client.capture.CaptureEngine;
import io.github.springstudent.dekstop.client.capture.RobotCaptureFactory;
import io.github.springstudent.dekstop.client.compress.CompressorEngine;
import io.github.springstudent.dekstop.client.compress.CompressorEngineListener;
import io.github.springstudent.dekstop.common.bean.CompressionMethod;
import io.github.springstudent.dekstop.common.bean.MemByteBuffer;
import io.github.springstudent.dekstop.common.command.*;
import io.github.springstudent.dekstop.common.configuration.CaptureEngineConfiguration;
import io.github.springstudent.dekstop.common.configuration.CompressorEngineConfiguration;

/**
 * 被控制方
 *
 * @author ZhouNing
 * @date 2024/12/9 8:40
 **/
public class RemoteControlled extends RemoteControll implements CompressorEngineListener {

    private CaptureEngine captureEngine;

    private CompressorEngine compressorEngine;

    private CaptureEngineConfiguration captureEngineConfiguration;

    private CompressorEngineConfiguration compressorEngineConfiguration;

    public RemoteControlled() {
        captureEngineConfiguration = new CaptureEngineConfiguration();
        compressorEngineConfiguration = new CompressorEngineConfiguration();
        captureEngine = new CaptureEngine(new RobotCaptureFactory(true));
        captureEngine.configure(captureEngineConfiguration);
        compressorEngine = new CompressorEngine();
        compressorEngine.configure(compressorEngineConfiguration);
        captureEngine.addListener(compressorEngine);
        compressorEngine.addListener(this);
    }

    @Override
    public void stop() {
        captureEngine.stop();
        compressorEngine.stop();
    }

    @Override
    public void start() {
        captureEngine.configure(new CaptureEngineConfiguration());
        compressorEngine.configure(new CompressorEngineConfiguration());
        captureEngine.start();
        compressorEngine.start(1);
    }

    public void closeSession(String deviceCode) {
        fireCmd(new CmdReqCapture(deviceCode, CmdReqCapture.STOP_CAPTURE));
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
        }
    }

    @Override
    public void onCompressed(Capture capture, CompressionMethod compressionMethod, CompressorEngineConfiguration compressionConfiguration, MemByteBuffer compressed) {
        fireCmd(new CmdCapture(capture.getId(), compressionMethod, compressionConfiguration, compressed));
    }
}
