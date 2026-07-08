package com.example.quranapp_part1;


import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class MainActivity extends AppCompatActivity {

    private Button btnResume;
    private DatabaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences(SettingsActivity.PREFS_NAME, MODE_PRIVATE);
        boolean isNight = prefs.getBoolean(SettingsActivity.KEY_NIGHT_MODE, false);
        if (isNight) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_main);

        TextView btnStart    = findViewById(R.id.btnStartMemorization);
        TextView btnSettings = findViewById(R.id.btn_settings);
        TextView btnHowTo    = findViewById(R.id.btn_how_to_use);
        btnResume            = findViewById(R.id.btnResume);

        // ابدأ الحفظ → شاشة اختيار السورة
        btnStart.setOnClickListener(v -> {
            Intent intent = new Intent(this, SurahListActivity.class);
            startActivity(intent);
        });

        // الإعدادات
        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });

        // كيف أستخدم
        btnHowTo.setOnClickListener(v -> {
            Intent intent = new Intent(this, HowToUseActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // نحدّث زر الاستئناف في كل مرة نرجع للرئيسية
        updateResumeButton();
    }

    // ===== تحديث زر الاستئناف =====
    private void updateResumeButton() {
        dbHelper = new DatabaseHelper(this);
        Cursor lastSurah = dbHelper.getLastOpenedSurah();

        if (lastSurah != null && lastSurah.moveToFirst()) {
            int lastSurahNum    = lastSurah.getInt(0);
            int lastVerseNum    = lastSurah.getInt(1);
            String lastSurahName = lastSurah.getString(2);
            int lastRepeatCount  = lastSurah.getInt(3);
            int lastCompletedRep = lastSurah.getInt(4);
            int lastStartVerse   = lastSurah.getInt(5);
            int lastEndVerse     = lastSurah.getInt(6);
            lastSurah.close();

            android.util.Log.d("MainActivity", "Resume - Surah: " + lastSurahNum +
                    ", Verse: " + lastVerseNum + ", Name: " + lastSurahName +
                    ", Range: " + lastStartVerse + "-" + lastEndVerse);

            if (lastSurahNum > 0 && lastVerseNum > 1) {
                btnResume.setVisibility(View.VISIBLE);
                btnResume.setText("استئناف من " + lastSurahName + " آية " + lastVerseNum + " ↩");

                btnResume.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, MemorizationActivity.class);
                    intent.putExtra(SurahListActivity.EXTRA_SURAH_NUMBER, lastSurahNum);
                    intent.putExtra(SurahListActivity.EXTRA_SURAH_NAME_AR, lastSurahName);
                    intent.putExtra(MemorizationSettingsActivity.EXTRA_START_VERSE, lastStartVerse);
                    intent.putExtra(MemorizationSettingsActivity.EXTRA_END_VERSE, lastEndVerse);
                    intent.putExtra(MemorizationSettingsActivity.EXTRA_REPEAT_COUNT, lastRepeatCount);
                    startActivity(intent);
                });
            } else {
                btnResume.setVisibility(View.GONE);
            }
        } else {
            btnResume.setVisibility(View.GONE);
            if (lastSurah != null) lastSurah.close();
        }

        dbHelper.close();
    }
}