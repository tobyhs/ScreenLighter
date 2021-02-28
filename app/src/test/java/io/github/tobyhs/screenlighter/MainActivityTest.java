package io.github.tobyhs.screenlighter;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Intent;
import android.provider.Settings;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowToast;

import io.github.tobyhs.screenlighter.shadows.ShadowSettingsSystem;

import static androidx.test.core.app.ActivityScenario.launch;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.robolectric.Shadows.shadowOf;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    private final Application application = ApplicationProvider.getApplicationContext();

    @Test
    public void increaseBrightness() {
        ContentResolver contentResolver = application.getContentResolver();
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, 5);

        try (ActivityScenario<MainActivity> scenario = launch(MainActivity.class)) {
            int brightness = Settings.System.getInt(
                    contentResolver, Settings.System.SCREEN_BRIGHTNESS, 0
            );
            assertThat(brightness, is(15));

            assertThat(ShadowToast.shownToastCount(), is(1));
            String actualToastText = ShadowToast.getTextOfLatestToast();
            String expectedToastText = application.getResources().getString(
                    R.string.increased_screen_brightness
            );
            assertThat(actualToastText, is(expectedToastText));

            assertThat(scenario.getState(), is(Lifecycle.State.DESTROYED));
        }
    }

    @Test
    @Config(shadows = {ShadowSettingsSystem.class})
    public void requestWriteSettingsPermission() {
        ShadowSettingsSystem.setCanWrite(false);

        try (ActivityScenario<MainActivity> scenario = launch(MainActivity.class)) {
            ShadowApplication shadowApp = shadowOf(application);
            Intent activityIntent = shadowApp.getNextStartedActivity();
            assertThat(activityIntent.getAction(), is(Settings.ACTION_MANAGE_WRITE_SETTINGS));
            assertThat(activityIntent.getDataString(), is("package:io.github.tobyhs.screenlighter"));

            assertThat(scenario.getState(), is(Lifecycle.State.DESTROYED));
        }
    }
}
