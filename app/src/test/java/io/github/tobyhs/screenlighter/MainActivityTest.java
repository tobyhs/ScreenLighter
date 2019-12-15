package io.github.tobyhs.screenlighter;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Intent;
import android.provider.Settings;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowToast;

import io.github.tobyhs.screenlighter.shadows.ShadowSettingsSystem;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
public class MainActivityTest {
    private Application application = ApplicationProvider.getApplicationContext();

    @Test
    public void increaseBrightness() {
        ContentResolver contentResolver = application.getContentResolver();
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, 5);
        MainActivity activity = Robolectric.setupActivity(MainActivity.class);

        int brightness = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, 0);
        assertThat(brightness, is(15));

        assertThat(ShadowToast.shownToastCount(), is(1));
        String actualToastText = ShadowToast.getTextOfLatestToast();
        String expectedToastText = application.getResources().getString(R.string.increased_screen_brightness);
        assertThat(actualToastText, is(expectedToastText));

        assertThat(activity.isFinishing(), is(true));
    }

    @Test
    @Config(shadows = {ShadowSettingsSystem.class})
    public void requestWriteSettingsPermission() {
        ShadowSettingsSystem.setCanWrite(false);
        MainActivity activity = Robolectric.setupActivity(MainActivity.class);

        ShadowApplication shadowApp = shadowOf(application);
        Intent activityIntent = shadowApp.getNextStartedActivity();
        assertThat(activityIntent.getAction(), is(Settings.ACTION_MANAGE_WRITE_SETTINGS));
        assertThat(activityIntent.getDataString(), is("package:io.github.tobyhs.screenlighter"));

        assertThat(activity.isFinishing(), is(true));
    }
}
