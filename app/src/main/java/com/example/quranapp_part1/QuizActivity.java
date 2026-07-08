package com.example.quranapp_part1;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizActivity extends BaseActivity {

    private int surahNumber;
    private int startVerse;
    private int endVerse;
    private String surahNameAr;

    private TextView tvQuizTitle, tvQuestion, tvScore, tvQuestionNumber;
    private Button btnOption1, btnOption2, btnOption3, btnOption4;
    private Button btnNext;

    private List<String[]> questions = new ArrayList<>(); // [سؤال, صحيح, خطأ1, خطأ2, خطأ3]
    private int currentQuestion = 0;
    private int score = 0;
    private boolean answered = false;

    // بيانات الآيات المخزّنة في الـ DB
    private List<String> verseTexts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        surahNumber = getIntent().getIntExtra(SurahListActivity.EXTRA_SURAH_NUMBER, 1);
        surahNameAr = getIntent().getStringExtra(SurahListActivity.EXTRA_SURAH_NAME_AR);
        startVerse  = getIntent().getIntExtra(MemorizationSettingsActivity.EXTRA_START_VERSE, 1);
        endVerse    = getIntent().getIntExtra(MemorizationSettingsActivity.EXTRA_END_VERSE, 999);

        tvQuizTitle      = findViewById(R.id.tvQuizTitle);
        tvQuestion       = findViewById(R.id.tvQuestion);
        tvScore          = findViewById(R.id.tvScore);
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber);
        btnOption1       = findViewById(R.id.btnOption1);
        btnOption2       = findViewById(R.id.btnOption2);
        btnOption3       = findViewById(R.id.btnOption3);
        btnOption4       = findViewById(R.id.btnOption4);
        btnNext          = findViewById(R.id.btnNextQuestion);

        tvQuizTitle.setText("اختبار " + surahNameAr);

        // جلب الآيات من قاعدة البيانات
        loadVersesFromDB();
    }

    // ===== جلب الآيات من الـ DB =====
    private void loadVersesFromDB() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        android.database.Cursor cursor = dbHelper.getVerse(surahNumber, 1);

        // جلب كل الآيات المخزّنة لهذي السورة
        android.database.sqlite.SQLiteDatabase db = dbHelper.getReadableDatabase();
        android.database.Cursor allVerses = db.rawQuery(
                "SELECT verse_text FROM verses WHERE surah_number = ? AND verse_number >= ? AND verse_number <= ? ORDER BY verse_number ASC",
                new String[]{String.valueOf(surahNumber), String.valueOf(startVerse), String.valueOf(endVerse)}
        );

        if (allVerses != null) {
            while (allVerses.moveToNext()) {
                verseTexts.add(allVerses.getString(0));
            }
            allVerses.close();
        }
        dbHelper.close();

        if (verseTexts.size() < 4) {
            Toast.makeText(this, "يلزم حفظ 4 آيات على الأقل للاختبار", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // توليد الأسئلة
        generateQuestions();
        showQuestion();
    }

    // ===== توليد 3 أسئلة عشوائية =====
    private void generateQuestions() {
        // نخلط الآيات
        List<String> shuffled = new ArrayList<>(verseTexts);
        Collections.shuffle(shuffled);

        int count = Math.min(3, shuffled.size());

        for (int i = 0; i < count; i++) {
            String correctVerse = shuffled.get(i);

            // بداية الآية كسؤال (أول 20 حرف)
            String questionText = correctVerse.length() > 20
                    ? correctVerse.substring(0, 20) + "..."
                    : correctVerse;

            // اختار 3 خيارات خاطئة
            List<String> wrongOptions = new ArrayList<>();
            for (String v : verseTexts) {
                if (!v.equals(correctVerse)) {
                    wrongOptions.add(v);
                }
            }
            Collections.shuffle(wrongOptions);

            String wrong1 = wrongOptions.size() > 0 ? wrongOptions.get(0) : "خيار خاطئ 1";
            String wrong2 = wrongOptions.size() > 1 ? wrongOptions.get(1) : "خيار خاطئ 2";
            String wrong3 = wrongOptions.size() > 2 ? wrongOptions.get(2) : "خيار خاطئ 3";

            questions.add(new String[]{questionText, correctVerse, wrong1, wrong2, wrong3});
        }
    }

    // ===== عرض السؤال الحالي =====
    private void showQuestion() {
        if (currentQuestion >= questions.size()) {
            showResult();
            return;
        }

        answered = false;
        String[] q = questions.get(currentQuestion);

        tvQuestionNumber.setText("السؤال " + (currentQuestion + 1) + " من " + questions.size());
        tvQuestion.setText("ما تكملة هذه الآية؟\n\n« " + q[0] + " »");
        tvScore.setText("النتيجة: " + score + " / " + questions.size());

        // رتب الخيارات عشوائياً
        List<String> options = new ArrayList<>();
        options.add(q[1]); // الصحيح
        options.add(q[2]);
        options.add(q[3]);
        options.add(q[4]);
        Collections.shuffle(options);

        // إعادة ضبط الألوان
        resetButtonColors();

        btnOption1.setText(options.get(0));
        btnOption2.setText(options.get(1));
        btnOption3.setText(options.get(2));
        btnOption4.setText(options.get(3));

        btnNext.setEnabled(false);
        btnNext.setText("التالي ←");

        // ربط الأزرار
        btnOption1.setOnClickListener(v -> checkAnswer(btnOption1, options.get(0), q[1]));
        btnOption2.setOnClickListener(v -> checkAnswer(btnOption2, options.get(1), q[1]));
        btnOption3.setOnClickListener(v -> checkAnswer(btnOption3, options.get(2), q[1]));
        btnOption4.setOnClickListener(v -> checkAnswer(btnOption4, options.get(3), q[1]));

        btnNext.setOnClickListener(v -> {
            currentQuestion++;
            showQuestion();
        });
    }

    // ===== التحقق من الإجابة =====
    private void checkAnswer(Button selected, String selectedText, String correctText) {
        if (answered) return;
        answered = true;

        if (selectedText.equals(correctText)) {
            // إجابة صحيحة
            selected.setBackgroundColor(Color.parseColor("#4CAF50")); // أخضر
            score++;
            Toast.makeText(this, "✅ أحسنت!", Toast.LENGTH_SHORT).show();
        } else {
            // إجابة خاطئة
            selected.setBackgroundColor(Color.parseColor("#F44336")); // أحمر

            // أظهر الصحيح
            highlightCorrect(correctText);
            Toast.makeText(this, "❌ الإجابة خاطئة", Toast.LENGTH_SHORT).show();
        }

        // تفعيل زر التالي بعد ثانية
        new Handler().postDelayed(() -> btnNext.setEnabled(true), 1000);
    }

    // ===== تلوين الإجابة الصحيحة =====
    private void highlightCorrect(String correctText) {
        if (btnOption1.getText().toString().equals(correctText))
            btnOption1.setBackgroundColor(Color.parseColor("#4CAF50"));
        else if (btnOption2.getText().toString().equals(correctText))
            btnOption2.setBackgroundColor(Color.parseColor("#4CAF50"));
        else if (btnOption3.getText().toString().equals(correctText))
            btnOption3.setBackgroundColor(Color.parseColor("#4CAF50"));
        else if (btnOption4.getText().toString().equals(correctText))
            btnOption4.setBackgroundColor(Color.parseColor("#4CAF50"));
    }

    // ===== إعادة ألوان الأزرار =====
    private void resetButtonColors() {
        int bgColor   = Color.parseColor("#FFFFFF");
        int textColor = Color.parseColor("#000000");

        btnOption1.setBackgroundColor(bgColor);
        btnOption2.setBackgroundColor(bgColor);
        btnOption3.setBackgroundColor(bgColor);
        btnOption4.setBackgroundColor(bgColor);

        btnOption1.setTextColor(textColor);
        btnOption2.setTextColor(textColor);
        btnOption3.setTextColor(textColor);
        btnOption4.setTextColor(textColor);
    }

    // ===== عرض النتيجة النهائية =====
    private void showResult() {
        String message;
        if (score == 3) {
            message = "🌟 ممتاز! حافظ بارع!";
        } else if (score == 2) {
            message = "👍 جيد جداً! استمر!";
        } else if (score == 1) {
            message = "💪 راجع الآيات وحاول مجدداً!";
        } else {
            message = "📖 راجع الآيات جيداً!";
        }

        new android.app.AlertDialog.Builder(this)
                .setTitle("نتيجة الاختبار")
                .setMessage(message + "\n\nنتيجتك: " + score + " / " + questions.size())
                .setPositiveButton("العودة للرئيسية", (d, w) -> {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("إعادة الاختبار", (d, w) -> {
                    // إعادة تشغيل الاختبار
                    currentQuestion = 0;
                    score = 0;
                    questions.clear();
                    generateQuestions();
                    showQuestion();
                })
                .setCancelable(false)
                .show();
    }
}
