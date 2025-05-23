package io.github.springstudent.dekstop.common.configuration;

import io.github.springstudent.dekstop.common.bean.Gray8Bits;

public class CaptureEngineConfiguration extends Configuration {
    private static final String PREF_VERSION = "capture.version";

    private static final String PREF_CAPTURE_TICK = "capture.tick";

    private static final String PREF_CAPTURE_QUANTIZATION = "capture.grayLevelQuantization";

    private static final String PREF_CAPTURE_COLORS = "capture.colors";

    /**
     * A capture is performed every tick (millis).
     */
    private final int captureTick;

    /**
     * The actual number of gray levels.
     */
    private final Gray8Bits captureQuantization;

    private final boolean captureColors;

    /**
     * Default : takes its values from the current preferences.
     *
     */
    public CaptureEngineConfiguration() {
        final Preferences prefs = Preferences.getPreferences();
        captureTick = prefs.getIntPreference(PREF_CAPTURE_TICK, 30);
        captureQuantization = prefs.getEnumPreference(PREF_CAPTURE_QUANTIZATION, Gray8Bits.X_128, Gray8Bits.values());
        captureColors = prefs.getBooleanPreference(PREF_CAPTURE_COLORS, true);
    }

    public CaptureEngineConfiguration(int captureTick, Gray8Bits captureQuantization, boolean captureColor) {
        this.captureTick = captureTick;
        this.captureQuantization = captureQuantization;
        this.captureColors = captureColor;
    }

    public int getCaptureTick() {
        return captureTick;
    }

    public Gray8Bits getCaptureQuantization() {
        return captureQuantization;
    }

    public boolean isCaptureColors() {
        return captureColors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final CaptureEngineConfiguration that = (CaptureEngineConfiguration) o;

        return captureTick == that.getCaptureTick() && captureQuantization == that.getCaptureQuantization() && captureColors == that.captureColors;
    }

    @Override
    public int hashCode() {
        return 31 * captureTick + (captureQuantization != null ? captureQuantization.hashCode() : 0) + (captureColors ? 1 : 0);
    }

    /**
     * @param clear allows for clearing properties from previous version
     */
    @Override
    protected void persist(boolean clear) {
        final Preferences.Props props = getProps(clear);
        Preferences.getPreferences().update(props); // atomic (!)
    }

    private Preferences.Props getProps(boolean clear) {
        final Preferences.Props props = new Preferences.Props();
        props.set(PREF_VERSION, String.valueOf(1));
        props.set(PREF_CAPTURE_TICK, String.valueOf(captureTick));
        props.set(PREF_CAPTURE_QUANTIZATION, String.valueOf(captureQuantization.ordinal()));
        props.set(PREF_CAPTURE_COLORS, String.valueOf(captureColors));

        // migration support (!)
        if (clear) {
            props.clear("generations");
        }
        return props;
    }

    @Override
    public String toString() {
        return "[tick:" + captureTick + "][quantization:" + captureQuantization + "][color:" + captureColors + "]";
    }
}
