package io.github.tobyhs.screenlighter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

/**
 * An activity that increases the screen brightness and exits.
 */
public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Settings.System.canWrite(this)) {
            increaseBrightness();
            finishAndRemoveTask();
        } else {
            requestWriteSettingsPermission();
        }
    }

    private void increaseBrightness() {
        ContentResolver contentResolver = getContentResolver();
        int brightness = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, 0);
        Log.d(this.getClass().getName(), "Brightness before: " + brightness);
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightness + 10);
        Toast.makeText(this, R.string.increased_screen_brightness, Toast.LENGTH_SHORT).show();
    }

    private void requestWriteSettingsPermission() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.write_permissions_prompt)
                .setPositiveButton(android.R.string.ok, null)
                .setOnDismissListener(dialog -> {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                    intent.setData(Uri.parse("package:" + getApplicationInfo().packageName));
                    startActivityForResult(intent, 0);
                })
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        finishAndRemoveTask();
    }
}
