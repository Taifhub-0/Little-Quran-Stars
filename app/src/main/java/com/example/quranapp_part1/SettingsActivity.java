package com.example.quranapp_part1;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatDelegate;
import android.view.View;


public class SettingsActivity extends BaseActivity {


    public static final String PREFS_NAME       = "QuranKidsPrefs";
    public static final String KEY_NIGHT_MODE   = "night_mode";
    public static final String KEY_REPEAT_DELAY = "repeat_delay_ms";
    public static final String KEY_READER       = "reader_id";
    public static final String KEY_FONT_SIZE    = "font_size";

    // ====== Views ======
    private Switch  switchNight;
    private Button  btn2Sec, btn4Sec, btn6Sec, btnSave, btnBack;
    private Button  btnFontSmall, btnFontMedium, btnFontLarge;
    private Spinner spinnerReader;

    private int selectedDelay = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        // ربط Views
        switchNight   = findViewById(R.id.switchNightMode);
        btn2Sec       = findViewById(R.id.btn2Sec);
        btn4Sec       = findViewById(R.id.btn4Sec);
        btn6Sec       = findViewById(R.id.btn6Sec);
        btnSave       = findViewById(R.id.btnSaveSettings);
        btnBack       = findViewById(R.id.btnBack);
        spinnerReader = findViewById(R.id.spinnerReader);
        btnFontSmall  = findViewById(R.id.btnFontSmall);
        btnFontMedium = findViewById(R.id.btnFontMedium);
        btnFontLarge  = findViewById(R.id.btnFontLarge);

        // تعبئة قائمة القراء
        String[] readers = {
                "مشاري العفاسي",
                "عبدالباسط عبدالصمد",
                "عبدالرحمن السديس",
                "سعود الشريم",
                "علي الحذيفي",
                "هاني الرفاعي",
                "ماهر المعيقلي"
        };

        spinnerReader.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, readers));

        // تحميل الإعدادات المحفوظة أولاً
        loadSettings();


        // ======= زر الرجوع =======
        btnBack.setOnClickListener(v -> finish());

        // ======= زر "تم" بدل الحفظ =======
        btnSave.setOnClickListener(v -> finish());

        // ======= الوضع الليلي: يحفظ ويطبق فوراً =======
        switchNight.setOnCheckedChangeListener((btn, isChecked) -> {
            saveBoolean(KEY_NIGHT_MODE, isChecked);
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        //  أزرار الوقت
        btn2Sec.setOnClickListener(v -> selectDelay(2000, btn2Sec));
        btn4Sec.setOnClickListener(v -> selectDelay(4000, btn4Sec));
        btn6Sec.setOnClickListener(v -> selectDelay(6000, btn6Sec));

        //  أزرار حجم الخط ← جديد
        btnFontSmall.setOnClickListener(v -> {
            saveInt(KEY_FONT_SIZE, 0);
            selectFont(btnFontSmall);

        });
        btnFontMedium.setOnClickListener(v -> {
            saveInt(KEY_FONT_SIZE, 1);
            selectFont(btnFontMedium);

        });
        btnFontLarge.setOnClickListener(v -> {
            saveInt(KEY_FONT_SIZE, 2);
            selectFont(btnFontLarge);

        });

        //  القارئ
        spinnerReader.post(() ->
                spinnerReader.setOnItemSelectedListener(
                        new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent,
                                                       View view, int position, long id) {
                                saveInt(KEY_READER, position);
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {}
                        })
        );
    }

    //  تحديد التأخير المختار
    private void selectDelay(int ms, Button selected) {
        selectedDelay = ms;
        btn2Sec.setAlpha(0.5f);
        btn4Sec.setAlpha(0.5f);
        btn6Sec.setAlpha(0.5f);
        selected.setAlpha(1.0f);
        saveInt(KEY_REPEAT_DELAY, ms);
    }

    //  تمييز زر حجم الخط المختار
    private void selectFont(Button selected) {
        btnFontSmall.setAlpha(0.5f);
        btnFontMedium.setAlpha(0.5f);
        btnFontLarge.setAlpha(0.5f);
        selected.setAlpha(1.0f);
    }

    //  تحميل الإعدادات المحفوظة
    private void loadSettings() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        switchNight.setChecked(prefs.getBoolean(KEY_NIGHT_MODE, false));
        selectedDelay = prefs.getInt(KEY_REPEAT_DELAY, 4000);
        spinnerReader.setSelection(prefs.getInt(KEY_READER, 0));

        if      (selectedDelay == 2000) selectDelay(2000, btn2Sec);
        else if (selectedDelay == 6000) selectDelay(6000, btn6Sec);
        else                            selectDelay(4000, btn4Sec);


        int fontMode = prefs.getInt(KEY_FONT_SIZE, 1);
        if      (fontMode == 0) selectFont(btnFontSmall);
        else if (fontMode == 2) selectFont(btnFontLarge);
        else                    selectFont(btnFontMedium);
    }

    //  دوال مساعدة للحفظ السريع
    private void saveBoolean(String key, boolean value) {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
                .putBoolean(key, value).apply();
    }
    private void saveInt(String key, int value) {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
                .putInt(key, value).apply();
    }

}