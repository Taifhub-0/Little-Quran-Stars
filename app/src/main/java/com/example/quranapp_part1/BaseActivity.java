package com.example.quranapp_part1;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    @Override
    public Resources getResources() {
        if (getApplicationContext() == null) {
            return super.getResources();
        }

        int fontMode = getApplicationContext()
                .getSharedPreferences("QuranKidsPrefs", Context.MODE_PRIVATE)
                .getInt("font_size", 1);

        float scale;
        if      (fontMode == 0) scale = 0.7f;
        else if (fontMode == 2) scale = 1.5f;
        else                    scale = 1.0f;

        Resources res = super.getResources();
        Configuration config = res.getConfiguration();
        config.fontScale = scale;
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = getSharedPreferences(
                "QuranKidsPrefs", MODE_PRIVATE);
        int fontMode = prefs.getInt("font_size", 1);

        float savedScale;
        if      (fontMode == 0) savedScale = 0.7f;
        else if (fontMode == 2) savedScale = 1.5f;
        else                    savedScale = 1.0f;

        float currentScale = getResources()
                .getConfiguration().fontScale;

        if (Math.abs(currentScale - savedScale) > 0.05f) {
            recreate();
        }
    }
}