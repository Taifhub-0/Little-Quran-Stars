package com.example.quranapp_part1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class CompletionActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completion);


        // استقبال البيانات
        String surahNameAr  = getIntent().getStringExtra(SurahListActivity.EXTRA_SURAH_NAME_AR);
        int    surahNumber  = getIntent().getIntExtra(SurahListActivity.EXTRA_SURAH_NUMBER, 1);
        int    repeatCount  = getIntent().getIntExtra(MemorizationSettingsActivity.EXTRA_REPEAT_COUNT, 3);

        // ضبط العنوان وعداد التكرار
        TextView tvSurahName = findViewById(R.id.tvSurahNameDone);
        TextView tvRepeatDone = findViewById(R.id.tvRepeatCounterDone);

        int startVerse = getIntent().getIntExtra(MemorizationSettingsActivity.EXTRA_START_VERSE, 1);
        int endVerse   = getIntent().getIntExtra(MemorizationSettingsActivity.EXTRA_END_VERSE, 6);

        if (surahNameAr != null) tvSurahName.setText(surahNameAr);
        tvRepeatDone.setText(repeatCount + " / " + repeatCount);

        TextView tvVerseRange = findViewById(R.id.tvVerseRangeDone);
        tvVerseRange.setText("الآية " + startVerse + " من " + endVerse);

        // ===== زر ابدأ الاختبار =====
        Button btnQuiz = findViewById(R.id.btnStartQuiz);
        btnQuiz.setOnClickListener(v -> {
            Intent intent = new Intent(this, QuizActivity.class);
            intent.putExtra(SurahListActivity.EXTRA_SURAH_NUMBER, surahNumber);
            intent.putExtra(SurahListActivity.EXTRA_SURAH_NAME_AR, surahNameAr);
            intent.putExtra(MemorizationSettingsActivity.EXTRA_START_VERSE, startVerse);
            intent.putExtra(MemorizationSettingsActivity.EXTRA_END_VERSE, endVerse);
            startActivity(intent);
        });

        // ===== زر كرر مرة أخرى → يرجع لشاشة الإعدادات =====
        Button btnRepeat = findViewById(R.id.btnRepeatAgain);
        btnRepeat.setOnClickListener(v -> {

            Intent intent = new Intent(this, MemorizationSettingsActivity.class);
            intent.putExtra(SurahListActivity.EXTRA_SURAH_NUMBER,      surahNumber);
            intent.putExtra(SurahListActivity.EXTRA_SURAH_NAME_AR,     surahNameAr);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        // ===== زر العودة للرئيسية =====
        Button btnHome = findViewById(R.id.btnBackHome);
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // ===== زر رجوع في الرأس =====
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }
}
