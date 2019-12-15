package io.github.tobyhs.screenlighter.shadows;

import android.content.Context;
import android.provider.Settings;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowSystem;

/**
 * A Robolectric shadow for {@code android.provider.Settings.System}
 */
@Implements(Settings.System.class)
public class ShadowSettingsSystem extends ShadowSystem {
    private static boolean canWrite = true;

    /**
     * Sets whether apps can write to system settings
     *
     * @param newCanWrite whether apps can write to system settings
     */
    public static void setCanWrite(boolean newCanWrite) {
        canWrite = newCanWrite;
    }

    /**
     * I couldn't find a way to set canWrite in Robolectric (as of version 4.3.1)
     *
     * @see android.provider.Settings.System#canWrite(Context)
     */
    @SuppressWarnings("unused")
    @Implementation
    protected static boolean canWrite(Context context) {
        return canWrite;
    }
}
