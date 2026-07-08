package com.example.quranapp_part1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.text.InputType;
import android.widget.EditText;
import java.util.function.Consumer;

public class MemorizationSettingsActivity extends BaseActivity {

    public static final String EXTRA_START_VERSE  = "start_verse";
    public static final String EXTRA_END_VERSE    = "end_verse";
    public static final String EXTRA_REPEAT_COUNT = "repeat_count";

    // حدود القيم
    private static final int MIN_VERSE  = 1;
    private static final int MIN_REPEAT = 1;
    private static final int MAX_REPEAT = 20;

    // حالة العدادات
    private int startVerse, endVerse, maxVerse;
    private String surahNameAr;
    private int surahNumber;

    // NumberPicker للتكرار فقط
    private NumberPicker npRepeat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memorization_settings);

        // استقبال بيانات السورة من SurahListActivity
        surahNumber = getIntent().getIntExtra(SurahListActivity.EXTRA_SURAH_NUMBER, 1);
        surahNameAr = getIntent().getStringExtra(SurahListActivity.EXTRA_SURAH_NAME_AR);
        maxVerse    = getIntent().getIntExtra(SurahListActivity.EXTRA_SURAH_VERSE_COUNT, 7);

        // القيم الافتراضية
        startVerse = 1;
        endVerse   = maxVerse;

        // ربط Views
        TextView tvSurahName  = findViewById(R.id.tvSurahName);
        TextView tvStartVerse = findViewById(R.id.tvStartVerse);
        TextView tvEndVerse   = findViewById(R.id.tvEndVerse);
        Button btnBack        = findViewById(R.id.btnBack);
        Button btnStartPlus   = findViewById(R.id.btnStartPlus);
        Button btnStartMinus  = findViewById(R.id.btnStartMinus);
        Button btnEndPlus     = findViewById(R.id.btnEndPlus);
        Button btnEndMinus    = findViewById(R.id.btnEndMinus);
        Button btnStartNow    = findViewById(R.id.btnStartNow);
        npRepeat              = findViewById(R.id.npRepeatCount);

        // ضبط العنوان والعرض
        tvSurahName.setText("إعدادات " + surahNameAr);
        updateDisplays(tvStartVerse, tvEndVerse);

        // ضبط NumberPicker التكرار
        npRepeat.setMinValue(MIN_REPEAT);
        npRepeat.setMaxValue(MAX_REPEAT);
        npRepeat.setValue(3);

        // ===== زر الرجوع =====
        btnBack.setOnClickListener(v -> finish());

        // ===== ضغط على رقم البداية =====
        tvStartVerse.setOnClickListener(v ->
                showNumberInputDialog("آية البداية", startVerse, MIN_VERSE, endVerse - 1, val -> {
                    startVerse = val;
                    updateDisplays(tvStartVerse, tvEndVerse);
                })
        );

        // ===== ضغط على رقم النهاية =====
        tvEndVerse.setOnClickListener(v ->
                showNumberInputDialog("آية النهاية", endVerse, startVerse + 1, maxVerse, val -> {
                    endVerse = val;
                    updateDisplays(tvStartVerse, tvEndVerse);
                })
        );

        // ===== بداية الآيات =====
        btnStartPlus.setOnClickListener(v -> {
            if (startVerse < endVerse) {
                startVerse++;
                updateDisplays(tvStartVerse, tvEndVerse);
            } else {
                Toast.makeText(this, "لا يمكن أن تتجاوز بداية الآيات نهايتها!", Toast.LENGTH_SHORT).show();
            }
        });
        btnStartMinus.setOnClickListener(v -> {
            if (startVerse > MIN_VERSE) {
                startVerse--;
                updateDisplays(tvStartVerse, tvEndVerse);
            }
        });

        // ===== نهاية الآيات =====
        btnEndPlus.setOnClickListener(v -> {
            if (endVerse < maxVerse) {
                endVerse++;
                updateDisplays(tvStartVerse, tvEndVerse);
            } else {
                Toast.makeText(this, "وصلت لنهاية السورة!", Toast.LENGTH_SHORT).show();
            }
        });
        btnEndMinus.setOnClickListener(v -> {
            if (endVerse > startVerse) {
                endVerse--;
                updateDisplays(tvStartVerse, tvEndVerse);
            } else {
                Toast.makeText(this, "لا يمكن أن تكون النهاية قبل البداية!", Toast.LENGTH_SHORT).show();
            }
        });

        // ===== ابدأ الحفظ الآن =====
        btnStartNow.setOnClickListener(v -> {
            int repeatCount = npRepeat.getValue();


            Intent intent = new Intent(this, MemorizationActivity.class);
            intent.putExtra(SurahListActivity.EXTRA_SURAH_NUMBER,  surahNumber);
            intent.putExtra(SurahListActivity.EXTRA_SURAH_NAME_AR, surahNameAr);
            intent.putExtra(EXTRA_START_VERSE,  startVerse);
            intent.putExtra(EXTRA_END_VERSE,    endVerse);
            intent.putExtra(EXTRA_REPEAT_COUNT, repeatCount);
            startActivity(intent);
        });
    }

    private void showNumberInputDialog(String title, int current, int min, int max, Consumer<Integer> onConfirm) {
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setText(String.valueOf(current));
        input.setSelection(input.getText().length());

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setView(input)
                .setPositiveButton("تأكيد", (dialog, which) -> {
                    String val = input.getText().toString().trim();
                    if (!val.isEmpty()) {
                        int num = Integer.parseInt(val);
                        if (num >= min && num <= max) {
                            onConfirm.accept(num);
                        } else {
                            Toast.makeText(this, "رقم خارج النطاق!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("إلغاء", null)
                .show();
    }

    private void updateDisplays(TextView tvStart, TextView tvEnd) {
        tvStart.setText(String.valueOf(startVerse));
        tvEnd.setText(String.valueOf(endVerse));
    }
}