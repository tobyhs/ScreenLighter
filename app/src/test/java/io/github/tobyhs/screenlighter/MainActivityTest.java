package io.github.tobyhs.screenlighter;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation.ActivityResult;
import android.content.ContentResolver;
import android.provider.Settings;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.rule.IntentsRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;

import io.github.tobyhs.screenlighter.shadows.ShadowSettingsSystem;

import static androidx.test.core.app.ActivityScenario.launch;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public final IntentsRule intentsRule = new IntentsRule();

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
    public void requestWriteSettingsPermissionPositiveButton() {
        checkRequestWriteSettingsPermission(
                () -> onView(withText(android.R.string.ok)).inRoot(isDialog()).perform(click())
        );
    }

    @Test
    @Config(shadows = {ShadowSettingsSystem.class})
    public void requestWriteSettingsPermissionDismiss() {
        checkRequestWriteSettingsPermission(Espresso::pressBack);
    }

    private void checkRequestWriteSettingsPermission(Runnable action) {
        ShadowSettingsSystem.setCanWrite(false);

        try (ActivityScenario<MainActivity> scenario = launch(MainActivity.class)) {
            ActivityResult result = new ActivityResult(Activity.RESULT_CANCELED, null);
            intending(hasAction(Settings.ACTION_MANAGE_WRITE_SETTINGS)).respondWith(result);
            action.run();
            intended(hasAction(Settings.ACTION_MANAGE_WRITE_SETTINGS));
            intended(hasData("package:io.github.tobyhs.screenlighter"));
            scenario.onActivity(activity -> assertThat(activity.isFinishing(), is(true)));
        }
    }
}
